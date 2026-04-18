# MarketWise Wireframe Documentation 🎨

> Wireframe สำหรับ MarketWise Android App - Crypto & Stock Tracking

---

## 📱 App Structure Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         MarketWise                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐      ┌──────────┐      ┌──────────┐             │
│   │Dashboard │◄────►│  Crypto  │◄────►│  Stock   │             │
│   │  (Home)  │      │   List   │      │   List   │             │
│   └────┬─────┘      └────┬─────┘      └────┬─────┘             │
│        │                 │                 │                    │
│        ▼                 ▼                 ▼                    │
│   ┌──────────┐      ┌──────────┐      ┌──────────┐             │
│   │ Watchlist│      │  Crypto  │      │  Stock   │             │
│   │  Detail  │      │  Detail  │      │  Detail  │             │
│   └──────────┘      └──────────┘      └──────────┘             │
│        │                 │                 │                    │
│        └────────────────┴─────────────────┘                    │
│                          │                                      │
│                          ▼                                      │
│                   ┌──────────────┐                             │
│                   │ Price Alert  │                             │
│                   │  (Bottom)    │                             │
│                   └──────────────┘                             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🏠 1. Dashboard (Home Screen)

**File:** `fragment_dashboard.xml`

```
┌─────────────────────────────────────┐
│  MarketWise              [🔔] ⚙️  │  ← Header
├─────────────────────────────────────┤
│                                     │
│  ┌─────────────────────────────┐    │
│  │      Market Overview        │    │
│  │   [Gainers/Losers Chart]   │    │
│  └─────────────────────────────┘    │
│                                     │
│  📊 MY WATCHLIST                    │
│  ─────────────────────────────────  │
│                                     │
│  ┌─────────────────────────────┐    │
│  │ 🪙 Bitcoin           ▲ 2.5%  │    │
│  │    BTC              $67,234 │    │
│  │    ───────────────────────  │    │
│  │ 💎 Ethereum          ▲ 1.2%  │    │
│  │    ETH               $3,456 │    │
│  │    ───────────────────────  │    │
│  │ 📈 AAPL              ▼ 0.8%  │    │
│  │    $174.50                  │    │
│  └─────────────────────────────┘    │
│                                     │
├─────────────────────────────────────┤
│  [💱 Crypto]     [🏦 Stocks]        │  ← Bottom Nav
└─────────────────────────────────────┘
```

### Components:
- **Header:** App title + Notification bell + Settings
- **Market Overview:** Summary cards showing market status
- **Watchlist:** Vertical list of favorited assets
- **Bottom Navigation:** Switch between Crypto/Stocks

---

## 💱 2. Crypto List

**File:** `fragment_crypto_list.xml`

```
┌─────────────────────────────────────┐
│  [←] Cryptocurrency      [🔍] ⚙️  │
├─────────────────────────────────────┤
│  [Trending 🔥]  [Top 100]  [DeFi]   │  ← Tab Filter
├─────────────────────────────────────┤
│                                     │
│  ┌─────────────────────────────┐    │
│  │ 🥇  🪙 Bitcoin              │    │
│  │      BTC             ▲ 2.5%│    │
│  │      $67,234         [⭐]   │    │
│  └─────────────────────────────┘    │
│                                     │
│  ┌─────────────────────────────┐    │
│  │ 🥈  💎 Ethereum             │    │
│  │      ETH             ▲ 1.2%│    │
│  │      $3,456          [⭐]   │    │
│  └─────────────────────────────┘    │
│                                     │
│  ┌─────────────────────────────┐    │
│  │ 🥉  ⚫ Binance Coin         │    │
│  │      BNB             ▼ 0.5%│    │
│  │      $589            [⭐]   │    │
│  └─────────────────────────────┘    │
│                                     │
│         [Pull to refresh]           │
└─────────────────────────────────────┘
```

### Components:
- **Header:** Back + Title + Search
- **Tab Filter:** Categories (Trending, Top 100, DeFi, etc.)
- **List Items:** Rank + Icon + Name + Symbol + Price + Change% + Favorite

---

## 📈 3. Crypto Detail

**File:** `fragment_crypto_detail.xml`

```
┌─────────────────────────────────────┐
│  [←] Bitcoin (BTC)     [⭐] [🔔]  │
├─────────────────────────────────────┤
│                                     │
│         🪙 Bitcoin                  │
│                                     │
│         $67,234.00                  │
│         ▲ +$1,234.56                 │
│         ▲ +2.54% (24h)              │
│                                     │
│  ┌─────────────────────────────┐    │
│  │                             │    │
│  │      [Price Chart]          │    │
│  │      Line/Candlestick       │    │
│  │                             │    │
│  │  1D  1W  1M  3M  1Y  ALL   │    │
│  └─────────────────────────────┘    │
│                                     │
│  ┌──────────┬──────────┬─────────┐ │
│  │Market Cap│ Vol (24h)│ Circ.   │ │
│  │ $1.3T    │ $34.5B   │ 19.5M   │ │
│  └──────────┴──────────┴─────────┘ │
│                                     │
│  ┌──────────┬──────────┬─────────┐ │
│  │ 24h High │ 24h Low  │ ATH     │ │
│  │ $68,500  │ $65,200  │ $73,750 │ │
│  └──────────┴──────────┴─────────┘ │
│                                     │
│  ┌─────────────────────────────┐    │
│  │    🔔 Set Price Alert     │    │
│  └─────────────────────────────┘    │
│                                     │
│  [Live Price Stream: Binance WS]    │
└─────────────────────────────────────┘
```

### Components:
- **Header:** Back + Asset name + Favorite + Alert
- **Price Section:** Current price, absolute change, % change
- **Chart:** Interactive price chart with timeframe selector
- **Stats Grid:** Market data cards
- **Action Button:** Set price alert

---

## 🏦 4. Stock List

**File:** `fragment_stock_list.xml`

```
┌─────────────────────────────────────┐
│  [←] Stocks              [🔍] ⚙️  │
├─────────────────────────────────────┤
│  [US 🇺🇸]  [TH 🇹🇭]  [EU 🇪🇺]  [Asia] │  ← Market Filter
├─────────────────────────────────────┤
│                                     │
│  ┌─────────────────────────────┐    │
│  │ 📈  Apple Inc.              │    │
│  │      AAPL            ▲ 0.8%│    │
│  │      NASDAQ   $174.50 [⭐]  │    │
│  └─────────────────────────────┘    │
│                                     │
│  ┌─────────────────────────────┐    │
│  │ 📈  Tesla, Inc.             │    │
│  │      TSLA            ▼ 1.2%│    │
│  │      NASDAQ   $242.30 [⭐]  │    │
│  └─────────────────────────────┘    │
│                                     │
│  ┌─────────────────────────────┐    │
│  │ 📈  Microsoft Corp.       │    │
│  │      MSFT            ▲ 0.5%│    │
│  │      NASDAQ   $423.50 [⭐]  │    │
│  └─────────────────────────────┘    │
│                                     │
└─────────────────────────────────────┘
```

### Components:
- **Header:** Same as Crypto List
- **Market Filter:** US, Thailand, EU, Asia markets
- **List Items:** Icon + Company name + Symbol + Exchange + Price + Change% + Favorite

---

## 📊 5. Stock Detail

**File:** `fragment_stock_detail.xml`

```
┌─────────────────────────────────────┐
│  [←] AAPL        [⭐]  [🔔]  📊  │
├─────────────────────────────────────┤
│                                     │
│         📈 Apple Inc.               │
│         NASDAQ: AAPL                │
│                                     │
│         $174.50                     │
│         ▲ +$1.45 (+0.84%)           │
│         After: $174.80              │
│                                     │
│  ┌─────────────────────────────┐    │
│  │                             │    │
│  │    [Candlestick Chart]    │    │
│  │    Price/Volume           │    │
│  │                             │    │
│  │  1D  1W  1M  3M  1Y  5Y   │    │
│  └─────────────────────────────┘    │
│                                     │
│  ┌──────────┬──────────┬─────────┐ │
│  │  P/E     │ MarketCap│ DivYld  │ │
│  │  28.5    │ $2.7T    │ 0.52%   │ │
│  └──────────┴──────────┴─────────┘ │
│                                     │
│  ┌──────────┬──────────┬─────────┐ │
│  │ 52W High │ 52W Low  │ Volume  │ │
│  │ $199.62  │ $124.17  │ 55.2M   │ │
│  └──────────┴──────────┴─────────┘ │
│                                     │
│  ┌─────────────────────────────┐    │
│  │    🔔 Set Price Alert     │    │
│  └─────────────────────────────┘    │
└─────────────────────────────────────┘
```

### Components:
- **Header:** Back + Symbol + Favorite + Alert + Technical Analysis
- **Company Info:** Name + Exchange
- **Price:** Current, change, after-hours
- **Chart:** Candlestick with volume
- **Stats:** P/E, Market Cap, Dividend Yield, 52W Range

---

## ⭐ 6. Add to Watchlist (Bottom Sheet)

**File:** `bottom_sheet_add_watchlist.xml`

```
         ═══════════════════
         Drag to dismiss
     ═══════════════════════

┌─────────────────────────────────────┐
│         Add to Watchlist            │
│         ─────────────────           │
│                                     │
│   Bitcoin (BTC)                     │
│   $67,234.00  ▲ +2.54%              │
│                                     │
│   ───────────────────────────────   │
│                                     │
│   ◉ ⭐ My Watchlist                 │
│                                     │
│   ○ 💼 Investment Portfolio          │
│                                     │
│   ○ 🎯 Trading Shortlist           │
│                                     │
│   ───────────────────────────────   │
│                                     │
│   [+ Create New List]               │
│                                     │
│   ┌─────────────────────────────┐   │
│   │        Cancel    [Add]      │   │
│   └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

### Components:
- **Drag Handle:** Dismiss bottom sheet
- **Asset Preview:** Icon, name, current price
- **List Selection:** Radio buttons for existing lists
- **Create New:** Button to add new watchlist
- **Actions:** Cancel + Add

---

## 🔔 7. Price Alert Dialog

```
┌─────────────────────────────────────┐
│          🔔 Price Alert             │
│         ─────────────────           │
│                                     │
│   Bitcoin (BTC)                     │
│   Current: $67,234.00               │
│                                     │
│   ───────────────────────────────   │
│                                     │
│   Alert me when price is:           │
│                                     │
│   [Above ▲]  [Below ▼]  [Any =]    │
│                                     │
│   ┌─────────────────────────┐       │
│   │  $  [ 70,000    ]      │       │
│   └─────────────────────────┘       │
│                                     │
│   [☐] One time    [☑] Repeat       │
│                                     │
│   ┌───────────┬───────────┐         │
│   │  Cancel   │  Set      │         │
│   └───────────┴───────────┘         │
│                                     │
└─────────────────────────────────────┘
```

---

## 🧩 8. Quick Pulse Widget

**File:** `widget_quick_pulse.xml`

```
     ╭──────────────────────╮
     │   ⚡ Quick Pulse     │
     ├──────────────────────┤
     │                      │
     │  BTC    $67,234   ▲  │
     │  ETH    $3,456    ▲  │
     │  AAPL   $174.50   ▲  │
     │                      │
     │  ─────────────────   │
     │  Last: 2 min ago     │
     │                      │
     ╰──────────────────────╯
```

### Components:
- **Widget Header:** Quick Pulse title
- **Asset List:** Top 3 assets from watchlist
- **Timestamp:** Last update time
- **Tap Action:** Opens Dashboard

---

## 🔄 Navigation Flow

```
                    ┌─────────────────┐
                    │   MainActivity  │
                    │  (Bottom Nav)   │
                    └────────┬────────┘
                             │
           ┌─────────────────┼─────────────────┐
           │                 │                 │
           ▼                 ▼                 ▼
    ┌────────────┐    ┌────────────┐    ┌────────────┐
    │ Dashboard  │    │ CryptoList │    │ StockList  │
    │  Fragment  │    │  Fragment  │    │  Fragment  │
    └─────┬──────┘    └─────┬──────┘    └─────┬──────┘
          │                 │                 │
          │                 │                 │
          │         ┌───────┴───────┐         │
          │         │               │         │
          │         ▼               ▼         │
          │    ┌─────────┐    ┌─────────┐     │
          │    │ Crypto  │    │ Price   │     │
          │    │ Detail  │    │ Alert   │     │
          │    └────┬────┘    └─────────┘     │
          │         │                          │
          │         │                          │
          └─────────┴──────────────────────────┘
                    │
                    ▼
           ┌────────────┐
           │ BottomSheet │
           │ (Add to WL) │
           └────────────┘
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
| Search Item | `item_search_coin.xml` | List item |
| Stock Item | `item_stock.xml` | List item |
| Watchlist Item | `item_watchlist.xml` | List item |
| Widget | `widget_quick_pulse.xml` | AppWidgetProvider |

---

## 🎨 Design Notes

### Color Palette (Suggestion)
```
Primary:    #1E88E5 (Blue)     - App brand
Success:    #00C853 (Green)    - Price up
Danger:     #FF1744 (Red)      - Price down
Background: #FAFAFA (Gray-50)  - Screen bg
Surface:    #FFFFFF (White)    - Cards
Text:       #212121 (Gray-900) - Primary text
TextMuted:  #757575 (Gray-600) - Secondary text
```

### Typography (Suggestion)
```
Heading:    24sp Bold
Title:      20sp Medium
Body:       16sp Regular
Caption:    12sp Regular
Price:      32sp Bold (numbers)
```

---

## 📱 Responsive Considerations

- **Phone Portrait:** Stack everything vertically
- **Phone Landscape:** Chart expands, side-by-side stats
- **Tablet:** Two-pane (list + detail) on same screen

---

*Generated by OpenClaw - MarketWise Project Wireframe*
