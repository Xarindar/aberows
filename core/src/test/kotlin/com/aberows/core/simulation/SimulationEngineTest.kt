package com.aberows.core.simulation

import com.aberows.core.model.Arrow
import com.aberows.core.model.ArrowState
import com.aberows.core.model.Board
import com.aberows.core.model.Direction
import com.aberows.core.model.Position
import kotlin.test.Test
import kotlin.test.assertEquals

class SimulationEngineTest {
    @Test
    fun `activation switches idle arrow to active`() {
        val initial = SimulationState(
            board = Board(
                width = 3,
                height = 3,
                arrows = listOf(
                    Arrow(id = "a", position = Position(1, 1), direction = Direction.RIGHT),
                ),
            ),
        )

        val activated = SimulationEngine.activateArrow(initial, "a")

        assertEquals(ArrowState.ACTIVE, activated.arrowsById.getValue("a").state)
        assertEquals(SimulationStatus.RUNNING, activated.status)
    }

    @Test
    fun `active arrow clears when it exits the board`() {
        val initial = SimulationState(
            board = Board(
                width = 2,
                height = 1,
                arrows = listOf(
                    Arrow(
                        id = "a",
                        position = Position(1, 0),
                        direction = Direction.RIGHT,
                        state = ArrowState.ACTIVE,
                    ),
                ),
            ),
        )

        val stepped = SimulationEngine.step(initial)

        assertEquals(ArrowState.CLEARED, stepped.arrowsById.getValue("a").state)
        assertEquals(SimulationStatus.WON, stepped.status)
    }

    @Test
    fun `head on landing collision crashes both arrows and increments collisions`() {
        val initial = SimulationState(
            board = Board(
                width = 3,
                height = 1,
                arrows = listOf(
                    Arrow(
                        id = "a",
                        position = Position(0, 0),
                        direction = Direction.RIGHT,
                        state = ArrowState.ACTIVE,
                    ),
                    Arrow(
                        id = "b",
                        position = Position(2, 0),
                        direction = Direction.LEFT,
                        state = ArrowState.ACTIVE,
                    ),
                ),
            ),
        )

        val stepped = SimulationEngine.step(initial)

        assertEquals(ArrowState.CRASHED, stepped.arrowsById.getValue("a").state)
        assertEquals(ArrowState.CRASHED, stepped.arrowsById.getValue("b").state)
        assertEquals(1, stepped.collisionsUsed)
        assertEquals(SimulationStatus.WON, stepped.status)
    }

    @Test
    fun `swap collision is detected when arrows cross on the same tick`() {
        val initial = SimulationState(
            board = Board(
                width = 2,
                height = 1,
                arrows = listOf(
                    Arrow(
                        id = "a",
                        position = Position(0, 0),
                        direction = Direction.RIGHT,
                        state = ArrowState.ACTIVE,
                    ),
                    Arrow(
                        id = "b",
                        position = Position(1, 0),
                        direction = Direction.LEFT,
                        state = ArrowState.ACTIVE,
                    ),
                ),
            ),
        )

        val stepped = SimulationEngine.step(initial)

        assertEquals(ArrowState.CRASHED, stepped.arrowsById.getValue("a").state)
        assertEquals(ArrowState.CRASHED, stepped.arrowsById.getValue("b").state)
        assertEquals(1, stepped.collisionsUsed)
    }

    @Test
    fun `mixed same cell and swap collision counts each arrow at most once per tick`() {
        val initial = SimulationState(
            board = Board(
                width = 3,
                height = 2,
                arrows = listOf(
                    Arrow(
                        id = "a",
                        position = Position(0, 0),
                        direction = Direction.RIGHT,
                        state = ArrowState.ACTIVE,
                    ),
                    Arrow(
                        id = "b",
                        position = Position(2, 0),
                        direction = Direction.LEFT,
                        state = ArrowState.ACTIVE,
                    ),
                    Arrow(
                        id = "c",
                        position = Position(1, 1),
                        direction = Direction.UP,
                        state = ArrowState.ACTIVE,
                    ),
                ),
            ),
        )

        val stepped = SimulationEngine.step(initial)

        assertEquals(ArrowState.CRASHED, stepped.arrowsById.getValue("a").state)
        assertEquals(ArrowState.CRASHED, stepped.arrowsById.getValue("b").state)
        assertEquals(ArrowState.CRASHED, stepped.arrowsById.getValue("c").state)
        assertEquals(1, stepped.collisionsUsed)
        assertEquals(SimulationStatus.WON, stepped.status)
    }

    @Test
    fun `third collision fails the run`() {
        val initial = SimulationState(
            board = Board(
                width = 3,
                height = 1,
                arrows = listOf(
                    Arrow(
                        id = "a",
                        position = Position(0, 0),
                        direction = Direction.RIGHT,
                        state = ArrowState.ACTIVE,
                    ),
                    Arrow(
                        id = "b",
                        position = Position(2, 0),
                        direction = Direction.LEFT,
                        state = ArrowState.ACTIVE,
                    ),
                ),
            ),
            collisionsUsed = 2,
        )

        val stepped = SimulationEngine.step(initial)

        assertEquals(3, stepped.collisionsUsed)
        assertEquals(SimulationStatus.FAILED, stepped.status)
    }
}
