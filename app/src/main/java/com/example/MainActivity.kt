package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.repository.XcartRepository
import com.example.ui.account.AccountScreen
import com.example.ui.account.SavedAddressesScreen
import com.example.ui.auth.AuthScreen
import com.example.ui.cart.CartScreen
import com.example.ui.category.CategoriesScreen
import com.example.ui.checkout.CheckoutScreen
import com.example.ui.components.XcartBottomNavBar
import com.example.ui.home.HomeScreen
import com.example.ui.notifications.NotificationsScreen
import com.example.ui.orders.OrderTrackingScreen
import com.example.ui.orders.OrdersScreen
import com.example.ui.product.ProductDetailsScreen
import com.example.ui.search.SearchScreen
import com.example.ui.theme.XcartTheme
import com.example.ui.wishlist.WishlistScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = XcartRepository(applicationContext)

        setContent {
            XcartTheme {
                XcartMainApp(repository = repository)
            }
        }
    }
}

@Composable
fun XcartMainApp(repository: XcartRepository) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    val cartBadgeCount by repository.cartCount.collectAsState(initial = 0)

    val bottomNavRoutes = setOf("home", "categories", "cart", "orders", "account")
    val shouldShowBottomNav = currentRoute in bottomNavRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (shouldShowBottomNav) {
                XcartBottomNavBar(
                    currentRoute = currentRoute,
                    cartBadgeCount = cartBadgeCount,
                    onNavigateToDestination = { targetRoute ->
                        navController.navigate(targetRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .fillMaxSize()
                .padding(if (shouldShowBottomNav) innerPadding else androidx.compose.foundation.layout.PaddingValues())
        ) {
            // 1. Home Screen
            composable("home") {
                HomeScreen(
                    repository = repository,
                    onNavigateToProduct = { productId ->
                        navController.navigate("product/$productId")
                    },
                    onNavigateToCategory = { catId, catName ->
                        navController.navigate("search?query=&category=$catId")
                    },
                    onNavigateToCategoriesList = {
                        navController.navigate("categories")
                    },
                    onNavigateToSearch = { initialQuery ->
                        navController.navigate("search?query=$initialQuery&category=")
                    },
                    onNavigateToCart = {
                        navController.navigate("cart")
                    },
                    onNavigateToNotifications = {
                        navController.navigate("notifications")
                    },
                    onNavigateToWishlist = {
                        navController.navigate("wishlist")
                    }
                )
            }

            // 2. Categories Screen
            composable("categories") {
                CategoriesScreen(
                    repository = repository,
                    onCategoryClick = { catId, catName ->
                        navController.navigate("search?query=&category=$catId")
                    }
                )
            }

            // 3. Shopping Cart Screen
            composable("cart") {
                CartScreen(
                    repository = repository,
                    onNavigateToProduct = { productId ->
                        navController.navigate("product/$productId")
                    },
                    onNavigateToCheckout = {
                        navController.navigate("checkout")
                    },
                    onStartShopping = {
                        navController.navigate("home")
                    }
                )
            }

            // 4. Orders Screen
            composable("orders") {
                OrdersScreen(
                    repository = repository,
                    onNavigateToOrderDetails = { orderId ->
                        navController.navigate("order_tracking/$orderId")
                    },
                    onStartShopping = {
                        navController.navigate("home")
                    }
                )
            }

            // 5. Account Screen
            composable("account") {
                AccountScreen(
                    repository = repository,
                    onNavigateToOrders = {
                        navController.navigate("orders")
                    },
                    onNavigateToWishlist = {
                        navController.navigate("wishlist")
                    },
                    onNavigateToNotifications = {
                        navController.navigate("notifications")
                    },
                    onNavigateToAuth = {
                        navController.navigate("auth")
                    },
                    onNavigateToSavedAddresses = {
                        navController.navigate("saved_addresses")
                    }
                )
            }

            // 6. Product Details Screen
            composable(
                route = "product/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailsScreen(
                    productId = productId,
                    repository = repository,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCart = { navController.navigate("cart") },
                    onNavigateToCheckout = { navController.navigate("checkout") }
                )
            }

            // 7. Search & Catalog Screen
            composable(
                route = "search?query={query}&category={category}",
                arguments = listOf(
                    navArgument("query") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument("category") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val query = backStackEntry.arguments?.getString("query") ?: ""
                val category = backStackEntry.arguments?.getString("category")?.takeIf { it.isNotEmpty() }
                SearchScreen(
                    repository = repository,
                    initialQuery = query,
                    initialCategory = category,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProduct = { productId ->
                        navController.navigate("product/$productId")
                    }
                )
            }

            // 8. Checkout Screen
            composable("checkout") {
                CheckoutScreen(
                    repository = repository,
                    onNavigateBack = { navController.popBackStack() },
                    onOrderPlaced = { orderId ->
                        navController.navigate("order_tracking/$orderId") {
                            popUpTo("home")
                        }
                    }
                )
            }

            // 9. Order Tracking Screen
            composable(
                route = "order_tracking/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderTrackingScreen(
                    orderId = orderId,
                    repository = repository,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 10. Wishlist Screen
            composable("wishlist") {
                WishlistScreen(
                    repository = repository,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProduct = { productId ->
                        navController.navigate("product/$productId")
                    },
                    onStartShopping = {
                        navController.navigate("home")
                    }
                )
            }

            // 11. Notifications Screen
            composable("notifications") {
                NotificationsScreen(
                    repository = repository,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 12. Auth Screen
            composable("auth") {
                AuthScreen(
                    repository = repository,
                    onNavigateBack = { navController.popBackStack() },
                    onAuthSuccess = { navController.popBackStack() }
                )
            }

            // 13. Saved Addresses Screen
            composable("saved_addresses") {
                SavedAddressesScreen(
                    repository = repository,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
