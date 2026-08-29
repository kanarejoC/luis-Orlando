package com.example.data.repository

import com.example.data.db.AppDao
import com.example.data.model.AppNotificationEntity
import com.example.data.model.BankAccountEntity
import com.example.data.model.LeaseContractEntity
import com.example.data.model.PaymentRecordEntity
import com.example.data.model.PropertyEntity
import kotlinx.coroutines.flow.Flow

class AppRepository(private val dao: AppDao) {

    // Properties
    val allProperties: Flow<List<PropertyEntity>> = dao.getAllProperties()

    suspend fun getPropertyById(id: Long): PropertyEntity? = dao.getPropertyById(id)
    fun getPropertyByIdFlow(id: Long): Flow<PropertyEntity?> = dao.getPropertyByIdFlow(id)
    suspend fun insertProperty(property: PropertyEntity): Long = dao.insertProperty(property)
    suspend fun updateProperty(property: PropertyEntity) = dao.updateProperty(property)
    suspend fun deleteProperty(id: Long) = dao.deletePropertyById(id)

    // Bank Accounts
    val allBankAccounts: Flow<List<BankAccountEntity>> = dao.getAllBankAccounts()

    suspend fun getBankAccountById(id: Long): BankAccountEntity? = dao.getBankAccountById(id)
    suspend fun getDefaultBankAccount(): BankAccountEntity? = dao.getDefaultBankAccount()
    suspend fun insertBankAccount(account: BankAccountEntity): Long {
        if (account.isDefault) {
            dao.resetDefaultBankAccounts()
        }
        return dao.insertBankAccount(account)
    }
    suspend fun setDefaultBankAccount(id: Long) {
        dao.resetDefaultBankAccounts()
        dao.setDefaultBankAccount(id)
    }
    suspend fun updateBankAccount(account: BankAccountEntity) = dao.updateBankAccount(account)
    suspend fun deleteBankAccount(id: Long) = dao.deleteBankAccountById(id)

    // Contracts
    val allContracts: Flow<List<LeaseContractEntity>> = dao.getAllContracts()

    suspend fun getContractById(id: Long): LeaseContractEntity? = dao.getContractById(id)
    fun getContractByIdFlow(id: Long): Flow<LeaseContractEntity?> = dao.getContractByIdFlow(id)
    suspend fun insertContract(contract: LeaseContractEntity): Long = dao.insertContract(contract)
    suspend fun updateContract(contract: LeaseContractEntity) = dao.updateContract(contract)
    suspend fun deleteContract(id: Long) = dao.deleteContractById(id)

    // Payments
    val allPayments: Flow<List<PaymentRecordEntity>> = dao.getAllPayments()

    fun getPaymentsByContractId(contractId: Long): Flow<List<PaymentRecordEntity>> = dao.getPaymentsByContractId(contractId)
    suspend fun getPaymentById(id: Long): PaymentRecordEntity? = dao.getPaymentById(id)
    suspend fun insertPayment(payment: PaymentRecordEntity): Long = dao.insertPayment(payment)
    suspend fun updatePayment(payment: PaymentRecordEntity) = dao.updatePayment(payment)
    suspend fun deletePayment(id: Long) = dao.deletePaymentById(id)

    // Notifications
    val allNotifications: Flow<List<AppNotificationEntity>> = dao.getAllNotifications()
    val unreadNotificationCount: Flow<Int> = dao.getUnreadNotificationCount()

    suspend fun insertNotification(notification: AppNotificationEntity): Long = dao.insertNotification(notification)
    suspend fun markNotificationAsRead(id: Long) = dao.markNotificationAsRead(id)
    suspend fun markAllNotificationsAsRead() = dao.markAllNotificationsAsRead()
    suspend fun deleteNotification(id: Long) = dao.deleteNotificationById(id)
    suspend fun clearAllNotifications() = dao.clearAllNotifications()
}
