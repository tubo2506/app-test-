package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.alpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MenuItemEntity
import com.example.ui.CartItem
import com.example.ui.Voucher
import com.example.ui.components.SpecialPromoBanner
import com.example.ui.components.TableQrStatusHeader
import com.example.ui.components.TableSelectorRow
import com.example.ui.components.TagBadge
import com.example.ui.components.formatVnd
import com.example.ui.theme.IceCreamBorder
import com.example.ui.theme.IceCreamCoral
import com.example.ui.theme.IceCreamMint
import com.example.ui.theme.IceCreamPink
import com.example.ui.theme.IceCreamPinkDark
import com.example.ui.theme.IceCreamPinkLight
import com.example.ui.theme.IceCreamTextPrimary
import com.example.ui.theme.IceCreamTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuOrderScreen(
    menuItems: List<MenuItemEntity>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedTable: String,
    onSelectTable: (String) -> Unit = {},
    customerPhone: String? = null,
    onOpenQrScanner: () -> Unit = {},
    cartItems: List<CartItem>,
    onOpenCustomize: (MenuItemEntity) -> Unit,
    onQuickAdd: (MenuItemEntity) -> Unit,
    onUpdateCartQty: (String, Int) -> Unit,
    onRemoveCartItem: (String) -> Unit,
    appliedVoucher: Voucher?,
    onSubmitOrder: (String) -> Unit,
    // Customization Dialog props
    customizingItem: MenuItemEntity?,
    customizeQty: Int,
    onIncreaseQty: () -> Unit,
    onDecreaseQty: () -> Unit,
    availableToppings: List<MenuItemEntity>,
    selectedToppings: List<MenuItemEntity>,
    onToggleTopping: (MenuItemEntity) -> Unit,
    customizeNote: String,
    onCustomizeNoteChange: (String) -> Unit,
    onConfirmCustomize: () -> Unit,
    onCloseCustomize: () -> Unit,
    onHeaderVisibilityChange: (Boolean) -> Unit = {},
    onApplySpecialPromoVoucher: () -> Unit = {},
    onOpenServiceCall: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showCartSheet by remember { mutableStateOf(false) }
    var orderGeneralNote by remember { mutableStateOf("") }
    val cartSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    var isHeaderVisible by remember { mutableStateOf(true) }

    // Detect scroll direction: scrolling down collapses top bar, keeping ONLY the category selector sticky
    LaunchedEffect(gridState) {
        var previousIndex = 0
        var previousScrollOffset = 0
        snapshotFlow { gridState.firstVisibleItemIndex to gridState.firstVisibleItemScrollOffset }
            .collect { (currentIndex, currentOffset) ->
                if (currentIndex == 0 && currentOffset <= 15) {
                    if (!isHeaderVisible) {
                        isHeaderVisible = true
                        onHeaderVisibilityChange(true)
                    }
                } else {
                    val isScrollingDown = (currentIndex > previousIndex) ||
                        (currentIndex == previousIndex && currentOffset > previousScrollOffset + 15)
                    val isScrollingUp = (currentIndex < previousIndex) ||
                        (currentIndex == previousIndex && currentOffset < previousScrollOffset - 15)

                    if (isScrollingDown && isHeaderVisible) {
                        isHeaderVisible = false
                        onHeaderVisibilityChange(false)
                    } else if (isScrollingUp && !isHeaderVisible) {
                        isHeaderVisible = true
                        onHeaderVisibilityChange(true)
                    }
                }
                previousIndex = currentIndex
                previousScrollOffset = currentOffset
            }
    }

    val categories = listOf("Tất cả", "Kem viên", "Topping", "Đồ uống")

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Collapsible Top Section: Table QR Status & Special Promo Banner & Search Bar
            // Automatically scrolls away when customer scrolls down, freeing maximum screen space!
            AnimatedVisibility(
                visible = isHeaderVisible,
                enter = expandVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)) + fadeIn(),
                exit = shrinkVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)) + fadeOut()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Modern QR Checked-in Table Status Header
                    TableQrStatusHeader(
                        selectedTable = selectedTable,
                        customerPhone = customerPhone,
                        onOpenQrScanner = onOpenQrScanner,
                        onOpenServiceCall = onOpenServiceCall
                    )

                    // Special Promotion Banner (displayed on entry, auto-cycles deals, dismissible)
                    SpecialPromoBanner(
                        isVoucherApplied = appliedVoucher?.id == "PROMO_GELATO20",
                        onApplySpecialVoucher = onApplySpecialPromoVoucher,
                        onSelectCategory = { cat ->
                            onSelectCategory(cat)
                            coroutineScope.launch {
                                gridState.scrollToItem(0)
                            }
                        }
                    )

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text("Tìm vị kem, topping, bingsu...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = IceCreamPink)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Xóa", tint = Color.Gray)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IceCreamPink,
                            unfocusedBorderColor = IceCreamBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .testTag("menu_search_field")
                    )
                }
            }

            // STICKY Category Horizontal Tabs
            // ALWAYS kept sticky at the top when scrolling down so customer can easily filter
            Surface(
                color = MaterialTheme.colorScheme.background,
                shadowElevation = if (!isHeaderVisible) 4.dp else 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (!isHeaderVisible) Modifier.statusBarsPadding() else Modifier)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = cat == selectedCategory
                        val emoji = when (cat) {
                            "Kem viên" -> "🍦 "
                            "Topping" -> "🍬 "
                            "Đồ uống" -> "🍹 "
                            else -> "✨ "
                        }
                        val catScale by androidx.compose.animation.core.animateFloatAsState(
                            targetValue = if (isSelected) 1.05f else 1.0f,
                            animationSpec = androidx.compose.animation.core.spring(
                                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy
                            ),
                            label = "catScale"
                        )

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) IceCreamPink else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) IceCreamPink else IceCreamBorder
                            ),
                            shadowElevation = if (isSelected) 3.dp else 0.dp,
                            modifier = Modifier
                                .scale(catScale)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    onSelectCategory(cat)
                                    coroutineScope.launch {
                                        gridState.scrollToItem(0)
                                    }
                                }
                                .testTag("category_tab_$cat")
                        ) {
                            Text(
                                text = "$emoji$cat",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isSelected) Color.White else IceCreamTextPrimary,
                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 9.dp)
                            )
                        }
                    }
                }
            }

            // Menu Items Grid - Takes maximum viewport space
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("menu_items_grid")
            ) {
                items(menuItems, key = { it.id }) { item ->
                    MenuItemGridCard(
                        item = item,
                        onOpenCustomize = { onOpenCustomize(item) },
                        onQuickAdd = { onQuickAdd(item) }
                    )
                }
            }
        }

        // Floating Cart Summary Bar at Bottom
        AnimatedVisibility(
            visible = cartItems.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            val totalItems = cartItems.sumOf { it.quantity }
            val rawTotal = cartItems.sumOf { it.totalPrice }
            val discount = appliedVoucher?.discountAmount ?: 0L
            val finalTotal = (rawTotal - discount).coerceAtLeast(0L)

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { showCartSheet = true }
                    .testTag("floating_cart_bar"),
                shape = RoundedCornerShape(20.dp),
                color = IceCreamPink,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "$totalItems",
                                    color = IceCreamPink,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "$selectedTable • Đang chọn món",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp
                            )
                            Text(
                                text = formatVnd(finalTotal),
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Xem giỏ & Gọi món",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    // Customization Dialog
    if (customizingItem != null) {
        Dialog(onDismissRequest = onCloseCustomize) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .testTag("customize_item_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(customizingItem.emoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = customizingItem.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = IceCreamTextPrimary
                                )
                                Text(
                                    text = formatVnd(customizingItem.price),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = IceCreamPink
                                )
                            }
                        }

                        IconButton(onClick = onCloseCustomize) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = customizingItem.description,
                        fontSize = 12.sp,
                        color = IceCreamTextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = IceCreamBorder)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Quantity Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Số lượng",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = IceCreamPinkLight,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { onDecreaseQty() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Remove, contentDescription = "Giảm", tint = IceCreamPink, modifier = Modifier.size(16.dp))
                                }
                            }

                            Text(
                                text = "$customizeQty",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Surface(
                                shape = CircleShape,
                                color = IceCreamPink,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { onIncreaseQty() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Add, contentDescription = "Tăng", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    // Topping Selection (if category is Kem viên or Đồ uống)
                    if (customizingItem.category != "Topping" && availableToppings.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Thêm Topping Yêu Thích 🍬",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = IceCreamTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            availableToppings.take(5).forEach { topping ->
                                val isChecked = selectedToppings.any { it.id == topping.id }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onToggleTopping(topping) }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { onToggleTopping(topping) },
                                            colors = CheckboxDefaults.colors(checkedColor = IceCreamPink)
                                        )
                                        Text(
                                            text = "${topping.emoji} ${topping.name}",
                                            fontSize = 13.sp
                                        )
                                    }
                                    Text(
                                        text = "+${formatVnd(topping.price)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = IceCreamPink
                                    )
                                }
                            }
                        }
                    }

                    // Custom Note field
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = customizeNote,
                        onValueChange = onCustomizeNoteChange,
                        label = { Text("Ghi chú cho món này (VD: ít ngọt, nhiều sốt)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IceCreamPink,
                            unfocusedBorderColor = IceCreamBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Calculation & Submit button
                    val singlePrice = customizingItem.price + selectedToppings.sumOf { it.price }
                    val totalPrice = singlePrice * customizeQty

                    Button(
                        onClick = onConfirmCustomize,
                        colors = ButtonDefaults.buttonColors(containerColor = IceCreamPink),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("confirm_customize_add_button")
                    ) {
                        Text(
                            text = "Thêm vào giỏ • ${formatVnd(totalPrice)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }

    // Cart & Checkout Modal Bottom Sheet
    if (showCartSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCartSheet = false },
            sheetState = cartSheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp)
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Giỏ Hàng $selectedTable",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = IceCreamTextPrimary
                        )
                        Text(
                            text = "${cartItems.sumOf { it.quantity }} món đã chọn",
                            fontSize = 12.sp,
                            color = IceCreamTextSecondary
                        )
                    }

                    IconButton(onClick = { showCartSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = IceCreamBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // Group ordering info banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF3E5F5),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👥", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Đơn của $selectedTable • Mọi thành viên cùng bàn quét QR đều gọi chung vào hóa đơn này.",
                            fontSize = 11.sp,
                            color = Color(0xFF6A1B9A),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Cart items list
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    cartItems.forEach { item ->
                        CartItemRow(
                            item = item,
                            onUpdateQty = { delta -> onUpdateCartQty(item.id, delta) },
                            onRemove = { onRemoveCartItem(item.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // General Note field for the entire table order
                OutlinedTextField(
                    value = orderGeneralNote,
                    onValueChange = { orderGeneralNote = it },
                    label = { Text("Ghi chú chung cho đơn hàng của bàn") },
                    placeholder = { Text("VD: Mang ra cùng lúc, cho thêm khăn giấy...") },
                    leadingIcon = {
                        Icon(Icons.Default.EditNote, contentDescription = null, tint = IceCreamPink)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IceCreamPink,
                        unfocusedBorderColor = IceCreamBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Price Breakdown
                val rawTotal = cartItems.sumOf { it.totalPrice }
                val discount = appliedVoucher?.discountAmount ?: 0L
                val finalTotal = (rawTotal - discount).coerceAtLeast(0L)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tạm tính:", color = IceCreamTextSecondary, fontSize = 13.sp)
                    Text(formatVnd(rawTotal), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                if (discount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Voucher ưu đãi:", color = IceCreamMint, fontSize = 13.sp)
                        Text("-${formatVnd(discount)}", color = IceCreamMint, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tổng thanh toán:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        formatVnd(finalTotal),
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = IceCreamPink
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Big Confirm Order Button
                Button(
                    onClick = {
                        onSubmitOrder(orderGeneralNote)
                        showCartSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IceCreamPink),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_table_order_button")
                ) {
                    Text(
                        text = "🚀 Gửi Order Đến Quầy (${selectedTable})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MenuItemGridCard(
    item: MenuItemEntity,
    onOpenCustomize: () -> Unit,
    onQuickAdd: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val addBtnScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 1.35f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy
        ),
        finishedListener = { isPressed = false },
        label = "addBounce"
    )

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, if (item.isAvailable) IceCreamBorder else Color(0xFFE0E0E0)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .clickable(enabled = item.isAvailable) { onOpenCustomize() }
            .testTag("menu_item_${item.id}")
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .then(if (!item.isAvailable) Modifier.alpha(0.6f) else Modifier)
        ) {
            // Emoji circle container & Tag badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(item.colorHex).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.emoji,
                    fontSize = 44.sp
                )

                if (!item.isAvailable) {
                    Surface(
                        color = Color(0xFF757575),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "TẠM HẾT",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else if (item.tag.isNotBlank()) {
                    TagBadge(
                        tag = item.tag,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = IceCreamTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = item.description,
                fontSize = 11.sp,
                color = IceCreamTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp,
                modifier = Modifier.height(30.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Price & Quick Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatVnd(item.price),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = if (item.isAvailable) IceCreamPink else Color.Gray
                )

                if (item.isAvailable) {
                    Surface(
                        shape = CircleShape,
                        color = IceCreamPink,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .size(34.dp)
                            .scale(addBtnScale)
                            .clip(CircleShape)
                            .clickable {
                                isPressed = true
                                onQuickAdd()
                            }
                            .testTag("quick_add_${item.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Thêm",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEEEEEE)
                    ) {
                        Text(
                            text = "Hết món",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onUpdateQty: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFFAF8F5),
        border = androidx.compose.foundation.BorderStroke(1.dp, IceCreamBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(item.menuItem.emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.menuItem.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = IceCreamTextPrimary
                )

                if (item.selectedToppings.isNotEmpty()) {
                    Text(
                        text = "+ ${item.selectedToppings.joinToString(", ") { it.name }}",
                        fontSize = 11.sp,
                        color = IceCreamCoral
                    )
                }

                if (item.note.isNotBlank()) {
                    Text(
                        text = "📝 ${item.note}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = formatVnd(item.totalPrice),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = IceCreamPink
                )
            }

            // Quantity stepper
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, IceCreamBorder),
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable { onUpdateQty(-1) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Remove, contentDescription = "Giảm", modifier = Modifier.size(14.dp))
                    }
                }

                Text(
                    text = "${item.quantity}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                Surface(
                    shape = CircleShape,
                    color = IceCreamPink,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable { onUpdateQty(1) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = "Tăng", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}
