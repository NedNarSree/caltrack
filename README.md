# CalTrack 🥑

A modern, native Android calorie and nutrition tracking application built with **Kotlin** and **Jetpack Compose (Material 3)**.

Designed with a **Local-Only & Offline-First** philosophy — all user data, meals, macro metrics, and weight records are stored privately on-device using Room SQLite with zero cloud dependencies, accounts, or telemetry.

---

## ✨ Features

- **Local & Offline**: Powered by Room Database. No account creation, cloud sync, or telemetry required.
- **Modern Jetpack Compose UI**: Clean emerald theme (`#10B981`), rounded cards (16–20dp), subtle elevations, and macro color indicators (Protein: Blue, Carbs: Amber, Fat: Red).
- **Daily Dashboard**: Energy balance cards, macronutrient breakdown, remaining allowance, and logged meal timelines.
- **Snap & Log**: Quick meal logging and entry staging.
- **Continuous History**: Review past logs, macronutrient splits, and daily weigh-ins.
- **Health Metrics & BMI**: Live BMI computation (`weightKg / (heightM)²`) dynamically calculated against your profile.
- **Profile & Targets**: Configure height, weight, daily calorie allowance, and personal targets.

---

## 🛠 Tech Stack & Architecture

- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose with Material 3
- **Architecture**: MVVM with `ViewModel` and `StateFlow`
- **Database**: Room Database (SQLite) with Coroutines & Flow
- **Image Loading**: Coil Compose
- **Build System**: Gradle with Kotlin DSL (`build.gradle.kts`) & KSP

---

## 🚀 Building & Running

### Prerequisites
- JDK 17+
- Android SDK (API 34 compile, minSdk 24)

### Build Debug APK
```bash
./gradlew assembleDebug
```
The output APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`
