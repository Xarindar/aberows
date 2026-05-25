# Technical Architecture — Aberows

## Module structure

```
aberows/
├── app/                     # Android application module (UI, rendering, input, DI)
│   ├── ui/                  # Compose screens and ViewModels
│   ├── renderer/            # Custom board Canvas renderer
│   └── platform/            # Android adapters (persistence, analytics, audio)
├── core/                    # Pure Kotlin module — NO Android dependencies
│   ├── simulation/          # Deterministic tick engine
│   ├── solver/              # BFS/A* exact validator
│   ├── generator/           # Hybrid PCG pipeline
│   ├── scorer/              # Difficulty estimation
│   ├── replay/              # Replay serializer / deserializer
│   └── model/               # Shared data types (Board, Arrow, State, Seed)
└── shared/                  # KMP-ready contracts for future iOS shell
```

The **core module is the test surface**. All game logic lives here; Android is just an adapter.

### Canonical types (as of Sprint 1)

```kotlin
// model/
data class Arrow(val id: Int, val col: Int, val row: Int, val heading: Heading, val state: ArrowState)
data class Board(val cols: Int, val rows: Int, val arrows: List<Arrow>)
enum class Heading { UP, RIGHT, DOWN, LEFT }  // with deltaCol, deltaRow
enum class ArrowState { IDLE, ACTIVE, CLEARED, CRASHED }

// simulation/
data class GameState(val board: Board, val collisionsUsed: Int, val isGameOver: Boolean)
class SimEngine { fun tick(state: GameState): GameState }
fun activate(state: GameState, arrowId: Int): GameState

// solver/ (stubbed, Sprint 2)
interface Solver { fun solve(board: Board): SolverResult; fun solve(state: GameState): SolverResult }
```

## Architecture diagram

```
Compose UI Shell
       │
Screen ViewModels  ←→  Repositories
       │                    │
 Domain Facade          Room DB
       │                DataStore
  ┌────┴──────────────┐  Remote Config Cache
  │  Simulation Core  │
  │  Solver/Validator │
  │  PCG Generator    │
  │  Difficulty Score │
  │  Replay Serializer│
  └───────────────────┘

Input Layer → Compose UI → Custom Board Renderer
Firebase Analytics / Crashlytics / Perf Monitoring → Repositories
WorkManager Sync → Repositories
```

## Android stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI shell | Jetpack Compose + Material 3 |
| Board renderer | Compose Canvas (upgrade to SurfaceView/AGDK if profiling demands it) |
| Architecture pattern | ViewModel → Domain Facade → pure core |
| State | Immutable UI state, coroutine/Flow pipelines |
| Persistence | Room (relational) + DataStore (preferences) |
| Background work | WorkManager |
| Analytics | Firebase Analytics + DebugView |
| Crash reporting | Firebase Crashlytics |
| Performance | Firebase Performance + Macrobenchmark + Baseline Profiles |
| Remote ops | Firebase Remote Config + A/B Testing |
| Distribution | Firebase App Distribution (pre-release) |
| Release | Android App Bundle, Play App Signing, Play Console tracks |
| Min SDK | TBD in sprint zero (target modern mainstream devices) |
| Target SDK | 35 (Android 15) — required by Play for new apps |

## Room tables

| Table | Contents |
|---|---|
| `player_profile` | XP, stars, unlock state |
| `campaign_progress` | Chapter, level, best stars per seed |
| `seeded_daily_runs` | Daily seed result history |
| `replay_header` | Seed, generator version, result, timestamp |
| `replay_events` | Per-event tap/collision stream for one replay |
| `generator_audit` | Generator params, metrics, difficulty bucket per generated board |
| `queued_telemetry` | Offline event buffer for WorkManager flush |
| `experiment_assignments` | Remote Config experiment keys and variant assignments |

## DataStore keys

Settings, not relational: sound on/off, haptics on/off, assist mode, reduced motion, left-handed mode, color theme, privacy consent state, first-launch flag.

## Networking stance

Core play is **fully offline**. Network is used only for:
- Remote Config fetch (startup, background refresh)
- Analytics / crash report upload (WorkManager, best-effort)
- Purchase verification (Google Play Billing — required)
- Daily seed signing (optional, add post-launch if anti-cheat needed)

No custom backend required at launch.

## Performance targets

| Metric | Target |
|---|---|
| Steady-state frame rate | 60 FPS |
| Frame budget | 16 ms |
| Frozen frames | Zero during gameplay |
| Cold start | < 2 s on mid-range device (Macrobenchmark gate in CI) |
| APK size | < Play baseline; PAD not needed at launch |

## Build and CI/CD

| Step | Tool |
|---|---|
| Build and unit tests | Gradle on GitHub Actions (Linux hosted runner) |
| Core logic tests | Pure JVM — no emulator needed |
| Compose UI tests | Robolectric + emulator on CI |
| Performance gates | Macrobenchmark — runs in CI, gates on regression |
| Pre-release distribution | Firebase App Distribution via fastlane |
| Play tracks | internal → closed → open → staged production |
| Pre-launch reports | Play Console (accessibility, stability, performance, security) |
| Firebase Test Lab | Automated Robo crawl + selected Espresso/UI Automator tests |

## Testing pyramid

```
[Performance gates]         Macrobenchmark + Baseline Profiles
      [E2E / UI Automator]  Play store flows
        [Compose UI tests]  Screen behavior + accessibility semantics
     [Property/fuzz tests]  Generator invariants, replay idempotence
[Pure JVM — simulation]     Movement, collision, solver oracle, replay, scoring
```

## Future: Kotlin Multiplatform

The `core/` module has no Android dependencies by design. When iOS is targeted:
- Share `core/` via KMP
- iOS shell: SwiftUI Canvas (simple) or SpriteKit (richer 2D graph)
- Map Play Billing → StoreKit, App Distribution → TestFlight
