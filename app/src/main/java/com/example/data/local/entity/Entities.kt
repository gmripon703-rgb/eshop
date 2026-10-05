package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: String,
    val productTitle: String,
    val productImage: String,
    val selectedVariant: String,
    val unitPrice: Double,
    val originalPrice: Double,
    val quantity: Int = 1,
    val isSelected: Boolean = true
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey
    val productId: String,
    val productTitle: String,
    val productImage: String,
    val currentPrice: Double,
    val originalPrice: Double,
    val rating: Float,
    val reviewCount: Int,
    val isInStock: Boolean = true,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderRecordEntity(
    @PrimaryKey
    val orderId: String,
    val orderNumber: String,
    val dateString: String,
    val subtotal: Double,
    val shippingFee: Double,
    val discount: Double,
    val totalAmount: Double,
    val statusName: String,
    val paymentMethodName: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val trackingNumber: String,
    val itemsSummary: String, // e.g. "Wireless Earbuds (x1), Smart Watch (x1)"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_products")
data class RecentProductEntity(
    @PrimaryKey
    val productId: String,
    val productTitle: String,
    val productImage: String,
    val currentPrice: Double,
    val originalPrice: Double,
    val rating: Float,
    val viewedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey
    val query: String,
    val searchedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_addresses")
data class SavedAddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val phone: String,
    val division: String,
    val district: String,
    val upazila: String,
    val area: String,
    val fullAddress: String,
    val instructions: String = "",
    val isDefault: Boolean = false
)
