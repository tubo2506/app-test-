package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Stars
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.IceCreamBorder
import com.example.ui.theme.IceCreamPink
import com.example.ui.theme.IceCreamPinkLight
import com.example.ui.theme.IceCreamTextPrimary
import com.example.ui.theme.IceCreamTextSecondary

@Composable
fun LoyaltyPhoneDialog(
    gameBonusPoints: Int,
    orderPointsEstimate: Int,
    initialPhone: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var phoneInput by remember { mutableStateOf(initialPhone) }
    var isError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("loyalty_phone_dialog"),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, IceCreamBorder),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = IceCreamPinkLight,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = IceCreamPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tích Điểm Thành Viên",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = IceCreamTextPrimary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = IceCreamTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Points highlight box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = IceCreamPinkLight
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎁 Bạn nhận được:",
                            fontSize = 13.sp,
                            color = IceCreamTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+${gameBonusPoints + orderPointsEstimate} Điểm Thưởng",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = IceCreamPink
                        )
                        Text(
                            text = "(+$orderPointsEstimate điểm hóa đơn + $gameBonusPoints điểm minigame)",
                            fontSize = 11.sp,
                            color = IceCreamTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nhập số điện thoại để lưu điểm và nhận các voucher kem miễn phí cho lần ghé tiếp theo:",
                    fontSize = 13.sp,
                    color = IceCreamTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = {
                        if (it.length <= 11) {
                            phoneInput = it.filter { char -> char.isDigit() }
                            isError = false
                        }
                    },
                    label = { Text("Số điện thoại của bạn") },
                    placeholder = { Text("0987654321") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = IceCreamPink)
                    },
                    isError = isError,
                    supportingText = if (isError) {
                        { Text("Vui lòng nhập số điện thoại hợp lệ (9-11 số)", color = Color.Red) }
                    } else null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("phone_input_field"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IceCreamPink,
                        unfocusedBorderColor = IceCreamBorder
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (phoneInput.length >= 9) {
                            onConfirm(phoneInput)
                        } else {
                            isError = true
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IceCreamPink),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_loyalty_phone_button")
                ) {
                    Text(
                        text = "Lưu Điểm & Nhận Quà Ngay",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
