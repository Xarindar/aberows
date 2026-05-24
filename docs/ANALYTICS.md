# Analytics Plan — Aberows

## Philosophy

Analytics should be **sparse, purposeful, and tied to the generator**. Every event should answer a design question. The right model captures `seed + generator_version + solver_properties + player_outcome` so PCG and design can be tuned against real play.

## Event schema

| Event | Key parameters | Why it matters |
|---|---|---|
| `session_start` | app_version, install_cohort, consent_state | Retention and release health |
| `tutorial_step` | step_id, completion_time, skip_flag | FTUE friction points |
| `level_generated` | seed, generator_version, board_size, density, predicted_difficulty | Generator auditability |
| `level_started` | seed, mode, chapter, retry_count | Funnel entry |
| `arrow_tapped` | arrow_id, tap_order, t_since_start | Order/timing analysis |
| `collision` | seed, collision_index, active_arrow_count, board_phase | Fairness diagnosis |
| `level_cleared` | clear_time, collisions_used, stars, efficiency_score | Difficulty calibration |
| `level_failed` | fail_reason, collisions_used, active_arrow_count_peak | Failure taxonomy |
| `ad_offer_shown` | placement, accepted_flag, reward_type | Ad placement tuning |
| `ad_reward_granted` | placement, reward_type | Monetization tuning |
| `purchase_completed` | sku, price_tier, source_screen | Revenue attribution |
| `accessibility_option_changed` | option_name, value | Accessibility usage and prioritization |
| `assist_mode_engaged` | level_seed, option | Difficulty signal |

## Weekly dashboards to build

- FTUE step completion and drop-off funnel
- Level retry distribution (by chapter and seed bucket)
- Star distribution per chapter
- Collision index histogram per chapter
- Solver-predicted vs observed difficulty correlation
- Monetization opt-in rate by cohort
- Accessibility option adoption over time

## Development instrumentation

- Use **Firebase DebugView** to validate all events during development — do not ship without verifying the event stream.
- Attach `generator_version` to every level event; when generator changes, all analytics comparisons need version filtering.
- `efficiency_score` on `level_cleared` = tap count / solver_baseline_tap_count (set at board generation time).

## Remote Config and A/B testing

Use Firebase Remote Config to tune without client updates:

| Parameter | Purpose |
|---|---|
| `ad_cadence_interstitial` | Time between ad offers |
| `revive_offer_collision_index` | Which collision triggers revive offer |
| `hint_price_coins` | Hint pack pricing |
| `ftue_sequence` | FTUE level order |
| `difficulty_bucket_thresholds` | PCG bucket boundaries |
| `daily_seed_source` | Remote vs local seeding for daily challenge |

A/B tests typically need longer collection windows before declaring a winner — plan for minimum 1–2 week test windows.

## Privacy stance

- Implement consent-mode / UMP before first analytics write in regulated regions.
- Do not collect location, advertising ID (without consent), or persistent device identifiers beyond what Firebase requires.
- Record `consent_state` on `session_start` to segment consented vs unconsented analytics.
- Complete Play **Data safety** declaration accurately before any public release.
