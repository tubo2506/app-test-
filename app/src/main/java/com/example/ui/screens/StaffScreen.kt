package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.ui.ServiceCall
import com.example.ui.components.VietQrPaymentDialog
import com.example.ui.components.formatVnd
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantGreenDark
import com.example.ui.theme.VibrantMint
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkDark
import com.example.ui.theme.VibrantPinkLight
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StaffScreen(
    orders: List<OrderEntity>,
    menuItems: List<MenuItemEntity>,
    serviceCalls: List<ServiceCall>,
    orderPayments: Map<String, String>,
    bankName: String = "MB Bank (Ngân hàng Quân Đội)",
    bankAccount: String = "0987654321",
    bankOwner: String = "TIEM KEM GELATO GEN Z",
    onAdvanceStatus: (String, String) -> Unit,
    onResolveServiceCall: (String) -> Unit,
    onConfirmPayment: (String, String) -> Unit,
    onSwitchTable: (String, String) -> Unit,
    onClearTable: (String) -> Unit = {},
    onToggleItemAvailability: (String, Boolean) -> Unit,
    onCreatePosOrder: (String, List<String>, Long, String) -> Unit,
    onExitToCustomer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedStaffTab by remember { mutableStateOf("TABLES") } // "TABLES", "KITCHEN", "POS", "SERVICE"
    var selectedTableDetail by remember { mutableStateOf<String?>(null) }
    var tableToSwitch by remember { mutableStateOf<Pair<String, String>?>(null) } // (orderId, currentTable)

    val allTables = listOf(
        "Bàn 01", "Bàn 02", "Bàn 03", "Bàn 04",
        "Bàn 05", "Bàn 06", "Bàn 07", "Bàn 08",
        "Bàn 09", "Bàn 10", "Bàn 11", "Bàn 12",
        "Quầy Bar 01", "Quầy Bar 02", "Sân Vườn 01", "Sân Vườn 02"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFC))
            .padding(horizontal = 14.dp)
    ) {
        // Staff Header Role & Fast Exit Bar
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEDE7F6))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("👩‍🍳", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CHẾ ĐỘ NHÂN VIÊN & BẾP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A148C)
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFD1C4E9)),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onExitToCustomer() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Thoát về khách hàng",
                        tint = Color(0xFF6A1B9A),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Khóa / Thoát",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6A1B9A)
                    )
                }
            }
        }

        // Staff Header Navigation Tabs
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StaffNavigationChip(
                label = "Sơ đồ bàn",
                icon = Icons.Default.TableRestaurant,
                selected = selectedStaffTab == "TABLES",
                badge = null,
                onClick = { selectedStaffTab = "TABLES" },
                modifier = Modifier.weight(1f)
            )
            StaffNavigationChip(
                label = "Bếp KDS",
                icon = Icons.Default.Kitchen,
                selected = selectedStaffTab == "KITCHEN",
                badge = orders.count { it.status in listOf("RECEIVED", "PREPARING") }.takeIf { it > 0 },
                onClick = { selectedStaffTab = "KITCHEN" },
                modifier = Modifier.weight(1f)
            )
            StaffNavigationChip(
                label = "Tạo đơn POS",
                icon = Icons.Default.PointOfSale,
                selected = selectedStaffTab == "POS",
                badge = null,
                onClick = { selectedStaffTab = "POS" },
                modifier = Modifier.weight(1f)
            )
            StaffNavigationChip(
                label = "Chuông gọi",
                icon = Icons.Default.NotificationsActive,
                selected = selectedStaffTab == "SERVICE",
                badge = serviceCalls.size.takeIf { it > 0 },
                badgeColor = Color(0xFFD32F2F),
                onClick = { selectedStaffTab = "SERVICE" },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content body based on subtab
        when (selectedStaffTab) {
            "TABLES" -> {
                StaffTableMapContent(
                    allTables = allTables,
                    orders = orders,
                    serviceCalls = serviceCalls,
                    orderPayments = orderPayments,
                    onSelectTable = { selectedTableDetail = it }
                )
            }
            "KITCHEN" -> {
                KitchenScreen(
                    orders = orders,
                    onAdvanceStatus = onAdvanceStatus,
                    serviceCalls = serviceCalls,
                    onResolveServiceCall = onResolveServiceCall,
                    menuItems = menuItems,
                    onToggleItemAvailability = onToggleItemAvailability,
                    orderPayments = orderPayments
                )
            }
            "POS" -> {
                StaffQuickPosContent(
                    allTables = allTables,
                    menuItems = menuItems,
                    onSubmitOrder = { table, items, total, note ->
                        onCreatePosOrder(table, items, total, note)
                        selectedStaffTab = "TABLES"
                    }
                )
            }
            "SERVICE" -> {
                StaffServiceCallsContent(
                    serviceCalls = serviceCalls,
                    onResolveCall = onResolveServiceCall
                )
            }
        }
    }

    // Dialog for Table Detail & Fast Actions (Payment, Switch Table, View Items)
    selectedTableDetail?.let { tableName ->
        val tableOrders = orders.filter { it.tableNumber == tableName }
        val activeTableOrder = tableOrders.firstOrNull { it.status != "COMPLETED" } ?: tableOrders.firstOrNull()
        val tableCalls = serviceCalls.filter { it.tableNumber == tableName }

        TableDetailDialog(
            tableNumber = tableName,
            order = activeTableOrder,
            serviceCalls = tableCalls,
            paymentMethod = activeTableOrder?.let { orderPayments[it.orderId] },
            bankName = bankName,
            bankAccount = bankAccount,
            bankOwner = bankOwner,
            onDismiss = { selectedTableDetail = null },
            onAdvanceStatus = { next ->
                activeTableOrder?.let { onAdvanceStatus(it.orderId, next) }
            },
            onConfirmPayment = { method ->
                activeTableOrder?.let { onConfirmPayment(it.orderId, method) }
            },
            onRequestSwitchTable = {
                activeTableOrder?.let {
                    tableToSwitch = it.orderId to tableName
                }
            },
            onClearTable = {
                onClearTable(tableName)
                selectedTableDetail = null
            },
            onResolveCall = { callId -> onResolveServiceCall(callId) }
        )
    }

    // Dialog for Switching Table
    tableToSwitch?.let { (orderId, currentTable) ->
        SwitchTableDialog(
            currentTable = currentTable,
            availableTables = allTables.filter { it != currentTable },
            onDismiss = { tableToSwitch = null },
            onConfirm = { newTable ->
                onSwitchTable(orderId, newTable)
                tableToSwitch = null
                selectedTableDetail = null
            }
        )
    }
}

@Composable
fun StaffNavigationChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    badge: Int?,
    badgeColor: Color = VibrantPink,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (selected) VibrantPinkLight else Color.Transparent,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) VibrantPink else VibrantTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                if (badge != null && badge > 0) {
                    Surface(
                        shape = CircleShape,
                        color = badgeColor,
                        modifier = Modifier
                            .size(15.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$badge",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) VibrantPinkDark else VibrantTextSecondary
            )
        }
    }
}

@Composable
fun StaffTableMapContent(
    allTables: List<String>,
    orders: List<OrderEntity>,
    serviceCalls: List<ServiceCall>,
    orderPayments: Map<String, String>,
    onSelectTable: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Legend explanation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TableLegendItem(color = VibrantMint, label = "Trống")
            TableLegendItem(color = Color(0xFFFFB74D), label = "Đang ăn")
            TableLegendItem(color = Color(0xFF2E7D32), label = "Đã trả tiền")
            TableLegendItem(color = VibrantPink, label = "Chờ bếp")
            TableLegendItem(color = Color(0xFFEF5350), label = "Chuông")
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize().testTag("staff_table_grid")
        ) {
            items(allTables) { table ->
                val tableOrders = orders.filter { it.tableNumber == table }
                val activeOrder = tableOrders.firstOrNull { it.status != "COMPLETED" }
                val hasServiceCall = serviceCalls.any { it.tableNumber == table }
                val isPaid = activeOrder?.let { orderPayments.containsKey(it.orderId) } ?: false

                // Status calculation
                val tableState = when {
                    hasServiceCall -> "SERVICE"
                    activeOrder == null -> "EMPTY"
                    isPaid -> "PAID"
                    activeOrder.status in listOf("RECEIVED", "PREPARING") -> "COOKING"
                    else -> "OCCUPIED"
                }

                val cardBg = when (tableState) {
                    "EMPTY" -> Color.White
                    "SERVICE" -> Color(0xFFFFEBEE)
                    "COOKING" -> Color(0xFFFFF0F5)
                    "PAID" -> Color(0xFFE8F5E9)
                    else -> Color(0xFFFFF8E1)
                }

                val borderColor = when (tableState) {
                    "EMPTY" -> Color(0xFFE0E0E0)
                    "SERVICE" -> Color(0xFFEF5350)
                    "COOKING" -> VibrantPink
                    "PAID" -> Color(0xFF2E7D32)
                    else -> Color(0xFFFFB74D)
                }

                val statusText = when (tableState) {
                    "EMPTY" -> "🟢 Bàn trống"
                    "SERVICE" -> "🔔 CẦN HỖ TRỢ"
                    "COOKING" -> "🍨 Chờ múc kem"
                    "PAID" -> "✅ Đã trả tiền • Chờ dọn"
                    else -> "😋 Đang thưởng thức"
                }

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.5.dp, borderColor),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onSelectTable(table) }
                        .testTag("table_card_$table")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = table,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantTextPrimary
                            )
                            if (hasServiceCall) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFEF5350),
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.NotificationsActive,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = statusText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (tableState == "SERVICE") Color(0xFFC62828) else VibrantTextSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        if (activeOrder != null) {
                            Text(
                                text = formatVnd(activeOrder.finalAmount),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = VibrantGreenDark
                            )
                            Text(
                                text = activeOrder.itemsSummary.take(28) + if (activeOrder.itemsSummary.length > 28) "..." else "",
                                fontSize = 10.sp,
                                color = VibrantTextMuted,
                                maxLines = 1
                            )
                        } else {
                            Text(
                                text = "Sẵn sàng đón khách",
                                fontSize = 11.sp,
                                color = VibrantTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = color, modifier = Modifier.size(8.dp)) {}
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 11.sp, color = VibrantTextSecondary)
    }
}

@Composable
fun StaffQuickPosContent(
    allTables: List<String>,
    menuItems: List<MenuItemEntity>,
    onSubmitOrder: (table: String, items: List<String>, total: Long, note: String) -> Unit
) {
    var selectedTable by remember { mutableStateOf(allTables.first()) }
    var orderNote by remember { mutableStateOf("") }
    val itemQuantities = remember { mutableStateMapOf<String, Int>() }

    val totalAmount = remember(itemQuantities.values) {
        menuItems.sumOf { item ->
            val qty = itemQuantities[item.id] ?: 0
            qty * item.price
        }
    }

    val totalCount = itemQuantities.values.sum()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("staff_quick_pos_screen")
    ) {
        // Table Picker
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, VibrantBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("CHỌN BÀN GỌI MÓN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VibrantTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                LazyColumn(modifier = Modifier.height(72.dp)) {
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            allTables.take(8).forEach { tbl ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selectedTable == tbl) VibrantPink else Color(0xFFF0F0F2),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedTable = tbl }
                                ) {
                                    Text(
                                        text = tbl,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedTable == tbl) Color.White else VibrantTextPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            allTables.drop(8).forEach { tbl ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selectedTable == tbl) VibrantPink else Color(0xFFF0F0F2),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedTable = tbl }
                                ) {
                                    Text(
                                        text = tbl,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedTable == tbl) Color.White else VibrantTextPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Items List
        Text(
            text = "CHỌN MÓN TẠI QUẦY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = VibrantTextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(menuItems) { item ->
                val qty = itemQuantities[item.id] ?: 0
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, VibrantBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = item.emoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = item.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantTextPrimary
                                )
                                Text(
                                    text = formatVnd(item.price),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VibrantGreenDark
                                )
                            }
                        }

                        // Counter
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (qty > 0) {
                                IconButton(
                                    onClick = {
                                        if (qty <= 1) itemQuantities.remove(item.id)
                                        else itemQuantities[item.id] = qty - 1
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = null, tint = VibrantPink)
                                }
                                Text(
                                    text = "$qty",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                            }
                            IconButton(
                                onClick = { itemQuantities[item.id] = qty + 1 },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = VibrantPink)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Note field
        OutlinedTextField(
            value = orderNote,
            onValueChange = { orderNote = it },
            placeholder = { Text("Ghi chú cho bếp (vd: mang về, ít ngọt, ly giấy)", fontSize = 12.sp) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VibrantPink,
                unfocusedBorderColor = VibrantBorder
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Submit Button
        Button(
            onClick = {
                if (totalCount > 0) {
                    val summaryList = menuItems.filter { (itemQuantities[it.id] ?: 0) > 0 }.map {
                        "${it.name} x${itemQuantities[it.id]}"
                    }
                    onSubmitOrder(selectedTable, summaryList, totalAmount, orderNote)
                    itemQuantities.clear()
                    orderNote = ""
                }
            },
            enabled = totalCount > 0,
            colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("submit_pos_order_button")
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Gửi vào Bếp: $selectedTable • ${formatVnd(totalAmount)}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun StaffServiceCallsContent(
    serviceCalls: List<ServiceCall>,
    onResolveCall: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("staff_service_calls_screen")
    ) {
        Text(
            text = "YÊU CẦU PHỤC VỤ TẠI BÀN (${serviceCalls.size})",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = VibrantTextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (serviceCalls.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔔", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Không có yêu cầu nào đang chờ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantTextPrimary
                    )
                    Text(
                        "Tất cả các bàn đang được phục vụ chu đáo",
                        fontSize = 12.sp,
                        color = VibrantTextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(serviceCalls) { call ->
                    val timeString = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(call.timestamp))
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        border = BorderStroke(1.2.dp, Color(0xFFEF5350)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = call.tableNumber,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFC62828)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = timeString,
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = call.requestText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = VibrantTextPrimary
                                )
                            }

                            Button(
                                onClick = { onResolveCall(call.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Đã hỗ trợ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableDetailDialog(
    tableNumber: String,
    order: OrderEntity?,
    serviceCalls: List<ServiceCall>,
    paymentMethod: String?,
    bankName: String = "MB Bank (Ngân hàng Quân Đội)",
    bankAccount: String = "0987654321",
    bankOwner: String = "TIEM KEM GELATO GEN Z",
    onDismiss: () -> Unit,
    onAdvanceStatus: (String) -> Unit,
    onConfirmPayment: (String) -> Unit,
    onRequestSwitchTable: () -> Unit,
    onClearTable: () -> Unit = {},
    onResolveCall: (String) -> Unit
) {
    var showVietQrDialog by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, VibrantBorder),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("table_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = VibrantPinkLight,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.TableBar, contentDescription = null, tint = VibrantPink, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(tableNumber, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                            Text(
                                text = if (order != null) "Đơn #${order.orderId}" else "Bàn đang trống",
                                fontSize = 12.sp,
                                color = VibrantTextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.Gray)
                    }
                }

                // Service alert banner if any
                if (serviceCalls.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    serviceCalls.forEach { call ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFEBEE),
                            border = BorderStroke(1.dp, Color(0xFFEF5350)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🔔 ${call.requestText}", fontSize = 12.sp, color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                                OutlinedButton(
                                    onClick = { onResolveCall(call.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFC62828)),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Xong", fontSize = 10.sp, color = Color(0xFFC62828))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (order == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Bàn này chưa có khách gọi món", fontSize = 13.sp, color = VibrantTextSecondary)
                    }
                } else {
                    // Order items summary
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF9F9FB),
                        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("CHI TIẾT MÓN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VibrantTextSecondary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(order.itemsSummary, fontSize = 13.sp, color = VibrantTextPrimary, lineHeight = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFFE0E0E0))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tổng thanh toán:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(formatVnd(order.finalAmount), fontSize = 14.sp, fontWeight = FontWeight.Black, color = VibrantGreenDark)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status pill & Payment pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = VibrantPinkLight,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Trạng thái: ${order.status}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantPinkDark,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (paymentMethod != null) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (paymentMethod != null) "✅ Đã thanh toán ($paymentMethod)" else "⚠️ Chưa thanh toán",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (paymentMethod != null) VibrantGreenDark else Color(0xFFE65100),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Status advancement if cooking/preparing
                    if (order.status in listOf("RECEIVED", "PREPARING", "SERVING")) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (order.status == "RECEIVED") {
                                Button(
                                    onClick = { onAdvanceStatus("PREPARING") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(38.dp)
                                ) {
                                    Text("Bắt đầu chuẩn bị món 🍨", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else if (order.status == "PREPARING") {
                                Button(
                                    onClick = { onAdvanceStatus("SERVING") },
                                    colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(38.dp)
                                ) {
                                    Text("Đã múc xong • Mang ra bàn 🚀", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else if (order.status == "SERVING") {
                                Button(
                                    onClick = { onAdvanceStatus("COMPLETED") },
                                    colors = ButtonDefaults.buttonColors(containerColor = VibrantGreenDark),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(38.dp)
                                ) {
                                    Text("Đã phục vụ xong bàn 🍽️", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Action buttons (Chuyển bàn, Thu tiền, Chuyển trạng thái)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onRequestSwitchTable,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, VibrantBorder),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Đổi bàn", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showVietQrDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantGreenDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.2f).height(42.dp)
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Thu tiền", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Button to clear / reset table when customers finish dining
                    Button(
                        onClick = onClearTable,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (paymentMethod != null || order == null) VibrantMint else Color(0xFFECEFF1),
                            contentColor = if (paymentMethod != null || order == null) Color(0xFF1B5E20) else Color(0xFF37474F)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("clear_table_button")
                    ) {
                        Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (paymentMethod != null) "✨ Dọn bàn & Trả bàn trống đón khách" else "Dọn bàn / Đặt lại bàn trống",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showVietQrDialog && order != null) {
        VietQrPaymentDialog(
            order = order,
            currentPaymentMethod = paymentMethod,
            bankName = bankName,
            accountNumber = bankAccount,
            accountHolder = bankOwner,
            onDismiss = { showVietQrDialog = false },
            onConfirmPayment = { method ->
                onConfirmPayment(method)
                showVietQrDialog = false
                onDismiss()
            },
            onViewReceipt = {
                showVietQrDialog = false
            }
        )
    }
}

@Composable
fun SwitchTableDialog(
    currentTable: String,
    availableTables: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedTargetTable by remember { mutableStateOf(availableTables.firstOrNull() ?: "Bàn 01") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, VibrantBorder),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "CHUYỂN ĐƠN BÀN",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = VibrantTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Chuyển đơn từ $currentTable sang bàn mới:",
                    fontSize = 12.sp,
                    color = VibrantTextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.height(180.dp)
                ) {
                    items(availableTables) { tbl ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedTargetTable == tbl) VibrantPink else Color(0xFFF2F3F5),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedTargetTable = tbl }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
                                Text(
                                    tbl,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTargetTable == tbl) Color.White else VibrantTextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
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
                        onClick = { onConfirm(selectedTargetTable) },
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text("Chuyển bàn", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
