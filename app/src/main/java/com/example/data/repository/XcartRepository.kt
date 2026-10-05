package com.example.data.repository

import android.content.Context
import com.example.core.config.EnvironmentConfig
import com.example.core.model.BannerItem
import com.example.core.model.Category
import com.example.core.model.DeliveryAddress
import com.example.core.model.NotificationItem
import com.example.core.model.NotificationType
import com.example.core.model.Order
import com.example.core.model.OrderItem
import com.example.core.model.OrderStatus
import com.example.core.model.OrderTimelineEvent
import com.example.core.model.PaymentMethod
import com.example.core.model.Product
import com.example.core.model.UserProfile
import com.example.data.local.database.XcartDatabase
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.OrderRecordEntity
import com.example.data.local.entity.RecentProductEntity
import com.example.data.local.entity.SavedAddressEntity
import com.example.data.local.entity.SearchHistoryEntity
import com.example.data.local.entity.WishlistItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class XcartRepository(context: Context) {
    private val db = XcartDatabase.getDatabase(context)
    private val cartDao = db.cartDao()
    private val wishlistDao = db.wishlistDao()
    private val orderDao = db.orderDao()
    private val recentProductDao = db.recentProductDao()
    private val searchHistoryDao = db.searchHistoryDao()
    private val addressDao = db.addressDao()

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    // Current User Session State (supports Google Sign-In, Facebook Sign-In, and Guest)
    private val _userProfile = MutableStateFlow(
        UserProfile(
            id = "guest_usr_01",
            name = "Guest Shopper",
            email = "",
            phone = "",
            authProvider = "guest",
            isLoggedIn = false
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Local in-memory notifications
    private val _notifications = MutableStateFlow(ProductCatalogData.initialNotifications)
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    init {
        // Pre-populate sample address if database is empty
        repositoryScope.launch {
            // Check default address setup
        }
    }

    // --- Products & Catalog ---
    fun getBanners(): List<BannerItem> = ProductCatalogData.banners

    fun getCategories(): List<Category> = ProductCatalogData.categories

    fun getCategoryById(categoryId: String): Category? =
        ProductCatalogData.categories.find { it.id == categoryId }

    fun getProductById(productId: String): Product? =
        ProductCatalogData.products.find { it.id == productId }

    fun getProducts(
        categoryId: String? = null,
        searchQuery: String? = null,
        sortBy: String = "Recommended",
        minRating: Float = 0f,
        maxPrice: Double? = null
    ): List<Product> {
        var list = ProductCatalogData.products

        if (!categoryId.isNullOrBlank()) {
            list = list.filter { it.categoryId == categoryId }
        }

        if (!searchQuery.isNullOrBlank()) {
            val q = searchQuery.trim().lowercase(Locale.ROOT)
            list = list.filter {
                it.title.lowercase(Locale.ROOT).contains(q) ||
                it.description.lowercase(Locale.ROOT).contains(q) ||
                it.categoryName.lowercase(Locale.ROOT).contains(q) ||
                it.brand.lowercase(Locale.ROOT).contains(q)
            }
        }

        if (minRating > 0f) {
            list = list.filter { it.rating >= minRating }
        }

        if (maxPrice != null && maxPrice > 0) {
            list = list.filter { it.currentPrice <= maxPrice }
        }

        return when (sortBy) {
            "Price: Low to High" -> list.sortedBy { it.currentPrice }
            "Price: High to Low" -> list.sortedByDescending { it.currentPrice }
            "Highest Rated" -> list.sortedByDescending { it.rating }
            "Newest" -> list.sortedByDescending { it.isNewArrival }
            "Popular" -> list.sortedByDescending { it.reviewCount }
            else -> list // Recommended
        }
    }

    fun getFlashDeals(): List<Product> =
        ProductCatalogData.products.filter { it.isFlashDeal }

    fun getFeaturedProducts(): List<Product> =
        ProductCatalogData.products.filter { it.isFeatured }

    fun getNewArrivals(): List<Product> =
        ProductCatalogData.products.filter { it.isNewArrival }

    // --- Shopping Cart (Room) ---
    val cartItems: Flow<List<CartItemEntity>> = cartDao.getAllCartItems()

    val cartCount: Flow<Int> = cartItems.map { list ->
        list.sumOf { it.quantity }
    }

    suspend fun addToCart(product: Product, selectedVariant: String, quantity: Int = 1) {
        val existing = cartDao.findItem(product.id, selectedVariant)
        if (existing != null) {
            cartDao.updateQuantity(existing.id, existing.quantity + quantity)
        } else {
            cartDao.insertCartItem(
                CartItemEntity(
                    productId = product.id,
                    productTitle = product.title,
                    productImage = product.mainImage,
                    selectedVariant = selectedVariant,
                    unitPrice = product.currentPrice,
                    originalPrice = product.originalPrice,
                    quantity = quantity,
                    isSelected = true
                )
            )
        }
    }

    suspend fun updateCartQuantity(cartItemId: Long, quantity: Int) {
        if (quantity <= 0) {
            cartDao.deleteCartItem(cartItemId)
        } else {
            cartDao.updateQuantity(cartItemId, quantity)
        }
    }

    suspend fun toggleCartItemSelection(cartItemId: Long, isSelected: Boolean) {
        cartDao.updateSelection(cartItemId, isSelected)
    }

    suspend fun selectAllCartItems(selectAll: Boolean) {
        cartDao.updateSelectAll(selectAll)
    }

    suspend fun removeCartItem(cartItemId: Long) {
        cartDao.deleteCartItem(cartItemId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    suspend fun clearSelectedCartItems() {
        cartDao.deleteSelectedItems()
    }

    // --- Wishlist (Room) ---
    val wishlistItems: Flow<List<WishlistItemEntity>> = wishlistDao.getAllWishlistItems()

    fun isProductInWishlist(productId: String): Flow<Boolean> =
        wishlistDao.isProductInWishlist(productId)

    suspend fun toggleWishlist(product: Product) {
        // Toggle item in room
        wishlistDao.addToWishlist(
            WishlistItemEntity(
                productId = product.id,
                productTitle = product.title,
                productImage = product.mainImage,
                currentPrice = product.currentPrice,
                originalPrice = product.originalPrice,
                rating = product.rating,
                reviewCount = product.reviewCount,
                isInStock = product.isInStock
            )
        )
    }

    suspend fun removeFromWishlist(productId: String) {
        wishlistDao.removeFromWishlist(productId)
    }

    // --- Recently Viewed (Room) ---
    val recentProducts: Flow<List<RecentProductEntity>> = recentProductDao.getRecentProducts()

    suspend fun recordProductView(product: Product) {
        recentProductDao.recordProductView(
            RecentProductEntity(
                productId = product.id,
                productTitle = product.title,
                productImage = product.mainImage,
                currentPrice = product.currentPrice,
                originalPrice = product.originalPrice,
                rating = product.rating
            )
        )
    }

    // --- Search History (Room) ---
    val recentSearches: Flow<List<String>> = searchHistoryDao.getRecentSearches()

    suspend fun addSearchQuery(query: String) {
        if (query.isNotBlank()) {
            searchHistoryDao.insertSearch(SearchHistoryEntity(query = query.trim()))
        }
    }

    suspend fun deleteSearchQuery(query: String) {
        searchHistoryDao.deleteSearch(query)
    }

    suspend fun clearSearchHistory() {
        searchHistoryDao.clearSearchHistory()
    }

    // --- Orders (Room) ---
    val orders: Flow<List<OrderRecordEntity>> = orderDao.getAllOrders()

    suspend fun placeOrder(
        items: List<CartItemEntity>,
        deliveryAddress: DeliveryAddress,
        paymentMethod: PaymentMethod,
        discountAmount: Double = 0.0,
        deliveryInstructions: String = ""
    ): Order {
        val orderId = "ORD-" + UUID.randomUUID().toString().take(8).uppercase(Locale.ROOT)
        val orderNumber = "XC-" + (100000..999999).random()
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateString = dateFormat.format(Date())

        val subtotal = items.sumOf { it.unitPrice * it.quantity }
        val isDhaka = deliveryAddress.district.equals("Dhaka", ignoreCase = true)
        val shippingFee = if (subtotal >= EnvironmentConfig.FREE_SHIPPING_THRESHOLD) 0.0
                          else if (isDhaka) EnvironmentConfig.DEFAULT_SHIPPING_FEE
                          else EnvironmentConfig.OUTSIDE_DHAKA_SHIPPING_FEE

        val totalAmount = (subtotal + shippingFee - discountAmount).coerceAtLeast(0.0)
        val trackingNum = "BDX" + (10000000..99999999).random()
        val itemsSummary = items.joinToString(", ") { "${it.productTitle} (${it.quantity}x)" }

        val orderRecord = OrderRecordEntity(
            orderId = orderId,
            orderNumber = orderNumber,
            dateString = dateString,
            subtotal = subtotal,
            shippingFee = shippingFee,
            discount = discountAmount,
            totalAmount = totalAmount,
            statusName = OrderStatus.PENDING.name,
            paymentMethodName = paymentMethod.name,
            customerName = deliveryAddress.fullName,
            customerPhone = deliveryAddress.phone,
            customerAddress = deliveryAddress.formattedAddress,
            trackingNumber = trackingNum,
            itemsSummary = itemsSummary
        )

        // Save order to room database
        orderDao.insertOrder(orderRecord)

        // Clear purchased items from cart
        clearSelectedCartItems()

        // Create timeline
        val timeline = listOf(
            OrderTimelineEvent(
                status = OrderStatus.PENDING,
                title = "Order Placed Successfully",
                description = "We received your order #$orderNumber",
                timestamp = dateString,
                isCompleted = true
            ),
            OrderTimelineEvent(
                status = OrderStatus.CONFIRMED,
                title = "Order Confirmation",
                description = "Our verification team will confirm your order details shortly",
                timestamp = "Pending Verification",
                isCompleted = false
            ),
            OrderTimelineEvent(
                status = OrderStatus.PROCESSING,
                title = "Packaging & Quality Check",
                description = "Your items are being packed in our fulfillment center",
                timestamp = "Pending",
                isCompleted = false
            ),
            OrderTimelineEvent(
                status = OrderStatus.SHIPPED,
                title = "Handed to Courier Partner",
                description = "Courier will deliver to your doorstep",
                timestamp = "Pending",
                isCompleted = false
            ),
            OrderTimelineEvent(
                status = OrderStatus.DELIVERED,
                title = "Delivered to Customer",
                description = "Payment collected on delivery",
                timestamp = "Pending",
                isCompleted = false
            )
        )

        val domainOrder = Order(
            orderId = orderId,
            orderNumber = orderNumber,
            date = dateString,
            items = items.map {
                OrderItem(
                    productId = it.productId,
                    productTitle = it.productTitle,
                    productImage = it.productImage,
                    selectedVariant = it.selectedVariant,
                    unitPrice = it.unitPrice,
                    quantity = it.quantity
                )
            },
            subtotal = subtotal,
            shippingFee = shippingFee,
            discount = discountAmount,
            totalAmount = totalAmount,
            status = OrderStatus.PENDING,
            deliveryAddress = deliveryAddress,
            paymentMethod = paymentMethod,
            timeline = timeline,
            trackingNumber = trackingNum
        )

        // Add notification
        val newNotification = NotificationItem(
            id = "notif_" + System.currentTimeMillis(),
            title = "Order Placed #$orderNumber",
            message = "Thank you! Your order for ${items.size} item(s) has been placed with ${paymentMethod.title}.",
            type = NotificationType.ORDER,
            date = "Just now",
            isRead = false
        )
        _notifications.value = listOf(newNotification) + _notifications.value

        return domainOrder
    }

    suspend fun getOrderById(orderId: String): Order? {
        val record = orderDao.getOrderById(orderId) ?: return null
        val status = try { OrderStatus.valueOf(record.statusName) } catch (e: Exception) { OrderStatus.PENDING }
        val paymentMethod = try { PaymentMethod.valueOf(record.paymentMethodName) } catch (e: Exception) { PaymentMethod.CASH_ON_DELIVERY }

        val timeline = listOf(
            OrderTimelineEvent(
                status = OrderStatus.PENDING,
                title = "Order Placed Successfully",
                description = "Order #${record.orderNumber} received",
                timestamp = record.dateString,
                isCompleted = true
            ),
            OrderTimelineEvent(
                status = OrderStatus.CONFIRMED,
                title = "Order Confirmed",
                description = "Verified by merchant fulfillment desk",
                timestamp = if (status.stepIndex >= 1) "Confirmed" else "Pending Verification",
                isCompleted = status.stepIndex >= 1
            ),
            OrderTimelineEvent(
                status = OrderStatus.PROCESSING,
                title = "Processing at Hub",
                description = "Package sealed & quality inspected",
                timestamp = if (status.stepIndex >= 2) "Ready for dispatch" else "Upcoming",
                isCompleted = status.stepIndex >= 2
            ),
            OrderTimelineEvent(
                status = OrderStatus.SHIPPED,
                title = "Out for Delivery",
                description = "Tracking: ${record.trackingNumber}",
                timestamp = if (status.stepIndex >= 3) "In transit" else "Upcoming",
                isCompleted = status.stepIndex >= 3
            ),
            OrderTimelineEvent(
                status = OrderStatus.DELIVERED,
                title = "Delivered to Customer",
                description = "Delivered to ${record.customerAddress}",
                timestamp = if (status.stepIndex >= 4) "Delivered" else "Estimated in 2-3 days",
                isCompleted = status.stepIndex >= 4
            )
        )

        return Order(
            orderId = record.orderId,
            orderNumber = record.orderNumber,
            date = record.dateString,
            items = listOf(
                OrderItem(
                    productId = "prod_item",
                    productTitle = record.itemsSummary,
                    productImage = "banner_mega_sale_1791119940020",
                    selectedVariant = "Standard",
                    unitPrice = record.subtotal,
                    quantity = 1
                )
            ),
            subtotal = record.subtotal,
            shippingFee = record.shippingFee,
            discount = record.discount,
            totalAmount = record.totalAmount,
            status = status,
            deliveryAddress = DeliveryAddress(
                fullName = record.customerName,
                phone = record.customerPhone,
                division = "Dhaka",
                district = "Dhaka",
                upazila = "Gulshan",
                area = "Gulshan 1",
                fullAddress = record.customerAddress
            ),
            paymentMethod = paymentMethod,
            timeline = timeline,
            trackingNumber = record.trackingNumber
        )
    }

    // --- Saved Addresses ---
    val savedAddresses: Flow<List<SavedAddressEntity>> = addressDao.getAllAddresses()

    suspend fun saveAddress(address: SavedAddressEntity): Long {
        return addressDao.insertAddress(address)
    }

    suspend fun deleteAddress(id: Long) {
        addressDao.deleteAddress(id)
    }

    // --- Customer Authentication (Google, Facebook, Guest) ---
    fun loginWithGoogle(email: String = "shopper.bd@gmail.com", name: String = "Rafiqul Islam") {
        _userProfile.value = UserProfile(
            id = "usr_g_" + UUID.randomUUID().toString().take(6),
            name = name,
            email = email,
            phone = "+880 1712 345678",
            avatarUrl = null,
            authProvider = "google",
            isLoggedIn = true
        )
    }

    fun loginWithFacebook(name: String = "Tariqul Hasan") {
        _userProfile.value = UserProfile(
            id = "usr_fb_" + UUID.randomUUID().toString().take(6),
            name = name,
            email = "tariqul.h@gmail.com",
            phone = "+880 1819 876543",
            avatarUrl = null,
            authProvider = "facebook",
            isLoggedIn = true
        )
    }

    fun logout() {
        _userProfile.value = UserProfile(
            id = "guest_usr_01",
            name = "Guest Shopper",
            email = "",
            phone = "",
            authProvider = "guest",
            isLoggedIn = false
        )
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }
}
