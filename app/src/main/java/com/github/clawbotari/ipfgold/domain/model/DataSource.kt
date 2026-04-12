package com.github.clawbotari.ipfgold.domain.model

enum class DataSource(val displayName: String, val requiresApiKey: Boolean) {
    ALPHA_VANTAGE("Alpha Vantage", true),
    METALS_API("Metals-API", true),
    GOLD_API("GoldAPI.io", true)
}