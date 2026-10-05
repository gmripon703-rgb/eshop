package com.example.ui.cart

import android.widget.Toast
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.core.config.EnvironmentConfig
import com.example.data.local.entity.CartItemEntity
import com.example.data.repository.XcartRepository
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.DiscountRed
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeDark
import com.example.ui.theme.OrangeLight
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun CartScreen(
    repository: XcartRepository,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToCheckout: () -> Unit,
    onStartShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val cartItems by repository.cartItems.collectAsState(initial = emptyList())

    var couponCode by remember { mutableStateOf("") }
    var couponDiscount by remember { mutableDoubleStateOf(0.0) }
    var appliedCouponName by remember { mutableStateOf("") }

    val selectedItems = remember(cartItems) { cartItems.filter { it.isSelected } }
    val allSelected = remember(cartItems, selectedItems) {
        cartItems.isNotEmpty() && selectedItems.size == cartItems.size
    }

    val selectedSubtotal = remember(selectedItems) {
        selectedItems.sumOf { it.unitPrice * it.quantity }
    }

    val shippingFee = remember(selectedSubtotal) {
        if (selectedSubtotal == 0.0 || selectedSubtotal >= EnvironmentConfig.FREE_SHIPPING_THRESHOLD) 0.0
        else EnvironmentConfig.DEFAULT_SHIPPING_FEE
    }

    val grandTotal = remember(selectedSubtotal, shippingFee, couponDiscount) {
        (selectedSubtotal + shippingFee - couponDiscount).coerceAtLeast(0.0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Cart Header
        Surface(
            color = NavyPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "My Shopping Cart",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (cartItems.isNotEmpty()) {
                    Text(
                        text = "${cartItems.sumOf { it.quantity }} item(s)",
                        color = OrangeLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (cartItems.isEmpty()) {
            EmptyStateView(
                title = "Your Shopping Cart is Empty",
                message = "Explore our exciting collection of electronics, gadgets, and lifestyle items to fill it up!",
                buttonText = "Start Shopping Now",
                onButtonClick = onStartShopping,
                icon = {
                    Icon(
                        imageVector = Icons.Default.RemoveShoppingCart,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(40.dp)
                    )
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Cart Content with Sticky Checkout Bar
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Select All Bar
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = allSelected,
                                        onCheckedChange = { checked ->
                                            coroutineScope.launch {
                                                repository.selectAllCartItems(checked)
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = OrangePrimary)
                                    )
                                    Text(
                                        text = "Select All (${cartItems.size})",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = NavyDark
                                    )
                                }

                                if (selectedItems.isNotEmpty()) {
                                    Text(
                                        text = "Delete Selected",
                                        color = DiscountRed,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clickable {
                                                coroutineScope.launch {
                                                    repository.clearSelectedCartItems()
                                                    Toast.makeText(context, "Removed selected items", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            .padding(8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Cart Items List
                    items(cartItems, key = { it.id }) { item ->
                        CartItemRow(
                            item = item,
                            onToggleSelect = { isChecked ->
                                coroutineScope.launch {
                                    repository.toggleCartItemSelection(item.id, isChecked)
                                }
                            },
                            onQuantityChange = { newQty ->
                                coroutineScope.launch {
                                    repository.updateCartQuantity(item.id, newQty)
                                }
                            },
                            onDelete = {
                                coroutineScope.launch {
                                    repository.removeCartItem(item.id)
                                }
                            },
                            onItemClick = { onNavigateToProduct(item.productId) }
                        )
                    }

                    // Voucher / Promo Code Box
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalOffer,
                                        contentDescription = null,
                                        tint = OrangePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Store Promo Voucher",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = NavyDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = couponCode,
                                        onValueChange = { couponCode = it.uppercase() },
                                        placeholder = { Text("Enter coupon (Try: XCART10)", fontSize = 12.sp) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = OrangePrimary,
                                            unfocusedBorderColor = Color.LightGray
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (couponCode.trim() == "XCART10") {
                                                couponDiscount = selectedSubtotal * 0.10
                                                appliedCouponName = "XCART10 (10% OFF)"
                                                Toast.makeText(context, "Voucher Applied! 10% Discount", Toast.LENGTH_SHORT).show()
                                            } else if (couponCode.isBlank()) {
                                                Toast.makeText(context, "Please enter a valid coupon code", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Invalid voucher code", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(48.dp)
                                    ) {
                                        Text("Apply", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                                if (appliedCouponName.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Applied: $appliedCouponName (-${EnvironmentConfig.formatBdt(couponDiscount)})",
                                        color = SuccessGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // Order Summary Breakdown
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Order Summary",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = NavyDark
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                SummaryRow("Subtotal (${selectedItems.size} items)", EnvironmentConfig.formatBdt(selectedSubtotal))
                                SummaryRow(
                                    "Estimated Shipping",
                                    if (shippingFee == 0.0) "FREE" else EnvironmentConfig.formatBdt(shippingFee),
                                    valueColor = if (shippingFee == 0.0) SuccessGreen else TextPrimary
                                )
                                if (couponDiscount > 0.0) {
                                    SummaryRow("Coupon Discount", "-${EnvironmentConfig.formatBdt(couponDiscount)}", valueColor = DiscountRed)
                                }
                                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Grand Total", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyDark)
                                    Text(
                                        EnvironmentConfig.formatBdt(grandTotal),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 20.sp,
                                        color = OrangePrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // Sticky Bottom Checkout Action
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total Amount",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = EnvironmentConfig.formatBdt(grandTotal),
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = OrangePrimary
                            )
                        }

                        Button(
                            onClick = {
                                if (selectedItems.isEmpty()) {
                                    Toast.makeText(context, "Please select at least 1 item to checkout", Toast.LENGTH_SHORT).show()
                                } else {
                                    onNavigateToCheckout()
                                }
                            },
                            enabled = selectedItems.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("cart_checkout_button")
                        ) {
                            Text(
                                text = "Proceed to Checkout (${selectedItems.sumOf { it.quantity }})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItemEntity,
    onToggleSelect: (Boolean) -> Unit,
    onQuantityChange: (Int) -> Unit,
    onDelete: () -> Unit,
    onItemClick: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = context.resources.getIdentifier(item.productImage, "drawable", context.packageName)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isSelected,
                onCheckedChange = onToggleSelect,
                colors = CheckboxDefaults.colors(checkedColor = OrangePrimary)
            )

            // Product Thumbnail
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF3F4F6))
                    .clickable { onItemClick() }
            ) {
                if (imageResId != 0) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageResId)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.productTitle,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(NavyPrimary.copy(alpha = 0.05f)))
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Details & Quantity
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.productTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = NavyDark,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Remove",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = "Variant: ${item.selectedVariant}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = EnvironmentConfig.formatBdt(item.unitPrice),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = OrangePrimary
                    )

                    // Stepper (- 1 +)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = { onQuantityChange(item.quantity - 1) },
                            modifier = Modifier
                                .size(26.dp)
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease",
                                modifier = Modifier.size(14.dp),
                                tint = NavyDark
                            )
                        }

                        Text(
                            text = item.quantity.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        IconButton(
                            onClick = { onQuantityChange(item.quantity + 1) },
                            modifier = Modifier
                                .size(26.dp)
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                modifier = Modifier.size(14.dp),
                                tint = NavyDark
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    title: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 13.sp, color = TextSecondary)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}
