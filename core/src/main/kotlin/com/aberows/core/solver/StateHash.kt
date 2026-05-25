package com.aberows.core.solver

import com.aberows.core.simulation.GameState

object StateHash {
    fun hash(state: GameState): Long {
        var hash = FNV_OFFSET_BASIS

        hash = hash.mix(state.board.cols)
        hash = hash.mix(state.board.rows)
        hash = hash.mix(state.collisionsUsed)
        hash = hash.mix(if (state.isGameOver) 1 else 0)

        state.board.arrows
            .sortedBy { it.id }
            .forEach { arrow ->
                hash = hash.mix(arrow.id)
                hash = hash.mix(arrow.col)
                hash = hash.mix(arrow.row)
                hash = hash.mix(arrow.heading.ordinal)
                hash = hash.mix(arrow.state.ordinal)
            }

        return hash
    }

    private fun Long.mix(value: Int): Long = (this xor value.toLong()) * FNV_PRIME

    private const val FNV_OFFSET_BASIS: Long = -3750763034362895579L
    private const val FNV_PRIME: Long = 1099511628211L
}
