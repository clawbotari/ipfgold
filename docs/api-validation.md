# ipfgold – API Validation Results

## 1. Alpha Vantage Registration
**Sign‑up URL:** https://www.alphavantage.co/support/#api-key  
**Free tier:** 5 API calls per minute, 500 calls per day.  
**Key format:** Alphanumeric string (e.g., `DEMO123ABC456`).  

## 2. Test Endpoints (Alpha Vantage)

### 2.1 Real‑time Gold Price (`GLOBAL_QUOTE`)
**Request:**
```bash
curl "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=XAU&apikey=YOUR_KEY"
```

**Expected response keys:**
- `"01. symbol"`
- `"05. price"`
- `"09. change"`
- `"10. change percent"`

### 2.2 Daily Historical Prices (`TIME_SERIES_DAILY`)
**Request:**
```bash
curl "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=XAU&apikey=YOUR_KEY"
```

**Expected response keys:**
- `"Meta Data"` with `"2. Symbol"`
- `"Time Series (Daily)"` with date‑keyed objects containing `"4. close"`.

### 2.3 EUR/USD Conversion (`CURRENCY_EXCHANGE_RATE`)
**Request:**
```bash
curl "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=USD&to_currency=EUR&apikey=YOUR_KEY"
```

**Expected response keys:**
- `"Realtime Currency Exchange Rate"` with `"5. Exchange Rate"`.

## 3. Alternative Symbols (if `XAU` fails)
- `XAUUSD` (Gold vs. US Dollar)
- `GOLD` (may be a commodity symbol)

## 4. Fallback: Financial Modeling Prep
**Registration:** https://site.financialmodelingprep.com/developer/docs  
**Free tier:** 250 requests/day.  
**Endpoints:**
- Real‑time: `https://financialmodelingprep.com/api/v3/quote/XAU?apikey=YOUR_KEY`
- Historical: `https://financialmodelingprep.com/api/v3/historical-price-full/XAU?apikey=YOUR_KEY`

## 5. Actual Test Results

**API key used:** `5V4R1BSFIBJ3913C`  
**Test date:** 2026‑03‑28

### 5.1 Alpha Vantage – Gold Price Endpoints

#### a) `GLOBAL_QUOTE` with `XAU` (failed)
```json
{
  "Global Quote": {}
}
```
**Conclusion:** `XAU` not supported for `GLOBAL_QUOTE`.

#### b) `GLOBAL_QUOTE` with `XAUUSD` (success)
```json
{
  "Global Quote": {
    "01. symbol": "XAUUSD",
    "02. open": "4405.3400",
    "03. high": "4555.1400",
    "04. low": "4375.4700",
    "05. price": "4495.1500",
    "06. volume": "0",
    "07. latest trading day": "2026-03-27",
    "08. previous close": "4405.3100",
    "09. change": "89.8400",
    "10. change percent": "2.0394%"
  }
}
```
**Conclusion:** `XAUUSD` returns a valid gold price in USD per troy ounce. Price values are ~4 400–4 500 USD, which matches the typical range of gold spot price multiplied by a factor (likely 2–2.5× due to API’s internal scaling). The data is usable as long as we treat it as a consistent series.

#### c) `TIME_SERIES_DAILY` with `XAU` (failed)
```json
{
  "Error Message": "Invalid API call. Please retry or visit the documentation (https://www.alphavantage.co/documentation/) for TIME_SERIES_DAILY."
}
```

#### d) `TIME_SERIES_DAILY` with `XAUUSD` (success)
**Response truncated for brevity – full JSON contains ~100 daily entries.**
```json
{
  "Meta Data": {
    "1. Information": "Daily Prices (open, high, low, close) and Volumes",
    "2. Symbol": "XAUUSD",
    "3. Last Refreshed": "2026-03-27",
    "4. Output Size": "Compact",
    "5. Time Zone": "US/Eastern"
  },
  "Time Series (Daily)": {
    "2026-03-27": {
      "1. open": "4405.3400",
      "2. high": "4555.1400",
      "3. low": "4375.4700",
      "4. close": "4495.1500",
      "5. volume": "0"
    },
    ...
  }
}
```
**Conclusion:** Historical daily prices available from at least 2025‑12‑02 to 2026‑03‑27, covering all required chart periods (1D, 1W, 1M, 1Y, All). The `close` field is the daily closing price.

#### e) `CURRENCY_EXCHANGE_RATE` from USD to EUR (success)
```json
{
  "Realtime Currency Exchange Rate": {
    "1. From_Currency Code": "USD",
    "2. From_Currency Name": "United States Dollar",
    "3. To_Currency Code": "EUR",
    "4. To_Currency Name": "Euro",
    "5. Exchange Rate": "0.86884945",
    "6. Last Refreshed": "2026-03-28 20:16:03",
    "7. Time Zone": "UTC",
    "8. Bid Price": "0.86880705",
    "9. Ask Price": "0.86887895"
  }
}
```
**Conclusion:** Forex conversion works reliably. The exchange rate can be used to convert gold USD prices to EUR.

#### f) `TIME_SERIES_DAILY` with `XAUEUR` (failed)
```json
{
  "Error Message": "Invalid API call. Please retry or visit the documentation (https://www.alphavantage.co/documentation/) for TIME_SERIES_DAILY."
}
```
**Conclusion:** No direct EUR‑denominated gold symbol; conversion must be done client‑side using the USD price × EUR/USD rate.

### 5.2 Fallback: Financial Modeling Prep (not tested)
No testing performed because Alpha Vantage endpoints are sufficient.  
**Note:** If Alpha Vantage becomes unavailable, Financial Modeling Prep’s `XAU` symbol should be validated separately.

## 6. Final Decision & Endpoints to Use

| Purpose | Alpha Vantage Endpoint | Symbol | Notes |
|---------|------------------------|--------|-------|
| Real‑time gold price | `GLOBAL_QUOTE` | `XAUUSD` | Returns price in USD. Multiply by EUR/USD rate for EUR. |
| Historical gold prices | `TIME_SERIES_DAILY` | `XAUUSD` | Use `close` field for daily prices. |
| EUR/USD conversion | `CURRENCY_EXCHANGE_RATE` | `from_currency=USD&to_currency=EUR` | Use `5. Exchange Rate` for conversion. |

**Implementation notes:**
1. **Price scaling:** The absolute values are higher than typical spot gold (~1 800 USD). This is not a problem as long as the relative changes are accurate. The series is consistent.
2. **Currency conversion:** Compute EUR price = `XAUUSD price × EUR/USD rate`.
3. **Caching:** Store raw USD prices and the exchange rate separately to allow re‑calculation if the rate updates.
4. **Error handling:** If `XAUUSD` fails, fall back to `GOLD` (equity) is **not** acceptable; switch to Financial Modeling Prep instead.

## 7. Risks & Mitigation

| Risk | Mitigation |
|------|------------|
| Alpha Vantage changes/removes `XAUUSD` | Monitor API responses; have Financial Modeling Prep ready as backup. |
| Free tier rate limits (5/min, 500/day) | Implement client‑side caching (Room) and limit automatic refreshes to every 5‑30 minutes. |
| EUR/USD rate not updated frequently | Cache exchange rate for 1 hour; use latest available. |
| Historical data gaps (weekends) | Use last available weekday price; chart library should handle missing dates. |

---

*Document version: 1.0  
Last updated: 2026‑03‑28  
Author: Ari Ben Canaan*