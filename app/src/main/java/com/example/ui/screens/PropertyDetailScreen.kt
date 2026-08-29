package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.PropertyEntity
import com.example.data.model.PropertyType
import com.example.data.model.SectorType
import com.example.ui.MainViewModel
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldDark
import com.example.ui.theme.AmberGoldLight
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PropertyDetailScreen(
    property: PropertyEntity,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onGenerateContract: () -> Unit,
    onDirectPayment: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bankAccounts by viewModel.bankAccounts.collectAsStateWithLifecycle()
    val linkedAccount = bankAccounts.find { it.id == property.bankAccountId }
        ?: bankAccounts.firstOrNull()

    val imageResId = when (property.imageDrawableRes) {
        "img_dept_modern" -> R.drawable.img_dept_modern
        "img_local_front" -> R.drawable.img_local_front
        "img_house_urban" -> R.drawable.img_house_urban
        "img_country_rural" -> R.drawable.img_country_rural
        else -> when (property.propertyType) {
            PropertyType.LOCAL -> R.drawable.img_local_front
            PropertyType.DEPARTAMENTO -> R.drawable.img_dept_modern
            PropertyType.CASA -> if (property.sectorType == SectorType.RURAL) R.drawable.img_country_rural else R.drawable.img_house_urban
            PropertyType.SUITE -> R.drawable.img_dept_modern
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = property.propertyType.displayName,
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyPrimary
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Canon Mensual",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate500
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$${property.monthlyRent.toInt()}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyDark
                            )
                            Text(
                                text = " /mes",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate500,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }

                    Button(
                        onClick = onGenerateContract,
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.8f)
                            .height(48.dp)
                            .testTag("generate_contract_cta_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Contrato Digital",
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
        ) {
            // Large Photo Hero Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xAA000000)),
                                startY = 120f
                            )
                        )
                )

                // Sector Badge overlay
                val (sectorText, sectorColor, sectorIcon) = if (property.sectorType == SectorType.URBANO) {
                    Triple("Sector Urbano", Color(0xFF1E3E62), Icons.Default.LocationCity)
                } else {
                    Triple("Sector Rural", Color(0xFF1E5128), Icons.Default.Landscape)
                }

                Surface(
                    shape = RoundedCornerShape(30.dp),
                    color = sectorColor,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = sectorIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = sectorText,
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Guarantee Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AmberGold,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Garantía Requerida: $${property.guaranteeAmount.toInt()} USD",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Main Info Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Title and Location
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ubicación",
                        tint = TealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${property.address}, ${property.neighborhood}, ${property.city}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate700
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price and Guarantee breakdown Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Canon Mensual", style = MaterialTheme.typography.labelSmall, color = Slate500)
                            Text(
                                text = "$${property.monthlyRent.toInt()} USD",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            Text("Vence c/mes", style = MaterialTheme.typography.labelSmall, color = TealDark)
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(Slate200)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Depósito en Garantía", style = MaterialTheme.typography.labelSmall, color = Slate500)
                            Text(
                                text = "$${property.guaranteeAmount.toInt()} USD",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AmberGoldDark
                            )
                            Text("Reembolsable", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Key Specs Matrix
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SpecItem(
                        icon = Icons.Default.SquareFoot,
                        label = "Área Total",
                        value = "${property.areaSqMeters.toInt()} m²",
                        modifier = Modifier.weight(1f)
                    )
                    if (property.bedrooms > 0) {
                        SpecItem(
                            icon = Icons.Default.Bed,
                            label = "Habitaciones",
                            value = "${property.bedrooms}",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    SpecItem(
                        icon = Icons.Default.Bathtub,
                        label = "Baños",
                        value = "${property.bathrooms}",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Sector description banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (property.sectorType == SectorType.URBANO) TealLight else Color(0xFFE8F5E9)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (property.sectorType == SectorType.URBANO) Icons.Default.LocationCity else Icons.Default.Landscape,
                            contentDescription = null,
                            tint = if (property.sectorType == SectorType.URBANO) TealDark else Color(0xFF2E7D32),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = property.sectorType.displayName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (property.sectorType == SectorType.URBANO) TealDark else Color(0xFF2E7D32)
                            )
                            Text(
                                text = property.sectorType.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate700
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Description
                Text(
                    text = "Descripción del Inmueble",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = property.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate700,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Amenities Tags
                Text(
                    text = "Servicios y Características",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    property.amenities.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { amenity ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Slate100
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = amenity,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate800,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bank & Savings Cooperative Account Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = "Banco",
                                tint = NavyPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cuenta Bancaria / Coop. Vinculada",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "El canon de arriendo mensual se acredita automáticamente a esta cuenta verificada:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (linkedAccount != null) {
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
                                    Text(
                                        text = linkedAccount.institutionName,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyDark,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Surface(
                                        color = TealLight,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = linkedAccount.accountType.displayName,
                                            color = TealDark,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "No. Cuenta: ${linkedAccount.accountNumber}",
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate800,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Titular: ${linkedAccount.accountHolderName} (CI/RUC: ${linkedAccount.accountHolderIdCard})",
                                    color = Slate500,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Landlord Contact Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(NavyPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = property.landlordName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark
                                )
                                Text(
                                    text = "Arrendador / Propietario Verificado",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TealDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = Slate500, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(property.landlordPhone, style = MaterialTheme.typography.bodySmall, color = Slate700)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Slate500, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(property.landlordEmail, style = MaterialTheme.typography.bodySmall, color = Slate700)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun SpecItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Slate100,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NavyPrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Slate500
            )
        }
    }
}
