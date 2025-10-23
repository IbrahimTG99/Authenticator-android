package com.test.totp

import android.app.Application
import com.test.totp.di.appModule
import com.test.totp.di.databaseModule
import com.test.totp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Application class for TOTP app
 * Initializes Koin dependency injection
 */
class TotpApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TotpApplication)
            modules(appModule, databaseModule, viewModelModule)
        }
    }
}
