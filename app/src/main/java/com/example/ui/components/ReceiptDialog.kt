package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.OrderEntity
import com.example.ui.theme.IceCreamPink
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
fun ReceiptDialog(
    order: OrderEntity,
    paymentMethod: String?,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    val dateString = dateFormat.format(Date(order.createdAt))

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
                .testTag("e_receipt_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp),
            border = BorderStroke(1.dp, VibrantBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Top Close button & Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HÓA ĐƠN ĐIỆN TỬ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantPinkDark,
                        letterSpacing = 1.2.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Brand Header
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🍨", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "GELATO GEN Z",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = VibrantPink
                    )
                    Text(
                        text = "Chi nhánh 124 Nguyễn Huệ, Quận 1, TP.HCM",
                        fontSize = 11.sp,
                        color = VibrantTextSecondary
                    )
                    Text(
                        text = "Hotline: 1900 8888 • Wifi: GelatoGenZ_Free",
                        fontSize = 10.sp,
                        color = VibrantTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = VibrantBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // Meta Info
                ReceiptMetaRow("Mã hóa đơn:", order.orderId)
                ReceiptMetaRow("Bàn phục vụ:", order.tableNumber)
                ReceiptMetaRow("Thời gian:", dateString)
                if (!order.customerPhone.isNullOrBlank()) {
                    ReceiptMetaRow("Khách hàng:", order.customerPhone)
                }
                ReceiptMetaRow(
                    "Thanh toán:",
                    when (paymentMethod) {
                        "VIETQR" -> "Chuyển khoản VietQR (Đã thanh toán)"
                        "CASH" -> "Tiền mặt tại bàn"
                        else -> "Chưa thanh toán"
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = VibrantBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // Items Breakdown
                Text(
                    text = "CHI TIẾT MÓN:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = VibrantTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = order.itemsSummary,
                    fontSize = 13.sp,
                    color = VibrantTextPrimary,
                    lineHeight = 22.sp,
                    fontFamily = FontFamily.Monospace
                )

                if (order.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Ghi chú: ${order.note}",
                        fontSize = 11.sp,
                        color = VibrantPinkDark
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = VibrantBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // Totals
                ReceiptPriceRow("Tạm tính:", formatVnd(order.totalAmount))
                if (order.discountAmount > 0) {
                    ReceiptPriceRow(
                        "Giảm giá Voucher:",
                        "-${formatVnd(order.discountAmount)}",
                        valueColor = VibrantGreenDark
                    )
                }
                ReceiptPriceRow("Thuế VAT (0%):", "0₫")

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = VibrantBorder)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TỔNG CỘNG:",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = VibrantTextPrimary
                    )
                    Text(
                        text = formatVnd(order.finalAmount),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = VibrantPink
                    )
                }

                if (order.earnedPoints > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    ReceiptPriceRow("Điểm tích lũy nhận được:", "+${order.earnedPoints} điểm", valueColor = VibrantPinkDark)
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Thank you note
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Cảm ơn quý khách đã ghé thăm!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantTextPrimary
                    )
                    Text(
                        text = "Chúc bạn thưởng thức kem thật ngon miệng 🍨",
                        fontSize = 11.sp,
                        color = VibrantTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Đóng Hóa Đơn", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ReceiptMetaRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = VibrantTextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VibrantTextPrimary)
    }
}

@Composable
private fun ReceiptPriceRow(
    label: String,
    value: String,
    valueColor: Color = VibrantTextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = VibrantTextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
