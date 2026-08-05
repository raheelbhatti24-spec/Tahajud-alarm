package com.example

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.MainViewModel
import com.example.ui.screens.MainContainer
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.TahajjudTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep screen on when tracking sleep so device doesn't destroy active timer
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            TahajjudTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MidnightBackground
                ) {
                    MainContainer(viewModel = viewModel)
                }
            }
        }
    }

    /**
     * Intercept hardware Volume Keys (Up / Down) to postpone check-in when user is still awake.
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_HEADSETHOOK,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                viewModel.onUserReacted()
                return true // Intercept key event
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopTestAudio()
    }
}
