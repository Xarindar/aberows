package com.aberows.core.simulation

import com.aberows.core.model.Arrow
import com.aberows.core.model.ArrowState
import com.aberows.core.model.Board

object SimulationEngine {
    fun activateArrow(state: SimulationState, arrowId: String): SimulationState {
        if (state.status != SimulationStatus.RUNNING) {
            return state
        }

        val updatedArrows = state.board.arrows.map { arrow ->
            if (arrow.id == arrowId && arrow.state == ArrowState.IDLE) {
                arrow.copy(state = ArrowState.ACTIVE)
            } else {
                arrow
            }
        }

        return state.copy(board = state.board.copy(arrows = updatedArrows))
            .normalizeStatus()
    }

    fun step(state: SimulationState): SimulationState {
        if (state.status != SimulationStatus.RUNNING) {
            return state
        }

        val previousPositions = state.board.arrows.associate { it.id to it.position }
        var collisionCount = state.collisionsUsed

        val movedArrows = state.board.arrows.map { arrow ->
            if (arrow.state != ArrowState.ACTIVE) {
                arrow
            } else {
                val nextPosition = arrow.position.step(arrow.direction)
                if (state.board.contains(nextPosition)) {
                    arrow.copy(position = nextPosition)
                } else {
                    arrow.copy(state = ArrowState.CLEARED)
                }
            }
        }

        val activeArrows = movedArrows.filter { it.state == ArrowState.ACTIVE }
        val collidedIds = mutableSetOf<String>()

        activeArrows
            .groupBy { it.position }
            .values
            .filter { it.size > 1 }
            .forEach { colliding ->
                collidedIds += colliding.map { it.id }
                collisionCount += 1
            }

        val activeIds = activeArrows.map { it.id }
        for (leftIndex in activeIds.indices) {
            for (rightIndex in leftIndex + 1 until activeIds.size) {
                val leftId = activeIds[leftIndex]
                val rightId = activeIds[rightIndex]
                val leftArrow = activeArrows.first { it.id == leftId }
                val rightArrow = activeArrows.first { it.id == rightId }
                val leftPrevious = previousPositions.getValue(leftId)
                val rightPrevious = previousPositions.getValue(rightId)

                if (leftArrow.position == rightPrevious && rightArrow.position == leftPrevious) {
                    if (leftId !in collidedIds && rightId !in collidedIds) {
                        collidedIds += leftId
                        collidedIds += rightId
                        collisionCount += 1
                    }
                }
            }
        }

        val resolvedArrows = movedArrows.map { arrow ->
            if (arrow.id in collidedIds) {
                arrow.copy(state = ArrowState.CRASHED)
            } else {
                arrow
            }
        }

        return state.copy(
            board = state.board.copy(arrows = resolvedArrows),
            tick = state.tick + 1,
            collisionsUsed = collisionCount,
        ).normalizeStatus()
    }

    private fun SimulationState.normalizeStatus(): SimulationState {
        val allResolved = board.arrows.all { arrow ->
            arrow.state == ArrowState.CLEARED || arrow.state == ArrowState.CRASHED
        }
        val nextStatus = when {
            collisionsUsed >= maxCollisions -> SimulationStatus.FAILED
            allResolved -> SimulationStatus.WON
            else -> SimulationStatus.RUNNING
        }
        return copy(status = nextStatus)
    }
}
