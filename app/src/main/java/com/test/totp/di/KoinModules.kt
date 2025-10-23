package com.test.totp.di

import androidx.room.Room
import com.test.totp.data.database.TotpDatabase
import com.test.totp.data.repository.TotpRepository
import com.test.totp.data.security.EncryptionService
import com.test.totp.domain.service.ClipboardService
import com.test.totp.domain.service.QrCodeService
import com.test.totp.domain.service.TotpService
import com.test.totp.presentation.viewmodel.AddAccountViewModel
import com.test.totp.presentation.viewmodel.MainViewModel
import com.test.totp.presentation.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { EncryptionService(androidContext()) }
    single { TotpService(get()) }
    single { QrCodeService() }
    single { ClipboardService(androidContext()) }
    single { TotpRepository(get(), get()) }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            TotpDatabase::class.java,
            "totp_database"
        ).build()
    }
    single { get<TotpDatabase>().totpAccountDao() }
    single { get<TotpDatabase>().userPreferencesDao() }
}

val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { AddAccountViewModel(get(), get(), get(), get()) }
    viewModel { SettingsViewModel(get()) }
}
