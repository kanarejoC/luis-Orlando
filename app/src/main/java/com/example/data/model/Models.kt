package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PropertyType(val displayName: String, val labelPlural: String) {
    LOCAL("Local Comercial", "Locales Comerciales"),
    DEPARTAMENTO("Departamento", "Departamentos"),
    CASA("Casa de Arriendo", "Casas de Arriendo"),
    SUITE("Suite Ejecutiva", "Suites")
}

enum class SectorType(val displayName: String, val description: String) {
    URBANO("Sector Urbano", "En zona céntrica o residencial con acceso a servicios urbanos completos"),
    RURAL("Sector Rural", "En entorno campestre o campiña con amplias áreas verdes y tranquilidad")
}

enum class InstitutionType(val displayName: String) {
    BANCO("Banco"),
    COOPERATIVA("Coop. de Ahorro y Crédito")
}

enum class AccountType(val displayName: String) {
    AHORROS("Cuenta de Ahorros"),
    CORRIENTE("Cuenta Corriente")
}

enum class RenewalType(val displayName: String, val description: String) {
    AUTOMATICA("Renovación Automática", "El contrato se renueva anualmente de forma tácita salvo notificación previa"),
    PREAVISO_30_DIAS("Preaviso 30 días", "Requiere notificación escrita de renovación con 30 días de anticipación"),
    PREAVISO_60_DIAS("Preaviso 60 días", "Requiere notificación escrita de renovación con 60 días de anticipación"),
    NO_RENOVABLE("Plazo Fijo (No Renovable)", "Vence estrictamente en la fecha pactada sin prórroga tácita")
}

enum class ContractStatus(val displayName: String) {
    BORRADOR("Borrador"),
    PENDIENTE_FIRMA("Pendiente de Firma"),
    VIGENTE_FIRMADO("Vigente y Firmado"),
    POR_VENCER("Próximo a Vencer"),
    RENOVADO("Renovado"),
    FINALIZADO("Finalizado")
}

enum class PaymentStatus(val displayName: String) {
    PENDIENTE("Pendiente"),
    PAGADO("Pagado (Por Verificar)"),
    VERIFICADO("Verificado y Acreditado"),
    VENCIDO("Vencido / En Mora")
}

enum class NotificationType(val displayName: String) {
    PAGO_PENDIENTE("Recordatorio de Pago"),
    PAGO_RECIBIDO("Canon de Arriendo Recibido"),
    CONTRATO_VIGENCIA("Vigencia de Contrato"),
    RENOVACION_AVISO("Aviso de Renovación"),
    FIRMA_SOLICITADA("Firma de Contrato Requerida")
}

enum class UserRole(val displayName: String) {
    ARRENDATARIO("Arrendatario / Inquilino"),
    ARRENDADOR("Arrendador / Propietario")
}

@Entity(tableName = "bank_accounts")
data class BankAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val institutionName: String, // e.g. "Banco Pichincha", "Coop. JEP", "Banco Guayaquil"
    val institutionType: InstitutionType,
    val accountType: AccountType,
    val accountNumber: String,
    val accountHolderName: String,
    val accountHolderIdCard: String, // Cédula o RUC
    val notificationEmail: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "properties")
data class PropertyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val propertyType: PropertyType,
    val sectorType: SectorType,
    val city: String,
    val neighborhood: String,
    val address: String,
    val monthlyRent: Double, // Canon de arrendamiento mensual en USD
    val guaranteeAmount: Double, // Monto de garantía en USD
    val bedrooms: Int,
    val bathrooms: Int,
    val areaSqMeters: Double,
    val amenities: String, // Comma separated: Agua caliente, Parqueadero, Wifi, Seguridad, Mascotas permitidas
    val imageDrawableRes: String, // Resource name like "img_dept_modern", "img_local_front", "img_house_urban", "img_country_rural"
    val isAvailable: Boolean = true,
    val bankAccountId: Long? = null,
    val landlordName: String,
    val landlordPhone: String,
    val landlordEmail: String,
    val landlordIdCard: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "lease_contracts")
data class LeaseContractEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contractNumber: String, // e.g. "CTR-2026-0089"
    val propertyId: Long,
    val propertyTitle: String,
    val propertyAddress: String,
    val sectorType: SectorType,
    val landlordName: String,
    val landlordIdCard: String,
    val landlordPhone: String,
    val landlordEmail: String,
    val tenantName: String,
    val tenantIdCard: String,
    val tenantPhone: String,
    val tenantEmail: String,
    val monthlyRent: Double,
    val guaranteeAmount: Double,
    val startDate: Long, // timestamp
    val endDate: Long, // timestamp
    val durationMonths: Int, // e.g. 12 months
    val renewalType: RenewalType,
    val status: ContractStatus,
    val landlordSignatureSvg: String = "", // Vector strokes data
    val tenantSignatureSvg: String = "",
    val signedDate: Long? = null,
    val digitalVerificationHash: String = "", // Hash SHA-256 simulation for digital validity
    val linkedBankAccountId: Long? = null,
    val bankAccountSummary: String = "",
    val clausesNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "payment_records")
data class PaymentRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contractId: Long,
    val propertyId: Long,
    val monthPeriod: String, // e.g. "Septiembre 2026"
    val amount: Double,
    val dueDate: Long,
    val paymentDate: Long? = null,
    val status: PaymentStatus,
    val paymentMethod: String = "Transferencia Bancaria / Cooperativa",
    val referenceNumber: String = "",
    val receiptProofNotes: String = "",
    val bankAccountId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_notifications")
data class AppNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val targetRole: UserRole = UserRole.ARRENDATARIO,
    val relatedId: Long? = null
)
