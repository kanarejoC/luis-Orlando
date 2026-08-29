package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppNotificationEntity
import com.example.data.model.BankAccountEntity
import com.example.data.model.LeaseContractEntity
import com.example.data.model.PaymentRecordEntity
import com.example.data.model.PropertyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // Properties
    @Query("SELECT * FROM properties ORDER BY createdAt DESC")
    fun getAllProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE id = :id")
    suspend fun getPropertyById(id: Long): PropertyEntity?

    @Query("SELECT * FROM properties WHERE id = :id")
    fun getPropertyByIdFlow(id: Long): Flow<PropertyEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyEntity): Long

    @Update
    suspend fun updateProperty(property: PropertyEntity)

    @Query("DELETE FROM properties WHERE id = :id")
    suspend fun deletePropertyById(id: Long)

    // Bank Accounts
    @Query("SELECT * FROM bank_accounts ORDER BY isDefault DESC, createdAt DESC")
    fun getAllBankAccounts(): Flow<List<BankAccountEntity>>

    @Query("SELECT * FROM bank_accounts WHERE id = :id")
    suspend fun getBankAccountById(id: Long): BankAccountEntity?

    @Query("SELECT * FROM bank_accounts WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultBankAccount(): BankAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankAccount(account: BankAccountEntity): Long

    @Update
    suspend fun updateBankAccount(account: BankAccountEntity)

    @Query("UPDATE bank_accounts SET isDefault = 0")
    suspend fun resetDefaultBankAccounts()

    @Query("UPDATE bank_accounts SET isDefault = 1 WHERE id = :id")
    suspend fun setDefaultBankAccount(id: Long)

    @Query("DELETE FROM bank_accounts WHERE id = :id")
    suspend fun deleteBankAccountById(id: Long)

    // Lease Contracts
    @Query("SELECT * FROM lease_contracts ORDER BY createdAt DESC")
    fun getAllContracts(): Flow<List<LeaseContractEntity>>

    @Query("SELECT * FROM lease_contracts WHERE id = :id")
    suspend fun getContractById(id: Long): LeaseContractEntity?

    @Query("SELECT * FROM lease_contracts WHERE id = :id")
    fun getContractByIdFlow(id: Long): Flow<LeaseContractEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContract(contract: LeaseContractEntity): Long

    @Update
    suspend fun updateContract(contract: LeaseContractEntity)

    @Query("DELETE FROM lease_contracts WHERE id = :id")
    suspend fun deleteContractById(id: Long)

    // Payment Records
    @Query("SELECT * FROM payment_records ORDER BY dueDate ASC")
    fun getAllPayments(): Flow<List<PaymentRecordEntity>>

    @Query("SELECT * FROM payment_records WHERE contractId = :contractId ORDER BY dueDate ASC")
    fun getPaymentsByContractId(contractId: Long): Flow<List<PaymentRecordEntity>>

    @Query("SELECT * FROM payment_records WHERE id = :id")
    suspend fun getPaymentById(id: Long): PaymentRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentRecordEntity): Long

    @Update
    suspend fun updatePayment(payment: PaymentRecordEntity)

    @Query("DELETE FROM payment_records WHERE id = :id")
    suspend fun deletePaymentById(id: Long)

    // Notifications
    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotificationEntity>>

    @Query("SELECT COUNT(*) FROM app_notifications WHERE isRead = 0")
    fun getUnreadNotificationCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotificationEntity): Long

    @Query("UPDATE app_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Long)

    @Query("UPDATE app_notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM app_notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: Long)

    @Query("DELETE FROM app_notifications")
    suspend fun clearAllNotifications()
}
