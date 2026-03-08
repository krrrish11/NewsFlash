# 📰 NewsFlash

A modern Android news application that delivers real-time news headlines from around the world, built with Java in Android Studio.

---

## ✨ Features

- 🗂️ **Category Filtering** — Browse news by categories such as Technology, Sports, Business, Entertainment, Health, and Science
- 🔄 **Real-Time News** — Fetches live headlines using the [NewsAPI](https://newsapi.org/)
- 🖼️ **Image Loading** — Smooth article thumbnail loading powered by Glide
- 📡 **Retrofit2 Networking** — Clean, efficient HTTP client for API communication
- 📰 **Article Detail View** — Tap any headline to read the full article summary
- 🌐 **Open in Browser** — Redirect to the full article on the original source website

---

## 🛠️ Tech Stack

| Component        | Technology              |
|------------------|-------------------------|
| Language         | Java                    |
| IDE              | Android Studio          |
| Networking       | Retrofit2 + OkHttp      |
| Image Loading    | Glide                   |
| News Data Source | NewsAPI (`newsapi.org`) |
| Build System     | Gradle (Kotlin DSL)     |
| Min SDK          | Android 7.0 (API 24)    |

---

## 📂 Project Structure

```
NewsFlash/
├── app/
│   └── src/
│       └── main/
│           ├── java/          # Java source files (Activities, Adapters, Models, API)
│           ├── res/           # Layouts, drawables, strings, colors
│           └── AndroidManifest.xml
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio (Flamingo or newer recommended)
- Java 11+
- A free API key from [https://newsapi.org](https://newsapi.org)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/krrrish11/NewsFlash.git
   cd NewsFlash
   ```

2. **Open in Android Studio**
   - Launch Android Studio → `File` → `Open` → select the `NewsFlash` folder

3. **Add your NewsAPI key**
   - Open `app/src/main/java/.../ApiClient.java` (or wherever the base URL / API key is defined)
   - Replace the placeholder with your actual NewsAPI key:
     ```java
     private static final String API_KEY = "your_api_key_here";
     ```

4. **Build & Run**
   - Connect an Android device or start an emulator
   - Click **Run ▶** in Android Studio

---

## 📸 Screenshots

> _Add screenshots here to showcase the app UI_

| Home Screen | Category Filter | Article Detail |
|-------------|-----------------|----------------|
| _(screenshot)_ | _(screenshot)_ | _(screenshot)_ |

---

## 🔌 API Reference

This app uses the **NewsAPI** — a simple HTTP REST API for live news data.

- Base URL: `https://newsapi.org/v2/`
- Endpoint used: `top-headlines`
- Docs: [https://newsapi.org/docs](https://newsapi.org/docs)

> **Note:** The free tier of NewsAPI is limited to development use only (100 requests/day). For production, a paid plan is required.

---

## 📦 Dependencies

```kotlin
// Retrofit2
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// Glide
implementation("com.github.bumptech.glide:glide:4.16.0")

// OkHttp Logging (optional, for debug)
implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
```

---

## 🌐 Permissions

The following permissions are declared in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "Add some feature"`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👨‍💻 Author

**Krrish**
- GitHub: [@krrrish11](https://github.com/krrrish11)
