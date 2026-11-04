"""Create a complete human-readable report from the public research snapshot."""

from __future__ import annotations

import json
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parent
PROJECT = ROOT.parent.parent
data = json.loads((ROOT / "public_market_review_summary.json").read_text(encoding="utf-8"))


def section(title: str, body: str) -> str:
    return f"\n\n{'=' * 88}\n{title}\n{'=' * 88}\n{body.strip()}\n"


parts = []
parts.append("""SPIN BOTTLE / SPINSCORE — DEEP PRODUCT, COMPETITOR, REVIEWS & ASO AUDIT
As of: 02 November 2026 (India) | Target: Android / Google Play
Project: P:\\Projects\\spinscore
Primary artifact: SpinBottle_Complete_Product_Market_ASO_Research_2026-11-02.txt

RESEARCH VERDICT: PROMISING FUNCTIONAL PROTOTYPE; NOT YET PROVEN IN A REAL MARKET.
Provisional overall score: 6.8 / 10. Recommended release disposition: PRIVATE BETA.

Evidence labels in this report:
[CODE] inspected current repository source; [RUN] executed on 02 Nov 2026;
[IMAGE] visual assessment of dated Oct 12 screenshot; [STORE] public listing data;
[REVIEW] sampled user review signals; [ESTIMATE] expert judgment, not measured telemetry;
[PENDING] requires physical device, Play Console, or actual user testing.

This research intentionally focuses on age-appropriate party and family game
comparisons. It does not include harmful challenge instructions or age-restricted
game content. Public reviews were analyzed by rating without redistributing
third-party review text or reviewers' personal information.
""")

parts.append(section("01 | THE PRODUCT TODAY — VERDICT", """
Product: 'Spin Bottle' (repository folder SpinScore), a local-first Kotlin/Compose
party game with Classic Truth or Dare, Quick Fire, Couples, Kids Safe, custom
question creation, mode/difficulty/packs, favorites, scores, short session recap,
optional local dare photos, settings, and a one-time supporter purchase. [CODE]

Repository applicationId: com.spinbottle.truthdare.games
Visible name: Spin Bottle; versionName: 1.0.0; versionCode: 1; minSdk 24;
targetSdk and compileSdk: 36; Kotlin/Compose/Material3; no ad or backend SDK. [CODE]

GOOD: Broad game-mode scope; offline play is a clear value proposition; local
privacy design is meaningful; restored session architecture is present; content
is structured into categories; 75 host unit tests passed; billing, backup,
data reset, and a PIN/content boundary have received engineering attention.

WEAK: It tries to serve multiple audiences in one funnel; home layout uses very
large imagery, a giant button, scattered small secondary actions and extensive
empty space on the dated screenshot. The current name is indistinct and highly
generic. Premium is mostly a voluntary supporter purchase. Core value is hard
to articulate against established games with 1M to 10M+ download bands.

UNVERIFIED: real-device usability, frame times, battery use, TalkBack, end-to-end
release minification, Play Billing purchase flow, and real user retention.
Play Console ownership/listing status and actual download count are unknown.

The codebase has many pre-existing uncommitted source changes. Do not reset,
overwrite, commit, or merge them as part of implementing this roadmap.
"""))

parts.append(section("02 | SCORES BY DIMENSION (0–10, PROVISIONAL)", """
Core idea / social game concept............. 7.3  Familiar group entertainment
Gameplay feature breadth................... 8.0  Several functional game modes
Game rules / interaction clarity........... 7.1  More steps than needed to begin
Visual identity & first impression......... 6.2  Colorful, but crowded and oversized
UI information hierarchy................... 5.9  CTA and secondary controls unbalanced
Ease of starting a game.................... 6.1  Setup -> mode -> pack -> difficulty
Question quality and replayability......... 7.0  Many local prompts, untested by users
Content boundaries and user comfort........ 7.3  PIN/skip framework; verify end-to-end
Reliability / correctness.................. 7.7  75 unit tests pass; device untested
Performance and battery.................... 5.9  Particle/animation cost not profiled
Accessibility and responsive layouts....... 6.6  Improvements present; manual QA due
Privacy and data minimization.............. 8.7  Offline-first, app-private proofs
Security and state migration............... 7.4  Substantial work; release validation due
Maintainability........................... 7.0  87 source Kotlin files; central state
Monetization / revenue model............... 4.8  Supporter purchase without strong value
Market differentiation.................... 4.7  Crowded, commoditized core mechanics
ASO / brand positioning.................... 4.4  Generic title; no proven listing
Growth / repeat-use strategy............... 4.8  No observed retention or experiments
Release readiness......................... 6.1  Build passes; Play QA incomplete

OVERALL SUBJECTIVE PRODUCT READINESS: 6.8 / 10 [ESTIMATE]
This is an expert rubric, not a mathematical user satisfaction score; do not
claim current product is 8.5/10 or 9.5/10 just because older plans target that.

Score conditions: code and the October 12 image inspected; host checks passed;
no connected Android device appeared in `adb devices` during this audit. Live
UI flow, release build on a device, and consumer validation did not occur.
"""))

parts.append(section("03 | EXECUTED ENGINEERING CHECKS AND PRODUCT EVIDENCE", """
Observed command: .\\gradlew.bat testDebugUnitTest lintDebug assembleDebug --console=plain
Result: BUILD SUCCESSFUL (6m 33s). [RUN]
Unit suite XML: 19 test suites, 75 tests, 0 failures, 0 errors, 0 skipped.
Lint report XML: 0 errors; 48 warnings and 2 hints. Main warning families:
UseKtx (24), GradleDependency (9), InlinedApi (5), ModifierParameter (4).
Debug APK exists, 18,991,787 bytes (~18.1 MiB). [RUN]
No connected test device; instrumentation and final Play release flow were NOT
run for this snapshot. Existing October logs/README are historical evidence.

Observed implementation locations:
  app/src/main/java/.../screens/HomeScreen.kt — home and CTA
  app/src/main/java/.../navigation/AppNavigation.kt — mode routing
  app/src/main/java/.../game/SessionEnvelope.kt — schema 3 session format
  app/src/main/java/.../data/GameSessionHolder.kt — durable state + restore
  app/src/main/java/.../couples/CouplesSessionRepository.kt — session storage
  app/src/main/java/.../data/ContentAccess.kt — audience access boundary
  app/src/main/java/.../data/PromptsDatabase.kt — questions and dares
  app/src/main/java/.../screens/PremiumScreen.kt — supporter product
  app/build.gradle.kts — Android/build/Billing configuration
  PRIVACY.md, README.md and AUDIT_FINDINGS.md — existing project records

Do not assume a passing unit test certifies final published functionality.
Prior October review found significant session, gating and navigation defects;
new code appears to address several, but device + minified QA still matter.
"""))

parts.append(section("04 | UI/UX DESIGN AUDIT AND REDESIGN SPECIFICATION", """
Reference image reviewed: audit/2026-10-12/updated-home.png [IMAGE; DATED]

What works: coherent dark backdrop; visible primary action; friendly secondary
mode button; fun party mood; basic contrast on main labels.

Problems in reference: large bottle illustration and oversized wordmark consume
the top; subtitle below also occupies a full block; bright gradients compete
for attention; 'Start Game' is huge relative to other actions; three low-level
menu tiles resemble equal priorities; large unused space under them; premium
star at upper right lacks a clear value proposition. Visual hierarchy becomes
especially awkward on short devices or at high font scale.

Suggested positioning: 'The quick offline question game for friends and family.'
Use one inclusive visual identity. A neutral spinning pointer / speech bubble
illustration can communicate the game without relying on celebration/drink imagery.

REDESIGNED HOME, top to bottom:
  1. Compact header: mark + product name; settings as labeled icon.
  2. Hero card: 'Ready for a game night?' and one-line social promise.
  3. Primary CTA: 'Quick Play' (launch safe default with 2 sample players or a
     minimal add-players step); secondary: 'Customize Game'.
  4. Two balanced cards: 'Friends & Family' and 'Timed Quick Fire'.
  5. Compact 'Resume last session' only when available.
  6. Quiet row: 'My questions', 'How to play', 'Settings'.
  7. No distracting always-running background motion when idle.

UI TOKENS PROPOSAL (not implemented): background #10121C; surface #1B2030;
text #F8FAFF; muted #B8C2D6; primary #7C6BFF; warm accent #FFB75E;
safe mode #79D4A5; divider #32394D. Use restrained gradients only on the
primary CTA and selection feedback; use 16–20dp corner radii consistently,
16dp edge margins, 8dp spacing grid, minimum 48dp touch targets, font scaling
up to 200%, and accessibility labels for icons.

REDESIGN EACH SCREEN:
  Setup: add names on one screen, persistent roster chips, inline duplicates error.
  Mode: four succinct cards, avoid 'select then continue' if one tap can route.
  Pack: show audience, sample question themes, estimated diversity, not vague heat.
  Game: one prominent spinner, clear player, clear Truth/Question and Dare/Task
        actions, visible Skip/Next, last-turn indicator, progress where relevant.
  Quick Fire: readable timer and pause controls; respect backgrounding.
  Kids: separate calming theme, clear parent boundary, safe exit route.
  Results: simple podium/score recap, replay CTA, share opt-in, clean end state.
  Premium: support benefits/limitations clearly; avoid confusing 'remove ads'
           messages where there is no advertising SDK.

VISUAL QA ACCEPTANCE: 320/360/393/600dp widths; light/dark contrast; landscape;
1.0/1.5/2.0 font scales; screen reader traversal; one-hand usability; reduced
motion; touch targets; keyboard inset behavior. Screenshot and user-test results
are required before raising the design score.
"""))

parts.append(section("05 | GAMEPLAY, CONTENT AND SAFETY ROADMAP", """
P0: Prioritize consent and comfort. Every question/task must be optional;
skip without penalty and avoid pressure from the app or other players. Avoid
dangerous physical actions, harmful stunts, humiliation, nonconsensual privacy
requests or any task that asks for intimate pictures. This is critical for
family-safe positioning and protects users of different ages.

P0: Test strict separation between family/general prompt pools and any
restricted content. Verify all entry paths: home, resume, rematch, custom cards,
favorites, shared session, background restart, direct navigation, difficulty,
and pack selection. Make target audience and store listing match actual content.

P1: Replace repeated or unbalanced question selection with measurable rules:
track prompt IDs seen in current session; exclude recently seen until exhausted;
balance categories, difficultly and truth/task types; offer a clear reset.
Measure duplicate rate, skip rate, and 'not suitable' reports in beta.

P1: Add 'Play again with same group' preserving selected settings; allow opt-in
custom family-safe question packs and a local import/export format with on-device
validation. Never import arbitrary unmoderated remote prompts into Kids mode.

P1: Add India-focused English and Hindi translations only after professional
review; don't announce languages that aren't fully supported. Validate fonts,
phrase length and safety meaning across locales.

P2: Optional 2-minute icebreaker, themed family-friendly rounds, seasonal
non-repeating question packs, and collaborative score goals. All modes should
remain playable offline and without creating an account.
"""))

parts.append(section("06 | GOOGLE PLAY MARKET: EVIDENCE AND METHOD", f"""
Live collection completed on: {data['collected_at_utc']} UTC.
Market setting: {data['market']}.
{data['total_analyzed_review_records']} public review records classified across
{len(data['catalog'])} competitor or adjacent-party listings, 1-to-5 stars.

Collection method: google_play_scraper read-only requests with
lang='en', country='in', sort=NEWEST, filter_score_with=1..5,
maximum 50 samples per star for each listing. All calls completed without
reported API errors. Only samples visibly returned were counted. [REVIEW]

WARNING: Rating-stratified pulls intentionally over-sample rare 1- and 2-star
reviews. These counts are NOT the actual star distribution of any app and
cannot establish market share, app quality, retention or revenue. English-language
India-specific reviews are not a global representative sample. Some metadata
is missing or region-dependent. Store install badges (1M+, etc.) are public
LOWER-BOUND BANDS, not exact download numbers, active users or MAU.

Review signals are simple keyword/topic matches. A phrase in a 1-star review
may be criticism, while a phrase in a 5-star review may be praise or still a
complaint. They are directional evidence, not validated topic sentiment.
No review text, user names, reviewer IDs or sensitive personal information
were copied into the resulting dataset.
"""))


rows = [
    "Abbrev | Age rating | Installs (Play lower bound) | Store stars (en-IN) | Rating count | Samples [1,2,3,4,5] | Business model",
    "-" * 140,
]
for x in data["catalog"]:
    a = data["aggregates"][x["alias"]]
    stars = ",".join(str(a["stars"][str(i)]) for i in range(1, 6))
    score = f"{x['store_rating']:.2f}" if isinstance(x["store_rating"], (int, float)) else "n/a"
    rating_count = x["rating_count"] if x["rating_count"] is not None else "n/a"
    model = "/".join(s for s, ok in (("ads", x["contains_ads"]), ("IAP", x["offers_iap"])) if ok) or "unconfirmed"
    rows.append(f"{x['alias']} | {x['content_rating']} | {x['installs_floor_display']} | {score} | {rating_count} | [{stars}] | {model}")
rows.append("\nListings / packages for source verification (not an endorsement):")
for x in data["catalog"]:
    rows.append(f"{x['alias']}: {x['title']} | {x['developer']} | {x['store_url']}")
rows.append("\nDirect game competitors: bhrigu, xynapse, korobov, idayrus, kordia.")
rows.append("Adjacent substitutes competing for party-game attention: what_am_i_charades, klikapp_charades, would_you_rather.")
parts.append(section("07 | COMPETITOR BENCHMARK — DOWNLOAD BANDS, RATINGS, MODELS", "\n".join(rows)))

star_counts = Counter()
low = Counter()
high = Counter()
for alias, agg in data["aggregates"].items():
    star_counts.update({i: agg["stars"][str(i)] for i in range(1, 6)})
    low.update(agg["themes_low_stars"])
    high.update(agg["themes_high_stars"])

review_detail = [
    "ACTUAL SAMPLED COUNTS (not population distribution):",
    *(f"  {i} star: {star_counts[i]} analyzed" for i in range(1, 6)),
    "  Low 1–2 stars:  %s records" % (star_counts[1] + star_counts[2]),
    "  Mixed 3 stars:    %s records" % star_counts[3],
    "  High 4–5 stars: %s records" % (star_counts[4] + star_counts[5]),
    "",
    "THEMES MENTIONED IN 1–2 STAR REVIEWS (mentions, not proven grievances):",
    *(f"  {name:28} {count:4}" for name, count in low.most_common()),
    "",
    "THEMES MENTIONED IN 4–5 STAR REVIEWS (mentions, not proven praise):",
    *(f"  {name:28} {count:4}" for name, count in high.most_common()),
    "",
    "A STAR-BY-STAR READING GUIDE:",
    "1 star: investigate severe ad interruption, unfun/repeating questions, failures and broken flows.",
    "2 stars: look for one or two unmet expectations such as quality or controls.",
    "3 stars: mixed impressions; focus on small fixes and clear value communication.",
    "4 stars: understand strengths worth preserving and friction preventing 5 stars.",
    "5 stars: core fun, group compatibility and enjoyable short sessions recur.",
    "These are general interpretations informed by keyword counts, NOT direct quotations.",
]
parts.append(section("08 | REVIEW STUDY — 1/2/3/4/5 STARS AND LIKES / DISLIKES", "\n".join(review_detail)))

per_app = []
for x in data["catalog"]:
    a = data["aggregates"][x["alias"]]
    lo = ", ".join(f"{k} ({v})" for k, v in sorted(a["themes_low_stars"].items(), key=lambda z: -z[1])[:4]) or "insufficient low-star signal"
    hi = ", ".join(f"{k} ({v})" for k, v in sorted(a["themes_high_stars"].items(), key=lambda z: -z[1])[:4]) or "insufficient high-star signal"
    per_app.append(f"{x['alias']} — {x['title']}\n  Retrieved: {a['sample_total']} | low-star keyword topics: {lo}\n  high-star keyword topics: {hi}")
per_app.append("\nIMPORTANT: The two low-volume cross-language direct competitors and the small adjacent title do not supply enough English reviews for reliable theme comparison. Review volume and locale limitations are explicit.")
parts.append(section("09 | EACH COMPETITOR — RATING-SEGMENTED REVIEW SIGNALS", "\n\n".join(per_app)))

parts.append(section("10 | PRODUCT OPPORTUNITIES FROM THE REVIEW STUDY", """
1. Few interruptions: Make the complete safe core experience playable without
   interruptions during a round. Ads are mentioned in 62 low-star samples.
2. Fresh content: Avoid recent repeats and provide high-quality curated
   categories. Prompt quality (42) and repetition (24) appear in low stars.
3. Family/group joy: Group/family is mentioned in 43 high-star samples and fun
   in 95. Optimize for shared laughter and easy turn-taking.
4. Clear controls: A discoverable skip, visible next player, good type scale and
   explicit round ending reduce friction on one shared phone.
5. Pricing transparency: Paywalls/pricing appeared in 20 low-star samples;
   a supporter purchase needs clear optional value and no misleading promises.
6. Customization: Choice/customization appeared in both low and high reviews;
   test whether groups want custom question packs and filtering, but do not
   mistake mentions for precise prevalence.
7. Test crash paths: Bugs/freezes appeared in 16 low-star samples. Run cold
   start, background resume, rotation, low-memory and minified release tests.

Competitive thesis: beat alternatives on immediate setup, thoughtful safe
prompts, offline privacy, and calm visual clarity. Bigger prompt count alone
will not be a defensible moat.
"""))

parts.append(section("11 | ASO — BRAND, NAME, KEYWORDS, PACKAGE NAME", """
Current Play-facing name: Spin Bottle [CODE]. Problem: generic, duplicates
common search intent and has weak recall. The repository nickname 'SpinScore'
also collides with an existing Google Play table-tennis product under that
title (tech.spirart.spinscore); avoid a standalone 'SpinScore' relaunch.

PRIMARY PROVISIONAL BRAND: JoyRounds
Recommended store title: JoyRounds: Truth or Dare
Reason: recognizable searchable phrase + distinctive front-end brand; under
Play's 30-character title cap. No identical title surfaced in a limited web
check, but trademark, domain, social handles, and Play Console package uniqueness
remain unverified; no availability guarantee.

ALTERNATIVE NAMES FOR TESTING:
  Truth or Dare: JoyRounds
  Party Circle: Truth or Dare
  Spin Bottle: Truth or Dare (high relevance, much weaker differentiation)
  Truth or Dare: Group Night (generic fallback)
Do not name the app an unverified '#1', 'Top', 'Best', or 'Free' title.

PACKAGE-NAME DECISION:
  Existing ID: com.spinbottle.truthdare.games — keep if the Play app has EVER
  been published under that package; Android package identity is not a marketing
  title, and changing it requires a separate app listing/migration.
  If definitively unpublished and you intentionally create a new app identity:
       com.joyrounds.truthordare  [candidate; no uniqueness guarantee]
       com.joyrounds.party       [candidate; no uniqueness guarantee]
  Domain ownership and trademark clearance are separate checks. Do not change
  build.gradle.kts during the research stage.

KEYWORD CLUSTERS TO VERIFY WITH PLAY CONSOLE SEARCH-TERM DATA:
  Primary: 'truth or dare', 'spin the bottle', 'party game'.
  Secondary: 'games with friends', 'group party game', 'offline party game'.
  Differentiator: 'family game night', 'custom questions', 'quick party rounds'.
  Long-tail hypotheses: 'truth or dare offline with friends',
  'group question game without internet', 'party games for family gatherings'.
  India expansion hypotheses: 'truth dare Hindi', 'party game Hindi' ONLY after
  Hindi support is fully implemented and QA tested.

Keyword volume/difficulty cannot be truthfully quantified from this public
sample; verify through Play Console acquisition queries and A/B experiments.
Google explicitly caps the title at 30 characters and the short description
at 80; full description limit is 4,000 characters. No keyword stuffing.
"""))

title = "JoyRounds: Truth or Dare"
short = "Play offline Truth or Dare with friends, quick rounds and custom questions."
assert len(title) <= 30 and len(short) <= 80

parts.append(section("12 | DRAFT READY-TO-EDIT STORE COPY (DO NOT POST UNTESTED CLAIMS)", f"""
TITLE ({len(title)} / 30):
{title}

SHORT DESCRIPTION ({len(short)} / 80):
{short}

FULL DESCRIPTION (starter draft; verify features against release build):

Make your next game night easy. Gather your friends or family, add player names,
choose a mode, and take turns with fun questions and optional playful tasks.
JoyRounds brings a familiar group game to one phone, even when you're offline.

Start with Classic Truth or Dare, use Quick Fire for short timed rounds, or
create your own questions for the people you know. Keep favorites, switch packs,
and replay with your group. When available, resume a recent game after leaving
the app.

Planning a family gathering? A separate Kids Safe mode offers age-appropriate
prompts and parent controls. Every player can skip a question or task. The aim
is conversation, laughter, and comfortable play for everyone.

Your gameplay data stays on your device. Player names, local question history,
and game settings are stored locally; no account is required for the core game.
Optional photo features should be used only with everyone's permission.

Features:
- Play on a shared phone without an internet connection.
- Choose Classic, Quick Fire, and other available game modes.
- Add your own questions, select packs, and save favorites.
- Keep score, replay sessions, and resume where supported.
- Adjust motion, sounds, and haptics.

Gather your group and start a round.

CHECK BEFORE PUBLICATION: change 'JoyRounds' only after brand review; remove any
feature not verified end-to-end in the intended release; ensure the public
listing and content-rating questionnaire describe the same audience.
"""))

parts.append(section("13 | STORE CREATIVE PACKAGE — EVERYTHING TO PREPARE", """
Required / recommended deliverables:
  [ ] Launcher icon: adaptive foreground/background and monochrome variant.
  [ ] Google Play icon: 512x512 px, 32-bit PNG with alpha, <=1,024 KB.
  [ ] Feature graphic: 1024x500 px, JPG or 24-bit PNG, no alpha.
  [ ] At least 2 genuine screenshots; plan 6-8 polished phone screenshots.
  [ ] Suggested preview sequence: (1) party headline/Quick Play; (2) spinner
      gameplay; (3) fun question card; (4) offline play; (5) custom questions;
      (6) family-safe mode/parent settings; (7) scores/rematch; (8) preferences.
  [ ] Optional 15–30-second landscape/portrait gameplay trailer with captions.
  [ ] English (India) title/short/full descriptions and release notes.
  [ ] Hindi localization and its matching screenshots ONLY after full QA.
  [ ] Developer public contact address, support email and accessible help page.
  [ ] Live public privacy policy as normal accessible webpage (not PDF).
  [ ] Accurate Data Safety, content rating and target audience declarations.
  [ ] Tested signing/key custody, signed release AAB, license testing.
  [ ] Content ownership audit: every illustration, font, sound, and question.
  [ ] Tested accessibility names, small-screen screenshots, dark mode assets.

Creative guidelines: show real UI, not speculative screens; use short headline
overlays and strong contrast; communicate shared phone/offline/family modes.
Avoid copied competitor art, false rankings, misleading purchase guarantees,
age-inappropriate promotional imagery and aggressive keyword repetition.

Official image constraints and metadata guidance:
https://support.google.com/googleplay/android-developer/answer/9866151
https://support.google.com/googleplay/android-developer/answer/13393723
"""))

parts.append(section("14 | MONETIZATION — SUSTAINABLE WITHOUT SPOILING THE GAME", """
Today: Google Play Billing supporter one-time purchase; no ad SDK seen [CODE].
Do not advertise 'Remove ads' as a purchase benefit when none exist.

Suggested sustainable revenue model, in order:
  First: polished free offline safe core with a voluntarily purchased supporter
  badge or cosmetic themes, transparently priced.
  Next: optional curated family-friendly question packs only when value is clear.
  Avoid: intrusive interstitials during game rounds, aggressive subscription
  prompts, manipulative timers, surprise charges, or locking core safety controls.

Data you need before pricing: store listing views -> installs -> first game
completion -> next-week return; supporter screen opens -> verified purchases;
refund/support rates. Forecasts without acquisition data are guesses.

Low-risk first growth approach: small, consent-based beta groups, then
in-product optional feedback and Play review prompts at natural session ends.
Never incentivize five-star reviews or pressure family/kids users to rate.
"""))

parts.append(section("15 | IMPLEMENTATION BACKLOG — EXACT PRIORITIES AND ACCEPTANCE", """
P0 | RELEASE BLOCKERS (DO FIRST)
 A. Validate content audience/prompt isolation through every route including
    resume, favorites, custom questions, rematch and after process death.
    PASS: automated route/selection tests + manual screen flow show no leaks.
 B. Run minified QA/release persistence and billing tests on API 34/36 physical
    device or emulator; verify R8/Gson field compatibility across versions.
    PASS: saved session survives crash/force stop/update; correct turn returns.
 C. Finish Kids-safe exit, parent controls and child-appropriate disclosure.
    PASS: all exits from in-progress/completed/restored Kids game obey boundary.
 D. Test photo capture/storage/delete/share/permanent denial on real device.
    PASS: no orphan files, accidental share, or sensitive cloud backup.
 E. Finish Play Console production sign, content rating, Data Safety,
    privacy/support URLs, policy disclosures and install testing.
    PASS: Console validation accepted and manual checklist signed off.

P1 | GREAT FIRST RELEASE (NEXT)
 F. Rebuild home+setup+mode selection around quick, understandable entry.
    PASS: 80%+ of observed beta groups start a playable first round <=30 sec
    from app open (target to validate, not current measured result).
 G. Rebalance prompt engine to reduce same-session repetition.
    PASS: no repeats until reasonable eligible pool exhausted in repeat tests.
 H. Redesign game screen with clear player, action, skip, and next controls.
    PASS: 90%+ beta participants finish round without instructions (target).
 I. Reduce perpetual animation and profile frames/battery.
    PASS: documented median/p95 frame times, thermal and low-end smoke test.
 J. Keyboard/landscape/large font/TalkBack testing.
    PASS: full manual accessibility matrix with recorded issues fixed.

P2 | GROWTH AFTER RETENTION PROOF
 K. Create localized English/Hindi question packs with human safety QA.
 L. Optional easy question pack export/import with validation.
 M. Earned visual themes that don't interfere with accessibility.
 N. Seasonal packs only if players show need; avoid feature bloat.
 O. Store creative experiments and country-specific listings as traffic grows.

Do NOT prioritize online multiplayer, chat with strangers, AI-generated dares,
or a social feed without demonstrated user need and a new privacy/safety review.
"""))

parts.append(section("16 | 90-DAY EXECUTION AND MEASUREMENT PLAN", """
DAYS 01–14 / VERIFY FOUNDATIONS
  Snapshot existing branch and dirty worktree; assign each P0 issue; run release
  variant testing; recruit age-appropriate testers; finalize a safe content
  spec; finish user flows on actual devices. No paid acquisition yet.

DAYS 15–30 / MAKE FIRST SESSION EXCELLENT
  Compact home, Quick Play, clearer gameplay, no-repeat selection; 5–10 sessions
  of moderated usability testing with participants and guardian participation
  where appropriate. Measure open-to-first-round, round completion, skip rate,
  crash/ANR, usability issues and perceived fun (1–5).

DAYS 31–45 / STORE KIT AND CONTROLLED BETA
  Complete genuine screenshots, feature graphic, icon, short description,
  privacy URL, user support, license tests; run internal test. If your Google
  Play personal account was created after Nov 13, 2023, plan for 12 opted-in
  closed testers continuously for at least 14 days before production access.

DAYS 46–60 / IMPROVE RETENTION
  Fix the top three observed beta complaints; tune 15-minute game-night flow;
  trim slow animations; test English-language creative. Estimate next-day and
  7-day return only from users who consent to eligible measurement.

DAYS 61–90 / STAGED LAUNCH
  Publish after Console approval; run controlled staged rollout with tight
  stability monitoring, then A/B test one listing component at a time. Add
  another language only after full localization verification and demand.

SUGGESTED KPI TARGETS (HYPOTHESES, NOT CURRENT RESULTS):
  80% of first-time beta groups begin in <=30 sec; >=85% finish one round;
  <1% obvious duplicate prompt exposure in a short session; >=4.2/5 test
  session enjoyment; zero P0 safety failures; zero observed startup crashes.
  Monitor Google Play core vitals, accessibility, content reports and refunds.
  Treat all numeric targets as experiments; revise after real sample sizes.
"""))

parts.append(section("17 | LAUNCH CHECKLIST AND STORE POLICY SOURCES", """
  [ ] App title <=30 chars, short description <=80, full description <=4000.
  [ ] Minimum two screenshots, Play icon 512px, feature graphic 1024x500.
  [ ] SDK target >=36 for 2026 normal mobile submissions (current matches).
  [ ] Proper target-audience and content-rating questionnaire; Family policies
      apply if targeting age groups legally regarded as children.
  [ ] Public privacy URL globally reachable via HTTPS; privacy link in-app.
  [ ] Complete accurate Data Safety including app SDK and sharing behavior.
  [ ] Signed, reproducible AAB with supported architectures / page sizes.
  [ ] Production billing product availability and licensed purchase tests.
  [ ] No misleading advertisements for absent features or purchases.
  [ ] Parent controls verified after resume and every game termination path.
  [ ] Smoothness, background/foreground, font scale, TalkBack, dark theme.
  [ ] Closed testing and production-access requirements, if account applies.
  [ ] Human permissions/content review, assets licensing, release notes.

Google Play primary sources read for this audit:
  Store listing best practices:
    https://support.google.com/googleplay/android-developer/answer/13393723
  Metadata restrictions:
    https://support.google.com/googleplay/android-developer/answer/9898842
  Screenshot/icon/feature graphic specs:
    https://support.google.com/googleplay/android-developer/answer/9866151
  Target SDK (Aug 31, 2026 API 36 requirement):
    https://support.google.com/googleplay/android-developer/answer/11926878
  Target audience / families:
    https://support.google.com/googleplay/android-developer/answer/9867159
  Content rating:
    https://support.google.com/googleplay/android-developer/answer/9859655
  Data Safety form:
    https://support.google.com/googleplay/android-developer/answer/10787469
  Privacy policy preparation:
    https://support.google.com/googleplay/android-developer/answer/9859455
  New personal accounts' closed testing requirements:
    https://support.google.com/googleplay/android-developer/answer/14151465
  Android vitals and quality:
    https://support.google.com/googleplay/android-developer/answer/17492799
  Store listing A/B experiments:
    https://google.play/business/store-listing-experiments/
  Custom store listings:
    https://google.play/business/customstorelistings/
  Existing name collision ('SpinScore' table-tennis app):
    https://play.google.com/store/apps/details?id=tech.spirart.spinscore

The source pages and public listing data are time-sensitive. Recheck before
release. A limited web search is not a trademark or package-name clearance.
"""))

parts.append(section("18 | RESEARCH OUTPUTS, REPRODUCTION AND LIMITATIONS", """
Files created in competitor_analysis/deep_research_2026-11-02/:
  collect_public_signals.py — replayable public metadata + reviews sampler.
  public_market_review_summary.json — listing fields and aggregate keyword
      count by app and by 1–2 vs 4–5 star strata.
  rating_stratified_review_signals.csv — one row per sampled review including
      star, timestamp, thumbs-up, review-length and broad topic labels.
      The file deliberately excludes review bodies, reviewers and review IDs.
  make_full_report.py — reproduce this readable report from JSON summary.

Reproduce: run `python competitor_analysis/deep_research_2026-11-02/
collect_public_signals.py` and then `python competitor_analysis/
deep_research_2026-11-02/make_full_report.py` from the repo root.
It needs Python + google-play-scraper and network; results change over time.

RESEARCH LIMITS:
- Did not open Play Console or measure Spin Bottle's actual store metrics.
- Did not install or operate competitors; listing features are vendor claims.
- No user-level retention, conversion, revenue, TAM or organic keyword volume.
- English en-IN snippets may not represent global or local-language sentiment.
- Sparse star buckets are reported as retrieved; no invented counts.
- No review text redistributed; keyword matches are approximate sentiment cues.
- Current UI assessment is based on an October screenshot, not a fresh capture.
- No physical connected device; debug host builds don't prove release runtime.

FINAL DECISION:
Proceed with a focused, family-safe/offline private beta. Invest in faster
start, better questions, calmer visual design, repeat avoidance and verified
quality before adding more modes. Publish only when P0 and Play Console gates
are evidenced. The brand recommendation is provisional until trademark and
listing checks are complete.
"""))

output = PROJECT / "SpinBottle_Complete_Product_Market_ASO_Research_2026-11-02.txt"
output.write_text("".join(parts), encoding="utf-8")
print(f"CREATED: {output}")
print(f"SIZE: {output.stat().st_size} bytes")
print(f"REVIEWS: {data['total_analyzed_review_records']}; COMPETITORS: {len(data['catalog'])}")
print(f"ASO TITLE: {title!r} ({len(title)} chars); SHORT: {len(short)} chars")
