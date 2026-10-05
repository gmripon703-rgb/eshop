package com.example.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeLight
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.TextMuted

sealed class BottomNavDestination(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Home : BottomNavDestination("home", "Home", Icons.Filled.Home, Icons.Outlined.Home, "bottom_nav_home")
    object Categories : BottomNavDestination("categories", "Categories", Icons.Filled.Category, Icons.Outlined.Category, "bottom_nav_categories")
    object Cart : BottomNavDestination("cart", "Cart", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart, "bottom_nav_cart")
    object Orders : BottomNavDestination("orders", "Orders", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong, "bottom_nav_orders")
    object Account : BottomNavDestination("account", "Account", Icons.Filled.Person, Icons.Outlined.Person, "bottom_nav_account")
}

@Composable
fun XcartBottomNavBar(
    currentRoute: String,
    cartBadgeCount: Int = 0,
    onNavigateToDestination: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavDestination.Home,
        BottomNavDestination.Categories,
        BottomNavDestination.Cart,
        BottomNavDestination.Orders,
        BottomNavDestination.Account
    )

    NavigationBar(
        modifier = modifier.testTag("main_bottom_nav_bar"),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigateToDestination(item.route) },
                icon = {
                    if (item is BottomNavDestination.Cart && cartBadgeCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = OrangePrimary,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = if (cartBadgeCount > 99) "99+" else cartBadgeCount.toString(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.5.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OrangePrimary,
                    selectedTextColor = OrangePrimary,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor = OrangeLight
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}
