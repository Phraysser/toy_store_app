package com.toystore.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.toystore.app.navigation.NavGraph
import com.toystore.app.ui.theme.ToyStoreTheme
import com.toystore.app.ui.viewmodel.AuthViewModel
import com.toystore.app.ui.viewmodel.CartViewModel
import com.toystore.app.ui.viewmodel.SettingsViewModel
import com.toystore.app.ui.viewmodel.ToyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val authViewModel: AuthViewModel = hiltViewModel()
            val toyViewModel: ToyViewModel = hiltViewModel()
            val cartViewModel: CartViewModel = hiltViewModel()


            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

            ToyStoreTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        settingsViewModel = settingsViewModel,
                        authViewModel = authViewModel,
                        toyViewModel = toyViewModel,
                        cartViewModel = cartViewModel
                    )
                }
            }
        }
    }
}