plugins {
    id("com.android.application") version "9.2.1" apply false
    id("org.jetbrains.kotlin.android") version "2.2.10" apply false  // Turunkan ke 1.9.0
    // HAPUS baris kapt di sini, karena kapt sudah include di kotlin plugin
}