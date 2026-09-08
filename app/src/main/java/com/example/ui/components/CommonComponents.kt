package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusPreparing
import com.example.ui.theme.StatusReceived
import com.example.ui.theme.StatusServing
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantCoral
import com.example.ui.theme.VibrantGreenBg
import com.example.ui.theme.VibrantGreenDark
import com.example.ui.theme.VibrantGreenText
import com.example.ui.theme.VibrantLavender
import com.example.ui.theme.VibrantMint
import com.example.ui.theme.VibrantPeachEnd
import com.example.ui.theme.VibrantPeachStart
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkLight
import com.example.ui.theme.VibrantPinkSubtle
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary
import java.text.NumberFormat
import java.util.Locale

fun formatVnd(amount: Long): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return formatter.format(amount).replace("₫", "").trim() + "₫"
}

@Composable
fun TableQrStatusHeader(
    selectedTable: String,
    customerPhone: String?,
    onOpenQrScanner: () -> Unit,
    onOpenServiceCall: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseDot")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("table_qr_status_bar"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.2.dp, VibrantBorder),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Table info and status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Glowing dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(VibrantGreenText)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = selectedTable,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = VibrantTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(VibrantGreenBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = VibrantGreenDark,
                                    modifier = Modifier.size(9.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Đã khóa bàn theo QR",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantGreenDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (!customerPhone.isNullOrBlank()) "👑 SĐT $customerPhone • Đang tích điểm nhóm" else "Chạm để quét bàn khác hoặc nhập SĐT",
                        fontSize = 11.sp,
                        color = if (!customerPhone.isNullOrBlank()) VibrantPink else VibrantTextSecondary,
                        fontWeight = if (!customerPhone.isNullOrBlank()) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }

            // Right: Service Call Button & Button to re-scan QR
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Call Service Button
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFFF3E0),
                    border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.5f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onOpenServiceCall() }
                        .testTag("service_call_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔔", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Gọi phục vụ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                }

                // Rescan / Change table button (Auto-locks table, tap to scan new table)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = VibrantPinkLight,
                    border = BorderStroke(1.dp, VibrantPink.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onOpenQrScanner() }
                        .testTag("rescan_qr_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = "Quét lại mã QR",
                            tint = VibrantPink,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Quét QR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantPink
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TableSelectorRow(
    selectedTable: String,
    onSelectTable: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tables = listOf("Bàn 01", "Bàn 02", "Bàn 03", "Bàn 04", "Bàn 05", "Bàn 06", "Bàn 07", "Bàn 08")
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tables.forEach { table ->
            val isSelected = table == selectedTable
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "tableScale"
            )
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) VibrantPink else Color.White,
                label = "tableBg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else VibrantTextPrimary,
                label = "tableText"
            )

            Surface(
                modifier = Modifier
                    .scale(scale)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSelectTable(table) }
                    .testTag("table_select_${table.replace(" ", "_")}"),
                shape = RoundedCornerShape(20.dp),
                color = bgColor,
                shadowElevation = if (isSelected) 4.dp else 1.dp,
                border = if (isSelected) null else BorderStroke(1.dp, VibrantBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSelected) "📍 $table" else table,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun TagBadge(tag: String, modifier: Modifier = Modifier) {
    if (tag.isBlank()) return
    val (bgColor, textColor) = when (tag.lowercase()) {
        "bestseller" -> VibrantPink to Color.White
        "hot" -> VibrantCoral to Color.White
        "mới" -> VibrantMint to Color.White
        "gen z hot" -> VibrantLavender to Color.White
        else -> Color(0xFFFFB300) to Color(0xFF3E2723)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = tag,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val (label, containerColor, textColor, dotColor) = when (status) {
        "RECEIVED" -> Quadruple("Đã nhận đơn", Color(0xFFEDE7F6), Color(0xFF5E35B1), Color(0xFF7E57C2))
        "PREPARING" -> Quadruple("Đang làm kem", VibrantGreenBg, VibrantGreenDark, VibrantGreenText)
        "SERVING" -> Quadruple("Đang mang ra", Color(0xFFE1F5FE), Color(0xFF0288D1), Color(0xFF29B6F6))
        "COMPLETED" -> Quadruple("Đã phục vụ", VibrantGreenBg, VibrantGreenDark, VibrantGreenText)
        else -> Quadruple(status, Color(0xFFF5F5F5), Color(0xFF616161), Color(0xFF9E9E9E))
    }

    Surface(
        modifier = modifier.clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = BorderStroke(1.dp, dotColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Pulsing dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(if (status == "PREPARING" || status == "SERVING") pulseScale else 1f)
                    .clip(CircleShape)
                    .background(dotColor)
            )

            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

/**
 * Minigame banner card styled per "Vibrant Palette" Design:
 * bg-gradient-to-br from-[#FFF0E0] to-[#FFCC80] rounded-[32px] p-6 border-4 border-white shadow-sm
 */
@Composable
fun MinigameBannerCard(
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(3.dp, Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("minigame_banner_card")
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(VibrantPeachStart, VibrantPeachEnd)
                    )
                )
                .padding(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Frosted pill tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x1F000000))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "CHỜ KEM KHÔNG CHÁN",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Hứng Kem Trúng Quà!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = VibrantTextPrimary,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Chơi ngay nhận voucher 20k & điểm thưởng",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = VibrantTextPrimary.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onPlayClick,
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .testTag("minigame_banner_play_button")
                    ) {
                        Icon(Icons.Default.Gamepad, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Chơi Ngay",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Fun decorative graphic
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(76.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🎮🍦", fontSize = 32.sp)
                    }
                }
            }
        }
    }
}
