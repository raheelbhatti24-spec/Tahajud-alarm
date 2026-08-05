package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.TextSecondary

@Composable
fun TahajjudGuideScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("tahajjud_guide_screen")
    ) {
        // Banner Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Unspecified),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(GoldAccent.copy(alpha = 0.5f), EmeraldPrimary.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tahajjud Prayer Guide",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "The Virtues & Method of Night Prayer (Qiyam al-Layl)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(GoldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Nightlight, contentDescription = null, tint = GoldAccent)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quran Verse Card 1: Surah Al-Isra 17:79
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Book, contentDescription = null, tint = GoldAccent)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Holy Quran - Surah Al-Isra (17:79)",
                        style = MaterialTheme.typography.titleSmall,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "وَمِنَ ٱلَّيْلِ فَتَهَجَّدْ بِهِۦ نَافِلَةًۭ لَّكَ عَسَىٰ أَن يَبْعَثَكَ رَبُّكَ مَقَامًۭا مَّحْمُودًۭا",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "\"And during a part of the night, pray Tahajjud as an additional prayer for yourself; it may be that your Lord will raise you to a praised station.\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hadith Virtue Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "The Last Third of the Night (Hadith Qudsi)",
                        style = MaterialTheme.typography.titleSmall,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "The Messenger of Allah (ﷺ) said:\n\"Our Lord descends every night to the lowest heaven when one-third of the night remains, saying: 'Who is calling upon Me that I may answer him? Who is asking from Me that I may give him? Who is seeking My forgiveness that I may forgive him?'\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    lineHeight = 22.sp
                )
                Text(
                    text = "— Sahih al-Bukhari (1145) & Sahih Muslim (758)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Niyyah (Intention) & Method
        Text(
            text = "How to Offer Tahajjud Prayer",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "1. Sleep & Wake Up First",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tahajjud specifically means praying after waking up from sleep in the night. That is why Tahajjud Pulse helps you track when you fall asleep!",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "2. Make Intention (Niyyah)",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Perform Wudu (ablution) with care. Intend in your heart to perform 2 or more Rak'ahs of Nafl Tahajjud prayer for Allah Almighty.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "3. Pray in Pairs (2 Rak'ahs)",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Pray Tahajjud 2 Rak'ahs by 2 Rak'ahs (Minimum 2 Rak'ahs, standard is 8 Rak'ahs). Recite Surah Al-Fatihah followed by any Surahs you love.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "4. Conclude with Witr & Dua",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "If you have not offered Witr yet, pray Witr at the end. Make heartfelt Dua in Sujood and after prayer during this blessed hour.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Supplications (Duas) for Night Awakening
        Text(
            text = "Duas for Night Awakening",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Dua Upon Waking Up",
                    style = MaterialTheme.typography.titleSmall,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ٱلْحَمْدُ لِلَّهِ ٱلَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ ٱلنُّشُورُ",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "\"Alhamdu lillahil-ladhi ahyana ba'da ma amatana wa ilaihin-nushur.\"\n\n(Praise is to Allah Who gave us life after causing us to die, and unto Him is the resurrection.)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }
    }
}
