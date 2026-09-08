package com.example.ui.screens.manager

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MenuItemEntity
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantGreenDark
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkDark
import com.example.ui.theme.VibrantPinkLight
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary
import java.util.Locale

@Composable
fun ManagerMenuStockTab(
    menuItems: List<MenuItemEntity>,
    stockMap: Map<String, Int>,
    costMap: Map<String, Long>,
    onToggleAvailability: (String, Boolean) -> Unit,
    onQuickAddStock: (String, Int) -> Unit,
    onUpdateStock: (String, Int) -> Unit,
    onUpdateCost: (String, Long) -> Unit,
    onOpenAddNew: () -> Unit,
    onEditItem: (MenuItemEntity) -> Unit,
    onDeleteItem: (MenuItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Tất cả") }
    var stockFilter by remember { mutableStateOf("ALL") } // "ALL", "AVAILABLE", "LOW", "OUT"
    var adjustingStockItem by remember { mutableStateOf<MenuItemEntity?>(null) }

    val categories = listOf("Tất cả", "Kem viên", "Topping", "Đồ uống")

    // Filter items
    val filteredItems = remember(menuItems, searchQuery, selectedCategory, stockFilter, stockMap) {
        menuItems.filter { item ->
            val matchQuery = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.tag.contains(searchQuery, ignoreCase = true)
            val matchCategory = selectedCategory == "Tất cả" || item.category == selectedCategory
            val stock = stockMap[item.id] ?: 20
            val matchStock = when (stockFilter) {
                "AVAILABLE" -> item.isAvailable && stock > 8
                "LOW" -> item.isAvailable && stock in 1..8
                "OUT" -> !item.isAvailable || stock == 0
                else -> true
            }
            matchQuery && matchCategory && matchStock
        }
    }

    // High level KPIs
    val totalCount = menuItems.size
    val activeCount = menuItems.count { it.isAvailable }
    val lowStockCount = menuItems.count { (stockMap[it.id] ?: 20) in 1..8 && it.isAvailable }
    val outOfStockCount = menuItems.count { !it.isAvailable || (stockMap[it.id] ?: 20) == 0 }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("manager_menu_stock_tab")
    ) {
        // Top KPI summary row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StockSummaryBadge(
                title = "TỔNG MÓN",
                value = "$totalCount",
                color = VibrantTextPrimary,
                bgColor = Color(0xFFF3F4F6),
                modifier = Modifier.weight(1f)
            )
            StockSummaryBadge(
                title = "ĐANG BÁN",
                value = "$activeCount",
                color = VibrantGreenDark,
                bgColor = Color(0xFFE8F5E9),
                modifier = Modifier.weight(1f)
            )
            StockSummaryBadge(
                title = "SẮP HẾT",
                value = "$lowStockCount",
                color = Color(0xFFE65100),
                bgColor = Color(0xFFFFF3E0),
                modifier = Modifier.weight(1f)
            )
            StockSummaryBadge(
                title = "HẾT HÀNG",
                value = "$outOfStockCount",
                color = Color(0xFFD32F2F),
                bgColor = Color(0xFFFFEBEE),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search & Add new button row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Tìm theo tên món, vị kem...", fontSize = 12.sp, color = VibrantTextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = VibrantPink, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Xóa tìm kiếm", modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            )

            Button(
                onClick = onOpenAddNew,
                colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("manager_add_item_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Thêm Món", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Categories & Stock Filter Chips (horizontal scrollable)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) VibrantPink else Color.White,
                    border = BorderStroke(1.dp, if (isSelected) VibrantPink else VibrantBorder),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { selectedCategory = cat }
                ) {
                    Text(
                        text = cat,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else VibrantTextSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Stock filter fast pills
            listOf(
                "ALL" to "Tất cả kho",
                "AVAILABLE" to "🟢 Còn nhiều",
                "LOW" to "⚠️ Sắp hết (<8)",
                "OUT" to "🔴 Hết hàng"
            ).forEach { (key, label) ->
                val isSelected = stockFilter == key
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color(0xFF263238) else Color(0xFFF3F4F6),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { stockFilter = key }
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else Color(0xFF455A64),
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Menu items list with stock & margin breakdown
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredItems, key = { it.id }) { item ->
                val stock = stockMap[item.id] ?: 20
                val cost = costMap[item.id] ?: ((item.price * 38L) / 100L)
                val grossProfit = item.price - cost
                val marginPercent = if (item.price > 0) ((grossProfit * 100) / item.price).toInt() else 0

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(
                        1.dp,
                        if (!item.isAvailable || stock == 0) Color(0xFFFFCDD2) else VibrantBorder
                    ),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Header row: Emoji, Name, Price, Margin, Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = VibrantPinkLight,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(item.emoji, fontSize = 20.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = VibrantTextPrimary
                                        )
                                        if (item.tag.isNotBlank()) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = VibrantPinkLight
                                            ) {
                                                Text(
                                                    text = item.tag,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = VibrantPinkDark,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = formatVndShort(item.price),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = VibrantPink
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFE8F5E9)
                                        ) {
                                            Text(
                                                text = "Lãi: $marginPercent%",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = VibrantGreenDark,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Vốn: ${formatVndShort(cost)}",
                                            fontSize = 10.sp,
                                            color = VibrantTextMuted
                                        )
                                    }
                                }
                            }

                            // Availability switch
                            Switch(
                                checked = item.isAvailable && stock > 0,
                                onCheckedChange = { isChecked ->
                                    onToggleAvailability(item.id, isChecked)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = VibrantPink
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFF3F4F6))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Stock management row: Current stock badge + Quick add buttons + Edit/Delete actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Current Stock Badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when {
                                    !item.isAvailable || stock == 0 -> Color(0xFFFFEBEE)
                                    stock <= 8 -> Color(0xFFFFF3E0)
                                    else -> Color(0xFFE8F5E9)
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { adjustingStockItem = item }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when {
                                            !item.isAvailable || stock == 0 -> Icons.Default.Close
                                            stock <= 8 -> Icons.Default.Warning
                                            else -> Icons.Default.Inventory
                                        },
                                        contentDescription = null,
                                        tint = when {
                                            !item.isAvailable || stock == 0 -> Color(0xFFD32F2F)
                                            stock <= 8 -> Color(0xFFE65100)
                                            else -> VibrantGreenDark
                                        },
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (stock == 0 || !item.isAvailable) "Hết hàng (0)" else "Tồn: $stock suất",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            !item.isAvailable || stock == 0 -> Color(0xFFD32F2F)
                                            stock <= 8 -> Color(0xFFE65100)
                                            else -> VibrantGreenDark
                                        }
                                    )
                                }
                            }

                            // Quick restock + Edit + Delete
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                QuickStockBtn(label = "+5", onClick = { onQuickAddStock(item.id, 5) })
                                QuickStockBtn(label = "+10", onClick = { onQuickAddStock(item.id, 10) })

                                IconButton(onClick = { onEditItem(item) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa", tint = VibrantPinkDark, modifier = Modifier.size(16.dp))
                                }

                                IconButton(onClick = { onDeleteItem(item) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Xóa món", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Dialog: Adjust Stock & Cost precisely
    adjustAdjustingStockDialog(
        item = adjustingStockItem,
        currentStock = adjustingStockItem?.let { stockMap[it.id] ?: 20 } ?: 20,
        currentCost = adjustingStockItem?.let { costMap[it.id] ?: ((it.price * 38L) / 100L) } ?: 0L,
        onDismiss = { adjustingStockItem = null },
        onSave = { newStock, newCost ->
            adjustingStockItem?.let { itm ->
                onUpdateStock(itm.id, newStock)
                onUpdateCost(itm.id, newCost)
            }
            adjustingStockItem = null
        }
    )
}

@Composable
private fun QuickStockBtn(label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFF3F4F6),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = VibrantTextPrimary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun StockSummaryBadge(
    title: String,
    value: String,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun adjustAdjustingStockDialog(
    item: MenuItemEntity?,
    currentStock: Int,
    currentCost: Long,
    onDismiss: () -> Unit,
    onSave: (Int, Long) -> Unit
) {
    if (item == null) return

    var stockStr by remember(item) { mutableStateOf(currentStock.toString()) }
    var costStr by remember(item) { mutableStateOf(currentCost.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, VibrantBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(item.emoji, fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantTextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = stockStr,
                    onValueChange = { if (it.all { c -> c.isDigit() }) stockStr = it },
                    label = { Text("Số suất tồn kho trong ngày", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = costStr,
                    onValueChange = { if (it.all { c -> c.isDigit() }) costStr = it },
                    label = { Text("Giá vốn ước tính (COGS) VNĐ", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Hủy", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val parsedStock = stockStr.toIntOrNull() ?: currentStock
                            val parsedCost = costStr.toLongOrNull() ?: currentCost
                            onSave(parsedStock, parsedCost)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Lưu Thay Đổi", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun formatVndShort(amount: Long): String {
    return String.format(Locale.GERMANY, "%,d₫", amount)
}
