package com.example.ui.screens

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.TrackingState
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PitchBlack
import kotlinx.coroutines.delay
import java.util.Date
import java.util.Locale

@Composable
fun BlackoutScreen(
    viewModel: MainViewModel,
    onExitBlackout: () -> Unit
) {
    val context = LocalContext.current
    val trackingState by viewModel.trackingState.collectAsState()
    val timeToNextCheckInSecs by viewModel.timeToNextCheckInSecs.collectAsState()
    val reactionTimeRemainingSecs by viewModel.reactionTimeRemainingSecs.collectAsState()
    val timeToAlarmSecs by viewModel.timeToAlarmSecs.collectAsState()
    val reactionsCount by viewModel.reactionsCount.collectAsState()
    val detectedSleepTimeMs by viewModel.detectedSleepTimeMs.collectAsState()
    val alarmScheduledTimeMs by viewModel.alarmScheduledTimeMs.collectAsState()

    var showControlsHud by remember { mutableStateOf(false) }

    // Auto-hide controls overlay after 6 seconds
    LaunchedEffect(showControlsHud) {
        if (showControlsHud) {
            delay(6000)
            showControlsHud = false
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack)
            .testTag("blackout_screen")
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (trackingState == TrackingState.AWAITING_REACTION || trackingState == TrackingState.CHECK_IN_COUNTDOWN) {
                            // Reaction during check-in / waiting window
                            viewModel.onUserReacted()
                            showControlsHud = true
                        } else {
                            // Toggle controls HUD
                            showControlsHud = !showControlsHud
                        }
                    },
                    onDoubleTap = {
                        showControlsHud = true
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        when (trackingState) {
            TrackingState.AWAITING_REACTION -> {
                // Soft pulsing indicator so user knows ting just sounded
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(GoldAccent.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(GoldAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = "Tap to confirm awake",
                                tint = Color.Black,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Are you still awake?",
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tap screen or press Volume button to reset check-in (${reactionTimeRemainingSecs}s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.onUserReacted() },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                        modifier = Modifier.testTag("awake_reaction_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("I'm Still Awake", fontWeight = FontWeight.Bold)
                    }
                }
            }

            TrackingState.ALARM_RINGING -> {
                // High-visibility alarm trigger state
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0F172A))
                        .padding(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Tahajjud Alarm Ringing",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(64.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "TIME FOR TAHAJJUD",
                        style = MaterialTheme.typography.headlineMedium,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Blessed is the night prayer. Rise and communicate with your Creator.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = { viewModel.dismissAlarm() },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("dismiss_alarm_button"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Dismiss & Pray Tahajjud", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            else -> {
                // Completely black pitch screen while tracking sleep or waiting for check-in
                AnimatedVisibility(
                    visible = showControlsHud,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        color = Color(0xFF1E293B).copy(alpha = 0.92f),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .padding(24.dp)
                            .testTag("blackout_hud_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Bedtime,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Night Blackout Active",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close HUD",
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { showControlsHud = false }
                                        .padding(4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            when (trackingState) {
                                TrackingState.CHECK_IN_COUNTDOWN -> {
                                    val mins = timeToNextCheckInSecs / 60
                                    val secs = timeToNextCheckInSecs % 60
                                    Text(
                                        text = "Next Ting Sound in",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = String.format(Locale.getDefault(), "%02d:%02d", mins, secs),
                                        style = MaterialTheme.typography.displayMedium,
                                        color = GoldAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Press Volume key or tap screen when ting sounds to postpone.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                TrackingState.SLEEP_DETECTED -> {
                                    val formattedSleepTime = detectedSleepTimeMs?.let {
                                        DateFormat.format("hh:mm a", Date(it)).toString()
                                    } ?: "--:--"
                                    val formattedAlarmTime = alarmScheduledTimeMs?.let {
                                        DateFormat.format("hh:mm a", Date(it)).toString()
                                    } ?: "--:--"

                                    val alarmHrs = timeToAlarmSecs / 3600
                                    val alarmMins = (timeToAlarmSecs % 3600) / 60
                                    val alarmSecs = timeToAlarmSecs % 60

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Alarm,
                                            contentDescription = null,
                                            tint = EmeraldPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Sleep Detected at $formattedSleepTime",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = EmeraldPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Tahajjud Alarm set for $formattedAlarmTime",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = String.format(Locale.getDefault(), "%02dh %02dm %02ds remaining", alarmHrs, alarmMins, alarmSecs),
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = GoldAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                else -> {
                                    Text("Sleep Session Active", color = Color.White)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.onUserReacted()
                                        showControlsHud = false
                                    },
                                    modifier = Modifier.testTag("blackout_postpone_button")
                                ) {
                                    Text("Postpone Ting", color = Color.White)
                                }

                                Button(
                                    onClick = {
                                        onExitBlackout()
                                        viewModel.stopSession()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                    modifier = Modifier.testTag("blackout_exit_button")
                                ) {
                                    Text("End Session")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
