# Game Design Document — Aberows

## Core mechanic

The board is a **directed graph**. Stationary arrow pieces occupy nodes with fixed headings. Tapping an idle arrow activates it; it travels at constant speed along its heading until it:

- **Exits** the board boundary (cleared)
- **Reaches a sink/goal node** (cleared, bonus score)
- **Collides** with another active arrow (collision counted)

The player's problem is **ordering, timing, and interference management** — not aim or precision. All arrows move deterministically once tapped.

## Collision system (default mode)

| Collisions used | Result |
|---|---|
| 0 | Perfect clear — 3 stars |
| 1 | 2 stars |
| 2 | 1 star |
| 3 | Level failed |

- Each collision pauses, gives strong visual/haptic feedback, and clearly marks life loss.
- "Perfect clear" = zero collisions. The buffer lowers frustration; it does not erase mastery.
- **Precision mode**: 1 allowed collision.
- **Zen / Turn-Based Assist**: Reduced speed + tap-preview tools. Lowers barrier without changing puzzle topology.

## Game modes

### Campaign (primary)
- Short curated FTUE (hand-authored, ~5–10 levels).
- Hand-authored chapter intros for each new mechanic.
- Seeded procedural boards once grammar is established.
- Clear star-rating per level; stars unlock next chapter.

### Endless / Daily
- Daily seeded boards — same seed for all players on a given day.
- Streak play and challenge ladders.
- Secondary numeric score drives leaderboards.

## Numeric scoring formula

```
score = (completion_time_factor × w1)
      + (tap_efficiency × w2)          // taps relative to solver baseline
      + (collision_penalty × w3)
      + (chapter_modifier × w4)
```

Weights tuned per chapter and live-ops event via Remote Config.

## Chapter progression plan

| Chapter | New mechanics introduced |
|---|---|
| 1 — Basics | Arrows, movement, exits, three-collision rule |
| 2 — Obstacles | Static blockers, wall deflectors |
| 3 — Rotators | Rotator tiles that change heading on arrival |
| 4 — Timed Gates | Gates that open/close on a tick cycle |
| 5 — Splitters | One arrow becomes two on split tile |
| 6 — Portals | Teleport exits with heading preservation |
| 7 — Locks / Keys | Arrow must pass key tile before lock opens |
| 8 — Mixed | High-density boards combining prior mechanics |

Difficulty rises through **interaction density, order-dependence, and simultaneity** — not board size alone.

## HUD layout

```
┌─────────────────────────────────┐
│ [Arrows left]  [Collisions ●●○]  [⏸] │  ← top bar, edge-to-edge safe
├─────────────────────────────────┤
│                                 │
│         BOARD (full-width)      │
│                                 │
└─────────────────────────────────┘
│  [Booster tray — optional]      │  ← bottom cluster, only if boosters exist
```

- Meets Android edge-to-edge insets from day one (API 35 requirement).
- Each board arrow exposes semantic identity for accessibility services.
- Large touch targets, strong contrast, redundant directional cues (not color alone).

## Accessibility

| Feature | Implementation |
|---|---|
| Large touch targets | ≥ 48dp per WCAG / Android guidance |
| Strong contrast | 4.5:1 minimum; color-blind-safe palette |
| Redundant direction cues | Shape + color + animation — never color only |
| Reduced-motion mode | Toggle that slows / stops non-essential animation |
| Haptic / audio toggles | Separate controls in settings |
| Left-handed HUD option | Mirror booster/pause cluster |
| Assist mode | Pauses on tap, previews first path segment, speed slider |
| Compose semantics | Full labels on all interactive elements |

Design accessibility into sprint zero; do not bolt on later.

## Monetization

| Model | Verdict |
|---|---|
| Free + rewarded ads + remove-ads purchase | **Primary** — rewarded ads map naturally to revives/hints |
| Hint packs / cosmetic themes / chapter DLC | **Secondary** — aligns cost with value |
| Paid premium | Viable only with very strong brand/art positioning |
| Subscription | Do not lead with this; poor fit without strong daily content |
| Aggressive interstitials | Avoid — degrades puzzle tone, high churn risk |

Rewarded ad placements: extra-life on third collision, hint reveal, daily challenge entry. Never interrupt mid-puzzle with interstitials.

## Privacy and compliance

- Complete Play **Data safety** disclosure accurately.
- Wire Google UMP / consent-mode before ad requests in regulated regions.
- Keep data collection narrow: no location, minimal device identifiers, opt-in telemetry.
