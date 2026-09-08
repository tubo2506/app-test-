package com.example.ui.screens.manager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.PeakHourStat
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantGreenDark
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkDark
import com.example.ui.theme.VibrantPinkLight
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary
import java.util.Calendar
import java.util.Locale

@Composable
fun ManagerAnalyticsDeepTab(
    orders: List<OrderEntity>,
    menuItems: List<MenuItemEntity>,
    orderPayments: Map<String, String>,
    onGenerateFinancialReport: () -> String,
    modifier: Modifier = Modifier
) {
    var selectedTimeframe by remember { mutableStateOf("TODAY") } // "TODAY", "7DAYS", "MONTH", "ALL"
    var showExportDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Filter orders based on timeframe
    val now = System.currentTimeMillis()
    val filteredOrders = remember(orders, selectedTimeframe) {
        val cal = Calendar.getInstance()
        when (selectedTimeframe) {
            "TODAY" -> {
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                val startOfDay = cal.timeInMillis
                orders.filter { it.createdAt >= startOfDay }
            }
            "7DAYS" -> {
                val sevenDaysAgo = now - 7L * 24 * 60 * 60 * 1000
                orders.filter { it.createdAt >= sevenDaysAgo }
            }
            "MONTH" -> {
                val thirtyDaysAgo = now - 30L * 24 * 60 * 60 * 1000
                orders.filter { it.createdAt >= thirtyDaysAgo }
            }
            else -> orders
        }
    }

    // Financial Metrics Calculation
    val grossSales = filteredOrders.sumOf { it.totalAmount }
    val voucherDiscounts = filteredOrders.sumOf { it.discountAmount }
    val netRevenue = grossSales - voucherDiscounts
    val orderCount = filteredOrders.size
    val aov = if (orderCount > 0) grossSales / orderCount else 0L

    val completedCount = filteredOrders.count { it.status == "SERVED" || it.status == "READY" }
    val fulfillmentRate = if (orderCount > 0) (completedCount * 100) / orderCount else 100

    // Payment methods breakdown
    val qrOrders = filteredOrders.filter { orderPayments[it.orderId] == "VIETQR" }
    val cashOrders = filteredOrders.filter { orderPayments[it.orderId] == "CASH" }
    val qrRevenue = qrOrders.sumOf { it.totalAmount }
    val cashRevenue = cashOrders.sumOf { it.totalAmount }

    // Channel breakdown
    val tableQrOrders = filteredOrders.filter { !it.orderId.startsWith("#POS") }
    val posOrders = filteredOrders.filter { it.orderId.startsWith("#POS") }

    // Peak Hours Calculation
    val peakHourStats = remember(filteredOrders) {
        calculatePeakHours(filteredOrders, grossSales)
    }

    // Item popularity & revenue contribution calculation
    val topItems = remember(filteredOrders, menuItems) {
        calculateTopFlavorSales(filteredOrders, menuItems)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("manager_analytics_deep_tab")
    ) {
        // Timeframe selector bar + Export report button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Timeframe pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    listOf(
                        "TODAY" to "Hôm nay",
                        "7DAYS" to "7 ngày",
                        "MONTH" to "Tháng này",
                        "ALL" to "Tất cả"
                    ).forEach { (key, label) ->
                        val isSelected = selectedTimeframe == key
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) VibrantPink else Color(0xFFF3F4F6),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedTimeframe = key }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else VibrantTextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Export Text button
                Button(
                    onClick = { showExportDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Xuất Báo Cáo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Financial Overview Card (Hero)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TỔNG DOANH THU THỰC NHẬN (NET)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantTextMuted,
                            letterSpacing = 0.5.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = "Chuẩn F&B",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantGreenDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = formatVnd(netRevenue),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = VibrantPink
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF3F4F6))
                    Spacer(modifier = Modifier.height(12.dp))

                    // 4 Sub-metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FinancialMetricItem(
                            label = "Doanh số gộp",
                            value = formatVnd(grossSales),
                            sub = "${orderCount} đơn hàng"
                        )
                        FinancialMetricItem(
                            label = "Chi phí Voucher",
                            value = "-${formatVnd(voucherDiscounts)}",
                            sub = "Khuyến mãi"
                        )
                        FinancialMetricItem(
                            label = "AOV (Đơn TB)",
                            value = formatVnd(aov),
                            sub = "Mỗi bàn"
                        )
                        FinancialMetricItem(
                            label = "Tỷ lệ giao món",
                            value = "$fulfillmentRate%",
                            sub = "$completedCount/$orderCount đơn"
                        )
                    }
                }
            }
        }

        // Peak Hours Analysis (Biểu đồ giờ cao điểm)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = VibrantPink, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Phân Bổ Doanh Số Theo Khung Giờ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantTextPrimary
                            )
                        }
                        Text(
                            text = "Peak Hours",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    peakHourStats.forEach { stat ->
                        val ratio = if (grossSales > 0) (stat.revenue.toFloat() / grossSales.toFloat()).coerceIn(0f, 1f) else 0f
                        val percent = (ratio * 100).toInt()

                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(stat.iconEmoji, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${stat.label} (${stat.timeSlot})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = VibrantTextPrimary
                                    )
                                }

                                Text(
                                    text = "${formatVnd(stat.revenue)} ($percent%) • ${stat.orderCount} đơn",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (percent >= 30) VibrantPink else VibrantTextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            LinearProgressIndicator(
                                progress = { ratio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (percent >= 30) VibrantPink else Color(0xFF64B5F6),
                                trackColor = Color(0xFFF3F4F6),
                                strokeCap = StrokeCap.Round
                            )
                        }
                    }
                }
            }
        }

        // Payment & Channel Breakdown
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Payment Mix
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, VibrantBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = VibrantPink, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Thanh Toán", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("🏦 VietQR Napas 247", fontSize = 11.sp, color = VibrantTextSecondary)
                        Text(formatVnd(qrRevenue), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VibrantPink)
                        Text("${qrOrders.size} giao dịch", fontSize = 10.sp, color = VibrantTextMuted)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("💵 Tiền Mặt (Cash)", fontSize = 11.sp, color = VibrantTextSecondary)
                        Text(formatVnd(cashRevenue), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VibrantGreenDark)
                        Text("${cashOrders.size} giao dịch", fontSize = 10.sp, color = VibrantTextMuted)
                    }
                }

                // Channel Mix
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, VibrantBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Assessment, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Kênh Đặt Món", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("📱 Khách quét QR Bàn", fontSize = 11.sp, color = VibrantTextSecondary)
                        Text("${tableQrOrders.size} đơn", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                        Text(formatVnd(tableQrOrders.sumOf { it.totalAmount }), fontSize = 10.sp, color = VibrantTextMuted)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("🛎️ Nhân viên POS Quầy", fontSize = 11.sp, color = VibrantTextSecondary)
                        Text("${posOrders.size} đơn", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF455A64))
                        Text(formatVnd(posOrders.sumOf { it.totalAmount }), fontSize = 10.sp, color = VibrantTextMuted)
                    }
                }
            }
        }

        // Top Flavors & Sales contribution
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = VibrantPink, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Top Món Sinh Lời & Bán Chạy Nhất",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    topItems.take(6).forEachIndexed { index, itemStat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (index == 0) VibrantPinkLight else Color(0xFFF3F4F6),
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "#${index + 1}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (index == 0) VibrantPink else VibrantTextSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(itemStat.emoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))

                                Column {
                                    Text(
                                        text = itemStat.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VibrantTextPrimary
                                    )
                                    Text(
                                        text = "${itemStat.quantity} phần đã bán",
                                        fontSize = 10.sp,
                                        color = VibrantTextMuted
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = formatVnd(itemStat.totalRevenue),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantPink
                                )
                                Text(
                                    text = "${itemStat.sharePercent}% doanh thu",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = VibrantGreenDark
                                )
                            }
                        }

                        if (index < 5) {
                            HorizontalDivider(color = Color(0xFFF9FAFB))
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Export Financial Text Dialog
    if (showExportDialog) {
        val reportContent = remember { onGenerateFinancialReport() }
        var copiedToast by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showExportDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📄 Báo Cáo Kế Toán Chi Tiết",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantTextPrimary
                        )
                        IconButton(onClick = { showExportDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8F9FA),
                        border = BorderStroke(1.dp, Color(0xFFE9ECEF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    ) {
                        LazyColumn(modifier = Modifier.padding(12.dp)) {
                            item {
                                Text(
                                    text = reportContent,
                                    fontSize = 11.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    color = Color(0xFF212529),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showExportDialog = false },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Đóng", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Gelato Financial Report", reportContent)
                                clipboard.setPrimaryClip(clip)
                                copiedToast = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (copiedToast) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (copiedToast) "Đã Sao Chép!" else "Sao Chép Text", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FinancialMetricItem(
    label: String,
    value: String,
    sub: String
) {
    Column {
        Text(label, fontSize = 10.sp, color = VibrantTextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
        Spacer(modifier = Modifier.height(1.dp))
        Text(sub, fontSize = 9.sp, color = VibrantTextSecondary)
    }
}

private fun calculatePeakHours(orders: List<OrderEntity>, totalRev: Long): List<PeakHourStat> {
    val cal = Calendar.getInstance()
    var countMorning = 0; var revMorning = 0L
    var countLunch = 0; var revLunch = 0L
    var countAfternoon = 0; var revAfternoon = 0L
    var countDinner = 0; var revDinner = 0L
    var countNight = 0; var revNight = 0L

    orders.forEach { o ->
        cal.timeInMillis = o.createdAt
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 8..10 -> { countMorning++; revMorning += o.totalAmount }
            in 11..13 -> { countLunch++; revLunch += o.totalAmount }
            in 14..16 -> { countAfternoon++; revAfternoon += o.totalAmount }
            in 17..19 -> { countDinner++; revDinner += o.totalAmount }
            else -> { countNight++; revNight += o.totalAmount }
        }
    }

    return listOf(
        PeakHourStat("08:00 - 11:00", "Buổi sáng", countMorning, revMorning, "🌅"),
        PeakHourStat("11:00 - 14:00", "Trưa ăn trưa", countLunch, revLunch, "☀️"),
        PeakHourStat("14:00 - 17:00", "Chiều tan học", countAfternoon, revAfternoon, "🌤️"),
        PeakHourStat("17:00 - 20:00", "Tối cao điểm", countDinner, revDinner, "🌙"),
        PeakHourStat("20:00 - 23:00", "Đêm hẹn hò", countNight, revNight, "✨")
    )
}

data class ItemFlavorStat(
    val id: String,
    val name: String,
    val emoji: String,
    val quantity: Int,
    val totalRevenue: Long,
    val sharePercent: Int
)

private fun calculateTopFlavorSales(orders: List<OrderEntity>, menuItems: List<MenuItemEntity>): List<ItemFlavorStat> {
    val mapCount = mutableMapOf<String, Int>()
    val mapRev = mutableMapOf<String, Long>()

    // Parse itemsSummary or item descriptions
    orders.forEach { o ->
        val parts = o.itemsSummary.split("•", ",")
        parts.forEach { p ->
            val clean = p.trim()
            menuItems.forEach { item ->
                if (clean.contains(item.name, ignoreCase = true)) {
                    val count = mapCount.getOrDefault(item.id, 0) + 1
                    mapCount[item.id] = count
                    mapRev[item.id] = mapRev.getOrDefault(item.id, 0L) + item.price
                }
            }
        }
    }

    val totalItemRevenue = mapRev.values.sum().coerceAtLeast(1L)
    return menuItems.map { item ->
        val qty = mapCount[item.id] ?: 0
        val rev = mapRev[item.id] ?: (qty * item.price)
        val share = ((rev * 100) / totalItemRevenue).toInt()
        ItemFlavorStat(
            id = item.id,
            name = item.name,
            emoji = item.emoji,
            quantity = qty,
            totalRevenue = rev,
            sharePercent = share
        )
    }.sortedByDescending { it.totalRevenue }
}

private fun formatVnd(amount: Long): String {
    return String.format(Locale.GERMANY, "%,d₫", amount)
}
