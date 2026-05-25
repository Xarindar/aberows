package com.aberows.core.replay

import com.aberows.core.model.ArrowState
import com.aberows.core.simulation.GameState
import com.aberows.core.simulation.SimEngine
import com.aberows.core.simulation.activate

data class ReplayResult(
    val finalState: GameState,
    val ticksSimulated: Int,
)

class ReplayPlayer(
    private val engine: SimEngine = SimEngine(),
) {
    fun play(initialState: GameState, replay: Replay): ReplayResult {
        var state = initialState
        var tick = 0
        val tapsByTick = replay.taps.groupBy { it.tick }
        val lastTapTick = replay.taps.maxOfOrNull { it.tick } ?: -1

        while (true) {
            tapsByTick[tick].orEmpty().forEach { tap ->
                state = activate(state, tap.arrowId)
            }

            if (state.isWon || state.isFailed) {
                return ReplayResult(finalState = state, ticksSimulated = tick)
            }

            val hasActiveArrows = state.board.arrows.any { it.state == ArrowState.ACTIVE }
            val hasFutureTaps = tick < lastTapTick
            if (!hasActiveArrows && !hasFutureTaps) {
                return ReplayResult(finalState = state, ticksSimulated = tick)
            }

            state = engine.tick(state)
            tick += 1

            if (state.isWon || state.isFailed) {
                return ReplayResult(finalState = state, ticksSimulated = tick)
            }
        }
    }
}
