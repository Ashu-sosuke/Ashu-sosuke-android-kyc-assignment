# Proguard rules for DigitalBank KYC

# Room
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.indexing.IndexDescriptor

# Hilt / Dagger
-keep class dagger.hilt.android.internal.managers.** { *; }
-keep class * implements dagger.hilt.android.internal.managers.GeneratedComponentManager { *; }
-keep class * implements dagger.hilt.internal.GeneratedComponent { *; }
-keep class * implements dagger.hilt.internal.UnsafeCasts { *; }
-keep class * implements hilt_aggregated_deps.generator.AggregatedDeps { *; }

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

# Gson
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.TypeAdapter
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod

# Coil
-dontwarn coil.**
-keep class coil.** { *; }