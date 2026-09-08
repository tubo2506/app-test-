package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.IceCreamViewModel
import com.example.ui.components.CustomerFeedbackDialog
import com.example.ui.components.LoyaltyPhoneDialog
import com.example.ui.components.PinVerificationDialog
import com.example.ui.components.QrCheckInDialog
import com.example.ui.components.ServiceCallSheet
import com.example.ui.screens.KitchenScreen
import com.example.ui.screens.LoyaltyScreen
import com.example.ui.screens.ManagerScreen
import com.example.ui.screens.MenuOrderScreen
import com.example.ui.screens.MinigameScreen
import com.example.ui.screens.OrderTrackerScreen
import com.example.ui.screens.StaffScreen
import com.example.ui.theme.VibrantBg
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantGreenBg
import com.example.ui.theme.VibrantGreenDark
import com.example.ui.theme.VibrantMint
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkDark
import com.example.ui.theme.VibrantPinkLight
import com.example.ui.theme.VibrantPinkSubheading
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                IceCreamOrderApp()
            }
        }
    }
}

@Composable
fun IceCreamOrderApp(viewModel: IceCreamViewModel = viewModel()) {
    val appMode by viewModel.appMode.collectAsState()
    val currentTab by viewModel.currentCustomerTab.collectAsState()
    val selectedTable by viewModel.selectedTable.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val menuItems by viewModel.filteredMenuItems.collectAsState()
    val allRawMenuItems by viewModel.menuItems.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val appliedVoucher by viewModel.appliedVoucher.collectAsState()
    val activeOrder by viewModel.activeOrder.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val customerProfile by viewModel.customerProfile.collectAsState()
    val customerPhone by viewModel.customerPhone.collectAsState()
    val showLoyaltyDialog by viewModel.showLoyaltyDialog.collectAsState()
    val showQrScanner by viewModel.showQrScanner.collectAsState()
    val minigameScore by viewModel.minigameScore.collectAsState()
    val loyaltySuccessMsg by viewModel.loyaltySuccessMsg.collectAsState()
    val serviceCalls by viewModel.serviceCalls.collectAsState()
    val showServiceCallSheet by viewModel.showServiceCallSheet.collectAsState()
    val orderPayments by viewModel.orderPayments.collectAsState()
    val showFeedbackDialog by viewModel.showFeedbackDialog.collectAsState()
    val allCustomers by viewModel.allCustomers.collectAsState()
    val feedbacks by viewModel.feedbacks.collectAsState()
    val availableVouchers by viewModel.availableVouchers.collectAsState()

    // Security and Bank state
    val pinProtectionEnabled by viewModel.pinProtectionEnabled.collectAsState()
    val managerPin by viewModel.managerPin.collectAsState()
    val staffPin by viewModel.staffPin.collectAsState()
    val bankName by viewModel.bankName.collectAsState()
    val bankAccount by viewModel.bankAccount.collectAsState()
    val bankOwner by viewModel.bankOwner.collectAsState()

    // Deep Management state
    val stockMap by viewModel.stockMap.collectAsState()
    val costMap by viewModel.costMap.collectAsState()
    val staffList by viewModel.staffList.collectAsState()
    val shiftRecords by viewModel.shiftRecords.collectAsState()
    val soundAlertsEnabled by viewModel.soundAlertsEnabled.collectAsState()
    val deviceRole by viewModel.deviceRole.collectAsState()

    var pendingRoleToSwitch by remember { mutableStateOf<String?>(null) }
    var showDeviceSetupDialog by remember { mutableStateOf(false) }
    var showAdminPinPrompt by remember { mutableStateOf(false) }

    // Customization state
    val customizingItem by viewModel.customizingItem.collectAsState()
    val customizeQty by viewModel.customizeQuantity.collectAsState()
    val customizeSelectedToppings by viewModel.customizeSelectedToppings.collectAsState()
    val customizeNote by viewModel.customizeNote.collectAsState()

    val availableToppings = remember(allRawMenuItems) {
        allRawMenuItems.filter { it.category == "Topping" }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var isMenuHeaderVisible by remember { mutableStateOf(true) }

    LaunchedEffect(currentTab) {
        if (currentTab != "MENU") {
            isMenuHeaderVisible = true
        }
    }

    LaunchedEffect(loyaltySuccessMsg) {
        loyaltySuccessMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissSuccessMsg()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AnimatedVisibility(
                visible = if (appMode == "CUSTOMER" && currentTab == "MENU") isMenuHeaderVisible else true,
                enter = expandVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)) + fadeIn(),
                exit = shrinkVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)) + fadeOut()
            ) {
                TopAppBarHeader(
                    deviceRole = deviceRole,
                    appMode = appMode,
                    onSelectAppMode = { newMode ->
                        if (newMode == "CUSTOMER" || !pinProtectionEnabled) {
                            viewModel.setAppMode(newMode)
                        } else {
                            pendingRoleToSwitch = newMode
                        }
                    },
                    selectedTable = selectedTable,
                    activeOrderStatus = activeOrder?.status,
                    onOpenQrScanner = { viewModel.openQrScanner() },
                    onOpenDeviceSetup = {
                        if (pinProtectionEnabled && appMode != "MANAGER") {
                            showAdminPinPrompt = true
                        } else {
                            showDeviceSetupDialog = true
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (appMode == "CUSTOMER") {
                BottomNavBar(
                    currentTab = currentTab,
                    onSelectTab = { viewModel.setCustomerTab(it) },
                    cartCount = cartItems.sumOf { it.quantity },
                    activeOrderStatus = activeOrder?.status
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = when (appMode) {
                    "STAFF", "KITCHEN" -> "STAFF"
                    "MANAGER" -> "MANAGER"
                    else -> currentTab
                },
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    "STAFF" -> {
                        StaffScreen(
                            orders = allOrders,
                            menuItems = allRawMenuItems,
                            serviceCalls = serviceCalls,
                            orderPayments = orderPayments,
                            bankName = bankName,
                            bankAccount = bankAccount,
                            bankOwner = bankOwner,
                            onAdvanceStatus = { id, next ->
                                viewModel.advanceOrderStatusManual(id, next)
                            },
                            onResolveServiceCall = { viewModel.resolveServiceCall(it) },
                            onConfirmPayment = { id, method ->
                                viewModel.confirmOrderPayment(id, method)
                            },
                            onSwitchTable = { orderId, newTable ->
                                viewModel.switchTableForOrder(orderId, newTable)
                            },
                            onClearTable = { table ->
                                viewModel.clearTableAndCompleteOrder(table)
                            },
                            onToggleItemAvailability = { id, isAvail ->
                                viewModel.toggleMenuItemAvailability(id, isAvail)
                            },
                            onCreatePosOrder = { table, items, total, note ->
                                viewModel.createStaffPosOrder(table, items, total, note)
                            },
                            onExitToCustomer = {
                                viewModel.setAppMode("CUSTOMER")
                            }
                        )
                    }
                    "MANAGER" -> {
                        ManagerScreen(
                            menuItems = allRawMenuItems,
                            orders = allOrders,
                            orderPayments = orderPayments,
                            customers = allCustomers,
                            vouchers = availableVouchers,
                            feedbacks = feedbacks,
                            stockMap = stockMap,
                            costMap = costMap,
                            staffList = staffList,
                            shiftRecords = shiftRecords,
                            pinProtectionEnabled = pinProtectionEnabled,
                            managerPin = managerPin,
                            staffPin = staffPin,
                            bankName = bankName,
                            bankAccount = bankAccount,
                            bankOwner = bankOwner,
                            soundAlertsEnabled = soundAlertsEnabled,
                            deviceRole = deviceRole,
                            selectedTable = selectedTable,
                            onUpdateSecurityPins = { mPin, sPin, enabled ->
                                viewModel.updateSecurityPins(mPin, sPin, enabled)
                            },
                            onUpdateBankDetails = { bName, bAcc, bOwner ->
                                viewModel.updateBankDetails(bName, bAcc, bOwner)
                            },
                            onExportBackup = { viewModel.exportDataSummary() },
                            onResetDemoData = { viewModel.resetDemoData() },
                            onUpdateDeviceRole = { newRole, tbl ->
                                viewModel.setDeviceRole(newRole, tbl)
                            },
                            onToggleAvailability = { id, isAvail ->
                                viewModel.toggleMenuItemAvailability(id, isAvail)
                            },
                            onAddNewMenuItem = { name, cat, price, desc, emoji, hex, tag ->
                                viewModel.addNewMenuItem(name, cat, price, desc, emoji, hex, tag)
                            },
                            onUpdateMenuItem = { viewModel.updateMenuItemDetails(it) },
                            onDeleteMenuItem = { viewModel.deleteMenuItem(it) },
                            onAddNewVoucher = { title, desc, points, discount ->
                                viewModel.addNewVoucher(title, desc, points, discount)
                            },
                            onDeleteVoucher = { viewModel.deleteVoucher(it) },
                            onQuickAddStock = { id, delta -> viewModel.quickAddStock(id, delta) },
                            onUpdateStock = { id, s -> viewModel.updateItemStock(id, s) },
                            onUpdateCost = { id, c -> viewModel.updateItemCost(id, c) },
                            onToggleStaffStatus = { id -> viewModel.toggleStaffStatus(id) },
                            onAddNewStaff = { n, r, p, e -> viewModel.addNewStaff(n, r, p, e) },
                            onDeleteStaff = { id -> viewModel.deleteStaff(id) },
                            onCloseShiftAudit = { sName, staff, open, count, note ->
                                viewModel.closeShiftAudit(sName, staff, open, count, note)
                            },
                            onAdjustPoints = { phone, delta, reason ->
                                viewModel.adjustCustomerPoints(phone, delta, reason)
                            },
                            onToggleSoundAlerts = { viewModel.toggleSoundAlerts(it) },
                            onGenerateFinancialReport = { viewModel.generateDetailedFinancialReport() },
                            onSelectTableForCustomerMode = { table ->
                                viewModel.selectTable(table)
                                viewModel.setAppMode("CUSTOMER")
                                viewModel.setCustomerTab("MENU")
                            },
                            onClearTable = { table -> viewModel.clearTableAndCompleteOrder(table) },
                            onExitToCustomer = {
                                viewModel.setAppMode("CUSTOMER")
                            }
                        )
                    }
                    "MENU" -> {
                        MenuOrderScreen(
                            menuItems = menuItems,
                            selectedCategory = selectedCategory,
                            onSelectCategory = { viewModel.selectCategory(it) },
                            searchQuery = searchQuery,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            selectedTable = selectedTable,
                            onSelectTable = { viewModel.selectTable(it) },
                            customerPhone = customerPhone,
                            onOpenQrScanner = { viewModel.openQrScanner() },
                            cartItems = cartItems,
                            onOpenCustomize = { viewModel.openCustomizeDialog(it) },
                            onQuickAdd = { viewModel.quickAddToCart(it) },
                            onUpdateCartQty = { id, delta -> viewModel.updateCartItemQuantity(id, delta) },
                            onRemoveCartItem = { viewModel.removeCartItem(it) },
                            appliedVoucher = appliedVoucher,
                            onSubmitOrder = { note -> viewModel.submitOrder(note) },
                            // Customization dialog props
                            customizingItem = customizingItem,
                            customizeQty = customizeQty,
                            onIncreaseQty = { viewModel.increaseCustomizeQty() },
                            onDecreaseQty = { viewModel.decreaseCustomizeQty() },
                            availableToppings = availableToppings,
                            selectedToppings = customizeSelectedToppings,
                            onToggleTopping = { viewModel.toggleCustomizeTopping(it) },
                            customizeNote = customizeNote,
                            onCustomizeNoteChange = { viewModel.setCustomizeNote(it) },
                            onConfirmCustomize = { viewModel.addCustomizedToCart() },
                            onCloseCustomize = { viewModel.closeCustomizeDialog() },
                            onHeaderVisibilityChange = { isMenuHeaderVisible = it },
                            onApplySpecialPromoVoucher = { viewModel.applySpecialPromoVoucher() },
                            onOpenServiceCall = { viewModel.openServiceCallSheet() }
                        )
                    }
                    "ORDERS" -> {
                        val tableOrders = allOrders.filter { it.tableNumber == selectedTable }
                        OrderTrackerScreen(
                            orders = if (tableOrders.isNotEmpty()) tableOrders else allOrders,
                            activeOrder = activeOrder,
                            orderPayments = orderPayments,
                            bankName = bankName,
                            bankAccount = bankAccount,
                            bankOwner = bankOwner,
                            onConfirmPayment = { id, method ->
                                viewModel.confirmOrderPayment(id, method)
                            },
                            onOpenServiceCall = { viewModel.openServiceCallSheet() },
                            onOpenFeedback = { viewModel.openFeedbackDialog() },
                            onPlayMinigame = { viewModel.setCustomerTab("MINIGAME") },
                            onAddMoreItems = { viewModel.setCustomerTab("MENU") }
                        )
                    }
                    "MINIGAME" -> {
                        MinigameScreen(
                            activeOrder = activeOrder,
                            onClaimReward = { score ->
                                viewModel.endMinigame(score)
                            }
                        )
                    }
                    "LOYALTY" -> {
                        LoyaltyScreen(
                            customer = customerProfile,
                            phone = customerPhone,
                            onLookupPhone = { viewModel.loadCustomerProfile(it) },
                            availableVouchers = availableVouchers,
                            appliedVoucher = appliedVoucher,
                            onApplyVoucher = { viewModel.applyVoucher(it) },
                            onRemoveVoucher = { viewModel.removeVoucher() },
                            onOpenFeedback = { viewModel.openFeedbackDialog() }
                        )
                    }
                }
            }
        }
    }

    // Popup for Loyalty Phone input after game or on demand
    if (showLoyaltyDialog) {
        val estimatedOrderPoints = ((activeOrder?.finalAmount ?: 35000L) / 10000).toInt()
        val gameBonusPoints = (minigameScore / 10).coerceAtLeast(5)

        LoyaltyPhoneDialog(
            gameBonusPoints = gameBonusPoints,
            orderPointsEstimate = estimatedOrderPoints,
            initialPhone = customerPhone,
            onDismiss = { viewModel.closeLoyaltyDialog() },
            onConfirm = { phone ->
                viewModel.claimLoyaltyPoints(phone)
            }
        )
    }

    // Interactive QR Code Check-in Dialog (For entering table & phone loyalty)
    if (showQrScanner) {
        QrCheckInDialog(
            currentTable = selectedTable,
            currentPhone = customerPhone,
            onConfirmCheckIn = { table, phone ->
                viewModel.checkInWithQr(table, phone)
            },
            onDismiss = {
                viewModel.closeQrScanner()
            },
            canDismiss = true
        )
    }

    // Service Call Sheet for table requests (Water, Cutlery, Staff, Payment)
    if (showServiceCallSheet) {
        ServiceCallSheet(
            selectedTable = selectedTable,
            onDismiss = { viewModel.closeServiceCallSheet() },
            onSubmitRequest = { requestText ->
                viewModel.requestService(selectedTable, requestText)
            }
        )
    }

    // Customer Feedback & Rating Dialog
    if (showFeedbackDialog) {
        CustomerFeedbackDialog(
            tableNumber = selectedTable,
            onDismiss = { viewModel.closeFeedbackDialog() },
            onSubmit = { rating, tags, comment ->
                viewModel.submitFeedback(rating, tags, comment)
            }
        )
    }

    // PIN Verification Dialog for Staff and Manager role gate
    pendingRoleToSwitch?.let { targetRole ->
        PinVerificationDialog(
            targetRole = targetRole,
            onDismiss = { pendingRoleToSwitch = null },
            onVerifyPin = { enteredPin ->
                viewModel.verifyPinForRole(targetRole, enteredPin)
            },
            onSuccess = {
                viewModel.setAppMode(targetRole)
                pendingRoleToSwitch = null
            }
        )
    }

    // Admin PIN gate for opening device role configuration from locked device
    if (showAdminPinPrompt) {
        PinVerificationDialog(
            targetRole = "Quản Trị Thiết Bị",
            onDismiss = { showAdminPinPrompt = false },
            onVerifyPin = { enteredPin ->
                viewModel.verifyPinForRole("MANAGER", enteredPin)
            },
            onSuccess = {
                showAdminPinPrompt = false
                showDeviceSetupDialog = true
            }
        )
    }

    // Dedicated Device Role Setup Dialog (Cách 1)
    if (showDeviceSetupDialog) {
        DeviceRoleSetupDialog(
            currentRole = deviceRole,
            currentTable = selectedTable,
            onDismiss = { showDeviceSetupDialog = false },
            onConfirm = { newRole, tbl ->
                viewModel.setDeviceRole(newRole, tbl)
                showDeviceSetupDialog = false
            }
        )
    }
}

@Composable
fun TopAppBarHeader(
    deviceRole: String,
    appMode: String,
    onSelectAppMode: (String) -> Unit,
    selectedTable: String,
    activeOrderStatus: String?,
    onOpenQrScanner: () -> Unit,
    onOpenDeviceSetup: () -> Unit
) {
    var showRoleMenu by remember { mutableStateOf(false) }

    val roleSubtitle = when (deviceRole) {
        "CUSTOMER_TABLE" -> "$selectedTable • GỌI MÓN TẠI BÀN"
        "STAFF_POS" -> "TRẠM THU NGÂN & BẾP POS"
        "MANAGER_PORTAL" -> "BẢNG ĐIỀU HÀNH QUẢN LÝ"
        else -> when (appMode) {
            "CUSTOMER" -> "$selectedTable • ĐÃ QUÉT QR"
            "STAFF", "KITCHEN" -> "QUẦY PHỤC VỤ & BẾP LIVE"
            "MANAGER" -> "BẢNG ĐIỀU HÀNH QUẢN LÝ"
            else -> "QUÁN KEM GEN Z"
        }
    }

    val activeRoleLabel = when (appMode) {
        "CUSTOMER" -> "Khách"
        "STAFF", "KITCHEN" -> "Nhân viên"
        "MANAGER" -> "Quản lý"
        else -> "Khách"
    }

    val activeRoleIcon = when (appMode) {
        "CUSTOMER" -> Icons.Default.Dining
        "STAFF", "KITCHEN" -> Icons.Default.Kitchen
        "MANAGER" -> Icons.Default.SupervisorAccount
        else -> Icons.Default.Dining
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = VibrantBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Branding matching Vibrant Palette header
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = if (deviceRole == "CUSTOMER_TABLE" || (deviceRole == "DEMO_MULTI_ROLE" && appMode == "CUSTOMER")) {
                        Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onOpenQrScanner() }
                            .padding(vertical = 1.dp)
                    } else Modifier
                ) {
                    Text(
                        text = roleSubtitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantPinkSubheading,
                        letterSpacing = 1.2.sp
                    )
                    if (deviceRole == "CUSTOMER_TABLE" || (deviceRole == "DEMO_MULTI_ROLE" && appMode == "CUSTOMER")) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Quét lại QR",
                            tint = VibrantPink,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Kem Gen Z",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = VibrantPink,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PWA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(VibrantMint)
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }

            // Right Actions: Pure Role-dependent UI (Cách 1)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (deviceRole) {
                    "CUSTOMER_TABLE" -> {
                        // 1. Quét QR / Chọn lại bàn Button
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            shadowElevation = 1.dp,
                            border = BorderStroke(1.dp, VibrantBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onOpenQrScanner() }
                                .testTag("top_qr_scanner_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Quét mã QR",
                                    tint = VibrantPink,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Quét QR",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantPink
                                )
                            }
                        }

                        // 2. Discreet Admin Lock: Tap to input manager PIN and reconfigure device
                        // NOTE: NO ROLE DROPDOWN IS SHOWN TO CUSTOMERS AT ALL!
                        IconButton(
                            onClick = onOpenDeviceSetup,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("admin_device_lock")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Cấu hình thiết bị",
                                tint = VibrantTextMuted.copy(alpha = 0.35f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    "STAFF_POS" -> {
                        // Staff Terminal
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            shadowElevation = 1.dp,
                            border = BorderStroke(1.dp, VibrantBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onOpenDeviceSetup() }
                                .testTag("staff_device_setup_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = VibrantMint,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Trạm POS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantTextPrimary
                                )
                            }
                        }
                    }
                    "MANAGER_PORTAL" -> {
                        // Manager Portal
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            shadowElevation = 1.dp,
                            border = BorderStroke(1.dp, VibrantBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onOpenDeviceSetup() }
                                .testTag("manager_device_setup_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = VibrantPink,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Cài đặt máy",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantTextPrimary
                                )
                            }
                        }
                    }
                    else -> {
                        // "DEMO_MULTI_ROLE": Shows testing dropdown & quick lock
                        if (appMode == "CUSTOMER") {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White,
                                shadowElevation = 1.dp,
                                border = BorderStroke(1.dp, VibrantBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onOpenQrScanner() }
                                    .testTag("top_qr_scanner_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = "Quét mã QR",
                                        tint = VibrantPink,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Quét QR",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VibrantPink
                                    )
                                }
                            }
                        }

                        // Role switcher button (for demo mode only)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            shadowElevation = 1.dp,
                            border = BorderStroke(1.dp, VibrantBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { showRoleMenu = true }
                                .testTag("mode_toggle_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = activeRoleIcon,
                                    contentDescription = null,
                                    tint = VibrantPink,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = activeRoleLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantTextPrimary
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = VibrantTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Quick lock button into dedicated mode
                        IconButton(
                            onClick = onOpenDeviceSetup,
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("lock_device_mode_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Khóa vai trò máy",
                                tint = VibrantPinkDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                if (showRoleMenu) {
                    RoleSelectionDialog(
                        currentMode = appMode,
                        onDismiss = { showRoleMenu = false },
                        onSelectMode = { newMode ->
                            showRoleMenu = false
                            onSelectAppMode(newMode)
                        },
                        onOpenDeviceSetup = {
                            showRoleMenu = false
                            onOpenDeviceSetup()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RoleSelectionDialog(
    currentMode: String,
    onDismiss: () -> Unit,
    onSelectMode: (String) -> Unit,
    onOpenDeviceSetup: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Chuyển Đổi Phân Hệ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = VibrantTextPrimary
                        )
                        Text(
                            text = "Chọn giao diện trải nghiệm quán kem",
                            fontSize = 12.sp,
                            color = VibrantTextSecondary
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = VibrantBg,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable { onDismiss() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "✕",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Role 1: Customer
                RoleOptionItem(
                    title = "Khách Hàng (Customer)",
                    subtitle = "Xem thực đơn, tùy chọn topping, giỏ hàng, thanh toán VietQR & minigame",
                    icon = Icons.Default.Dining,
                    iconTint = VibrantPink,
                    iconBg = VibrantPinkLight,
                    isSelected = currentMode == "CUSTOMER",
                    onClick = { onSelectMode("CUSTOMER") }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Role 2: Staff & Kitchen
                RoleOptionItem(
                    title = "Nhân Viên (Staff & Bếp)",
                    subtitle = "Sơ đồ bàn, tạo đơn POS tại quầy, KDS điều phối bếp & nhận chuông phục vụ",
                    icon = Icons.Default.Kitchen,
                    iconTint = VibrantMint,
                    iconBg = VibrantGreenBg,
                    isSelected = currentMode == "STAFF" || currentMode == "KITCHEN",
                    onClick = { onSelectMode("STAFF") }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Role 3: Manager
                RoleOptionItem(
                    title = "Quản Lý (Manager)",
                    subtitle = "Báo cáo doanh thu ca, quản lý món & giá bán, voucher khuyến mãi, CRM VIP",
                    icon = Icons.Default.SupervisorAccount,
                    iconTint = VibrantPinkDark,
                    iconBg = VibrantPinkLight,
                    isSelected = currentMode == "MANAGER",
                    onClick = { onSelectMode("MANAGER") }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Option to lock into Dedicated Device Mode (Cách 1)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = VibrantPinkLight.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, VibrantPink.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onOpenDeviceSetup() }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.TabletAndroid,
                            contentDescription = null,
                            tint = VibrantPink,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Mô Hình Chuyên Biệt (Cách 1)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantPinkDark
                            )
                            Text(
                                "Khóa máy này vào Bàn Khách hoặc Quầy Thu Ngân",
                                fontSize = 10.sp,
                                color = VibrantTextSecondary
                            )
                        }
                        Text("Cài đặt →", fontSize = 11.sp, fontWeight = FontWeight.Black, color = VibrantPink)
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceRoleSetupDialog(
    currentRole: String,
    currentTable: String,
    onDismiss: () -> Unit,
    onConfirm: (newRole: String, defaultTable: String?) -> Unit
) {
    var selectedRole by remember { mutableStateOf(currentRole) }
    var selectedTbl by remember { mutableStateOf(currentTable) }
    val allTables = (1..16).map { String.format("Bàn %02d", it) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, VibrantPink.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp)
                .testTag("device_role_setup_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "⚙️ THIẾT LẬP THIẾT BỊ NÀY",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = VibrantTextPrimary
                        )
                        Text(
                            text = "Mô hình thiết bị chuyên biệt (Cách 1)",
                            fontSize = 11.sp,
                            color = VibrantTextSecondary
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF0F0F0),
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable { onDismiss() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("✕", fontSize = 12.sp, color = VibrantTextSecondary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Chỉ định chức năng cho máy này trong cửa hàng:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = VibrantTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val roles = listOf(
                    Triple(
                        "CUSTOMER_TABLE",
                        "📱 Máy Bàn Khách (Khuyến nghị)",
                        "Khách chỉ thấy menu, đặt món & thanh toán. ẨN HOÀN TOÀN thanh chọn vai trò!"
                    ),
                    Triple(
                        "STAFF_POS",
                        "🛎️ Máy Trạm POS / Bếp",
                        "Cố định giao diện nhân viên nhận đơn bàn, làm món và thanh toán tiền mặt."
                    ),
                    Triple(
                        "MANAGER_PORTAL",
                        "👑 Máy Quản Lý / Chủ Quán",
                        "Toàn quyền xem báo cáo tài chính, kho hàng COGS, phân ca và cài đặt."
                    ),
                    Triple(
                        "DEMO_MULTI_ROLE",
                        "🧪 Chế Độ Thử Nghiệm",
                        "Hiện lại thanh chọn vai trò ở trên cùng để bạn demo nhanh cho khách xem."
                    )
                )

                roles.forEach { (roleKey, title, desc) ->
                    val isSelected = selectedRole == roleKey
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) VibrantPinkLight.copy(alpha = 0.5f) else Color(0xFFF8F9FA),
                        border = BorderStroke(1.5.dp, if (isSelected) VibrantPink else VibrantBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedRole = roleKey }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) VibrantPink else Color(0xFFDCDCDC),
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
                                    fontSize = 11.sp,
                                    color = VibrantTextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                if (selectedRole == "CUSTOMER_TABLE") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Chọn số bàn cố định đặt máy này:",
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
                            val isSel = selectedTbl == tbl
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) VibrantPink else Color(0xFFF1F2F4),
                                border = BorderStroke(1.dp, if (isSel) VibrantPink else VibrantBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedTbl = tbl }
                            ) {
                                Text(
                                    text = tbl,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Black else FontWeight.Medium,
                                    color = if (isSel) Color.White else VibrantTextPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Hủy", color = VibrantTextSecondary)
                    }

                    androidx.compose.material3.Button(
                        onClick = {
                            onConfirm(selectedRole, if (selectedRole == "CUSTOMER_TABLE") selectedTbl else null)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = VibrantPink
                        ),
                        modifier = Modifier.weight(1.5f).testTag("confirm_device_role_button")
                    ) {
                        Text("Áp Dụng Cho Máy", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleOptionItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBg: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) VibrantBg else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) iconTint else VibrantBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = iconBg,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isSelected) iconTint else VibrantTextPrimary
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = iconTint
                        ) {
                            Text(
                                text = "Đang chọn",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = VibrantTextMuted,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(
    currentTab: String,
    onSelectTab: (String) -> Unit,
    cartCount: Int,
    activeOrderStatus: String?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
        shadowElevation = 4.dp
    ) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            // Tab 1: Menu
            NavigationBarItem(
                selected = currentTab == "MENU",
                onClick = { onSelectTab("MENU") },
                icon = {
                    if (cartCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = VibrantPink) {
                                    Text("$cartCount")
                                }
                            }
                        ) {
                            Icon(Icons.Default.RestaurantMenu, contentDescription = "Thực Đơn")
                        }
                    } else {
                        Icon(Icons.Default.RestaurantMenu, contentDescription = "Thực Đơn")
                    }
                },
                label = { Text("Thực Đơn", fontSize = 11.sp, fontWeight = if (currentTab == "MENU") FontWeight.Bold else FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VibrantPink,
                    selectedTextColor = VibrantPink,
                    unselectedIconColor = VibrantTextMuted,
                    unselectedTextColor = VibrantTextMuted,
                    indicatorColor = VibrantPinkLight
                ),
                modifier = Modifier.testTag("nav_menu_tab")
            )

            // Tab 2: Orders Tracking
            NavigationBarItem(
                selected = currentTab == "ORDERS",
                onClick = { onSelectTab("ORDERS") },
                icon = {
                    if (activeOrderStatus != null && activeOrderStatus != "COMPLETED") {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = VibrantMint) {
                                    Text("!")
                                }
                            }
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = "Đơn Hàng")
                        }
                    } else {
                        Icon(Icons.Default.ReceiptLong, contentDescription = "Đơn Hàng")
                    }
                },
                label = { Text("Đơn Hàng", fontSize = 11.sp, fontWeight = if (currentTab == "ORDERS") FontWeight.Bold else FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VibrantPink,
                    selectedTextColor = VibrantPink,
                    unselectedIconColor = VibrantTextMuted,
                    unselectedTextColor = VibrantTextMuted,
                    indicatorColor = VibrantPinkLight
                ),
                modifier = Modifier.testTag("nav_orders_tab")
            )

            // Tab 3: Minigame
            NavigationBarItem(
                selected = currentTab == "MINIGAME",
                onClick = { onSelectTab("MINIGAME") },
                icon = {
                    Icon(Icons.Default.SportsEsports, contentDescription = "Hứng Kem")
                },
                label = { Text("Hứng Kem", fontSize = 11.sp, fontWeight = if (currentTab == "MINIGAME") FontWeight.Bold else FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VibrantPink,
                    selectedTextColor = VibrantPink,
                    unselectedIconColor = VibrantTextMuted,
                    unselectedTextColor = VibrantTextMuted,
                    indicatorColor = VibrantPinkLight
                ),
                modifier = Modifier.testTag("nav_minigame_tab")
            )

            // Tab 4: Loyalty & Membership
            NavigationBarItem(
                selected = currentTab == "LOYALTY",
                onClick = { onSelectTab("LOYALTY") },
                icon = {
                    Icon(Icons.Default.Stars, contentDescription = "Tích Điểm")
                },
                label = { Text("Tích Điểm", fontSize = 11.sp, fontWeight = if (currentTab == "LOYALTY") FontWeight.Bold else FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VibrantPink,
                    selectedTextColor = VibrantPink,
                    unselectedIconColor = VibrantTextMuted,
                    unselectedTextColor = VibrantTextMuted,
                    indicatorColor = VibrantPinkLight
                ),
                modifier = Modifier.testTag("nav_loyalty_tab")
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
