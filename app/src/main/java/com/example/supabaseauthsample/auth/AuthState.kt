package com.example.supabaseauthsample.auth

/**
 * 인증 상태를 나타내는 sealed class
 * 앱 UI가 인증 상태에 따라 적절한 화면을 표시하는 데 사용됩니다.
 */
sealed class AuthState {
    /**
     * 인증 상태 초기화 중 또는 인증 프로세스 진행 중
     */
    object Loading : AuthState()
    
    /**
     * 인증 완료된 상태로, 사용자가 로그인됨
     */
    object SignedIn : AuthState()
    
    /**
     * 인증되지 않은 상태로, 사용자가 로그아웃됨
     */
    object SignedOut : AuthState()
    
    /**
     * 인증 과정에서 오류 발생
     * @param message 오류 메시지
     */
    data class Error(val message: String) : AuthState()
} 