package com.maya.ai.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var mayaActive by remember { mutableStateOf(false) }
    var wakeWordEnabled by remember { mutableStateOf(true) }
    var ttsEnabled by remember { mutableStateOf(true) }
    var autoReadSms by remember { mutableStateOf(false) }
    var floatingBubble by remember { mutableStateOf(false) }
    var autoStart by remember { mutableStateOf(false) }
    var rootAccessEnabled by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "General Settings",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            SettingCard {
                SettingSwitchRow(
                    title = "Maya Active",
                    subtitle = "Enable Maya AI assistant",
                    icon = Icons.Default.Power,
                    checked = mayaActive,
                    onCheckedChange = { mayaActive = it }
                )
            }
        }

        item {
            Text(
                text = "Voice Settings",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        item {
            SettingCard {
                Column {
                    SettingSwitchRow(
                        title = "Wake Word Detection",
                        subtitle = "Listen for \"Hey Maya\"",
                        icon = Icons.Default.RecordVoiceOver,
                        checked = wakeWordEnabled,
                        onCheckedChange = { wakeWordEnabled = it }
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    SettingSwitchRow(
                        title = "Text-to-Speech",
                        subtitle = "Enable voice responses",
                        icon = Icons.Default.VolumeUp,
                        checked = ttsEnabled,
                        onCheckedChange = { ttsEnabled = it }
                    )
                }
            }
        }

        item {
            Text(
                text = "AI Provider",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        item {
            SettingCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Current Provider",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "OpenAI GPT-4",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* Open provider selection */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Change Provider")
                    }
                }
            }
        }

        item {
            Text(
                text = "Automation",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        item {
            SettingCard {
                Column {
                    SettingSwitchRow(
                        title = "Auto-read SMS",
                        subtitle = "Read incoming SMS aloud",
                        icon = Icons.Default.Message,
                        checked = autoReadSms,
                        onCheckedChange = { autoReadSms = it }
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    SettingSwitchRow(
                        title = "Floating Bubble",
                        subtitle = "Show floating assistant bubble",
                        icon = Icons.Default.BubbleChart,
                        checked = floatingBubble,
                        onCheckedChange = { floatingBubble = it }
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    SettingSwitchRow(
                        title = "Auto-start on Boot",
                        subtitle = "Start Maya when device boots",
                        icon = Icons.Default.PowerSettingsNew,
                        checked = autoStart,
                        onCheckedChange = { autoStart = it }
                    )
                }
            }
        }

        item {
            Text(
                text = "Advanced",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        item {
            SettingCard {
                Column {
                    SettingSwitchRow(
                        title = "Root Access",
                        subtitle = "Enable root shell access",
                        icon = Icons.Default.Security,
                        checked = rootAccessEnabled,
                        onCheckedChange = { rootAccessEnabled = it }
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    SettingClickableRow(
                        title = "Accessibility Service",
                        subtitle = "Configure UI automation",
                        icon = Icons.Default.Accessibility,
                        onClick = { /* Open accessibility settings */ }
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    SettingClickableRow(
                        title = "Notification Access",
                        subtitle = "Configure notification listening",
                        icon = Icons.Default.Notifications,
                        onClick = { /* Open notification settings */ }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SettingCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        content()
    }
}

@Composable
fun SettingSwitchRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingClickableRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}
