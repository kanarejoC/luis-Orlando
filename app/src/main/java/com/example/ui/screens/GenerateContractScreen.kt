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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PropertyEntity
import com.example.data.model.RenewalType
import com.example.ui.MainViewModel
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldDark
import com.example.ui.theme.AmberGoldLight
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateContractScreen(
    property: PropertyEntity,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onContractCreated: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var tenantName by remember { mutableStateOf("Lic. Andrea Belén Paredes") }
    var tenantIdCard by remember { mutableStateOf("1724589312") }
    var tenantPhone by remember { mutableStateOf("+593 99 501 8823") }
    var tenantEmail by remember { mutableStateOf("andrea.paredes@gmail.com") }

    var durationMonths by remember { mutableIntStateOf(12) }
    var selectedRenewalType by remember { mutableStateOf(RenewalType.AUTOMATICA) }
    var customClauses by remember {
        mutableStateOf(
            "1. El canon mensual se pagará hasta el día 5 de cada mes.\n" +
            "2. El inmueble se destinará exclusivamente para vivienda/actividad comercial autorizada.\n" +
            "3. La garantía de $${property.guaranteeAmount.toInt()} USD será liquidada al término del contrato previa constatación del estado del inmueble."
        )
    }

    val startCal = Calendar.getInstance()
    val endCal = Calendar.getInstance().apply {
        add(Calendar.MONTH, durationMonths)
    }
    val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Generar Contrato Digital",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            viewModel.createContract(
                                property = property,
                                tenantName = if (tenantName.isBlank()) "Arrendatario Registrado" else tenantName,
                                tenantIdCard = if (tenantIdCard.isBlank()) "9999999999" else tenantIdCard,
                                tenantPhone = tenantPhone,
                                tenantEmail = tenantEmail,
                                durationMonths = durationMonths,
                                renewalType = selectedRenewalType,
                                clausesNotes = customClauses,
                                onSuccess = onContractCreated
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_generate_contract_button")
                    ) {
                        Icon(imageVector = Icons.Default.Description, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Crear Contrato y Abrir Firma Digital",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
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
            // Property Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate100)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = property.propertyType.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TealDark
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (property.sectorType.name == "URBANO") TealLight else Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = property.sectorType.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = property.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                    Text(
                        text = "${property.address}, ${property.city}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Canon: $${property.monthlyRent.toInt()} USD/mes",
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Garantía: $${property.guaranteeAmount.toInt()} USD",
                            fontWeight = FontWeight.Bold,
                            color = AmberGoldDark,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tenant info inputs
            Text(
                text = "Datos del Arrendatario / Inquilino",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = tenantName,
                onValueChange = { tenantName = it },
                label = { Text("Nombre y Apellidos del Arrendatario *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tenant_name_input")
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = tenantIdCard,
                    onValueChange = { tenantIdCard = it },
                    label = { Text("Cédula / Pasaporte *") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = tenantPhone,
                    onValueChange = { tenantPhone = it },
                    label = { Text("Celular / WhatsApp") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = tenantEmail,
                onValueChange = { tenantEmail = it },
                label = { Text("Correo Electrónico para Notificaciones") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Vigencia del Contrato
            Text(
                text = "Plazo de Vigencia del Contrato *",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(6, 12, 24, 36).forEach { months ->
                    FilterChip(
                        selected = durationMonths == months,
                        onClick = { durationMonths = months },
                        label = { Text("$months meses", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dates Preview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TealLight)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = TealDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cronograma de Vigencia ($durationMonths meses)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = TealDark
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Fecha Inicio: ${dateFormat.format(Date(startCal.timeInMillis))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate700
                    )
                    Text(
                        text = "• Fecha Expiración: ${dateFormat.format(Date(endCal.timeInMillis))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate700
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Cláusula de Renovación
            Text(
                text = "Términos de Renovación de Contrato *",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
            Text(
                text = "Se enviarán notificaciones automáticas al celular antes del vencimiento:",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )
            Spacer(modifier = Modifier.height(8.dp))

            RenewalType.values().forEach { type ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedRenewalType = type },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedRenewalType == type) AmberGoldLight else Slate100
                    ),
                    border = if (selectedRenewalType == type) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AmberGold)) else null
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (selectedRenewalType == type) Icons.Default.CheckCircle else Icons.Default.History,
                            contentDescription = null,
                            tint = if (selectedRenewalType == type) AmberGoldDark else Slate500,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = type.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedRenewalType == type) AmberGoldDark else Slate700
                            )
                            Text(
                                text = type.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate500,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Cláusulas Adicionales
            Text(
                text = "Cláusulas Legales y Obligaciones",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = customClauses,
                onValueChange = { customClauses = it },
                label = { Text("Condiciones del Arrendamiento") },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
