package com.aberows.core.solver

import com.aberows.core.model.Arrow
import com.aberows.core.model.ArrowState
import com.aberows.core.model.Board
import com.aberows.core.model.Heading
import com.aberows.core.simulation.GameState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BfsSolverTest {
    private val solver = BfsSolver()

    @Test
    fun `solver finds single arrow perfect clear`() {
        val result = solver.solve(
            Board(
                cols = 3,
                rows = 1,
                arrows = listOf(
                    Arrow(id = 1, col = 0, row = 0, heading = Heading.RIGHT),
                ),
            ),
        )

        assertTrue(result.isSolvable)
        assertTrue(result.perfectSolutionExists)
        assertEquals(1, result.solutionCount)
        assertEquals(listOf(1), result.fastestTapSequence)
    }

    @Test
    fun `solver counts multiple tap orderings for independent arrows`() {
        val result = solver.solve(
            Board(
                cols = 4,
                rows = 1,
                arrows = listOf(
                    Arrow(id = 1, col = 0, row = 0, heading = Heading.RIGHT),
                    Arrow(id = 2, col = 3, row = 0, heading = Heading.LEFT),
                ),
            ),
        )

        assertTrue(result.isSolvable)
        assertTrue(result.perfectSolutionExists)
        assertEquals(listOf(1, 2), result.fastestTapSequence)
        assertTrue(result.solutionCount >= 2)
    }

    @Test
    fun `solver handles already won state with empty tap sequence`() {
        val result = solver.solve(
            GameState(
                board = Board(
                    cols = 1,
                    rows = 1,
                    arrows = listOf(
                        Arrow(id = 1, col = 0, row = 0, heading = Heading.RIGHT, state = ArrowState.CLEARED),
                    ),
                ),
            ),
        )

        assertTrue(result.isSolvable)
        assertTrue(result.perfectSolutionExists)
        assertEquals(1, result.solutionCount)
        assertEquals(emptyList(), result.fastestTapSequence)
    }

    @Test
    fun `solver reports failed state as unsolvable`() {
        val result = solver.solve(
            GameState(
                board = Board(
                    cols = 1,
                    rows = 1,
                    arrows = listOf(
                        Arrow(id = 1, col = 0, row = 0, heading = Heading.RIGHT, state = ArrowState.CRASHED),
                    ),
                ),
                collisionsUsed = 3,
                isGameOver = true,
            ),
        )

        assertFalse(result.isSolvable)
        assertFalse(result.perfectSolutionExists)
        assertEquals(0, result.solutionCount)
        assertEquals(emptyList(), result.fastestTapSequence)
    }
}
