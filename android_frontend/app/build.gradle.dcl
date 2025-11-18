androidApplication {
    namespace = "org.example.app"

    // Enable Compose using supported declarative block
    compose {
        enabled = true
    }

    dependencies {
        // Jetpack Compose BOM to align versions
        implementation(platform("androidx.compose:compose-bom:2024.10.01"))
        implementation("androidx.activity:activity-compose:1.9.3")
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.ui:ui-tooling-preview")
        implementation("androidx.compose.material3:material3")
        implementation("androidx.compose.material:material-icons-extended")
        implementation("androidx.navigation:navigation-compose:2.8.3")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
        implementation("androidx.core:core-ktx:1.13.1")
        // AppCompat for resource theme parent
        implementation("androidx.appcompat:appcompat:1.7.0")

        // Coroutines
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    }

    testing {
        dependencies {
            implementation("junit:junit:4.13.2")
        }
    }
}
