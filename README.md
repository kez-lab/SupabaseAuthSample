# Supabase Auth Sample

Android 애플리케이션에서 Supabase 인증 기능을 구현한 샘플 프로젝트입니다.

## 기능

- GitHub OAuth를 통한 소셜 로그인
- 사용자 인증 상태 관리
- Jetpack Compose를 사용한 현대적인 UI

## 기술 스택

- **언어**: Kotlin
- **UI 프레임워크**: Jetpack Compose
- **인증 서비스**: Supabase Auth
- **아키텍처**: MVVM 패턴
- **최소 SDK**: 28 (Android 9.0 Pie)

## 시작하기

### 사전 요구사항

- Android Studio Iguana | 2023.2.1 이상
- JDK 11
- Supabase 계정 및 프로젝트

### 설정 방법

1. 이 저장소를 클론합니다:
```bash
git clone https://github.com/kez-lab/SupabaseAuthSample.git
```

2. `local.properties` 파일에 Supabase 프로젝트 정보를 추가합니다:
```properties
SUPABASE_URL="your-supabase-url"
SUPABASE_ANON_KEY="your-supabase-anon-key"
```

3. GitHub OAuth 설정:
   - Supabase 대시보드에서 GitHub 인증 공급자를 활성화합니다
   - 콜백 URL을 `io.github.kezlab.commitmate://auth-callback/` 으로 설정합니다

4. Android Studio에서 프로젝트를 열고 실행합니다.

## 구조

```
app/src/main/java/com/example/supabaseauthsample/
├── AuthManager.kt - Supabase 인증 관리 및 상태 처리
├── HomeScreen.kt - 로그인 후 메인 화면
├── LoginScreen.kt - 로그인 화면
├── MainActivity.kt - 앱 시작점 및 인증 상태에 따른 화면 전환
└── SupabaseConfig.kt - Supabase 구성 정보
```

## 인증 흐름

1. 사용자가 GitHub 로그인 버튼을 클릭합니다
2. Supabase Auth가 GitHub OAuth 페이지로 리디렉션합니다
3. 사용자가 GitHub에서 인증을 완료하면 앱으로 리디렉션됩니다
4. AuthManager가 인증 토큰을 처리하고 세션을 설정합니다
5. UI가 로그인 상태에 따라 업데이트됩니다

## 주요 컴포넌트

### AuthManager

Supabase 클라이언트를 생성하고 인증 상태를 관리합니다. StateFlow를 통해 인증 상태를 노출하여 UI가 반응적으로 업데이트될 수 있도록 합니다.

### AuthState

인증 상태를 나타내는 sealed class:
- `Loading`: 초기화 중
- `SignedIn`: 로그인됨
- `SignedOut`: 로그아웃됨
- `Error`: 오류 발생

## 라이선스

MIT

## 기여 방법

1. 저장소를 포크합니다
2. 새 기능 브랜치를 생성합니다: `git checkout -b feature/amazing-feature`
3. 변경사항을 커밋합니다: `git commit -m 'Add amazing feature'`
4. 브랜치를 푸시합니다: `git push origin feature/amazing-feature`
5. Pull Request를 생성합니다 