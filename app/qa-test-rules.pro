# AndroidX Test tracing references this compile-time-only annotation.
-dontwarn com.google.errorprone.annotations.MustBeClosed

# Instrumentation discovers JUnit tests and their rules through reflection.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-keep class com.spinbottle.truthdare.games.screens.**Test { *; }
-keep class org.junit.** { *; }
