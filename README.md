# Supabase Auth Sample

Android 애플리케이션에서 Supabase 인증 기능을 구현한 샘플 프로젝트입니다. Jetpack Compose와 Supabase SDK를 활용하여 현대적인 Android 앱을 개발하는 방법을 보여줍니다.

![Supabase Auth](https://supabase.com/_next/image?url=%2Fimages%2Fproduct%2Fauth%2Fauth-providers.png&w=1920&q=75)

## 프로젝트 개요

이 프로젝트는 Supabase의 인증 서비스를 Android 앱에 통합하는 방법을 실제 예제로 보여주는 레퍼런스입니다. Compose UI를 사용한 현대적인
UI/UX와 함께 안전하고 사용하기 쉬운 인증 시스템을 구현합니다.

## 기능

- **GitHub OAuth 소셜 로그인**: 별도의 회원가입 없이 GitHub 계정으로 간편 로그인
- **보안 인증 플로우**: Supabase의 PKCE 플로우를 이용한 안전한 인증 처리
- **상태 관리**: Kotlin Flow를 활용한 반응형 인증 상태 관리
- **딥링크 처리**: OAuth 리다이렉트를 위한 딥링크 처리 구현
- **세션 관리**: 자동 세션 갱신 및 토큰 관리
- **사용자 상태 UI**: 로그인 여부에 따른 동적 UI 표시

## 기술 스택

- **언어**: Kotlin 1.9.x
- **UI 프레임워크**: Jetpack Compose
- **인증 서비스**: Supabase Auth
- **네트워킹**: Ktor Client
- **상태 관리**: Kotlin Flow & StateFlow
- **아키텍처**: MVVM 패턴
- **최소 SDK**: 28 (Android 9.0 Pie)
- **타겟 SDK**: 35 (Android 15)

## 시작하기

### 사전 요구사항

- Android Studio Iguana | 2023.2.1 이상
- JDK 11
- Supabase 계정 및 프로젝트
- GitHub 계정 (OAuth 테스트용)

### 설정 방법

#### 1. Supabase 프로젝트 설정

1. [Supabase](https://supabase.com)에서 새 프로젝트를 생성합니다.
2. Authentication > Providers로 이동하여 GitHub 공급자를 활성화합니다.
3. GitHub OAuth 애플리케이션 설정:
   - GitHub Developer Settings에서 새 OAuth 앱을 생성
   - Authorization callback URL에 `https://<YOUR_PROJECT_REF>.supabase.co/auth/v1/callback`를 입력
   - 클라이언트 ID와 비밀 키를 Supabase GitHub 공급자 설정에 입력
4. Supabase 프로젝트 URL과 anon key를 복사해둡니다.

#### 2. 프로젝트 설정

1. 이 저장소를 클론합니다:
```bash
git clone https://github.com/kez-lab/SupabaseAuthSample.git
cd SupabaseAuthSample
```

2. `local.properties` 파일에 Supabase 프로젝트 정보를 추가합니다:
```properties
SUPABASE_URL="https://your-project-ref.supabase.co"
SUPABASE_ANON_KEY="your-anon-key"
```

3. Android 프로젝트의 딥링크 설정 확인 (`AndroidManifest.xml`):
```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data
        android:scheme="io.github.kezlab.commitmate"
        android:host="auth-callback" />
</intent-filter>
```

4. Android Studio에서 프로젝트를 열고 실행합니다.

## 디렉토리 구조

```
app/src/main/java/com/example/supabaseauthsample/
├── auth/
│   ├── AuthManager.kt     - Supabase 인증 관리 및 상태 처리
│   └── AuthState.kt       - 인증 상태 정의 클래스
├── ui/
│   ├── HomeScreen.kt      - 로그인 후 메인 화면
│   ├── LoginScreen.kt     - 로그인 화면
│   └── theme/             - Material 테마 및 스타일 정의
├── MainActivity.kt        - 앱 시작점 및 인증 상태에 따른 화면 전환
└── SupabaseConfig.kt      - Supabase 구성 정보
```

## 인증 흐름 상세

1. **초기화**: 앱 시작 시 `AuthManager` 인스턴스 생성 및 `SessionStatus` 모니터링 시작
   ```kotlin
   init {
       CoroutineScope(Dispatchers.IO).launch {
           supabaseClient.auth.sessionStatus.collect { status ->
               // 상태에 따른 처리
           }
       }
   }
   ```

2. **로그인 시작**: 사용자가 GitHub 로그인 버튼 클릭 시 `signInWithGithub()` 호출
   ```kotlin
   suspend fun signInWithGithub() {
       supabaseClient.auth.signInWith(Github)
   }
   ```

3. **OAuth 리디렉션**: Supabase SDK가 브라우저/CustomTabs를 통해 GitHub 인증 페이지로 이동
   ```kotlin
   install(Auth) {
       scheme = "io.github.kezlab.commitmate"
       host = "auth-callback"
       defaultExternalAuthAction = ExternalAuthAction.CustomTabs()
       flowType = FlowType.PKCE
   }
   ```

4. **인증 완료 및 리디렉션**: 사용자 인증 후 앱으로 리디렉션되며 `handleDeeplink()` 메소드로 처리
   ```kotlin
   fun handleDeeplink(intent: Intent) {
       supabaseClient.handleDeeplinks(intent)
   }
   ```

5. **상태 갱신**: 인증 결과에 따라 `AuthState` 업데이트 및 UI 반영
   ```kotlin
   when (status) {
       is SessionStatus.Authenticated -> {
           _authState.value = AuthState.SignedIn
       }
       // 기타 상태 처리
   }
   ```

## 주요 컴포넌트 상세

### AuthManager

Supabase 클라이언트를 생성하고 인증 상태를 관리합니다. StateFlow를 통해 인증 상태를 노출하여 UI가 반응적으로 업데이트될 수 있도록 합니다.

```kotlin
class AuthManager(context: Context) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val supabaseClient = createSupabaseClient(
        supabaseUrl = SupabaseConfig.SUPABASE_URL,
        supabaseKey = SupabaseConfig.SUPABASE_ANON_KEY
    ) {
        // 클라이언트 설정
    }
    
    // 인증 메소드들
}
```

### AuthState

인증 상태를 나타내는 sealed class로 UI 표시를 결정합니다:
- `Loading`: 초기화 중이거나 인증 진행 중
- `SignedIn`: 인증 완료 상태
- `SignedOut`: 로그아웃 상태
- `Error`: 인증 오류 발생

### Compose UI 컴포넌트

#### LoginScreen
GitHub 로그인 버튼을 표시하고 인증 프로세스를 시작하는 화면입니다.

#### HomeScreen
인증된 사용자에게 표시되는 화면으로 사용자 정보 표시 및 로그아웃 기능을 제공합니다.

## 보안 고려사항

- PKCE (Proof Key for Code Exchange) 플로우를 사용하여 인증 코드 인터셉트 공격 방지
- 클라이언트에 민감한 Supabase 비밀 키를 저장하지 않음
- 인증 상태 전환 시 적절한 오류 처리

## 테스트

애플리케이션 테스트를 위한 간단한 절차:

1. 앱 실행 및 GitHub 로그인 버튼 클릭
2. GitHub 계정으로 인증
3. 앱으로 돌아와 인증 상태 확인
4. 로그아웃 기능 테스트

## 문제 해결

일반적인 문제와 해결 방법:

- **인증 리디렉션 실패**: 매니페스트의 딥링크 설정 확인
- **GitHub 로그인 오류**: Supabase 대시보드에서 GitHub 공급자 설정 검증
- **빌드 오류**: local.properties 파일에 Supabase 정보가 올바르게 설정되었는지 확인

## 라이선스

MIT

## 기여 방법

1. 저장소를 포크합니다
2. 새 기능 브랜치를 생성합니다: `git checkout -b feature/amazing-feature`
3. 변경사항을 커밋합니다: `git commit -m 'Add amazing feature'`
4. 브랜치를 푸시합니다: `git push origin feature/amazing-feature`
5. Pull Request를 생성합니다

## 관련 자료

- [Supabase 공식 문서](https://supabase.com/docs)
- [Supabase Kotlin 클라이언트](https://github.com/supabase-community/supabase-kt)
- [Jetpack Compose 문서](https://developer.android.com/jetpack/compose) 