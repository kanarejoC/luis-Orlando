package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.MainViewModel
import com.example.ui.navigation.Screen
import com.example.ui.theme.AmberGold
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.Slate500
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary

data class BottomNavItem(
    val title: String,
    val route: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
)

@Composable
fun MainAppContainer(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val unreadCount by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()
    val selectedProperty by viewModel.selectedProperty.collectAsStateWithLifecycle()
    val selectedContract by viewModel.selectedContract.collectAsStateWithLifecycle()

    val bottomNavItems = listOf(
        BottomNavItem(
            title = "Inmuebles",
            route = Screen.Home.route,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            testTag = "nav_home_tab"
        ),
        BottomNavItem(
            title = "Contratos",
            route = Screen.Contracts.route,
            selectedIcon = Icons.Filled.Description,
            unselectedIcon = Icons.Outlined.Description,
            testTag = "nav_contracts_tab"
        ),
        BottomNavItem(
            title = "Pagos & Cta.",
            route = Screen.Payments.route,
            selectedIcon = Icons.Filled.Payment,
            unselectedIcon = Icons.Outlined.Payment,
            testTag = "nav_payments_tab"
        ),
        BottomNavItem(
            title = "Avisos",
            route = Screen.Notifications.route,
            selectedIcon = Icons.Filled.Notifications,
            unselectedIcon = Icons.Outlined.Notifications,
            testTag = "nav_notifications_tab"
        )
    )

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Contracts.route,
        Screen.Payments.route,
        Screen.Notifications.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("main_bottom_nav_bar")
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                if (item.route == Screen.Notifications.route && unreadCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = AmberGold,
                                                contentColor = Color.White
                                            ) {
                                                Text(text = unreadCount.toString())
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.title,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyDark,
                                selectedTextColor = NavyPrimary,
                                unselectedIconColor = Slate500,
                                unselectedTextColor = Slate500,
                                indicatorColor = TealLight
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Home / Catalog Screen
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onPropertyClick = { prop ->
                        viewModel.selectProperty(prop)
                        navController.navigate(Screen.PropertyDetail.route)
                    },
                    onCreatePropertyClick = {
                        navController.navigate(Screen.CreateProperty.route)
                    }
                )
            }

            // 2. Property Detail Screen
            composable(Screen.PropertyDetail.route) {
                if (selectedProperty != null) {
                    PropertyDetailScreen(
                        property = selectedProperty!!,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onGenerateContract = {
                            navController.navigate(Screen.GenerateContract.route)
                        },
                        onDirectPayment = {
                            navController.navigate(Screen.Payments.route)
                        }
                    )
                } else {
                    navController.popBackStack()
                }
            }

            // 3. Create Property Screen
            composable(Screen.CreateProperty.route) {
                CreatePropertyScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onCreated = { newPropId ->
                        navController.popBackStack()
                    }
                )
            }

            // 4. Generate Digital Contract Screen
            composable(Screen.GenerateContract.route) {
                if (selectedProperty != null) {
                    GenerateContractScreen(
                        property = selectedProperty!!,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onContractCreated = { contractId ->
                            navController.navigate(Screen.Contracts.route) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                } else {
                    navController.popBackStack()
                }
            }

            // 5. Contracts List Screen
            composable(Screen.Contracts.route) {
                ContractsScreen(
                    viewModel = viewModel,
                    onSelectContract = { contract ->
                        viewModel.selectContract(contract)
                        navController.navigate(Screen.ContractDetail.route)
                    }
                )
            }

            // 6. Contract Detail & Signing Screen
            composable(Screen.ContractDetail.route) {
                if (selectedContract != null) {
                    ContractDetailScreen(
                        contract = selectedContract!!,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    navController.popBackStack()
                }
            }

            // 7. Payments & Bank Accounts Screen
            composable(Screen.Payments.route) {
                PaymentsScreen(viewModel = viewModel)
            }

            // 8. Notifications Screen
            composable(Screen.Notifications.route) {
                NotificationsScreen(viewModel = viewModel)
            }
        }
    }
}
