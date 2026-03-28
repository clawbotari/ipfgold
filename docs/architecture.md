# ipfgold – Architecture Decisions

## 1. Gold Price API

| Option | Free Tier Limits | Refresh ≤60s | Historical Data (1D/1W/1M/1Y) | EUR & USD | Personal Use Sufficiency |
|--------|------------------|--------------|--------------------------------|-----------|--------------------------|
| **MetalPriceAPI** | 100 requests/month | Yes (60s) | 30 days only | Yes | ❌ Insufficient: historical range limited to 30 days. |
| **GoldAPI.io** | 30 requests/day | No (10min) | 7 days only | Yes | ❌ Insufficient: refresh too slow, historical range limited. |
| **Alpha Vantage** | 5 calls/min, 500 calls/day | Yes (real‑time) | Full (daily up to 20+ years) | Yes (via forex conversion) | ✅ **Sufficient:** rate limits allow personal use; historical data covers all required periods. |
| **Financial Modeling Prep** | 250 requests/day | Yes (real‑time) | Full (daily) | Yes | ✅ **Sufficient:** good daily limit, but API focused on stocks; gold symbol “XAU” supported. |

**Decision: Alpha Vantage**  
**Justification:** Provides real‑time gold price (XAU) with daily historical data covering all chart periods (1D,1W,1M,1Y). The free tier (5 calls/minute, 500/day) is ample for a personal app that fetches prices every 5‑30 minutes. EUR/USD conversion available via separate forex endpoint.

**Implementation notes:**
- Endpoint: `https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=XAU&apikey=...`
- Historical: `function=TIME_SERIES_DAILY&symbol=XAU`
- Forex conversion: `function=CURRENCY_EXCHANGE_RATE&from_currency=USD&to_currency=EUR`

---

## 2. Layered Architecture

| Option | Components | Pros | Cons |
|--------|------------|------|------|
| **MVVM (Model‑View‑ViewModel)** | View (Compose) ↔ ViewModel ↔ Repository ↔ DataSources | Google‑recommended, clear separation, lifecycle‑aware | Boilerplate for simple apps |
| **MVI (Model‑View‑Intent)** | View ↔ Intent → ViewModel → State | Unidirectional data flow, predictable state | Steeper learning curve, more boilerplate |
| **Clean Architecture** | Use Cases, Entities, multiple layers | Highly testable, decoupled | Over‑engineering for a small app |

**Decision: MVVM with concrete components**  
**Justification:** The app is small (3‑4 screens) and MVVM is the standard Android architecture supported by Jetpack. It provides a clear separation between UI logic (ViewModel) and data (Repository), simplifying testing and maintenance.

**Components:**
- **View:** Jetpack Compose screens.
- **ViewModel:** Holds UI state, exposes events, consumes Flows from Repository.
- **Repository:** Single source of truth, combines RemoteDataSource (API) and LocalDataSource (Room cache).
- **RemoteDataSource:** Retrofit service for Alpha Vantage.
- **LocalDataSource:** Room DAO for caching prices and preferences.

---

## 3. Chart Library

| Option | Type | Pros | Cons |
|--------|------|------|------|
| **Vico** | Native Jetpack Compose | Compose‑first, declarative, modern API | Less mature, fewer features, smaller community |
| **MPAndroidChart** | Android View (via `AndroidView`) | Extremely mature, rich feature set, well‑documented | Requires interoperability (`AndroidView`), imperative API, larger binary |
| **Compose‑Charts** (Google) | Native Compose (experimental) | Official, integrates seamlessly | Still experimental, limited customization |

**Decision: Vico**  
**Justification:** The app requires a simple line chart with period selection and tooltips. Vico’s Compose‑native API aligns with the UI toolkit, avoids View interoperability overhead, and is lightweight. Its maturity is sufficient for the MVP.

**Fallback:** If Vico lacks needed features, switch to MPAndroidChart wrapped in `AndroidView`.

---

## 4. Local Storage

| Storage Type | Use Case | Pros | Cons |
|--------------|----------|------|------|
| **Room** | Structured cache (price history, timestamps) | Type‑safe, SQLite power, observable queries | Overkill for simple key‑value |
| **DataStore** | User preferences (currency, theme, refresh interval) | Coroutines/Flow support, proto or preferences | Not suited for complex relational data |
| **SharedPreferences** | Simple preferences | Simple API, widely known | Not Coroutine‑friendly, no type safety |

**Decision: Room + DataStore (Preferences)**  
**Justification:** Use **Room** to cache price history (date, price, currency) because it’s structured and needs querying for chart ranges. Use **DataStore (Preferences)** for user settings (currency, theme, interval) because it’s modern, type‑safe, and integrates with Kotlin Flow.

---

## 5. Dependency Injection

| Option | Pros | Cons |
|--------|------|------|
| **Hilt** | Google‑supported, reduces boilerplate, integrates with Jetpack | Learning curve, compile‑time generation |
| **Koin** | Lightweight, Kotlin‑first, easy setup | Runtime resolution, less tooling support |
| **Manual DI** | No extra libraries, full control | Tedious, error‑prone, hard to maintain |

**Decision: Hilt**  
**Justification:** Hilt is the standard DI solution for Android, recommended by Google. It works seamlessly with ViewModel, Room, Retrofit, and reduces boilerplate. The learning curve is acceptable for a professional project.

---

## 6. HTTP Client & JSON Parsing

| Option | Pros | Cons |
|--------|------|------|
| **Retrofit + OkHttp + Moshi** | Retrofit: type‑safe, declarative. OkHttp: interceptors, caching. Moshi: Kotlin‑first, efficient | Moshi requires adapters for complex types |
| **Retrofit + OkHttp + Gson** | Gson: simple, reflective, no code generation | Slower, less Kotlin‑idiomatic |
| **Ktor Client** | Multiplatform, Coroutine‑native, Kotlin‑first | Less Android‑specific tooling, smaller ecosystem |

**Decision: Retrofit + OkHttp + Moshi**  
**Justification:** Retrofit is the industry standard for Android REST clients. OkHttp provides robust networking features (cache, timeouts, interceptors). Moshi is Kotlin‑friendly, generates adapters at compile time, and outperforms Gson for Kotlin data classes.

---

## 7. Identified Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| **Alpha Vantage free tier rate limits** | App may hit daily limit if user refreshes excessively | Implement client‑side caching (Room) and respect 5‑calls/min limit. Educate user about “free tier” in settings. |
| **Vico library instability** | Chart features may be buggy or missing | Pin to a stable version; have a fallback plan to switch to MPAndroidChart. |
| **Gold price accuracy & latency** | Displayed price may lag real market | Use “last updated” timestamp prominently; consider a second data source for verification. |
| **EUR/USD conversion reliability** | Forex rates may not update frequently | Use Alpha Vantage’s `CURRENCY_EXCHANGE_RATE` endpoint; cache conversion for 1 hour. |
| **Offline cache staleness** | User sees outdated prices after 24h | Limit cache TTL to 24h; show clear “data may be stale” warning. |
| **API key exposure** | Key could be extracted from APK | Use Android’s `secrets-gradle-plugin` or backend proxy (future). For MVP, obfuscate and restrict key usage. |

---

*Document version: 1.0  
Last updated: 2026‑03‑28  
Author: Ari Ben Canaan*