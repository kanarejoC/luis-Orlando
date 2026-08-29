package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.db.AppDatabase
import com.example.data.model.AccountType
import com.example.data.model.AppNotificationEntity
import com.example.data.model.BankAccountEntity
import com.example.data.model.ContractStatus
import com.example.data.model.InstitutionType
import com.example.data.model.LeaseContractEntity
import com.example.data.model.NotificationType
import com.example.data.model.PaymentRecordEntity
import com.example.data.model.PaymentStatus
import com.example.data.model.PropertyEntity
import com.example.data.model.PropertyType
import com.example.data.model.RenewalType
import com.example.data.model.SectorType
import com.example.data.model.UserRole
import com.example.data.repository.AppRepository
import com.example.notifications.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository
    init {
        val db = AppDatabase.getDatabase(application)
        repository = AppRepository(db.appDao())
        NotificationHelper.createNotificationChannels(application)
    }

    // Role state
    private val _currentRole = MutableStateFlow(UserRole.ARRENDATARIO)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    fun switchRole(newRole: UserRole) {
        _currentRole.value = newRole
    }

    // Filter states for catalog
    private val _selectedPropertyTypeFilter = MutableStateFlow<PropertyType?>(null)
    val selectedPropertyTypeFilter: StateFlow<PropertyType?> = _selectedPropertyTypeFilter.asStateFlow()

    private val _selectedSectorFilter = MutableStateFlow<SectorType?>(null)
    val selectedSectorFilter: StateFlow<SectorType?> = _selectedSectorFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setPropertyTypeFilter(type: PropertyType?) {
        _selectedPropertyTypeFilter.value = type
    }

    fun setSectorFilter(sector: SectorType?) {
        _selectedSectorFilter.value = sector
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Filtered properties
    val properties: StateFlow<List<PropertyEntity>> = combine(
        repository.allProperties,
        _selectedPropertyTypeFilter,
        _selectedSectorFilter,
        _searchQuery
    ) { list, propFilter, sectorFilter, query ->
        list.filter { prop ->
            val matchesType = propFilter == null || prop.propertyType == propFilter
            val matchesSector = sectorFilter == null || prop.sectorType == sectorFilter
            val matchesQuery = query.isBlank() ||
                    prop.title.contains(query, ignoreCase = true) ||
                    prop.city.contains(query, ignoreCase = true) ||
                    prop.neighborhood.contains(query, ignoreCase = true) ||
                    prop.address.contains(query, ignoreCase = true)
            matchesType && matchesSector && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Bank Accounts
    val bankAccounts: StateFlow<List<BankAccountEntity>> = repository.allBankAccounts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Contracts
    val contracts: StateFlow<List<LeaseContractEntity>> = repository.allContracts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Payments
    val payments: StateFlow<List<PaymentRecordEntity>> = repository.allPayments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Notifications
    val notifications: StateFlow<List<AppNotificationEntity>> = repository.allNotifications
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val unreadNotificationCount: StateFlow<Int> = repository.unreadNotificationCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Selected Property for detail view
    private val _selectedProperty = MutableStateFlow<PropertyEntity?>(null)
    val selectedProperty: StateFlow<PropertyEntity?> = _selectedProperty.asStateFlow()

    fun selectProperty(property: PropertyEntity?) {
        _selectedProperty.value = property
    }

    // Selected Contract for viewer / signing / renewal
    private val _selectedContract = MutableStateFlow<LeaseContractEntity?>(null)
    val selectedContract: StateFlow<LeaseContractEntity?> = _selectedContract.asStateFlow()

    fun selectContract(contract: LeaseContractEntity?) {
        _selectedContract.value = contract
    }

    // Actions: Create Property
    fun createProperty(
        title: String,
        description: String,
        propertyType: PropertyType,
        sectorType: SectorType,
        city: String,
        neighborhood: String,
        address: String,
        monthlyRent: Double,
        guaranteeAmount: Double,
        bedrooms: Int,
        bathrooms: Int,
        areaSqMeters: Double,
        amenities: String,
        imageDrawableRes: String,
        bankAccountId: Long?,
        landlordName: String,
        landlordPhone: String,
        landlordEmail: String,
        landlordIdCard: String,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val entity = PropertyEntity(
                title = title,
                description = description,
                propertyType = propertyType,
                sectorType = sectorType,
                city = city,
                neighborhood = neighborhood,
                address = address,
                monthlyRent = monthlyRent,
                guaranteeAmount = guaranteeAmount,
                bedrooms = bedrooms,
                bathrooms = bathrooms,
                areaSqMeters = areaSqMeters,
                amenities = amenities,
                imageDrawableRes = imageDrawableRes,
                isAvailable = true,
                bankAccountId = bankAccountId,
                landlordName = landlordName,
                landlordPhone = landlordPhone,
                landlordEmail = landlordEmail,
                landlordIdCard = landlordIdCard
            )
            val newId = repository.insertProperty(entity)

            // Auto-log notification
            val notif = AppNotificationEntity(
                title = "Inmueble Publicado con Éxito",
                message = "Se ha puesto en oferta $title en el $sectorType ($city). Canon: $$monthlyRent USD/mes.",
                type = NotificationType.CONTRATO_VIGENCIA,
                targetRole = UserRole.ARRENDADOR,
                relatedId = newId
            )
            repository.insertNotification(notif)
            onSuccess(newId)
        }
    }

    // Actions: Bank & Coop Account
    fun addBankAccount(
        institutionName: String,
        institutionType: InstitutionType,
        accountType: AccountType,
        accountNumber: String,
        accountHolderName: String,
        accountHolderIdCard: String,
        notificationEmail: String,
        isDefault: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val account = BankAccountEntity(
                institutionName = institutionName,
                institutionType = institutionType,
                accountType = accountType,
                accountNumber = accountNumber,
                accountHolderName = accountHolderName,
                accountHolderIdCard = accountHolderIdCard,
                notificationEmail = notificationEmail,
                isDefault = isDefault
            )
            repository.insertBankAccount(account)
            onSuccess()
        }
    }

    fun setDefaultBankAccount(id: Long) {
        viewModelScope.launch {
            repository.setDefaultBankAccount(id)
        }
    }

    // Actions: Digital Lease Contract Generation
    fun createContract(
        property: PropertyEntity,
        tenantName: String,
        tenantIdCard: String,
        tenantPhone: String,
        tenantEmail: String,
        durationMonths: Int,
        renewalType: RenewalType,
        clausesNotes: String,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val startCal = Calendar.getInstance()
            val endCal = Calendar.getInstance().apply {
                add(Calendar.MONTH, durationMonths)
            }
            val contractNum = "CTR-${Calendar.getInstance().get(Calendar.YEAR)}-${(1000..9999).random()}"

            var bankSummary = "A convenir con el arrendador"
            val linkedBank = property.bankAccountId?.let { repository.getBankAccountById(it) }
                ?: repository.getDefaultBankAccount()

            if (linkedBank != null) {
                bankSummary = "${linkedBank.institutionName} (${linkedBank.accountType.displayName}) Cta. #${linkedBank.accountNumber} - ${linkedBank.accountHolderName}"
            }

            val contract = LeaseContractEntity(
                contractNumber = contractNum,
                propertyId = property.id,
                propertyTitle = property.title,
                propertyAddress = "${property.address}, ${property.neighborhood}, ${property.city}",
                sectorType = property.sectorType,
                landlordName = property.landlordName,
                landlordIdCard = property.landlordIdCard,
                landlordPhone = property.landlordPhone,
                landlordEmail = property.landlordEmail,
                tenantName = tenantName,
                tenantIdCard = tenantIdCard,
                tenantPhone = tenantPhone,
                tenantEmail = tenantEmail,
                monthlyRent = property.monthlyRent,
                guaranteeAmount = property.guaranteeAmount,
                startDate = startCal.timeInMillis,
                endDate = endCal.timeInMillis,
                durationMonths = durationMonths,
                renewalType = renewalType,
                status = ContractStatus.PENDIENTE_FIRMA,
                linkedBankAccountId = linkedBank?.id,
                bankAccountSummary = bankSummary,
                clausesNotes = clausesNotes
            )

            val contractId = repository.insertContract(contract)

            // Create initial payment schedule for month 1
            val dueCal = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 5)
            }
            val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
            val monthPeriod = "Canon " + monthFormat.format(Date()).replaceFirstChar { it.uppercase() }

            val initialPayment = PaymentRecordEntity(
                contractId = contractId,
                propertyId = property.id,
                monthPeriod = monthPeriod,
                amount = property.monthlyRent,
                dueDate = dueCal.timeInMillis,
                status = PaymentStatus.PENDIENTE,
                bankAccountId = linkedBank?.id
            )
            repository.insertPayment(initialPayment)

            // Notifications for both landlord and tenant
            val notifTenant = AppNotificationEntity(
                title = "Contrato Digital Listo para Firma",
                message = "Se ha generado el contrato digital #$contractNum para el arriendo de '${property.title}'. Por favor revisa las cláusulas y estampa tu firma táctil.",
                type = NotificationType.FIRMA_SOLICITADA,
                targetRole = UserRole.ARRENDATARIO,
                relatedId = contractId
            )
            val notifLandlord = AppNotificationEntity(
                title = "Nuevo Contrato Generado",
                message = "Contrato #$contractNum generado con el arrendatario $tenantName. Canon mensual: $${property.monthlyRent} USD. Garantía: $${property.guaranteeAmount} USD.",
                type = NotificationType.FIRMA_SOLICITADA,
                targetRole = UserRole.ARRENDADOR,
                relatedId = contractId
            )
            repository.insertNotification(notifTenant)
            repository.insertNotification(notifLandlord)

            // Trigger System Notification
            NotificationHelper.showSystemNotification(
                getApplication(),
                "Contrato Digital Listo para Firma",
                "Contrato #$contractNum generado para ${property.title}. Firma requerida.",
                NotificationType.FIRMA_SOLICITADA,
                UserRole.ARRENDATARIO
            )

            onSuccess(contractId)
        }
    }

    // Actions: Digital Signing
    fun signContract(
        contract: LeaseContractEntity,
        isLandlord: Boolean,
        svgStrokeData: String,
        hash: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val updated = if (isLandlord) {
                contract.copy(
                    landlordSignatureSvg = svgStrokeData,
                    digitalVerificationHash = hash,
                    signedDate = System.currentTimeMillis(),
                    status = if (contract.tenantSignatureSvg.isNotEmpty()) ContractStatus.VIGENTE_FIRMADO else ContractStatus.PENDIENTE_FIRMA
                )
            } else {
                contract.copy(
                    tenantSignatureSvg = svgStrokeData,
                    digitalVerificationHash = hash,
                    signedDate = System.currentTimeMillis(),
                    status = if (contract.landlordSignatureSvg.isNotEmpty()) ContractStatus.VIGENTE_FIRMADO else ContractStatus.PENDIENTE_FIRMA
                )
            }

            repository.updateContract(updated)
            _selectedContract.value = updated

            if (updated.status == ContractStatus.VIGENTE_FIRMADO) {
                val notif = AppNotificationEntity(
                    title = "¡Contrato Firmado y Vigente!",
                    message = "El contrato #${updated.contractNumber} ha sido firmado digitalmente por ambas partes. Vigencia por ${updated.durationMonths} meses activa.",
                    type = NotificationType.CONTRATO_VIGENCIA,
                    targetRole = UserRole.ARRENDATARIO,
                    relatedId = updated.id
                )
                repository.insertNotification(notif)

                NotificationHelper.showSystemNotification(
                    getApplication(),
                    "Contrato Digital Vigente y Firmado",
                    "Contrato #${updated.contractNumber} de ${updated.propertyTitle} ha entrado en plena vigencia.",
                    NotificationType.CONTRATO_VIGENCIA,
                    UserRole.ARRENDATARIO
                )
            }
            onComplete()
        }
    }

    // Actions: Renew Contract
    fun renewContract(
        contract: LeaseContractEntity,
        extendedMonths: Int,
        newMonthlyRent: Double,
        renewalType: RenewalType,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val currentEndCal = Calendar.getInstance().apply {
                timeInMillis = contract.endDate
            }
            val newEndCal = Calendar.getInstance().apply {
                timeInMillis = currentEndCal.timeInMillis
                add(Calendar.MONTH, extendedMonths)
            }

            val renewed = contract.copy(
                monthlyRent = newMonthlyRent,
                durationMonths = contract.durationMonths + extendedMonths,
                endDate = newEndCal.timeInMillis,
                renewalType = renewalType,
                status = ContractStatus.RENOVADO,
                clausesNotes = contract.clausesNotes + "\n[Renovación]: Plazo prorrogado por $extendedMonths meses hasta fecha de vigencia finalizada."
            )
            repository.updateContract(renewed)
            _selectedContract.value = renewed

            val notif = AppNotificationEntity(
                title = "Contrato Renovado con Éxito",
                message = "El contrato #${contract.contractNumber} ha sido renovado por $extendedMonths meses adicionales. Nueva vigencia hasta ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(newEndCal.timeInMillis))}.",
                type = NotificationType.RENOVACION_AVISO,
                targetRole = UserRole.ARRENDATARIO,
                relatedId = contract.id
            )
            repository.insertNotification(notif)

            NotificationHelper.showSystemNotification(
                getApplication(),
                "Contrato de Arriendo Renovado",
                "El contrato #${contract.contractNumber} fue renovado por $extendedMonths meses.",
                NotificationType.RENOVACION_AVISO,
                UserRole.ARRENDADOR
            )
            onSuccess()
        }
    }

    // Actions: Pay monthly rent
    fun submitPayment(
        payment: PaymentRecordEntity,
        paymentMethod: String,
        referenceNumber: String,
        receiptProofNotes: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val updated = payment.copy(
                paymentDate = System.currentTimeMillis(),
                status = PaymentStatus.VERIFICADO, // Direct verification for demo flow or marked paid
                paymentMethod = paymentMethod,
                referenceNumber = referenceNumber,
                receiptProofNotes = receiptProofNotes
            )
            repository.updatePayment(updated)

            // Notifications for both landlord and tenant
            val notifLandlord = AppNotificationEntity(
                title = "Canon de Arriendo Recibido",
                message = "Pago de $${payment.amount} USD (${payment.monthPeriod}) recibido mediante $paymentMethod. Ref: $referenceNumber. Acreditado a la cuenta bancaria vinculada.",
                type = NotificationType.PAGO_RECIBIDO,
                targetRole = UserRole.ARRENDADOR,
                relatedId = payment.id
            )
            val notifTenant = AppNotificationEntity(
                title = "Comprobante de Pago Generado",
                message = "Tu pago de $${payment.amount} USD correspondiente a ${payment.monthPeriod} ha sido procesado y registrado con éxito. Ref: $referenceNumber.",
                type = NotificationType.PAGO_RECIBIDO,
                targetRole = UserRole.ARRENDATARIO,
                relatedId = payment.id
            )
            repository.insertNotification(notifLandlord)
            repository.insertNotification(notifTenant)

            // Trigger System Push Notifications for both landlord & tenant
            NotificationHelper.showSystemNotification(
                getApplication(),
                "Canon de Arriendo Recibido ($${payment.amount} USD)",
                "Acreditación registrada en cuenta de Coop/Banco. Ref: $referenceNumber",
                NotificationType.PAGO_RECIBIDO,
                UserRole.ARRENDADOR
            )

            NotificationHelper.showSystemNotification(
                getApplication(),
                "Pago de Arriendo Exitoso",
                "Tu canon de ${payment.monthPeriod} por $${payment.amount} USD está al día.",
                NotificationType.PAGO_RECIBIDO,
                UserRole.ARRENDATARIO
            )

            onSuccess()
        }
    }

    // Actions: Send Payment Reminder Notification manually / test trigger
    fun triggerPaymentReminder(payment: PaymentRecordEntity) {
        viewModelScope.launch {
            val notifTenant = AppNotificationEntity(
                title = "Recordatorio de Pago de Canon",
                message = "Recordatorio: El canon de arriendo de $${payment.amount} USD para el periodo ${payment.monthPeriod} debe ser cancelado oportunamente.",
                type = NotificationType.PAGO_PENDIENTE,
                targetRole = UserRole.ARRENDATARIO,
                relatedId = payment.id
            )
            val notifLandlord = AppNotificationEntity(
                title = "Aviso de Cobro Programado",
                message = "Notificación de cobro enviada al arrendatario por el canon de $${payment.amount} USD (${payment.monthPeriod}).",
                type = NotificationType.PAGO_PENDIENTE,
                targetRole = UserRole.ARRENDADOR,
                relatedId = payment.id
            )
            repository.insertNotification(notifTenant)
            repository.insertNotification(notifLandlord)

            NotificationHelper.showSystemNotification(
                getApplication(),
                "Recordatorio de Pago de Arriendo",
                "Tu canon de arriendo por $${payment.amount} USD vence pronto. Revisa tu cuenta.",
                NotificationType.PAGO_PENDIENTE,
                UserRole.ARRENDATARIO
            )
        }
    }

    // Actions: Send Contract Expiry & Renewal Notification
    fun triggerContractExpiryAlert(contract: LeaseContractEntity) {
        viewModelScope.launch {
            val remainingDays = ((contract.endDate - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
            val notifMsg = "El contrato #${contract.contractNumber} para '${contract.propertyTitle}' tiene $remainingDays días de vigencia restante. Tipo: ${contract.renewalType.displayName}."

            val notifTenant = AppNotificationEntity(
                title = "Aviso de Vigencia de Contrato",
                message = notifMsg,
                type = NotificationType.CONTRATO_VIGENCIA,
                targetRole = UserRole.ARRENDATARIO,
                relatedId = contract.id
            )
            val notifLandlord = AppNotificationEntity(
                title = "Aviso de Vigencia y Renovación",
                message = "$notifMsg Arrendatario: ${contract.tenantName}.",
                type = NotificationType.RENOVACION_AVISO,
                targetRole = UserRole.ARRENDADOR,
                relatedId = contract.id
            )
            repository.insertNotification(notifTenant)
            repository.insertNotification(notifLandlord)

            NotificationHelper.showSystemNotification(
                getApplication(),
                "Vigencia de Contrato de Arriendo",
                "Contrato #${contract.contractNumber}: $remainingDays días restantes. Revisa opciones de renovación.",
                NotificationType.CONTRATO_VIGENCIA,
                UserRole.ARRENDADOR
            )
        }
    }

    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
        }
    }
}
