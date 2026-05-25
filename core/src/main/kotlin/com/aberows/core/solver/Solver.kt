package com.aberows.core.solver

import com.aberows.core.model.Board
import com.aberows.core.simulation.SimulationState

data class SolverResult(
    val isSolvable: Boolean,
    val perfectSolutionExists: Boolean,  // zero collisions
    val solutionCount: Int,              // distinct tap orderings that clear the board
    val fastestTapSequence: List<String>, // arrowIds in tap order for fastest clear
    val estimatedDifficulty: Float,      // 0.0 (trivial) to 1.0 (expert)
)

/**
 * Exact BFS/A* solver over the game state space.
 * Uses transposition hashing + dominance pruning.
 *
 * State dominates another if: same idle/active configuration,
 * ≤ collisions used, and no worse temporal cost.
 *
 * TODO: Implement solve() using BFS with the dominance rule in PCG.md.
 */
interface Solver {
    fun solve(board: Board): SolverResult
    fun solve(state: SimulationState): SolverResult
}
