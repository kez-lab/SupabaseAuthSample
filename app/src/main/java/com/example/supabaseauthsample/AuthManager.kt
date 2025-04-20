package com.example.supabaseauthsample

import android.content.Context
import android.content.Intent
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.ExternalAuthAction
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.providers.Github
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.realtime.Realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthManager(context: Context) {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val supabaseClient = createSupabaseClient(
        supabaseUrl = SupabaseConfig.SUPABASE_URL,
        supabaseKey = SupabaseConfig.SUPABASE_ANON_KEY
    ) {
        install(Auth) {
            scheme = "io.github.kezlab.commitmate"
            host = "auth-callback"
            defaultExternalAuthAction = ExternalAuthAction.CustomTabs()
            flowType = FlowType.PKCE
        }
        install(Realtime)
    }

    val user = supabaseClient.auth.currentUserOrNull()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            supabaseClient.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        _authState.value = AuthState.SignedIn
                    }

                    SessionStatus.Initializing -> {
                        _authState.value = AuthState.Loading
                    }

                    is SessionStatus.NotAuthenticated -> {
                        _authState.value = AuthState.SignedOut
                    }

                    is SessionStatus.RefreshFailure -> {
                        _authState.value = AuthState.Error(status.cause.toString())
                    }
                }
            }
        }
    }

    suspend fun signInWithGithub() {
        supabaseClient.auth.signInWith(Github)
    }

    suspend fun signOut() {
        supabaseClient.auth.signOut()
    }

    fun handleDeeplink(intent: Intent) {
        supabaseClient.handleDeeplinks(intent)
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object SignedIn : AuthState()
    object SignedOut : AuthState()
    data class Error(val message: String) : AuthState()
}