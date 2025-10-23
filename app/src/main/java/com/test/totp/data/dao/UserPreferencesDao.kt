package com.test.totp.data.dao

import androidx.room.*
import com.test.totp.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for user preferences
 */
@Dao
interface UserPreferencesDao {
    
    @Query("SELECT * FROM user_preferences WHERE id = 'preferences'")
    fun getPreferences(): Flow<UserPreferences?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(preferences: UserPreferences)
    
    @Update
    suspend fun updatePreferences(preferences: UserPreferences)
    
    @Query("UPDATE user_preferences SET isDarkModeEnabled = :isDarkMode WHERE id = 'preferences'")
    suspend fun updateDarkMode(isDarkMode: Boolean)
    
    @Query("UPDATE user_preferences SET autoRefreshInterval = :interval WHERE id = 'preferences'")
    suspend fun updateRefreshInterval(interval: Int)
    
    @Query("UPDATE user_preferences SET showSeconds = :showSeconds WHERE id = 'preferences'")
    suspend fun updateShowSeconds(showSeconds: Boolean)
    
    @Query("UPDATE user_preferences SET hapticFeedback = :hapticFeedback WHERE id = 'preferences'")
    suspend fun updateHapticFeedback(hapticFeedback: Boolean)
}
