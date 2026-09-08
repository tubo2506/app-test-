package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CustomerEntity
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ShiftRecord
import com.example.data.model.StaffMember
import com.example.ui.CustomerFeedback
import com.example.ui.Voucher
import com.example.ui.screens.manager.ManagerAnalyticsDeepTab
import com.example.ui.screens.manager.ManagerCrmVouchersTab
import com.example.ui.screens.manager.ManagerMenuStockTab
import com.example.ui.screens.manager.ManagerSettingsSecurityTab
import com.example.ui.screens.manager.ManagerShiftsStaffTab
import com.example.ui.screens.manager.ManagerTablesQrTab
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkDark
import com.example.ui.theme.VibrantPinkLight
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary

@Composable
fun ManagerScreen(
    menuItems: List<MenuItemEntity>,
    orders: List<OrderEntity>,
    orderPayments: Map<String, String>,
    customers: List<CustomerEntity>,
    vouchers: List<Voucher>,
    feedbacks: List<CustomerFeedback>,
    stockMap: Map<String, Int> = emptyMap(),
    costMap: Map<String, Long> = emptyMap(),
    staffList: List<StaffMember> = emptyList(),
    shiftRecords: List<ShiftRecord> = emptyList(),
    pinProtectionEnabled: Boolean = true,
    managerPin: String = "8888",
    staffPin: String = "1234",
    bankName: String = "MB Bank (Ngân hàng Quân Đội)",
    bankAccount: String = "0987654321",
    bankOwner: String = "TIEM KEM GELATO GEN Z",
    soundAlertsEnabled: Boolean = true,
    deviceRole: String = "CUSTOMER_TABLE",
    selectedTable: String = "Bàn 03",
    onUpdateSecurityPins: (String, String, Boolean) -> Boolean = { _, _, _ -> true },
    onUpdateBankDetails: (String, String, String) -> Unit = { _, _, _ -> },
    onExportBackup: () -> String = { "" },
    onResetDemoData: () -> Unit = {},
    onUpdateDeviceRole: (String, String?) -> Unit = { _, _ -> },
    onToggleAvailability: (String, Boolean) -> Unit,
    onAddNewMenuItem: (name: String, cat: String, price: Long, desc: String, emoji: String, colorHex: Long, tag: String) -> Unit,
    onUpdateMenuItem: (MenuItemEntity) -> Unit,
    onDeleteMenuItem: (MenuItemEntity) -> Unit,
    onAddNewVoucher: (title: String, desc: String, pointsCost: Int, discount: Long) -> Unit,
    onDeleteVoucher: (String) -> Unit,
    onQuickAddStock: (String, Int) -> Unit = { _, _ -> },
    onUpdateStock: (String, Int) -> Unit = { _, _ -> },
    onUpdateCost: (String, Long) -> Unit = { _, _ -> },
    onToggleStaffStatus: (String) -> Unit = {},
    onAddNewStaff: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onDeleteStaff: (String) -> Unit = {},
    onCloseShiftAudit: (String, String, Long, Long, String) -> Unit = { _, _, _, _, _ -> },
    onAdjustPoints: (String, Int, String) -> Unit = { _, _, _ -> },
    onToggleSoundAlerts: (Boolean) -> Unit = {},
    onGenerateFinancialReport: () -> String = { "" },
    onSelectTableForCustomerMode: (String) -> Unit = {},
    onClearTable: (String) -> Unit = {},
    onExitToCustomer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 6 Specialized Tabs: "MENU", "ANALYTICS", "TABLES", "STAFF", "CRM", "SECURITY"
    var selectedManagerTab by remember { mutableStateOf("MENU") }
    var showAddItemDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<MenuItemEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFC))
            .padding(horizontal = 14.dp)
            .testTag("manager_screen")
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Manager Top Role Banner & Quick Exit
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFCE4EC))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("👑", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "PHÂN HỆ QUẢN TRỊ GELATO TOÀN DIỆN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = VibrantPinkDark
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFF8BBD0)),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onExitToCustomer() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Thoát về khách hàng",
                        tint = VibrantPinkDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Khóa / Thoát",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantPinkDark
                    )
                }
            }
        }

        // Manager Horizontal Navigation Bar (6 Tabs)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ManagerNavigationChip(
                label = "Thực đơn & Kho",
                icon = Icons.Default.RestaurantMenu,
                selected = selectedManagerTab == "MENU",
                onClick = { selectedManagerTab = "MENU" }
            )
            ManagerNavigationChip(
                label = "Báo cáo & BI",
                icon = Icons.Default.Assessment,
                selected = selectedManagerTab == "ANALYTICS",
                onClick = { selectedManagerTab = "ANALYTICS" }
            )
            ManagerNavigationChip(
                label = "Bàn & QR",
                icon = Icons.Default.TableBar,
                selected = selectedManagerTab == "TABLES",
                onClick = { selectedManagerTab = "TABLES" }
            )
            ManagerNavigationChip(
                label = "Ca & Nhân sự",
                icon = Icons.Default.Badge,
                selected = selectedManagerTab == "STAFF",
                onClick = { selectedManagerTab = "STAFF" }
            )
            ManagerNavigationChip(
                label = "CRM & Voucher",
                icon = Icons.Default.People,
                selected = selectedManagerTab == "CRM",
                onClick = { selectedManagerTab = "CRM" }
            )
            ManagerNavigationChip(
                label = "Cài đặt & Mã PIN",
                icon = Icons.Default.Security,
                selected = selectedManagerTab == "SECURITY",
                onClick = { selectedManagerTab = "SECURITY" }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Content Switching
        when (selectedManagerTab) {
            "MENU" -> {
                ManagerMenuStockTab(
                    menuItems = menuItems,
                    stockMap = stockMap,
                    costMap = costMap,
                    onToggleAvailability = onToggleAvailability,
                    onQuickAddStock = onQuickAddStock,
                    onUpdateStock = onUpdateStock,
                    onUpdateCost = onUpdateCost,
                    onOpenAddNew = { showAddItemDialog = true },
                    onEditItem = { editingItem = it },
                    onDeleteItem = onDeleteMenuItem
                )
            }
            "ANALYTICS" -> {
                ManagerAnalyticsDeepTab(
                    orders = orders,
                    menuItems = menuItems,
                    orderPayments = orderPayments,
                    onGenerateFinancialReport = onGenerateFinancialReport
                )
            }
            "TABLES" -> {
                ManagerTablesQrTab(
                    orders = orders,
                    onSelectTableForCustomerMode = onSelectTableForCustomerMode,
                    onClearTable = onClearTable
                )
            }
            "STAFF" -> {
                ManagerShiftsStaffTab(
                    orders = orders,
                    orderPayments = orderPayments,
                    staffList = staffList,
                    shiftRecords = shiftRecords,
                    onToggleStaffStatus = onToggleStaffStatus,
                    onAddNewStaff = onAddNewStaff,
                    onDeleteStaff = onDeleteStaff,
                    onCloseShiftAudit = onCloseShiftAudit
                )
            }
            "CRM" -> {
                ManagerCrmVouchersTab(
                    customers = customers,
                    vouchers = vouchers,
                    feedbacks = feedbacks,
                    onAdjustPoints = onAdjustPoints,
                    onAddVoucher = {
                        onAddNewVoucher(it.title, it.description, it.pointsCost, it.discountAmount)
                    },
                    onDeleteVoucher = { onDeleteVoucher(it.id) }
                )
            }
            "SECURITY" -> {
                ManagerSettingsSecurityTab(
                    managerPin = managerPin,
                    staffPin = staffPin,
                    pinProtectionEnabled = pinProtectionEnabled,
                    bankName = bankName,
                    bankAccount = bankAccount,
                    bankOwner = bankOwner,
                    soundAlertsEnabled = soundAlertsEnabled,
                    deviceRole = deviceRole,
                    selectedTable = selectedTable,
                    onUpdateSecurityPins = onUpdateSecurityPins,
                    onUpdateBankDetails = onUpdateBankDetails,
                    onToggleSoundAlerts = onToggleSoundAlerts,
                    onExportDataSummary = onExportBackup,
                    onResetDemoData = onResetDemoData,
                    onUpdateDeviceRole = onUpdateDeviceRole
                )
            }
        }
    }

    // Add new Item Dialog
    if (showAddItemDialog) {
        AddOrEditMenuItemDialog(
            initialItem = null,
            onDismiss = { showAddItemDialog = false },
            onConfirm = { name, cat, price, desc, emoji, tag ->
                onAddNewMenuItem(name, cat, price, desc, emoji, 0xFFFF5388, tag)
                showAddItemDialog = false
            }
        )
    }

    // Edit Item Dialog
    editingItem?.let { item ->
        AddOrEditMenuItemDialog(
            initialItem = item,
            onDismiss = { editingItem = null },
            onConfirm = { name, cat, price, desc, emoji, tag ->
                onUpdateMenuItem(
                    item.copy(
                        name = name,
                        category = cat,
                        price = price,
                        description = desc,
                        emoji = emoji,
                        tag = tag
                    )
                )
                editingItem = null
            }
        )
    }
}

@Composable
fun ManagerNavigationChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (selected) VibrantPinkLight else Color.Transparent,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) VibrantPink else VibrantTextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) VibrantPinkDark else VibrantTextSecondary
            )
        }
    }
}

@Composable
fun AddOrEditMenuItemDialog(
    initialItem: MenuItemEntity?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, cat: String, price: Long, desc: String, emoji: String, tag: String) -> Unit
) {
    var name by remember { mutableStateOf(initialItem?.name ?: "") }
    var category by remember { mutableStateOf(initialItem?.category ?: "Kem viên") }
    var priceStr by remember { mutableStateOf(initialItem?.price?.toString() ?: "35000") }
    var desc by remember { mutableStateOf(initialItem?.description ?: "") }
    var emoji by remember { mutableStateOf(initialItem?.emoji ?: "🍨") }
    var tag by remember { mutableStateOf(initialItem?.tag ?: "") }

    val categories = listOf("Kem viên", "Topping", "Đồ uống")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, VibrantBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = if (initialItem == null) "THÊM MÓN MỚI" else "CHỈNH SỬA MÓN",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = VibrantTextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = emoji,
                        onValueChange = { emoji = it },
                        label = { Text("Icon/Emoji", fontSize = 10.sp) },
                        modifier = Modifier.width(80.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Tên món kem", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Giá bán (VNĐ)", fontSize = 10.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = tag,
                        onValueChange = { tag = it },
                        label = { Text("Nhãn (Tag)", fontSize = 10.sp) },
                        placeholder = { Text("Bestseller", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Danh mục món:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VibrantTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEach { c ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (category == c) VibrantPink else Color(0xFFF0F0F2),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { category = c }
                        ) {
                            Text(
                                text = c,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (category == c) Color.White else VibrantTextPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Mô tả hương vị / Thành phần", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                        Text("Hủy", fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            val price = priceStr.toLongOrNull() ?: 35000L
                            if (name.isNotBlank()) {
                                onConfirm(name, category, price, desc, emoji, tag)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text(if (initialItem == null) "Thêm ngay" else "Lưu lại", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
