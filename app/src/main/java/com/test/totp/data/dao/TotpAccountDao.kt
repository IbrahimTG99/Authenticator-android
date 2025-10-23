package com.test.totp.data.dao

import androidx.room.*
import com.test.totp.data.model.TotpAccount
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for TOTP accounts
 */
@Dao
interface TotpAccountDao {
    
    @Query("SELECT * FROM totp_accounts ORDER BY createdAt DESC")
    fun getAllAccounts(): Flow<List<TotpAccount>>
    
    @Query("SELECT * FROM totp_accounts WHERE id = :id")
    suspend fun getAccountById(id: String): TotpAccount?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: TotpAccount)
    
    @Update
    suspend fun updateAccount(account: TotpAccount)
    
    @Delete
    suspend fun deleteAccount(account: TotpAccount)
    
    @Query("DELETE FROM totp_accounts WHERE id = :id")
    suspend fun deleteAccountById(id: String)
    
    @Query("SELECT COUNT(*) FROM totp_accounts")
    suspend fun getAccountCount(): Int
}
