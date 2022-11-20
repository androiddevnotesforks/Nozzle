package com.kaiwolfram.nozzle

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.kaiwolfram.nozzle.ui.app.NozzleApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = AppContainer(applicationContext)
        setContent {
            NozzleApp(appContainer = appContainer)
        }
    }
}
