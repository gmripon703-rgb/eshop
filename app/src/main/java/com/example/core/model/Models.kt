package com.example.core.model

import java.io.Serializable

data class Product(
    val id: String,
    val title: String,
    val description: String,
    val currentPrice: Double,
    val originalPrice: Double,
    val rating: Float,
    val reviewCount: Int,
    val stockCount: Int,
    val categoryId: String,
    val categoryName: String,
    val brand: String,
    val images: List<String>,
    val variants: List<ProductVariant> = emptyList(),
    val specifications: Map<String, String> = emptyMap(),
    val isFlashDeal: Boolean = false,
    val isFeatured: Boolean = false,
    val isNewArrival: Boolean = false,
    val deliveryDays: String = "2-4 Days",
    val freeShipping: Boolean = false
) : Serializable {
    val discountPercent: Int
        get() = if (originalPrice > currentPrice && originalPrice > 0) {
            (((originalPrice - currentPrice) / originalPrice) * 100).toInt()
        } else 0

    val isInStock: Boolean
        get() = stockCount > 0

    val mainImage: String
        get() = images.firstOrNull() ?: ""
}

data class ProductVariant(
    val id: String,
    val name: String, // e.g. "Color" or "Size"
    val options: List<String> // e.g. ["Midnight Blue", "Matte Black"] or ["M", "L", "XL"]
) : Serializable

data class Category(
    val id: String,
    val name: String,
    val iconName: String, // vector icon key or image
    val productCount: Int,
    val subcategories: List<String> = emptyList()
) : Serializable

data class Review(
    val id: String,
    val userName: String,
    val rating: Float,
    val date: String,
    val comment: String,
    val verifiedPurchase: Boolean = true
) : Serializable

enum class OrderStatus(val displayName: String, val stepIndex: Int) {
    PENDING("Order Placed", 0),
    CONFIRMED("Confirmed", 1),
    PROCESSING("Processing", 2),
    SHIPPED("Shipped", 3),
    DELIVERED("Delivered", 4),
    CANCELLED("Cancelled", -1)
}

data class OrderTimelineEvent(
    val status: OrderStatus,
    val title: String,
    val description: String,
    val timestamp: String,
    val isCompleted: Boolean
) : Serializable

data class OrderItem(
    val productId: String,
    val productTitle: String,
    val productImage: String,
    val selectedVariant: String,
    val unitPrice: Double,
    val quantity: Int
) : Serializable {
    val subtotal: Double
        get() = unitPrice * quantity
}

data class DeliveryAddress(
    val fullName: String,
    val phone: String,
    val email: String = "",
    val division: String,
    val district: String,
    val upazila: String,
    val area: String,
    val fullAddress: String,
    val deliveryInstructions: String = "",
    val isDefault: Boolean = false
) : Serializable {
    val formattedAddress: String
        get() = "$fullAddress, $area, $upazila, $district, $division"
}

enum class PaymentMethod(val title: String, val subtitle: String) {
    CASH_ON_DELIVERY("Cash on Delivery (COD)", "Pay with cash when package arrives at your doorstep"),
    BKASH("bKash Online Payment", "Instant payment with bKash digital wallet"),
    NAGAD("Nagad", "Fast payment with Nagad mobile wallet"),
    CARD("Credit/Debit Card", "Visa, Mastercard or Amex")
}

data class Order(
    val orderId: String,
    val orderNumber: String,
    val date: String,
    val items: List<OrderItem>,
    val subtotal: Double,
    val shippingFee: Double,
    val discount: Double,
    val totalAmount: Double,
    val status: OrderStatus,
    val deliveryAddress: DeliveryAddress,
    val paymentMethod: PaymentMethod,
    val timeline: List<OrderTimelineEvent> = emptyList(),
    val trackingNumber: String = ""
) : Serializable

data class BannerItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val tag: String,
    val drawableResName: String,
    val categoryTarget: String? = null
) : Serializable

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val date: String,
    val isRead: Boolean = false
) : Serializable

enum class NotificationType {
    ORDER, PROMO, PRICE_DROP, SYSTEM
}

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val avatarUrl: String? = null,
    val authProvider: String = "guest", // "google", "facebook", "guest", "phone"
    val isLoggedIn: Boolean = false
) : Serializable
