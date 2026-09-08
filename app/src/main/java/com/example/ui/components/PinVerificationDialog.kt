package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.VibrantBg
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantGreenBg
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkDark
import com.example.ui.theme.VibrantPinkLight
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary

@Composable
fun PinVerificationDialog(
    targetRole: String, // "STAFF" or "MANAGER"
    onDismiss: () -> Unit,
    onVerifyPin: (String) -> Boolean,
    onSuccess: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val roleTitle = if (targetRole == "MANAGER") "Quản Lý (Manager)" else "Nhân Viên (Staff & Bếp)"
    val roleColor = if (targetRole == "MANAGER") VibrantPinkDark else VibrantPink
    val defaultHint = if (targetRole == "MANAGER") "8888" else "1234"

    fun handleDigit(digit: String) {
        if (pin.length < 6) {
            val newPin = pin + digit
            pin = newPin
            errorMessage = null
            if (newPin.length >= 4) {
                // Auto verify if length is 4 or more
                if (onVerifyPin(newPin)) {
                    onSuccess()
                } else if (newPin.length == 4) {
                    errorMessage = "Mã PIN không đúng! (Mặc định: $defaultHint)"
                }
            }
        }
    }

    fun handleBackspace() {
        if (pin.isNotEmpty()) {
            pin = pin.dropLast(1)
            errorMessage = null
        }
    }

    fun handleClear() {
        pin = ""
        errorMessage = null
    }

    fun handleConfirm() {
        if (pin.isEmpty()) {
            errorMessage = "Vui lòng nhập mã PIN"
            return
        }
        if (onVerifyPin(pin)) {
            onSuccess()
        } else {
            errorMessage = "Mã PIN không đúng! (Mặc định: $defaultHint)"
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp)
                .testTag("pin_verification_dialog"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = VibrantPinkLight,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = roleColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("pin_cancel_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = VibrantTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Bảo Mật Phân Quyền",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = VibrantTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Nhập mã PIN để truy cập $roleTitle",
                    fontSize = 13.sp,
                    color = VibrantTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // PIN Dots Display
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val maxDots = 4
                    for (i in 0 until maxDots) {
                        val isFilled = i < pin.length
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) roleColor else VibrantBorder)
                                .border(
                                    width = 1.dp,
                                    color = if (isFilled) roleColor else VibrantBorder,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                // Error message
                AnimatedVisibility(
                    visible = errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFD32F2F),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Friendly Hint Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = VibrantBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = VibrantPinkDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mã PIN mặc định: $defaultHint (Có thể đổi trong Quản lý)",
                            fontSize = 11.sp,
                            color = VibrantTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Touch-Friendly Numeric Keypad (48dp+ buttons)
                val keypadRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("C", "0", "DEL")
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (row in keypadRows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (key in row) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = when (key) {
                                        "C" -> Color(0xFFFFEBEE)
                                        "DEL" -> Color(0xFFF0F4F8)
                                        else -> Color(0xFFF9FBFA)
                                    },
                                    border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            when (key) {
                                                "C" -> handleClear()
                                                "DEL" -> handleBackspace()
                                                else -> handleDigit(key)
                                            }
                                        }
                                        .testTag("pin_key_$key")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        when (key) {
                                            "C" -> Text(
                                                text = "C",
                                                fontSize = 17.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFFD32F2F)
                                            )
                                            "DEL" -> Icon(
                                                imageVector = Icons.Default.Backspace,
                                                contentDescription = "Xóa",
                                                tint = VibrantTextSecondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            else -> Text(
                                                text = key,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = VibrantTextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(text = "Hủy bỏ", color = VibrantTextSecondary)
                    }

                    Button(
                        onClick = { handleConfirm() },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = roleColor),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("pin_confirm_button")
                    ) {
                        Text(
                            text = "Xác Nhận",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
