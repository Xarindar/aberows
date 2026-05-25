package com.aberows.core.simulation

import com.aberows.core.model.ArrowState
import com.aberows.core.model.Board

data class GameState(
    val board: Board,
    val collisionsUsed: Int = 0,
    val isGameOver: Boolean = false,
) {
    val isWon: Boolean
        get() = !isGameOver && board.arrows.all {
            it.state == ArrowState.CLEARED || it.state == ArrowState.CRASHED
        }
    val isFailed: Boolean
        get() = isGameOver
}
