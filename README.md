# Aberows — Procedural Arrow Puzzle Game

A native Android puzzle game built on a deterministic arrow-routing mechanic. Players tap idle arrows to activate them; each arrow travels its direction until it exits, reaches a goal, or collides. The core challenge is **ordering, timing, and interference management** — not aim.

## Quick links

| Doc | Purpose |
|---|---|
| [Game Design](docs/GAME_DESIGN.md) | Mechanic, modes, scoring, accessibility, monetization |
| [Architecture](docs/ARCHITECTURE.md) | Android stack, module structure, data model, CI/CD |
| [Procedural Generation](docs/PCG.md) | Generator pipeline, solver, difficulty estimation |
| [Analytics](docs/ANALYTICS.md) | Event model, dashboards, telemetry strategy |
| [Roadmap](ROADMAP.md) | Sprint plan and milestone targets |
| [Resources](RESOURCES.md) | Asset and infrastructure checklist |

## Concept

```
Board = directed graph
Arrow token = node + heading + activation state
Player action = choose tap order and timing
Win condition = all arrows cleared before 3rd collision
```

The puzzle is formally a **deterministic scheduling and routing problem** on a discrete graph. This makes it exactly simulable, solver-backed, and compatible with procedural generation with solvability guarantees.

## Tech stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Board renderer**: Compose Canvas (upgrade path to custom View / AGDK if needed)
- **Architecture**: ViewModel → Domain Facade → pure Kotlin simulation core
- **Persistence**: Room (progress, replays, telemetry) + DataStore (settings)
- **Background**: WorkManager
- **Analytics / Ops**: Firebase Analytics, Crashlytics, Remote Config, App Distribution
- **Release**: Android App Bundle, Play Console

## Current status

**Sprint 1 complete — simulation core shipping.** CI green.

| Module | Status |
|---|---|
| `:core` — board model, tick engine, collision detection | ✅ Done — 10 tests passing |
| `:core` — replay serializer + BFS solver | 🔄 Sprint 2 next |
| `:app` — Compose graybox renderer | 🔄 In progress |
| `:app` — Android scaffold (API 35, edge-to-edge) | ⏳ Pending |

### Canonical model (`:core`)

```kotlin
data class Arrow(val id: Int, val col: Int, val row: Int, val heading: Heading, val state: ArrowState)
data class Board(val cols: Int, val rows: Int, val arrows: List<Arrow>)
data class GameState(val board: Board, val collisionsUsed: Int, val isGameOver: Boolean)

// SimEngine.tick(state) — advances all ACTIVE arrows, detects same-cell and swap collisions
// activate(state, arrowId) — IDLE → ACTIVE transition
```

## Graybox spike goals (first 6 weeks)

1. ✅ Prove the feel of the tap-and-release loop (simulation core done).
2. Validate correctness and speed of the deterministic solver/validator.
3. Measure retention impact of the three-collision forgiveness system vs stricter modes.
