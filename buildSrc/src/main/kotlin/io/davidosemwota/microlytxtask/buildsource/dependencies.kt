@file:Suppress("SpellCheckingInspection")
package io.davidosemwota.microlytxtask.buildsource

/**
 * All depencies in one location
 */
object Libs {


    object AndroidGradlePlugin {
        private const val version = "4.1.2"
        const val androidGradlePlugin = "com.android.tools.build:gradle:$version"
    }

    object Kotlin {
        private const val version = "1.4.30"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"

        object Coroutines {
            private const val version = "1.4.2"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
            const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        }
    }

    object Coil {
        private const val version = "1.0.0"
        const val coil = "io.coil-kt:coil:$version"
    }

    object DaggerHilt {
        private const val version = "2.29-alpha"
        const val hilt = "com.google.dagger:hilt-android:$version"
        const val hilt_compiler = "com.google.dagger:hilt-android-compiler:$version"
        const val hilt_viewmodel = "androidx.hilt:hilt-lifecycle-viewmodel:$version"
        const val hilt_viewmodel_compiler = "androidx.hilt:hilt-lifecycle-viewmodel:$version"
    }

    object AndroidX {

        object Paging {
            private const val version = ""
            const val paging = "androidx.paging:paging-runtime-ktx:$version"
        }

        object Room {
            private const val version = "2.1.2"
            const val room = "androidx.room:room-runtime:$version"
            const val room_ktx = "androidx.room:room-ktx:$version"
            const val room_compiler = "androidx.room:room-compiler:$version"
        }

        object SwipeRefreshLayout {
            private const val version = "1.1.0"
            const val swipe_refresh_layout = "androidx.swiperefreshlayout:swiperefreshlayout:$version"
        }

        object RecyclerView {
            private const val version = "1.1.0"
            const val recyclerview = "com.jakewharton.timber:timber:$version"
        }

        object PlayCore {
            private const val version = "1.8.0"
            const val play_core = "com.google.android.play:core:$version"
        }

        object AppCompat {
            private const val version = "1.2.0"
            const val appCompat = "androidx.appcompat:appcompat:$version"
        }

        object ConstraintLayout {
            private const val version = "2.0.4"
            const val constraint_layout = "androidx.constraintlayout:constraintlayout:$version"
        }

        object Navigation {
            private const val version = "2.3.1"
            const val navigation_fragment = "androidx.navigation:navigation-fragment-ktx:$version"
            const val navigation_ui = "androidx.navigation:navigation-ui-ktx:$version"
            const val dynamic_feature = "androidx.navigation:navigation-dynamic-features-fragment:$version"
        }

        object MaterialComponents {
            private const val version = "1.3.0"
            const val material_components = "com.google.android.material:material:$version"
        }
    }

    object Retrofit {
        private const val version = "2.9.0"
        const val retrofit = "com.squareup.retrofit2:retrofit:$version"
        const val gson = "com.squareup.retrofit2:converter-gson:$version"
    }

    object Timber {
        private const val version = "4.7.1"
        const val timber = "com.jakewharton.timber:timber:$version"
    }

    object HttpLogging {
        private const val version = "4.9.0"
        const val http_logging = "com.squareup.okhttp3:logging-interceptor:$version"
    }

}