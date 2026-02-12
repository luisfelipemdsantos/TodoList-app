package com.example.todofirebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.todofirebase.navigation.MyAppNavigation
import com.example.todofirebase.ui.theme.TodoFirebaseTheme // Seu tema pode ter nome diferente, verifique
import com.example.todofirebase.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel: AuthViewModel by viewModels()
        setContent {
            // Se TodoFirebaseTheme der erro (vermelho), apague e deixe apenas Scaffold ou use o nome do seu tema atual
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                MyAppNavigation(modifier = Modifier.padding(innerPadding), authViewModel = authViewModel)
            }
        }
    }
}