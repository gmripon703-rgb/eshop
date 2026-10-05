package com.example.ui.wishlist

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.config.EnvironmentConfig
import com.example.data.repository.XcartRepository
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ProductCard
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.DiscountRed
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangePrimary
import kotlinx.coroutines.launch

@Composable
fun WishlistScreen(
    repository: XcartRepository,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onStartShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val wishlistItems by repository.wishlistItems.collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // App Bar
        Surface(
            color = NavyPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("wishlist_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "My Wishlist (${wishlistItems.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        if (wishlistItems.isEmpty()) {
            EmptyStateView(
                title = "Your Wishlist is Empty",
                message = "Tap the heart icon on any product to save items you love for later!",
                buttonText = "Explore Trending Products",
                onButtonClick = onStartShopping,
                icon = {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(36.dp)
                    )
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            val pairs = wishlistItems.chunked(2)
            LazyColumn(
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(pairs) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val prod1 = repository.getProductById(pair[0].productId)
                        if (prod1 != null) {
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(
                                    product = prod1,
                                    onProductClick = onNavigateToProduct,
                                    isWishlisted = true,
                                    onWishlistToggle = {
                                        coroutineScope.launch {
                                            repository.removeFromWishlist(prod1.id)
                                            Toast.makeText(context, "Removed from wishlist", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }

                        if (pair.size > 1) {
                            val prod2 = repository.getProductById(pair[1].productId)
                            if (prod2 != null) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ProductCard(
                                        product = prod2,
                                        onProductClick = onNavigateToProduct,
                                        isWishlisted = true,
                                        onWishlistToggle = {
                                            coroutineScope.launch {
                                                repository.removeFromWishlist(prod2.id)
                                                Toast.makeText(context, "Removed from wishlist", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
