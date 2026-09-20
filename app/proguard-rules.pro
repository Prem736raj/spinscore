# Spin Bottle release shrinker rules.
#
# Jetpack Compose, Billing and Coil ship consumer rules where required. Do not
# keep the entire libraries here; doing so defeats R8 optimization.

# Gson persists these local models without @SerializedName. Keep their field
# names stable so an app update can still read JSON written by older versions.
-keepclassmembers class com.spinbottle.truthdare.games.data.CustomPrompt {
    <fields>;
}
-keepclassmembers class com.spinbottle.truthdare.games.data.FavoritePrompt {
    <fields>;
}
-keepclassmembers class com.spinbottle.truthdare.games.data.PlayerProfile {
    <fields>;
}
-keepclassmembers class com.spinbottle.truthdare.games.data.DareProof {
    <fields>;
}
-keepclassmembers class com.spinbottle.truthdare.games.data.GameSessionHolder$SessionSnapshot {
    <fields>;
}
-keepclassmembers class com.spinbottle.truthdare.games.data.GameSessionHolder$StoredPlayer {
    <fields>;
}

# CustomPrompt stores these enums through Gson. Preserve enum member names to
# avoid changing the persisted wire format across minified releases.
-keepclassmembers enum com.spinbottle.truthdare.games.data.PromptItemType {
    *;
}
-keepclassmembers enum com.spinbottle.truthdare.games.data.PromptCategory {
    *;
}
-keepclassmembers enum com.spinbottle.truthdare.games.data.Difficulty {
    *;
}

# Retain fields that are explicitly annotated if annotations are introduced
# later, without keeping whole classes.
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
