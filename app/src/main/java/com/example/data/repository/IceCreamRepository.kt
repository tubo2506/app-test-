package com.example.data.repository

import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.model.CustomerEntity
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class IceCreamRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val menuDao = database.menuDao()
    private val orderDao = database.orderDao()
    private val customerDao = database.customerDao()
    private val appScope = CoroutineScope(Dispatchers.IO)

    val allMenuItems: Flow<List<MenuItemEntity>> = menuDao.getAllMenuItems()
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()
    val allCustomers: Flow<List<CustomerEntity>> = customerDao.getAllCustomers()

    fun getOrdersForTable(tableNumber: String): Flow<List<OrderEntity>> {
        return orderDao.getOrdersByTable(tableNumber)
    }

    fun getOrderById(orderId: String): Flow<OrderEntity?> {
        return orderDao.getOrderById(orderId)
    }

    fun getCustomer(phone: String): Flow<CustomerEntity?> {
        return customerDao.getCustomer(phone)
    }

    suspend fun initDefaultMenuIfNeeded(forceReset: Boolean = false) {
        val count = menuDao.getCount()
        if (count == 0 || forceReset) {
            val sampleItems = listOf(
                // Kem viên
                MenuItemEntity(
                    id = "KEM_01",
                    name = "Kem Dâu Hokkaido",
                    category = "Kem viên",
                    price = 35000,
                    description = "Dâu tây tươi ngọt thanh kết hợp sữa béo Hokkaido mịn màng",
                    emoji = "🍓",
                    colorHex = 0xFFFF5388,
                    tag = "Bestseller"
                ),
                MenuItemEntity(
                    id = "KEM_02",
                    name = "Kem Matcha Uji Kyoto",
                    category = "Kem viên",
                    price = 39000,
                    description = "Trà xanh Uji chuẩn Nhật đậm vị trà, hậu ngọt thanh dịu",
                    emoji = "🍵",
                    colorHex = 0xFF4CAF50,
                    tag = "Bestseller"
                ),
                MenuItemEntity(
                    id = "KEM_03",
                    name = "Kem Socola Bỉ 70%",
                    category = "Kem viên",
                    price = 38000,
                    description = "Cacao Bỉ nguyên chất đậm đặc đắng nhẹ quyến rũ",
                    emoji = "🍫",
                    colorHex = 0xFF6D4C41,
                    tag = "Hot"
                ),
                MenuItemEntity(
                    id = "KEM_04",
                    name = "Kem Vani Hạt Madagascar",
                    category = "Kem viên",
                    price = 32000,
                    description = "Hạt vani Madagascar tự nhiên, thơm nồng ngọt thanh tinh tế",
                    emoji = "🍨",
                    colorHex = 0xFFFFD54F,
                    tag = ""
                ),
                MenuItemEntity(
                    id = "KEM_05",
                    name = "Kem Xoài Cát Chu",
                    category = "Kem viên",
                    price = 35000,
                    description = "Xoài chín cây miền Tây thơm ngào ngạt, chua ngọt sảng khoái",
                    emoji = "🥭",
                    colorHex = 0xFFFFB300,
                    tag = "Mới"
                ),
                MenuItemEntity(
                    id = "KEM_06",
                    name = "Kem Bơ Sáp Dừa Non",
                    category = "Kem viên",
                    price = 38000,
                    description = "Bơ sáp Đắk Lắk bùi dẻo béo ngậy phối cơm dừa non sần sật",
                    emoji = "🥑",
                    colorHex = 0xFF689F38,
                    tag = "Hot"
                ),
                MenuItemEntity(
                    id = "KEM_07",
                    name = "Kem Việt Quất Sữa Chua",
                    category = "Kem viên",
                    price = 39000,
                    description = "Việt quất mọng nước hòa cùng sữa chua Hy Lạp lên men mát lạnh",
                    emoji = "🫐",
                    colorHex = 0xFF7E57C2,
                    tag = "Ít ngọt"
                ),
                MenuItemEntity(
                    id = "KEM_08",
                    name = "Kem Sầu Riêng Ri6",
                    category = "Kem viên",
                    price = 45000,
                    description = "100% sầu riêng tươi Ri6 béo ngậy nồng nàn mê đắm",
                    emoji = "👑",
                    colorHex = 0xFFFBC02D,
                    tag = "Đặc biệt"
                ),

                // Topping
                MenuItemEntity(
                    id = "TOP_01",
                    name = "Trân Châu Hoàng Kim",
                    category = "Topping",
                    price = 8000,
                    description = "Nấu cùng đường nâu mật mía, dẻo dai thơm lừng",
                    emoji = "🧋",
                    colorHex = 0xFFFFA000,
                    tag = "Must try"
                ),
                MenuItemEntity(
                    id = "TOP_02",
                    name = "Marshmallow Nướng Xém",
                    category = "Topping",
                    price = 10000,
                    description = "Kẹo xốp khò lửa thơm lừng, xốp mềm ngọt ngào chuẩn Gen Z",
                    emoji = "🍡",
                    colorHex = 0xFFFF80AB,
                    tag = "Gen Z Hot"
                ),
                MenuItemEntity(
                    id = "TOP_03",
                    name = "Bánh Quế Bơ Giòn Rụm",
                    category = "Topping",
                    price = 6000,
                    description = "Ống bánh quế thủ công nướng giòn tan béo ngậy",
                    emoji = "🧇",
                    colorHex = 0xFFD7CCC8,
                    tag = ""
                ),
                MenuItemEntity(
                    id = "TOP_04",
                    name = "Sốt Socola Ấm Nóng",
                    category = "Topping",
                    price = 8000,
                    description = "Sốt socola đen nguyên chất ấm nóng tưới lên kem lạnh",
                    emoji = "🍫",
                    colorHex = 0xFF4E342E,
                    tag = ""
                ),
                MenuItemEntity(
                    id = "TOP_05",
                    name = "Sốt Dâu Tây Nấu Chậm",
                    category = "Topping",
                    price = 8000,
                    description = "Dâu tây tươi sên mứt chua ngọt đậm đà có hạt",
                    emoji = "🍓",
                    colorHex = 0xFFE91E63,
                    tag = ""
                ),
                MenuItemEntity(
                    id = "TOP_06",
                    name = "Vụn Bánh Cookie Oreo",
                    category = "Topping",
                    price = 7000,
                    description = "Vụn Oreo đen giòn giòn nhai cực đã miệng",
                    emoji = "🍪",
                    colorHex = 0xFF37474F,
                    tag = ""
                ),
                MenuItemEntity(
                    id = "TOP_07",
                    name = "Hạnh Nhân Lát Rang Bơ",
                    category = "Topping",
                    price = 9000,
                    description = "Lát hạnh nhân sấy bơ vàng ruộm bùi béo",
                    emoji = "🥜",
                    colorHex = 0xFFFFCC80,
                    tag = ""
                ),

                // Đồ uống & Tráng miệng
                MenuItemEntity(
                    id = "DRK_01",
                    name = "Float Kem Soda Dâu Tuyết",
                    category = "Đồ uống",
                    price = 42000,
                    description = "Soda dâu sủi bọt mát lạnh thả 1 viên kem dâu béo ngậy bồng bềnh",
                    emoji = "🥤",
                    colorHex = 0xFFFF4081,
                    tag = "Bestseller"
                ),
                MenuItemEntity(
                    id = "DRK_02",
                    name = "Float Trà Matcha Kem Cheese",
                    category = "Đồ uống",
                    price = 45000,
                    description = "Matcha thơm đậm, bọt kem mặn macchiato và viên kem béo",
                    emoji = "🍵",
                    colorHex = 0xFF2E7D32,
                    tag = "Hot"
                ),
                MenuItemEntity(
                    id = "DRK_03",
                    name = "Bingsu Xoài Kem Tuyết",
                    category = "Đồ uống",
                    price = 59000,
                    description = "Đá bào tuyết sữa béo ngập tràn sốt xoài tươi và kem xoài",
                    emoji = "🍧",
                    colorHex = 0xFFFFB300,
                    tag = "Đặc biệt"
                ),
                MenuItemEntity(
                    id = "DRK_04",
                    name = "Cà Phê Cốt Dừa Kem Vani",
                    category = "Đồ uống",
                    price = 42000,
                    description = "Espresso đậm đà kết hợp cốt dừa tuyết và kem vani béo ngậy",
                    emoji = "☕",
                    colorHex = 0xFF5D4037,
                    tag = ""
                ),
                MenuItemEntity(
                    id = "DRK_05",
                    name = "Trà Đào Cam Sả Kem Tươi",
                    category = "Đồ uống",
                    price = 45000,
                    description = "Trà đào thơm ngát cam sả tươi phủ bông kem tươi dịu ngọt",
                    emoji = "🍹",
                    colorHex = 0xFFFF9800,
                    tag = "Mới"
                )
            )
            menuDao.insertAll(sampleItems)

            // Add a sample VIP customer for instant demonstration
            customerDao.insertOrUpdate(
                CustomerEntity(
                    phone = "0987654321",
                    name = "Bạn Kem Gen Z",
                    points = 85,
                    tier = "Kem Vàng",
                    totalSpent = 850000L,
                    orderCount = 5,
                    minigameHighScore = 120
                )
            )
        }
    }

    suspend fun createOrder(
        tableNumber: String,
        items: List<OrderItemDto>,
        note: String,
        discountAmount: Long = 0L,
        customerPhone: String? = null
    ): OrderEntity {
        val totalAmount = items.sumOf { it.totalItemPrice }
        val finalAmount = (totalAmount - discountAmount).coerceAtLeast(0L)
        val calculatedPoints = (finalAmount / 10000).toInt()

        val summary = items.joinToString(", ") { "${it.quantity}x ${it.menuItemName}" }
        // Simple JSON representation for items
        val json = buildSimpleItemsJson(items)
        val shortId = (1000..9999).random().toString()
        val orderId = "#KEM-$shortId"

        val order = OrderEntity(
            orderId = orderId,
            tableNumber = tableNumber,
            itemsSummary = summary,
            itemsJson = json,
            totalAmount = totalAmount,
            discountAmount = discountAmount,
            finalAmount = finalAmount,
            note = note,
            status = "RECEIVED",
            customerPhone = customerPhone,
            earnedPoints = calculatedPoints,
            createdAt = System.currentTimeMillis()
        )

        orderDao.insertOrder(order)

        // Launch real-time simulation background progression
        simulateKitchenOrderProgression(orderId)

        // If customer phone is already provided, update points
        if (!customerPhone.isNullOrBlank()) {
            addCustomerPoints(customerPhone, calculatedPoints, finalAmount)
        }

        return order
    }

    suspend fun addCustomerPoints(phone: String, pointsToAdd: Int, spentAmount: Long) {
        val existing = customerDao.findCustomerByPhone(phone)
        val currentPoints = existing?.points ?: 0
        val newPoints = currentPoints + pointsToAdd
        val currentSpent = existing?.totalSpent ?: 0L
        val newSpent = currentSpent + spentAmount
        val orderCount = (existing?.orderCount ?: 0) + 1
        val tier = when {
            newPoints >= 200 -> "VIP Kim Cương 💎"
            newPoints >= 100 -> "Kem Vàng 🌟"
            newPoints >= 50 -> "Kem Bạc 🥈"
            else -> "Thành viên Mới 🌱"
        }
        val updated = CustomerEntity(
            phone = phone,
            name = existing?.name ?: "Khách $phone",
            points = newPoints,
            tier = tier,
            totalSpent = newSpent,
            orderCount = orderCount,
            minigameHighScore = existing?.minigameHighScore ?: 0,
            updatedAt = System.currentTimeMillis()
        )
        customerDao.insertOrUpdate(updated)
    }

    private fun simulateKitchenOrderProgression(orderId: String) {
        appScope.launch {
            // Stage 1: "RECEIVED" -> After 7 seconds move to "PREPARING"
            delay(7000)
            orderDao.updateStatus(orderId, "PREPARING")

            // Stage 2: "PREPARING" -> After 10 seconds move to "SERVING"
            delay(10000)
            orderDao.updateStatus(orderId, "SERVING")

            // Stage 3: "SERVING" -> After 8 seconds move to "COMPLETED"
            delay(8000)
            orderDao.updateStatus(orderId, "COMPLETED")
        }
    }

    suspend fun updateOrderStatusManual(orderId: String, nextStatus: String) {
        orderDao.updateStatus(orderId, nextStatus)
    }

    suspend fun claimPointsForOrder(orderId: String, phone: String, minigameBonus: Int): CustomerEntity {
        val order = orderDao.getOrderById(orderId)
        val basePoints = ((order?.collectFirst()?.finalAmount ?: 35000L) / 10000).toInt()
        val totalPointsToAdd = basePoints + minigameBonus

        val existingCustomer = customerDao.findCustomerByPhone(phone)
        val currentPoints = existingCustomer?.points ?: 0
        val currentSpent = existingCustomer?.totalSpent ?: 0L
        val orderCount = (existingCustomer?.orderCount ?: 0) + 1
        val newPoints = currentPoints + totalPointsToAdd
        val orderFinalAmount = order?.collectFirst()?.finalAmount ?: 35000L
        val newSpent = currentSpent + orderFinalAmount

        val tier = when {
            newPoints >= 200 -> "VIP Kim Cương 💎"
            newPoints >= 100 -> "Kem Vàng 🌟"
            newPoints >= 50 -> "Kem Bạc 🥈"
            else -> "Thành viên Mới 🌱"
        }

        val updated = CustomerEntity(
            phone = phone,
            name = existingCustomer?.name ?: "Khách $phone",
            points = newPoints,
            tier = tier,
            totalSpent = newSpent,
            orderCount = orderCount,
            minigameHighScore = maxOf(existingCustomer?.minigameHighScore ?: 0, minigameBonus * 5),
            updatedAt = System.currentTimeMillis()
        )

        customerDao.insertOrUpdate(updated)
        orderDao.updateCustomerLoyalty(orderId, phone, totalPointsToAdd)
        return updated
    }

    suspend fun saveMinigameScore(phone: String, score: Int, bonusPoints: Int): CustomerEntity? {
        val existing = customerDao.findCustomerByPhone(phone) ?: return null
        val updated = existing.copy(
            points = existing.points + bonusPoints,
            minigameHighScore = maxOf(existing.minigameHighScore, score),
            updatedAt = System.currentTimeMillis()
        )
        customerDao.insertOrUpdate(updated)
        return updated
    }

    suspend fun getCustomerByPhone(phone: String): CustomerEntity? {
        return customerDao.findCustomerByPhone(phone)
    }

    suspend fun findCustomer(phone: String): CustomerEntity? {
        return customerDao.findCustomerByPhone(phone)
    }

    suspend fun saveCustomer(customer: CustomerEntity) {
        customerDao.insertOrUpdate(customer)
    }

    suspend fun addLoyaltyPoints(phone: String, pointsToAdd: Int): CustomerEntity? {
        val existing = customerDao.findCustomerByPhone(phone) ?: return null
        val updated = existing.copy(
            points = existing.points + pointsToAdd,
            updatedAt = System.currentTimeMillis()
        )
        customerDao.insertOrUpdate(updated)
        return updated
    }

    suspend fun checkInCustomer(phone: String): CustomerEntity {
        val existing = customerDao.findCustomerByPhone(phone)
        if (existing != null) return existing

        val newCustomer = CustomerEntity(
            phone = phone,
            name = "Khách $phone",
            points = 20, // Welcome points
            tier = "Thành viên Mới 🌱",
            totalSpent = 0L,
            orderCount = 0,
            minigameHighScore = 0,
            updatedAt = System.currentTimeMillis()
        )
        customerDao.insertOrUpdate(newCustomer)
        return newCustomer
    }

    suspend fun updateItemAvailability(itemId: String, isAvailable: Boolean) {
        menuDao.updateAvailability(itemId, isAvailable)
    }

    suspend fun saveMenuItem(item: MenuItemEntity) {
        menuDao.insertItem(item)
    }

    suspend fun updateMenuItem(item: MenuItemEntity) {
        menuDao.updateItem(item)
    }

    suspend fun deleteMenuItem(item: MenuItemEntity) {
        menuDao.deleteItem(item)
    }

    suspend fun switchOrderTable(orderId: String, newTable: String) {
        orderDao.updateTableNumber(orderId, newTable)
    }

    private fun buildSimpleItemsJson(items: List<OrderItemDto>): String {
        val sb = StringBuilder()
        sb.append("[")
        items.forEachIndexed { index, item ->
            val toppings = item.selectedToppings.joinToString(";")
            sb.append("{\"id\":\"${item.menuItemId}\",\"name\":\"${item.menuItemName}\",\"qty\":${item.quantity},\"price\":${item.unitPrice},\"toppings\":\"$toppings\",\"note\":\"${item.note}\",\"total\":${item.totalItemPrice}}")
            if (index < items.size - 1) sb.append(",")
        }
        sb.append("]")
        return sb.toString()
    }

    suspend fun insertRawOrder(order: OrderEntity) {
        orderDao.insertOrder(order)
    }
}

// Helper extension to grab first flow value without collecting continuously
private suspend fun <T> Flow<T>.collectFirst(): T? {
    var result: T? = null
    try {
        collect {
            result = it
            throw RuntimeException()
        }
    } catch (_: Exception) { }
    return result
}
