package com.aberows.core.replay

data class ReplayTap(
    val tick: Int,
    val arrowId: Int,
)

data class Replay(
    val version: Int = CURRENT_VERSION,
    val taps: List<ReplayTap>,
) {
    init {
        require(version == CURRENT_VERSION) { "unsupported replay version: $version" }
        require(taps.all { it.tick >= 0 }) { "replay ticks must be non-negative" }
    }

    companion object {
        const val CURRENT_VERSION: Int = 1
    }
}
