package com.toystore.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var showAgreementDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Dark Theme Toggle
            SettingsItem(
                icon = Icons.Default.Palette,
                title = "Dark theme",
                onClick = { onThemeChange(!isDarkTheme) },
                trailing = {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onThemeChange
                    )
                }
            )

            HorizontalDivider()

            // Share App
            SettingsItem(
                icon = Icons.Default.Share,
                title = "Share app",
                onClick = { shareApp(context) }
            )

            HorizontalDivider()

            // Message to Support
            SettingsItem(
                icon = Icons.Default.Support,
                title = "Message to support",
                onClick = { contactSupport(context) }
            )

            HorizontalDivider()

            // User Agreement
            SettingsItem(
                icon = Icons.Default.Description,
                title = "User agreement",
                onClick = { showAgreementDialog = true }
            )
        }

        // Dialog User Agreement
        if (showAgreementDialog) {
            UserAgreementDialog(onDismiss = { showAgreementDialog = false })
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    trailing: @Composable (() -> Unit)? = null
) {
    ListItem(
        headlineContent = { Text(title) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = trailing ?: {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}

@Composable
fun UserAgreementDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "User Agreement",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "USER AGREEMENT\n\n",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "1. TERMS OF USE\n",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "By using Toy Store App, you agree to these terms.\n\n",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "2. PRIVACY POLICY\n",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "We collect and process your data according to our privacy policy.\n\n",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "3. PURCHASES\n",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "All purchases are subject to our return policy.\n\n",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "4. CONTACT\n",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "For questions, contact us at proncha200484@gmail.com\n\n",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "© 2026 Toy Store. All rights reserved.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    )
}



fun shareApp(context: Context) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Toy Store App")
        putExtra(
            Intent.EXTRA_TEXT,
            "Check out Toy Store App " +
                    "Share it if you liked it"
        )
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
}

fun contactSupport(context: Context) {
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:proncha200484@gmail.com")
        putExtra(Intent.EXTRA_SUBJECT, "Toy Store App - Support Request")
        putExtra(Intent.EXTRA_TEXT, "Hello Support Team,\n\n")
    }
    context.startActivity(Intent.createChooser(emailIntent, "Send email"))
}