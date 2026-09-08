package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.CustomerEntity
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {
    @Query("SELECT * FROM menu_items ORDER BY id ASC")
    fun getAllMenuItems(): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items WHERE category = :category")
    fun getMenuItemsByCategory(category: String): Flow<List<MenuItemEntity>>

    @Query("SELECT COUNT(*) FROM menu_items")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MenuItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: MenuItemEntity)

    @androidx.room.Update
    suspend fun updateItem(item: MenuItemEntity)

    @androidx.room.Delete
    suspend fun deleteItem(item: MenuItemEntity)

    @Query("UPDATE menu_items SET isAvailable = :isAvailable WHERE id = :itemId")
    suspend fun updateAvailability(itemId: String, isAvailable: Boolean)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM table_orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM table_orders WHERE tableNumber = :tableNumber ORDER BY createdAt DESC")
    fun getOrdersByTable(tableNumber: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM table_orders WHERE orderId = :orderId")
    fun getOrderById(orderId: String): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("UPDATE table_orders SET status = :status WHERE orderId = :orderId")
    suspend fun updateStatus(orderId: String, status: String)

    @Query("UPDATE table_orders SET tableNumber = :newTable WHERE orderId = :orderId")
    suspend fun updateTableNumber(orderId: String, newTable: String)

    @Query("UPDATE table_orders SET customerPhone = :phone, earnedPoints = :points WHERE orderId = :orderId")
    suspend fun updateCustomerLoyalty(orderId: String, phone: String, points: Int)
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers WHERE phone = :phone")
    fun getCustomer(phone: String): Flow<CustomerEntity?>

    @Query("SELECT * FROM customers WHERE phone = :phone LIMIT 1")
    suspend fun findCustomerByPhone(phone: String): CustomerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(customer: CustomerEntity)

    @Query("SELECT * FROM customers ORDER BY points DESC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>
}
