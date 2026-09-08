package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String, // "Kem viên", "Topping", "Đồ uống", "Combo Gen Z"
    val price: Long,
    val description: String,
    val emoji: String,
    val colorHex: Long,
    val tag: String = "", // "Bestseller", "Mới", "Hot", "Ít ngọt"
    val isAvailable: Boolean = true
)

data class OrderItemDto(
    val menuItemId: String,
    val menuItemName: String,
    val category: String,
    val emoji: String,
    val quantity: Int,
    val unitPrice: Long,
    val selectedToppings: List<String> = emptyList(),
    val note: String = "",
    val totalItemPrice: Long
)

@Entity(tableName = "table_orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val tableNumber: String,
    val itemsSummary: String,
    val itemsJson: String,
    val totalAmount: Long,
    val discountAmount: Long = 0L,
    val finalAmount: Long,
    val note: String = "",
    val status: String = "RECEIVED", // "RECEIVED", "PREPARING", "SERVING", "COMPLETED"
    val customerPhone: String? = null,
    val earnedPoints: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val phone: String,
    val name: String,
    val points: Int = 0,
    val tier: String = "Thành viên Mới", // "Thành viên Mới", "Kem Bạc", "Kem Vàng", "VIP Kim Cương"
    val totalSpent: Long = 0L,
    val orderCount: Int = 0,
    val minigameHighScore: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)
