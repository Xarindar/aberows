package com.aberows.core.simulation

import com.aberows.core.model.Arrow
import com.aberows.core.model.Board

data class SimulationState(
    val board: Board,
    val tick: Int = 0,
    val collisionsUsed: Int = 0,
    val maxCollisions: Int = 3,
    val status: SimulationStatus = SimulationStatus.RUNNING,
) {
    val arrowsById: Map<String, Arrow> = board.arrows.associateBy { it.id }
}
