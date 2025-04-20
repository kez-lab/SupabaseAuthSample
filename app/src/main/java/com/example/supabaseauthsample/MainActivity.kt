package com.example.supabaseauthsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.supabaseauthsample.ui.theme.SupabaseAuthSampleTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the auth manager
        authManager = AuthManager(this)

        enableEdgeToEdge()
        setContent {
            SupabaseAuthSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AuthScreen(
                        authManager = authManager,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        authManager.handleDeeplink(intent)
    }
}

@Composable
fun AuthScreen(
    authManager: AuthManager,
    modifier: Modifier = Modifier
) {
    val authState by authManager.authState.collectAsState()
    val scope = rememberCoroutineScope()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (authState) {
            is AuthState.Loading -> {
                Text(text = "Loading...")
            }

            is AuthState.SignedIn -> {
                HomeScreen(
                    onLogoutClick = {
                        scope.launch {
                            authManager.signOut()
                        }
                    }
                )
            }

            is AuthState.SignedOut -> {
                LoginScreen(
                    onGithubLoginClick = {
                        scope.launch {
                            authManager.signInWithGithub()
                        }
                    }
                )
            }

            is AuthState.Error -> {
                Text(text = "Error: ${(authState as AuthState.Error).message}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    SupabaseAuthSampleTheme {
        // Preview with mocked auth state
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LoginScreen(onGithubLoginClick = {})
        }
    }
}