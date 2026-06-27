package com.digitalbank.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.digitalbank.ui.accounts.AccountsScreen
import com.digitalbank.ui.accounts.AccountsViewModel
import com.digitalbank.ui.camera.CameraScreen
import com.digitalbank.ui.camera.CameraViewModel
import com.digitalbank.ui.detail.AccountDetailScreen
import com.digitalbank.ui.detail.AccountDetailViewModel
import com.digitalbank.ui.theme.DigitalBankTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DigitalBankTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "accounts") {
                    composable("accounts") {
                        val accountsViewModel: AccountsViewModel = viewModel(
                            factory = AccountsViewModel.factory(application)
                        )
                        AccountsScreen(
                            viewModel = accountsViewModel,
                            onNavigateToDetail = { customerId ->
                                navController.navigate("detail/$customerId")
                            },
                            onNavigateToCamera = { customerId ->
                                navController.navigate("camera/$customerId")
                            }
                        )
                    }

                    composable(
                        route = "detail/{customerId}",
                        arguments = listOf(navArgument("customerId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val customerId = backStackEntry.arguments?.getInt("customerId")
                            ?: throw IllegalArgumentException("customerId is required")

                        val detailViewModel: AccountDetailViewModel = viewModel(
                            factory = AccountDetailViewModel.factory(application)
                        )

                        // Listen for returned selfie paths from camera screen
                        val selfiePathState by backStackEntry.savedStateHandle
                            .getStateFlow<String?>("selfie_path", null)
                            .collectAsStateWithLifecycle()

                        LaunchedEffect(selfiePathState) {
                            selfiePathState?.let { path ->
                                detailViewModel.completeKyc(customerId, path)
                                backStackEntry.savedStateHandle["selfie_path"] = null
                            }
                        }

                        AccountDetailScreen(
                            customerId = customerId,
                            viewModel = detailViewModel,
                            onBack = { navController.popBackStack() },
                            onNavigateToCamera = { id ->
                                navController.navigate("camera/$id")
                            }
                        )
                    }

                    composable(
                        route = "camera/{customerId}",
                        arguments = listOf(navArgument("customerId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val customerId = backStackEntry.arguments?.getInt("customerId")
                            ?: throw IllegalArgumentException("customerId is required")

                        val cameraViewModel: CameraViewModel = viewModel(
                            factory = CameraViewModel.factory(application)
                        )

                        CameraScreen(
                            customerId = customerId,
                            viewModel = cameraViewModel,
                            onSelfieCaptured = { filePath ->
                                navController.previousBackStackEntry?.savedStateHandle?.set("selfie_path", filePath)
                                navController.popBackStack()
                            },
                            onClose = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
