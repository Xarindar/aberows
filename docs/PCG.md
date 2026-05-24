# Procedural Content Generation — Aberows

## Formal model

A board is a **directed graph** `G = (V, E)`:

- `V` — cells/anchor nodes
- `E` — legal travel segments
- Each arrow token `aᵢ` has: starting node `vᵢ`, heading `dᵢ`, state `sᵢ ∈ {idle, active, cleared, crashed}`, optional mechanic modifiers

A game state is `S = (I, A, c, t)`:

- `I` — set of remaining idle arrows
- `A` — snapshot of active arrows (position + sub-tick offset)
- `c` — collisions used so far
- `t` — simulation step count

A collision occurs when two active arrows occupy the same node in the same tick, or traverse conflicting edges in the same time slice. Because the system is **discrete and deterministic**, it is exactly simulable and solver-tractable.

## Hybrid generator pipeline

```
1. Constructive skeleton
   Build a candidate board with a known solvable interaction structure.
   Seed from solution motifs: precedence chains, safe-release windows,
   controlled path intersections.

2. Local legality filter
   Fast reject for impossible/degenerate boards before expensive solving.
   (e.g., overlapping start positions, unsatisfiable gate dependencies)

3. Exact solvability validation
   Run deterministic BFS/A* solver over the state space.
   Transposition hashing + dominance pruning.
   A state dominates if: same idle/active configuration,
   ≤ collisions, no worse temporal cost.

4. Shortcut / alternate-solution analysis
   Ask three questions:
   a. Is there a perfect solution (zero collisions)?
   b. How many materially distinct solutions exist?
   c. Does any solution bypass the intended mechanic/ordering?
   Reject boards where most arrows can be tapped in any order,
   or where a key mechanic is never actually needed.

5. Difficulty estimation
   Compute weighted difficulty score from metrics below.

6. Repair or reject
   Mutate parameters until board lands in target difficulty bucket.
   Retry up to N times; if still out of bucket, discard and reseed.
```

## Generation approaches by use case

| Approach | Use in Aberows |
|---|---|
| Pure random + rejection | Early prototyping only — low yield at depth |
| Template-driven constructive | FTUE levels and chapter gateway boards |
| Reverse-play constructive | Base arrow grammar — strong solvability guarantees |
| CSP / SAT / ASP | Offline authoring tool, validator extensions |
| Search-based repair | Second-stage difficulty tuning |
| MCTS / simulated-play | Daily seeds, medium-depth endless boards |
| Evolutionary simulation | Future only, if later mechanics become much richer |

## Difficulty feature vector

| Feature | What it measures |
|---|---|
| Board scale | Size, occupancy density, obstacle ratio |
| Path interaction | Intersections, shared corridors, gate dependencies |
| Ordering depth | Longest precedence chain among tap actions |
| Simultaneity pressure | Peak concurrent active arrows |
| Timing margin | Average slack between safe and unsafe activation windows |
| Solution breadth | Number of valid distinct solutions / duplicate-solution ratio |
| Mechanic variety | Count and novelty of active mechanics on this board |
| Spatial legibility | Symmetry, cluster balance, path readability |
| Recovery headroom | How often player survives an early suboptimal move with 2-collision margin |

Prefer **mid-to-high interaction density with low shortcut count** over raw board size expansion.

## Interestingness score

```
interestingness = w₁ × solution_length
               + w₂ × decision_entropy_first_3_taps
               + w₃ × interaction_density
               + w₄ × (1 / shortcut_count)
               + w₅ × mechanic_utilization
               + w₆ × aesthetic_balance
```

Reject boards with high unused-mechanic counts — they look richer than they are.

## Seeded randomness (required)

Every generated board must be identified by:
```
(generator_version, ruleset_version, seed, target_bucket)
```

This enables:
- Deterministic replay
- Bug reproduction from telemetry
- Live-ops scheduling of daily seeds
- Rebalancing without re-generating
- Telemetry joins to connect generation params with player outcomes

## Solver as production infrastructure

The exact solver is not a tool-side extra. It is called:

1. At generation time (validate every candidate board)
2. At level-start (cache solver metadata for analytics)
3. In CI (golden board regression suite — all historical mechanic interactions)

Solver must return: `{is_solvable, perfect_solution_exists, solution_count, fastest_solution_tap_sequence, difficulty_estimate}`.

## Generator telemetry

The `generator_audit` Room table records for each generated board:
```
seed, generator_version, ruleset_version, target_bucket,
board_size, density, predicted_difficulty, solution_count,
generation_attempts, accepted_flag, timestamp
```

This data closes the loop between PCG and player outcomes in analytics.
