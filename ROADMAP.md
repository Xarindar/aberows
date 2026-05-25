# Roadmap — Aberows

Two-week sprints. Milestones map to GitHub Milestones.

## Milestone 1 — Graybox Spike (Weeks 1–6)
**Goal:** De-risk design, simulation, and generator before full production.

### Sprint 1 · Core rules graybox (June 1–14) ✅ COMPLETE
- [x] Define board data model: `Board(cols, rows, arrows)`, `Arrow(id: Int, col, row, heading, state)`, `Heading` enum, `ArrowState` enum
- [x] Implement deterministic tick engine: `SimEngine.tick()` — pure Kotlin, zero Android deps
- [x] Tap-activate → travel → exit / collision detection (same-cell + swap/crossing)
- [x] Three-collision lifecycle: idle → active → cleared / crashed, `isGameOver` on 3rd collision
- [ ] Minimal Compose board renderer for playtesting (no art) — **@copilot in progress**
- [ ] Tap input wired to simulation
- [ ] Manual playtest: does the tap-and-release loop feel right?
- [x] 10 unit tests passing (CI green): activation, exit-clear, head-on, swap, mixed 3-arrow, no-op, third-collision-fail
- [x] GitHub Actions CI: JDK 17 + Gradle 8.7, `:core:test` on push/PR

### Sprint 2 · Deterministic sim and replay spike (June 15–28) 🔄 NEXT
- [ ] Replay serializer: record tap events → deterministic playback
- [ ] State hash for transposition table
- [ ] Property tests: replay idempotence, no impossible state transitions
- [ ] BFS solver prototype (brute force, no pruning yet) — `Solver` interface stubbed in `:core`
- [ ] Benchmark solver on 4×4, 5×5, 6×6 boards

### Sprint 3 · Generator/solver feasibility (June 29 – July 12)
- [ ] Reverse-play constructive generator (base arrow grammar)
- [ ] Solvability validator integrated into generator loop
- [ ] Shortcut detection: alternate-solution counter
- [ ] Difficulty feature extraction (ordering depth, simultaneity, timing margin)
- [ ] Yield measurement: % valid boards per difficulty bucket
- [ ] Decision on real-time vs discrete-event-driven board (answer open question)

---

## Milestone 2 — Core Production (Weeks 7–14)
**Goal:** Shippable app skeleton with one playable chapter.

### Sprint 4 · App architecture and persistence (July 13–26)
- [ ] Android project setup (App Bundle, API 35 target, edge-to-edge)
- [ ] Module split: `:app`, `:core`, proguard config
- [ ] Room schema + migrations scaffolded
- [ ] DataStore settings schema
- [ ] Firebase project wired (Analytics, Crashlytics, Remote Config)
- [ ] CI pipeline: GitHub Actions, Gradle cache, unit tests on push
- [ ] Baseline Profiles setup

### Sprint 5 · Board renderer and input polish (July 27 – Aug 9)
- [ ] Production board renderer: arrow glyphs, state colors, animation
- [ ] Idle / selected / active / danger / crashed / cleared visual states
- [ ] Compose semantics for board arrows (accessibility)
- [ ] Tap input with 48dp+ hit targets
- [ ] HUD: arrows remaining, collisions used, pause
- [ ] Haptic and sound hooks wired (no final audio yet)
- [ ] Macrobenchmark baseline for frame timing

### Sprint 6a · FTUE and campaign chapter 1 (Aug 10–23)
- [ ] Hand-author 10 FTUE levels
- [ ] Level loader from seed + ruleset
- [ ] Star rating system (0/1/2/3 per level)
- [ ] Chapter 1 completion and transition UI
- [ ] Campaign progress persistence (Room)

### Sprint 6b · Analytics and crash/perf plumbing (Aug 10–23, parallel)
- [ ] All events from analytics schema wired + DebugView verified
- [ ] Generator metadata attached to level events
- [ ] Crashlytics breadcrumbs on simulation errors
- [ ] Performance traces on level load and board render
- [ ] Remote Config fetch and default values

---

## Milestone 3 — Content and Tuning (Weeks 15–24)
**Goal:** Full chapter arc, monetization, accessibility, tuned difficulty.

### Sprint 7 · Generator buckets and validators (Aug 24 – Sep 6)
- [ ] Difficulty bucketing: easy / medium / hard / expert targets
- [ ] Interestingness score implemented
- [ ] Seeded daily generator wired
- [ ] Generator audit table populated on every generated board
- [ ] Golden board regression suite in CI

### Sprint 8–9 · Chapters 2–4: Obstacles, Rotators, Timed Gates (Sep 7 – Oct 4)
- [ ] Obstacles mechanic + chapter 2 boards
- [ ] Rotators mechanic + chapter 3 boards
- [ ] Timed gates mechanic + chapter 4 boards
- [ ] Generator extended to handle each new mechanic
- [ ] Solver extended for each new mechanic

### Sprint 10 · Chapters 5–8: Splitters, Portals, Locks, Mixed (Oct 5–18)
- [ ] Splitters, portals, locks/keys mechanics
- [ ] Chapter 5–8 boards authored/generated
- [ ] Mixed-mechanic generator validation
- [ ] Difficulty curve QA (solver-predicted vs observed)

### Sprint 11 · Accessibility and assist modes (Oct 5–18, parallel)
- [ ] Assist mode: pause-on-tap, first-path preview, speed slider
- [ ] Reduced-motion mode
- [ ] Left-handed HUD layout
- [ ] Color-blind-safe palette review
- [ ] Full Compose semantics audit
- [ ] Accessibility testing with TalkBack

### Sprint 12 · Monetization and store assets (Oct 19 – Nov 1)
- [ ] Google Play Billing integration (remove-ads SKU, hint pack)
- [ ] AdMob rewarded ad placements (revive on collision 3, hint, daily entry)
- [ ] Consent / UMP flow wired
- [ ] Play store listing drafted
- [ ] Data safety declaration completed
- [ ] Store screenshots and feature graphic

---

## Milestone 4 — Soft Launch (Weeks 25–32)
**Goal:** Validate retention, monetization, and difficulty with real players.

### Sprint 13 · Internal testing and Test Lab (Nov 2–15)
- [ ] Firebase App Distribution to internal team
- [ ] Play internal testing track live
- [ ] Firebase Test Lab automated Robo crawl
- [ ] Pre-launch report reviewed and issues triaged
- [ ] Telemetry dashboards live

### Sprint 14–15 · Closed/open track soft launch (Nov 16 – Dec 13)
- [ ] Closed testing track open to external testers
- [ ] FTUE funnel analysis — iterate on first 5 levels if drop-off > threshold
- [ ] Difficulty curve calibration from collision histograms
- [ ] Ad cadence A/B test started
- [ ] Remote Config tuning based on early data

### Sprint 16–17 · A/B tests and balancing (Dec 14 – Jan 10)
- [ ] Revive offer timing A/B test
- [ ] Hint pricing A/B test
- [ ] Star threshold A/B test (2 vs 3 stars to unlock)
- [ ] Generator bucket rebalance from solver-vs-observed data
- [ ] Monetization opt-in baseline established

---

## Milestone 5 — Launch (Weeks 33–35)
**Goal:** Production rollout.

### Sprint 18 · Launch candidate hardening (Jan 11–24)
- [ ] Crash rate < 1% on pre-launch report
- [ ] No P0/P1 open bugs
- [ ] ANR rate < Play threshold
- [ ] Accessibility audit passed
- [ ] Legal review of store listing and privacy policy

### Sprint 19 · Production rollout (Jan 25–31)
- [ ] Staged rollout: 1% → 10% → 50% → 100%
- [ ] Monitor crash rate, ANR rate, rating/review velocity
- [ ] Hotfix process confirmed ready
- [ ] Pre-registration campaign closed

---

## Open questions (answer in Milestone 1)

1. Real-time continuous simulation or discrete event-driven with interpolation?
2. Campaign-first premium positioning or free-to-play casual reach?
3. How strict should the validator be on alternate solutions before yield collapses?
4. Will advanced mechanics stay graph-friendly or push toward heavier simulation?
