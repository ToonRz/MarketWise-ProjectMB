# MarketWise Project

Mobile app (Android) for tracking **crypto** and **stock** markets in one place, with dashboard views, detail screens, price alerts, and a home-screen widget.

## Features

- 📊 Dashboard with watchlist-style market view
- ₿ Crypto data (CoinGecko + Binance WebSocket real-time stream)
- 🏦 Stock detail data (Finnhub)
- 🔔 Price alert notifications (WorkManager)
- 🧩 Home-screen widget (Quick Pulse)
- 📈 Technical-indicator helpers and chart support (MPAndroidChart)

## Tech Stack

- **Kotlin** + Android SDK
- **MVVM** with ViewModel / LiveData
- **Retrofit + Gson + OkHttp** for networking
- **Room** for local persistence
- **WorkManager** for periodic/background tasks
- **Navigation Component** + Bottom Navigation

## Requirements

- Android Studio (latest stable recommended)
- JDK 17
- Android SDK:
  - `compileSdk = 36`
  - `targetSdk = 36`
  - `minSdk = 26`

## Setup

1. Clone repo
2. Open folder `MarketWiseProject` in Android Studio
3. Create/update `MarketWiseProject/local.properties` and add your API key:

```properties
FINNHUB_API_KEY=your_finnhub_api_key
```

4. Sync Gradle and run app on emulator/device

> `FINNHUB_API_KEY` is injected into `BuildConfig` via `app/build.gradle.kts`.

## Project Structure

```text
MarketWiseProject/
  app/
    src/main/java/com/example/marketwiseproject/
      data/
        api/
        db/
        models/
        repository/
      services/
      ui/
        dashboard/
        crypto/
        stock/
        widget/
      utils/
      MainActivity.kt
```

## Permissions Used

- `INTERNET`
- `ACCESS_NETWORK_STATE`
- `POST_NOTIFICATIONS`
- `WAKE_LOCK`

## Notes

- If `FINNHUB_API_KEY` is missing, stock API requests may fail or return empty/error results.
- Keep `local.properties` out of version control for security.

## License

No license file is currently defined in this repository.
