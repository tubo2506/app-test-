package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.OrderEntity
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantGreenDark
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkLight
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ShiftSummaryDialog(
    orders: List<OrderEntity>,
    orderPayments: Map<String, String>,
    onDismiss: () -> Unit,
    onConfirmCloseShift: () -> Unit
) {
    val totalRevenue = orders.sumOf { it.totalAmount }
    val qrPaidOrders = orders.filter { orderPayments[it.orderId] == "VIETQR" }
    val cashPaidOrders = orders.filter { orderPayments[it.orderId] == "CASH" }
    val unpaidOrders = orders.filter { !orderPayments.containsKey(it.orderId) }

    val qrAmount = qrPaidOrders.sumOf { it.totalAmount }
    val cashAmount = cashPaidOrders.sumOf { it.totalAmount }
    val unpaidAmount = unpaidOrders.sumOf { it.totalAmount }

    val completedOrdersCount = orders.count { it.status == "COMPLETED" }
    val timeFormat = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
    val currentTime = timeFormat.format(Date())

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.2.dp, VibrantBorder),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("shift_summary_dialog")
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
                                Icon(
                                    Icons.Default.Assessment,
                                    contentDescription = null,
                                    tint = VibrantPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Biên Bản Bàn Giao Ca",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantTextPrimary
                            )
                            Text(
                                text = currentTime,
                                fontSize = 11.sp,
                                color = VibrantTextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Big Revenue Banner
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF6FAF8),
                    border = BorderStroke(1.dp, Color(0xFFE0ECE6)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TỔNG DOANH THU CA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantTextSecondary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatVnd(totalRevenue),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = VibrantGreenDark
                        )
                        Text(
                            text = "${orders.size} đơn gọi món • $completedOrdersCount đơn đã hoàn tất phục vụ",
                            fontSize = 11.sp,
                            color = VibrantTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Breakdown list
                Text(
                    text = "CHI TIẾT KIỂM KÊ TIỀN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VibrantTextSecondary,
                    letterSpacing = 0.6.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Cash
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payments, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tiền mặt tại két (${cashPaidOrders.size} đơn):", fontSize = 12.sp, color = VibrantTextPrimary)
                    }
                    Text(formatVnd(cashAmount), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // VietQR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.QrCode, contentDescription = null, tint = VibrantGreenDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Chuyển khoản VietQR (${qrPaidOrders.size} đơn):", fontSize = 12.sp, color = VibrantTextPrimary)
                    }
                    Text(formatVnd(qrAmount), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VibrantGreenDark)
                }

                if (unpaidOrders.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚠️ Đơn đang phục vụ (chưa thu):", fontSize = 12.sp, color = Color(0xFFD32F2F))
                        Text(formatVnd(unpaidAmount), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = VibrantBorder)
                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, VibrantBorder),
                        modifier = Modifier.weight(1f).height(46.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, tint = VibrantTextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("In phiếu", fontSize = 12.sp, color = VibrantTextPrimary, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onConfirmCloseShift,
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1.3f).height(46.dp).testTag("confirm_close_shift_btn")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Xác nhận bàn giao", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
