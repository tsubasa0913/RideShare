import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
                }
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting
        val iosMain by creating {
            dependsOn(commonMain)
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        iosX64Main.dependsOn(iosMain)
        iosArm64Main.dependsOn(iosMain)
        iosSimulatorArm64Main.dependsOn(iosMain)

        // 全プラットフォームで共有する依存関係
        commonMain.dependencies {
            implementation("dev.gitlive:firebase-auth:1.13.0")
            implementation("dev.gitlive:firebase-firestore:1.13.0")
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        // Android固有の依存関係
        val androidMain by getting {
            dependencies {
                implementation("com.google.firebase:firebase-auth-ktx")
                implementation("com.google.firebase:firebase-firestore-ktx")
                // ▲▲▲ 不要なkotlinx-datetimeの行を削除しました ▲▲▲
            }
        }

        // iOS固有の依存関係
        iosMain.dependencies {
            // (empty for now)
        }
    }
}

android {
    namespace = "com.websarva.wings.android.rideshare"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

