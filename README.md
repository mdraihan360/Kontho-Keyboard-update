# ⌨️ KonthoBoard AI

**সবচেয়ে উন্নত বাংলা + English AI কীবোর্ড**

[![Build APK](https://github.com/YOUR_USERNAME/KonthoBoardAI/actions/workflows/build.yml/badge.svg)](https://github.com/YOUR_USERNAME/KonthoBoardAI/actions)

---

## ✨ ফিচার সমূহ

| ফিচার | বিবরণ |
|-------|-------|
| ⌨️ বাংলা ফোনেটিক | Avro phonetic engine সহ সম্পূর্ণ বাংলা কীবোর্ড |
| 🔤 English QWERTY | Caps Lock, popup keys সহ |
| 🔢 Number & Symbol | দুটি আলাদা layer |
| 🤖 AI সাজেশন | বাংলা + English smart word prediction |
| 🎤 ভয়েস ইনপুট | বাংলা (bn-BD) + English (en-US) |
| 🎨 ৫টি থিম | Dark, Light, AMOLED, RGB Gaming, Glass |
| 📋 ক্লিপবোর্ড | OTP detection সহ |
| ⚙️ সেটিংস | থিম, ভাইব্রেশন, অটো-কারেকশন |
| 🔠 যুক্তবর্ণ | ৩০+ বাংলা conjunct characters |

---

## 🚀 APK ডাউনলোড করুন (GitHub Actions)

1. এই repo টি GitHub-এ push করুন
2. **Actions** ট্যাবে যান
3. **Build KonthoBoard AI APK** workflow রান হবে
4. **Artifacts** থেকে `KonthoBoardAI-debug.apk` ডাউনলোড করুন
5. মোবাইলে install করুন

---

## 📱 মোবাইলে ইনস্টল করার পর

1. অ্যাপটি খুলুন
2. **"কীবোর্ড সক্রিয় করুন"** বোতামে চাপুন
3. Settings থেকে KonthoBoard AI চালু করুন
4. **"ডিফল্ট হিসেবে সেট করুন"** বোতামে চাপুন
5. যেকোনো text field-এ KonthoBoard AI ব্যবহার করুন ✅

---

## 🏗️ Project Structure

```
KonthoBoardAI/
├── .github/workflows/build.yml     ← GitHub Actions (APK build)
├── app/src/main/
│   ├── AndroidManifest.xml         ← App permissions + IME service
│   ├── kotlin/com/konthoboard/ai/
│   │   ├── KonthoBoardIME.kt       ← Core IME Service ⭐
│   │   ├── SetupActivity.kt        ← Setup screen
│   │   ├── SettingsActivity.kt     ← Settings screen
│   │   ├── keyboard/
│   │   │   ├── KeyboardConstants.kt
│   │   │   ├── KeyboardLayout.kt   ← All key row definitions
│   │   │   └── KeyboardView.kt     ← Custom key rendering + touch
│   │   ├── bangla/
│   │   │   └── BanglaEngine.kt     ← Avro phonetic engine
│   │   ├── prediction/
│   │   │   └── PredictionEngine.kt ← AI word suggestions
│   │   └── theme/
│   │       └── ThemeManager.kt     ← 5 themes
│   └── res/
│       ├── layout/                 ← UI layouts
│       ├── xml/method.xml          ← IME subtype declaration
│       ├── xml/preferences.xml     ← Settings preferences
│       └── values/                 ← strings, colors, themes
└── build.gradle.kts
```

---

## ⚙️ Build করুন (Local)

```bash
git clone https://github.com/YOUR_USERNAME/KonthoBoardAI.git
cd KonthoBoardAI
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔧 Requirements
- Android 7.0+ (API 24+)
- minSdk: 24, targetSdk: 34
