package com.example.supabaseauthsample.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.supabaseauthsample.auth.AuthManager
import com.example.supabaseauthsample.ui.theme.SupabaseAuthSampleTheme

/**
 * 홈 화면
 * 로그인 성공 후 표시되는 화면으로 사용자 정보와 로그아웃 버튼을 제공합니다.
 * 
 * @param authManager 인증 관리자 인스턴스
 * @param onLogoutClick 로그아웃 버튼 클릭 콜백
 */
@Composable
fun HomeScreen(
    authManager: AuthManager,
    onLogoutClick: () -> Unit
) {
    val userSession by authManager.userSession.collectAsState()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "환영합니다!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            userSession?.user?.email?.let { email ->
                Text(
                    text = "이메일: $email",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "GitHub 계정으로 성공적으로 로그인되었습니다",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onLogoutClick) {
                Text("로그아웃")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SupabaseAuthSampleTheme {
        // Preview with mocked data
        HomeScreen(
            authManager = AuthManager(),
            onLogoutClick = {}
        )
    }
} 