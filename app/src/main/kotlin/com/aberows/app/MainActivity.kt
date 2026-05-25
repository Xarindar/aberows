package com.aberows.app

import android.graphics.Paint as AndroidPaint
import android.graphics.Typeface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.aberows.core.model.Arrow
import com.aberows.core.model.ArrowState
import com.aberows.core.model.Board
import com.aberows.core.model.Heading
import com.aberows.core.simulation.GameState
import com.aberows.core.simulation.SimEngine
import com.aberows.core.simulation.activate
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AberowsApp()
                }
            }
        }
    }
}

@Composable
private fun AberowsApp() {
    val engine = remember { SimEngine() }
    var gameState by remember { mutableStateOf(sampleGameState()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(200)

            val hasActiveArrows = gameState.board.arrows.any { it.state == ArrowState.ACTIVE }
            if (!gameState.isWon && !gameState.isFailed && hasActiveArrows) {
                gameState = engine.tick(gameState)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF10141C))
            .systemBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Aberows Graybox",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Collisions: ${gameState.collisionsUsed}/3",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
            )

            Button(onClick = { gameState = sampleGameState() }) {
                Text("Reset")
            }
        }

        Text(
            text = when {
                gameState.isFailed -> "Game over"
                gameState.isWon -> "Board resolved"
                else -> "Tap arrows to activate them"
            },
            color = Color(0xFFD1D5DB),
            style = MaterialTheme.typography.bodyLarge,
        )

        BoardScreen(
            gameState = gameState,
            onArrowTapped = { arrowId ->
                gameState = activate(gameState, arrowId)
            },
        )
    }
}

@Composable
private fun BoardScreen(
    gameState: GameState,
    onArrowTapped: (Int) -> Unit,
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val textPaint = remember {
        AndroidPaint().apply {
            isAntiAlias = true
            textAlign = AndroidPaint.Align.CENTER
            color = android.graphics.Color.WHITE
            typeface = Typeface.MONOSPACE
        }
    }

    val board = gameState.board

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(board.cols.toFloat() / board.rows.toFloat())
            .background(Color(0xFF0B1020)),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { canvasSize = it }
                .pointerInput(board, canvasSize) {
                    detectTapGestures { offset ->
                        val size = canvasSize
                        if (size.width == 0 || size.height == 0) {
                            return@detectTapGestures
                        }

                        val cellWidth = size.width.toFloat() / board.cols
                        val cellHeight = size.height.toFloat() / board.rows
                        val tappedCol = (offset.x / cellWidth).toInt()
                        val tappedRow = (offset.y / cellHeight).toInt()

                        val tappedArrow = board.arrows.firstOrNull {
                            it.col == tappedCol && it.row == tappedRow
                        }

                        if (tappedArrow != null) {
                            onArrowTapped(tappedArrow.id)
                        }
                    }
                },
        ) {
            val cellWidth = size.width / board.cols
            val cellHeight = size.height / board.rows
            textPaint.textSize = minOf(cellWidth, cellHeight) * 0.42f

            for (row in 0 until board.rows) {
                for (col in 0 until board.cols) {
                    val topLeft = Offset(col * cellWidth, row * cellHeight)
                    drawRect(
                        color = Color(0xFF172033),
                        topLeft = topLeft,
                        size = Size(cellWidth, cellHeight),
                    )
                    drawRect(
                        color = Color(0xFF263244),
                        topLeft = topLeft,
                        size = Size(cellWidth, cellHeight),
                        style = Stroke(width = 2f),
                    )
                }
            }

            board.arrows.forEach { arrow ->
                val topLeft = Offset(arrow.col * cellWidth, arrow.row * cellHeight)
                val color = arrowColor(arrow.state)

                if (color.alpha > 0f) {
                    drawRect(
                        color = color,
                        topLeft = topLeft,
                        size = Size(cellWidth, cellHeight),
                    )
                }

                if (arrow.state != ArrowState.CLEARED) {
                    drawContext.canvas.nativeCanvas.drawText(
                        headingLabel(arrow.heading),
                        topLeft.x + (cellWidth / 2f),
                        topLeft.y + (cellHeight / 2f) - ((textPaint.ascent() + textPaint.descent()) / 2f),
                        textPaint,
                    )
                }
            }
        }

        Text(
            text = "Idle=gray  Active=blue  Crashed=red  Cleared=transparent",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Color(0xCC0B1020))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color(0xFFD1D5DB),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

private fun arrowColor(state: ArrowState): Color = when (state) {
    ArrowState.IDLE -> Color(0xFF6B7280)
    ArrowState.ACTIVE -> Color(0xFF2563EB)
    ArrowState.CRASHED -> Color(0xFFDC2626)
    ArrowState.CLEARED -> Color.Transparent
}

private fun headingLabel(heading: Heading): String = when (heading) {
    Heading.UP -> "U"
    Heading.RIGHT -> "R"
    Heading.DOWN -> "D"
    Heading.LEFT -> "L"
}

private fun sampleGameState(): GameState = GameState(
    board = Board(
        cols = 5,
        rows = 5,
        arrows = listOf(
            Arrow(id = 1, col = 0, row = 1, heading = Heading.RIGHT),
            Arrow(id = 2, col = 4, row = 1, heading = Heading.LEFT),
            Arrow(id = 3, col = 2, row = 4, heading = Heading.UP),
            Arrow(id = 4, col = 1, row = 3, heading = Heading.UP),
            Arrow(id = 5, col = 4, row = 3, heading = Heading.LEFT),
            Arrow(id = 6, col = 0, row = 4, heading = Heading.UP),
        ),
    ),
)
