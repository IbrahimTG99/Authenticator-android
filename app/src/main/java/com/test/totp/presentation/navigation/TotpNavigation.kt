package com.test.totp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.test.totp.presentation.screen.AddAccountScreen
import com.test.totp.presentation.screen.MainScreen
import com.test.totp.presentation.screen.SettingsScreen

/**
 * Navigation routes for the TOTP app
 */
object TotpRoutes {
    const val MAIN = "main"
    const val ADD_ACCOUNT = "add_account"
    const val SETTINGS = "settings"
}

/**
 * Navigation composable for the TOTP app
 */
@Composable
fun TotpNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = TotpRoutes.MAIN
    ) {
        composable(TotpRoutes.MAIN) {
            MainScreen(
                onNavigateToAddAccount = {
                    navController.navigate(TotpRoutes.ADD_ACCOUNT)
                },
                onNavigateToSettings = {
                    navController.navigate(TotpRoutes.SETTINGS)
                }
            )
        }
        
        composable(TotpRoutes.ADD_ACCOUNT) {
            AddAccountScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(TotpRoutes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
