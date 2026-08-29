package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PropertyEntity
import com.example.data.model.PropertyType
import com.example.data.model.SectorType
import com.example.data.model.UserRole
import com.example.ui.MainViewModel
import com.example.ui.components.PropertyCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.GreenSoft
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
fun HomeScreen(
    viewModel: MainViewModel,
    onPropertyClick: (PropertyEntity) -> Unit,
    onCreatePropertyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val properties by viewModel.properties.collectAsStateWithLifecycle()
    val selectedPropertyType by viewModel.selectedPropertyTypeFilter.collectAsStateWithLifecycle()
    val selectedSectorType by viewModel.selectedSectorFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = TealPrimary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ArriendoDigital",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Locales, Departamentos y Casas",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFB0C4DE),
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    // Role Switch Button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0x33FFFFFF),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                val nextRole = if (currentRole == UserRole.ARRENDATARIO) UserRole.ARRENDADOR else UserRole.ARRENDATARIO
                                viewModel.switchRole(nextRole)
                            }
                            .testTag("role_switch_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (currentRole == UserRole.ARRENDATARIO) "Modo Inquilino" else "Modo Propietario",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePropertyClick,
                containerColor = TealPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("publish_property_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Ofertar Inmueble")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ofertar Inmueble", fontWeight = FontWeight.Bold)
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Buscar por ciudad, sector, barrio o tipo...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Slate500)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Borrar", tint = Slate500)
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate100,
                        unfocusedContainerColor = Slate100,
                        focusedBorderColor = TealPrimary,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("property_search_input")
                )
            }

            // Filter Bar 1: Sector Type (Rural vs Urbano)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sector:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate500
                )

                FilterChip(
                    selected = selectedSectorType == null,
                    onClick = { viewModel.setSectorFilter(null) },
                    label = { Text("Todos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NavyPrimary,
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedSectorType == SectorType.URBANO,
                    onClick = { viewModel.setSectorFilter(if (selectedSectorType == SectorType.URBANO) null else SectorType.URBANO) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = { Text("Sector Urbano") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0F2C59),
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedSectorType == SectorType.RURAL,
                    onClick = { viewModel.setSectorFilter(if (selectedSectorType == SectorType.RURAL) null else SectorType.RURAL) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Landscape,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = { Text("Sector Rural") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1E5128),
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    )
                )
            }

            // Filter Bar 2: Property Type (Locales, Deptos, Casas, Suites)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tipo:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate500
                )

                FilterChip(
                    selected = selectedPropertyType == null,
                    onClick = { viewModel.setPropertyTypeFilter(null) },
                    label = { Text("Cualquiera") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NavyPrimary,
                        selectedLabelColor = Color.White
                    )
                )

                PropertyType.values().forEach { type ->
                    FilterChip(
                        selected = selectedPropertyType == type,
                        onClick = { viewModel.setPropertyTypeFilter(if (selectedPropertyType == type) null else type) },
                        label = { Text(type.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Properties Results Count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${properties.size} Inmuebles Disponibles",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )

                if (selectedSectorType != null || selectedPropertyType != null || searchQuery.isNotEmpty()) {
                    Text(
                        text = "Limpiar Filtros",
                        style = MaterialTheme.typography.labelSmall,
                        color = TealDark,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            viewModel.setSectorFilter(null)
                            viewModel.setPropertyTypeFilter(null)
                            viewModel.setSearchQuery("")
                        }
                    )
                }
            }

            // Main Catalog List
            if (properties.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Apartment,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No se encontraron inmuebles",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                        Text(
                            text = "Intenta cambiando los filtros o publica un nuevo local, departamento o casa de arriendo.",
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(properties, key = { it.id }) { property ->
                        PropertyCard(
                            property = property,
                            onClick = {
                                viewModel.selectProperty(property)
                                onPropertyClick(property)
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}
