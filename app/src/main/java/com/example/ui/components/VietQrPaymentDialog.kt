package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ReceiptLong
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.OrderEntity
import com.example.ui.theme.FreshBerry
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantGreenDark
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkDark
import com.example.ui.theme.VibrantPinkLight
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary

@Composable
fun VietQrPaymentDialog(
    order: OrderEntity,
    currentPaymentMethod: String?, // "VIETQR", "CASH", or null
    bankName: String = "MB Bank (Ngân hàng Quân Đội)",
    accountNumber: String = "0987654321",
    accountHolder: String = "TIEM KEM GELATO GEN Z",
    onDismiss: () -> Unit,
    onConfirmPayment: (method: String) -> Unit,
    onViewReceipt: () -> Unit
) {
    val context = LocalContext.current
    var selectedMethod by remember { mutableStateOf(currentPaymentMethod ?: "VIETQR") }
    var hasConfirmedSuccess by remember { mutableStateOf(currentPaymentMethod != null) }

    val transferNote = "${order.tableNumber.replace(" ", "")} HD${order.orderId.takeLast(4)}"

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Đã sao chép $label: $text", Toast.LENGTH_SHORT).show()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 20.dp)
                .testTag("vietqr_payment_dialog"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
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
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = VibrantPinkDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Thanh Toán Tại Bàn",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = VibrantTextPrimary
                            )
                            Text(
                                text = "${order.tableNumber} • Đơn #${order.orderId.takeLast(6)}",
                                fontSize = 12.sp,
                                color = VibrantPinkDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payment Method Selector Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (selectedMethod == "VIETQR") VibrantPinkLight else Color(0xFFF6FAF8),
                        border = BorderStroke(
                            1.2.dp,
                            if (selectedMethod == "VIETQR") VibrantPink else VibrantBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedMethod = "VIETQR" }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, tint = VibrantPink, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Chuyển VietQR",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedMethod == "VIETQR") VibrantPinkDark else VibrantTextPrimary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (selectedMethod == "CASH") VibrantPinkLight else Color(0xFFF6FAF8),
                        border = BorderStroke(
                            1.2.dp,
                            if (selectedMethod == "CASH") VibrantPink else VibrantBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedMethod = "CASH" }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("💵", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Tiền mặt tại bàn",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedMethod == "CASH") VibrantPinkDark else VibrantTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // VietQR Payment Display
                if (selectedMethod == "VIETQR") {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FDFB)),
                        border = BorderStroke(1.dp, VibrantBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Napas 247 & VietQR Header Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "NAPAS 247 • VIETQR CHUẨN",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Surface(
                                    color = VibrantPinkLight,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "TỰ ĐỘNG KHỚP LỆNH",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VibrantPinkDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Simulated QR Code Graphic Box with Scanner Border
                            Box(
                                modifier = Modifier
                                    .size(190.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .border(2.dp, VibrantPink, RoundedCornerShape(16.dp))
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.QrCode,
                                        contentDescription = "VietQR",
                                        tint = VibrantTextPrimary,
                                        modifier = Modifier.size(120.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = formatVnd(order.finalAmount),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = VibrantPinkDark
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Mở ứng dụng Mobile Banking bất kỳ quét mã trên",
                                fontSize = 12.sp,
                                color = VibrantTextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = VibrantBorder)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Bank Transfer Details with 1-click Copy
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                InfoCopyRow(
                                    label = "Ngân hàng",
                                    value = bankName,
                                    onCopy = null
                                )
                                InfoCopyRow(
                                    label = "Số tài khoản",
                                    value = accountNumber,
                                    onCopy = { copyToClipboard("Số tài khoản", accountNumber) }
                                )
                                InfoCopyRow(
                                    label = "Chủ tài khoản",
                                    value = accountHolder,
                                    onCopy = null
                                )
                                InfoCopyRow(
                                    label = "Số tiền thanh toán",
                                    value = formatVnd(order.finalAmount),
                                    valueColor = VibrantPinkDark,
                                    isBold = true,
                                    onCopy = { copyToClipboard("Số tiền", order.finalAmount.toString()) }
                                )
                                InfoCopyRow(
                                    label = "Nội dung chuyển khoản",
                                    value = transferNote,
                                    valueColor = FreshBerry,
                                    isBold = true,
                                    onCopy = { copyToClipboard("Nội dung", transferNote) }
                                )
                            }
                        }
                    }
                } else {
                    // Cash Option Display
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FDFB)),
                        border = BorderStroke(1.dp, VibrantBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("💵", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Thanh Toán Tiền Mặt Tại Bàn",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = VibrantTextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Sau khi chọn, nhân viên thu ngân sẽ mang hóa đơn và tiền thừa (nếu có) đến tận bàn của bạn.",
                                fontSize = 12.sp,
                                color = VibrantTextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = VibrantPinkLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Số tiền cần chuẩn bị:", fontSize = 13.sp, color = VibrantTextPrimary)
                                    Text(
                                        formatVnd(order.finalAmount),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        color = VibrantPinkDark
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                if (!hasConfirmedSuccess) {
                    Button(
                        onClick = {
                            hasConfirmedSuccess = true
                            onConfirmPayment(selectedMethod)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("confirm_payment_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedMethod == "VIETQR") "Xác Nhận Đã Chuyển Khoản" else "Yêu Cầu Thanh Toán Tiền Mặt",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = VibrantGreenDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (selectedMethod == "VIETQR") "Đã ghi nhận chuyển khoản VietQR" else "Đã gọi nhân viên thu tiền mặt",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantGreenDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // View E-Receipt Button
                OutlinedButton(
                    onClick = onViewReceipt,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, VibrantBorder)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = VibrantPinkDark, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Xem Hóa Đơn Điện Tử (E-Receipt)", fontSize = 13.sp, color = VibrantTextPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun InfoCopyRow(
    label: String,
    value: String,
    valueColor: Color = VibrantTextPrimary,
    isBold: Boolean = false,
    onCopy: (() -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = VibrantTextSecondary
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                color = valueColor,
                fontFamily = if (isBold) FontFamily.Monospace else FontFamily.Default
            )
            if (onCopy != null) {
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Sao chép",
                        tint = VibrantPinkDark,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
