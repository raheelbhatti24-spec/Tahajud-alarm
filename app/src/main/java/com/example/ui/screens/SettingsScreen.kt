package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppSettings
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsState()
    val isTestPlaying by viewModel.testPlaying.collectAsState()
    val context = LocalContext.current

    // Audio file picker launcher from phone storage
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}

            var fileName = "Custom Storage Audio"
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    fileName = cursor.getString(nameIndex)
                }
            }

            viewModel.updateSettings(
                settings.copy(
                    alarmRingtoneStyle = AppSettings.RINGTONE_CUSTOM,
                    customRingtoneUri = uri.toString(),
                    customRingtoneName = fileName
                )
            )
        }
    }

    // State for direct custom duration text field
    var customMinsText by remember(settings.sleepAlarmMins) {
        mutableStateOf(settings.sleepAlarmMins.toString())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("settings_screen")
    ) {
        Text(
            text = "Audio & Alarm Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Customize sweet check-in tones, Tahajjud ringtones, and sensitivity",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Custom Tahajjud Alarm Duration Section (Allows entering exact minutes, e.g., 15 mins)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Alarm, contentDescription = null, tint = GoldAccent)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Tahajjud Alarm Duration After Sleep",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Specify any duration (e.g. 15 minutes, 30 minutes, 120 minutes) for when the alarm sounds after sleep is detected.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Minute Entry Field & Steppers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            val current = settings.sleepAlarmMins
                            val updated = (current - 5).coerceAtLeast(1)
                            viewModel.updateSettings(settings.copy(sleepAlarmMins = updated))
                        },
                        modifier = Modifier
                            .background(Color(0xFF334155), shape = RoundedCornerShape(12.dp))
                            .testTag("decrease_mins_button")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease 5 mins", tint = Color.White)
                    }

                    OutlinedTextField(
                        value = customMinsText,
                        onValueChange = { input ->
                            customMinsText = input
                            val num = input.filter { it.isDigit() }.toIntOrNull()
                            if (num != null && num > 0) {
                                viewModel.updateSettings(settings.copy(sleepAlarmMins = num))
                            }
                        },
                        label = { Text("Alarm Minutes", color = TextSecondary) },
                        suffix = { Text("mins", color = GoldAccent, fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .width(160.dp)
                            .testTag("custom_alarm_mins_input")
                    )

                    IconButton(
                        onClick = {
                            val current = settings.sleepAlarmMins
                            val updated = current + 5
                            viewModel.updateSettings(settings.copy(sleepAlarmMins = updated))
                        },
                        modifier = Modifier
                            .background(Color(0xFF334155), shape = RoundedCornerShape(12.dp))
                            .testTag("increase_mins_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase 5 mins", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Quick Presets:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                val quickMins = listOf(15, 20, 30, 45, 60, 90, 120, 180, 240)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(quickMins) { mins ->
                        val isSelected = settings.sleepAlarmMins == mins
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) GoldAccent else Color(0xFF334155),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.updateSettings(settings.copy(sleepAlarmMins = mins))
                                }
                                .testTag("quick_mins_preset_$mins")
                        ) {
                            Text(
                                text = "$mins mins",
                                color = if (isSelected) Color.Black else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Check-in Sound Selector
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Check-in Sound Style",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { viewModel.testCheckInSound() },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("play_ting_test_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test Ting", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val soundStyles = listOf(
                    AppSettings.SOUND_CRYSTAL_TING to "Pure Crystal Ting (Clear & Gentle)",
                    AppSettings.SOUND_BRASS_BELL to "Brass Bell (Warm Harmonic Resonator)",
                    AppSettings.SOUND_RAIN_DROP to "Rain Drop (Soft Frequency Glide)",
                    AppSettings.SOUND_SOFT_HARP to "Soft Harp Arpeggio (4-Note Chord)"
                )

                soundStyles.forEach { (style, description) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.updateSettings(settings.copy(checkInSoundStyle = style))
                            }
                            .padding(vertical = 6.dp)
                    ) {
                        RadioButton(
                            selected = settings.checkInSoundStyle == style,
                            onClick = { viewModel.updateSettings(settings.copy(checkInSoundStyle = style)) },
                            colors = RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = description,
                            color = if (settings.checkInSoundStyle == style) Color.White else TextSecondary,
                            fontWeight = if (settings.checkInSoundStyle == style) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tahajjud Ringtone Selector (Includes Storage Audio File Picker!)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = GoldAccent)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Tahajjud Alarm Ringtone",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { viewModel.testAlarmRingtone() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTestPlaying) Color(0xFFEF4444) else GoldAccent,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("test_ringtone_button")
                    ) {
                        Icon(
                            if (isTestPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isTestPlaying) "Stop" else "Test Alarm", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Storage Audio File Choice Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (settings.alarmRingtoneStyle == AppSettings.RINGTONE_CUSTOM) EmeraldContainer.copy(alpha = 0.3f) else Color(0xFF0F172A),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (settings.alarmRingtoneStyle == AppSettings.RINGTONE_CUSTOM) GoldAccent else Color(0xFF334155),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            RadioButton(
                                selected = settings.alarmRingtoneStyle == AppSettings.RINGTONE_CUSTOM,
                                onClick = {
                                    if (settings.customRingtoneUri != null) {
                                        viewModel.updateSettings(settings.copy(alarmRingtoneStyle = AppSettings.RINGTONE_CUSTOM))
                                    } else {
                                        audioPickerLauncher.launch("audio/*")
                                    }
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = GoldAccent)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Custom Audio from Storage",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = settings.customRingtoneName ?: "No audio file selected yet",
                                    color = if (settings.customRingtoneName != null) GoldAccent else TextSecondary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Button(
                            onClick = { audioPickerLauncher.launch("audio/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("pick_custom_audio_button")
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (settings.customRingtoneUri == null) "Select File" else "Change", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Built-in Spiritual Tones:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                val ringtones = listOf(
                    AppSettings.RINGTONE_PEACEFUL_ADHAN to "Peaceful Adhan Tone (Spiritual Melody)",
                    AppSettings.RINGTONE_DAWN_CHIME to "Dawn Sunrise Chime (Ascending Harmonics)",
                    AppSettings.RINGTONE_SINGING_BOWL to "Deep Singing Bowl (432Hz Calm Resonance)",
                    AppSettings.RINGTONE_RISING_ZEN to "Rising Solfeggio (Transformation Tones)",
                    AppSettings.RINGTONE_CLASSIC to "Classic Gentle Bell (Rhythmic Chime)"
                )

                ringtones.forEach { (ringtone, description) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.updateSettings(settings.copy(alarmRingtoneStyle = ringtone))
                            }
                            .padding(vertical = 6.dp)
                    ) {
                        RadioButton(
                            selected = settings.alarmRingtoneStyle == ringtone,
                            onClick = { viewModel.updateSettings(settings.copy(alarmRingtoneStyle = ringtone)) },
                            colors = RadioButtonDefaults.colors(selectedColor = GoldAccent)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = description,
                            color = if (settings.alarmRingtoneStyle == ringtone) Color.White else TextSecondary,
                            fontWeight = if (settings.alarmRingtoneStyle == ringtone) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Reaction Response Window Selector
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Reaction Window Duration",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "How long the app waits after ting plays for you to press a button before concluding you've fallen asleep.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                val reactionWindows = listOf(15, 30, 45, 60)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    reactionWindows.forEach { secs ->
                        val isSelected = settings.reactionWindowSecs == secs
                        Button(
                            onClick = { viewModel.updateSettings(settings.copy(reactionWindowSecs = secs)) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) EmeraldPrimary else Color(0xFF334155),
                                contentColor = if (isSelected) Color.Black else Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("reaction_window_$secs")
                        ) {
                            Text("${secs}s", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Volume Level Slider & Vibration Toggle
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Sound Volume Level: ${(settings.volume * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = settings.volume,
                    onValueChange = { vol ->
                        viewModel.updateSettings(settings.copy(volume = vol))
                    },
                    valueRange = 0.1f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = EmeraldPrimary,
                        activeTrackColor = EmeraldPrimary,
                        inactiveTrackColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.testTag("volume_slider")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Vibration, contentDescription = null, tint = GoldAccent)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Vibrate on Check-in",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Gentle haptic pulse when ting sounds",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Switch(
                        checked = settings.vibrateOnCheckIn,
                        onCheckedChange = { vib ->
                            viewModel.updateSettings(settings.copy(vibrateOnCheckIn = vib))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = GoldAccent
                        ),
                        modifier = Modifier.testTag("vibration_switch")
                    )
                }
            }
        }
    }
}

