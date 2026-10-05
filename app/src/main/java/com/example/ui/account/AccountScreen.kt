package com.example.ui.account

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.DiscountRed
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeLight
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AccountScreen(
    repository: XcartRepository,
    onNavigateToOrders: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToSavedAddresses: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val user by repository.userProfile.collectAsState()
    val orders by repository.orders.collectAsState(initial = emptyList())
    val wishlistItems by repository.wishlistItems.collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // App Bar / Profile Header
        Surface(
            color = NavyPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Avatar
                    Surface(
                        shape = CircleShape,
                        color = OrangePrimary,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (user.name.isNotBlank()) user.name.take(1).uppercase() else "G",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (user.isLoggedIn) user.name else "Guest Shopper",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (user.isLoggedIn) (if (user.email.isNotEmpty()) user.email else user.phone) else "Sign in to sync your wishlist and orders",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                }

                if (!user.isLoggedIn) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        color = OrangePrimary,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToAuth() }
                            .testTag("account_signin_prompt")
                    ) {
                        Text(
                            text = "Sign In / Create Account",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Quick Dashboard Stats
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickStatItem(
                            title = "My Orders",
                            count = orders.size.toString(),
                            onClick = onNavigateToOrders
                        )
                        QuickStatItem(
                            title = "Wishlist",
                            count = wishlistItems.size.toString(),
                            onClick = onNavigateToWishlist
                        )
                        QuickStatItem(
                            title = "Country",
                            count = "BD (৳)",
                            onClick = {}
                        )
                    }
                }
            }

            // Orders & Addresses Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                        AccountMenuItem(
                            icon = Icons.Default.ReceiptLong,
                            title = "My Orders",
                            subtitle = "${orders.size} active & completed orders",
                            onClick = onNavigateToOrders
                        )
                        Divider(color = Color(0xFFF1F5F9))
                        AccountMenuItem(
                            icon = Icons.Default.FavoriteBorder,
                            title = "Wishlist",
                            subtitle = "${wishlistItems.size} saved items",
                            onClick = onNavigateToWishlist
                        )
                        Divider(color = Color(0xFFF1F5F9))
                        AccountMenuItem(
                            icon = Icons.Default.LocationOn,
                            title = "Delivery Addresses",
                            subtitle = "Manage saved shipping addresses",
                            onClick = onNavigateToSavedAddresses
                        )
                        Divider(color = Color(0xFFF1F5F9))
                        AccountMenuItem(
                            icon = Icons.Default.Notifications,
                            title = "Notification Center",
                            subtitle = "Offers, order updates & alerts",
                            onClick = onNavigateToNotifications
                        )
                    }
                }
            }

            // Customer Support & Policies
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                        AccountMenuItem(
                            icon = Icons.Default.Headphones,
                            title = "Customer Support",
                            subtitle = "${EnvironmentConfig.STORE_PHONE} • 24/7 Helpline",
                            onClick = {
                                Toast.makeText(context, "Call Support: ${EnvironmentConfig.STORE_PHONE}", Toast.LENGTH_LONG).show()
                            }
                        )
                        Divider(color = Color(0xFFF1F5F9))
                        AccountMenuItem(
                            icon = Icons.Default.Policy,
                            title = "Privacy Policy",
                            subtitle = "How we protect customer information",
                            onClick = {
                                Toast.makeText(context, "Xcart adheres to strict data privacy guidelines", Toast.LENGTH_SHORT).show()
                            }
                        )
                        Divider(color = Color(0xFFF1F5F9))
                        AccountMenuItem(
                            icon = Icons.Default.Description,
                            title = "Terms & Conditions",
                            subtitle = "Consumer rights and warranty details",
                            onClick = {
                                Toast.makeText(context, "Authentic products with 7-day money back guarantee", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // Logout / Sign in action
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (user.isLoggedIn) {
                                    repository.logout()
                                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                                } else {
                                    onNavigateToAuth()
                                }
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (user.isLoggedIn) Icons.Default.Logout else Icons.Default.Person,
                            contentDescription = null,
                            tint = if (user.isLoggedIn) DiscountRed else OrangePrimary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (user.isLoggedIn) "Log Out" else "Sign In with Google or Facebook",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (user.isLoggedIn) DiscountRed else NavyDark
                        )
                    }
                }
            }

            // Store Info & Version
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Xcart Bangladesh Limited",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyDark
                    )
                    Text(
                        text = "Version 1.0.0 • Verified Commercial Store",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatItem(
    title: String,
    count: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = count,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = OrangePrimary
        )
        Text(
            text = title,
            fontSize = 11.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AccountMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                shape = CircleShape,
                color = OrangeLight,
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = NavyDark
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}
