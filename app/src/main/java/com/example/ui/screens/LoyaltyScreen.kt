package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerEntity
import com.example.ui.Voucher
import com.example.ui.components.formatVnd
import com.example.ui.theme.IceCreamBorder
import com.example.ui.theme.IceCreamCoral
import com.example.ui.theme.IceCreamMint
import com.example.ui.theme.IceCreamPink
import com.example.ui.theme.IceCreamPinkLight
import com.example.ui.theme.IceCreamTextPrimary
import com.example.ui.theme.IceCreamTextSecondary

@Composable
fun LoyaltyScreen(
    customer: CustomerEntity?,
    phone: String,
    onLookupPhone: (String) -> Unit,
    availableVouchers: List<Voucher>,
    appliedVoucher: Voucher?,
    onApplyVoucher: (Voucher) -> Unit,
    onRemoveVoucher: () -> Unit,
    onOpenFeedback: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchPhone by remember { mutableStateOf(phone) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Phone search / Switch account
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, IceCreamBorder),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchPhone,
                        onValueChange = { searchPhone = it },
                        label = { Text("Số điện thoại thành viên") },
                        placeholder = { Text("0987654321") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("lookup_phone_field"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IceCreamPink,
                            unfocusedBorderColor = IceCreamBorder
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onLookupPhone(searchPhone.trim()) },
                        colors = ButtonDefaults.buttonColors(containerColor = IceCreamPink),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("lookup_phone_button")
                    ) {
                        Text("Tra cứu")
                    }
                }
            }
        }

        // Holographic / Gradient VIP Membership Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("membership_card"),
                shape = RoundedCornerShape(32.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    IceCreamPink,
                                    Color(0xFFFF8E72),
                                    Color(0xFF8E44AD)
                                )
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🍦", fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "KEM GEN Z VIP PASS",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = customer?.name ?: "Khách hàng thân thiết",
                                        color = Color.White,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = customer?.tier ?: "Thành viên Mới",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(26.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "ĐIỂM TÍCH LŨY HIỆN CÓ",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${customer?.points ?: 0} PTS",
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "SĐT: ${customer?.phone ?: phone}",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Kỷ lục game: ${customer?.minigameHighScore ?: 0}đ",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Loyalty Rules Information
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⭐ Cơ Chế Tích Điểm Quán Kem",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = IceCreamPink
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Cứ mỗi 10.000₫ thanh toán hóa đơn = 1 Điểm.\n• Thưởng thêm điểm khi chơi Minigame hứng kem sau khi đặt món.\n• Dùng điểm để đổi trực tiếp Topping và Giảm giá hóa đơn.",
                        fontSize = 13.sp,
                        color = IceCreamTextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Customer Feedback Banner for Loyalty Points
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBE7)),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFC0CA33)),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("loyalty_feedback_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("💬", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Góp Ý Dịch Vụ & Đánh Giá",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = IceCreamTextPrimary
                            )
                            Text(
                                text = "Đánh giá chất lượng phục vụ & nhận ngay +5 điểm thưởng!",
                                fontSize = 11.sp,
                                color = IceCreamTextSecondary
                            )
                        }
                    }

                    Button(
                        onClick = onOpenFeedback,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF827717)),
                        modifier = Modifier.testTag("open_feedback_from_loyalty")
                    ) {
                        Text("Đánh giá", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Redeemable Vouchers section
        item {
            Text(
                text = "🎁 Kho Đổi Thưởng Điểm Tích Lũy",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = IceCreamTextPrimary
            )
        }

        items(availableVouchers) { voucher ->
            val userPoints = customer?.points ?: 0
            val canAfford = userPoints >= voucher.pointsCost
            val isApplied = appliedVoucher?.id == voucher.id

            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isApplied) IceCreamPinkLight else Color.White
                ),
                border = if (isApplied) androidx.compose.foundation.BorderStroke(2.dp, IceCreamPink) else androidx.compose.foundation.BorderStroke(1.dp, IceCreamBorder),
                elevation = CardDefaults.cardElevation(if (isApplied) 4.dp else 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (canAfford) IceCreamPink else Color.LightGray,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.ConfirmationNumber,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = voucher.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = IceCreamTextPrimary
                        )
                        Text(
                            text = voucher.description,
                            fontSize = 12.sp,
                            color = IceCreamTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Yêu cầu: ${voucher.pointsCost} điểm (Giảm -${formatVnd(voucher.discountAmount)})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (canAfford) IceCreamMint else Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (isApplied) {
                        OutlinedButton(
                            onClick = onRemoveVoucher,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Bỏ chọn", fontSize = 12.sp)
                        }
                    } else {
                        Button(
                            onClick = { onApplyVoucher(voucher) },
                            enabled = canAfford,
                            colors = ButtonDefaults.buttonColors(containerColor = IceCreamPink),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (canAfford) "Áp Dụng" else "Chưa đủ điểm",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
