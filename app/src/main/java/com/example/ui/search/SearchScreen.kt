package com.example.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.Product
import com.example.data.repository.XcartRepository
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ProductCard
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeLight
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    repository: XcartRepository,
    initialQuery: String = "",
    initialCategory: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    var searchQuery by remember { mutableStateOf(initialQuery) }
    var selectedSort by remember { mutableStateOf("Recommended") }
    var selectedCategory by remember { mutableStateOf(initialCategory) }

    val recentSearches by repository.recentSearches.collectAsState(initial = emptyList())
    val wishlistItems by repository.wishlistItems.collectAsState(initial = emptyList())
    val wishlistedIds = remember(wishlistItems) { wishlistItems.map { it.productId }.toSet() }

    val popularSearches = listOf(
        "Earbuds ANC", "Fast Charger 65W", "Smart Watch AMOLED",
        "Cotton Shirt", "Leather Wallet", "Travel Backpack", "Power Bank"
    )

    val sortOptions = listOf(
        "Recommended", "Price: Low to High", "Price: High to Low", "Highest Rated", "Newest"
    )

    // Trigger search
    val searchResults = remember(searchQuery, selectedCategory, selectedSort) {
        if (searchQuery.isNotBlank() || selectedCategory != null) {
            repository.getProducts(
                categoryId = selectedCategory,
                searchQuery = searchQuery.ifBlank { null },
                sortBy = selectedSort
            )
        } else {
            emptyList()
        }
    }

    val isSearching = searchQuery.isNotBlank() || selectedCategory != null

    LaunchedEffect(Unit) {
        if (initialQuery.isBlank() && initialCategory == null) {
            focusRequester.requestFocus()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Search Header
        Surface(
            color = NavyPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("search_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search Xcart...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = NavyDark
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .testTag("search_input_field"),
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        keyboardController?.hide()
                        if (searchQuery.isNotBlank()) {
                            coroutineScope.launch {
                                repository.addSearchQuery(searchQuery)
                            }
                        }
                    })
                )

                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        if (!isSearching) {
            // Recent & Popular Searches
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                // Recent Searches
                if (recentSearches.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Recent Searches",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark
                                )
                            }
                            Text(
                                text = "Clear All",
                                style = MaterialTheme.typography.bodySmall,
                                color = OrangePrimary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable {
                                    coroutineScope.launch {
                                        repository.clearSearchHistory()
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            recentSearches.forEach { term ->
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(16.dp),
                                    shadowElevation = 1.dp,
                                    modifier = Modifier.clickable {
                                        searchQuery = term
                                        keyboardController?.hide()
                                    }
                                ) {
                                    Text(
                                        text = term,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Divider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Popular Searches
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Popular Right Now",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        popularSearches.forEach { term ->
                            Surface(
                                color = OrangeLight,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.clickable {
                                    searchQuery = term
                                    keyboardController?.hide()
                                    coroutineScope.launch {
                                        repository.addSearchQuery(term)
                                    }
                                }
                            ) {
                                Text(
                                    text = term,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = OrangePrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Search Results with Sort Chips
            Column(modifier = Modifier.fillMaxSize()) {
                // Sort Chips Bar
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    items(sortOptions) { sort ->
                        val isSelected = selectedSort == sort
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSort = sort },
                            label = {
                                Text(
                                    text = sort,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OrangePrimary,
                                selectedLabelColor = Color.White,
                                containerColor = BackgroundLight,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                // Results count
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Found ${searchResults.size} item(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                if (searchResults.isEmpty()) {
                    EmptyStateView(
                        title = "No Products Found",
                        message = "We couldn't find any products matching your search criteria. Try a different keyword or category.",
                        buttonText = "Clear Search",
                        onButtonClick = {
                            searchQuery = ""
                            selectedCategory = null
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    )
                } else {
                    val pairs = searchResults.chunked(2)
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(pairs) { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
                    }
                }
            }
        }
    }
}
