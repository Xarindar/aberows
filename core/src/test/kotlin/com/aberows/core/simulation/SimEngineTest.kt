package com.aberows.core.simulation

import com.aberows.core.model.Arrow
import com.aberows.core.model.ArrowState
import com.aberows.core.model.Board
import com.aberows.core.model.Heading
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SimEngineTest {
    private val engine = SimEngine()

    @Test
    fun `straight travel to exit`() {
        val initial = GameState(
            board = Board(
                cols = 2,
                rows = 1,
                arrows = listOf(
                    Arrow(id = 1, col = 1, row = 0, heading = Heading.RIGHT, state = ArrowState.ACTIVE),
                ),
            ),
        )

        val ticked = engine.tick(initial)

        assertEquals(ArrowState.CLEARED, ticked.board.arrows.single().state)
        assertEquals(0, ticked.collisionsUsed)
        assertFalse(ticked.isGameOver)
        assertTrue(ticked.isWon)
        assertFalse(ticked.isFailed)
    }

    @Test
    fun `head on collision`() {
        val initial = GameState(
            board = Board(
                cols = 3,
                rows = 1,
                arrows = listOf(
                    Arrow(id = 1, col = 0, row = 0, heading = Heading.RIGHT, state = ArrowState.ACTIVE),
                    Arrow(id = 2, col = 2, row = 0, heading = Heading.LEFT, state = ArrowState.ACTIVE),
                ),
            ),
        )

        val ticked = engine.tick(initial)

        assertEquals(listOf(ArrowState.CRASHED, ArrowState.CRASHED), ticked.board.arrows.map(Arrow::state))
        assertEquals(1, ticked.collisionsUsed)
        assertFalse(ticked.isGameOver)
        assertTrue(ticked.isWon)
    }

    @Test
    fun `swap collision detected when arrows cross on the same tick`() {
        val initial = GameState(
            board = Board(
                cols = 2,
                rows = 1,
                arrows = listOf(
                    Arrow(id = 1, col = 0, row = 0, heading = Heading.RIGHT, state = ArrowState.ACTIVE),
                    Arrow(id = 2, col = 1, row = 0, heading = Heading.LEFT, state = ArrowState.ACTIVE),
                ),
            ),
        )

        val ticked = engine.tick(initial)

        assertEquals(ArrowState.CRASHED, ticked.board.arrows.first { it.id == 1 }.state)
        assertEquals(ArrowState.CRASHED, ticked.board.arrows.first { it.id == 2 }.state)
        assertEquals(1, ticked.collisionsUsed)
        assertTrue(ticked.isWon)
    }

    @Test
    fun `t intersection collision`() {
        val initial = GameState(
            board = Board(
                cols = 3,
                rows = 3,
                arrows = listOf(
                    Arrow(id = 1, col = 0, row = 1, heading = Heading.RIGHT, state = ArrowState.ACTIVE),
                    Arrow(id = 2, col = 1, row = 2, heading = Heading.UP, state = ArrowState.ACTIVE),
                ),
            ),
        )

        val ticked = engine.tick(initial)

        assertEquals(listOf(ArrowState.CRASHED, ArrowState.CRASHED), ticked.board.arrows.map(Arrow::state))
        assertEquals(1, ticked.collisionsUsed)
        assertFalse(ticked.isGameOver)
        assertTrue(ticked.isWon)
    }

    @Test
    fun `mixed same-cell and swap collision counts exactly one event`() {
        // All three converge on (1,0) in the same tick
        val initial = GameState(
            board = Board(
                cols = 3,
                rows = 2,
                arrows = listOf(
                    Arrow(id = 1, col = 0, row = 0, heading = Heading.RIGHT, state = ArrowState.ACTIVE),
                    Arrow(id = 2, col = 2, row = 0, heading = Heading.LEFT, state = ArrowState.ACTIVE),
                    Arrow(id = 3, col = 1, row = 1, heading = Heading.UP, state = ArrowState.ACTIVE),
                ),
            ),
        )

        val ticked = engine.tick(initial)

        ticked.board.arrows.forEach { assertEquals(ArrowState.CRASHED, it.state) }
        assertEquals(1, ticked.collisionsUsed)
        assertTrue(ticked.isWon)
    }

    @Test
    fun `all clear win`() {
        val initial = GameState(
            board = Board(
                cols = 1,
                rows = 1,
                arrows = listOf(
                    Arrow(id = 1, col = 0, row = 0, heading = Heading.RIGHT, state = ArrowState.ACTIVE),
                ),
            ),
        )

        val ticked = engine.tick(initial)

        assertTrue(ticked.board.arrows.all { it.state == ArrowState.CLEARED || it.state == ArrowState.CRASHED })
        assertFalse(ticked.isGameOver)
        assertTrue(ticked.isWon)
        assertFalse(ticked.isFailed)
    }

    @Test
    fun `third collision game over`() {
        val initial = GameState(
            board = Board(
                cols = 3,
                rows = 1,
                arrows = listOf(
                    Arrow(id = 1, col = 0, row = 0, heading = Heading.RIGHT, state = ArrowState.ACTIVE),
                    Arrow(id = 2, col = 2, row = 0, heading = Heading.LEFT, state = ArrowState.ACTIVE),
                ),
            ),
            collisionsUsed = 2,
        )

        val ticked = engine.tick(initial)

        assertEquals(3, ticked.collisionsUsed)
        assertTrue(ticked.isGameOver)
        assertTrue(ticked.isFailed)
        assertFalse(ticked.isWon)
    }

    @Test
    fun `activating already active arrow is a no-op`() {
        val initial = GameState(
            board = Board(
                cols = 2,
                rows = 1,
                arrows = listOf(
                    Arrow(id = 1, col = 0, row = 0, heading = Heading.RIGHT, state = ArrowState.ACTIVE),
                ),
            ),
        )

        val activated = activate(initial, 1)

        assertEquals(initial, activated)
    }

    @Test
    fun `tick is a no-op when game is already over`() {
        val initial = GameState(
            board = Board(
                cols = 1,
                rows = 1,
                arrows = listOf(
                    Arrow(id = 1, col = 0, row = 0, heading = Heading.RIGHT, state = ArrowState.CLEARED),
                ),
            ),
            isGameOver = true,
        )

        assertEquals(initial, engine.tick(initial))
    }

    @Test
    fun `idle arrows are not moved by tick`() {
        val initial = GameState(
            board = Board(
                cols = 3,
                rows = 3,
                arrows = listOf(
                    Arrow(id = 1, col = 1, row = 1, heading = Heading.RIGHT),
                ),
            ),
        )

        val ticked = engine.tick(initial)

        val arrow = ticked.board.arrows.single()
        assertEquals(1, arrow.col)
        assertEquals(1, arrow.row)
        assertEquals(ArrowState.IDLE, arrow.state)
        assertFalse(ticked.isGameOver)
    }
}
