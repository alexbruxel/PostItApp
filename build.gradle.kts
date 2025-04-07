plugins {
//    alias(libs.plugins.android.application) apply false
//    alias(libs.plugins.kotlin.android) apply false

    id("com.android.application") version "8.2.2" apply false
//    id("org.jetbrains.kotlin.android") version "2.1.20" apply false

    kotlin("android") version "1.9.20" apply false

    id("com.google.devtools.ksp") version "1.9.21-1.0.16" apply false
}


task<Delete>("clean") {
    delete(rootProject.buildDir)
}