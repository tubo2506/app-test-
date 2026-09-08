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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.OrderEntity
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
fun ManagerTablesQrTab(
    orders: List<OrderEntity>,
    onSelectTableForCustomerMode: (String) -> Unit,
    onClearTable: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var previewTable by remember { mutableStateOf<String?>(null) }
    var copiedLinkToast by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // 16 tables list
    val allTableList = remember { (1..16).map { "Bàn %02d".format(it) } }

    // Map active status for each table
    val activeTableOrders = remember(orders) {
        val map = mutableMapOf<String, OrderEntity>()
        orders.forEach { o ->
            if (o.status != "COMPLETED") {
                if (!map.containsKey(o.tableNumber)) {
                    map[o.tableNumber] = o
                }
            }
        }
        map
    }

    val occupiedCount = activeTableOrders.size
    val totalTables = allTableList.size
    val occupancyRate = (occupiedCount * 100) / totalTables

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("manager_tables_qr_tab")
    ) {
        // Top Occupancy Summary Banner
        item(span = { GridItemSpan(2) }) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = VibrantPinkLight,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.TableBar, contentDescription = null, tint = VibrantPink, modifier = Modifier.size(22.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "SƠ ĐỒ BÀN & QR STUDIO",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = VibrantTextPrimary
                            )
                            Text(
                                text = "$occupiedCount / $totalTables bàn đang có khách ($occupancyRate% công suất)",
                                fontSize = 11.sp,
                                color = VibrantTextSecondary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (occupancyRate >= 75) Color(0xFFFFF3E0) else Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = if (occupancyRate >= 75) "Giờ Cao Điểm" else "Đang Ổn Định",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (occupancyRate >= 75) Color(0xFFE65100) else VibrantGreenDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Section 1 Header: Khu Máy Lạnh
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("❄️ Khu Máy Lạnh Trong Nhà (Bàn 01 - Bàn 10)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
            }
        }

        // Tables 01 to 10
        items(allTableList.take(10)) { tableName ->
            val order = activeTableOrders[tableName]
            TableGridCard(
                tableName = tableName,
                activeOrder = order,
                onClick = { previewTable = tableName }
            )
        }

        // Section 2 Header: Khu Sân Vườn
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🌿 Khu Sân Vườn & Ban Công (Bàn 11 - Bàn 16)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
            }
        }

        // Tables 11 to 16
        items(allTableList.drop(10)) { tableName ->
            val order = activeTableOrders[tableName]
            TableGridCard(
                tableName = tableName,
                activeOrder = order,
                onClick = { previewTable = tableName }
            )
        }

        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // QR Preview & Studio Dialog
    previewTable?.let { tbl ->
        val activeOrder = activeTableOrders[tbl]

        Dialog(onDismissRequest = { previewTable = null }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MÃ QR GỌI MÓN $tbl",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = VibrantTextPrimary
                        )
                        IconButton(onClick = { previewTable = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // QR Card Design Mockup
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFFF7F9),
                        border = BorderStroke(1.5.dp, VibrantPink.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🍨 TIỆM KEM GELATO GEN Z", fontSize = 12.sp, fontWeight = FontWeight.Black, color = VibrantPinkDark)
                            Text("Quét QR gọi món & tích điểm thành viên", fontSize = 10.sp, color = VibrantTextMuted)

                            Spacer(modifier = Modifier.height(14.dp))

                            // Big Styled QR Icon
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                                modifier = Modifier.size(140.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode2,
                                        contentDescription = "Mã QR bàn",
                                        tint = Color(0xFF1E293B),
                                        modifier = Modifier.size(110.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(tbl, fontSize = 16.sp, fontWeight = FontWeight.Black, color = VibrantPink)
                            Text("Mã liên kết: gelato-pos://$tbl", fontSize = 10.sp, color = VibrantTextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Current status of this table
                    if (activeOrder != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFF3E0),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Bàn đang có đơn: ${activeOrder.orderId}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                    Text(activeOrder.itemsSummary.take(28) + "...", fontSize = 10.sp, color = VibrantTextSecondary)
                                }
                                Text(
                                    formatVnd(activeOrder.finalAmount),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = VibrantPink
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Copy QR link
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Table QR URL", "https://gelato-genz.app/table?id=$tbl")
                                clipboard.setPrimaryClip(clip)
                                copiedLinkToast = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (copiedLinkToast) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (copiedLinkToast) "Đã chép!" else "Sao chép link", fontSize = 11.sp)
                        }

                        // Test Check-in at this table
                        Button(
                            onClick = {
                                onSelectTableForCustomerMode(tbl)
                                previewTable = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mở Bàn Này", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Option to clear table if occupied
                    if (activeOrder != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                onClearTable(tbl)
                                previewTable = null
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                            border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dọn Bàn & Trả Về Trạng Thái Trống", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableGridCard(
    tableName: String,
    activeOrder: OrderEntity?,
    onClick: () -> Unit
) {
    val isOccupied = activeOrder != null

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isOccupied) Color(0xFFFFF9E6) else Color.White),
        border = BorderStroke(1.dp, if (isOccupied) Color(0xFFFFE082) else VibrantBorder),
        elevation = CardDefaults.cardElevation(if (isOccupied) 2.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tableName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = VibrantTextPrimary
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isOccupied) Color(0xFFFFF3E0) else Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = if (isOccupied) "Có khách" else "Trống",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOccupied) Color(0xFFE65100) else VibrantGreenDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isOccupied) {
                Text(
                    text = formatVnd(activeOrder.finalAmount),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = VibrantPink
                )
                Text(
                    text = "Trạng thái: ${activeOrder.status}",
                    fontSize = 10.sp,
                    color = VibrantTextSecondary
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = VibrantTextMuted, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sẵn sàng đón khách",
                        fontSize = 10.sp,
                        color = VibrantTextMuted
                    )
                }
            }
        }
    }
}

private fun formatVnd(amount: Long): String {
    return String.format(Locale.GERMANY, "%,d₫", amount)
}
