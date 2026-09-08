package com.example.ui.screens.manager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantGreenDark
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkDark
import com.example.ui.theme.VibrantPinkLight
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary

@Composable
fun ManagerSettingsSecurityTab(
    managerPin: String,
    staffPin: String,
    pinProtectionEnabled: Boolean,
    bankName: String,
    bankAccount: String,
    bankOwner: String,
    soundAlertsEnabled: Boolean,
    deviceRole: String = "CUSTOMER_TABLE",
    selectedTable: String = "Bàn 03",
    onUpdateSecurityPins: (String, String, Boolean) -> Boolean,
    onUpdateBankDetails: (String, String, String) -> Unit,
    onToggleSoundAlerts: (Boolean) -> Unit,
    onExportDataSummary: () -> String,
    onResetDemoData: () -> Unit,
    onUpdateDeviceRole: (String, String?) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showBackupDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    // State for PIN edit
    var currentMgrPin by remember(managerPin) { mutableStateOf(managerPin) }
    var currentStfPin by remember(staffPin) { mutableStateOf(staffPin) }
    var pinEnabled by remember(pinProtectionEnabled) { mutableStateOf(pinProtectionEnabled) }

    // State for Bank edit
    var currentBankName by remember(bankName) { mutableStateOf(bankName) }
    var currentAccount by remember(bankAccount) { mutableStateOf(bankAccount) }
    var currentOwner by remember(bankOwner) { mutableStateOf(bankOwner) }

    var localRole by remember(deviceRole) { mutableStateOf(deviceRole) }
    var localTable by remember(selectedTable) { mutableStateOf(selectedTable) }

    val allTables = (1..16).map { String.format("Bàn %02d", it) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("manager_settings_security_tab")
    ) {
        // Section 0: Dedicated Device Mode Configuration (Cách 1)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, VibrantPink.copy(alpha = 0.5f)),
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
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.TabletAndroid,
                                        contentDescription = null,
                                        tint = VibrantPink,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "VAI TRÒ THIẾT BỊ NÀY (CÁCH 1)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = VibrantTextPrimary
                                )
                                Text(
                                    "Mô hình thiết bị chuyên biệt cho từng trạm",
                                    fontSize = 11.sp,
                                    color = VibrantTextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = VibrantPinkLight
                        ) {
                            Text(
                                text = "Mô hình F&B",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantPinkDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        "Chọn vai trò cố định cho thiết bị này trong quán:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // 4 Role Options
                    val roleOptions = listOf(
                        Triple("CUSTOMER_TABLE", "📱 Máy Bàn Khách", "Khách chỉ gọi món, giỏ hàng, thanh toán. ẨN HOÀN TOÀN thanh chọn vai trò!"),
                        Triple("STAFF_POS", "🛎️ Trạm Thu Ngân / Bếp", "Cố định giao diện nhân viên POS nhận đơn, làm món & thanh toán."),
                        Triple("MANAGER_PORTAL", "👑 Máy Chủ Quán / Quản Lý", "Toàn quyền quản trị kho, doanh số BI, nhân sự và cài đặt."),
                        Triple("DEMO_MULTI_ROLE", "🧪 Chế Độ Thử Nghiệm", "Hiện thanh chuyển đổi nhanh giữa các vai trò để trải nghiệm.")
                    )

                    roleOptions.forEach { (roleKey, title, desc) ->
                        val isSelected = localRole == roleKey
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) VibrantPinkLight.copy(alpha = 0.6f) else Color(0xFFF8F9FA),
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) VibrantPink else VibrantBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    localRole = roleKey
                                    onUpdateDeviceRole(roleKey, if (roleKey == "CUSTOMER_TABLE") localTable else null)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) VibrantPink else Color(0xFFE0E0E0),
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    if (isSelected) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Surface(
                                                shape = CircleShape,
                                                color = Color.White,
                                                modifier = Modifier.size(8.dp)
                                            ) {}
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) VibrantPinkDark else VibrantTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = desc,
                                        fontSize = 10.sp,
                                        color = VibrantTextSecondary,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    // If CUSTOMER_TABLE is selected, show Table Picker
                    if (localRole == "CUSTOMER_TABLE") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Gán số bàn cố định cho máy này:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantPinkDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            allTables.forEach { tbl ->
                                val isSelectedTbl = localTable == tbl
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelectedTbl) VibrantPink else Color(0xFFF1F2F4),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelectedTbl) VibrantPink else VibrantBorder
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            localTable = tbl
                                            onUpdateDeviceRole("CUSTOMER_TABLE", tbl)
                                        }
                                ) {
                                    Text(
                                        text = tbl,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelectedTbl) FontWeight.Black else FontWeight.Medium,
                                        color = if (isSelectedTbl) Color.White else VibrantTextPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        // Section 1: Security & PIN Management
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
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
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = VibrantPink, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("BẢO MẬT & PHÂN QUYỀN RBAC", fontSize = 13.sp, fontWeight = FontWeight.Black, color = VibrantTextPrimary)
                                Text("Mã PIN truy cập Quản Lý & Nhân Viên", fontSize = 11.sp, color = VibrantTextSecondary)
                            }
                        }

                        Switch(
                            checked = pinEnabled,
                            onCheckedChange = {
                                pinEnabled = it
                                onUpdateSecurityPins(currentMgrPin, currentStfPin, it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = VibrantPink
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = currentMgrPin,
                            onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) currentMgrPin = it },
                            label = { Text("Mã PIN Quản Lý (6 số)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = currentStfPin,
                            onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) currentStfPin = it },
                            label = { Text("Mã PIN Nhân Viên (4 số)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            onUpdateSecurityPins(currentMgrPin, currentStfPin, pinEnabled)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Lưu Cấu Hình Mã PIN", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 2: Bank & VietQR Napas 247 Settings
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE3F2FD),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("TÀI KHOẢN NHẬN TIỀN VIETQR", fontSize = 13.sp, fontWeight = FontWeight.Black, color = VibrantTextPrimary)
                            Text("Mã QR động Napas 247 hiển thị cho khách", fontSize = 11.sp, color = VibrantTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = currentBankName,
                        onValueChange = { currentBankName = it },
                        label = { Text("Tên Ngân Hàng Thụ Hưởng") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = currentAccount,
                        onValueChange = { if (it.all { c -> c.isDigit() }) currentAccount = it },
                        label = { Text("Số Tài Khoản Ngân Hàng") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = currentOwner,
                        onValueChange = { currentOwner = it.uppercase() },
                        label = { Text("Tên Chủ Tài Khoản (VIẾT HOA KHÔNG DẤU)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            onUpdateBankDetails(currentBankName, currentAccount, currentOwner)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cập Nhật Tài Khoản VietQR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 3: Sound Alerts & Maintenance
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Sound Alerts Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = VibrantPink, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Âm Báo Hệ Thống", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                                Text("Chuông báo đơn mới, gọi phục vụ & thanh toán", fontSize = 11.sp, color = VibrantTextSecondary)
                            }
                        }

                        Switch(
                            checked = soundAlertsEnabled,
                            onCheckedChange = onToggleSoundAlerts,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = VibrantPink
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF3F4F6))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Backup & Reset Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showBackupDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sao Lưu JSON", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showResetConfirmDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                            border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tải Lại Mẫu Món", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Dialog: Full JSON Backup View & Copy
    if (showBackupDialog) {
        val backupJson = remember { onExportDataSummary() }
        var copiedToast by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showBackupDialog = false }) {
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
                        Text("💾 Sao Lưu Dữ Liệu Quản Trị", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                        IconButton(onClick = { showBackupDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF263238),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {
                        LazyColumn(modifier = Modifier.padding(12.dp)) {
                            item {
                                Text(
                                    text = backupJson,
                                    fontSize = 11.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    color = Color(0xFF81C784),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showBackupDialog = false },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Đóng", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Gelato Backup JSON", backupJson)
                                clipboard.setPrimaryClip(clip)
                                copiedToast = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (copiedToast) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (copiedToast) "Đã Chép!" else "Sao Chép JSON", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Dialog: Reset Demo Data Confirmation
    if (showResetConfirmDialog) {
        Dialog(onDismissRequest = { showResetConfirmDialog = false }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, VibrantBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("⚠️ Tải Lại Thực Đơn Chuẩn?", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Thao tác này sẽ đặt lại danh mục món mẫu về trạng thái ban đầu của quán kem Gelato. Dữ liệu đơn hàng và khách hàng vẫn được bảo toàn.",
                        fontSize = 12.sp,
                        color = VibrantTextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showResetConfirmDialog = false },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Hủy", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                onResetDemoData()
                                showResetConfirmDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Tải Lại Món", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
