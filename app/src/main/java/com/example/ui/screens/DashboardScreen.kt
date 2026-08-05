package com.example.ui.screens

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.TrackingState
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onEnterBlackoutRequested: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val trackingState by viewModel.trackingState.collectAsState()
    val timeToNextCheckInSecs by viewModel.timeToNextCheckInSecs.collectAsState()
    val reactionTimeRemainingSecs by viewModel.reactionTimeRemainingSecs.collectAsState()
    val timeToAlarmSecs by viewModel.timeToAlarmSecs.collectAsState()
    val reactionsCount by viewModel.reactionsCount.collectAsState()
    val detectedSleepTimeMs by viewModel.detectedSleepTimeMs.collectAsState()
    val alarmScheduledTimeMs by viewModel.alarmScheduledTimeMs.collectAsState()

    val isTracking = trackingState != TrackingState.IDLE && trackingState != TrackingState.COMPLETED

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("dashboard_screen")
    ) {
        // Hero Header Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Unspecified),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(EmeraldPrimary.copy(alpha = 0.5f), GoldAccent.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Tahajjud Pulse",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Smart Sleep Detection & Night Prayer",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(EmeraldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NightlightRound,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Active Tracking Banner / Start Action Button
                    if (isTracking) {
                        Surface(
                            color = EmeraldContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = when (trackingState) {
                                            TrackingState.CHECK_IN_COUNTDOWN -> "Sleep Check Active"
                                            TrackingState.AWAITING_REACTION -> "Check-in Sound Played!"
                                            TrackingState.SLEEP_DETECTED -> "Sleep Detected"
                                            TrackingState.ALARM_RINGING -> "Tahajjud Alarm Ringing!"
                                            else -> "Active Session"
                                        },
                                        style = MaterialTheme.typography.titleMedium,
                                        color = EmeraldPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Reactions logged: $reactionsCount",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                }

                                IconButton(
                                    onClick = { onEnterBlackoutRequested() },
                                    modifier = Modifier.testTag("open_blackout_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DarkMode,
                                        contentDescription = "Enter Pitch Black OLED Mode",
                                        tint = GoldAccent
                                    )
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = { viewModel.startSession(enterBlackout = settings.blackoutModeEnabled) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldPrimary,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("start_session_button")
                        ) {
                            Icon(Icons.Default.Bedtime, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Start Sleep Detection Session",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Live Countdown Status Card (If active session)
        AnimatedVisibility(visible = isTracking) {
            Column {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (trackingState) {
                            TrackingState.CHECK_IN_COUNTDOWN -> {
                                val mins = timeToNextCheckInSecs / 60
                                val secs = timeToNextCheckInSecs % 60

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = GoldAccent)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Next Check-in Ting Sound",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = String.format(Locale.getDefault(), "%02d:%02d", mins, secs),
                                    style = MaterialTheme.typography.displayMedium,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "When the sweet sound plays, press Volume button or tap screen to postpone.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }

                            TrackingState.AWAITING_REACTION -> {
                                Text(
                                    text = "DID YOU FALL ASLEEP?",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Response Window: ${reactionTimeRemainingSecs}s",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { viewModel.onUserReacted() },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                                    modifier = Modifier.testTag("dashboard_postpone_button")
                                ) {
                                    Text("I'm Still Awake (Postpone)", fontWeight = FontWeight.Bold)
                                }
                            }

                            TrackingState.SLEEP_DETECTED -> {
                                val formattedSleep = detectedSleepTimeMs?.let {
                                    DateFormat.format("hh:mm a", Date(it)).toString()
                                } ?: ""
                                val formattedAlarm = alarmScheduledTimeMs?.let {
                                    DateFormat.format("hh:mm a", Date(it)).toString()
                                } ?: ""

                                val alarmHrs = timeToAlarmSecs / 3600
                                val alarmMins = (timeToAlarmSecs % 3600) / 60
                                val alarmSecs = timeToAlarmSecs % 60

                                Text(
                                    text = "SLEEP DETECTED AT $formattedSleep",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Tahajjud Alarm scheduled for $formattedAlarm",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = String.format(Locale.getDefault(), "%02dh %02dm %02ds", alarmHrs, alarmMins, alarmSecs),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            TrackingState.ALARM_RINGING -> {
                                Text(
                                    text = "TAHAJJUD ALARM RINGING!",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { viewModel.dismissAlarm() },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("dashboard_dismiss_alarm_button")
                                ) {
                                    Text("Dismiss Alarm", fontWeight = FontWeight.Bold)
                                }
                            }

                            else -> {}
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OutlinedButton(
                                onClick = { onEnterBlackoutRequested() },
                                modifier = Modifier.testTag("blackout_mode_button")
                            ) {
                                Icon(Icons.Default.DarkMode, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Blackout Mode", color = Color.White)
                            }

                            Button(
                                onClick = { viewModel.stopSession() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                modifier = Modifier.testTag("stop_session_button")
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("End Session")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Configuration Section 1: Check-in Interval Selector
        Text(
            text = "Check-in Interval",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Gentle non-disturbing ting sound plays at this frequency to check if you're asleep.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        val intervalOptions = listOf(5, 10, 15, 20, 25, 30, 45)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(intervalOptions) { mins ->
                val isSelected = settings.checkInIntervalMins == mins
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) EmeraldPrimary else Color(0xFF1E293B),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            viewModel.updateSettings(settings.copy(checkInIntervalMins = mins))
                        }
                        .testTag("interval_chip_$mins")
                ) {
                    Text(
                        text = "$mins mins",
                        color = if (isSelected) Color.Black else Color.White,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Configuration Section 2: Tahajjud Alarm After Sleep Duration
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tahajjud Alarm Timer After Falling Asleep",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Once sleep is detected, alarm triggers after this duration.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = GoldAccent.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
            ) {
                Text(
                    text = "${settings.sleepAlarmMins} mins",
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val alarmOptions = listOf(
            15 to "15 mins",
            20 to "20 mins",
            30 to "30 mins",
            45 to "45 mins",
            60 to "1 hour",
            90 to "1.5 hours",
            120 to "2 hours",
            180 to "3 hours",
            240 to "4 hours"
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(alarmOptions) { (mins, label) ->
                val isSelected = settings.sleepAlarmMins == mins
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) GoldAccent else Color(0xFF1E293B),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            viewModel.updateSettings(settings.copy(sleepAlarmMins = mins))
                        }
                        .testTag("alarm_duration_chip_$mins")
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.Black else Color.White,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sound Test Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = GoldAccent)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Check-in Sound Tone",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = settings.checkInSoundStyle,
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldAccent
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.testCheckInSound() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("test_sound_button")
                    ) {
                        Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Play Ting", fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pitch Black Mode Toggle Switch
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.DarkMode, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Auto Pitch-Black OLED Mode",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Blank screen completely so light won't disturb sleep",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Switch(
                    checked = settings.blackoutModeEnabled,
                    onCheckedChange = { enabled ->
                        viewModel.updateSettings(settings.copy(blackoutModeEnabled = enabled))
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = EmeraldPrimary
                    ),
                    modifier = Modifier.testTag("blackout_switch")
                )
            }
        }
    }
}
