package com.github.clawbotari.ipfgold.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebugLogger @Inject constructor() {
    private val _logs = MutableStateFlow<List<DebugLog>>(emptyList())
    val logs: StateFlow<List<DebugLog>> = _logs

    fun log(type: LogType, message: String) {
        val entry = DebugLog(
            timestamp = System.currentTimeMillis(),
            type = type,
            message = message
        )
        _logs.value = (_logs.value + entry).takeLast(50)
    }

    fun clear() {
        _logs.value = emptyList()
    }
}

data class DebugLog(
    val timestamp: Long,
    val type: LogType,
    val message: String
)

enum class LogType {
    REQUEST,
    RESPONSE,
    ERROR,
    INFO
}