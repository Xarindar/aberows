package com.aberows.core.model

data class Arrow(
    val id: String,
    val position: Position,
    val direction: Direction,
    val state: ArrowState = ArrowState.IDLE,
)
