# Prometheus AgeTech ProGuard Rules

# Keep data models
-keep class com.prometheus.seniorcare.data.models.** { *; }

# Keep API client and data classes
-keep class com.prometheus.seniorcare.data.ApiClient { *; }
-keep class com.prometheus.seniorcare.data.ApiService { *; }
-keep class com.prometheus.seniorcare.data.LoginRequest { *; }
-keep class com.prometheus.seniorcare.data.LoginResponse { *; }
-keep class com.prometheus.seniorcare.data.UserResponse { *; }
-keep class com.prometheus.seniorcare.data.UserProfile { *; }
-keep class com.prometheus.seniorcare.data.DashboardResponse { *; }
-keep class com.prometheus.seniorcare.data.SOSRequest { *; }
-keep class com.prometheus.seniorcare.data.SOSResponse { *; }
-keep class com.prometheus.seniorcare.data.DailyCheckRequest { *; }
-keep class com.prometheus.seniorcare.data.DailyCheckResponse { *; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Compose
-dontwarn androidx.compose.**

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# Gson
-keep class com.google.gson.** { *; }
-keepattributes EnclosingMethod
