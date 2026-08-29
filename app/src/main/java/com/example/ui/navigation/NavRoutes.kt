package com.example.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object PropertyDetail : Screen("property_detail")
    data object CreateProperty : Screen("create_property")
    data object GenerateContract : Screen("generate_contract")
    data object Contracts : Screen("contracts")
    data object ContractDetail : Screen("contract_detail")
    data object Payments : Screen("payments")
    data object Notifications : Screen("notifications")
}
