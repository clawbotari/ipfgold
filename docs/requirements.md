# ipfgold – Product Requirements Document

## 1. Product Description
**ipfgold** is an Android application that provides real‑time gold price tracking with interactive historical charts. The app is designed for personal and family use, offering a clean, professional financial interface without overwhelming non‑expert users.

## 2. Target User
- **Primary:** Individuals and families who want to monitor gold prices for personal savings or investment awareness.
- **Secondary:** Users with basic financial literacy, not professional traders.
- **Platform:** Android smartphones (API level 24+).

## 3. Supported Currencies
- Euro (EUR)
- US Dollar (USD)

Users can switch between currencies at any time; the app will convert prices accordingly.

## 4. Visual Style
- **Theme:** Financial / professional.
- **Color palette:** Gold‑inspired accents, dark/light mode support.
- **Typography:** Clean, readable sans‑serif fonts.
- **Layout:** Card‑based, with clear separations and ample whitespace.
- **Charts:** Interactive, with pinch‑to‑zoom and period selection (1D, 1W, 1M, 1Y, All).

## 5. Mandatory Technology Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM (Model‑View‑ViewModel)
- **Asynchronous:** Kotlin Coroutines + Flow
- **Networking:** Retrofit + OkHttp
- **Dependency injection:** Hilt
- **Persistence:** Room Database (for offline caching)
- **Charting:** MPAndroidChart or compose‑based equivalent

## 6. Feature List (Prioritized)

### Must‑Have (MVP)
1. **Real‑time gold price display**  
   - Current price in selected currency (EUR/USD).
   - Price change (absolute and percentage) in the last 24 hours.
   - Timestamp of last update.

2. **Currency switching**  
   - One‑tap toggle between EUR and USD.
   - Persist user’s preference.

3. **Historical price chart**  
   - Line chart showing gold price over a selectable period (1D, 1W, 1M, 1Y, All).
   - Interactive: tap for price at a specific point.
   - Display min/max values for the visible range.

4. **Offline support**  
   - Cache latest price and chart data.
   - Show “last known” data when offline, with a clear offline indicator.

5. **Basic settings**  
   - Select default currency.
   - Choose chart default period.
   - Toggle dark/light theme.

6. **Error handling**  
   - Graceful network error messages.
   - Retry mechanism.

### Should‑Have (Post‑MVP)
7. **Price alerts**  
   - Set a target price and receive a notification when reached.
   - Manage multiple alerts.

8. **Multiple gold benchmarks**  
   - Support for different gold types (e.g., XAU, gold ETFs).
   - Allow switching between benchmarks.

9. **Widget**  
   - Home‑screen widget showing current price.

10. **Historical data export**  
    - Export chart data as CSV.

### Could‑Have (Future)
11. **Multiple languages**  
    - Spanish, English, etc.

12. **Portfolio tracking**  
    - Allow user to input gold holdings and track total value.

13. **News feed**  
    - Curated financial news related to gold.

14. **Advanced chart indicators**  
    - Moving averages, RSI, etc.

## 7. Non‑Functional Requirements

### Performance
- App launch time < 2 seconds.
- Chart rendering smooth (60 fps) with up to 1000 data points.
- Network requests cached; no redundant calls within a 5‑minute window.

### Offline Behavior
- Cache lasts up to 24 hours.
- Offline mode clearly indicated; user can manually refresh when connection returns.

### Accessibility
- Support for TalkBack.
- Sufficient color contrast (WCAG AA).
- Scalable text.

### Security
- No authentication required for basic features.
- API keys stored securely (not hard‑coded).
- HTTPS only.

### Maintainability
- Modular code structure.
- Unit tests for ViewModels and data layer.
- UI tests for critical flows.

### Compatibility
- Minimum SDK: API 24 (Android 7.0).
- Target SDK: Latest stable.
- Support for different screen sizes and orientations.

## 8. Technical Constraints

- **Data source:** Free gold price API (e.g., MetalPricesAPI, GoldAPI) with rate limits.
- **Chart library:** Must work with Jetpack Compose; native Compose solutions preferred.
- **No server‑side component:** All logic runs on‑device; only external API calls.
- **Storage:** Use Room for caching; avoid large on‑disk footprint.
- **Analytics:** Optional; if added, must respect user privacy (opt‑in).

---

*Document version: 1.0  
Last updated: 2026‑03‑28  
Author: Ari Ben Canaan*