package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CustomerEntity
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemDto
import com.example.data.model.StaffMember
import com.example.data.model.ShiftRecord
import com.example.data.model.PeakHourStat
import com.example.data.repository.IceCreamRepository
import com.example.ui.util.SoundNotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val menuItem: MenuItemEntity,
    val quantity: Int = 1,
    val selectedToppings: List<MenuItemEntity> = emptyList(),
    val note: String = ""
) {
    val unitPrice: Long
        get() = menuItem.price + selectedToppings.sumOf { it.price }

    val totalPrice: Long
        get() = unitPrice * quantity

    fun toDto(): OrderItemDto {
        return OrderItemDto(
            menuItemId = menuItem.id,
            menuItemName = menuItem.name,
            category = menuItem.category,
            emoji = menuItem.emoji,
            quantity = quantity,
            unitPrice = unitPrice,
            selectedToppings = selectedToppings.map { it.name },
            note = note,
            totalItemPrice = totalPrice
        )
    }
}

data class Voucher(
    val id: String,
    val title: String,
    val description: String,
    val pointsCost: Int,
    val discountAmount: Long
)

class IceCreamViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = IceCreamRepository(application)
    private val securityPrefs = application.getSharedPreferences("icecream_security_prefs", Context.MODE_PRIVATE)

    // Device Operational Role (Cách 1: Mô hình thiết bị chuyên biệt)
    // "CUSTOMER_TABLE" -> Máy Bàn Khách (Ẩn thanh chọn vai trò, chỉ hiển thị menu/giỏ/thanh toán)
    // "STAFF_POS" -> Máy Trạm POS / Bếp dành cho nhân viên
    // "MANAGER_PORTAL" -> Máy Quản Lý / Chủ quán
    // "DEMO_MULTI_ROLE" -> Chế độ Thử Nghiệm đa vai trò (có thanh chuyển đổi nhanh)
    private val _deviceRole = MutableStateFlow(
        securityPrefs.getString("device_role", "CUSTOMER_TABLE") ?: "CUSTOMER_TABLE"
    )
    val deviceRole: StateFlow<String> = _deviceRole.asStateFlow()

    // Current app view: "CUSTOMER", "STAFF" or "MANAGER"
    private val _appMode = MutableStateFlow(
        when (securityPrefs.getString("device_role", "CUSTOMER_TABLE")) {
            "STAFF_POS" -> "STAFF"
            "MANAGER_PORTAL" -> "MANAGER"
            else -> "CUSTOMER"
        }
    )
    val appMode: StateFlow<String> = _appMode.asStateFlow()

    // Active bottom navigation / tab in customer view: "MENU", "ORDERS", "MINIGAME", "LOYALTY"
    private val _currentCustomerTab = MutableStateFlow("MENU")
    val currentCustomerTab: StateFlow<String> = _currentCustomerTab.asStateFlow()

    // Table selection & QR Check-in
    private val _selectedTable = MutableStateFlow(
        securityPrefs.getString("default_table", "Bàn 03") ?: "Bàn 03"
    )
    val selectedTable: StateFlow<String> = _selectedTable.asStateFlow()

    private val _isTableCheckedIn = MutableStateFlow(false)
    val isTableCheckedIn: StateFlow<Boolean> = _isTableCheckedIn.asStateFlow()

    private val _showQrScanner = MutableStateFlow(true)
    val showQrScanner: StateFlow<Boolean> = _showQrScanner.asStateFlow()

    // Menu category filter: "Tất cả", "Kem viên", "Topping", "Đồ uống"
    private val _selectedCategory = MutableStateFlow("Tất cả")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Database menus
    val menuItems: StateFlow<List<MenuItemEntity>> = repository.allMenuItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered menu items
    val filteredMenuItems: StateFlow<List<MenuItemEntity>> = combine(
        menuItems,
        _selectedCategory,
        _searchQuery
    ) { items, cat, query ->
        items.filter { item ->
            val matchesCategory = if (cat == "Tất cả") true else item.category == cat
            val matchesQuery = if (query.isBlank()) true else {
                item.name.contains(query, ignoreCase = true) ||
                        item.description.contains(query, ignoreCase = true) ||
                        item.tag.contains(query, ignoreCase = true)
            }
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart items
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Active applied voucher
    private val _appliedVoucher = MutableStateFlow<Voucher?>(null)
    val appliedVoucher: StateFlow<Voucher?> = _appliedVoucher.asStateFlow()

    private val _vouchers = MutableStateFlow(
        listOf(
            Voucher("VOUCH_50", "Topping Trân Châu Free", "Đổi 50 điểm lấy 1 phần topping miễn phí", 50, 8000),
            Voucher("VOUCH_100", "Voucher Ngọt Ngào -15K", "Giảm ngay 15.000₫ cho hóa đơn", 100, 15000),
            Voucher("VOUCH_150", "Voucher Đại Tiệc -30K", "Giảm ngay 30.000₫ cho hóa đơn nhóm", 150, 30000),
            Voucher("VOUCH_SUMMER", "Gelato Summer Vibe -20K", "Giảm 20k cho hóa đơn bất kỳ trên 70k", 0, 20000)
        )
    )
    val availableVouchers: StateFlow<List<Voucher>> = _vouchers.asStateFlow()

    // Customize Dialog State
    private val _customizingItem = MutableStateFlow<MenuItemEntity?>(null)
    val customizingItem: StateFlow<MenuItemEntity?> = _customizingItem.asStateFlow()

    private val _customizeQuantity = MutableStateFlow(1)
    val customizeQuantity: StateFlow<Int> = _customizeQuantity.asStateFlow()

    private val _customizeSelectedToppings = MutableStateFlow<List<MenuItemEntity>>(emptyList())
    val customizeSelectedToppings: StateFlow<List<MenuItemEntity>> = _customizeSelectedToppings.asStateFlow()

    private val _customizeNote = MutableStateFlow("")
    val customizeNote: StateFlow<String> = _customizeNote.asStateFlow()

    // Active Order tracking
    private val _activeOrderId = MutableStateFlow<String?>(null)
    val activeOrderId: StateFlow<String?> = _activeOrderId.asStateFlow()

    // All orders from Room
    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active order flow: Prioritize the active order of current selected table
    val activeOrder: StateFlow<OrderEntity?> = combine(allOrders, _activeOrderId, _selectedTable) { orders, id, table ->
        if (id != null) {
            orders.find { it.orderId == id }
        } else {
            // Find active non-completed order for current selected table
            orders.firstOrNull { it.tableNumber == table && it.status != "COMPLETED" }
                ?: orders.firstOrNull { it.tableNumber == table }
                ?: orders.firstOrNull()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Customer Loyalty & Phone State
    private val _customerPhone = MutableStateFlow("0987654321")
    val customerPhone: StateFlow<String> = _customerPhone.asStateFlow()

    private val _customerProfile = MutableStateFlow<CustomerEntity?>(null)
    val customerProfile: StateFlow<CustomerEntity?> = _customerProfile.asStateFlow()

    private val _showLoyaltyDialog = MutableStateFlow(false)
    val showLoyaltyDialog: StateFlow<Boolean> = _showLoyaltyDialog.asStateFlow()

    private val _loyaltySuccessMsg = MutableStateFlow<String?>(null)
    val loyaltySuccessMsg: StateFlow<String?> = _loyaltySuccessMsg.asStateFlow()

    // Service Calls (Gọi phục vụ tại bàn)
    private val _serviceCalls = MutableStateFlow<List<ServiceCall>>(emptyList())
    val serviceCalls: StateFlow<List<ServiceCall>> = _serviceCalls.asStateFlow()

    private val _showServiceCallSheet = MutableStateFlow(false)
    val showServiceCallSheet: StateFlow<Boolean> = _showServiceCallSheet.asStateFlow()

    // Order Payment Status (orderId -> "VIETQR" / "CASH")
    private val _orderPayments = MutableStateFlow<Map<String, String>>(emptyMap())
    val orderPayments: StateFlow<Map<String, String>> = _orderPayments.asStateFlow()

    // Minigame State
    private val _minigameScore = MutableStateFlow(0)
    val minigameScore: StateFlow<Int> = _minigameScore.asStateFlow()

    private val _minigameGameOver = MutableStateFlow(false)
    val minigameGameOver: StateFlow<Boolean> = _minigameGameOver.asStateFlow()

    private val _minigameBonusClaimed = MutableStateFlow(false)
    val minigameBonusClaimed: StateFlow<Boolean> = _minigameBonusClaimed.asStateFlow()

    // SECURITY & SETTINGS (Stored in SharedPreferences for persistent RBAC)
    private val _managerPin = MutableStateFlow(securityPrefs.getString("manager_pin", "8888") ?: "8888")
    val managerPin: StateFlow<String> = _managerPin.asStateFlow()

    private val _staffPin = MutableStateFlow(securityPrefs.getString("staff_pin", "1234") ?: "1234")
    val staffPin: StateFlow<String> = _staffPin.asStateFlow()

    private val _pinProtectionEnabled = MutableStateFlow(securityPrefs.getBoolean("pin_protection_enabled", true))
    val pinProtectionEnabled: StateFlow<Boolean> = _pinProtectionEnabled.asStateFlow()

    // STORE BANK DETAILS FOR VIETQR
    private val _bankName = MutableStateFlow(securityPrefs.getString("bank_name", "MB Bank (Ngân hàng Quân Đội)") ?: "MB Bank (Ngân hàng Quân Đội)")
    val bankName: StateFlow<String> = _bankName.asStateFlow()

    private val _bankAccount = MutableStateFlow(securityPrefs.getString("bank_account", "0987654321") ?: "0987654321")
    val bankAccount: StateFlow<String> = _bankAccount.asStateFlow()

    private val _bankOwner = MutableStateFlow(securityPrefs.getString("bank_owner", "TIEM KEM GELATO GEN Z") ?: "TIEM KEM GELATO GEN Z")
    val bankOwner: StateFlow<String> = _bankOwner.asStateFlow()

    // SOUND NOTIFICATIONS TOGGLE
    private val _soundAlertsEnabled = MutableStateFlow(true)
    val soundAlertsEnabled: StateFlow<Boolean> = _soundAlertsEnabled.asStateFlow()

    // INVENTORY STOCK & COST (COGS) ENGINE
    private val _stockMap = MutableStateFlow<Map<String, Int>>(emptyMap())
    val stockMap: StateFlow<Map<String, Int>> = _stockMap.asStateFlow()

    private val _costMap = MutableStateFlow<Map<String, Long>>(emptyMap())
    val costMap: StateFlow<Map<String, Long>> = _costMap.asStateFlow()

    // STAFF DIRECTORY
    private val _staffList = MutableStateFlow<List<StaffMember>>(
        listOf(
            StaffMember("NV01", "Nguyễn Thu Hà", "Trưởng ca & Thu ngân", "0912345678", "ACTIVE", 22, "👩‍💼", "11/2025"),
            StaffMember("NV02", "Trần Minh Quân", "Barista & Nghệ nhân Gelato", "0987654321", "ACTIVE", 31, "🧑‍🍳", "12/2025"),
            StaffMember("NV03", "Lê Hoàng Yến", "Nhân viên Phục vụ Bàn", "0905123987", "ACTIVE", 17, "👧", "01/2026"),
            StaffMember("NV04", "Phạm Đức Anh", "Pha chế Topping & Kho", "0934567890", "BREAK", 14, "👦", "02/2026")
        )
    )
    val staffList: StateFlow<List<StaffMember>> = _staffList.asStateFlow()

    // SHIFT AUDIT & CLOSING LOGS
    private val _shiftRecords = MutableStateFlow<List<ShiftRecord>>(
        listOf(
            ShiftRecord(
                shiftId = "CA_20260907_01",
                shiftName = "Ca Sáng (08:00 - 15:00)",
                startTime = System.currentTimeMillis() - 1000 * 60 * 60 * 9,
                endTime = System.currentTimeMillis() - 1000 * 60 * 60 * 2,
                staffName = "Nguyễn Thu Hà",
                openingCash = 1500000L,
                cashRevenue = 1120000L,
                qrRevenue = 2480000L,
                totalRevenue = 3600000L,
                orderCount = 32,
                isBalanced = true,
                notes = "Đối soát chuẩn xác 100%, nộp két đầy đủ"
            )
        )
    )
    val shiftRecords: StateFlow<List<ShiftRecord>> = _shiftRecords.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initDefaultMenuIfNeeded()
            loadCustomerProfile(_customerPhone.value)

            // Populate initial stocks and COGS costs
            repository.allMenuItems.collect { items ->
                if (items.isNotEmpty()) {
                    val currentStocks = _stockMap.value.toMutableMap()
                    val currentCosts = _costMap.value.toMutableMap()
                    items.forEach { item ->
                        if (!currentStocks.containsKey(item.id)) {
                            val seed = kotlin.math.abs(item.id.hashCode()) % 25
                            currentStocks[item.id] = 20 + seed // default stock between 20 and 44
                        }
                        if (!currentCosts.containsKey(item.id)) {
                            currentCosts[item.id] = (item.price * 38L) / 100L // default COGS ~38%
                        }
                    }
                    _stockMap.value = currentStocks
                    _costMap.value = currentCosts
                }
            }
        }
    }

    fun setAppMode(mode: String) {
        _appMode.value = mode
    }

    fun setDeviceRole(newRole: String, defaultTable: String? = null) {
        _deviceRole.value = newRole
        securityPrefs.edit().putString("device_role", newRole).apply()
        if (!defaultTable.isNullOrBlank()) {
            _selectedTable.value = defaultTable
            securityPrefs.edit().putString("default_table", defaultTable).apply()
        }
        when (newRole) {
            "CUSTOMER_TABLE" -> {
                _appMode.value = "CUSTOMER"
                _currentCustomerTab.value = "MENU"
            }
            "STAFF_POS" -> {
                _appMode.value = "STAFF"
            }
            "MANAGER_PORTAL" -> {
                _appMode.value = "MANAGER"
            }
            "DEMO_MULTI_ROLE" -> {
                // Keep current role
            }
        }
    }

    fun setCustomerTab(tab: String) {
        _currentCustomerTab.value = tab
    }

    fun selectTable(table: String) {
        _selectedTable.value = table
        securityPrefs.edit().putString("default_table", table).apply()
    }

    fun openQrScanner() {
        _showQrScanner.value = true
    }

    fun closeQrScanner() {
        _showQrScanner.value = false
    }

    fun checkInWithQr(table: String, phone: String?) {
        _selectedTable.value = table
        _isTableCheckedIn.value = true
        _showQrScanner.value = false

        // Check if there is an existing ongoing order for this table (Multi-user / group join session)
        val existingTableOrder = allOrders.value.firstOrNull { it.tableNumber == table && it.status != "COMPLETED" }
        if (existingTableOrder != null) {
            _activeOrderId.value = existingTableOrder.orderId
        }

        if (!phone.isNullOrBlank()) {
            _customerPhone.value = phone
            viewModelScope.launch {
                val cust = repository.checkInCustomer(phone)
                _customerProfile.value = cust
                if (existingTableOrder != null) {
                    _loyaltySuccessMsg.value = "Quét QR thành công! Đã tham gia nhóm gọi món tại $table (${existingTableOrder.orderId}) & tích điểm cho $phone 🍧"
                } else {
                    _loyaltySuccessMsg.value = "Quét QR thành công! Đã vào $table & liên kết tích điểm cho SĐT $phone 🍧"
                }
            }
        } else {
            if (existingTableOrder != null) {
                _loyaltySuccessMsg.value = "Quét QR thành công! Đã tham gia phiên gọi món nhóm tại $table (${existingTableOrder.orderId}) 🍧"
            } else {
                _loyaltySuccessMsg.value = "Quét QR thành công! Chào mừng bạn đến với $table 🍧"
            }
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Customization dialog handlers
    fun openCustomizeDialog(item: MenuItemEntity) {
        _customizingItem.value = item
        _customizeQuantity.value = 1
        _customizeSelectedToppings.value = emptyList()
        _customizeNote.value = ""
    }

    fun closeCustomizeDialog() {
        _customizingItem.value = null
    }

    fun increaseCustomizeQty() {
        _customizeQuantity.value = _customizeQuantity.value + 1
    }

    fun decreaseCustomizeQty() {
        if (_customizeQuantity.value > 1) {
            _customizeQuantity.value = _customizeQuantity.value - 1
        }
    }

    fun toggleCustomizeTopping(topping: MenuItemEntity) {
        val current = _customizeSelectedToppings.value.toMutableList()
        if (current.any { it.id == topping.id }) {
            current.removeAll { it.id == topping.id }
        } else {
            current.add(topping)
        }
        _customizeSelectedToppings.value = current
    }

    fun setCustomizeNote(note: String) {
        _customizeNote.value = note
    }

    fun addCustomizedToCart() {
        val item = _customizingItem.value ?: return
        val cartItem = CartItem(
            menuItem = item,
            quantity = _customizeQuantity.value,
            selectedToppings = _customizeSelectedToppings.value,
            note = _customizeNote.value.trim()
        )
        _cartItems.update { it + cartItem }
        closeCustomizeDialog()
    }

    fun quickAddToCart(item: MenuItemEntity) {
        // Quick add 1 item without toppings
        val existingIndex = _cartItems.value.indexOfFirst {
            it.menuItem.id == item.id && it.selectedToppings.isEmpty() && it.note.isEmpty()
        }
        if (existingIndex >= 0) {
            val updated = _cartItems.value.toMutableList()
            val existing = updated[existingIndex]
            updated[existingIndex] = existing.copy(quantity = existing.quantity + 1)
            _cartItems.value = updated
        } else {
            _cartItems.update { it + CartItem(menuItem = item, quantity = 1) }
        }
    }

    fun updateCartItemQuantity(cartItemId: String, delta: Int) {
        val updated = _cartItems.value.mapNotNull { item ->
            if (item.id == cartItemId) {
                val newQty = item.quantity + delta
                if (newQty > 0) item.copy(quantity = newQty) else null
            } else item
        }
        _cartItems.value = updated
    }

    fun removeCartItem(cartItemId: String) {
        _cartItems.update { it.filterNot { item -> item.id == cartItemId } }
    }

    fun applyVoucher(voucher: Voucher) {
        val customer = _customerProfile.value
        if (customer != null && customer.points >= voucher.pointsCost) {
            _appliedVoucher.value = voucher
        }
    }

    fun applySpecialPromoVoucher() {
        val promo = Voucher(
            id = "PROMO_GELATO20",
            title = "Đại Tiệc Mùa Hè -20K",
            description = "Khuyến mãi đặc biệt: Giảm 20.000₫",
            pointsCost = 0,
            discountAmount = 20000
        )
        _appliedVoucher.value = promo
        _loyaltySuccessMsg.value = "🎉 Đã áp dụng mã ưu đãi đặc biệt: Giảm 20.000₫ cho đơn hàng!"
    }

    fun removeVoucher() {
        _appliedVoucher.value = null
    }

    // Submit Order
    fun submitOrder(note: String = "") {
        if (_cartItems.value.isEmpty()) return
        viewModelScope.launch {
            val dtoList = _cartItems.value.map { it.toDto() }
            val discount = _appliedVoucher.value?.discountAmount ?: 0L
            val phone = _customerPhone.value.takeIf { it.isNotBlank() }

            val order = repository.createOrder(
                tableNumber = _selectedTable.value,
                items = dtoList,
                note = note,
                discountAmount = discount,
                customerPhone = phone
            )

            // Deduct voucher points if used
            _appliedVoucher.value?.let { v ->
                val current = _customerProfile.value
                if (current != null) {
                    val updated = current.copy(points = (current.points - v.pointsCost).coerceAtLeast(0))
                    _customerProfile.value = updated
                }
            }

            val hadPreviousOrder = _activeOrderId.value != null
            _activeOrderId.value = order.orderId
            _cartItems.value = emptyList()
            _appliedVoucher.value = null

            SoundNotificationHelper.playNewOrderSound()

            if (hadPreviousOrder) {
                _loyaltySuccessMsg.value = "🍨 Đã gửi thêm món vào ${_selectedTable.value}! Bếp đang chuẩn bị thêm cho bạn."
            } else {
                _loyaltySuccessMsg.value = "🍨 Đã gửi đơn gọi món thành công! Bếp đang chuẩn bị cho ${_selectedTable.value}."
            }

            // Automatically switch to Minigame/Status waiting screen!
            _currentCustomerTab.value = "MINIGAME"
            _minigameGameOver.value = false
            _minigameScore.value = 0
            _minigameBonusClaimed.value = false
        }
    }

    // Minigame controls
    fun onMinigameScoreChanged(score: Int) {
        _minigameScore.value = score
    }

    fun endMinigame(score: Int) {
        _minigameScore.value = score
        _minigameGameOver.value = true
        _showLoyaltyDialog.value = true
    }

    fun restartMinigame() {
        _minigameScore.value = 0
        _minigameGameOver.value = false
        _minigameBonusClaimed.value = false
    }

    // Loyalty controls
    fun openLoyaltyDialog() {
        _showLoyaltyDialog.value = true
    }

    fun closeLoyaltyDialog() {
        _showLoyaltyDialog.value = false
    }

    fun claimLoyaltyPoints(phone: String) {
        if (phone.length < 9) return
        viewModelScope.launch {
            _customerPhone.value = phone
            val currentOrderId = _activeOrderId.value ?: "#KEM-DEMO"
            val minigameBonus = (_minigameScore.value / 10).coerceAtLeast(5) // Bonus points from game!

            val customer = repository.claimPointsForOrder(currentOrderId, phone, minigameBonus)
            _customerProfile.value = customer
            _minigameBonusClaimed.value = true
            _loyaltySuccessMsg.value = "+${customer.points} điểm đã được lưu cho số $phone (gồm +$minigameBonus điểm minigame)!"
            _showLoyaltyDialog.value = false
        }
    }

    fun loadCustomerProfile(phone: String) {
        viewModelScope.launch {
            _customerPhone.value = phone
            val cust = repository.getCustomerByPhone(phone)
            _customerProfile.value = cust
        }
    }

    fun advanceOrderStatusManual(orderId: String, nextStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatusManual(orderId, nextStatus)
            SoundNotificationHelper.playSuccessChime()
        }
    }

    fun dismissSuccessMsg() {
        _loyaltySuccessMsg.value = null
    }

    // Service Call actions
    fun openServiceCallSheet() {
        _showServiceCallSheet.value = true
    }

    fun closeServiceCallSheet() {
        _showServiceCallSheet.value = false
    }

    fun requestService(tableNumber: String, requestText: String) {
        val newCall = ServiceCall(
            tableNumber = tableNumber,
            requestType = "GENERAL",
            requestText = requestText
        )
        _serviceCalls.update { listOf(newCall) + it }
        SoundNotificationHelper.playServiceCallBell()
        _loyaltySuccessMsg.value = "🔔 Đã gửi yêu cầu tới nhân viên! Bàn $tableNumber sẽ được hỗ trợ ngay."
    }

    fun resolveServiceCall(callId: String) {
        _serviceCalls.update { list -> list.filterNot { it.id == callId } }
        SoundNotificationHelper.playSuccessChime()
    }

    // Payment actions
    fun confirmOrderPayment(orderId: String, method: String) {
        _orderPayments.update { current ->
            current + (orderId to method)
        }
        val methodLabel = if (method == "VIETQR") "Chuyển khoản VietQR" else "Tiền mặt tại bàn"
        SoundNotificationHelper.playSuccessChime()
        _loyaltySuccessMsg.value = "Đã xác nhận thanh toán qua $methodLabel cho đơn #$orderId 🎉"
    }

    // All Customers for Manager CRM
    val allCustomers: StateFlow<List<CustomerEntity>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Customer Feedback History for Manager
    private val _feedbacks = MutableStateFlow<List<CustomerFeedback>>(
        listOf(
            CustomerFeedback(
                tableNumber = "Bàn 02",
                phone = "0912***456",
                rating = 5,
                tags = listOf("Mềm mịn chuẩn Ý", "Trang trí đẹp", "Nhân viên thân thiện"),
                comment = "Kem hạt dẻ Pistachio ngon tuyệt đỉnh, thơm bùi rất tự nhiên!",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 35
            ),
            CustomerFeedback(
                tableNumber = "Bàn 07",
                phone = "0988***112",
                rating = 5,
                tags = listOf("Phục vụ nhanh nhẹn", "Không gian mát mẻ"),
                comment = "Quét QR đặt món siêu tiện lợi, kem bưng ra chỉ sau 3 phút.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 120
            )
        )
    )
    val feedbacks: StateFlow<List<CustomerFeedback>> = _feedbacks.asStateFlow()

    // Stock availability actions
    fun toggleMenuItemAvailability(itemId: String, isAvailable: Boolean) {
        viewModelScope.launch {
            repository.updateItemAvailability(itemId, isAvailable)
        }
    }

    // Customer Feedback
    private val _showFeedbackDialog = MutableStateFlow(false)
    val showFeedbackDialog = _showFeedbackDialog.asStateFlow()

    fun openFeedbackDialog() {
        _showFeedbackDialog.value = true
    }

    fun closeFeedbackDialog() {
        _showFeedbackDialog.value = false
    }

    fun submitFeedback(rating: Int, tags: List<String>, comment: String) {
        closeFeedbackDialog()
        val phone = _customerPhone.value
        val table = _selectedTable.value
        val newFeedback = CustomerFeedback(
            tableNumber = table,
            phone = if (phone.isNotBlank()) phone else "Khách tại bàn",
            rating = rating,
            tags = tags,
            comment = comment
        )
        _feedbacks.update { listOf(newFeedback) + it }
        SoundNotificationHelper.playSuccessChime()
        if (phone.isNotBlank()) {
            viewModelScope.launch {
                val updatedCustomer = repository.addLoyaltyPoints(phone, 5)
                _customerProfile.value = updatedCustomer
            }
            _loyaltySuccessMsg.value = "🍨 Cảm ơn bạn đã góp ý! Bạn nhận được +5 điểm thưởng tích lũy."
        } else {
            _loyaltySuccessMsg.value = "🍨 Cảm ơn bạn rất nhiều vì những góp ý quý giá!"
        }
    }

    // STAFF OPERATIONS
    fun switchTableForOrder(orderId: String, newTable: String) {
        viewModelScope.launch {
            repository.switchOrderTable(orderId, newTable)
            _loyaltySuccessMsg.value = "Đã chuyển đơn #$orderId sang $newTable"
            SoundNotificationHelper.playSuccessChime()
        }
    }

    fun clearTableAndCompleteOrder(tableNumber: String) {
        viewModelScope.launch {
            // Find active orders for this table and mark them as COMPLETED
            val tableOrders = allOrders.value.filter { it.tableNumber == tableNumber && it.status != "COMPLETED" }
            for (order in tableOrders) {
                repository.updateOrderStatusManual(order.orderId, "COMPLETED")
            }
            // Clear any lingering service calls for this table
            _serviceCalls.update { list -> list.filterNot { it.tableNumber == tableNumber } }
            // If the currently simulated customer session was this table, reset session
            if (_selectedTable.value == tableNumber) {
                _activeOrderId.value = null
                _cartItems.value = emptyList()
                _appliedVoucher.value = null
            }
            SoundNotificationHelper.playSuccessChime()
            _loyaltySuccessMsg.value = "🧹 Đã dọn bàn $tableNumber & chuyển thành Bàn Trống sẵn sàng đón khách mới!"
        }
    }

    fun createStaffPosOrder(
        tableNumber: String,
        itemSummaries: List<String>,
        totalAmount: Long,
        note: String
    ) {
        viewModelScope.launch {
            val shortId = (1000..9999).random()
            val orderId = "#POS-$shortId"
            val summary = itemSummaries.joinToString(" • ")
            val order = OrderEntity(
                orderId = orderId,
                tableNumber = tableNumber,
                itemsSummary = summary,
                itemsJson = "",
                totalAmount = totalAmount,
                discountAmount = 0L,
                finalAmount = totalAmount,
                note = if (note.isBlank()) "Đơn tạo bởi nhân viên tại quầy POS" else note,
                status = "RECEIVED",
                customerPhone = null,
                earnedPoints = (totalAmount / 10000).toInt(),
                createdAt = System.currentTimeMillis()
            )
            repository.insertRawOrder(order)
            SoundNotificationHelper.playNewOrderSound()
            _loyaltySuccessMsg.value = "🛎️ Nhân viên POS đã tạo đơn cho $tableNumber ($orderId)"
        }
    }

    // MANAGER OPERATIONS
    fun addNewMenuItem(
        name: String,
        category: String,
        price: Long,
        description: String,
        emoji: String,
        colorHex: Long,
        tag: String
    ) {
        viewModelScope.launch {
            val newItem = MenuItemEntity(
                id = "KEM_${System.currentTimeMillis() % 10000}",
                name = name,
                category = category,
                price = price,
                description = description,
                emoji = emoji.ifBlank { "🍨" },
                colorHex = colorHex,
                tag = tag,
                isAvailable = true
            )
            repository.saveMenuItem(newItem)
            _loyaltySuccessMsg.value = "✨ Đã thêm món mới: $name vào thực đơn!"
            SoundNotificationHelper.playSuccessChime()
        }
    }

    fun updateMenuItemDetails(item: MenuItemEntity) {
        viewModelScope.launch {
            repository.updateMenuItem(item)
            _loyaltySuccessMsg.value = "Đã cập nhật món: ${item.name}"
            SoundNotificationHelper.playSuccessChime()
        }
    }

    fun deleteMenuItem(item: MenuItemEntity) {
        viewModelScope.launch {
            repository.deleteMenuItem(item)
            _loyaltySuccessMsg.value = "Đã xóa món: ${item.name} khỏi thực đơn"
            SoundNotificationHelper.playSuccessChime()
        }
    }

    fun addNewVoucher(title: String, desc: String, pointsCost: Int, discount: Long) {
        val newVoucher = Voucher(
            id = "VOUCH_${System.currentTimeMillis() % 10000}",
            title = title,
            description = desc,
            pointsCost = pointsCost,
            discountAmount = discount
        )
        _vouchers.update { listOf(newVoucher) + it }
        _loyaltySuccessMsg.value = "🎉 Đã tạo mã khuyến mãi mới: $title"
        SoundNotificationHelper.playSuccessChime()
    }

    fun deleteVoucher(voucherId: String) {
        _vouchers.update { it.filterNot { v -> v.id == voucherId } }
        _loyaltySuccessMsg.value = "Đã gỡ bỏ mã khuyến mãi"
    }

    // SECURITY & ACCESS CONTROL
    fun updateSecurityPins(newManagerPin: String, newStaffPin: String, enabled: Boolean): Boolean {
        if (newManagerPin.length < 4 || newStaffPin.length < 4) return false
        _managerPin.value = newManagerPin
        _staffPin.value = newStaffPin
        _pinProtectionEnabled.value = enabled
        securityPrefs.edit()
            .putString("manager_pin", newManagerPin)
            .putString("staff_pin", newStaffPin)
            .putBoolean("pin_protection_enabled", enabled)
            .apply()
        _loyaltySuccessMsg.value = "🔒 Đã lưu cài đặt bảo mật và mã PIN thành công!"
        SoundNotificationHelper.playSuccessChime()
        return true
    }

    fun updateBankDetails(name: String, account: String, owner: String) {
        val safeName = name.ifBlank { "MB Bank (Ngân hàng Quân Đội)" }
        val safeAccount = account.ifBlank { "0987654321" }
        val safeOwner = owner.uppercase().ifBlank { "TIEM KEM GELATO GEN Z" }
        _bankName.value = safeName
        _bankAccount.value = safeAccount
        _bankOwner.value = safeOwner
        securityPrefs.edit()
            .putString("bank_name", safeName)
            .putString("bank_account", safeAccount)
            .putString("bank_owner", safeOwner)
            .apply()
        _loyaltySuccessMsg.value = "🏦 Đã cập nhật tài khoản nhận tiền VietQR thành công!"
        SoundNotificationHelper.playSuccessChime()
    }

    fun verifyPinForRole(role: String, inputPin: String): Boolean {
        if (!_pinProtectionEnabled.value) return true
        val targetPin = if (role == "MANAGER") _managerPin.value else _staffPin.value
        return inputPin == targetPin
    }

    fun exportDataSummary(): String {
        val totalRevenue = allOrders.value.filter { it.status == "SERVED" || it.status == "READY" }.sumOf { it.finalAmount }
        val ordersCount = allOrders.value.size
        val menuCount = menuItems.value.size
        val customerCount = allCustomers.value.size
        val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault())
        return """
            {
              "exportTime": "${dateFormat.format(java.util.Date())}",
              "store": "TIỆM KEM GELATO GEN Z",
              "databaseEngine": "Android Room SQLite",
              "stats": {
                "totalMenuItems": $menuCount,
                "totalOrders": $ordersCount,
                "totalCustomers": $customerCount,
                "accumulatedRevenueVND": $totalRevenue
              },
              "vietQrConfig": {
                "bankName": "${_bankName.value}",
                "accountNumber": "${_bankAccount.value}",
                "accountHolder": "${_bankOwner.value}"
              },
              "security": {
                "pinProtectionEnabled": ${_pinProtectionEnabled.value}
              }
            }
        """.trimIndent()
    }

    // STOCK & INVENTORY MANAGEMENT
    fun updateItemStock(itemId: String, newStock: Int) {
        _stockMap.update { it + (itemId to newStock.coerceAtLeast(0)) }
        _loyaltySuccessMsg.value = "Đã cập nhật tồn kho món!"
    }

    fun quickAddStock(itemId: String, delta: Int) {
        val current = _stockMap.value[itemId] ?: 20
        val updated = (current + delta).coerceAtLeast(0)
        _stockMap.update { it + (itemId to updated) }
        _loyaltySuccessMsg.value = "Đã cộng thêm $delta suất vào kho (Hiện có: $updated)"
    }

    fun updateItemCost(itemId: String, cost: Long) {
        _costMap.update { it + (itemId to cost.coerceAtLeast(0L)) }
        _loyaltySuccessMsg.value = "Đã cập nhật giá vốn món ăn!"
    }

    // STAFF DIRECTORY MANAGEMENT
    fun toggleStaffStatus(staffId: String) {
        _staffList.update { list ->
            list.map { staff ->
                if (staff.id == staffId) {
                    val next = when (staff.shiftStatus) {
                        "ACTIVE" -> "BREAK"
                        "BREAK" -> "OFF"
                        else -> "ACTIVE"
                    }
                    staff.copy(shiftStatus = next)
                } else staff
            }
        }
    }

    fun addNewStaff(name: String, role: String, phone: String, emoji: String) {
        val newStaff = StaffMember(
            id = "NV0${_staffList.value.size + 1}",
            name = name,
            role = role,
            phone = phone,
            shiftStatus = "ACTIVE",
            avatarEmoji = emoji.ifBlank { "👩‍🍳" },
            joinedDate = java.text.SimpleDateFormat("MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        )
        _staffList.update { it + newStaff }
        _loyaltySuccessMsg.value = "✨ Đã thêm nhân sự mới: $name ($role)"
        SoundNotificationHelper.playSuccessChime()
    }

    fun deleteStaff(staffId: String) {
        _staffList.update { it.filterNot { s -> s.id == staffId } }
        _loyaltySuccessMsg.value = "Đã xóa nhân sự khỏi hệ thống!"
    }

    // SHIFT AUDIT CLOSING
    fun closeShiftAudit(shiftName: String, staffName: String, openingCash: Long, countedCash: Long, notes: String) {
        val currentOrders = allOrders.value
        val cashRev = currentOrders.filter { orderPayments.value[it.orderId] == "CASH" }.sumOf { it.totalAmount }
        val qrRev = currentOrders.filter { orderPayments.value[it.orderId] == "VIETQR" }.sumOf { it.totalAmount }
        val totalRev = cashRev + qrRev
        val expectedCash = openingCash + cashRev
        val diff = countedCash - expectedCash
        val isBalanced = kotlin.math.abs(diff) <= 10000L
        val newRecord = ShiftRecord(
            shiftId = "CA_${System.currentTimeMillis() % 100000}",
            shiftName = shiftName,
            startTime = System.currentTimeMillis() - 1000 * 60 * 60 * 6,
            endTime = System.currentTimeMillis(),
            staffName = staffName,
            openingCash = openingCash,
            cashRevenue = cashRev,
            qrRevenue = qrRev,
            totalRevenue = totalRev,
            orderCount = currentOrders.size,
            isBalanced = isBalanced,
            notes = if (diff == 0L) "Cân bằng chính xác 100%" else "Chênh lệch tiền mặt: ${if (diff > 0) "+" else ""}${diff}đ. $notes"
        )
        _shiftRecords.update { listOf(newRecord) + it }
        _loyaltySuccessMsg.value = "🏁 Đã chốt sổ $shiftName thành công!"
        SoundNotificationHelper.playSuccessChime()
    }

    // CUSTOMER MANUAL POINTS ADJUSTMENT
    fun adjustCustomerPoints(phone: String, deltaPoints: Int, reason: String) {
        viewModelScope.launch {
            val currentCust = repository.findCustomer(phone)
            if (currentCust != null) {
                val updatedPoints = (currentCust.points + deltaPoints).coerceAtLeast(0)
                val updatedTier = when {
                    updatedPoints >= 500 -> "VIP Kim Cương"
                    updatedPoints >= 300 -> "Kem Vàng"
                    updatedPoints >= 100 -> "Kem Bạc"
                    else -> "Thành viên Mới"
                }
                val updated = currentCust.copy(points = updatedPoints, tier = updatedTier, updatedAt = System.currentTimeMillis())
                repository.saveCustomer(updated)
                if (_customerPhone.value == phone) {
                    _customerProfile.value = updated
                }
                _loyaltySuccessMsg.value = "Đã ${if (deltaPoints >= 0) "cộng" else "trừ"} $deltaPoints điểm cho $phone ($reason)"
                SoundNotificationHelper.playSuccessChime()
            }
        }
    }

    fun toggleSoundAlerts(enabled: Boolean) {
        _soundAlertsEnabled.value = enabled
        _loyaltySuccessMsg.value = if (enabled) "Đã bật âm báo âm thanh" else "Đã tắt âm báo âm thanh"
    }

    fun generateDetailedFinancialReport(): String {
        val orders = allOrders.value
        val totalRevenue = orders.sumOf { it.totalAmount }
        val discountTotal = orders.sumOf { it.discountAmount }
        val netRevenue = totalRevenue - discountTotal
        val cashRev = orders.filter { orderPayments.value[it.orderId] == "CASH" }.sumOf { it.totalAmount }
        val qrRev = orders.filter { orderPayments.value[it.orderId] == "VIETQR" }.sumOf { it.totalAmount }
        val nowStr = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        return """
==================================================
      BÁO CÁO DOANH THU & TÀI CHÍNH CHUYÊN SÂU
             TIỆM KEM GELATO GEN Z
==================================================
Thời điểm xuất: $nowStr

1. TỔNG QUAN TÀI CHÍNH:
- Tổng doanh số danh nghĩa: ${java.lang.String.format(java.util.Locale.GERMANY, "%,d₫", totalRevenue)}
- Chi phí chiết khấu/Voucher: -${java.lang.String.format(java.util.Locale.GERMANY, "%,d₫", discountTotal)}
- DOANH THU THỰC NHẬN (NET): ${java.lang.String.format(java.util.Locale.GERMANY, "%,d₫", netRevenue)}
- Tổng số đơn hàng: ${orders.size} đơn
- Giá trị đơn trung bình (AOV): ${if (orders.isNotEmpty()) java.lang.String.format(java.util.Locale.GERMANY, "%,d₫", totalRevenue / orders.size) else "0₫"}

2. CƠ CẤU PHƯƠNG THỨC THANH TOÁN:
- Chuyển khoản VietQR Napas 247: ${java.lang.String.format(java.util.Locale.GERMANY, "%,d₫", qrRev)}
- Tiền mặt tại quầy (Cash): ${java.lang.String.format(java.util.Locale.GERMANY, "%,d₫", cashRev)}

3. NHÂN SỰ & THÀNH VIÊN:
- Tổng thành viên tích điểm: ${allCustomers.value.size} khách
- Nhân sự trong ca: ${_staffList.value.count { it.shiftStatus == "ACTIVE" }} nhân viên
- Ca làm việc đã ghi nhận: ${_shiftRecords.value.size} ca

==================================================
(Báo cáo được lập tự động từ Hệ thống Quản trị Gelato)
        """.trimIndent()
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.initDefaultMenuIfNeeded(forceReset = true)
            _loyaltySuccessMsg.value = "🔄 Đã tải lại danh sách món mẫu thành công!"
            SoundNotificationHelper.playSuccessChime()
        }
    }
}

data class CustomerFeedback(
    val id: String = java.util.UUID.randomUUID().toString(),
    val tableNumber: String,
    val phone: String,
    val rating: Int,
    val tags: List<String>,
    val comment: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class ServiceCall(
    val id: String = java.util.UUID.randomUUID().toString(),
    val tableNumber: String,
    val requestType: String,
    val requestText: String,
    val timestamp: Long = System.currentTimeMillis()
)
