# 🌙 Tahajjud Pulse (Tahajjud Alarm & Sleep Tracking)

**Tahajjud Pulse** is a smart Android application designed to assist Muslims in waking up for **Tahajjud (Night Prayer)**. It monitors sleep readiness via periodic gentle audio/vibration check-ins, automatically detects when you fall asleep, and triggers a soulful Tahajjud alarm at your exact desired duration (e.g. 15 minutes, 30 minutes, 2 hours, etc.) after sleep onset.

---

## ✨ Features

- ⏱️ **Custom Tahajjud Alarm Duration**: Set any duration after falling asleep (e.g., 15 minutes, 30 minutes, 60 minutes) to trigger the alarm.
- 🎵 **Custom Audio Tone from Storage**: Choose any custom audio file/nasheed/Adhan directly from your phone's storage or pick built-in spiritual tones.
- 💤 **Smart Sleep Detection**: Periodic gentle check-ins (soft chime / vibration). Hardware Volume key presses postpone the check-in when you're still awake; if no reaction occurs, sleep is confirmed.
- 🖤 **OLED Pitch-Black Screen**: Screen darkens during sleep tracking to save battery and reduce ambient light.
- 📖 **Tahajjud Guide & Duas**: Includes Quranic verses, Hadith Qudsi, step-by-step prayer guidance, and authentic night awakening supplications.
- 📜 **Session History & Logs**: Detailed logs of detected sleep times, alarm triggers, and check-in reactions.

---

## 📱 How to Build APK on GitHub (No PC or Laptop Needed!)

This repository is pre-configured with **GitHub Actions** to automatically compile the **APK** directly in the cloud. You can download and install it on your Android phone without needing a computer!

### Steps:

1. **Push / Export Code to GitHub**
   - Click the **Export / GitHub** button in AI Studio to sync all project files to your GitHub repository.

2. **Trigger Automatic Build**
   - Go to your repository page on GitHub (`https://github.com/your-username/Tahajud-alarm`).
   - Tap the **Actions** tab at the top.
   - You will see a workflow named **Build Android APK** running automatically.

3. **Download & Install APK on Your Phone**
   - Once the build completes (shows a green checkmark `✓`), tap on the workflow run.
   - Scroll down to the **Artifacts** section at the bottom.
   - Tap **TahajjudPulse-Debug-APK** to download the ZIP file containing your Android `.apk`.
   - Extract the ZIP on your phone and open `app-debug.apk` to install!

---

## 🛠️ Project Structure

- `app/src/main/java/com/example/`
  - `ui/screens/`: Jetpack Compose screens (Dashboard, Settings, Guide, History, Blackout).
  - `service/`: `SleepTrackingManager` for state handling and sleep detection logic.
  - `audio/`: `AudioSynthesizer` for custom ringtones and soft check-in tones.
  - `data/`: Room database entity, DAO, and SharedPreferences settings repository.
- `.github/workflows/android.yml`: GitHub Actions workflow for automatic cloud APK build.
