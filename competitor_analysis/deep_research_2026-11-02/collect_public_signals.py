"""Read-only Google Play public metadata + rating-stratified review signals.

Samples review text temporarily to classify broad topics but intentionally does
not reproduce third-party review bodies, reviewer names, or identifiers.
Outputs a reproducible per-review signal index and aggregate metadata.
"""

from __future__ import annotations

import csv
import json
import re
from collections import Counter
from datetime import datetime, timezone
from pathlib import Path

from google_play_scraper import Sort, app, reviews


ROOT = Path(__file__).resolve().parent
LOCALE = {"lang": "en", "country": "in"}
SAMPLE_PER_STAR = 50
COMPETITORS = {
    "bhrigu_truth_or_dare": "com.bhrigu.truthordare.party.games",
    "xynapse_truth_or_dare": "com.xynapse.truth_or_dare",
    "korobov_spin_bottle": "com.truth_or_dare.spin_the_bottle",
    "idayrus_truth_or_dare": "com.idayrus.truth_or_dare_indonesia",
    "kordia_truth_or_dare": "com.kordia.truthdare",
    "what_am_i_charades": "com.twominds.HeadsUpCharadas",
    "klikapp_charades": "com.klikapp.charades2",
    "would_you_rather": "com.gtm.wouldyourather",
}

THEMES = {
    "ads": r"\bads?\b|advertis|commercials?",
    "paywall_or_price": r"\bpay\b|paid|money|price|purchase|premium|subscription|locked|unlock",
    "bugs_or_crashes": r"bug|crash|freez|stuck|lag|glitch|doesn.t work|not working|broken|loading",
    "repetitive_content": r"repetit|repeat|same question|same dare|boring|too few|not enough|limited|few question",
    "prompt_quality": r"question|challenge|dare|truth|card|prompt|topic|word|deck",
    "fun_or_laughter": r"\bfun\b|funny|laugh|enjoy|entertain|great time|amazing game",
    "group_or_family": r"friend|family|party|group|kid|children|sibling|school|together",
    "design_or_usability": r"design|ui\b|interface|graphic|screen|layout|button|easy|simple|hard to use|look",
    "offline_or_network": r"offline|internet|wifi|network|connection",
    "choice_or_customization": r"custom|option|choose|category|categories|mode|personaliz|filter",
    "spin_or_random": r"bottle|spin|random|select|turn|point|rotation",
    "language_or_translation": r"language|english|hindi|translate|translation|spanish|arabic|portuguese",
    "privacy_or_permissions": r"privacy|permission|camera|data|tracking",
    "short_generic": r"^(good|nice|bad|ok|okay|great|wow|love it|worst|best|fine|cool|fun|super)[.!\s]*$",
}


def classify(text: str) -> list[str]:
    t = " ".join(text.lower().split())
    return [name for name, pattern in THEMES.items() if re.search(pattern, t)]


def collect():
    collected_at = datetime.now(timezone.utc).isoformat()
    catalog = []
    signals = []
    errors = []
    for label, package in COMPETITORS.items():
        try:
            item = app(package, **LOCALE)
            entry = {
                "alias": label,
                "package": package,
                "title": item.get("title"),
                "developer": item.get("developer"),
                "store_rating": item.get("score"),
                "rating_count": item.get("ratings"),
                "text_review_count": item.get("reviews"),
                "installs_floor_display": item.get("installs"),
                "installs_floor": item.get("minInstalls"),
                "genre": item.get("genre"),
                "content_rating": item.get("contentRating"),
                "contains_ads": item.get("containsAds"),
                "offers_iap": item.get("offersIAP"),
                "last_updated": item.get("updated"),
                "store_url": "https://play.google.com/store/apps/details?id=" + package,
                "sample_counts": {},
            }
            # Avoid collecting public reviews for age-restricted listings.
            rating = (entry["content_rating"] or "").strip().lower()
            is_safe_sample = not any(
                restricted in rating
                for restricted in ("16+", "17+", "18+", "mature", "adults only")
            )
            if not is_safe_sample:
                entry["sampling_note"] = "Excluded age-restricted listing from review sample"
                catalog.append(entry)
                print(f"SKIP_REVIEW {label}: {entry['content_rating']}", flush=True)
                continue
            for stars in range(1, 6):
                try:
                    found, _ = reviews(
                        package, **LOCALE, count=SAMPLE_PER_STAR,
                        sort=Sort.NEWEST, filter_score_with=stars,
                    )
                    seen_ids = set()
                    added = 0
                    for r in found:
                        rid = r.get("reviewId", "")
                        if not rid or rid in seen_ids:
                            continue
                        seen_ids.add(rid)
                        body = r.get("content") or ""
                        if not body.strip():
                            continue
                        signal = {
                            "app_alias": label,
                            "star": stars,
                            "posted_utc": r.get("at").isoformat() if r.get("at") else "",
                            "thumbs_up": r.get("thumbsUpCount", 0),
                            "review_length": len(body),
                            "themes": classify(body),
                        }
                        signals.append(signal)
                        added += 1
                    entry["sample_counts"][str(stars)] = added
                    print(f"{label} {stars} star: {added}", flush=True)
                except Exception as e:
                    entry["sample_counts"][str(stars)] = 0
                    errors.append({"app": label, "stars": stars, "error": str(e)})
                    print(f"ERROR {label} {stars} star: {str(e)[:160]}", flush=True)
            catalog.append(entry)
        except Exception as e:
            errors.append({"app": label, "error": str(e)})
            print(f"METADATA_ERROR {label}: {str(e)[:160]}", flush=True)

    aggregates = {}
    for label in COMPETITORS:
        subset = [x for x in signals if x["app_alias"] == label]
        aggregates[label] = {
            "sample_total": len(subset),
            "stars": {str(i): sum(x["star"] == i for x in subset) for i in range(1, 6)},
            "themes_all": dict(Counter(t for x in subset for t in x["themes"])),
            "themes_low_stars": dict(Counter(t for x in subset if x["star"] <= 2 for t in x["themes"])),
            "themes_high_stars": dict(Counter(t for x in subset if x["star"] >= 4 for t in x["themes"])),
        }
    result = {
        "collected_at_utc": collected_at,
        "market": "Google Play, en-IN English-language results",
        "requested_max_per_star_per_app": SAMPLE_PER_STAR,
        "sampling": "Newest reviews, filtered separately by rating 1 through 5. Non-random; each star intentionally over-sampled.",
        "data_limits": "Keyword mentions are approximate, not manually verified sentiment; samples cannot estimate actual star or theme prevalence.",
        "review_body_policy": "Review text, user names, review IDs deliberately not redistributed.",
        "total_analyzed_review_records": len(signals),
        "catalog": catalog,
        "aggregates": aggregates,
        "errors": errors,
    }
    (ROOT / "public_market_review_summary.json").write_text(json.dumps(result, indent=2, ensure_ascii=False), encoding="utf-8")
    with (ROOT / "rating_stratified_review_signals.csv").open("w", encoding="utf-8", newline="") as f:
        writer = csv.DictWriter(f, fieldnames=["app_alias", "star", "posted_utc", "thumbs_up", "review_length", "themes"])
        writer.writeheader()
        for row in signals:
            writer.writerow({**row, "themes": "|".join(row["themes"])})
    print("TOTAL", len(signals), "APPS", len(catalog), "ERRORS", len(errors), flush=True)


if __name__ == "__main__":
    collect()
