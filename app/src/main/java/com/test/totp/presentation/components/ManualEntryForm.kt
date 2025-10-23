package com.test.totp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Manual entry form for adding TOTP accounts
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEntryForm(
    onAccountAdded: (String, String, String, String, Int, Int) -> Unit,
    onCancel: () -> Unit,
    onGenerateSecret: () -> String
) {
    var name by remember { mutableStateOf("") }
    var issuer by remember { mutableStateOf("") }
    var secret by remember { mutableStateOf("") }
    var algorithm by remember { mutableStateOf("SHA1") }
    var digits by remember { mutableIntStateOf(6) }
    var period by remember { mutableIntStateOf(30) }
    
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manual Entry") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Account Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Issuer
            OutlinedTextField(
                value = issuer,
                onValueChange = { issuer = it },
                label = { Text("Issuer (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Secret Key
            OutlinedTextField(
                value = secret,
                onValueChange = { secret = it },
                label = { Text("Secret Key *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = { secret = onGenerateSecret() }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Generate Secret")
                    }
                }
            )
            
            Text(
                text = "Enter the secret key from your authenticator app or generate a new one",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Algorithm
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = algorithm,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Algorithm") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("SHA1", "SHA256", "SHA512").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                algorithm = option
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            // Digits
            OutlinedTextField(
                value = digits.toString(),
                onValueChange = { 
                    it.toIntOrNull()?.let { value ->
                        if (value in 6..8) digits = value
                    }
                },
                label = { Text("Digits") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            
            // Period
            OutlinedTextField(
                value = period.toString(),
                onValueChange = { 
                    it.toIntOrNull()?.let { value ->
                        if (value > 0) period = value
                    }
                },
                label = { Text("Period (seconds)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Add Button
            Button(
                onClick = {
                    if (name.isNotBlank() && secret.isNotBlank()) {
                        onAccountAdded(name, issuer, secret, algorithm, digits, period)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && secret.isNotBlank()
            ) {
                Text("Add Account")
            }
            
            // Cancel Button
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    }
}
