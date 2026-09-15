"""
Google Play Store Review Fetcher for Competitor Analysis
Fetches reviews for Truth or Dare competitor apps
"""

import json
import os
from google_play_scraper import reviews, Sort
from datetime import datetime

# Competitor apps to analyze
APPS = {
    "cosmicode_tod": "pt.cosmicode.truthordare",
    "bhrigu_tod": "com.bhrigu.truthordare.party.games",
    "risingtech_tod": "com.therisingtechie.truthordare",
    "wheel_spinbottle": "com.Wheel.SpinTheBottle"
}

# Output directory
OUTPUT_DIR = "reviews_data"

def fetch_reviews_by_rating(app_id: str, app_name: str, rating: int, count: int = 1000):
    """
    Fetch reviews for a specific rating from Google Play Store
    
    Args:
        app_id: The package name of the app
        app_name: Friendly name for the app
        rating: Star rating (1-5)
        count: Number of reviews to fetch
    
    Returns:
        List of review texts
    """
    print(f"  Fetching {count} reviews with {rating}-star rating...")
    
    all_reviews = []
    continuation_token = None
    
    try:
        while len(all_reviews) < count:
            batch_count = min(200, count - len(all_reviews))  # API limit is 200 per request
            
            result, continuation_token = reviews(
                app_id,
                lang='en',
                country='us',
                sort=Sort.MOST_RELEVANT,  # Most helpful/relevant first
                count=batch_count,
                filter_score_with=rating,
                continuation_token=continuation_token
            )
            
            if not result:
                break
                
            # Extract only the review text
            review_texts = [r['content'] for r in result if r.get('content')]
            all_reviews.extend(review_texts)
            
            print(f"    Fetched {len(all_reviews)}/{count} reviews...")
            
            if continuation_token is None:
                break
                
    except Exception as e:
        print(f"    Error fetching reviews: {e}")
    
    return all_reviews[:count]

def fetch_all_reviews_for_app(app_id: str, app_name: str):
    """
    Fetch reviews for all ratings (1-5 stars) for an app
    
    Returns:
        Dictionary with rating as key and list of reviews as value
    """
    print(f"\n{'='*60}")
    print(f"Fetching reviews for: {app_name} ({app_id})")
    print(f"{'='*60}")
    
    reviews_by_rating = {}
    
    for rating in range(1, 6):
        reviews_list = fetch_reviews_by_rating(app_id, app_name, rating, count=1000)
        reviews_by_rating[f"{rating}_star"] = reviews_list
        print(f"  ✓ {rating}-star: {len(reviews_list)} reviews collected")
    
    return reviews_by_rating

def save_reviews(app_name: str, reviews_data: dict):
    """Save reviews to JSON file"""
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    
    filename = os.path.join(OUTPUT_DIR, f"{app_name}_reviews.json")
    
    # Add metadata
    output = {
        "app_name": app_name,
        "app_id": APPS[app_name],
        "fetched_at": datetime.now().isoformat(),
        "reviews": reviews_data,
        "summary": {
            f"{i}_star_count": len(reviews_data.get(f"{i}_star", [])) 
            for i in range(1, 6)
        }
    }
    
    with open(filename, 'w', encoding='utf-8') as f:
        json.dump(output, f, ensure_ascii=False, indent=2)
    
    print(f"  💾 Saved to {filename}")
    return filename

def main():
    print("="*60)
    print("Google Play Store Review Fetcher")
    print("Truth or Dare Competitor Analysis")
    print("="*60)
    
    saved_files = []
    
    for app_name, app_id in APPS.items():
        try:
            reviews_data = fetch_all_reviews_for_app(app_id, app_name)
            filename = save_reviews(app_name, reviews_data)
            saved_files.append(filename)
        except Exception as e:
            print(f"❌ Error processing {app_name}: {e}")
    
    print("\n" + "="*60)
    print("DONE! Review files saved:")
    for f in saved_files:
        print(f"  📁 {f}")
    print("="*60)

if __name__ == "__main__":
    main()
