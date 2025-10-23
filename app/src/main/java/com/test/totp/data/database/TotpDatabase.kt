package com.test.totp.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.test.totp.data.dao.TotpAccountDao
import com.test.totp.data.dao.UserPreferencesDao
import com.test.totp.data.model.TotpAccount
import com.test.totp.data.model.UserPreferences

/**
 * Room database for TOTP application
 */
@Database(
    entities = [TotpAccount::class, UserPreferences::class],
    version = 1,
    exportSchema = false
)
abstract class TotpDatabase : RoomDatabase() {
    
    abstract fun totpAccountDao(): TotpAccountDao
    abstract fun userPreferencesDao(): UserPreferencesDao
    
    companion object {
        @Volatile
        private var INSTANCE: TotpDatabase? = null
        
        fun getDatabase(context: Context): TotpDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TotpDatabase::class.java,
                    "totp_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
