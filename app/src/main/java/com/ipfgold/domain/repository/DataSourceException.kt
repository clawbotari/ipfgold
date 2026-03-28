package com.ipfgold.domain.repository

/**
 * Excepción lanzada cuando fallan tanto la fuente remota como la local.
 */
class DataSourceException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)