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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ContractStatus
import com.example.data.model.LeaseContractEntity
import com.example.data.model.SectorType
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractsScreen(
    viewModel: MainViewModel,
    onSelectContract: (LeaseContractEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val contracts by viewModel.contracts.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf<ContractStatus?>(null) }

    val filteredContracts = contracts.filter {
        selectedFilter == null || it.status == selectedFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Contratos Digitales de Arriendo",
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
            // Header summary Banner
            Surface(
                color = Slate100,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Gestión Legal y Firmas",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                        Text(
                            text = "Contratos con validez electrónica, vigencia y renovación",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500,
                            fontSize = 11.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = TealLight
                    ) {
                        Text(
                            text = "${contracts.size} Registrados",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TealDark,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Status Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("Todos (${contracts.size})", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NavyPrimary,
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedFilter == ContractStatus.VIGENTE_FIRMADO,
                    onClick = { selectedFilter = ContractStatus.VIGENTE_FIRMADO },
                    label = { Text("Vigentes", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenSuccess,
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedFilter == ContractStatus.PENDIENTE_FIRMA,
                    onClick = { selectedFilter = ContractStatus.PENDIENTE_FIRMA },
                    label = { Text("Pendientes", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AmberGold,
                        selectedLabelColor = Color.White
                    )
                )
            }

            // Contracts List
            if (filteredContracts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No hay contratos en esta categoría",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Selecciona un inmueble en el catálogo y genera su contrato digital con firma táctil.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredContracts, key = { it.id }) { contract ->
                        ContractCardItem(
                            contract = contract,
                            onClick = {
                                viewModel.selectContract(contract)
                                onSelectContract(contract)
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ContractCardItem(
    contract: LeaseContractEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    val now = System.currentTimeMillis()
    val totalDurationMillis = (contract.endDate - contract.startDate).coerceAtLeast(1)
    val elapsedMillis = (now - contract.startDate).coerceIn(0, totalDurationMillis)
    val progress = (elapsedMillis.toFloat() / totalDurationMillis.toFloat()).coerceIn(0f, 1f)
    val remainingDays = ((contract.endDate - now) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)

    val isSigned = contract.landlordSignatureSvg.isNotEmpty() && contract.tenantSignatureSvg.isNotEmpty()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("contract_card_${contract.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Number & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = contract.contractNumber,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSigned) GreenSoft else AmberGoldLight
                ) {
                    Text(
                        text = if (isSigned) "VIGENTE" else "POR FIRMAR",
                        color = if (isSigned) GreenSuccess else AmberGoldDark,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Property Title & Sector
            Text(
                text = contract.propertyTitle,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (contract.sectorType == SectorType.URBANO) Icons.Default.LocationCity else Icons.Default.Landscape,
                    contentDescription = null,
                    tint = Slate500,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${contract.sectorType.displayName} • ${contract.propertyAddress}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Parties: Landlord & Tenant
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate100, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Arrendador:", style = MaterialTheme.typography.labelSmall, color = Slate500, fontSize = 10.sp)
                    Text(contract.landlordName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Slate800, maxLines = 1)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Arrendatario:", style = MaterialTheme.typography.labelSmall, color = Slate500, fontSize = 10.sp)
                    Text(contract.tenantName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Slate800, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canon & Guarantee
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$${contract.monthlyRent.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Text(
                        text = " USD/mes",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500
                    )
                }

                Text(
                    text = "Garantía: $${contract.guaranteeAmount.toInt()} USD",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AmberGoldDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar for contract duration
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (remainingDays < 30) AmberGold else TealPrimary,
                trackColor = Slate200
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Vigencia: $remainingDays días restantes",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate700,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Vence: ${dateFormat.format(Date(contract.endDate))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500
                )
            }
        }
    }
}
