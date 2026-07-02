package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.CompositionLocalProvider
import com.example.myapplication.data.LocalPreferencesManager
import com.example.myapplication.data.PreferencesManager
import com.example.myapplication.data.settingsDataStore
import com.example.myapplication.data.ThemeMode
import com.example.myapplication.navigation.AppNavHost
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.di.viewModelModule
import com.example.myapplication.di.localModule
import com.example.myapplication.di.authModule
import com.example.myapplication.di.preferencesModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        startKoin {
            androidContext(this@MainActivity)
            modules(listOf(viewModelModule, localModule, authModule, preferencesModule))
        }
        
        enableEdgeToEdge()
        setContent {
            val preferencesManager = remember {
                PreferencesManager(this@MainActivity.settingsDataStore)
            }
            val themeMode by preferencesManager.themeMode.collectAsState(initial = ThemeMode.SYSTEM)

            MyApplicationTheme(themeMode = themeMode) {
                CompositionLocalProvider(LocalPreferencesManager provides preferencesManager) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        AppNavHost()
                    }
                }
            }
        }
    }
}
