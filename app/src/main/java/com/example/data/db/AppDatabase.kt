package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [
        PropertyEntity::class,
        BankAccountEntity::class,
        LeaseContractEntity::class,
        PaymentRecordEntity::class,
        AppNotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "arriendo_digital_database"
                )
                    .addCallback(DatabaseCallback(context))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = getDatabase(context).appDao()
                    seedDatabase(dao)
                }
            }
        }

        suspend fun seedDatabase(dao: AppDao) {
            // Seed Bank and Cooperative Accounts
            val bankId1 = dao.insertBankAccount(
                BankAccountEntity(
                    institutionName = "Coop. de Ahorro y Crédito JEP",
                    institutionType = InstitutionType.COOPERATIVA,
                    accountType = AccountType.AHORROS,
                    accountNumber = "406089234101",
                    accountHolderName = "Ing. Carlos Mendoza Viteri",
                    accountHolderIdCard = "1718293041",
                    notificationEmail = "carlos.mendoza.arriendos@gmail.com",
                    isDefault = true
                )
            )

            val bankId2 = dao.insertBankAccount(
                BankAccountEntity(
                    institutionName = "Banco Pichincha",
                    institutionType = InstitutionType.BANCO,
                    accountType = AccountType.CORRIENTE,
                    accountNumber = "2100458920",
                    accountHolderName = "Dra. Elena Salazar Morales",
                    accountHolderIdCard = "0921847593",
                    notificationEmail = "elena.salazar.propiedades@gmail.com",
                    isDefault = false
                )
            )

            val bankId3 = dao.insertBankAccount(
                BankAccountEntity(
                    institutionName = "Coop. 29 de Octubre",
                    institutionType = InstitutionType.COOPERATIVA,
                    accountType = AccountType.AHORROS,
                    accountNumber = "109823471200",
                    accountHolderName = "Ing. Roberto Alvear",
                    accountHolderIdCard = "1802934812",
                    notificationEmail = "roberto.alvear@gmail.com",
                    isDefault = false
                )
            )

            // Seed Properties
            val propId1 = dao.insertProperty(
                PropertyEntity(
                    title = "Departamento Moderno Vista Panorámica",
                    description = "Hermoso departamento de estreno ubicado en el corazón financiero. Cuenta con amplios ventanales, balcón privado, acabados de primera, cocina tipo americana con mesón de granito y ascensor directo.",
                    propertyType = PropertyType.DEPARTAMENTO,
                    sectorType = SectorType.URBANO,
                    city = "Quito",
                    neighborhood = "La Carolina / Av. República de El Salvador",
                    address = "Av. República de El Salvador N34-120 y Moscú",
                    monthlyRent = 580.0,
                    guaranteeAmount = 1160.0, // 2 meses de garantía
                    bedrooms = 2,
                    bathrooms = 2,
                    areaSqMeters = 85.0,
                    amenities = "Parqueadero subterráneo, Bodega, Guardia 24/7, Ascensor, Gas centralizado, Gimnasio, Mascotas pequeñas permitidas",
                    imageDrawableRes = "img_dept_modern",
                    isAvailable = true,
                    bankAccountId = bankId1,
                    landlordName = "Ing. Carlos Mendoza Viteri",
                    landlordPhone = "+593 99 823 4512",
                    landlordEmail = "carlos.mendoza.arriendos@gmail.com",
                    landlordIdCard = "1718293041"
                )
            )

            val propId2 = dao.insertProperty(
                PropertyEntity(
                    title = "Local Comercial en Esquina de Alto Tráfico",
                    description = "Amplio local comercial a pie de calle con doble vitrina de exhibición de vidrio templado, excelente iluminación LED, área de bodega posterior y baño privado. Ideal para franquicias, farmacia, tienda o cafetería.",
                    propertyType = PropertyType.LOCAL,
                    sectorType = SectorType.URBANO,
                    city = "Guayaquil",
                    neighborhood = "Urdesa Central",
                    address = "Av. Víctor Emilio Estrada y Las Monjas, esquina",
                    monthlyRent = 850.0,
                    guaranteeAmount = 1700.0,
                    bedrooms = 0,
                    bathrooms = 2,
                    areaSqMeters = 110.0,
                    amenities = "Vidrio templado antirobo, Sistema contra incendios, Trifásica instalada, Puerta enrollable automática, Parqueo clientes",
                    imageDrawableRes = "img_local_front",
                    isAvailable = true,
                    bankAccountId = bankId2,
                    landlordName = "Dra. Elena Salazar Morales",
                    landlordPhone = "+593 98 741 0293",
                    landlordEmail = "elena.salazar.propiedades@gmail.com",
                    landlordIdCard = "0921847593"
                )
            )

            val propId3 = dao.insertProperty(
                PropertyEntity(
                    title = "Casa Residencial Familiar con Jardín y Garaje",
                    description = "Espaciosa casa de 2 plantas dentro de urbanización privada con garita de seguridad. Posee sala con chimenea, comedor independiente, cocina cerrada con alacena, patio con pérgola y zona BBQ para eventos familiares.",
                    propertyType = PropertyType.CASA,
                    sectorType = SectorType.URBANO,
                    city = "Cuenca",
                    neighborhood = "Challuabamba / Urbanización Los Sauces",
                    address = "Calle Los Cipreses y Av. Los Cerezos, Lote 14",
                    monthlyRent = 720.0,
                    guaranteeAmount = 1440.0,
                    bedrooms = 4,
                    bathrooms = 3,
                    areaSqMeters = 220.0,
                    amenities = "Garaje 2 autos, Patio con BBQ, Seguridad privada 24h, Áreas verdes comunales, Calefón a gas, Mascotas permitidas",
                    imageDrawableRes = "img_house_urban",
                    isAvailable = true,
                    bankAccountId = bankId1,
                    landlordName = "Ing. Carlos Mendoza Viteri",
                    landlordPhone = "+593 99 823 4512",
                    landlordEmail = "carlos.mendoza.arriendos@gmail.com",
                    landlordIdCard = "1718293041"
                )
            )

            val propId4 = dao.insertProperty(
                PropertyEntity(
                    title = "Casa Campestre Finca Rural con Vista a la Montaña",
                    description = "Tranquila y acogedora propiedad campestre en sector rural con aire puro, huerto frutal, corredor exterior con hamacas, cabaña rústica de madera y ladrillo visto. Perfecta para descanso o teletrabajo en la naturaleza.",
                    propertyType = PropertyType.CASA,
                    sectorType = SectorType.RURAL,
                    city = "Valle de Tumbaco / Yaruquí",
                    neighborhood = "Sector Campiña El Quinche",
                    address = "Camino Real s/n a 500m de la Vía Principal",
                    monthlyRent = 450.0,
                    guaranteeAmount = 900.0,
                    bedrooms = 3,
                    bathrooms = 2,
                    areaSqMeters = 350.0,
                    amenities = "Terreno 1500m², Huerto de árboles frutales, Agua de riego, Internet fibra óptica rural, Horno de leña, Se aceptan mascotas",
                    imageDrawableRes = "img_country_rural",
                    isAvailable = true,
                    bankAccountId = bankId3,
                    landlordName = "Ing. Roberto Alvear",
                    landlordPhone = "+593 96 112 3489",
                    landlordEmail = "roberto.alvear@gmail.com",
                    landlordIdCard = "1802934812"
                )
            )

            // Calculate dates for sample contract
            val now = Calendar.getInstance()
            val startCal = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.MONTH, -2) // Started 2 months ago
            }
            val endCal = Calendar.getInstance().apply {
                timeInMillis = startCal.timeInMillis
                add(Calendar.MONTH, 12) // 12 months duration
            }

            // Seed Active Lease Contract
            val contractId1 = dao.insertContract(
                LeaseContractEntity(
                    contractNumber = "CTR-2026-0042",
                    propertyId = propId1,
                    propertyTitle = "Departamento Moderno Vista Panorámica",
                    propertyAddress = "Av. República de El Salvador N34-120 y Moscú, Quito",
                    sectorType = SectorType.URBANO,
                    landlordName = "Ing. Carlos Mendoza Viteri",
                    landlordIdCard = "1718293041",
                    landlordPhone = "+593 99 823 4512",
                    landlordEmail = "carlos.mendoza.arriendos@gmail.com",
                    tenantName = "Lic. Andrea Belén Paredes",
                    tenantIdCard = "1724589312",
                    tenantPhone = "+593 99 501 8823",
                    tenantEmail = "andrea.paredes@gmail.com",
                    monthlyRent = 580.0,
                    guaranteeAmount = 1160.0,
                    startDate = startCal.timeInMillis,
                    endDate = endCal.timeInMillis,
                    durationMonths = 12,
                    renewalType = RenewalType.AUTOMATICA,
                    status = ContractStatus.VIGENTE_FIRMADO,
                    landlordSignatureSvg = "M10,40 Q30,10 50,45 T90,20",
                    tenantSignatureSvg = "M15,35 Q40,15 65,30 T110,25",
                    signedDate = startCal.timeInMillis,
                    digitalVerificationHash = "SHA256-7F89B2E310CA4E8A91C23D88401E55AA993D7102BC4E67FF",
                    linkedBankAccountId = bankId1,
                    bankAccountSummary = "Coop. JEP (Ahorros) No. 406089234101 - Ing. Carlos Mendoza",
                    clausesNotes = "El arrendatario se compromete al pago del canon dentro de los primeros 5 días de cada mes. Garantía de $1160 depositada."
                )
            )

            // Seed Payments
            val dueCal1 = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 5)
            }
            val dueCal2 = Calendar.getInstance().apply {
                add(Calendar.MONTH, -1)
                set(Calendar.DAY_OF_MONTH, 5)
            }

            dao.insertPayment(
                PaymentRecordEntity(
                    contractId = contractId1,
                    propertyId = propId1,
                    monthPeriod = "Mes Actual (Septiembre 2026)",
                    amount = 580.0,
                    dueDate = dueCal1.timeInMillis,
                    status = PaymentStatus.PENDIENTE,
                    bankAccountId = bankId1
                )
            )

            dao.insertPayment(
                PaymentRecordEntity(
                    contractId = contractId1,
                    propertyId = propId1,
                    monthPeriod = "Mes Anterior (Agosto 2026)",
                    amount = 580.0,
                    dueDate = dueCal2.timeInMillis,
                    paymentDate = dueCal2.timeInMillis,
                    status = PaymentStatus.VERIFICADO,
                    referenceNumber = "TRF-JEP-982310",
                    receiptProofNotes = "Transferencia directa acreditada con éxito",
                    bankAccountId = bankId1
                )
            )

            // Seed Notifications
            dao.insertNotification(
                AppNotificationEntity(
                    title = "Recordatorio de Pago de Arriendo",
                    message = "Estimada Andrea, el canon mensual de $580.00 USD para el Depto en La Carolina vence en 3 días. Por favor realiza el depósito en Coop. JEP Cta. Ahorros #406089234101.",
                    type = NotificationType.PAGO_PENDIENTE,
                    timestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 2), // 2 hours ago
                    isRead = false,
                    targetRole = UserRole.ARRENDATARIO,
                    relatedId = contractId1
                )
            )

            dao.insertNotification(
                AppNotificationEntity(
                    title = "Aviso para Propietario: Estado de Cobro",
                    message = "El canon de arrendamiento de $580.00 del contrato #CTR-2026-0042 está programado para acreditación en su cuenta Coop. JEP en los próximos 3 días.",
                    type = NotificationType.PAGO_PENDIENTE,
                    timestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 4),
                    isRead = false,
                    targetRole = UserRole.ARRENDADOR,
                    relatedId = contractId1
                )
            )

            dao.insertNotification(
                AppNotificationEntity(
                    title = "Vigencia de Contrato Digital",
                    message = "El contrato de arriendo #CTR-2026-0042 cuenta con 10 meses de vigencia restante con cláusula de Renovación Automática activa.",
                    type = NotificationType.CONTRATO_VIGENCIA,
                    timestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 24),
                    isRead = true,
                    targetRole = UserRole.ARRENDATARIO,
                    relatedId = contractId1
                )
            )
        }
    }
}
