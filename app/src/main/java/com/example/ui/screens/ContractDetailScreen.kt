package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ContractStatus
import com.example.data.model.LeaseContractEntity
import com.example.data.model.RenewalType
import com.example.data.model.UserRole
import com.example.ui.MainViewModel
import com.example.ui.components.DigitalSignaturePad
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldDark
import com.example.ui.theme.AmberGoldLight
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueSoft
import com.example.ui.theme.GoldSeal
import com.example.ui.theme.GreenSoft
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractDetailScreen(
    contract: LeaseContractEntity,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showRenewalDialog by remember { mutableStateOf(false) }
    var renewalMonths by remember { mutableIntStateOf(12) }
    var renewalRentStr by remember { mutableStateOf(contract.monthlyRent.toInt().toString()) }
    var selectedRenewalType by remember { mutableStateOf(contract.renewalType) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    val now = System.currentTimeMillis()
    val totalDurationMillis = (contract.endDate - contract.startDate).coerceAtLeast(1)
    val elapsedMillis = (now - contract.startDate).coerceIn(0, totalDurationMillis)
    val progress = (elapsedMillis.toFloat() / totalDurationMillis.toFloat()).coerceIn(0f, 1f)
    val remainingDays = ((contract.endDate - now) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)

    val isFullySigned = contract.landlordSignatureSvg.isNotEmpty() && contract.tenantSignatureSvg.isNotEmpty()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Contrato ${contract.contractNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.triggerContractExpiryAlert(contract)
                            scope.launch {
                                snackbarHostState.showSnackbar("Notificación de vigencia enviada al celular")
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Alerta Celular",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Status and Validity Countdown Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = when (contract.status) {
                                ContractStatus.VIGENTE_FIRMADO -> GreenSoft
                                ContractStatus.PENDIENTE_FIRMA -> AmberGoldLight
                                ContractStatus.RENOVADO -> TealLight
                                else -> BlueSoft
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isFullySigned) Icons.Default.Verified else Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = if (isFullySigned) GreenSuccess else AmberGoldDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isFullySigned) "VIGENTE Y FIRMADO" else contract.status.displayName.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFullySigned) GreenSuccess else AmberGoldDark
                                )
                            }
                        }

                        Text(
                            text = "${contract.durationMonths} Meses de Plazo",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress bar for contract duration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Inicio: ${dateFormat.format(Date(contract.startDate))}", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        Text("Fin: ${dateFormat.format(Date(contract.endDate))}", style = MaterialTheme.typography.labelSmall, color = Slate500)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = if (remainingDays < 30) AmberGold else TealPrimary,
                        trackColor = Slate200
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Vigencia Restante: $remainingDays días",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (remainingDays < 30) AmberGoldDark else NavyDark
                        )

                        Text(
                            text = contract.renewalType.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = TealDark,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action buttons: Renew or Push notification
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { showRenewalDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("renew_contract_button")
                        ) {
                            Icon(Icons.Default.Autorenew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Renovar Contrato", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.triggerContractExpiryAlert(contract)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Alerta de vigencia enviada al celular del Arrendador y Arrendatario")
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Enviar Alerta", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Official Legal Document Container (Paper style)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("contract_document_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFBF7)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2D9C8)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Document Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "REPÚBLICA DEL ECUADOR",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Slate700,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "CONTRATO DIGITAL DE ARRENDAMIENTO",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyDark
                            )
                            Text(
                                text = "No. ${contract.contractNumber}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldSeal
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(GoldSeal.copy(alpha = 0.15f), CircleShape)
                                .border(1.dp, GoldSeal, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Sello Legal",
                                tint = GoldSeal,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFE2D9C8))
                    Spacer(modifier = Modifier.height(14.dp))

                    // CLAUSE 1: PARTIES
                    ClauseSection(
                        title = "CLÁUSULA PRIMERA: COMPARECIENTES",
                        content = "Comparecen a la celebración del presente Contrato Digital de Arrendamiento, por una parte como ARRENDADOR/A: ${contract.landlordName}, portador/a de la Cédula/RUC No. ${contract.landlordIdCard}, con teléfono ${contract.landlordPhone}; y por otra parte como ARRENDATARIO/A: ${contract.tenantName}, portador/a de la Cédula No. ${contract.tenantIdCard}, teléfono ${contract.tenantPhone}, quienes libre y voluntariamente convienen en suscribir las siguientes estipulaciones:"
                    )

                    // CLAUSE 2: OBJECT & LOCATION & SECTOR
                    ClauseSection(
                        title = "CLÁUSULA SEGUNDA: INMUEBLE Y SECTOR",
                        content = "El Arrendador da en arrendamiento el inmueble denominado '${contract.propertyTitle}', ubicado en la dirección: ${contract.propertyAddress}, clasificado expresamente en ${contract.sectorType.displayName.uppercase()} (${contract.sectorType.description}). El inmueble se entrega en óptimas condiciones de habitabilidad y operatividad."
                    )

                    // CLAUSE 3: MONTHLY RENT & BANK ACCOUNT
                    ClauseSection(
                        title = "CLÁUSULA TERCERA: CANON DE ARRENDAMIENTO Y CUENTA BANCARIA / COOPERATIVA",
                        content = "El canon de arrendamiento mensual pactado es de $${"%.2f".format(contract.monthlyRent)} DÓLARES DE LOS ESTADOS UNIDOS DE AMÉRICA ($ USD), el cual deberá ser cancelado de forma cumplida dentro de los primeros cinco (5) días de cada mes calendario mediante depósito o transferencia directa a la cuenta designada:\n• ${contract.bankAccountSummary}.\nEl sistema emitirá notificación de recordatorio de cobro y pago a los celulares de ambas partes."
                    )

                    // CLAUSE 4: WARRANTY DEPOSIT
                    ClauseSection(
                        title = "CLÁUSULA CUARTA: DEPÓSITO EN GARANTÍA",
                        content = "El Arrendatario entrega en este acto la cantidad de $${"%.2f".format(contract.guaranteeAmount)} USD en calidad de GARANTÍA para responder por posibles daños que no sean imputables al normal deterioro por uso, así como servicios básicos pendientes al desocupar el bien. Dicho monto será devuelto íntegramente al término del contrato."
                    )

                    // CLAUSE 5: DURATION & DATES
                    ClauseSection(
                        title = "CLÁUSULA QUINTA: PLAZO DE VIGENCIA",
                        content = "El plazo de vigencia del presente contrato es de ${contract.durationMonths} MESES, contados a partir del ${dateFormat.format(Date(contract.startDate))}, venciendo indefectiblemente el día ${dateFormat.format(Date(contract.endDate))}."
                    )

                    // CLAUSE 6: RENEWAL CLAUSE & NOTIFICATION
                    ClauseSection(
                        title = "CLÁUSULA SEXTA: RENOVACIÓN DEL CONTRATO Y NOTIFICACIONES",
                        content = "Modalidad pactada: ${contract.renewalType.displayName.uppercase()}. ${contract.renewalType.description}. El sistema digital emitirá avisos y alertas preventivas al celular de las partes con 30 días de anticipación para acordar la prórroga o firma de la renovación digital."
                    )

                    // CLAUSE 7: SPECIAL CLAUSES
                    if (contract.clausesNotes.isNotEmpty()) {
                        ClauseSection(
                            title = "CLÁUSULA SÉPTIMA: CONDICIONES PARTICULARES",
                            content = contract.clausesNotes
                        )
                    }

                    // CLAUSE 8: DIGITAL VALIDITY & SIGNATURES
                    ClauseSection(
                        title = "CLÁUSULA OCTAVA: VALIDEZ DE FIRMA ELECTRÓNICA Y ACEPTACIÓN",
                        content = "Las partes aceptan y ratifican todas las cláusulas mediante firma digital táctil estampada en el dispositivo móvil, la cual goza de plena validez jurídica, autenticada mediante sello de tiempo y cadena criptográfica hash."
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFE2D9C8))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Digital Signatures Display
                    Text(
                        text = "FIRMAS DIGITALES REGISTRADAS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Landlord Signature Stamp
                        SignatureDisplayBox(
                            title = "ARRENDADOR(A)",
                            name = contract.landlordName,
                            idCard = contract.landlordIdCard,
                            hasSignature = contract.landlordSignatureSvg.isNotEmpty(),
                            modifier = Modifier.weight(1f)
                        )

                        // Tenant Signature Stamp
                        SignatureDisplayBox(
                            title = "ARRENDATARIO(A)",
                            name = contract.tenantName,
                            idCard = contract.tenantIdCard,
                            hasSignature = contract.tenantSignatureSvg.isNotEmpty(),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (contract.digitalVerificationHash.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate100, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = TealDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Verificación Digital Blockchain / Hash SHA-256:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TealDark,
                                        fontSize = 10.sp
                                    )
                                }
                                Text(
                                    text = contract.digitalVerificationHash,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate500,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SIGNATURE INTERACTIVE PADS
            Text(
                text = "Lienzo de Firma Digital Táctil",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
            Text(
                text = "Estampe su firma con el dedo en el recuadro para validar el contrato digitalmente:",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tenant Signature Pad
            DigitalSignaturePad(
                signerRoleTitle = "Firma del Arrendatario / Inquilino",
                signerName = contract.tenantName,
                signerIdCard = contract.tenantIdCard,
                existingSignatureSvg = contract.tenantSignatureSvg,
                onSignatureConfirmed = { svg, hash, _ ->
                    viewModel.signContract(contract, isLandlord = false, svgStrokeData = svg, hash = hash) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Firma del Arrendatario registrada con éxito")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Landlord Signature Pad
            DigitalSignaturePad(
                signerRoleTitle = "Firma del Arrendador / Propietario",
                signerName = contract.landlordName,
                signerIdCard = contract.landlordIdCard,
                existingSignatureSvg = contract.landlordSignatureSvg,
                onSignatureConfirmed = { svg, hash, _ ->
                    viewModel.signContract(contract, isLandlord = true, svgStrokeData = svg, hash = hash) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Firma del Arrendador registrada con éxito")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Renewal Dialog
    if (showRenewalDialog) {
        AlertDialog(
            onDismissRequest = { showRenewalDialog = false },
            title = {
                Text(
                    text = "Renovación de Contrato de Arriendo",
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            },
            text = {
                Column {
                    Text(
                        text = "Establece el nuevo plazo de vigencia y canon acordado para la renovación de '${contract.propertyTitle}':",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Meses de prórroga:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(6, 12, 24).forEach { months ->
                            FilterChip(
                                selected = renewalMonths == months,
                                onClick = { renewalMonths = months },
                                label = { Text("$months m") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = renewalRentStr,
                        onValueChange = { renewalRentStr = it },
                        label = { Text("Nuevo Canon Mensual ($ USD)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Tipo de renovación:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    RenewalType.values().take(3).forEach { rType ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            FilterChip(
                                selected = selectedRenewalType == rType,
                                onClick = { selectedRenewalType = rType },
                                label = { Text(rType.displayName, fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val rent = renewalRentStr.toDoubleOrNull() ?: contract.monthlyRent
                        viewModel.renewContract(
                            contract = contract,
                            extendedMonths = renewalMonths,
                            newMonthlyRent = rent,
                            renewalType = selectedRenewalType,
                            onSuccess = {
                                showRenewalDialog = false
                                scope.launch {
                                    snackbarHostState.showSnackbar("Contrato renovado por $renewalMonths meses")
                                }
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Confirmar Renovación")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenewalDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ClauseSection(
    title: String,
    content: String
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = NavyDark
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            color = Slate800,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun SignatureDisplayBox(
    title: String,
    name: String,
    idCard: String,
    hasSignature: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (hasSignature) GreenSoft else Slate100,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (hasSignature) GreenSuccess else Slate500
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (hasSignature) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = "Firmado",
                    tint = GreenSuccess,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Firma Digital Válida",
                    style = MaterialTheme.typography.labelSmall,
                    color = GreenSuccess,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "(Pendiente de firma)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate400
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = NavyDark,
                textAlign = TextAlign.Center
            )
            Text(
                text = "CI: $idCard",
                style = MaterialTheme.typography.labelSmall,
                color = Slate500,
                fontSize = 10.sp
            )
        }
    }
}
