package com.aberows.core.replay

import kotlin.test.Test
import kotlin.test.assertEquals

class ReplaySerializerTest {
    @Test
    fun `serialize and deserialize round trip`() {
        val replay = Replay(
            taps = listOf(
                ReplayTap(tick = 0, arrowId = 10),
                ReplayTap(tick = 3, arrowId = 20),
                ReplayTap(tick = 3, arrowId = 30),
            ),
        )

        val encoded = ReplaySerializer.serialize(replay)
        val decoded = ReplaySerializer.deserialize(encoded)

        assertEquals("v1|0:10,3:20,3:30", encoded)
        assertEquals(replay, decoded)
    }

    @Test
    fun `empty replay round trips`() {
        val replay = Replay(taps = emptyList())

        val encoded = ReplaySerializer.serialize(replay)

        assertEquals("v1|", encoded)
        assertEquals(replay, ReplaySerializer.deserialize(encoded))
    }
}
