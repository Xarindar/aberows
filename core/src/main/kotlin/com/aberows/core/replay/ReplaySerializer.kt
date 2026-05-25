package com.aberows.core.replay

object ReplaySerializer {
    fun serialize(replay: Replay): String {
        val body = replay.taps.joinToString(",") { "${it.tick}:${it.arrowId}" }
        return "v${replay.version}|$body"
    }

    fun deserialize(encoded: String): Replay {
        val parts = encoded.split("|", limit = 2)
        require(parts.size == 2) { "invalid replay format" }
        val versionToken = parts[0]
        require(versionToken.startsWith("v")) { "invalid replay version token" }
        val version = versionToken.removePrefix("v").toInt()

        val taps = if (parts[1].isBlank()) {
            emptyList()
        } else {
            parts[1].split(",").map { token ->
                val eventParts = token.split(":", limit = 2)
                require(eventParts.size == 2) { "invalid replay event: $token" }
                ReplayTap(
                    tick = eventParts[0].toInt(),
                    arrowId = eventParts[1].toInt(),
                )
            }
        }

        return Replay(version = version, taps = taps)
    }
}
