package com.ipfgold

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Punto de entrada de la aplicación. Hilt genera el componente DI aquí.
 */
@HiltAndroidApp
class IpfGoldApplication : Application()