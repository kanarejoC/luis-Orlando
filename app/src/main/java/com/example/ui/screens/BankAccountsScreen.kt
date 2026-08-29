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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AccountType
import com.example.data.model.BankAccountEntity
import com.example.data.model.InstitutionType
import com.example.ui.MainViewModel
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldDark
import com.example.ui.theme.AmberGoldLight
import com.example.ui.theme.GreenSoft
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankAccountsScreen(
    viewModel: MainViewModel,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val accounts by viewModel.bankAccounts.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cuentas de Banco y Cooperativas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = TealPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_bank_account_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Vincular Cuenta")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Explanation Banner
            Surface(
                color = Slate100,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Recepción de Canones de Arriendo",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                        Text(
                            text = "Vincula tus cuentas de bancos o cooperativas de ahorro y crédito para que los arrendatarios paguen mensualmente con respaldo digital.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Account list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(accounts, key = { it.id }) { account ->
                    BankAccountCard(
                        account = account,
                        onSetDefault = {
                            viewModel.setDefaultBankAccount(account.id)
                            scope.launch {
                                snackbarHostState.showSnackbar("Cuenta predeterminada actualizada")
                            }
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showAddDialog) {
        AddBankAccountDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { instName, instType, accType, accNumber, holderName, holderId, email, isDefault ->
                viewModel.addBankAccount(
                    institutionName = instName,
                    institutionType = instType,
                    accountType = accType,
                    accountNumber = accNumber,
                    accountHolderName = holderName,
                    accountHolderIdCard = holderId,
                    notificationEmail = email,
                    isDefault = isDefault
                ) {
                    showAddDialog = false
                    scope.launch {
                        snackbarHostState.showSnackbar("Cuenta bancaria/cooperativa vinculada con éxito")
                    }
                }
            }
        )
    }
}

@Composable
private fun BankAccountCard(
    account: BankAccountEntity,
    onSetDefault: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bank_account_card_${account.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (account.isDefault) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TealPrimary)) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (account.institutionType == InstitutionType.COOPERATIVA) AmberGoldLight else TealLight,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = if (account.institutionType == InstitutionType.COOPERATIVA) AmberGoldDark else TealDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = account.institutionName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                        Text(
                            text = account.institutionType.displayName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                }

                if (account.isDefault) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GreenSoft
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Predeterminada",
                                tint = GreenSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Principal",
                                color = GreenSuccess,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Account details box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate100, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tipo de Cuenta:", style = MaterialTheme.typography.labelSmall, color = Slate500)
                    Text(account.accountType.displayName, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Slate800)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Número de Cuenta:", style = MaterialTheme.typography.labelSmall, color = Slate500)
                    Text(account.accountNumber, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = NavyPrimary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Titular:", style = MaterialTheme.typography.labelSmall, color = Slate500)
                    Text("${account.accountHolderName} (CI: ${account.accountHolderIdCard})", style = MaterialTheme.typography.labelSmall, color = Slate800)
                }
            }

            if (!account.isDefault) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onSetDefault) {
                        Text("Establecer como cuenta principal de cobro", fontSize = 12.sp, color = TealDark)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBankAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, InstitutionType, AccountType, String, String, String, String, Boolean) -> Unit
) {
    var institutionName by remember { mutableStateOf("Coop. de Ahorro y Crédito JEP") }
    var institutionType by remember { mutableStateOf(InstitutionType.COOPERATIVA) }
    var accountType by remember { mutableStateOf(AccountType.AHORROS) }
    var accountNumber by remember { mutableStateOf("") }
    var holderName by remember { mutableStateOf("Propietario Inmuebles") }
    var holderId by remember { mutableStateOf("1719823451") }
    var email by remember { mutableStateOf("arriendos@notificaciones.ec") }
    var isDefault by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Vincular Cuenta de Banco / Cooperativa", fontWeight = FontWeight.Bold, color = NavyDark)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Tipo de Entidad Financiera:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = institutionType == InstitutionType.COOPERATIVA,
                        onClick = {
                            institutionType = InstitutionType.COOPERATIVA
                            institutionName = "Coop. de Ahorro y Crédito JEP"
                        },
                        label = { Text("Cooperativa") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberGold,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = institutionType == InstitutionType.BANCO,
                        onClick = {
                            institutionType = InstitutionType.BANCO
                            institutionName = "Banco Pichincha"
                        },
                        label = { Text("Banco") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = institutionName,
                    onValueChange = { institutionName = it },
                    label = { Text("Nombre de la Institución") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = accountType == AccountType.AHORROS,
                        onClick = { accountType = AccountType.AHORROS },
                        label = { Text("Ahorros") }
                    )
                    FilterChip(
                        selected = accountType == AccountType.CORRIENTE,
                        onClick = { accountType = AccountType.CORRIENTE },
                        label = { Text("Corriente") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text("Número de Cuenta *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("account_number_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = holderName,
                    onValueChange = { holderName = it },
                    label = { Text("Nombre del Titular") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = holderId,
                    onValueChange = { holderId = it },
                    label = { Text("Cédula / RUC del Titular") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email para Comprobantes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (accountNumber.isNotBlank()) {
                        onConfirm(institutionName, institutionType, accountType, accountNumber, holderName, holderId, email, isDefault)
                    }
                },
                enabled = accountNumber.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Guardar Cuenta")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
