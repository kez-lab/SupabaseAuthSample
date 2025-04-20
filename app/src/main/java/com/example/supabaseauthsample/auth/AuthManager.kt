package com.example.supabaseauthsample.auth

import android.content.Intent
import com.example.supabaseauthsample.SupabaseConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.ExternalAuthAction
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.providers.Github
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.realtime.Realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Supabase 인증 관리 클래스
 * 사용자 인증 상태를 관리하고 인증 관련 작업을 처리합니다.
 */
class AuthManager {

    // 인증 상태를 저장하고 노출하는 StateFlow
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // 사용자 세션 정보를 저장하고 노출하는 StateFlow
    private val _userSession = MutableStateFlow<UserSession?>(null)
    val userSession = _userSession.asStateFlow()

    // Supabase 클라이언트 인스턴스 생성
    private val supabaseClient = createSupabaseClient(
        supabaseUrl = SupabaseConfig.SUPABASE_URL,
        supabaseKey = SupabaseConfig.SUPABASE_ANON_KEY
    ) {
        install(Auth) {
            // 딥링크를 위한 설정
            scheme = "io.github.kezlab.commitmate"
            host = "auth-callback"
            // 외부 인증 처리 방식 설정
            defaultExternalAuthAction = ExternalAuthAction.CustomTabs()
            // PKCE 플로우 사용 (보안 강화)
            flowType = FlowType.PKCE
        }
        // 실시간 기능 설치 (필요 시 확장 가능)
        install(Realtime)
    }

    init {
        // 인증 상태 모니터링
        CoroutineScope(Dispatchers.IO).launch {
            supabaseClient.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        // 인증 성공 시 사용자 세션 업데이트
                        _authState.value = AuthState.SignedIn
                        _userSession.value = supabaseClient.auth.currentSessionOrNull()
                    }

                    SessionStatus.Initializing -> {
                        // 초기화 중 상태
                        _authState.value = AuthState.Loading
                    }

                    is SessionStatus.NotAuthenticated -> {
                        // 인증되지 않은 상태
                        _authState.value = AuthState.SignedOut
                        _userSession.value = null
                    }

                    is SessionStatus.RefreshFailure -> {
                        // 토큰 갱신 실패
                        _authState.value = AuthState.Error(status.cause.toString())
                        _userSession.value = null
                    }
                }
            }
        }
    }

    /**
     * GitHub을 통한 사용자 로그인
     */
    suspend fun signInWithGithub() {
        try {
            supabaseClient.auth.signInWith(Github)
        } catch (e: Exception) {
            _authState.value = AuthState.Error("GitHub 로그인 실패: ${e.message}")
        }
    }

    /**
     * 사용자 로그아웃
     */
    suspend fun signOut() {
        try {
            supabaseClient.auth.signOut()
        } catch (e: Exception) {
            _authState.value = AuthState.Error("로그아웃 실패: ${e.message}")
        }
    }

    /**
     * OAuth 리다이렉트 딥링크 처리
     * @param intent 딥링크 인텐트
     */
    fun handleDeeplink(intent: Intent) {
        supabaseClient.handleDeeplinks(intent)
    }
    
    /**
     * 현재 인증된 사용자의 ID 반환
     * @return 사용자 ID 또는 null(인증되지 않은 경우)
     */
    fun getCurrentUserId(): String? {
        return supabaseClient.auth.currentUserOrNull()?.id
    }
} 