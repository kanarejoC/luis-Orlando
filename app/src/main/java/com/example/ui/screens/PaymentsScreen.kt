package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BankAccountEntity
import com.example.data.model.PaymentRecordEntity
import com.example.data.model.PaymentStatus
import com.example.ui.MainViewModel
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldDark
import com.example.ui.theme.AmberGoldLight
import com.example.ui.theme.BlueSoft
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
fun PaymentsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val payments by viewModel.payments.collectAsStateWithLifecycle()
    val bankAccounts by viewModel.bankAccounts.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var paymentToProcess by remember { mutableStateOf<PaymentRecordEntity?>(null) }
    var showReceiptFor by remember { mutableStateOf<PaymentRecordEntity?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Canones y Cuentas de Arriendo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
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
        ) {
            // Tabs: Pagos de Canon vs Cuentas Bancarias
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = NavyPrimary
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pagos de Canon (${payments.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cuentas Coop / Banco", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            if (selectedTabIndex == 0) {
                // Payments List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Summary Banner
                    item {
                        Surface(
                            color = Slate100,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Control Mensual de Pagos",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyDark
                                    )
                                    Text(
                                        text = "Notificaciones automáticas para cobro y comprobantes digitales",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate500,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    items(payments, key = { it.id }) { payment ->
                        val linkedBank = bankAccounts.find { it.id == payment.bankAccountId }
                            ?: bankAccounts.firstOrNull()

                        PaymentRecordCard(
                            payment = payment,
                            bankAccount = linkedBank,
                            onPayNow = { paymentToProcess = payment },
                            onViewReceipt = { showReceiptFor = payment },
                            onSendReminder = {
                                viewModel.triggerPaymentReminder(payment)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Notificación de cobro enviada al celular del Arrendatario")
                                }
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            } else {
                // Bank Accounts embedded view
                BankAccountsScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Payment Processing Modal Dialog
    if (paymentToProcess != null) {
        val payment = paymentToProcess!!
        val linkedBank = bankAccounts.find { it.id == payment.bankAccountId } ?: bankAccounts.firstOrNull()

        ProcessPaymentDialog(
            payment = payment,
            bankAccount = linkedBank,
            onDismiss = { paymentToProcess = null },
            onConfirmPayment = { method, ref, notes ->
                viewModel.submitPayment(
                    payment = payment,
                    paymentMethod = method,
                    referenceNumber = ref,
                    receiptProofNotes = notes,
                    onSuccess = {
                        paymentToProcess = null
                        scope.launch {
                            snackbarHostState.showSnackbar("¡Canon de arriendo pagado y acreditado con éxito!")
                        }
                    }
                )
            }
        )
    }

    // Digital Receipt Viewer Dialog
    if (showReceiptFor != null) {
        val payment = showReceiptFor!!
        val linkedBank = bankAccounts.find { it.id == payment.bankAccountId } ?: bankAccounts.firstOrNull()

        DigitalReceiptDialog(
            payment = payment,
            bankAccount = linkedBank,
            onDismiss = { showReceiptFor = null }
        )
    }
}

@Composable
private fun PaymentRecordCard(
    payment: PaymentRecordEntity,
    bankAccount: BankAccountEntity?,
    onPayNow: () -> Unit,
    onViewReceipt: () -> Unit,
    onSendReminder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    val isPaid = payment.status == PaymentStatus.VERIFICADO

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("payment_card_${payment.id}"),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = if (isPaid) GreenSuccess else AmberGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = payment.monthPeriod,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isPaid) GreenSoft else AmberGoldLight
                ) {
                    Text(
                        text = payment.status.displayName.uppercase(),
                        color = if (isPaid) GreenSuccess else AmberGoldDark,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Amount and Due Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$${payment.amount.toInt()}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyPrimary
                    )
                    Text(
                        text = " USD",
                        style = MaterialTheme.typography.labelMedium,
                        color = Slate500,
                        modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                    )
                }

                Text(
                    text = "Vence: ${dateFormat.format(Date(payment.dueDate))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate700,
                    fontWeight = FontWeight.Medium
                )
            }

            if (bankAccount != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = Slate500,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Cuenta de Destino: ${bankAccount.institutionName} (#${bankAccount.accountNumber})",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isPaid) {
                    Button(
                        onClick = onPayNow,
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pay_rent_button_${payment.id}")
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pagar Canon", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onSendReminder,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Recordatorio", fontSize = 12.sp)
                    }
                } else {
                    OutlinedButton(
                        onClick = onViewReceipt,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("view_receipt_button_${payment.id}")
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ver Comprobante Digital (Ref: ${payment.referenceNumber})", color = GreenSuccess, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProcessPaymentDialog(
    payment: PaymentRecordEntity,
    bankAccount: BankAccountEntity?,
    onDismiss: () -> Unit,
    onConfirmPayment: (String, String, String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("Transferencia Directa Coop / Banco") }
    var referenceNumber by remember { mutableStateOf("TRF-${(100000..999999).random()}") }
    var notes by remember { mutableStateOf("Pago puntual mensual canon de arriendo.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Pagar Canon de Arriendo", fontWeight = FontWeight.Bold, color = NavyDark)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Periodo: ${payment.monthPeriod} • Monto: $${payment.amount.toInt()} USD",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TealDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (bankAccount != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate100)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Cuenta Receptora:", style = MaterialTheme.typography.labelSmall, color = Slate500, fontWeight = FontWeight.Bold)
                            Text(bankAccount.institutionName, fontWeight = FontWeight.Bold, color = NavyDark, style = MaterialTheme.typography.bodySmall)
                            Text("Cta. ${bankAccount.accountType.displayName} #${bankAccount.accountNumber}", style = MaterialTheme.typography.bodySmall, color = Slate700)
                            Text("Beneficiario: ${bankAccount.accountHolderName} (CI: ${bankAccount.accountHolderIdCard})", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Método de Pago:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                listOf("Transferencia Directa Coop / Banco", "Depósito en Ventanilla", "Tarjeta de Débito/Crédito").forEach { m ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMethod = m }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = selectedMethod == m,
                            onClick = { selectedMethod = m },
                            label = { Text(m, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = referenceNumber,
                    onValueChange = { referenceNumber = it },
                    label = { Text("No. Referencia / Comprobante") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observaciones") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmPayment(selectedMethod, referenceNumber, notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Confirmar y Registrar Pago")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun DigitalReceiptDialog(
    payment: PaymentRecordEntity,
    bankAccount: BankAccountEntity?,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val paymentDateVal = payment.paymentDate ?: 0L
    val dateStr = if (paymentDateVal > 0) dateFormat.format(Date(paymentDateVal)) else "Registrado"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = GreenSuccess)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Comprobante Digital de Pago", fontWeight = FontWeight.Bold, color = NavyDark)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text("RECIBO ELECTRÓNICO DE ARRENDAMIENTO", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = Slate500)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Canon: ${payment.monthPeriod}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = NavyDark)
                Text("Monto Pagado: $${payment.amount.toInt()} USD", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GreenSuccess)
                Spacer(modifier = Modifier.height(10.dp))
                Text("• Referencia: ${payment.referenceNumber}", style = MaterialTheme.typography.bodySmall, color = Slate700)
                Text("• Fecha y Hora: $dateStr", style = MaterialTheme.typography.bodySmall, color = Slate700)
                Text("• Método: ${payment.paymentMethod}", style = MaterialTheme.typography.bodySmall, color = Slate700)
                if (bankAccount != null) {
                    Text("• Acreditado a: ${bankAccount.institutionName} (${bankAccount.accountNumber})", style = MaterialTheme.typography.bodySmall, color = Slate700)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Este comprobante digital ha sido notificado al celular del arrendador y arrendatario como constancia formal de pago.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500,
                    fontSize = 10.sp
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)) {
                Text("Cerrar")
            }
        }
    )
}
