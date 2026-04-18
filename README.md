# MarketWise Project

> แอป Android สำหรับติดตามตลาดหุ้นและคริปโตในที่เดียว พร้อม Watchlist, หน้า Dashboard, รายละเอียดสินทรัพย์, การแจ้งเตือนราคา และวิดเจ็ตบนหน้าจอหลัก

## ภาพรวมโปรเจกต์

**MarketWise** คือแอปสำหรับผู้ใช้งานที่ต้องการดูภาพรวมราคา **Stocks + Crypto** แบบรวดเร็วในมือถือ โดยเน้น 3 อย่างหลัก:

1. ดูข้อมูลตลาดได้ง่าย (Dashboard + รายการติดตาม)
2. เข้าไปดูรายละเอียดรายสินทรัพย์ได้ทันที (Stock/Crypto Detail)
3. ตั้งระบบให้ช่วยแจ้งเตือนและอัปเดตอัตโนมัติ (Notification + Widget + Background Worker)

โครงสร้างแอปถูกออกแบบตามแนวทาง **MVVM** เพื่อแยกหน้าที่ระหว่าง UI, business logic, และ data layer ชัดเจน ทำให้ดูแลง่ายและต่อยอดฟีเจอร์ในอนาคตได้สะดวก

## Tech Stack ที่ใช้

### Language / Platform

- **Kotlin**
- **Android SDK**
- **Java 17**

### Architecture

- **MVVM (ViewModel + LiveData)**
- **Repository Pattern** สำหรับจัดการ data source
- **Navigation Component** + Bottom Navigation

### Networking / Data Sources

- **Retrofit** + **Gson Converter**
- **OkHttp** + Logging Interceptor
- **CoinGecko API** (ข้อมูลคริปโต)
- **Binance WebSocket** (ราคาเรียลไทม์)
- **Finnhub API** (ข้อมูลหุ้น)

### Local Storage / Background

- **Room Database** (เก็บ watchlist)
- **WorkManager** (งาน background เช่นแจ้งเตือน/อัปเดต widget)

### UI / Visualization

- **ViewBinding**
- **Material Components**
- **RecyclerView / CardView / SwipeRefreshLayout**
- **MPAndroidChart**
- **Coil** (image loading)

### Testing

- **JUnit4**
- **MockK**
- **kotlinx-coroutines-test**
- **AndroidX Test / Espresso**

## Features

- 📊 **Dashboard รวมภาพตลาด** พร้อม watchlist view
- ₿ **Crypto List + Detail** พร้อมข้อมูลราคาล่าสุด
- 🏦 **Stock List + Detail** สำหรับติดตามหุ้นรายตัว
- 🔎 **Search / Add Watchlist** ผ่าน bottom sheet
- 🔔 **Price Alert Notifications** ผ่าน WorkManager
- 🧩 **Home-screen Widget (Quick Pulse)** ดูข้อมูลแบบเร็วโดยไม่ต้องเข้าแอป
- 📈 **Technical Indicator helpers** สำหรับการวิเคราะห์เบื้องต้น

## โครงสร้างโปรเจกต์

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

## ความต้องการของระบบ (Requirements)

- Android Studio (เวอร์ชันล่าสุดที่เสถียร)
- JDK 17
- Android SDK
  - `compileSdk = 36`
  - `targetSdk = 36`
  - `minSdk = 26`

## วิธีเริ่มต้นใช้งาน (Quick Start)

1. Clone repository นี้
2. เปิดโฟลเดอร์ `MarketWiseProject` ด้วย Android Studio
3. สร้าง/แก้ไขไฟล์ `MarketWiseProject/local.properties`
4. ใส่ API key ของ Finnhub:

```properties
FINNHUB_API_KEY=your_finnhub_api_key
```

5. Sync Gradle และ Run บน Emulator/Device

> หมายเหตุ: `FINNHUB_API_KEY` จะถูกส่งเข้า `BuildConfig` ผ่าน `app/build.gradle.kts`

## Permissions ที่แอปใช้งาน

- `INTERNET`
- `ACCESS_NETWORK_STATE`
- `POST_NOTIFICATIONS`
- `WAKE_LOCK`

## จุดเด่นเชิงเทคนิค

- รองรับทั้ง REST API และ WebSocket ภายในแอปเดียว
- แยก layer ชัดเจน ช่วยลด coupling และทดสอบง่าย
- มี unit test สำหรับ repository หลัก
- รองรับการทำงานต่อเนื่องผ่าน WorkManager และ Widget

## แนวทางพัฒนาต่อ (Roadmap Suggestion)

- เพิ่มระบบ Sign-in / Sync watchlist ข้ามอุปกรณ์
- เพิ่มการตั้งแจ้งเตือนแบบ %change และ trailing alert
- รองรับ dark mode แบบปรับธีมอัตโนมัติ
- เพิ่ม analytics screen เช่น P/L summary และ sector allocation

## Troubleshooting

- ถ้าไม่ใส่ `FINNHUB_API_KEY` ฟีเจอร์หุ้นอาจโหลดไม่สำเร็จ
- ถ้า widget ไม่อัปเดต ให้ตรวจสอบ battery optimization ของเครื่อง
- ถ้า notification ไม่ขึ้น ให้ตรวจสอบสิทธิ์ `POST_NOTIFICATIONS` (Android 13+)

## License

ยังไม่มีการกำหนด License อย่างเป็นทางการใน repository นี้
