package com.test.totp.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.test.totp.presentation.components.ManualEntryForm
import com.test.totp.presentation.components.QrCodeScanner
import com.test.totp.presentation.viewmodel.AddAccountViewModel

/**
 * Screen for adding new TOTP accounts
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddAccountViewModel = koinViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()
    
    var showQrScanner by remember { mutableStateOf(false) }
    var showManualEntry by remember { mutableStateOf(false) }
    
    LaunchedEffect(success) {
        if (success) {
            onNavigateBack()
        }
    }
    
    LaunchedEffect(error) {
        error?.let {
            // Handle error (show snackbar, etc.)
        }
    }
    
    if (showQrScanner) {
        QrCodeScanner(
            onQrCodeScanned = { qrData ->
                viewModel.addAccountFromQr(qrData)
                showQrScanner = false
            },
            onCancel = { showQrScanner = false }
        )
    } else if (showManualEntry) {
        ManualEntryForm(
            onAccountAdded = { name, issuer, secret, algorithm, digits, period ->
                viewModel.addAccountManually(name, issuer, secret, algorithm, digits, period)
            },
            onCancel = { showManualEntry = false },
            onGenerateSecret = { viewModel.generateSecret() }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Add Account") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Add TOTP Account",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    
                    Text(
                        text = "Choose how you want to add your account",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // QR Code Scanner Button
                    Button(
                        onClick = { showQrScanner = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan QR Code")
                    }
                    
                    // Manual Entry Button
                    OutlinedButton(
                        onClick = { showManualEntry = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enter Manually")
                    }
                }
            }
        }
    }
}
