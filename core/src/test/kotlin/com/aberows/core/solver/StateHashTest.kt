package com.aberows.core.solver

import com.aberows.core.model.Arrow
import com.aberows.core.model.ArrowState
import com.aberows.core.model.Board
import com.aberows.core.model.Heading
import com.aberows.core.simulation.GameState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class StateHashTest {
    @Test
    fun `equal states produce the same hash`() {
        val state = GameState(
            board = Board(
                cols = 3,
                rows = 2,
                arrows = listOf(
                    Arrow(id = 2, col = 2, row = 1, heading = Heading.LEFT, state = ArrowState.ACTIVE),
                    Arrow(id = 1, col = 0, row = 0, heading = Heading.RIGHT, state = ArrowState.IDLE),
                ),
            ),
            collisionsUsed = 1,
        )

        val reordered = state.copy(board = state.board.copy(arrows = state.board.arrows.reversed()))

        assertEquals(StateHash.hash(state), StateHash.hash(reordered))
    }

    @Test
    fun `state changes produce a different hash`() {
        val base = GameState(
            board = Board(
                cols = 2,
                rows = 1,
                arrows = listOf(
                    Arrow(id = 1, col = 0, row = 0, heading = Heading.RIGHT, state = ArrowState.IDLE),
                ),
            ),
        )
        val changed = base.copy(
            board = base.board.copy(
                arrows = listOf(
                    Arrow(id = 1, col = 1, row = 0, heading = Heading.RIGHT, state = ArrowState.ACTIVE),
                ),
            ),
        )

        assertNotEquals(StateHash.hash(base), StateHash.hash(changed))
    }
}
