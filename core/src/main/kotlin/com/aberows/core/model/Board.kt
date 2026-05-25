package com.aberows.core.model

data class Board(
    val cols: Int,
    val rows: Int,
    val arrows: List<Arrow>,
) {
    init {
        require(cols > 0) { "cols must be positive" }
        require(rows > 0) { "rows must be positive" }
        require(arrows.map { it.id }.distinct().size == arrows.size) { "arrow ids must be unique" }
    }

    fun contains(col: Int, row: Int): Boolean = col in 0 until cols && row in 0 until rows
}
