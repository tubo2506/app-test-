package com.example.ui.screens.manager

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.OrderEntity
import com.example.data.model.ShiftRecord
import com.example.data.model.StaffMember
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantGreenDark
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkDark
import com.example.ui.theme.VibrantPinkLight
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ManagerShiftsStaffTab(
    orders: List<OrderEntity>,
    orderPayments: Map<String, String>,
    staffList: List<StaffMember>,
    shiftRecords: List<ShiftRecord>,
    onToggleStaffStatus: (String) -> Unit,
    onAddNewStaff: (String, String, String, String) -> Unit,
    onDeleteStaff: (String) -> Unit,
    onCloseShiftAudit: (String, String, Long, Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCloseShiftDialog by remember { mutableStateOf(false) }
    var showAddStaffDialog by remember { mutableStateOf(false) }

    // Active Shift live calculation
    val cashSales = orders.filter { orderPayments[it.orderId] == "CASH" }.sumOf { it.totalAmount }
    val qrSales = orders.filter { orderPayments[it.orderId] == "VIETQR" }.sumOf { it.totalAmount }
    val totalShiftSales = cashSales + qrSales
    val openingCash = 1500000L // 1.500.000đ tiền mặt thối ban đầu
    val totalCashInDrawer = openingCash + cashSales

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("manager_shifts_staff_tab")
    ) {
        // Active Shift Card (Hero)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = VibrantPinkLight,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.PointOfSale, contentDescription = null, tint = VibrantPink, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("CA TRỰC HIỆN TẠI", fontSize = 13.sp, fontWeight = FontWeight.Black, color = VibrantTextPrimary)
                                Text("Thu ngân: ${staffList.firstOrNull { it.role.contains("Thu ngân") }?.name ?: "Nguyễn Thu Hà"}", fontSize = 11.sp, color = VibrantTextSecondary)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = "🟢 Đang mở ca",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantGreenDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Tiền mặt trong két (Dự kiến)", fontSize = 10.sp, color = VibrantTextMuted)
                            Text(formatVnd(totalCashInDrawer), fontSize = 18.sp, fontWeight = FontWeight.Black, color = VibrantGreenDark)
                            Text("Gồm ${formatVnd(openingCash)} đầu ca", fontSize = 10.sp, color = VibrantTextMuted)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Doanh thu ca hiện tại", fontSize = 10.sp, color = VibrantTextMuted)
                            Text(formatVnd(totalShiftSales), fontSize = 18.sp, fontWeight = FontWeight.Black, color = VibrantPink)
                            Text("${orders.size} đơn hàng đã bán", fontSize = 10.sp, color = VibrantTextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF3F4F6))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("💵 Tiền mặt: ${formatVnd(cashSales)}", fontSize = 11.sp, color = VibrantTextSecondary)
                            Text("🏦 VietQR: ${formatVnd(qrSales)}", fontSize = 11.sp, color = VibrantTextSecondary)
                        }

                        Button(
                            onClick = { showCloseShiftDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.LockClock, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Chốt Ca & Kiểm Két", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Staff Directory Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Badge, contentDescription = null, tint = VibrantPink, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Đội Ngũ Nhân Sự Quán (${staffList.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                }

                Button(
                    onClick = { showAddStaffDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Thêm Nhân Viên", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Staff List Cards
        items(staffList, key = { it.id }) { staff ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = VibrantPinkLight,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(staff.avatarEmoji, fontSize = 20.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(staff.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(staff.id, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VibrantTextMuted)
                            }
                            Text(
                                text = "${staff.role} • SĐT: ${staff.phone}",
                                fontSize = 11.sp,
                                color = VibrantTextSecondary
                            )
                            Text(
                                text = "Đã phục vụ: ${staff.ordersHandled} đơn • Vào làm: ${staff.joinedDate}",
                                fontSize = 10.sp,
                                color = VibrantTextMuted
                            )
                        }
                    }

                    // Shift status pill & actions
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when (staff.shiftStatus) {
                                "ACTIVE" -> Color(0xFFE8F5E9)
                                "BREAK" -> Color(0xFFFFF3E0)
                                else -> Color(0xFFF3F4F6)
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onToggleStaffStatus(staff.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = when (staff.shiftStatus) {
                                        "ACTIVE" -> "🟢 Đang làm"
                                        "BREAK" -> "🟡 Giải lao"
                                        else -> "⚪ Nghỉ ca"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (staff.shiftStatus) {
                                        "ACTIVE" -> VibrantGreenDark
                                        "BREAK" -> Color(0xFFE65100)
                                        else -> Color(0xFF757575)
                                    }
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(Icons.Default.SwapHoriz, contentDescription = "Đổi ca", modifier = Modifier.size(14.dp), tint = VibrantTextMuted)
                            }
                        }

                        IconButton(onClick = { onDeleteStaff(staff.id) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Xóa nhân viên", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Shift History Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.History, contentDescription = null, tint = VibrantPink, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Lịch Sử Các Ca Gần Nhất (${shiftRecords.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
            }
        }

        // Shift Records List
        items(shiftRecords, key = { it.shiftId }) { record ->
            val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(record.shiftName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                            Text("Thu ngân: ${record.staffName} • ${dateFormat.format(Date(record.endTime))}", fontSize = 10.sp, color = VibrantTextMuted)
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (record.isBalanced) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        ) {
                            Text(
                                text = if (record.isBalanced) "Khớp sổ 100%" else "Có chênh lệch",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (record.isBalanced) VibrantGreenDark else Color(0xFFD32F2F),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tổng thu: ${formatVnd(record.totalRevenue)} (${record.orderCount} đơn)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VibrantPink)
                        Text("Tiền mặt: ${formatVnd(record.cashRevenue)} | VietQR: ${formatVnd(record.qrRevenue)}", fontSize = 11.sp, color = VibrantTextSecondary)
                    }

                    if (record.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Ghi chú: ${record.notes}", fontSize = 10.sp, color = VibrantTextMuted)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Dialog: Chốt Ca & Kiểm Kê Két Tiền
    if (showCloseShiftDialog) {
        var countedCashStr by remember { mutableStateOf(totalCashInDrawer.toString()) }
        var notesStr by remember { mutableStateOf("Kiểm kê két đúng thực tế") }
        val selectedStaffName = staffList.firstOrNull { it.role.contains("Thu ngân") }?.name ?: "Nguyễn Thu Hà"

        Dialog(onDismissRequest = { showCloseShiftDialog = false }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔒 Chốt Ca & Kiểm Kê Két", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                        IconButton(onClick = { showCloseShiftDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Thu ngân chốt ca: $selectedStaffName", fontSize = 12.sp, color = VibrantTextSecondary)
                    Text("Tiền mặt trong két dự kiến: ${formatVnd(totalCashInDrawer)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VibrantGreenDark)

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = countedCashStr,
                        onValueChange = { if (it.all { c -> c.isDigit() }) countedCashStr = it },
                        label = { Text("Tiền mặt thực tế đếm được trong két (VNĐ)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val countedCash = countedCashStr.toLongOrNull() ?: totalCashInDrawer
                    val diff = countedCash - totalCashInDrawer
                    if (diff != 0L) {
                        Text(
                            text = "Chênh lệch tiền mặt: ${if (diff > 0) "+ " else ""}${formatVnd(diff)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (diff > 0) Color(0xFF1976D2) else Color(0xFFD32F2F)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    OutlinedTextField(
                        value = notesStr,
                        onValueChange = { notesStr = it },
                        label = { Text("Ghi chú chốt ca") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showCloseShiftDialog = false },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Hủy", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                onCloseShiftAudit(
                                    "Ca Hiện Tại",
                                    selectedStaffName,
                                    openingCash,
                                    countedCash,
                                    notesStr
                                )
                                showCloseShiftDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Xác Nhận Chốt Ca", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Dialog: Thêm Nhân Viên Mới
    if (showAddStaffDialog) {
        var nameStr by remember { mutableStateOf("") }
        var roleStr by remember { mutableStateOf("Nhân viên Phục vụ") }
        var phoneStr by remember { mutableStateOf("") }
        var emojiStr by remember { mutableStateOf("🧑‍🍳") }

        Dialog(onDismissRequest = { showAddStaffDialog = false }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✨ Thêm Nhân Viên Mới", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                        IconButton(onClick = { showAddStaffDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = nameStr,
                        onValueChange = { nameStr = it },
                        label = { Text("Họ và tên nhân viên") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = roleStr,
                        onValueChange = { roleStr = it },
                        label = { Text("Vị trí (Trưởng ca, Barista, Thu ngân, Phục vụ)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phoneStr,
                        onValueChange = { phoneStr = it },
                        label = { Text("Số điện thoại") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddStaffDialog = false },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Hủy", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (nameStr.isNotBlank()) {
                                    onAddNewStaff(nameStr.trim(), roleStr.trim(), phoneStr.trim(), emojiStr)
                                    showAddStaffDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Lưu Nhân Sự", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private fun formatVnd(amount: Long): String {
    return String.format(Locale.GERMANY, "%,d₫", amount)
}
