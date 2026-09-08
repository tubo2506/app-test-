package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.IceCreamBorder
import com.example.ui.theme.IceCreamCoral
import com.example.ui.theme.IceCreamLavender
import com.example.ui.theme.IceCreamMint
import com.example.ui.theme.IceCreamPink
import com.example.ui.theme.IceCreamPinkLight
import com.example.ui.theme.IceCreamTextPrimary
import com.example.ui.theme.IceCreamTextSecondary
import kotlinx.coroutines.delay
import kotlin.random.Random

data class FallingScoop(
    val id: Long = Random.nextLong(),
    var xPercent: Float, // 0.1f to 0.9f
    var yPercent: Float = 0f,
    val type: ScoopType,
    val speed: Float
)

enum class ScoopType(
    val emoji: String,
    val points: Int,
    val color: Color,
    val isHazard: Boolean = false
) {
    STRAWBERRY("🍓", 10, Color(0xFFFF4081)),
    MATCHA("🍵", 10, Color(0xFF4CAF50)),
    CHOCOLATE("🍫", 10, Color(0xFF795548)),
    MANGO("🥭", 10, Color(0xFFFFB300)),
    RAINBOW("⭐", 25, Color(0xFF9C27B0)),
    CHILI("🌶️", -10, Color(0xFFD32F2F), true)
}

@Composable
fun MinigameScreen(
    activeOrder: OrderEntity?,
    onClaimReward: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var gameScore by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(30) }
    var isRunning by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var comboCount by remember { mutableIntStateOf(0) }
    var comboBanner by remember { mutableStateOf("") }

    // Player cone horizontal position (0f to 1f)
    var coneXPercent by remember { mutableFloatStateOf(0.5f) }
    val fallingItems = remember { mutableStateListOf<FallingScoop>() }

    // Game loop
    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect

        // Spawn timer
        var spawnCounter = 0

        while (isRunning && timeLeft > 0) {
            delay(20) // ~50 fps
            spawnCounter += 20

            // Spawn new scoop every 600ms
            if (spawnCounter >= 600) {
                spawnCounter = 0
                val randomType = when (Random.nextInt(100)) {
                    in 0..24 -> ScoopType.STRAWBERRY
                    in 25..49 -> ScoopType.MATCHA
                    in 50..69 -> ScoopType.CHOCOLATE
                    in 70..84 -> ScoopType.MANGO
                    in 85..93 -> ScoopType.RAINBOW
                    else -> ScoopType.CHILI
                }
                fallingItems.add(
                    FallingScoop(
                        xPercent = Random.nextFloat() * 0.76f + 0.12f,
                        yPercent = 0.05f,
                        type = randomType,
                        speed = Random.nextFloat() * 0.008f + 0.009f
                    )
                )
            }

            // Update item positions and check collision
            val toRemove = mutableListOf<FallingScoop>()
            for (item in fallingItems) {
                item.yPercent += item.speed

                // Check collision near cone (cone is at yPercent ~ 0.85f)
                if (item.yPercent >= 0.80f && item.yPercent <= 0.88f) {
                    val distance = kotlin.math.abs(item.xPercent - coneXPercent)
                    if (distance < 0.14f) {
                        // Caught!
                        if (item.type.isHazard) {
                            gameScore = (gameScore + item.type.points).coerceAtLeast(0)
                            comboCount = 0
                            comboBanner = "Ối! Trúng ớt cay -10đ 💥"
                        } else {
                            val comboBonus = if (comboCount >= 3) 5 else 0
                            gameScore += item.type.points + comboBonus
                            comboCount++
                            comboBanner = if (comboCount >= 3) "Combo x${comboCount}! Siêu đỉnh 🔥" else "+${item.type.points}!"
                        }
                        toRemove.add(item)
                    }
                }

                // If fallen past bottom
                if (item.yPercent > 1.0f) {
                    if (!item.type.isHazard) {
                        comboCount = 0
                    }
                    toRemove.add(item)
                }
            }

            fallingItems.removeAll(toRemove)
        }

        if (timeLeft <= 0) {
            isRunning = false
            isGameOver = true
        }
    }

    // Countdown timer
    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect
        while (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF4F7),
                        Color(0xFFFFEEF3),
                        Color(0xFFFFF9E6)
                    )
                )
            )
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        Column(modifier = Modifier.fillMaxSize()) {
            // Live Order Status Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🍦 Đang làm kem cho ",
                                fontSize = 12.sp,
                                color = IceCreamTextSecondary
                            )
                            Text(
                                text = activeOrder?.tableNumber ?: "Bàn 03",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = IceCreamPink
                            )
                        }
                        Text(
                            text = activeOrder?.orderId ?: "#KEM-CHỜ-MÓN",
                            fontSize = 11.sp,
                            color = IceCreamTextSecondary
                        )
                    }

                    StatusBadge(status = activeOrder?.status ?: "PREPARING")
                }
            }

            // Game Status Header (Score, Time, Combo)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Score Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = IceCreamPink,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Điểm: ",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$gameScore",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Combo Banner
                if (comboBanner.isNotBlank()) {
                    Text(
                        text = comboBanner,
                        color = IceCreamCoral,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Timer Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (timeLeft <= 5) Color(0xFFD32F2F) else IceCreamMint,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏱️ ",
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${timeLeft}s",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Interactive Game Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pointerInput(isRunning) {
                        if (!isRunning) return@pointerInput
                        detectDragGestures { change, _ ->
                            change.consume()
                            val newX = change.position.x / widthPx
                            coneXPercent = newX.coerceIn(0.12f, 0.88f)
                        }
                    }
                    .pointerInput(isRunning) {
                        if (!isRunning) return@pointerInput
                        detectTapGestures { offset ->
                            val newX = offset.x / widthPx
                            coneXPercent = newX.coerceIn(0.12f, 0.88f)
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Draw falling scoops
                    for (scoop in fallingItems) {
                        val cx = scoop.xPercent * w
                        val cy = scoop.yPercent * h
                        val radius = 22.dp.toPx()

                        // Scoop shadow
                        drawCircle(
                            color = Color(0x22000000),
                            radius = radius * 1.1f,
                            center = Offset(cx, cy + 4.dp.toPx())
                        )

                        // Scoop body
                        drawCircle(
                            color = scoop.type.color,
                            radius = radius,
                            center = Offset(cx, cy)
                        )

                        // Highlight
                        drawCircle(
                            color = Color.White.copy(alpha = 0.4f),
                            radius = radius * 0.35f,
                            center = Offset(cx - radius * 0.3f, cy - radius * 0.3f)
                        )
                    }

                    // Draw Waffle Cone at bottom
                    val coneCenterX = coneXPercent * w
                    val coneY = h * 0.84f
                    val coneWidth = 72.dp.toPx()
                    val coneHeight = 65.dp.toPx()

                    // Cone shadow
                    drawOval(
                        color = Color(0x33000000),
                        topLeft = Offset(coneCenterX - coneWidth * 0.55f, coneY + coneHeight),
                        size = Size(coneWidth * 1.1f, 12.dp.toPx())
                    )

                    // Cone triangle
                    val conePath = Path().apply {
                        moveTo(coneCenterX - coneWidth / 2f, coneY)
                        lineTo(coneCenterX + coneWidth / 2f, coneY)
                        lineTo(coneCenterX, coneY + coneHeight)
                        close()
                    }
                    drawPath(path = conePath, color = Color(0xFFFFA726))

                    // Cone inner depth
                    val coneDetail = Path().apply {
                        moveTo(coneCenterX - coneWidth * 0.35f, coneY)
                        lineTo(coneCenterX + coneWidth * 0.35f, coneY)
                        lineTo(coneCenterX, coneY + coneHeight)
                        close()
                    }
                    drawPath(path = coneDetail, color = Color(0xFFFB8C00))

                    // Cone waffle rim
                    drawRoundRect(
                        color = Color(0xFFFFB74D),
                        topLeft = Offset(coneCenterX - coneWidth / 2f, coneY - 6.dp.toPx()),
                        size = Size(coneWidth, 10.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                    )
                }

                // Text labels overlay on scoops for emojis
                for (scoop in fallingItems) {
                    val density = LocalDensity.current
                    val leftDp = with(density) { (scoop.xPercent * widthPx - 16.dp.toPx()).toDp() }
                    val topDp = with(density) { (scoop.yPercent * heightPx - 16.dp.toPx()).toDp() }

                    Text(
                        text = scoop.type.emoji,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .padding(start = leftDp, top = topDp)
                    )
                }

                // Cone Kawaii Face label
                val density = LocalDensity.current
                val coneLeftDp = with(density) { (coneXPercent * widthPx - 18.dp.toPx()).toDp() }
                val coneTopDp = with(density) { (heightPx * 0.84f + 8.dp.toPx()).toDp() }

                Text(
                    text = "∪ω∪",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5D4037),
                    modifier = Modifier.padding(start = coneLeftDp, top = coneTopDp)
                )

                // Start Overlay when game not running yet
                if (!isRunning && !isGameOver) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f)),
                        color = Color.Transparent
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Card(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .testTag("minigame_start_card"),
                                shape = RoundedCornerShape(32.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, IceCreamBorder),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "🎮 Hứng Kem Gen Z 🍦",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IceCreamPink
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Vuốt màn hình để hứng các viên kem rơi vào ốc quế!\nNé ớt cay 🌶️ để không bị trừ điểm nha.",
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center,
                                        color = IceCreamTextSecondary,
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            gameScore = 0
                                            timeLeft = 30
                                            comboCount = 0
                                            fallingItems.clear()
                                            isRunning = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = IceCreamPink),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("start_game_button")
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Bắt đầu chơi ngay!", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Game Over Overlay
                if (isGameOver) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.45f)),
                        color = Color.Transparent
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Card(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .testTag("game_over_card"),
                                shape = RoundedCornerShape(32.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, IceCreamBorder),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "🎉 Hoàn Thành Xuất Sắc!",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = IceCreamPink
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Điểm hứng kem của bạn:",
                                        fontSize = 13.sp,
                                        color = IceCreamTextSecondary
                                    )
                                    Text(
                                        text = "$gameScore điểm",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Black,
                                        color = IceCreamCoral
                                    )

                                    val bonusPoints = (gameScore / 10).coerceAtLeast(5)
                                    Surface(
                                        color = IceCreamPinkLight,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = "🎁 Thưởng ngay +$bonusPoints điểm thành viên!",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = IceCreamPink,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = {
                                            onClaimReward(gameScore)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = IceCreamPink),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("claim_points_popup_button")
                                    ) {
                                        Icon(Icons.Default.CardGiftcard, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Nhập SĐT để Tích Điểm", fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedButton(
                                        onClick = {
                                            gameScore = 0
                                            timeLeft = 30
                                            comboCount = 0
                                            fallingItems.clear()
                                            isGameOver = false
                                            isRunning = true
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Chơi Lại")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick instruction footer
            Text(
                text = "💡 Di chuyển ngón tay để kéo ốc quế hứng kem rơi",
                fontSize = 12.sp,
                color = IceCreamTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, top = 4.dp)
            )
        }
    }
}
