# MarketWise Wireframe Documentation 📱

> Wireframe สำหรับ MarketWise Android App - Crypto & Stock Tracking

---

## 🗺️ App Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              MarketWise App                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ╔═══════════════╗      ╔═══════════════╗      ╔═══════════════╗          │
│   ║  Dashboard    ║◄────►║   Crypto      ║◄────►║    Stock      ║          │
│   ║   (Home)      ║      ║    List       ║      ║    List       ║          │
│   ╚═══════╤═══════╝      ╚═══════╤═══════╝      ╚═══════╤═══════╝          │
│           │                      │                      │                  │
│           │                      │                      │                  │
│           ▼                      ▼                      ▼                  │
│   ╔═══════════════╗      ╔═══════════════╗      ╔═══════════════╗          │
│   ║  Watchlist    ║      ║  Crypto Detail  ║      ║  Stock Detail ║          │
│   ║    Detail     ║      ║    + Chart      ║      ║   + Chart     ║          │
│   ╚═══════════════╝      ╚═══════╤═══════╝      ╚═══════╤═══════╝          │
│                                   │                      │                  │
│                                   │                      │                  │
│                                   ▼                      ▼                  │
│                          ╔═══════════════╗      ╔═══════════════╗          │
│                          ║  Price Alert  ║      ║  Price Alert  ║          │
│                          ║  (BottomSheet)║      ║  (BottomSheet)║          │
│                          ╚═══════╤═══════╝      ╚═══════╤═══════╝          │
│                                  │                      │                  │
│                                  └──────────┬───────────┘                  │
│                                             │                               │
│                                             ▼                               │
│                                    ╔═══════════════╗                        │
│                                    ║ Add to WL   ║                        │
│                                    ║(BottomSheet) ║                        │
│                                    ╚═══════════════╝                        │
│                                                                             │
│   🧩 Quick Pulse Widget (Home Screen)                                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 📱 1. Dashboard / Home Screen

**Layout:** `fragment_dashboard.xml`

```
┌─────────────────────────────────────────────────────────────────┐
│                    MarketWise                                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Welcome back,                                                  │
│  Investor                                                       │
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║  📊 Market Pulse                                          ║  │
│  ║  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐  ║  │
│  ║  │ 🪙 BTC    │ │ 💎 ETH    │ │ 📈 AAPL   │ │ ⚫ BNB    │  ║  │
│  ║  │ $67,234   │ │ $3,456    │ │ $174.50   │ │ $589      │  ║  │
│  ║  │ ▲ +2.5%   │ │ ▲ +1.2%   │ │ ▼ -0.8%   │ │ ▲ +0.3%   │  ║  │
│  ║  └───────────┘ └───────────┘ └───────────┘ └───────────┘  ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                              ◄── Scroll ──►                     │
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║  Fear & Greed Index                                     ║  │
│  ║                                                         ║  │
│  ║  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━●━━  ║  │
│  ║  Extreme      Fear      Neutral     Greed    Extreme   ║  │
│  ║   Fear                                      Greed      ║  │
│  ║                                                         ║  │
│  ║                    Value: 75 (Greed)                      ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│  Your Watchlist                                          [🔍]  │
│  ─────────────────────────────────────────────────────────────  │
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║  ┌──────────┐                                            ║  │
│  ║  │ 🪙 Logo  │  BTC              Bitcoin              $67k ║  │
│  ║  │ (circle) │                                      ▲ 2.5% ║  │
│  ║  └──────────┘                                            ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║  ┌──────────┐                                            ║  │
│  ║  │ 💎 Logo  │  ETH              Ethereum            $3.4k ║  │
│  ║  │ (circle) │                                      ▲ 1.2% ║  │
│  ║  └──────────┘                                            ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║  ┌──────────┐                                            ║  │
│  ║  │ 📈 Logo  │  AAPL             Apple Inc.          $175 ║  │
│  ║  │ (circle) │                                      ▼ 0.8% ║  │
│  ║  └──────────┘                                            ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│                 ┌───────────────────────────┐                   │
│                 │      + Add Asset          │                   │
│                 └───────────────────────────┘                   │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│  [🏠 Home]      [💱 Crypto]      [🏦 Stocks]                     │
│   Selected                                                      │
└─────────────────────────────────────────────────────────────────┘
```

### Actions:
| Action | Result |
|--------|--------|
| Tap Market Pulse card | Go to Crypto/Stock Detail |
| Swipe down | Refresh data |
| Tap Watchlist item | Go to Asset Detail |
| Tap [+ Add Asset] | Open Search/Bottom Sheet |
| Tap [🔍] | Open Search |
| Tap Bottom Nav | Switch between Home/Crypto/Stocks |

---

## 💱 2. Crypto List Screen

**Layout:** `fragment_crypto_list.xml`

```
┌─────────────────────────────────────────────────────────────────┐
│  [←] Crypto Market                          [🔍] [⚙️]          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌───────────┐ ┌───────────┐ ┌───────────┐                     │
│  │ Trending🔥│ │  Top 100  │ │   DeFi    │                     │
│  │  Selected │ │           │ │           │                     │
│  └───────────┘ └───────────┘ └───────────┘                     │
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║ 🥇  ┌──────────┐                                         ║  │
│  ║     │ 🪙       │  Bitcoin                          $67,234║  │
│  ║     │  Logo    │  BTC                              ▲ 2.5% ║  │
│  ║     └──────────┘                                    [⭐+] ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║ 🥈  ┌──────────┐                                         ║  │
│  ║     │ 💎       │  Ethereum                         $3,456║  │
│  ║     │  Logo    │  ETH                              ▲ 1.2% ║  │
│  ║     └──────────┘                                    [⭐+] ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║ 🥉  ┌──────────┐                                         ║  │
│  ║     │ ⚫       │  Binance Coin                       $589 ║  │
│  ║     │  Logo    │  BNB                              ▼ 0.5% ║  │
│  ║     └──────────┘                                    [⭐+] ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║ #4  ┌──────────┐                                         ║  │
│  ║     │ ⚪       │  Solana                           $145  ║  │
│  ║     │  Logo    │  SOL                              ▲ 3.2% ║  │
│  ║     └──────────┘                                    [⭐+] ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│                           Pull ↓ to refresh                     │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Actions:
| Action | Result |
|--------|--------|
| Tap Tab (Trending/Top 100/DeFi) | Filter list |
| Tap [🔍] | Open search |
| Tap List item | Go to Crypto Detail |
| Tap [⭐+] | Add to watchlist (shows bottom sheet) |
| Swipe down | Refresh list |

---

## 📈 3. Crypto Detail Screen

**Layout:** `fragment_crypto_detail.xml`

```
┌─────────────────────────────────────────────────────────────────┐
│  [←]                                        [🔔] [⭐] [⋮]        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────┐                                                   │
│  │          │  BTC                                              │
│  │   🪙     │  Bitcoin                                        │
│  │  (60dp)  │                                                 │
│  └──────────┘                                                   │
│                                                                 │
│                         $65,240.50                              │
│                         ▲ +$1,240.50                            │
│                         ▲ +2.45% (24h)                          │
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║                                                           ║  │
│  ║                    [Line Chart]                           ║  │
│  ║                                                           ║  │
│  ║                    Price Trend                            ║  │
│  ║                                                           ║  │
│  ║     ─────────────────────────────────────────             ║  │
│  ║    /                                                       ║  │
│  ║   /                                                        ║  │
│  ║  /                                                         ║  │
│  ║                                                             ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│     [1D]   [1W]   [1M]   [3M]   [1Y]   [ALL]                   │
│      Selected                                                   │
│                                                                 │
│  Market Statistics                                              │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║                                                           ║  │
│  ║  Market Cap          Volume (24h)                          ║  │
│  ║  $1.2T               $35.4B                               ║  │
│  ║                                                           ║  │
│  ║  High (24h)          Low (24h)                            ║  │
│  ║  $66,102             $64,800                               ║  │
│  ║                                                           ║  │
│  ║  Circulating Supply  Max Supply                           ║  │
│  ║  19.5M BTC           21M BTC                               ║  │
│  ║                                                           ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│  Technical Indicators                                           │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║  Overall Signal: STRONG BUY                               ║  │
│  ║  ─────────────────────────────────────────────────────    ║  │
│  ║  RSI (14)                          65.4                   ║  │
│  ║  MACD                              Bullish                 ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Actions:
| Action | Result |
|--------|--------|
| Tap [←] | Back to previous screen |
| Tap [🔔] | Open Price Alert bottom sheet |
| Tap [⭐] | Toggle favorite |
| Tap [⋮] | Open options menu |
| Tap Timeframe (1D/1W/etc) | Change chart timeframe |
| Swipe left/right on chart | Pan chart |
| Pinch | Zoom chart |

---

## 🏦 4. Stock List Screen

**Layout:** `fragment_stock_list.xml`

```
┌─────────────────────────────────────────────────────────────────┐
│  [←] Stock Market                           [🔍] [⚙️]          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌───────┐ ┌───────┐ ┌───────┐ ┌───────┐                     │
│  │ US 🇺🇸 │ │ TH 🇹🇭 │ │ EU 🇪🇺 │ │ Asia  │                     │
│  │Selected│ │       │ │       │ │       │                     │
│  └───────┘ └───────┘ └───────┘ └───────┘                     │
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║     ┌──────────┐                                         ║  │
│  ║     │ 📈       │  AAPL           Apple Inc.        $174  ║  │
│  ║     │  Logo    │  NASDAQ                           ▲ 0.8%║  │
│  ║     └──────────┘                                    [⭐+] ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║     ┌──────────┐                                         ║  │
│  ║     │ 📈       │  TSLA           Tesla, Inc.       $242  ║  │
│  ║     │  Logo    │  NASDAQ                           ▼ 1.2%║  │
│  ║     └──────────┘                                    [⭐+] ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║     ┌──────────┐                                         ║  │
│  ║     │ 📈       │  MSFT           Microsoft       $423.50 ║  │
│  ║     │  Logo    │  NASDAQ                           ▲ 0.5%║  │
│  ║     └──────────┘                                    [⭐+] ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📊 5. Stock Detail Screen

**Layout:** `fragment_stock_detail.xml`

```
┌─────────────────────────────────────────────────────────────────┐
│  [←]                                        [🔔] [⭐] [⋮]        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────┐                                                   │
│  │          │  AAPL                                             │
│  │   📈     │  Apple Inc.                                       │
│  │  (60dp)  │  NASDAQ                                           │
│  └──────────┘                                                   │
│                                                                 │
│                         $175.50                                 │
│                         ▼ -$2.45 (-1.38%)                       │
│                         After: $174.80                          │
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║                                                           ║  │
│  ║                 [Candlestick Chart]                       ║  │
│  ║                                                           ║  │
│  ║                 Price / Volume                            ║  │
│  ║                                                           ║  │
│  ║              ▓▓▓▓▓▓                                       ║  │
│  ║              ▓▓▓▓▓▓▓▓▓▓                                   ║  │
│  ║              ▓▓▓▓▓▓▓▓▓▓▓▓▓                                ║  │
│  ║                                                           ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│     [1D]   [1W]   [1M]   [3M]   [1Y]   [5Y]                    │
│              Selected                                           │
│                                                                 │
│  Key Statistics                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║                                                           ║  │
│  ║  Open                $177.20                                ║  │
│  ║  High                $178.49                                ║  │
│  ║  Low                 $175.11                                ║  │
│  ║  Previous Close      $177.95                                ║  │
│  ║  ──────────────────────────────────────────────────────   ║  │
│  ║  Market Cap          $2.75T                                 ║  │
│  ║  Volume              58.6M                                  ║  │
│  ║  P/E Ratio           29.8x                                  ║  │
│  ║  Shares Outstanding  15.5B                                  ║  │
│  ║                                                           ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │                                                         │  │
│  │               [Loading Spinner]                         │  │
│  │                                                         │  │
│  │                  or                                     │  │
│  │                                                         │  │
│  │      ⚠️ Failed to load stock data                       │  │
│  │               [Retry]                                     │  │
│  │                                                         │  │
│  └─────────────────────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## ⭐ 6. Add to Watchlist (Bottom Sheet)

**Layout:** `bottom_sheet_add_watchlist.xml`

```
                    ═══════════════
                    Drag to dismiss
                ═══════════════════

┌─────────────────────────────────────────────────────────────────┐
│                     Add to Watchlist                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║                                                           ║  │
│  ║  🪙 Bitcoin (BTC)                                         ║  │
│  ║     $67,234.00       ▲ +2.54%                             ║  │
│  ║                                                           ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│  ─────────────────────────────────────────────────────────────  │
│                                                                 │
│  Select watchlist:                                              │
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║  ● ⭐ My Watchlist                          [3 assets]   ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║  ○ 💼 Investment Portfolio                   [12 assets]  ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│  ╔═══════════════════════════════════════════════════════════╗  │
│  ║  ○ 🎯 Trading Shortlist                    [5 assets]   ║  │
│  ╚═══════════════════════════════════════════════════════════╝  │
│                                                                 │
│  ─────────────────────────────────────────────────────────────  │
│                                                                 │
│            [+ Create New Watchlist]                             │
│                                                                 │
│  ┌───────────────────────┐  ┌───────────────────────┐          │
│  │        Cancel         │  │         Add           │          │
│  └───────────────────────┘  └───────────────────────┘          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Actions:
| Action | Result |
|--------|--------|
| Drag down | Dismiss sheet |
| Tap Radio button | Select watchlist |
| Tap [+ Create New] | Open create dialog |
| Tap [Cancel] | Dismiss without saving |
| Tap [Add] | Add asset & dismiss |

---

## 🔔 7. Set Price Alert (Bottom Sheet)

**Layout:** `bottom_sheet_price_alert.xml`

```
                    ═══════════════
                    Drag to dismiss
                ═══════════════════

┌─────────────────────────────────────────────────────────────────┐
│                       Set Price Alert                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Notify me when BTC hits...                                     │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  Target Price ($)                                       │    │
│  │  ┌─────────────────────────────────────────────────┐   │    │
│  │  │ $  [ 70,000.00                           ]       │   │    │
│  │  └─────────────────────────────────────────────────┘   │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                 │
│  Alert Type:                                                    │
│                                                                 │
│  ┌───────────┐  ┌───────────┐                                  │
│  │ ● Above ▲ │  │ ○ Below ▼ │                                  │
│  │  Selected │  │           │                                  │
│  └───────────┘  └───────────┘                                  │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                   Set Alert                               │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Actions:
| Action | Result |
|--------|--------|
| Enter price | Set target price |
| Select Above/Below | Set alert direction |
| Tap [Set Alert] | Schedule alert with WorkManager |

---

## 🧩 8. Quick Pulse Widget

**Layout:** `widget_quick_pulse.xml`

```
    ╭──────────────────────────────────────╮
    │                                      │
    │   BTC/USDT                           │
    │                                      │
    │   $65,240.50                         │
    │   ▲ +2.45%                           │
    │                                      │
    │                     Updated: 14:32   │
    │                                      │
    ╰──────────────────────────────────────╯
```

### Tap Actions:
- **Tap widget** → Open MarketWise app (Dashboard)
- **Auto-update** → WorkManager periodic update

---

## 🔄 Complete User Flows

### Flow 1: Open App & Check Watchlist
```
[Home Screen]
     │
     ├──▶ View Market Pulse (horizontal scroll)
     │
     ├──▶ View Fear & Greed Index
     │
     └──▶ Tap Watchlist Item
              │
              ▼
        [Asset Detail Screen]
                  │
                  ├──▶ View Chart
                  ├──▶ See Statistics
                  └──▶ Set Price Alert
```

### Flow 2: Add Asset to Watchlist
```
[Home Screen]
     │
     └──▶ Tap [+ Add Asset]
              │
              ▼
        [Search/Browse Screen]
                  │
                  ├──▶ Tap Tab (Crypto/Stock)
                  │
                  ├──▶ Search [🔍]
                  │
                  └──▶ Tap Asset
                           │
                           ▼
                    [Bottom Sheet: Select List]
                           │
                           ├──▶ Select existing list
                           │
                           ├──▶ Create new list
                           │
                           └──▶ Tap [Add]
                                    │
                                    ▼
                              [Back to Home]
                              (Asset added)
```

### Flow 3: Set Price Alert
```
[Any Detail Screen]
     │
     └──▶ Tap [🔔]
              │
              ▼
        [Bottom Sheet: Price Alert]
                  │
                  ├──▶ Enter Target Price
                  │
                  ├──▶ Select Above/Below
                  │
                  └──▶ Tap [Set Alert]
                           │
                           ▼
                    [WorkManager Scheduled]
                           │
                           ▼
                    [Notification when hit]
```

### Flow 4: Remove from Watchlist
```
[Home Screen]
     │
     └──▶ Long press Watchlist Item
              │
              ▼
        [Bottom Sheet: Options]
                  │
                  ├──▶ Remove from watchlist
                  │
                  ├──▶ Move to another list
                  │
                  └──▶ Cancel
```

### Flow 5: Switch Between Markets
```
[Any Screen]
     │
     └──▶ Tap Bottom Navigation
              │
              ├──▶ [🏠 Home] → Dashboard
              │
              ├──▶ [💱 Crypto] → Crypto List
              │
              └──▶ [🏦 Stocks] → Stock List
```

---

## 📂 Layout Files Reference

| Screen | Layout File | Fragment/Activity |
|--------|-------------|-------------------|
| Main Container | `activity_main.xml` | MainActivity |
| Dashboard | `fragment_dashboard.xml` | DashboardFragment |
| Crypto List | `fragment_crypto_list.xml` | CryptoListFragment |
| Crypto Detail | `fragment_crypto_detail.xml` | CryptoDetailFragment |
| Stock List | `fragment_stock_list.xml` | StockListFragment |
| Stock Detail | `fragment_stock_detail.xml` | StockDetailFragment |
| Add Watchlist | `bottom_sheet_add_watchlist.xml` | BottomSheetDialog |
| Price Alert | `bottom_sheet_price_alert.xml` | BottomSheetDialog |
| Watchlist Item | `item_watchlist.xml` | RecyclerView Item |
| Search Item | `item_search_coin.xml` | RecyclerView Item |
| Stock Item | `item_stock.xml` | RecyclerView Item |
| Widget | `widget_quick_pulse.xml` | AppWidgetProvider |

---

## 🎨 Design System

### Warm Color Theme
```
Background:       #F5F0E8  (cream/off-white)
Surface:          #FFFFFF  (card backgrounds)
Primary:          #C9A96E  (golden accent)
Primary Dark:     #B08C5F
Text Primary:     #3D3D3D  (dark gray)
Text Secondary:   #8A8A8A  (medium gray)
Success:          #4CAF50  (green)
Danger:           #E53935  (red)
Card Accent:      #FDF1E6  (warm peach)
Gauge Track:      #E9E5D9  (light beige)
```

### Component Specs
```
Cards:
  - Border radius: 16dp
  - Elevation: 2dp
  - Padding: 16dp
  - Background: @color/warm_surface

Buttons (Primary):
  - Height: 56dp
  - Border radius: 12dp
  - Text: 16sp, bold

Buttons (Text):
  - Text color: @color/warm_primary
  - Icon size: 24dp

Input Fields:
  - Style: OutlinedBox
  - Border radius: 8dp
  - Stroke color: @color/warm_primary

Logos:
  - Size: 44dp (watchlist), 60dp (detail), 36dp (search)
  - Shape: Circular background
  - Background: #F8F3ED

Text Styles:
  - Price Large: 36sp, bold
  - Price Medium: 17sp, bold
  - Heading: 22sp, bold
  - Title: 20sp, bold
  - Subtitle: 18sp, bold
  - Body: 16sp, regular
  - Caption: 14sp, regular
  - Small: 12sp, regular
```

---

*Wireframe Documentation v2.0 - MarketWise Project*
*Generated from actual project layout files*
