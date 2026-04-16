// Top-level build file where you can add configuration options common to all sub-projects/modules.
// NOTE: AGP 9.0.1 bundles the Kotlin compiler — do NOT apply kotlin-android separately.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
}