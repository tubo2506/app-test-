package com.example.ui.screens.manager

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import com.example.data.model.CustomerEntity
import com.example.ui.CustomerFeedback
import com.example.ui.Voucher
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
fun ManagerCrmVouchersTab(
    customers: List<CustomerEntity>,
    vouchers: List<Voucher>,
    feedbacks: List<CustomerFeedback>,
    onAdjustPoints: (String, Int, String) -> Unit,
    onAddVoucher: (Voucher) -> Unit,
    onDeleteVoucher: (Voucher) -> Unit,
    modifier: Modifier = Modifier
) {
    var crmSubTab by remember { mutableStateOf("CUSTOMERS") } // "CUSTOMERS", "VOUCHERS", "CSAT"
    var customerSearch by remember { mutableStateOf("") }
    var selectedTierFilter by remember { mutableStateOf("ALL") }
    var adjustingCustomer by remember { mutableStateOf<CustomerEntity?>(null) }
    var showAddVoucherDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("manager_crm_vouchers_tab")
    ) {
        // Sub-Tab Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "CUSTOMERS" to "Khách VIP & Điểm (${customers.size})",
                "VOUCHERS" to "Voucher (${vouchers.size})",
                "CSAT" to "Đánh Giá & CSAT (${feedbacks.size})"
            ).forEach { (tabKey, label) ->
                val isSelected = crmSubTab == tabKey
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) VibrantPink else Color(0xFFF3F4F6),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { crmSubTab = tabKey }
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else VibrantTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (crmSubTab) {
            "CUSTOMERS" -> {
                // Customer Tab
                val filteredCustomers = remember(customers, customerSearch, selectedTierFilter) {
                    customers.filter { c ->
                        val matchQuery = customerSearch.isBlank() ||
                                c.name.contains(customerSearch, ignoreCase = true) ||
                                c.phone.contains(customerSearch)
                        val matchTier = selectedTierFilter == "ALL" || c.tier.contains(selectedTierFilter)
                        matchQuery && matchTier
                    }
                }

                // Search & Filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customerSearch,
                        onValueChange = { customerSearch = it },
                        placeholder = { Text("Tìm theo SĐT hoặc Tên khách...", fontSize = 12.sp, color = VibrantTextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = VibrantPink, modifier = Modifier.size(18.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tier filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "ALL" to "Tất cả",
                        "Kim Cương" to "💎 VIP Kim Cương",
                        "Vàng" to "🥇 Kem Vàng",
                        "Bạc" to "🥈 Kem Bạc",
                        "Mới" to "🌱 Thành viên Mới"
                    ).forEach { (key, label) ->
                        val isSelected = selectedTierFilter == key
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) Color(0xFF263238) else Color.White,
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF263238) else VibrantBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedTierFilter = key }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else VibrantTextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredCustomers, key = { it.phone }) { cust ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, VibrantBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
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
                                                Icon(
                                                    imageVector = if (cust.tier.contains("Kim Cương")) Icons.Default.Diamond else Icons.Default.MilitaryTech,
                                                    contentDescription = null,
                                                    tint = if (cust.tier.contains("Kim Cương")) Color(0xFF9C27B0) else VibrantPink,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(cust.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                                            Text(cust.phone, fontSize = 11.sp, color = VibrantTextSecondary)
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (cust.tier.contains("Kim Cương")) Color(0xFFF3E5F5) else VibrantPinkLight
                                    ) {
                                        Text(
                                            text = cust.tier,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (cust.tier.contains("Kim Cương")) Color(0xFF7B1FA2) else VibrantPinkDark,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color(0xFFF3F4F6))
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                        Text("⭐ ${cust.points} điểm", fontSize = 12.sp, fontWeight = FontWeight.Black, color = VibrantPink)
                                        Text("Chi tiêu: ${formatVnd(cust.totalSpent)}", fontSize = 11.sp, color = VibrantTextSecondary)
                                        Text("${cust.orderCount} đơn", fontSize = 11.sp, color = VibrantTextMuted)
                                    }

                                    Button(
                                        onClick = { adjustingCustomer = cust },
                                        colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Điều Chỉnh Điểm", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }

            "VOUCHERS" -> {
                // Vouchers Tab
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Danh Sách Mã Khuyến Mãi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                    Button(
                        onClick = { showAddVoucherDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tạo Mã Mới", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(vouchers, key = { it.id }) { voucher ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, VibrantBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
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
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = VibrantPink, modifier = Modifier.size(20.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(voucher.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                                        Text(voucher.description, fontSize = 11.sp, color = VibrantTextSecondary)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text(
                                                text = "Giảm ${formatVnd(voucher.discountAmount)}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = VibrantPink
                                            )
                                            Text(
                                                text = if (voucher.pointsCost == 0) "Miễn phí" else "Cần ${voucher.pointsCost} điểm",
                                                fontSize = 11.sp,
                                                color = VibrantTextMuted
                                            )
                                        }
                                    }
                                }

                                IconButton(onClick = { onDeleteVoucher(voucher) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Xóa voucher", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }

            "CSAT" -> {
                // CSAT & Customer Reviews Tab
                val avgRating = if (feedbacks.isNotEmpty()) feedbacks.map { it.rating }.average() else 5.0

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Summary CSAT Score card
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, VibrantBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("ĐÁNH GIÁ CHẤT LƯỢNG (CSAT)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VibrantTextMuted)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = String.format(Locale.US, "%.1f", avgRating),
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Black,
                                            color = VibrantPink
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("⭐/5.0", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
                                    }
                                    Text("Dựa trên ${feedbacks.size} đánh giá của khách", fontSize = 10.sp, color = VibrantTextSecondary)
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFE8F5E9),
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(
                                        text = "98% Hài Lòng",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VibrantGreenDark,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text("Phản hồi gần đây từ bàn khách:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                    }

                    items(feedbacks, key = { it.id }) { fb ->
                        val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, VibrantBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${fb.tableNumber} • ${fb.phone}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VibrantTextPrimary
                                    )
                                    Row {
                                        repeat(fb.rating) {
                                            Text("⭐", fontSize = 12.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = dateFormat.format(Date(fb.timestamp)),
                                    fontSize = 10.sp,
                                    color = VibrantTextMuted
                                )

                                if (fb.tags.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        fb.tags.forEach { tag ->
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFFF3F4F6)
                                            ) {
                                                Text(
                                                    text = tag,
                                                    fontSize = 10.sp,
                                                    color = VibrantTextSecondary,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                if (fb.comment.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "\"${fb.comment}\"",
                                        fontSize = 11.sp,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        color = Color(0xFF455A64)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }

    // Dialog: Adjust Points for Customer
    adjustingCustomer?.let { cust ->
        var pointsDeltaStr by remember { mutableStateOf("50") }
        var isAdd by remember { mutableStateOf(true) }
        var reasonStr by remember { mutableStateOf("Tặng tri ân khách hàng thân thiết") }

        Dialog(onDismissRequest = { adjustingCustomer = null }) {
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
                        Text("⭐ Điều Chỉnh Điểm Thưởng", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                        IconButton(onClick = { adjustingCustomer = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Khách hàng: ${cust.name} (${cust.phone})", fontSize = 12.sp, color = VibrantTextSecondary)
                    Text("Điểm hiện tại: ${cust.points} điểm (${cust.tier})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VibrantPink)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Toggle Add / Deduct
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isAdd) Color(0xFFE8F5E9) else Color(0xFFF3F4F6),
                            border = BorderStroke(1.dp, if (isAdd) VibrantGreenDark else Color.Transparent),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { isAdd = true }
                        ) {
                            Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                Text("➕ Cộng Điểm", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isAdd) VibrantGreenDark else VibrantTextSecondary)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (!isAdd) Color(0xFFFFEBEE) else Color(0xFFF3F4F6),
                            border = BorderStroke(1.dp, if (!isAdd) Color(0xFFD32F2F) else Color.Transparent),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { isAdd = false }
                        ) {
                            Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                Text("➖ Trừ Điểm", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (!isAdd) Color(0xFFD32F2F) else VibrantTextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = pointsDeltaStr,
                        onValueChange = { if (it.all { c -> c.isDigit() }) pointsDeltaStr = it },
                        label = { Text("Số điểm muốn ${if (isAdd) "cộng" else "trừ"}") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = reasonStr,
                        onValueChange = { reasonStr = it },
                        label = { Text("Lý do điều chỉnh") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { adjustingCustomer = null },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Hủy", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val amount = pointsDeltaStr.toIntOrNull() ?: 0
                                val delta = if (isAdd) amount else -amount
                                onAdjustPoints(cust.phone, delta, reasonStr)
                                adjustingCustomer = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Xác Nhận", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Dialog: Thêm Voucher Mới
    if (showAddVoucherDialog) {
        var vTitle by remember { mutableStateOf("") }
        var vDesc by remember { mutableStateOf("") }
        var vPoints by remember { mutableStateOf("50") }
        var vDiscount by remember { mutableStateOf("15000") }

        Dialog(onDismissRequest = { showAddVoucherDialog = false }) {
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
                        Text("🎁 Tạo Mã Voucher Mới", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                        IconButton(onClick = { showAddVoucherDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = vTitle,
                        onValueChange = { vTitle = it },
                        label = { Text("Tiêu đề (VD: Giảm 20.000₫)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = vDesc,
                        onValueChange = { vDesc = it },
                        label = { Text("Mô tả áp dụng") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = vDiscount,
                            onValueChange = { if (it.all { c -> c.isDigit() }) vDiscount = it },
                            label = { Text("Mức giảm (VNĐ)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = vPoints,
                            onValueChange = { if (it.all { c -> c.isDigit() }) vPoints = it },
                            label = { Text("Điểm đổi") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddVoucherDialog = false },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Hủy", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (vTitle.isNotBlank()) {
                                    val newVoucher = Voucher(
                                        id = "VOUCHER_${System.currentTimeMillis() % 10000}",
                                        title = vTitle.trim(),
                                        description = vDesc.trim().ifBlank { "Áp dụng cho mọi hóa đơn" },
                                        pointsCost = vPoints.toIntOrNull() ?: 0,
                                        discountAmount = vDiscount.toLongOrNull() ?: 10000L
                                    )
                                    onAddVoucher(newVoucher)
                                    showAddVoucherDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantPink),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Lưu Voucher", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
