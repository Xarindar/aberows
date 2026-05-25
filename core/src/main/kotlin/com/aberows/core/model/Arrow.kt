package com.aberows.core.model

data class Arrow(
    val id: Int,
    val col: Int,
    val row: Int,
    val heading: Heading,
    val state: ArrowState = ArrowState.IDLE,
)
