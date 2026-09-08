package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FreshBerry
import com.example.ui.theme.FreshMintNeon
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantGreenDark
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkDark
import com.example.ui.theme.VibrantPinkLight
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary
import kotlinx.coroutines.delay

data class PromoDeal(
    val id: String,
    val badge: String,
    val icon: String,
    val title: String,
    val description: String,
    val actionText: String,
    val actionType: String // "VOUCHER", "CATEGORY_KEM", "CATEGORY_DRINK"
)

val SamplePromoDeals = listOf(
    PromoDeal(
        id = "PROMO_20K",
        badge = "SIÊU ƯU ĐÃI THÀNH VIÊN",
        icon = "🎁",
        title = "Giảm 20.000₫ đơn từ 50K",
        description = "Mã GELATO20 áp dụng ngay cho tất cả khách gọi món tại bàn hôm nay!",
        actionText = "Lấy mã -20K",
        actionType = "VOUCHER"
    ),
    PromoDeal(
        id = "PROMO_BOGO",
        badge = "MUA 2 TẶNG 1",
        icon = "🍨",
        title = "Mua 2 Viên Gelato Tặng 1 Topping",
        description = "Thêm từ 2 viên kem Ý béo ngậy, tặng ngay 1 phần trân châu hoàng kim tươi mát!",
        actionText = "Xem vị kem",
        actionType = "CATEGORY_KEM"
    ),
    PromoDeal(
        id = "PROMO_HAPPY_HOUR",
        badge = "GIỜ VÀNG 14H - 17H",
        icon = "⚡",
        title = "Giảm 15% Toàn Bộ Đồ Uống",
        description = "Trà hoa quả nhiệt đới, soda sảng khoái và bingsu tuyết mịn giải nhiệt cực đã.",
        actionText = "Xem đồ uống",
        actionType = "CATEGORY_DRINK"
    )
)

/**
 * Modern Special Promotion Banner displayed prominently on app entry.
 * Features auto-cycling deals, pulsing live indicator, actionable buttons,
 * and a dismiss/reopen capability.
 */
@Composable
fun SpecialPromoBanner(
    isVoucherApplied: Boolean,
    onApplySpecialVoucher: () -> Unit,
    onSelectCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDismissed by remember { mutableStateOf(false) }
    var currentPromoIndex by remember { mutableIntStateOf(0) }

    // Auto cycle through deals every 4.5 seconds
    LaunchedEffect(isDismissed) {
        if (!isDismissed) {
            while (true) {
                delay(4500)
                currentPromoIndex = (currentPromoIndex + 1) % SamplePromoDeals.size
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "badgePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    AnimatedVisibility(
        visible = !isDismissed,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("special_promo_banner"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.2.dp, VibrantBorder),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFE8FAF6), // Soft ice mint
                                Color(0xFFE0F7FA), // Cool aqua breeze
                                Color(0xFFF0FDF8)  // Light crisp finish
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column {
                    // Header Bar: Pulsing Live Deal Tag + Close Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Pulsing flame indicator
                            Box(
                                modifier = Modifier
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(FreshBerry.copy(alpha = 0.15f))
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = FreshBerry,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "KHUYẾN MÃI ĐẶC BIỆT ĐANG DIỄN RA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = VibrantPinkDark,
                                letterSpacing = 0.6.sp
                            )
                        }

                        IconButton(
                            onClick = { isDismissed = true },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Đóng banner",
                                tint = VibrantTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Animated Deal Content
                    AnimatedContent(
                        targetState = currentPromoIndex,
                        transitionSpec = {
                            (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                                (slideOutHorizontally { width -> -width } + fadeOut())
                        },
                        label = "PromoDealTransition"
                    ) { index ->
                        val deal = SamplePromoDeals[index]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Emoji / Icon Box
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, VibrantBorder),
                                shadowElevation = 1.dp,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = deal.icon,
                                        fontSize = 24.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Middle: Title & description
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = VibrantPinkLight,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                ) {
                                    Text(
                                        text = deal.badge,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VibrantPinkDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = deal.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantTextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = deal.description,
                                    fontSize = 11.sp,
                                    color = VibrantTextSecondary,
                                    lineHeight = 14.sp,
                                    maxLines = 2
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Right: Action button
                            if (deal.actionType == "VOUCHER") {
                                Button(
                                    onClick = { onApplySpecialVoucher() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isVoucherApplied) VibrantGreenDark else VibrantPink
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("promo_apply_voucher_btn")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (isVoucherApplied) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("Đã nhận", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        } else {
                                            Icon(
                                                Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(deal.actionText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else {
                                Button(
                                    onClick = {
                                        if (deal.actionType == "CATEGORY_KEM") {
                                            onSelectCategory("Kem viên")
                                        } else {
                                            onSelectCategory("Đồ uống")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(deal.actionText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dots indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SamplePromoDeals.forEachIndexed { i, _ ->
                            val isCurrent = i == currentPromoIndex
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .height(4.dp)
                                    .width(if (isCurrent) 18.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (isCurrent) VibrantPink else VibrantBorder)
                                    .clickable { currentPromoIndex = i }
                            )
                        }
                    }
                }
            }
        }
    }

    // Reopen pill when dismissed
    if (isDismissed) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = VibrantPinkLight,
                border = BorderStroke(1.dp, VibrantBorder),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { isDismissed = false }
                    .testTag("reopen_promo_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = FreshBerry,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Xem ưu đãi đang diễn ra 🔥",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantPinkDark
                    )
                }
            }
        }
    }
}
