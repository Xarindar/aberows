# Resources Checklist — Aberows

Asset and infrastructure needs, organized by category. Check off as acquired or produced.

---

## Art

### Arrow pieces (board)
- [ ] Idle arrow — all 4 headings (up/right/down/left)
- [ ] Selected arrow (pre-tap highlight state)
- [ ] Armed/ready arrow (post-tap, before release)
- [ ] Active arrow (in motion)
- [ ] Danger arrow (heading toward collision)
- [ ] Crashed arrow (collision state)
- [ ] Cleared arrow (exit/sink state)
- [ ] Arrow trail / motion blur effect
- [ ] Arrow animations: activate, travel, clear, crash

### Board mechanics pieces
- [ ] Static blocker tile
- [ ] Wall deflector tile (all 4 orientations)
- [ ] Rotator tile (clockwise / counterclockwise)
- [ ] Timed gate tile (open / closed states + cycle animation)
- [ ] Splitter tile
- [ ] Portal tile (enter / exit pair, color-coded)
- [ ] Lock tile (locked / unlocked states)
- [ ] Key tile
- [ ] Sink / goal tile (destination)
- [ ] Exit boundary indicator

### Board and environment
- [ ] Grid cell background (default)
- [ ] Grid cell highlight (path preview in assist mode)
- [ ] Board frame / border
- [ ] Board background (per chapter theme)
- [ ] Chapter 1–8 background/theme variants

### HUD elements
- [ ] Arrow count icon
- [ ] Collision heart icon (full / used / empty states)
- [ ] Pause button icon
- [ ] Settings icon
- [ ] Star icons (0 / 1 / 2 / 3 stars)
- [ ] Level complete overlay/panel
- [ ] Level failed overlay/panel
- [ ] Booster slot icons (if boosters added)

### UI / shell
- [ ] App icon (all Android density variants + Play Store 512×512)
- [ ] Splash / launch screen asset
- [ ] Main menu background
- [ ] Chapter select screen art / chapter card art (×8)
- [ ] Level select thumbnail style
- [ ] Settings screen icons
- [ ] Font choice (readability-first; license must cover commercial use)
- [ ] Button styles (primary / secondary / destructive)
- [ ] Modal/dialog styles

### Store and marketing
- [ ] Play Store feature graphic (1024×500)
- [ ] Play Store screenshots — phone (min 2, ideally 4–8)
- [ ] Play Store screenshots — tablet (if targeting tablets)
- [ ] Short promo video / gameplay trailer (optional but recommended)
- [ ] Pre-registration banner art

---

## Audio

### Sound effects (earcons)
- [ ] Arrow tap / select
- [ ] Arrow activate (release)
- [ ] Arrow in motion loop (optional subtle)
- [ ] Near-miss / close pass
- [ ] Collision impact
- [ ] Level cleared — 1 star
- [ ] Level cleared — 2 stars
- [ ] Level cleared — 3 stars (perfect)
- [ ] Level failed
- [ ] Chapter complete fanfare
- [ ] UI button tap
- [ ] Menu open / close
- [ ] Settings toggle
- [ ] Revive / hint used
- [ ] Ad reward received
- [ ] Purchase success

### Music
- [ ] Main menu / idle loop
- [ ] Gameplay ambient loop — low intensity (optional)
- [ ] Chapter intro sting (per chapter, optional)
- [ ] Daily challenge theme variant (optional)

### Audio format and implementation
- [ ] All SFX pre-decoded for `SoundPool` (short, < 1 MB each)
- [ ] Music delivered as OGG/MP3 for `MediaPlayer` or ExoPlayer
- [ ] `AudioAttributes.USAGE_GAME` applied to all game audio
- [ ] Audio focus handling tested (incoming call, other apps)

---

## Infrastructure

### Firebase project
- [ ] Firebase project created and linked to Android app
- [ ] Firebase Analytics enabled
- [ ] Firebase Crashlytics enabled
- [ ] Firebase Performance Monitoring enabled
- [ ] Firebase Remote Config enabled + default config JSON committed to repo
- [ ] Firebase App Distribution set up for internal distribution
- [ ] Firebase A/B Testing enabled

### Google Play Console
- [ ] Developer account active
- [ ] App created in Play Console
- [ ] Play App Signing enrolled
- [ ] Internal testing track configured
- [ ] Data safety section completed (before any public track)
- [ ] Privacy policy URL added

### Google AdMob
- [ ] AdMob account created
- [ ] App registered in AdMob
- [ ] Rewarded ad unit created (revive placement)
- [ ] Rewarded ad unit created (hint placement)
- [ ] Rewarded ad unit created (daily challenge entry)
- [ ] Test ad unit IDs in debug build, live IDs behind Remote Config
- [ ] Consent / UMP SDK integrated

### Google Play Billing
- [ ] Billing client library added
- [ ] Remove-ads one-time SKU created in Play Console
- [ ] Hint pack consumable SKU(s) created
- [ ] Billing integration tested on closed track (billing APIs require published track)

### CI / CD
- [ ] GitHub repository created
- [ ] GitHub Actions workflow: build + unit tests on push
- [ ] GitHub Actions workflow: instrumented tests on PR
- [ ] Gradle build caching configured
- [ ] Fastlane (or equivalent) for App Distribution upload
- [ ] Macrobenchmark job in CI
- [ ] Branch protection on `main` (require passing CI)
- [ ] GitHub Milestones created (see ROADMAP.md)

### Android project scaffolding
- [ ] Kotlin + Compose project initialized
- [ ] Module split: `:app` and `:core` confirmed
- [ ] proguard/R8 rules stubbed
- [ ] API 35 target + edge-to-edge insets applied
- [ ] Compose BOM pinned
- [ ] Room + DataStore dependencies added
- [ ] WorkManager dependency added
- [ ] Baseline Profiles Gradle plugin added

---

## Design / Production tools

- [ ] Figma (or equivalent) workspace set up for wireframes + UI states
- [ ] Vector asset tool (Illustrator / Affinity Designer) licensed for commercial use
- [ ] Asset export naming convention documented (density suffixes, drawable naming)
- [ ] Audio tool licensed (DAW or SFX library with commercial license)
- [ ] Music license confirmed (original or licensed loop — verify commercial use terms)
- [ ] Design system / component library started in Figma
- [ ] Board grammar style guide (shared rules so new mechanic tiles inherit look)

---

## Legal / Compliance

- [ ] App name trademark search done
- [ ] Privacy policy written and hosted at public URL
- [ ] Terms of service written (if needed for accounts / leaderboards)
- [ ] Data safety declaration completed in Play Console
- [ ] AdMob / UMP consent flow implemented and tested in EEA/UK
- [ ] COPPA review (is the game directed at children?)
- [ ] All music and SFX licenses reviewed — commercial release permitted
- [ ] All font licenses reviewed — commercial app use permitted
- [ ] Open source dependency license audit (no GPL conflicts)

---

## Level design

- [ ] FTUE levels (hand-authored) — minimum 10
- [ ] Chapter 1 gateway level (hand-authored)
- [ ] Chapter 2–8 gateway levels (hand-authored, one each)
- [ ] Generator seed bible: curated daily seeds for first 90 days post-launch
- [ ] Golden board suite: at least one board per mechanic interaction for CI regression
