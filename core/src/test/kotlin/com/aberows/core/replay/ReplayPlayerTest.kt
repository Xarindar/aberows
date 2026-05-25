package com.aberows.core.replay

import com.aberows.core.model.Arrow
import com.aberows.core.model.ArrowState
import com.aberows.core.model.Board
import com.aberows.core.model.Heading
import com.aberows.core.simulation.GameState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReplayPlayerTest {
    private val player = ReplayPlayer()

    @Test
    fun `playback reproduces expected win state`() {
        val initial = GameState(
            board = Board(
                cols = 3,
                rows = 1,
                arrows = listOf(
                    Arrow(id = 1, col = 0, row = 0, heading = Heading.RIGHT),
                    Arrow(id = 2, col = 2, row = 0, heading = Heading.RIGHT),
                ),
            ),
        )
        val replay = Replay(
            taps = listOf(
                ReplayTap(tick = 0, arrowId = 1),
                ReplayTap(tick = 1, arrowId = 2),
            ),
        )

        val result = player.play(initial, replay)

        assertTrue(result.finalState.isWon)
        assertFalse(result.finalState.isFailed)
        assertEquals(listOf(ArrowState.CLEARED, ArrowState.CLEARED), result.finalState.board.arrows.map { it.state })
        assertEquals(3, result.ticksSimulated)
    }

    @Test
    fun `playback is deterministic for repeated runs`() {
        val initial = GameState(
            board = Board(
                cols = 3,
                rows = 1,
                arrows = listOf(
                    Arrow(id = 1, col = 0, row = 0, heading = Heading.RIGHT),
                    Arrow(id = 2, col = 2, row = 0, heading = Heading.LEFT),
                ),
            ),
        )
        val replay = Replay(
            taps = listOf(
                ReplayTap(tick = 0, arrowId = 1),
                ReplayTap(tick = 0, arrowId = 2),
            ),
        )

        val first = player.play(initial, replay)
        val second = player.play(initial, replay)

        assertEquals(first, second)
        assertTrue(first.finalState.isWon)
        assertEquals(1, first.finalState.collisionsUsed)
    }
}
