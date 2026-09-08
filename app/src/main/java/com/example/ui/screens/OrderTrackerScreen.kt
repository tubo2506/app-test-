package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.ui.components.MinigameBannerCard
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.StatusBadge
import com.example.ui.components.VietQrPaymentDialog
import com.example.ui.components.formatVnd
import com.example.ui.theme.IceCreamBorder
import com.example.ui.theme.IceCreamMint
import com.example.ui.theme.IceCreamPink
import com.example.ui.theme.IceCreamPinkLight
import com.example.ui.theme.IceCreamTextPrimary
import com.example.ui.theme.IceCreamTextSecondary
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusPreparing
import com.example.ui.theme.StatusReceived
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
fun OrderTrackerScreen(
    orders: List<OrderEntity>,
    activeOrder: OrderEntity?,
    orderPayments: Map<String, String> = emptyMap(),
    bankName: String = "MB Bank (Ngân hàng Quân Đội)",
    bankAccount: String = "0987654321",
    bankOwner: String = "TIEM KEM GELATO GEN Z",
    onConfirmPayment: (orderId: String, method: String) -> Unit = { _, _ -> },
    onOpenServiceCall: () -> Unit = {},
    onOpenFeedback: () -> Unit = {},
    onPlayMinigame: () -> Unit,
    onAddMoreItems: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showReceiptDialog by remember { mutableStateOf(false) }
    var selectedReceiptOrder by remember { mutableStateOf<OrderEntity?>(null) }

    if (orders.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text("🍨", fontSize = 56.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Chưa có đơn hàng nào!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = IceCreamTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Hãy chọn món từ thực đơn và gửi order nhé.",
                    fontSize = 13.sp,
                    color = IceCreamTextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onAddMoreItems,
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Xem thực đơn & Gọi món", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
        return
    }

    val displayOrder = activeOrder ?: orders.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Main Active Order Tracking Card matching Vibrant Palette rounded-[32px] & border-pink-50
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, IceCreamBorder),
                modifier = Modifier.testTag("active_order_card")
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            StatusBadge(status = displayOrder.status)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Đơn hàng ${displayOrder.orderId}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = IceCreamTextPrimary
                            )
                            Text(
                                text = "${displayOrder.tableNumber} • Đang phục vụ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = IceCreamTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Step Timeline
                    OrderTimelineStepView(currentStatus = displayOrder.status)
                }
            }

            if (displayOrder.status != "COMPLETED") {
                Spacer(modifier = Modifier.height(16.dp))
                // Minigame Banner Card matching Vibrant Palette HTML
                MinigameBannerCard(
                    onPlayClick = onPlayMinigame
                )
            } else {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFF9FBE7),
                    border = BorderStroke(1.2.dp, Color(0xFFC0CA33)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onOpenFeedback() }
                        .testTag("tracker_feedback_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("⭐", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Đánh giá trải nghiệm hôm nay?",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = VibrantTextPrimary
                                )
                                Text(
                                    text = "Góp ý nhanh để nhận ngay +5 điểm thưởng",
                                    fontSize = 11.sp,
                                    color = VibrantGreenDark
                                )
                            }
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = VibrantGreenDark,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Active Order Receipt Breakdown
        item {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, IceCreamBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = IceCreamPink)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Chi Tiết Món Gọi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = IceCreamTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = IceCreamBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = displayOrder.itemsSummary,
                        fontSize = 14.sp,
                        color = IceCreamTextPrimary,
                        lineHeight = 22.sp
                    )

                    if (displayOrder.note.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = IceCreamPinkLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "📝 Ghi chú: ${displayOrder.note}",
                                fontSize = 12.sp,
                                color = IceCreamPink,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = IceCreamBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tạm tính:", color = IceCreamTextSecondary, fontSize = 13.sp)
                        Text(formatVnd(displayOrder.totalAmount), color = IceCreamTextPrimary, fontSize = 13.sp)
                    }

                    if (displayOrder.discountAmount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Voucher giảm giá:", color = IceCreamMint, fontSize = 13.sp)
                            Text("-${formatVnd(displayOrder.discountAmount)}", color = IceCreamMint, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tổng thanh toán:", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = IceCreamTextPrimary)
                        Text(
                            formatVnd(displayOrder.finalAmount),
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = IceCreamPink
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Điểm tích lũy dự kiến:", color = IceCreamTextSecondary, fontSize = 12.sp)
                        Text("+${displayOrder.earnedPoints} điểm", color = IceCreamPink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = VibrantBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Payment and Action Controls
                    val isPaid = orderPayments.containsKey(displayOrder.orderId)
                    val paidMethod = orderPayments[displayOrder.orderId]

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TRẠNG THÁI THANH TOÁN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantTextSecondary,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            if (isPaid) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFE8F5E9)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = VibrantGreenDark, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (paidMethod == "VIETQR") "Đã thanh toán VietQR" else "Đã chọn Tiền mặt",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = VibrantGreenDark
                                        )
                                    }
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFFF3E0)
                                ) {
                                    Text(
                                        text = "Chưa thanh toán",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        // Right: View E-Receipt button
                        OutlinedButton(
                            onClick = {
                                selectedReceiptOrder = displayOrder
                                showReceiptDialog = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, VibrantBorder),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = VibrantPinkDark, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Xem hóa đơn", fontSize = 12.sp, color = VibrantTextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Big Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showPaymentDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("open_payment_btn")
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPaid) "Xem lại thanh toán" else "Thanh toán VietQR",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        // Call Staff Button on Order Tracker
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFFFF3E0),
                            border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.6f)),
                            modifier = Modifier
                                .height(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onOpenServiceCall() }
                                .testTag("tracker_service_call_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Gọi phục vụ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Re-order / Add more items to table button
                    OutlinedButton(
                        onClick = onAddMoreItems,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, VibrantPink.copy(alpha = 0.7f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VibrantPinkDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("reorder_add_more_btn")
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(17.dp), tint = VibrantPink)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🍨 Gọi thêm món vào ${displayOrder.tableNumber}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // All Orders History
        if (orders.size > 1) {
            item {
                Text(
                    text = "📜 Lịch Sử Các Đơn Trước",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = IceCreamTextPrimary
                )
            }

            items(orders.filter { it.orderId != displayOrder.orderId }) { pastOrder ->
                val timeFormat = SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault())
                val timeString = timeFormat.format(Date(pastOrder.createdAt))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            selectedReceiptOrder = pastOrder
                            showReceiptDialog = true
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${pastOrder.orderId} • ${pastOrder.tableNumber}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = pastOrder.itemsSummary,
                                fontSize = 12.sp,
                                color = IceCreamTextSecondary,
                                maxLines = 1
                            )
                            Text(
                                text = "$timeString • Chạm xem hóa đơn",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatVnd(pastOrder.finalAmount),
                                fontWeight = FontWeight.Bold,
                                color = IceCreamPink,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            StatusBadge(status = pastOrder.status)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Payment Dialog
    if (showPaymentDialog) {
        VietQrPaymentDialog(
            order = displayOrder,
            currentPaymentMethod = orderPayments[displayOrder.orderId],
            bankName = bankName,
            accountNumber = bankAccount,
            accountHolder = bankOwner,
            onDismiss = { showPaymentDialog = false },
            onConfirmPayment = { method ->
                onConfirmPayment(displayOrder.orderId, method)
            },
            onViewReceipt = {
                showPaymentDialog = false
                selectedReceiptOrder = displayOrder
                showReceiptDialog = true
            }
        )
    }

    // Receipt Dialog
    if (showReceiptDialog && selectedReceiptOrder != null) {
        ReceiptDialog(
            order = selectedReceiptOrder!!,
            paymentMethod = orderPayments[selectedReceiptOrder!!.orderId],
            onDismiss = {
                showReceiptDialog = false
                selectedReceiptOrder = null
            }
        )
    }
}

@Composable
fun OrderTimelineStepView(currentStatus: String) {
    val steps = listOf(
        "RECEIVED" to "Quán nhận đơn",
        "PREPARING" to "Đang múc kem",
        "SERVING" to "Đang mang ra",
        "COMPLETED" to "Đã phục vụ"
    )

    val currentStepIndex = when (currentStatus) {
        "RECEIVED" -> 0
        "PREPARING" -> 1
        "SERVING" -> 2
        "COMPLETED" -> 3
        else -> 0
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        steps.forEachIndexed { index, (statusKey, label) ->
            val isDone = index < currentStepIndex
            val isCurrent = index == currentStepIndex

            val dotColor = when {
                isDone -> StatusCompleted
                isCurrent -> when (statusKey) {
                    "RECEIVED" -> StatusReceived
                    "PREPARING" -> StatusPreparing
                    "SERVING" -> StatusServing
                    else -> StatusCompleted
                }
                else -> Color.LightGray.copy(alpha = 0.5f)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .scale(if (isCurrent) pulseScale else 1.0f)
                        .background(dotColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    } else if (isCurrent) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color.White, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = label,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp,
                        color = if (isCurrent) IceCreamTextPrimary else if (isDone) StatusCompleted else Color.Gray
                    )
                    if (isCurrent) {
                        Text(
                            text = when (statusKey) {
                                "RECEIVED" -> "Đơn hàng đã được ghi nhận trên hệ thống bếp"
                                "PREPARING" -> "Bếp đang múc kem & phối thêm topping nóng hổi..."
                                "SERVING" -> "Nhân viên đang bưng khay kem đến bàn của bạn!"
                                else -> "Chúc quý khách thưởng thức kem ngon miệng!"
                            },
                            fontSize = 11.sp,
                            color = IceCreamPink
                        )
                    }
                }
            }
        }
    }
}
