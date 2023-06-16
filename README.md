# spring-security-jwt

<details>
<summary>JWT 사용 전의 기본지식 및 설정</summary>

## 0. 개요

공부하는데 진짜 오랜 시간이 걸렸다.
제대로 된 예시가 많지 않았던 탓도 있고, 꼼꼼하게 보고 싶다보니 오래걸린 것 같다.
우선 JWT에 들어가기 앞서서 스프링 시큐리티의 체인 필터에 대해서 알고 가야한다.

스프링 시큐리티는 필터 체인이라는 여러개의 필터를 물고 물고 물어져서 실행을 하고, 단계마다 로직을 실행햔다.

1. **ChannelProcessingFilter**: 요청 및 응답의 보안 채널 처리를 담당하는 필터입니다.
2. **SecurityContextPersistenceFilter**: 보안 컨텍스트를 요청 간에 유지하는 역할을 담당하는 필터입니다.
3. **ConcurrentSessionFilter**: 동시 세션 제어를 위한 필터입니다.
4. **LogoutFilter**: 로그아웃 처리를 담당하는 필터입니다.
5. **UsernamePasswordAuthenticationFilter**: 사용자 이름과 비밀번호에 기반한 인증을 처리하는 필터입니다.
6. **DefaultLoginPageGeneratingFilter**: 기본 로그인 페이지를 생성하는 필터입니다.
7. **DefaultLogoutPageGeneratingFilter**: 기본 로그아웃 페이지를 생성하는 필터입니다.
8. **BasicAuthenticationFilter**: HTTP Basic 인증을 처리하는 필터입니다.
9. **RequestCacheAwareFilter**: 요청 캐시 처리를 담당하는 필터입니다.
10. **SecurityContextHolderAwareRequestFilter**: 보안 컨텍스트를 요청에 적용하는 필터입니다.
11. **AnonymousAuthenticationFilter**: 익명 사용자 인증을 처리하는 필터입니다.
12. **SessionManagementFilter**: 세션 관리를 담당하는 필터입니다.
13. **ExceptionTranslationFilter**: 인증 및 권한 예외를 처리하는 필터입니다.
14. **FilterSecurityInterceptor**: 권한 기반의 보안 처리를 담당하는 필터입니다.

이 외에도 여러가지 필터가 있고, 사용자가 필터의 순서를 바꿀 수 도 있으며, 중간에 커스텀 필터를 끼워 넣을 수 도 있다.

이 순서에 대해서 이해하고 있어야, 스프링 시큐리티를 제대로 사용할 수 있다.

우리는 JWT의 새로운 필터를 만들고, 이 사이에 끼워넣을것이다.

## 1. `SecurityConfig` 설정

![Untitled (21)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/d786595f-e4f2-4ab3-9e7f-4f014ab907e9)

위와 같이 설정했다.
우선 스프링 시큐리티의 로그인을 사용하지 않으니, 로그인 관련을 모두 제거했다.
또한 JWT 토큰을 사용하므로 `csrf` 토큰에 의존할 필요가 없어지니, 비활성화 하였다.

여기서 우리가 봐야 할 것은 

![Untitled (22)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/3a508d69-1895-4c3c-9405-175aa9a2b782)

이 부분인데, **UsernamePasswordAuthenticationFilter** 이전에 내가 만든 JWT Filter을 끼워넣을것이다.

## 2. `properties` 설정

![Untitled (23)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/1bdbe25e-4422-46fc-bfd4-d1e1fac65eaf)

3가지를 설정했다.
1. 시크릿 키
2. 억세스 토큰의 만료 시간
3. 리프래쉬 토큰의 만료 시간

시크릿 키의 경우는 랜덤으로 아무거나 만들어서 사용했다.
억세스 토큰의 경우는 만료 시간을 짧게 두는 게 좋기에 30분으로 설정했고
리프래쉬 토큰의 경우는 7일을 사용했다.
</details>



<details>
<summary>JWT 생성 및 파싱 로직</summary>
  
## 0. 개요

![JWT_Util](https://github.com/GiLik154/spring-security-jwt/assets/118507239/6ea21b60-f194-4746-ba21-d8a8f3e385ec)

전체적인 로직은 위와 같다
위의 로직은 토큰을 생성하고, 추출하는 메소드 들이 들어있다.
처음 보는 개념들이 있어서 하나씩 살펴보도록 하겠다.

## 1. 생성

![Untitled (25)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/38b8e262-3ac4-4124-a53f-17e75a81c7be)

위의 로직들이 생성하는 로직이다.
주석을 통해서 설명을 하고 있으나, 다시 설명을 하도록 하겠다.

![Untitled (26)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/cfe5247c-2cbc-4f9a-8394-d7fffb4db7cf)

위의 로직은 억세스 토큰을 생성하는 로직이다.
억세스 토큰의 Subject에는 username과 유저의 등급을 넣었다.

`Claims` 의 put 메소드를 통해 정보들을 넣을 수 있다.

![Untitled (27)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/c8ec3dbd-725b-4dfc-b450-d05dbcdd607d)

`Claims` 의 내부를 보면 설명이 잘 되어 있다.
Map을 상속받아서 put 메소드를 사용할 수 있다.

리프래쉬 토큰의 로직도 다르지 않다.

![Untitled (28)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/421a0dd5-2e17-43f6-8600-2b775c31e93f)

차이는 리프래쉬 토큰에는 유저의 등급을 넣지 않았다.
재발급을 받는 토큰이다보니 많은 정보가 담길 필요가 없기 때문이다.
이후 중요한 로직은 다음이다.

![Untitled (29)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/b0b77c4a-b0eb-40f3-a846-80a9c0d7faf3)

토큰을 생성하는 로직이 담겨있다.
주석에 어떻게 사용되는지 적혀있지만, 다시 보도록하면

우선 subject를 `Claims` 을 통해 불러온다.
그리고 오늘의 날짜와, 만료 날짜를 설정해주고

`Jwts.*builder*()` 을 사용하여 토큰을 생성한다.

Jwts 내부를 보면 아주 복잡한 과정들을 거치는데,
한 번 살펴보면 도움이 많이 될 것 같다.

## 2. 추출

![Untitled (30)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/c5a53d05-91a5-40e3-bb61-6cf49ea97f02)

추출하는 메소드는 간단하다.
중요하고, 처음 접하는 개념들만 살펴보겠다.

![Untitled (31)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/8cb2c429-f7c6-4d43-ab21-4a9e7c88dc87)

우선 펑션 기능을 처음 접했다.
형변환을 도와주는 메소드였다. 

![Untitled (32)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/c26be2a8-0c57-43f6-9a68-d8b54ce9c8b8)

이런 식으로 매개 변수를 넣어주면 String으로 형변환해서 돌려주었다.
처음보는 개념이어서 공부하는데 도움이 많이 되었다.

![Untitled (33)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/8de4de4f-7e63-47e1-8c42-c3197dcabcb8)

토큰의 복호화는 Jwts의 메소드를 이용했다.
간단하게 복호화 할 수 있게 된다.

## 3. 결론

로직이 복잡해보일 수 있으나, 조금만 천천히 보다 보면 이해하는데 큰 어려움이 없었다.
하나씩 뜯어보면서 공부도 많이 할 수 있었다.
작성해야 하는 메소드가 많아서 조금 두려움이 있었지만
하나씩 하다보니 금방 따라가고 이해할 수 있었다.
나의 코드가 누군가에게 도움이 되기를 원한다.
</details>
