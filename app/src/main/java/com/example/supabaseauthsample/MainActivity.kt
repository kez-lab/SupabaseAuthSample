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
import com.example.supabaseauthsample.auth.AuthManager
import com.example.supabaseauthsample.auth.AuthState
import com.example.supabaseauthsample.ui.HomeScreen
import com.example.supabaseauthsample.ui.LoginScreen
import com.example.supabaseauthsample.ui.theme.SupabaseAuthSampleTheme
import kotlinx.coroutines.launch

/**
 * 메인 액티비티
 * 앱의 진입점으로, 인증 상태에 따라 적절한 화면을 표시합니다.
 */
class MainActivity : ComponentActivity() {

    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 인증 관리자 초기화
        authManager = AuthManager()

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
        // OAuth 리다이렉트 딥링크 처리
        authManager.handleDeeplink(intent)
    }
}

/**
 * 인증 상태에 따라 적절한 화면을 표시하는 컴포저블
 * 
 * @param authManager 인증 관리자 인스턴스
 * @param modifier 수정자
 */
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
                Text(text = "로딩 중...")
            }

            is AuthState.SignedIn -> {
                HomeScreen(
                    authManager = authManager,
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
                Text(text = "오류: ${(authState as AuthState.Error).message}")
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