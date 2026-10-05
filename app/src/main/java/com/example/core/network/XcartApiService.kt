package com.example.core.network

import com.example.core.model.BannerItem
import com.example.core.model.Category
import com.example.core.model.DeliveryAddress
import com.example.core.model.Order
import com.example.core.model.OrderItem
import com.example.core.model.Product
import com.example.core.model.UserProfile
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class LoginRequest(
    val provider: String, // "google", "facebook"
    val idToken: String,
    val email: String? = null,
    val name: String? = null
)

data class AuthResponse(
    val token: String,
    val user: UserProfile
)

data class PlaceOrderRequest(
    val items: List<OrderItem>,
    val deliveryAddress: DeliveryAddress,
    val paymentMethod: String,
    val notes: String? = null
)

data class OrderResponse(
    val success: Boolean,
    val order: Order,
    val message: String
)

data class ProductsResponse(
    val products: List<Product>,
    val totalCount: Int,
    val page: Int
)

interface XcartApiService {
    @GET("banners")
    suspend fun getBanners(): Response<List<BannerItem>>

    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("products")
    suspend fun getProducts(
        @Query("category") categoryId: String? = null,
        @Query("search") search: String? = null,
        @Query("featured") featured: Boolean? = null,
        @Query("flashDeal") flashDeal: Boolean? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ProductsResponse>

    @GET("products/{id}")
    suspend fun getProductDetails(
        @Path("id") productId: String
    ): Response<Product>

    @POST("orders/create")
    suspend fun placeOrder(
        @Header("Authorization") token: String?,
        @Body request: PlaceOrderRequest
    ): Response<OrderResponse>

    @GET("orders/history")
    suspend fun getOrdersHistory(
        @Header("Authorization") token: String?
    ): Response<List<Order>>

    @POST("auth/login")
    suspend fun authenticate(
        @Body request: LoginRequest
    ): Response<AuthResponse>
}
