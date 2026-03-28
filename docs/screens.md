# ipfgold – Screen Flows & Navigation

## Overview
The app follows a simple navigation structure with **three main screens**. All screens support both light and dark themes and are built with Jetpack Compose.

## Screen 1: Home
**Purpose:**  
Display real‑time gold price, daily change, and an interactive historical chart.

**UI Elements:**
- App bar with app title, current date, and a settings icon (top‑right).
- Large card showing:
  - Current gold price in selected currency (e.g., `1,845.32 €`).
  - Absolute and percentage change in the last 24h (colored green/red).
  - Timestamp of the last update.
- Currency toggle chip/button (EUR | USD).
- Interactive line chart for the selected period.
- Period selector chips: **1D**, **1W**, **1M**, **1Y**, **All**.
- Floating Action Button (FAB) with refresh icon.
- Offline indicator (when no network).
- Bottom navigation bar (Home, Settings, About).

**User Actions:**
- Tap currency toggle to switch between EUR and USD.
- Tap a period chip to update the chart.
- Tap on the chart to see the exact price at a point (tooltip).
- Pull‑to‑refresh or tap FAB to manually update prices.
- Tap settings icon to navigate to Settings.
- Tap bottom‑navigation items to switch screens.

**Navigation:**
- → **Settings** (via top‑right icon or bottom nav)
- → **About** (via bottom nav)

---

## Screen 2: Settings
**Purpose:**  
Let the user customize app behavior and appearance.

**UI Elements:**
- App bar with back arrow and “Settings” title.
- List of preference cards:
  1. **Default currency** – radio buttons (EUR / USD).
  2. **Default chart period** – dropdown (1D, 1W, 1M, 1Y, All).
  3. **Theme** – radio buttons (System default, Light, Dark).
  4. **Data refresh interval** – slider or dropdown (5 min, 15 min, 30 min, 1 hour).
  5. **Clear cache** – button with confirmation dialog.
  6. **About this app** – button that navigates to About screen.
- Version number at the bottom (e.g., `v1.0.0`).

**User Actions:**
- Change any preference; changes are applied immediately.
- Tap “Clear cache” to delete stored price/chart data.
- Tap “About this app” to navigate to About screen.
- Tap back arrow to return to Home.

**Navigation:**
- ← **Home** (via back arrow or bottom nav)
- → **About** (via “About this app” button)

---

## Screen 3: About
**Purpose:**  
Show app information, legal notices, and contact details.

**UI Elements:**
- App bar with back arrow and “About” title.
- App icon and name (`ipfgold`).
- Version number and build date.
- Short description of the app.
- Links (non‑interactive labels or tappable rows):
  - **Privacy Policy** (opens in‑app WebView)
  - **Terms of Service** (opens in‑app WebView)
  - **Rate this app** (opens Play Store)
  - **Send feedback** (opens email client)
- Developer credits (“Built by Israel Pascual Fuente”).
- Copyright notice.

**User Actions:**
- Tap any link to open the corresponding external content.
- Tap back arrow to return to previous screen.

**Navigation:**
- ← **Home** or **Settings** (via back arrow or bottom nav)

---

## Navigation Graph
```
Home (start destination)
    ↑↓
Settings
    ↑↓
About
```

**Bottom Navigation Bar** (present on all screens):
- **Home** – navigates to Home (if not already there)
- **Settings** – navigates to Settings
- **About** – navigates to About

**Note:** The bottom bar is hidden on dialogs, full‑screen charts, or WebView screens.

---

## Edge Cases & Empty States
- **No network:** Home shows cached data with a persistent offline banner. Chart shows “data unavailable” placeholder.
- **First launch:** Settings picks system currency (EUR if available, else USD) and system theme.
- **Error loading prices:** Home shows a retry card with error message and a retry button.

---

*Document version: 1.0  
Last updated: 2026‑03‑28  
Author: Ari Ben Canaan*