package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.FreshBerry
import com.example.ui.theme.FreshCyan
import com.example.ui.theme.FreshMintBg
import com.example.ui.theme.FreshMintNeon
import com.example.ui.theme.QrLaserColor
import com.example.ui.theme.QrLaserGlow
import com.example.ui.theme.VibrantBg
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantGreenBg
import com.example.ui.theme.VibrantGreenDark
import com.example.ui.theme.VibrantGreenText
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkLight
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary
import kotlinx.coroutines.delay

data class TableQrItem(
    val tableId: String,
    val name: String,
    val zone: String,
    val icon: String
)

val SampleTableQrs = listOf(
    TableQrItem("Bàn 01", "Bàn 01", "🌿 Sân vườn mát rượi", "🌱"),
    TableQrItem("Bàn 02", "Bàn 02", "🪟 Góc chill view kính", "🌸"),
    TableQrItem("Bàn 03", "Bàn 03", "🍦 Khu Vườn Kem mát lạnh", "🍨"),
    TableQrItem("Bàn 04", "Bàn 04", "🍹 Quầy Bar sảng khoái", "🍉"),
    TableQrItem("Bàn 05", "Bàn 05", "🛋️ Sofa VIP Lounge", "👑"),
    TableQrItem("Bàn 06", "Bàn 06", "🎈 Bàn tiệc nhóm bạn", "🎉"),
    TableQrItem("Bàn 07", "Bàn 07", "✨ Sảnh chính trung tâm", "⭐"),
    TableQrItem("Bàn 08", "Bàn 08", "🌅 Ban công ngắm phố", "🌇")
)

/**
 * Modern & Refreshing QR Code Scanner & Check-in Dialog
 * Allows customers to scan table QR code to select their table, and optionally
 * enter phone number for automatic loyalty points accumulation.
 */
@Composable
fun QrCheckInDialog(
    currentTable: String,
    currentPhone: String,
    onConfirmCheckIn: (table: String, phone: String?) -> Unit,
    onDismiss: () -> Unit,
    canDismiss: Boolean = true
) {
    var selectedTable by remember { mutableStateOf(currentTable.ifBlank { "Bàn 03" }) }
    var phoneInput by remember { mutableStateOf(currentPhone) }
    var isTorchOn by remember { mutableStateOf(false) }
    var isScannedSuccess by remember { mutableStateOf(false) }
    var scannedTableName by remember { mutableStateOf(currentTable.ifBlank { "Bàn 03" }) }

    // Smooth laser animation
    val infiniteTransition = rememberInfiniteTransition(label = "laserTransition")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserProgress"
    )

    val reticlePulse by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "reticlePulse"
    )

    Dialog(
        onDismissRequest = { if (canDismiss) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 20.dp)
                .testTag("qr_checkin_dialog"),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, VibrantBorder),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar with Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = FreshMintBg,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = FreshMintNeon,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "QUÉT QR TẠI BÀN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FreshBerry,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = "Vào Bàn & Gọi Món",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = VibrantTextPrimary
                            )
                        }
                    }

                    if (canDismiss) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(VibrantPinkLight)
                                .testTag("close_qr_dialog_button")
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Đóng",
                                tint = VibrantTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Modern Fresh QR Scanner Viewfinder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                            )
                        )
                        .testTag("qr_scanner_viewfinder"),
                    contentAlignment = Alignment.Center
                ) {
                    // Scanning laser canvas with reticle corners
                    Canvas(
                        modifier = Modifier
                            .size(160.dp)
                            .scale(reticlePulse)
                    ) {
                        val w = size.width
                        val h = size.height
                        val cornerLen = 28.dp.toPx()
                        val strokeW = 4.dp.toPx()
                        val cornerColor = if (isScannedSuccess) VibrantGreenText else FreshMintNeon

                        // Top-left corner
                        drawLine(cornerColor, Offset(0f, 0f), Offset(cornerLen, 0f), strokeW)
                        drawLine(cornerColor, Offset(0f, 0f), Offset(0f, cornerLen), strokeW)

                        // Top-right corner
                        drawLine(cornerColor, Offset(w, 0f), Offset(w - cornerLen, 0f), strokeW)
                        drawLine(cornerColor, Offset(w, 0f), Offset(w, cornerLen), strokeW)

                        // Bottom-left corner
                        drawLine(cornerColor, Offset(0f, h), Offset(cornerLen, h), strokeW)
                        drawLine(cornerColor, Offset(0f, h), Offset(0f, h - cornerLen), strokeW)

                        // Bottom-right corner
                        drawLine(cornerColor, Offset(w, h), Offset(w - cornerLen, h), strokeW)
                        drawLine(cornerColor, Offset(w, h), Offset(w, h - cornerLen), strokeW)

                        // Sweeping glowing laser line
                        val laserY = h * laserProgress
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    QrLaserGlow,
                                    QrLaserColor,
                                    QrLaserGlow,
                                    Color.Transparent
                                ),
                                startY = laserY - 18f,
                                endY = laserY + 18f
                            ),
                            topLeft = Offset(8f, laserY - 14f),
                            size = Size(w - 16f, 28f)
                        )
                        drawLine(
                            color = QrLaserColor,
                            start = Offset(4f, laserY),
                            end = Offset(w - 4f, laserY),
                            strokeWidth = 3.dp.toPx()
                        )
                    }

                    // QR Code icon inside center
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !isScannedSuccess,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.QrCode,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.35f),
                                modifier = Modifier.size(68.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Hướng camera vào mã QR tại bàn",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Success animation overlay
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isScannedSuccess,
                        enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = VibrantGreenBg.copy(alpha = 0.95f),
                            border = BorderStroke(1.5.dp, VibrantGreenText),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = VibrantGreenDark,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "ĐÃ QUÉT THÀNH CÔNG!",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = VibrantGreenDark
                                    )
                                    Text(
                                        text = scannedTableName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VibrantTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Flash / Torch toggle
                    IconButton(
                        onClick = { isTorchOn = !isTorchOn },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isTorchOn) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Bật đèn flash",
                            tint = if (isTorchOn) Color(0xFF2C2523) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Live Scan Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(FreshMintNeon)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "LIVE SCAN",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Table QR Quick Simulation Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mã QR các bàn trong quán:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantTextSecondary
                    )
                    Text(
                        text = "(Chạm để quét mã)",
                        fontSize = 11.sp,
                        color = FreshBerry,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Horizontal scrollable table QR chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SampleTableQrs.forEach { qrItem ->
                        val isSelected = selectedTable == qrItem.tableId
                        val chipBg by animateColorAsState(
                            targetValue = if (isSelected) VibrantPinkLight else Color(0xFFFAFAFA),
                            label = "chipBg"
                        )
                        val chipBorder by animateColorAsState(
                            targetValue = if (isSelected) VibrantPink else VibrantBorder,
                            label = "chipBorder"
                        )

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    selectedTable = qrItem.tableId
                                    scannedTableName = "${qrItem.name} • ${qrItem.zone}"
                                    isScannedSuccess = true
                                }
                                .testTag("table_qr_${qrItem.tableId.replace(" ", "_")}"),
                            shape = RoundedCornerShape(16.dp),
                            color = chipBg,
                            border = BorderStroke(1.2.dp, chipBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(qrItem.icon, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = qrItem.name,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                        color = if (isSelected) VibrantPink else VibrantTextPrimary
                                    )
                                    Text(
                                        text = qrItem.zone,
                                        fontSize = 9.sp,
                                        color = VibrantTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Loyalty Phone Accumulation Section (Optional as requested)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = FreshMintBg),
                    border = BorderStroke(1.dp, FreshMintNeon.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Loyalty,
                                contentDescription = null,
                                tint = VibrantGreenDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TÍCH ĐIỂM THÀNH VIÊN (TUỲ CHỌN)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantGreenDark,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Nhập SĐT để tự động tích điểm thưởng và nhận voucher giảm giá. Có thể để trống nếu bạn muốn gọi món ngay.",
                            fontSize = 11.sp,
                            color = VibrantTextSecondary,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { input ->
                                phoneInput = input.filter { it.isDigit() }.take(11)
                            },
                            placeholder = {
                                Text("Ví dụ: 0987 654 321", fontSize = 13.sp, color = VibrantTextMuted)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = VibrantGreenDark)
                            },
                            trailingIcon = {
                                if (phoneInput.isNotEmpty()) {
                                    IconButton(onClick = { phoneInput = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Xoá", tint = Color.Gray)
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantPink,
                                unfocusedBorderColor = Color(0xFFC8E6C9),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("qr_checkin_phone_field")
                        )

                        if (phoneInput.length >= 9) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = VibrantPink,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Đã liên kết tài khoản $phoneInput với đơn hàng",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VibrantPink
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Group Ordering & Table Lock Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                    border = BorderStroke(1.dp, Color(0xFFCE93D8).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF8E24AA).copy(alpha = 0.15f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Group,
                                    contentDescription = null,
                                    tint = Color(0xFF8E24AA),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Gọi Món Nhóm & Khóa Bàn Tự Động",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6A1B9A)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF8E24AA),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Nhiều người cùng quét mã tại $selectedTable đều có thể gọi thêm món chung vào bàn này. Điểm thưởng sẽ tích lũy theo SĐT đặt hàng.",
                                fontSize = 10.sp,
                                color = VibrantTextSecondary,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Confirm Check-in Button
                val buttonScale by animateFloatAsState(
                    targetValue = if (isScannedSuccess) 1.03f else 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "btnScale"
                )

                Button(
                    onClick = {
                        val cleanPhone = phoneInput.trim().takeIf { it.length >= 9 }
                        onConfirmCheckIn(selectedTable, cleanPhone)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .scale(buttonScale)
                        .testTag("confirm_qr_checkin_button"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Vào $selectedTable & Xem Thực Đơn 🍨",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (canDismiss) {
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("skip_qr_checkin_button")
                    ) {
                        Text(
                            text = "Bỏ qua, chỉ xem menu",
                            fontSize = 12.sp,
                            color = VibrantTextSecondary
                        )
                    }
                }
            }
        }
    }
}
