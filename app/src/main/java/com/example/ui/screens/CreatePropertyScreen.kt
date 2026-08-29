package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.data.model.PropertyType
import com.example.data.model.SectorType
import com.example.ui.MainViewModel
import com.example.ui.theme.AmberGold
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePropertyScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onCreated: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val bankAccounts by viewModel.bankAccounts.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPropertyType by remember { mutableStateOf(PropertyType.DEPARTAMENTO) }
    var selectedSectorType by remember { mutableStateOf(SectorType.URBANO) }
    var city by remember { mutableStateOf("Quito") }
    var neighborhood by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var monthlyRentStr by remember { mutableStateOf("450") }
    var guaranteeAmountStr by remember { mutableStateOf("900") }
    var bedroomsStr by remember { mutableStateOf("2") }
    var bathroomsStr by remember { mutableStateOf("1") }
    var areaSqMetersStr by remember { mutableStateOf("75") }
    var amenities by remember { mutableStateOf("Agua caliente, Parqueadero, Internet fibra óptica, Mascotas permitidas") }
    var selectedImagePreset by remember { mutableStateOf("img_dept_modern") }
    var selectedBankAccountId by remember { mutableStateOf(bankAccounts.firstOrNull()?.id) }

    var landlordName by remember { mutableStateOf("Ing. Propietario Registrado") }
    var landlordPhone by remember { mutableStateOf("+593 99 876 5432") }
    var landlordEmail by remember { mutableStateOf("propietario@arriendos.ec") }
    var landlordIdCard by remember { mutableStateOf("1719823451") }

    var isBankDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ofertar Nuevo Inmueble",
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
                            val rent = monthlyRentStr.toDoubleOrNull() ?: 350.0
                            val guarantee = guaranteeAmountStr.toDoubleOrNull() ?: (rent * 2)
                            val beds = bedroomsStr.toIntOrNull() ?: 1
                            val baths = bathroomsStr.toIntOrNull() ?: 1
                            val area = areaSqMetersStr.toDoubleOrNull() ?: 60.0

                            viewModel.createProperty(
                                title = if (title.isBlank()) "${selectedPropertyType.displayName} en ${if (neighborhood.isBlank()) city else neighborhood}" else title,
                                description = if (description.isBlank()) "Excelente ${selectedPropertyType.displayName.lowercase()} en sector ${selectedSectorType.displayName.lowercase()} con cómodos ambientes y servicios completos." else description,
                                propertyType = selectedPropertyType,
                                sectorType = selectedSectorType,
                                city = city,
                                neighborhood = if (neighborhood.isBlank()) "Sector Central" else neighborhood,
                                address = if (address.isBlank()) "Av. Principal y Secundaria" else address,
                                monthlyRent = rent,
                                guaranteeAmount = guarantee,
                                bedrooms = beds,
                                bathrooms = baths,
                                areaSqMeters = area,
                                amenities = amenities,
                                imageDrawableRes = selectedImagePreset,
                                bankAccountId = selectedBankAccountId,
                                landlordName = landlordName,
                                landlordPhone = landlordPhone,
                                landlordEmail = landlordEmail,
                                landlordIdCard = landlordIdCard,
                                onSuccess = onCreated
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("publish_property_button")
                    ) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Publicar Inmueble en Arriendo",
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
            // Header instruction
            Text(
                text = "Detalles de la Oferta de Arriendo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
            Text(
                text = "Especifica si es local, departamento o casa, sector urbano o rural, canon mensual y garantía.",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Property Type Selector
            Text(
                text = "Tipo de Inmueble *",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Slate700
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PropertyType.values().forEach { type ->
                    FilterChip(
                        selected = selectedPropertyType == type,
                        onClick = {
                            selectedPropertyType = type
                            selectedImagePreset = when (type) {
                                PropertyType.LOCAL -> "img_local_front"
                                PropertyType.DEPARTAMENTO -> "img_dept_modern"
                                PropertyType.CASA -> if (selectedSectorType == SectorType.RURAL) "img_country_rural" else "img_house_urban"
                                PropertyType.SUITE -> "img_dept_modern"
                            }
                        },
                        label = { Text(type.displayName, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Sector Type Selector (Rural vs Urbano)
            Text(
                text = "Sector / Entorno *",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Slate700
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedSectorType = SectorType.URBANO
                            if (selectedPropertyType == PropertyType.CASA) selectedImagePreset = "img_house_urban"
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedSectorType == SectorType.URBANO) TealLight else Slate100
                    ),
                    border = if (selectedSectorType == SectorType.URBANO) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TealPrimary)) else null
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = null,
                            tint = if (selectedSectorType == SectorType.URBANO) TealDark else Slate500
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sector Urbano",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selectedSectorType == SectorType.URBANO) TealDark else Slate700
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedSectorType = SectorType.RURAL
                            if (selectedPropertyType == PropertyType.CASA) selectedImagePreset = "img_country_rural"
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedSectorType == SectorType.RURAL) Color(0xFFE8F5E9) else Slate100
                    ),
                    border = if (selectedSectorType == SectorType.RURAL) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2E7D32))) else null
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Landscape,
                            contentDescription = null,
                            tint = if (selectedSectorType == SectorType.RURAL) Color(0xFF2E7D32) else Slate500
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sector Rural",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selectedSectorType == SectorType.RURAL) Color(0xFF2E7D32) else Slate700
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Title & Description
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título de la Publicación") },
                placeholder = { Text("Ej. Hermoso Departamento Amoblado en La Carolina") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("property_title_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción Detallada") },
                placeholder = { Text("Describe iluminación, accesos, acabados, reglas de convivencia...") },
                minLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("property_description_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Ubicación
            Text(
                text = "Ubicación Geográfica",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Slate700
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Ciudad / Cantón") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = neighborhood,
                    onValueChange = { neighborhood = it },
                    label = { Text("Barrio / Parroquia") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Dirección Exacta / Calles / Referencia") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Canon de Arrendamiento y Garantía
            Text(
                text = "Valores Económicos (USD)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Slate700
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = monthlyRentStr,
                    onValueChange = {
                        monthlyRentStr = it
                        val rent = it.toDoubleOrNull() ?: 0.0
                        guaranteeAmountStr = (rent * 2).toInt().toString()
                    },
                    label = { Text("Canon Mensual ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("monthly_rent_input")
                )
                OutlinedTextField(
                    value = guaranteeAmountStr,
                    onValueChange = { guaranteeAmountStr = it },
                    label = { Text("Garantía ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("guarantee_input")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Características Físicas
            Text(
                text = "Dimensiones y Ambientes",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Slate700
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = bedroomsStr,
                    onValueChange = { bedroomsStr = it },
                    label = { Text("Dormitorios") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = bathroomsStr,
                    onValueChange = { bathroomsStr = it },
                    label = { Text("Baños") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = areaSqMetersStr,
                    onValueChange = { areaSqMetersStr = it },
                    label = { Text("Área (m²)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = amenities,
                onValueChange = { amenities = it },
                label = { Text("Servicios / Amenidades (separados por coma)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 7. Vinculación de Cuenta Bancaria / Coop. de Ahorro
            Text(
                text = "Cuenta Receptora de Canon de Arrendamiento *",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
            Text(
                text = "Selecciona la cuenta de Banco o Cooperativa donde se depositará el arriendo mensual:",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = isBankDropdownExpanded,
                onExpandedChange = { isBankDropdownExpanded = !isBankDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedAccount = bankAccounts.find { it.id == selectedBankAccountId }
                OutlinedTextField(
                    value = if (selectedAccount != null) "${selectedAccount.institutionName} - ${selectedAccount.accountType.displayName} #${selectedAccount.accountNumber}" else "Seleccionar Cuenta Bancaria / Coop.",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isBankDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isBankDropdownExpanded,
                    onDismissRequest = { isBankDropdownExpanded = false }
                ) {
                    bankAccounts.forEach { account ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = "${account.institutionName} (${account.institutionType.displayName})",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${account.accountType.displayName} #${account.accountNumber} - ${account.accountHolderName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate500
                                    )
                                }
                            },
                            onClick = {
                                selectedBankAccountId = account.id
                                isBankDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 8. Datos del Arrendador
            Text(
                text = "Datos de Contacto del Arrendador",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Slate700
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = landlordName,
                onValueChange = { landlordName = it },
                label = { Text("Nombre Completo del Propietario") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = landlordIdCard,
                    onValueChange = { landlordIdCard = it },
                    label = { Text("Cédula / RUC") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = landlordPhone,
                    onValueChange = { landlordPhone = it },
                    label = { Text("Celular / WhatsApp") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
