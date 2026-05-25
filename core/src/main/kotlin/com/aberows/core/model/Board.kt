package com.aberows.core.model

data class Board(
    val width: Int,
    val height: Int,
    val arrows: List<Arrow>,
) {
    init {
        require(width > 0) { "width must be positive" }
        require(height > 0) { "height must be positive" }
        require(arrows.map { it.id }.distinct().size == arrows.size) { "arrow ids must be unique" }
    }

    fun contains(position: Position): Boolean {
        return position.x in 0 until width && position.y in 0 until height
    }
}
