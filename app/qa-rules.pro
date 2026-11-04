# Keep the external test runner's tracing entry point and the public APIs used
# across the APK boundary. Private Gson DTO fields still rely on production rules.
-keep class androidx.tracing.** { *; }
# Player exposes this inline value class to the separate instrumentation APK.
-keep class androidx.compose.ui.graphics.Color { *; }
# Kotlin runtime methods called only by instrumentation (for example runBlocking)
# must remain callable from the separate test APK.
-keep class kotlin.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class com.spinbottle.truthdare.games.data.* { public *; }
-keep class com.spinbottle.truthdare.games.couples.CouplesSessionRepository { public *; }
-keep class com.spinbottle.truthdare.games.couples.CouplesPreferences { public *; }
-keep class com.spinbottle.truthdare.games.couples.CouplesSessionState { public *; }
