plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    // Apply the ksp plugin
    alias(libs.plugins.google.devtools.ksp) apply false
}
