package com.aberows.core.simulation

import com.aberows.core.model.Arrow
import com.aberows.core.model.ArrowState
import com.aberows.core.model.Board

class SimEngine {
    fun tick(state: GameState): GameState {
        if (state.isGameOver) return state

        val prevPositions = state.board.arrows.associate { it.id to (it.col to it.row) }

        val movedArrows = state.board.arrows.map { arrow ->
            if (arrow.state != ArrowState.ACTIVE) arrow
            else moveArrow(state.board, arrow)
        }

        val activeAfterMove = movedArrows.filter { it.state == ArrowState.ACTIVE }
        val collidedIds = linkedSetOf<Int>()
        var collisionEvents = 0

        // Same-cell collisions: all arrows landing on the same cell in one tick = 1 event
        activeAfterMove
            .groupBy { it.col to it.row }
            .values
            .filter { it.size > 1 }
            .forEach { group ->
                collidedIds += group.map { it.id }
                collisionEvents += 1
            }

        // Swap/crossing collisions: arrows that pass through each other in one tick
        for (i in activeAfterMove.indices) {
            for (j in i + 1 until activeAfterMove.size) {
                val a = activeAfterMove[i]
                val b = activeAfterMove[j]
                val aPrev = prevPositions.getValue(a.id)
                val bPrev = prevPositions.getValue(b.id)
                if ((a.col to a.row) == bPrev && (b.col to b.row) == aPrev) {
                    if (a.id !in collidedIds && b.id !in collidedIds) {
                        collidedIds += a.id
                        collidedIds += b.id
                        collisionEvents += 1
                    }
                }
            }
        }

        val resolvedArrows = movedArrows.map { arrow ->
            if (arrow.id in collidedIds) arrow.copy(state = ArrowState.CRASHED) else arrow
        }

        val updatedCollisions = (state.collisionsUsed + collisionEvents).coerceAtMost(3)

        return GameState(
            board = state.board.copy(arrows = resolvedArrows),
            collisionsUsed = updatedCollisions,
            isGameOver = updatedCollisions == 3,
        )
    }

    private fun moveArrow(board: Board, arrow: Arrow): Arrow {
        val nextCol = arrow.col + arrow.heading.deltaCol
        val nextRow = arrow.row + arrow.heading.deltaRow
        return if (board.contains(nextCol, nextRow)) {
            arrow.copy(col = nextCol, row = nextRow)
        } else {
            arrow.copy(state = ArrowState.CLEARED)
        }
    }
}

fun activate(state: GameState, arrowId: Int): GameState {
    if (state.isGameOver) return state
    var changed = false
    val updatedArrows = state.board.arrows.map { arrow ->
        if (arrow.id == arrowId && arrow.state == ArrowState.IDLE) {
            changed = true
            arrow.copy(state = ArrowState.ACTIVE)
        } else {
            arrow
        }
    }
    return if (changed) {
        state.copy(board = state.board.copy(arrows = updatedArrows))
    } else {
        state
    }
}
