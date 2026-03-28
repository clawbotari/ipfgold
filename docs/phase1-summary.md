# ipfgold – Phase 1 Summary

## 1. Agreed Screens & Core Functionalities

| Screen | Primary Functionality (one‑line) |
|--------|-----------------------------------|
| **Home** | Display real‑time gold price, daily change, and an interactive historical chart with period selection (1D/1W/1M/1Y/All). |
| **Settings** | Configure default currency (EUR/USD), chart period, theme (light/dark/system), refresh interval, and clear cache. |
| **About** | Show app version, legal links (Privacy Policy, Terms of Service), and developer credits. |

## 2. Definitive Technology Stack (Versions as of 2026‑03‑28)

| Dependency | Latest Stable Version | Purpose |
|------------|----------------------|---------|
| **Kotlin** | 2.0.21 | Language & standard library. |
| **Android Gradle Plugin** | 8.6.0 | Project build tool. |
| **Jetpack Compose BOM** | 2025.12.00 | Bill of Materials for Compose libraries. |
| **Compose UI** | (via BOM) | Declarative UI toolkit. |
| **Compose Material3** | (via BOM) | Material Design 3 components. |
| **Compose Navigation** | 2.8.0 | Navigation between screens. |
| **ViewModel** | 2.8.0 | Lifecycle‑aware UI state holder. |
| **LiveData** | 2.8.0 | Optional observer for migration. |
| **Kotlin Coroutines** | 1.8.0 | Asynchronous programming. |
| **Flow** | (in Kotlin) | Reactive streams. |
| **Hilt** | 2.51 | Dependency injection. |
| **Room** | 2.7.0 | Local SQLite caching for price history. |
| **DataStore (Preferences)** | 1.1.0 | Type‑safe user preferences storage. |
| **Retrofit** | 2.11.0 | Type‑safe HTTP client. |
| **OkHttp** | 4.12.0 | HTTP client with interceptors & cache. |
| **Moshi** | 1.15.1 | Kotlin‑first JSON serialization. |
| **Vico** | 1.13.0 | Compose‑native chart library (line chart). |
| **Coil** | 3.0.0 | Image loading (for future icons). |
| **Timber** | 5.0.1 | Logging utility. |
| **JUnit 5** | 5.10.0 | Unit testing. |
| **MockK** | 1.13.10 | Mocking library for Kotlin. |
| **Espresso** | 3.5.1 | UI testing. |
| **Compose UI Test** | (via BOM) | Compose UI testing. |

## 3. Chosen API: Alpha Vantage

**Primary data source:** [Alpha Vantage](https://www.alphavantage.co/)  
**Free tier limits:** 5 calls/minute, 500 calls/day.  
**Gold symbol:** `XAU` (Gold troy ounce).  
**Endpoints required:**

1. **Real‑time quote**  
   `https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=XAU&apikey=YOUR_KEY`

2. **Daily historical prices**  
   `https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=XAU&apikey=YOUR_KEY`

3. **EUR/USD conversion** (for currency toggle)  
   `https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=USD&to_currency=EUR&apikey=YOUR_KEY`

**⚠️ Critical note:**  
The exact endpoint responses for `XAU` must be validated with a real API key before implementing the `RemoteDataSource`. Alpha Vanguard’s documentation mentions “precious metals” but the actual symbol mapping should be confirmed (some sources use `XAUUSD` or `GOLD`). If Alpha Vantage does not return expected data, the fallback is **Financial Modeling Prep** (`https://financialmodelingprep.com/api/v3/quote/XAU?apikey=...`), which also offers gold pricing with similar limits.

## 4. Planned Code Folder Structure (Phase 2)

```
app/
├── src/main/java/com/ipfgold/
│   ├── di/                           # Hilt modules
│   ├── presentation/
│   │   ├── navigation/               # NavGraph, destinations
│   │   ├── theme/                    # Colors, Typography, AppTheme
│   │   ├── home/
│   │   │   ├── HomeScreen.kt
│   │   │   ├── HomeViewModel.kt
│   │   │   └── components/           # ChartCard, PriceCard, PeriodChips
│   │   ├── settings/
│   │   │   ├── SettingsScreen.kt
│   │   │   ├── SettingsViewModel.kt
│   │   │   └── components/
│   │   └── about/
│   │       ├── AboutScreen.kt
│   │       └── components/
│   ├── domain/
│   │   ├── model/                    # GoldPrice, ChartPoint, Currency
│   │   ├── repository/               # GoldPriceRepository interface
│   │   └── usecase/                  # (optional) GetCurrentPrice, GetHistoricalData
│   └── data/
│       ├── local/
│       │   ├── dao/                  # GoldPriceDao, PreferencesDao
│       │   ├── entity/               # GoldPriceEntity, PreferencesEntity
│       │   └── database/             # AppDatabase
│       ├── remote/
│       │   ├── api/                  # AlphaVantageService
│       │   ├── model/                # API response DTOs
│       │   └── mapper/               # DTO → Domain model mappers
│       ├── repository/
│       │   └── GoldPriceRepositoryImpl.kt
│       └── datasource/
│           ├── RemoteGoldPriceDataSource.kt
│           └── LocalGoldPriceDataSource.kt
├── src/androidTest/                  # Instrumented tests
└── src/test/                         # Unit tests
```

## 5. Risks & Pending Decisions Before Phase 2

| Risk / Decision | Status | Action Required |
|-----------------|--------|-----------------|
| **Alpha Vantage symbol validation** | Pending | Obtain a free API key and test the endpoints with `XAU` to confirm response format. |
| **Vico chart library suitability** | Pending | Create a small Compose preview with dummy data to verify line chart + tooltip functionality. |
| **API key security** | Pending | Decide on secret storage: `secrets-gradle-plugin` vs. environment variable vs. simple obfuscation for MVP. |
| **Offline cache strategy** | Pending | Define TTL for price data (24h) and chart data (7 days). Implement Room migrations if needed. |
| **Currency conversion accuracy** | Pending | Determine whether to fetch forex rate separately or rely on Alpha Vantage’s built‑in conversion. |
| **Error handling & retry logic** | Pending | Design user‑friendly error states (network, API limit, malformed data). |
| **Design system tokens** | Pending | Define exact color palette (gold accents), typography scale, and spacing constants. |
| **Testing strategy** | Pending | Decide unit‑test coverage targets (ViewModel, Repository) and UI test scope (critical paths). |
| **Continuous Integration** | Pending | Set up GitHub Actions for build, test, and lint (can be deferred to Phase 3). |

---

## Next Phase (Phase 2) – Implementation Kick‑off

1. **Set up Gradle modules** (`app/build.gradle.kts`) with all dependencies.
2. **Create Hilt modules** and `Application` class.
3. **Implement data layer** (Room entities, Retrofit service, DTOs, mappers).
4. **Build repository** with fallback logic (remote → local).
5. **Create presentation layer** (Theme, HomeScreen, ViewModel).
6. **Integrate Vico chart** with dummy data.
7. **Add navigation** (Compose Navigation) between three screens.
8. **Write unit tests** for ViewModel and Repository.

**Estimated effort:** 3–4 days of focused development.

---

*Document version: 1.0  
Last updated: 2026‑03‑28  
Author: Ari Ben Canaan*