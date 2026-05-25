package com.aberows.core.model

enum class Heading(
    val deltaCol: Int,
    val deltaRow: Int,
) {
    UP(0, -1),
    RIGHT(1, 0),
    DOWN(0, 1),
    LEFT(-1, 0),
}
