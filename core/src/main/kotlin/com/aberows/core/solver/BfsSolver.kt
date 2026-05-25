package com.aberows.core.solver

import com.aberows.core.model.ArrowState
import com.aberows.core.model.Board
import com.aberows.core.simulation.GameState
import com.aberows.core.simulation.SimEngine
import com.aberows.core.simulation.activate
import java.util.ArrayDeque

class BfsSolver(
    private val engine: SimEngine = SimEngine(),
) : Solver {
    private data class SearchNode(
        val state: GameState,
        val taps: List<Int>,
        val ticksElapsed: Int,
    )

    override fun solve(board: Board): SolverResult = solve(GameState(board = board))

    override fun solve(state: GameState): SolverResult {
        if (state.isFailed) {
            return SolverResult(
                isSolvable = false,
                perfectSolutionExists = false,
                solutionCount = 0,
                fastestTapSequence = emptyList(),
                estimatedDifficulty = 1.0f,
            )
        }

        val queue = ArrayDeque<SearchNode>()
        val visited = mutableMapOf<Long, Int>()
        var fastestWin: SearchNode? = null
        var solutionCount = 0
        var perfectSolutionExists = false

        fun enqueue(node: SearchNode) {
            val hash = StateHash.hash(node.state)
            val actionCost = node.taps.size + node.ticksElapsed
            val previousCost = visited[hash]
            if (previousCost == null || actionCost < previousCost) {
                visited[hash] = actionCost
                queue += node
            }
        }

        enqueue(SearchNode(state = state, taps = emptyList(), ticksElapsed = 0))

        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()

            if (node.state.isWon) {
                solutionCount += 1
                if (node.state.collisionsUsed == 0) {
                    perfectSolutionExists = true
                }
                if (fastestWin == null || isBetterSolution(node, fastestWin)) {
                    fastestWin = node
                }
                continue
            }
            if (node.state.isFailed) {
                continue
            }

            val idleArrowIds = node.state.board.arrows
                .asSequence()
                .filter { it.state == ArrowState.IDLE }
                .map { it.id }
                .sorted()
                .toList()

            idleArrowIds.forEach { arrowId ->
                val activatedState = activate(node.state, arrowId)
                if (activatedState != node.state) {
                    enqueue(
                        SearchNode(
                            state = activatedState,
                            taps = node.taps + arrowId,
                            ticksElapsed = node.ticksElapsed,
                        ),
                    )
                }
            }

            val hasActiveArrows = node.state.board.arrows.any { it.state == ArrowState.ACTIVE }
            if (hasActiveArrows) {
                enqueue(
                    SearchNode(
                        state = engine.tick(node.state),
                        taps = node.taps,
                        ticksElapsed = node.ticksElapsed + 1,
                    ),
                )
            }
        }

        val best = fastestWin
        return SolverResult(
            isSolvable = best != null,
            perfectSolutionExists = perfectSolutionExists,
            solutionCount = solutionCount,
            fastestTapSequence = best?.taps.orEmpty(),
            estimatedDifficulty = best?.let(::estimateDifficulty) ?: 1.0f,
        )
    }

    private fun isBetterSolution(candidate: SearchNode, incumbent: SearchNode): Boolean {
        if (candidate.ticksElapsed != incumbent.ticksElapsed) {
            return candidate.ticksElapsed < incumbent.ticksElapsed
        }
        if (candidate.taps.size != incumbent.taps.size) {
            return candidate.taps.size < incumbent.taps.size
        }
        return isLexicographicallySmaller(candidate.taps, incumbent.taps)
    }

    private fun estimateDifficulty(solution: SearchNode): Float {
        val totalArrows = solution.state.board.arrows.size.coerceAtLeast(1)
        val tapsScore = solution.taps.size.toFloat() / totalArrows.toFloat()
        val tickScore = (solution.ticksElapsed / (totalArrows * 2f)).coerceAtMost(1.0f)
        val collisionScore = solution.state.collisionsUsed / 3f
        return ((tapsScore * 0.45f) + (tickScore * 0.35f) + (collisionScore * 0.20f))
            .coerceIn(0.0f, 1.0f)
    }

    private fun isLexicographicallySmaller(left: List<Int>, right: List<Int>): Boolean {
        val limit = minOf(left.size, right.size)
        for (index in 0 until limit) {
            if (left[index] != right[index]) {
                return left[index] < right[index]
            }
        }
        return left.size < right.size
    }
}
