package com.example.ui.components

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FreshBerry
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkDark
import com.example.ui.theme.VibrantPinkLight
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary

data class QuickRequestOption(
    val id: String,
    val icon: String,
    val title: String,
    val subtitle: String
)

val QuickServiceOptions = listOf(
    QuickRequestOption("WATER", "🧊", "Xin thêm nước đá / nước lọc", "Phục vụ mang nước mát đến bàn"),
    QuickRequestOption("CUTLERY", "🥄", "Xin thêm muỗng nĩa / khăn giấy", "Muỗng nĩa kem phụ & khăn giấy lau"),
    QuickRequestOption("PAYMENT", "🧾", "Yêu cầu tính tiền / thanh toán", "Nhân viên mang hóa đơn đến bàn"),
    QuickRequestOption("STAFF", "🙋", "Gặp nhân viên tư vấn / hỗ trợ", "Nhân viên sẽ đến bàn ngay lập tức")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceCallSheet(
    selectedTable: String,
    onDismiss: () -> Unit,
    onSubmitRequest: (requestText: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedOptionId by remember { mutableStateOf("WATER") }
    var customNote by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .testTag("service_call_sheet")
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
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = VibrantPinkDark,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Gọi Phục Vụ Tại Bàn",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = VibrantTextPrimary
                        )
                        Text(
                            text = "Đang gửi từ $selectedTable",
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

            Spacer(modifier = Modifier.height(18.dp))

            if (!isSubmitted) {
                Text(
                    text = "Bạn cần nhân viên hỗ trợ điều gì?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = VibrantTextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Options list
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickServiceOptions.forEach { opt ->
                        val isSelected = opt.id == selectedOptionId
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) VibrantPinkLight else Color(0xFFFAFDFA),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) VibrantPink else VibrantBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedOptionId = opt.id }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(opt.icon, fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = opt.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) VibrantPinkDark else VibrantTextPrimary
                                    )
                                    Text(
                                        text = opt.subtitle,
                                        fontSize = 11.sp,
                                        color = VibrantTextSecondary
                                    )
                                }
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(VibrantPink),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Optional Custom Note
                OutlinedTextField(
                    value = customNote,
                    onValueChange = { customNote = it },
                    placeholder = { Text("Ghi chú thêm (ví dụ: xin thêm 2 cốc đá, khăn lạnh...)", fontSize = 12.sp) },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibrantPink,
                        unfocusedBorderColor = VibrantBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Submit Button
                Button(
                    onClick = {
                        val chosen = QuickServiceOptions.firstOrNull { it.id == selectedOptionId }
                        val fullText = buildString {
                            append(chosen?.title ?: "Yêu cầu hỗ trợ")
                            if (customNote.isNotBlank()) {
                                append(" - Ghi chú: ${customNote.trim()}")
                            }
                        }
                        onSubmitRequest(fullText)
                        isSubmitted = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_service_call_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.SupportAgent, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gửi Yêu Cầu Đến Nhân Viên",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Success Confirmation
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = VibrantPinkLight,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("✨", fontSize = 32.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Đã Báo Nhân Viên Thành Công!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = VibrantTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Nhân viên quầy sẽ nhanh chóng mang đồ hỗ trợ đến $selectedTable nhé ạ.",
                        fontSize = 13.sp,
                        color = VibrantTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Đã hiểu", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
