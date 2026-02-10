# Prometheus AgeTech ProGuard Rules

# Keep data models
-keep class com.prometheus.seniorcare.data.models.** { *; }

# Keep API client
-keep class com.prometheus.seniorcare.data.ApiClient { *; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Compose
-dontwarn androidx.compose.**
