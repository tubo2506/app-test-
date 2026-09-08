package com.example.data.model

data class StaffMember(
    val id: String,
    val name: String,
    val role: String, // "Trưởng ca", "Barista Gelato", "Thu ngân", "Phục vụ"
    val phone: String,
    val shiftStatus: String = "ACTIVE", // "ACTIVE" (Đang làm), "OFF" (Nghỉ ca), "BREAK" (Nghỉ giải lao)
    val ordersHandled: Int = 0,
    val avatarEmoji: String = "👩‍🍳",
    val joinedDate: String = "01/2026"
)

data class ShiftRecord(
    val shiftId: String,
    val shiftName: String, // "Ca Sáng (08:00 - 15:00)", "Ca Chiều Tối (15:00 - 22:30)"
    val startTime: Long,
    val endTime: Long,
    val staffName: String,
    val openingCash: Long,
    val cashRevenue: Long,
    val qrRevenue: Long,
    val totalRevenue: Long,
    val orderCount: Int,
    val isBalanced: Boolean = true,
    val notes: String = "Chốt ca đúng sổ quỹ"
)

data class PeakHourStat(
    val timeSlot: String,
    val label: String,
    val orderCount: Int,
    val revenue: Long,
    val iconEmoji: String
)

data class MenuItemMarginInfo(
    val itemId: String,
    val estimatedCost: Long, // Giá vốn ước tính
    val stockRemaining: Int = 30, // Tồn kho hiện tại trong ngày
    val safetyThreshold: Int = 10
)
