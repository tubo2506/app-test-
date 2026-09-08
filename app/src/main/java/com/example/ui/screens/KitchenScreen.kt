package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.ui.ServiceCall
import com.example.ui.components.ShiftSummaryDialog
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatVnd
import com.example.ui.theme.IceCreamBorder
import com.example.ui.theme.IceCreamPink
import com.example.ui.theme.IceCreamPinkLight
import com.example.ui.theme.IceCreamTextPrimary
import com.example.ui.theme.IceCreamTextSecondary
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusPreparing
import com.example.ui.theme.StatusServing
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantGreenDark
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkDark
import com.example.ui.theme.VibrantPinkLight
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun KitchenScreen(
    orders: List<OrderEntity>,
    onAdvanceStatus: (String, String) -> Unit,
    serviceCalls: List<ServiceCall> = emptyList(),
    onResolveServiceCall: (String) -> Unit = {},
    menuItems: List<MenuItemEntity> = emptyList(),
    onToggleItemAvailability: (String, Boolean) -> Unit = { _, _ -> },
    orderPayments: Map<String, String> = emptyMap(),
    modifier: Modifier = Modifier
) {
    var selectedKitchenTab by remember { mutableStateOf("ORDERS") } // "ORDERS", "INVENTORY", or "REPORTS"
    var showShiftSummaryDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Fresh Modern Header (NO dark background)
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.2.dp, VibrantBorder),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = VibrantGreenDark,
                            modifier = Modifier.size(9.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ĐỒNG BỘ THỜI GIAN THỰC",
                            color = VibrantGreenDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Quầy Pha Chế & Quản Lý",
                        color = VibrantTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Sub-tabs switcher (Đơn hàng / Tồn kho / Báo cáo)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF6FAF8))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(11.dp),
                        color = if (selectedKitchenTab == "ORDERS") VibrantPink else Color.Transparent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(11.dp))
                            .clickable { selectedKitchenTab = "ORDERS" }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "📋 Đơn (${orders.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedKitchenTab == "ORDERS") Color.White else VibrantTextSecondary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(11.dp),
                        color = if (selectedKitchenTab == "INVENTORY") VibrantPink else Color.Transparent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(11.dp))
                            .clickable { selectedKitchenTab = "INVENTORY" }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val outOfStockCount = menuItems.count { !it.isAvailable }
                            Text(
                                if (outOfStockCount > 0) "📦 ($outOfStockCount hết)" else "📦 Kho",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedKitchenTab == "INVENTORY") Color.White else VibrantTextSecondary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(11.dp),
                        color = if (selectedKitchenTab == "REPORTS") VibrantPink else Color.Transparent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(11.dp))
                            .clickable { selectedKitchenTab = "REPORTS" }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "📊 Ca",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedKitchenTab == "REPORTS") Color.White else VibrantTextSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Active Service Calls Alert Card (Top priority banner for staff)
        if (serviceCalls.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                border = BorderStroke(1.2.dp, Color(0xFFFFB74D)),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "YÊU CẦU PHỤC VỤ TẠI BÀN (${serviceCalls.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFE65100),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        serviceCalls.forEach { call ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFFFE082)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "📍 ${call.tableNumber}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = VibrantTextPrimary
                                        )
                                        Text(
                                            text = call.requestText,
                                            fontSize = 12.sp,
                                            color = Color(0xFF5D4037)
                                        )
                                    }

                                    Button(
                                        onClick = { onResolveServiceCall(call.id) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Đã hỗ trợ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Tab Content
        if (selectedKitchenTab == "ORDERS") {
            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🍨", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Hiện chưa có đơn gọi món nào!",
                            color = IceCreamTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders) { order ->
                        KitchenOrderItemCard(
                            order = order,
                            paymentMethod = orderPayments[order.orderId],
                            onAdvanceStatus = onAdvanceStatus
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        } else if (selectedKitchenTab == "INVENTORY") {
            // Inventory Management Tab (Tồn kho món)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Bật / Tắt Tồn Kho Món Trực Tiếp",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantTextPrimary
                    )
                    Text(
                        text = "Món đánh dấu 'Tạm hết' sẽ hiển thị hết hàng trên thực đơn của khách ngay lập tức.",
                        fontSize = 11.sp,
                        color = VibrantTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = VibrantBorder)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(menuItems) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(item.emoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = item.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (item.isAvailable) VibrantTextPrimary else Color.Gray
                                        )
                                        Text(
                                            text = "${item.category} • ${formatVnd(item.price)}",
                                            fontSize = 11.sp,
                                            color = VibrantTextSecondary
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (item.isAvailable) "Còn món" else "Tạm hết",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.isAvailable) VibrantGreenDark else Color(0xFFD32F2F)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = item.isAvailable,
                                        onCheckedChange = { isAvailable ->
                                            onToggleItemAvailability(item.id, isAvailable)
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = VibrantPink,
                                            uncheckedThumbColor = Color.White,
                                            uncheckedTrackColor = Color.LightGray
                                        )
                                    )
                                }
                            }
                            HorizontalDivider(color = Color(0xFFF0F5F2))
                        }
                    }
                }
            }
        } else {
            // REPORTS: Sales & Shift Reports Dashboard
            KitchenShiftReportContent(
                orders = orders,
                orderPayments = orderPayments,
                onOpenShiftClosing = { showShiftSummaryDialog = true },
                modifier = Modifier.weight(1f)
            )
        }

        if (showShiftSummaryDialog) {
            ShiftSummaryDialog(
                orders = orders,
                orderPayments = orderPayments,
                onDismiss = { showShiftSummaryDialog = false },
                onConfirmCloseShift = {
                    showShiftSummaryDialog = false
                }
            )
        }
    }
}

@Composable
fun KitchenShiftReportContent(
    orders: List<OrderEntity>,
    orderPayments: Map<String, String>,
    onOpenShiftClosing: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalRevenue = orders.sumOf { it.totalAmount }
    val completedCount = orders.count { it.status == "COMPLETED" }
    val qrPaidOrders = orders.filter { orderPayments[it.orderId] == "VIETQR" }
    val cashPaidOrders = orders.filter { orderPayments[it.orderId] == "CASH" }
    val unpaidOrders = orders.filter { !orderPayments.containsKey(it.orderId) }

    val qrAmount = qrPaidOrders.sumOf { it.totalAmount }
    val cashAmount = cashPaidOrders.sumOf { it.totalAmount }

    // Popular items tally
    val itemCounts = remember(orders) {
        val map = mutableMapOf<String, Int>()
        orders.forEach { order ->
            order.itemsSummary.split("•").forEach { part ->
                val trimmed = part.trim()
                if (trimmed.isNotEmpty()) {
                    val name = trimmed.substringBefore(" x").trim().ifEmpty { trimmed }
                    map[name] = (map[name] ?: 0) + 1
                }
            }
        }
        map.entries.sortedByDescending { it.value }.take(4)
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            // Revenue Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF6FAF8)),
                    border = BorderStroke(1.dp, Color(0xFFE0ECE6)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("TỔNG DOANH THU", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VibrantTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(formatVnd(totalRevenue), fontSize = 18.sp, fontWeight = FontWeight.Black, color = VibrantGreenDark)
                        Text("${orders.size} đơn gọi món", fontSize = 11.sp, color = VibrantTextSecondary)
                    }
                }

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F5)),
                    border = BorderStroke(1.dp, Color(0xFFFFD6E7)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("ĐÃ PHỤC VỤ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VibrantPinkDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$completedCount / ${orders.size}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = VibrantPink)
                        val rate = if (orders.isNotEmpty()) (completedCount * 100 / orders.size) else 0
                        Text("Tỷ lệ $rate% hoàn thành", fontSize = 11.sp, color = VibrantTextSecondary)
                    }
                }
            }
        }

        // Payment Method breakdown card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("PHƯƠNG THỨC THANH TOÁN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VibrantTextSecondary)
                        Text("${orders.size} giao dịch", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📱", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Chuyển khoản VietQR (${qrPaidOrders.size})", fontSize = 12.sp, color = VibrantTextPrimary)
                        }
                        Text(formatVnd(qrAmount), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VibrantGreenDark)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💵", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tiền mặt thu ngân (${cashPaidOrders.size})", fontSize = 12.sp, color = VibrantTextPrimary)
                        }
                        Text(formatVnd(cashAmount), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                    }

                    if (unpaidOrders.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⏳ Chưa thu (${unpaidOrders.size} đơn đang dùng)", fontSize = 12.sp, color = Color(0xFFD32F2F))
                            Text(formatVnd(unpaidOrders.sumOf { it.totalAmount }), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                        }
                    }
                }
            }
        }

        // Top Selling items card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = VibrantPink, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("TOP MÓN BÁN CHẠY NHẤT CA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VibrantTextSecondary)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    if (itemCounts.isEmpty()) {
                        Text("Chưa có dữ liệu gọi món ca này", fontSize = 12.sp, color = VibrantTextSecondary)
                    } else {
                        itemCounts.forEachIndexed { index, entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (index == 0) Color(0xFFFFD54F) else Color(0xFFECEFF1),
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                "${index + 1}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (index == 0) Color(0xFF5D4037) else Color.DarkGray
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(entry.key, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = VibrantTextPrimary)
                                }
                                Text("${entry.value} lượt", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VibrantPink)
                            }
                        }
                    }
                }
            }
        }

        // Shift Handover button
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onOpenShiftClosing,
                colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("open_shift_handover_btn")
            ) {
                Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Biên Bản Bàn Giao & Kết Ca", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun KitchenOrderItemCard(
    order: OrderEntity,
    paymentMethod: String?,
    onAdvanceStatus: (String, String) -> Unit
) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val timeString = timeFormat.format(Date(order.createdAt))

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, IceCreamBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("kitchen_order_${order.orderId}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Table, Order ID, Time, and Payment Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.tableNumber,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = VibrantPink
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "#${order.orderId.takeLast(4)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = IceCreamTextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Payment badge
                    if (paymentMethod != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = if (paymentMethod == "VIETQR") "VIETQR ✓" else "TIỀN MẶT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantGreenDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFF3E0),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "CHƯA TRẢ",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = timeString,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = IceCreamBorder)
            Spacer(modifier = Modifier.height(10.dp))

            // Items summary
            Text(
                text = order.itemsSummary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = IceCreamTextPrimary,
                lineHeight = 20.sp
            )

            if (order.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = IceCreamPinkLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "📝 Lưu ý khách: ${order.note}",
                        fontSize = 12.sp,
                        color = IceCreamPink,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Status bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = order.status)

                // Quick next status transition buttons for real-time demonstration
                when (order.status) {
                    "RECEIVED" -> {
                        Button(
                            onClick = { onAdvanceStatus(order.orderId, "PREPARING") },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusPreparing),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("kitchen_advance_${order.orderId}")
                        ) {
                            Icon(Icons.Default.Dining, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Bắt đầu múc kem", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    "PREPARING" -> {
                        Button(
                            onClick = { onAdvanceStatus(order.orderId, "SERVING") },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusServing),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("kitchen_advance_${order.orderId}")
                        ) {
                            Icon(Icons.Default.RoomService, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Bưng ra bàn", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    "SERVING" -> {
                        Button(
                            onClick = { onAdvanceStatus(order.orderId, "COMPLETED") },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusCompleted),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("kitchen_advance_${order.orderId}")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Đã phục vụ xong", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    else -> {
                        Text(
                            text = "✅ Đơn hoàn tất",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusCompleted
                        )
                    }
                }
            }
        }
    }
}
