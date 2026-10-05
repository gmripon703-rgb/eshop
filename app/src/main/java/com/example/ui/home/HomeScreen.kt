package com.example.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.core.model.BannerItem
import com.example.core.model.Category
import com.example.core.model.Product
import com.example.data.repository.XcartRepository
import com.example.ui.components.AppHeader
import com.example.ui.components.FlashDealTimer
import com.example.ui.components.ProductCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeLight
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    repository: XcartRepository,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToCategory: (String, String) -> Unit,
    onNavigateToCategoriesList: () -> Unit,
    onNavigateToSearch: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    val cartCount by repository.cartCount.collectAsState(initial = 0)
    val wishlistItems by repository.wishlistItems.collectAsState(initial = emptyList())
    val recentViewed by repository.recentProducts.collectAsState(initial = emptyList())

    val wishlistedIds = remember(wishlistItems) { wishlistItems.map { it.productId }.toSet() }

    val banners = remember { repository.getBanners() }
    val categories = remember { repository.getCategories() }
    val flashDeals = remember { repository.getFlashDeals() }
    val featuredProducts = remember { repository.getFeaturedProducts() }
    val newArrivals = remember { repository.getNewArrivals() }
    val allProducts = remember { repository.getProducts() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // App Header with Search bar and Cart / Notification
        AppHeader(
            cartCount = cartCount,
            onSearchClick = { onNavigateToSearch("") },
            onCartClick = onNavigateToCart,
            onNotificationClick = onNavigateToNotifications,
            onWishlistClick = onNavigateToWishlist
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // 1. Promotional Carousel
            item {
                BannerCarousel(
                    banners = banners,
                    onBannerClick = { banner ->
                        banner.categoryTarget?.let { catId ->
                            val cat = repository.getCategoryById(catId)
                            onNavigateToCategory(catId, cat?.name ?: "Products")
                        }
                    }
                )
            }

            // 2. Trust Badges / Value Props
            item {
                TrustBadgeRow()
            }

            // 3. Category Horizontal Grid
            item {
                SectionHeader(
                    title = "Shop by Category",
                    actionText = "All Categories",
                    onActionClick = onNavigateToCategoriesList
                )
                CategoryGridSection(
                    categories = categories,
                    onCategoryClick = { cat ->
                        onNavigateToCategory(cat.id, cat.name)
                    }
                )
            }

            // 4. Flash Deals Section with Timer
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = OrangePrimary,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = "Flash Deals",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark,
                            fontSize = 18.sp
                        )
                    }
                    FlashDealTimer()
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(flashDeals) { product ->
                        ProductCard(
                            product = product,
                            onProductClick = onNavigateToProduct,
                            isWishlisted = wishlistedIds.contains(product.id),
                            onWishlistToggle = {
                                coroutineScope.launch {
                                    if (wishlistedIds.contains(product.id)) {
                                        repository.removeFromWishlist(product.id)
                                    } else {
                                        repository.toggleWishlist(product)
                                    }
                                }
                            },
                            modifier = Modifier.width(165.dp)
                        )
                    }
                }
            }

            // 5. Featured Products Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(
                    title = "Featured & Best Sellers",
                    subtitle = "Curated top products in Bangladesh",
                    actionText = "See All",
                    onActionClick = { onNavigateToSearch("featured") }
                )
            }

            // 6. Featured Products 2-Column Grid
            val productPairs = featuredProducts.chunked(2)
            items(productPairs) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        ProductCard(
                            product = pair[0],
                            onProductClick = onNavigateToProduct,
                            isWishlisted = wishlistedIds.contains(pair[0].id),
                            onWishlistToggle = {
                                coroutineScope.launch {
                                    if (wishlistedIds.contains(pair[0].id)) {
                                        repository.removeFromWishlist(pair[0].id)
                                    } else {
                                        repository.toggleWishlist(pair[0])
                                    }
                                }
                            }
                        )
                    }
                    if (pair.size > 1) {
                        Box(modifier = Modifier.weight(1f)) {
                            ProductCard(
                                product = pair[1],
                                onProductClick = onNavigateToProduct,
                                isWishlisted = wishlistedIds.contains(pair[1].id),
                                onWishlistToggle = {
                                    coroutineScope.launch {
                                        if (wishlistedIds.contains(pair[1].id)) {
                                            repository.removeFromWishlist(pair[1].id)
                                        } else {
                                            repository.toggleWishlist(pair[1])
                                        }
                                    }
                                }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // 7. Recently Viewed (if any)
            if (recentViewed.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader(
                        title = "Recently Viewed",
                        actionText = null
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(recentViewed) { recent ->
                            val prod = repository.getProductById(recent.productId)
                            if (prod != null) {
                                ProductCard(
                                    product = prod,
                                    onProductClick = onNavigateToProduct,
                                    isWishlisted = wishlistedIds.contains(prod.id),
                                    modifier = Modifier.width(150.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 8. More to Love / All Products
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = "More to Love",
                    subtitle = "Just for you",
                    actionText = null
                )
            }

            val morePairs = allProducts.chunked(2)
            items(morePairs) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        ProductCard(
                            product = pair[0],
                            onProductClick = onNavigateToProduct,
                            isWishlisted = wishlistedIds.contains(pair[0].id)
                        )
                    }
                    if (pair.size > 1) {
                        Box(modifier = Modifier.weight(1f)) {
                            ProductCard(
                                product = pair[1],
                                onProductClick = onNavigateToProduct,
                                isWishlisted = wishlistedIds.contains(pair[1].id)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerCarousel(
    banners: List<BannerItem>,
    onBannerClick: (BannerItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (banners.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { banners.size })
    val context = LocalContext.current

    // Auto-scroll carousel every 4 seconds
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            pageSpacing = 12.dp
        ) { page ->
            val banner = banners[page]
            val resId = context.resources.getIdentifier(
                banner.drawableResName,
                "drawable",
                context.packageName
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clickable { onBannerClick(banner) }
                    .testTag("home_banner_${banner.id}"),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (resId != 0) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(resId)
                                .crossfade(true)
                                .build(),
                            contentDescription = banner.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(NavyPrimary)
                        )
                    }

                    // Gradient Overlay for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xB3000000))
                                )
                            )
                    )

                    // Text Content on Banner
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Surface(
                            color = OrangePrimary,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = banner.tag,
                                color = Color.White,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = banner.title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )
                        Text(
                            text = banner.subtitle,
                            color = Color(0xFFF1F5F9),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 13.5.sp
                        )
                    }
                }
            }
        }

        // Indicator dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (isSelected) 22.dp else 7.dp, 7.dp)
                        .clip(RoundedCornerShape(3.5.dp))
                        .background(if (isSelected) OrangePrimary else Color(0xFFCBD5E1))
                )
            }
        }
    }
}

@Composable
fun TrustBadgeRow(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.SurfaceBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BadgeItem(
                icon = Icons.Default.Payments,
                title = "Cash on Delivery",
                subtitle = "Pay at doorstep",
                tintColor = com.example.ui.theme.SuccessGreen,
                bgColor = com.example.ui.theme.SuccessGreenLight
            )
            BadgeItem(
                icon = Icons.Default.Security,
                title = "100% Genuine",
                subtitle = "Original products",
                tintColor = OrangePrimary,
                bgColor = OrangeLight
            )
            BadgeItem(
                icon = Icons.Default.LocalShipping,
                title = "Fast Delivery",
                subtitle = "All 64 districts",
                tintColor = com.example.ui.theme.ShippedBlue,
                bgColor = com.example.ui.theme.NavyContainer
            )
        }
    }
}

@Composable
private fun BadgeItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    tintColor: Color = OrangePrimary,
    bgColor: Color = OrangeLight
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = bgColor,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tintColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = NavyDark,
                fontSize = 12.5.sp
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 10.5.sp
            )
        }
    }
}

@Composable
fun CategoryGridSection(
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColors = listOf(
        Pair(Color(0xFF0284C7), Color(0xFFE0F2FE)), // Ocean Blue
        Pair(Color(0xFFEA580C), Color(0xFFFFEDD5)), // Sunset Orange
        Pair(Color(0xFF059669), Color(0xFFD1FAE5)), // Emerald Green
        Pair(Color(0xFF7C3AED), Color(0xFFEDE9FE)), // Royal Violet
        Pair(Color(0xFFDB2777), Color(0xFFFCE7F3)), // Vivid Pink
        Pair(Color(0xFFD97706), Color(0xFFFEF3C7)), // Warm Amber
        Pair(Color(0xFF0D9488), Color(0xFFCCFBF1)), // Deep Teal
        Pair(Color(0xFF4F46E5), Color(0xFFEEF2FF))  // Indigo
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(categories.size) { index ->
            val category = categories[index]
            val colorPair = categoryColors[index % categoryColors.size]

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(76.dp)
                    .clickable { onCategoryClick(category) }
                    .testTag("category_item_${category.id}")
            ) {
                Surface(
                    shape = CircleShape,
                    color = colorPair.second,
                    modifier = Modifier.size(58.dp),
                    shadowElevation = 1.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = category.name.take(2).uppercase(),
                            color = colorPair.first,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.5.sp,
                    maxLines = 2,
                    lineHeight = 16.sp,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
