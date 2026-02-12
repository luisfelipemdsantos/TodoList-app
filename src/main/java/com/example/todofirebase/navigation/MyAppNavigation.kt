package com.example.todofirebase.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todofirebase.ui.pages.HomePage
import com.example.todofirebase.ui.pages.LoginPage
import com.example.todofirebase.ui.pages.SignupPage
import com.example.todofirebase.viewmodel.AuthViewModel

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login") { LoginPage(modifier, navController, authViewModel) }
        composable("signup") { SignupPage(modifier, navController, authViewModel) }
        composable("home") { HomePage(modifier, navController, authViewModel) }
    })
}