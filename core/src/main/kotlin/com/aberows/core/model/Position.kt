package com.aberows.core.model

data class Position(
    val x: Int,
    val y: Int,
) {
    fun step(direction: Direction): Position = Position(
        x = x + direction.deltaX,
        y = y + direction.deltaY,
    )
}
