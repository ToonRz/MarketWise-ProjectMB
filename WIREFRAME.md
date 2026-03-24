# MarketWise Wireframe (Low-Fidelity)

เอกสารนี้สรุปโครงหน้าจอหลัก (wireframe) ของแอป **MarketWise** สำหรับใช้อ้างอิงตอนออกแบบ UI/UX และพัฒนาฟีเจอร์ต่อยอด

---

## 1) App Structure / User Flow

```text
[Launch App]
     |
     v
[MainActivity + Bottom Navigation]
     |
     +--> [Dashboard]
     |        |
     |        +--> Tap STOCK item  ---> [Stock Detail]
     |        |
     |        +--> Tap CRYPTO item ---> [Crypto Detail]
     |
     +--> [Stock Detail]
     |
     +--> [Crypto Detail]

(Background)
- Price Alert Worker
- Home Screen Widget (Quick Pulse)
```

---

## 2) Main Activity (Bottom Navigation)

```text
+--------------------------------------------------+
|                   MARKETWISE                     |
|--------------------------------------------------|
|                                                  |
|           [ Fragment Container / NavHost ]       |
|                                                  |
|                                                  |
|                                                  |
|--------------------------------------------------|
|  [🏠 Dashboard]   [📈 Stocks]   [₿ Crypto]        |
+--------------------------------------------------+
```

**แนวคิด UX:**
- Bottom Navigation อยู่ตลอดเพื่อสลับ context ได้เร็ว
- โซนกลางเป็น NavHost สำหรับแสดงแต่ละ Fragment

---

## 3) Dashboard Screen (`fragment_dashboard`)

```text
+--------------------------------------------------+
| Dashboard                                        |
|--------------------------------------------------|
| [ Search / Filter (optional future) ]            |
|--------------------------------------------------|
| Watchlist                                        |
| +----------------------------------------------+ |
| | SYMBOL | TYPE   | PRICE      | CHANGE      | |
| | AAPL   | Stock  | 189.24     | +1.32%      | |
| +----------------------------------------------+ |
| +----------------------------------------------+ |
| | BTC    | Crypto | 64,120.55  | -0.54%      | |
| +----------------------------------------------+ |
| +----------------------------------------------+ |
| | ETH    | Crypto | 3,210.11   | +2.06%      | |
| +----------------------------------------------+ |
|                                                  |
| (Tap list item => open detail by type)           |
+--------------------------------------------------+
```

**องค์ประกอบหลัก:**
- รายการ watchlist แบบ card/list item
- แสดง `symbol`, `type`, `current price`, `% change`
- แตะรายการเพื่อเปิดหน้ารายละเอียดเฉพาะประเภทสินทรัพย์

---

## 4) Stock Detail Screen (`fragment_stock_detail`)

```text
+--------------------------------------------------+
| <- Back        Stock Detail (AAPL)               |
|--------------------------------------------------|
| Current Price:      189.24                       |
| Day Change:         +1.32%                       |
| High / Low:         191.00 / 187.45              |
| Volume:             51.2M                        |
|--------------------------------------------------|
| [ Mini Chart / Placeholder ]                     |
|  ┌────────────────────────────────────────────┐  |
|  │             PRICE TREND LINE              │  |
|  └────────────────────────────────────────────┘  |
|--------------------------------------------------|
| Technical Indicators                             |
| - RSI: 58                                        |
| - MACD: +0.72                                    |
| - SMA(20): 186.90                                |
|--------------------------------------------------|
| [ Set Price Alert ]                              |
+--------------------------------------------------+
```

**องค์ประกอบหลัก:**
- กลุ่มข้อมูลราคาสำคัญ (current, high/low, change, volume)
- พื้นที่กราฟแนวโน้ม
- สรุป technical indicators
- ปุ่มตั้งแจ้งเตือนราคา

---

## 5) Crypto Detail Screen (`fragment_crypto_detail`)

```text
+--------------------------------------------------+
| <- Back        Crypto Detail (BTC)               |
|--------------------------------------------------|
| Current Price:      64,120.55                    |
| 24h Change:         -0.54%                       |
| 24h High / Low:     65,000 / 63,400              |
| Market Cap:         1.2T                         |
|--------------------------------------------------|
| [ Realtime Price Stream Indicator ]              |
|  Status: ● LIVE via WebSocket                    |
|--------------------------------------------------|
| [ Mini Chart / Placeholder ]                     |
|  ┌────────────────────────────────────────────┐  |
|  │             CRYPTO TREND LINE             │  |
|  └────────────────────────────────────────────┘  |
|--------------------------------------------------|
| [ Add to Watchlist ]   [ Set Alert ]             |
+--------------------------------------------------+
```

**องค์ประกอบหลัก:**
- เน้นค่า 24 ชั่วโมงและสถานะ real-time
- ปุ่ม action ที่ใช้บ่อย (watchlist / alert)

---

## 6) Watchlist Item Component (`item_watchlist`)

```text
+--------------------------------------------------+
| [ICON]  SYMBOL (NAME)                            |
|        Type: Stock/Crypto                        |
|        Price: 64,120.55                          |
|        Change: -0.54% (color-coded)              |
+--------------------------------------------------+
```

**Design notes:**
- สีเขียว/แดงสำหรับบวก/ลบ
- รองรับข้อมูลทั้ง stock/crypto component เดียวกัน

---

## 7) Home Screen Widget (`widget_quick_pulse`)

```text
+--------------------------------------+
|           Quick Pulse Widget         |
|--------------------------------------|
| BTC   64,120.55   -0.54%             |
| AAPL    189.24    +1.32%             |
|--------------------------------------|
| [Tap to open MarketWise]             |
+--------------------------------------+
```

**แนวคิด:**
- โชว์ข้อมูลสั้น กระชับ อ่านได้ทันที
- แตะเพื่อเปิดเข้าแอป

---

## 8) Suggested Next Step (Optional)

ถ้าจะต่อเป็น **mid-fidelity / hi-fidelity** แนะนำขั้นตอน:
1. กำหนด spacing scale และ typography token
2. ออกแบบสีสถานะราคา (gain/loss/neutral)
3. เพิ่ม loading, error, empty-state ของแต่ละหน้าจอ
4. ทำ interactive prototype (Figma)

