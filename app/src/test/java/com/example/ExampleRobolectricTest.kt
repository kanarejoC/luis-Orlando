package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDao
import com.example.data.db.AppDatabase
import com.example.data.model.AccountType
import com.example.data.model.BankAccountEntity
import com.example.data.model.ContractStatus
import com.example.data.model.InstitutionType
import com.example.data.model.LeaseContractEntity
import com.example.data.model.PaymentRecordEntity
import com.example.data.model.PaymentStatus
import com.example.data.model.PropertyEntity
import com.example.data.model.PropertyType
import com.example.data.model.RenewalType
import com.example.data.model.SectorType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AppDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.appDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testAppTitleResource() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ArriendoDigital", appName)
    }

    @Test
    fun testInsertAndQueryPropertyWithRuralUrbanSector() = runBlocking {
        val prop = PropertyEntity(
            title = "Casa Campestre San José",
            description = "Hermosa casa rural con huerto frutal",
            propertyType = PropertyType.CASA,
            sectorType = SectorType.RURAL,
            city = "Cayambe",
            neighborhood = "Cangahua",
            address = "Vía Principal km 4",
            monthlyRent = 550.0,
            guaranteeAmount = 1100.0,
            bedrooms = 3,
            bathrooms = 2,
            areaSqMeters = 160.0,
            amenities = "Agua de vertiente, Chimenea, Jardín",
            imageDrawableRes = "img_country_rural",
            landlordName = "Don Carlos Morales",
            landlordPhone = "0987654321",
            landlordEmail = "carlos@finca.ec",
            landlordIdCard = "1709823412"
        )
        val id = dao.insertProperty(prop)
        val fetched = dao.getPropertyById(id)

        assertNotNull(fetched)
        assertEquals("Casa Campestre San José", fetched?.title)
        assertEquals(SectorType.RURAL, fetched?.sectorType)
        assertEquals(550.0, fetched?.monthlyRent ?: 0.0, 0.01)
        assertEquals(1100.0, fetched?.guaranteeAmount ?: 0.0, 0.01)
    }

    @Test
    fun testBankAccountLinkingAndDefault() = runBlocking {
        val coopAccount = BankAccountEntity(
            institutionName = "Coop. de Ahorro y Crédito JEP",
            institutionType = InstitutionType.COOPERATIVA,
            accountType = AccountType.AHORROS,
            accountNumber = "406001234567",
            accountHolderName = "Juan Propietario",
            accountHolderIdCard = "1719823451",
            notificationEmail = "cobros@arriendos.ec",
            isDefault = true
        )
        val id = dao.insertBankAccount(coopAccount)
        val defaultAcc = dao.getDefaultBankAccount()

        assertNotNull(defaultAcc)
        assertEquals("Coop. de Ahorro y Crédito JEP", defaultAcc?.institutionName)
        assertEquals(InstitutionType.COOPERATIVA, defaultAcc?.institutionType)
        assertEquals("406001234567", defaultAcc?.accountNumber)
    }

    @Test
    fun testDigitalContractCreationAndRenewal() = runBlocking {
        val start = System.currentTimeMillis()
        val end = start + (365L * 24 * 60 * 60 * 1000)

        val contract = LeaseContractEntity(
            contractNumber = "CTR-2026-9001",
            propertyId = 1,
            propertyTitle = "Local Comercial Av. Amazonas",
            propertyAddress = "Av. Amazonas y Colón, Quito",
            sectorType = SectorType.URBANO,
            landlordName = "Dra. Patricia Silva",
            landlordIdCard = "1714523698",
            landlordPhone = "0998877665",
            landlordEmail = "patricia@inmuebles.ec",
            tenantName = "Ing. Roberto Salazar",
            tenantIdCard = "1720984561",
            tenantPhone = "0984512345",
            tenantEmail = "roberto.salazar@gmail.com",
            monthlyRent = 750.0,
            guaranteeAmount = 1500.0,
            startDate = start,
            endDate = end,
            durationMonths = 12,
            renewalType = RenewalType.AUTOMATICA,
            status = ContractStatus.PENDIENTE_FIRMA
        )
        val contractId = dao.insertContract(contract)

        // Sign contract
        val fetched = dao.getContractById(contractId)
        assertNotNull(fetched)

        val signedContract = fetched!!.copy(
            tenantSignatureSvg = "M10,10 L50,50 L100,20;",
            landlordSignatureSvg = "M20,20 L60,60 L120,30;",
            digitalVerificationHash = "SHA256-FA98B23C89DF00E",
            status = ContractStatus.VIGENTE_FIRMADO
        )
        dao.updateContract(signedContract)

        val verified = dao.getContractById(contractId)
        assertEquals(ContractStatus.VIGENTE_FIRMADO, verified?.status)
        assertTrue(verified?.tenantSignatureSvg?.isNotEmpty() == true)
        assertTrue(verified?.landlordSignatureSvg?.isNotEmpty() == true)
    }

    @Test
    fun testMonthlyPaymentWorkflow() = runBlocking {
        val payment = PaymentRecordEntity(
            contractId = 1,
            propertyId = 1,
            monthPeriod = "Canon Noviembre 2026",
            amount = 450.0,
            dueDate = System.currentTimeMillis(),
            status = PaymentStatus.PENDIENTE
        )
        val pId = dao.insertPayment(payment)

        val fetched = dao.getAllPayments().first().first { it.id == pId }
        assertEquals(PaymentStatus.PENDIENTE, fetched.status)

        // Pay and verify
        val paid = fetched.copy(
            status = PaymentStatus.VERIFICADO,
            paymentMethod = "Transferencia Directa Coop JEP",
            referenceNumber = "TRF-887766",
            paymentDate = System.currentTimeMillis()
        )
        dao.updatePayment(paid)

        val updated = dao.getAllPayments().first().first { it.id == pId }
        assertEquals(PaymentStatus.VERIFICADO, updated.status)
        assertEquals("TRF-887766", updated.referenceNumber)
    }
}
