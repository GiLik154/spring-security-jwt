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



<details>
<summary>JwtFilter을 이용한 검증 및 파싱</summary>
  

## 0. 개요

이 부분이 제일 어렵고 오래걸렸고 핵심 로직이다.
사실 사용하지 않아도 Jwt를 사용할 수 는 있지만,
스프링 시큐리티의 필터체인에 의존하면 많은 것을 편리하게 사용할 수 있다.
물론 적용하고, 이해하는데 시간이 조금 오래 걸릴 수 는 있다.

스프링 시큐리티의 필터체인에 대해 궁금하면 앞서 설명한 사이트를 참고해주길 바란다.
[JWT 사용 전의 기본지식 및 설정](https://www.notion.so/JWT-e954d8c8986b49a89f9fac965befdf61?pvs=21) 

## 1. 필터의 전체적인 로직

![JwtFilter](https://github.com/GiLik154/spring-security-jwt/assets/118507239/960548f4-80fc-4e4c-9c97-5d209828390c)

나도 많이 해맸던 부분이라 주석을 모두 달아놨다.
이 필터는 `OncePerRequestFilter` 를 상속받는다.
`OncePerRequestFilter` 는 직접 찾아보면 이해가 쉽겠지만, 쉽게 설명하면

우리가 요청을 하면 서블릿을 생성해서 메모리에 저장해둔다.
이후 똑같은 요청을 보내면 저장해 두었던 서블릿을 꺼내서 요청을 처리한다.
이런 과정을 통해서 메모리를 절약하고, 속도를 높일 수 있다.

Filter의 경우에는 서블릿이 저장되어 있든, 저장되어 있지 않든 실행이 되고
`OncePerRequestFilter` 의 경우는 새로운 요청 ( 새로운 서블릿 ) 에만 필터를 적용한다.

즉, `OncePerRequestFilter` 를 사용해야 재요청이 되는 것을 막고, 리소스 낭비를 막을 수 있다.

## 1. 토큰의 파싱

![Untitled (25)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/97e7e9d0-d84b-4363-a6e7-db1cd47a4f6e)
  
![Untitled (26)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/f267b749-adfa-45f0-a888-b81a5ef2fbf1)

위의 로직에서 토큰을 가지고 온다.
회사마다 다를 수 있지만 나는 헤더를 통해서 토큰을 보내주고
그 토큰을 파싱해서 사용하는 것으로 했다.

`request.getHeader(*AUTHORIZATION*);` 을 통해서 토큰을 받아오고

![Untitled (27)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/227c2bec-f23c-4e7d-96d8-cc7690e0e8d1)

내가 보낸 양식이 맞는지 확인한다.
만약 이 양식이 지켜지지 않을 경우에는 토큰 파싱을 진행하지 않는다.

이후 `"Bearer "` 를 제거한 토큰을 반환해주는 것으로 토큰을 받아온다. 

## 2. 토큰의 처리

![Untitled (28)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/9ef6c7eb-fa96-44e7-a012-895ae8c6bda2)

`request.getRequestURI();` 을 통해서 현재 접속한 URI를 받아온다.
내가 원하는 URI에만 실행하기 위해서이다.
이후 IF문으로 토큰이 존재하는지 점검하고, 내가 원하는 사이트인지 점검한다.

이후 `Authentication` 의 객체에 토큰을 담아서 `jwtProvider.authenticate` 로 전달한다.
(프로바이더에 관련해서는 다음 장에서 설명하도록 하겠다.)

`jwtProvider.authenticate` 에서 토큰이 사용 가능한지 검증하고, 유저의 정보를 담아서 `Authentication` 로 반환해준다. 
이후 `SecurityContextHolder` 에 반환된 `Authentication` 를 담아준다.

![Untitled (29)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/b302dfad-9381-4470-a1c7-c7d85cf657c3)

만약 토큰이 만료되었을 경우 403 을 반환하고, 오류 내역을 출력한다.
403이 반환되면 리프래쉬 토큰을 사용하여 재발급 하는 API로 이동한다.

![Untitled (30)](https://github.com/GiLik154/spring-security-jwt/assets/118507239/7b7a8800-da54-47cf-8e7c-6ef0edf56af5)

이후 다음 체인으로 넘겨준 Jwt Filter의 역할이 끝난다.
나는 원하는 URI에만 접근을 허용했지만, 반대로 원하지 않는 URI를 설정하고, 필터를 건너뛰는 방식을 사용해도 무난할 것으로 보인다.
예를 들면 `"/reissue"` 처럼 재발급 하는 API 의 경우 리프래쉬 토큰이 들어오면 오류가 발생할 가능성이 있으니, 미리 방지하는 것이 좋아보인다.

## 3.  결론

필터의 내용이 복잡하고 어려울 수 있으나, 백엔드 개발자를 선택한 이상 이해를 하고 넘어가야 하는 부분이다.
이러한 부분에 있어서 공부를 할 수 있어서 좋았고
스프링 시큐리티의 체인 필터에 관해서 공부하고 이해할 수 있는 시간이어서 좋았다.
필터를 잘 사용하면 개발자가 할 일이 많이 줄어든다. 어렵다고 피할 수 있는 것은 아니기에 이 참에 공부하고 이해하면 나중에 많은 도움이 될 것 같다고 생각했다.
  
</details>


<details>
<summary>JwtProvider 구현</summary>
  
  ## 0. 개요

우선 `Provider` 가 무엇인지 알아야 한다.
스프링 시큐리티의 `AuthenticationProvider` 는 사용자의 인증을 수행하고, 인증된 사용자 객체를 생성하여 스프링 시큐리티에 전달하는 역할을 한다.
즉, `Provider` 를 어떻게 구현하냐에 따라서 인증하는 방식이 달리잔다고 볼 수 있다.
인증에 관련된 인터페이스이다보니, 가장 중요한 핵심 로직을 담고 있는 곳이라고 봐도 무방하다.

## 1. 전체적인 로직

```java
/**
 * AuthenticationProvider는 인증과 관련된 인터페이스이다.
 * AuthenticationProvider를 구현해서 사용하여야 스프링 시큐리티의 체인을 이용할 수 있다.
 */
@Component
@RequiredArgsConstructor
public class JwtProvider implements AuthenticationProvider {
    private final JwtUtil jwtUtil;

    /**
     * JwtFilter에서 authentication를 받아오는데, authentication에는 토큰이 저장되어 있다.
     * 토큰을 사용하여 유저의 아이디와 권한을 가지고 오고
     * JwtAuthenticationToken에 토큰, 유저, 권한을 담아 보내준다. (123은 다른 것도 담을 수 있기에 예시로 넣어놨다.)
     *
     * @param authentication the authentication request object.
     * @return JwtAuthenticationToken (유저의 정보를 담아서 보내준다.)
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = getToken(authentication);

        String username = jwtUtil.extractUsername(token);

        List<GrantedAuthority> authorities = extractUserGrade(token);

        return new JwtAuthenticationToken(token, username, "123", authorities);
    }

    /**
     * authentication 에서 토큰을 파싱해온다.
     *
     * @param authentication JwtFilter에서 전달받은 인증 객체
     * @return 파싱된 토큰
     */
    private String getToken(Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;

        return jwtAuthenticationToken.getToken();
    }

    /**
     * 유저의 권한을 파싱해서 반환한다.
     *
     * @param token 파싱된 토큰
     * @return 유저의 권한
     */
    private List<GrantedAuthority> extractUserGrade(String token) {
        String userGrade = jwtUtil.extractUserGrade(token);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userGrade));

        return authorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
```

딱 보면 알겠지만 엄청 어려운 로직은 없다.
하나씩 살펴보도록 하겠다.

## 2. JwtAuthenticationToken

```java
/**
 * 인증을 위한 Authentication 클래스.
 */
@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    /** 저장되는 토큰 */
    private final String token;

    /** 판별이 가능한 정보 (여기서는 username을 사용) */
    private String principal;

    /** 다른 정보도 담을 수 있다는 것을 보여주기 위한 변수 */
    private String password;

    public JwtAuthenticationToken(String token) {
        super(null);
        this.token = token;
    }

    /** authorities는 유저의 권한을 담아서 보낼 수 있음. */
    public JwtAuthenticationToken(String token, String principal, String password,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.principal = principal;
        this.password = password;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JwtAuthenticationToken that = (JwtAuthenticationToken) o;
        return Objects.equals(token, that.token)
                && Objects.equals(principal, that.principal)
                && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token, principal, password);
    }
}
```

위는 인증 객체이다.
스프링 시큐리티를 사용하려면 인증 객체를 이용하여 처리해야한다.
`JwtAuthenticationToken` 는 `AbstractAuthenticationToken` 를 상속받아서 만들어야 한다.

`private String principal;` 를 구현하여서 구별 가능한 필드를 만들어줘야 한다.
안그러면 NPE 가 발생한다.

```java
/** authorities는 유저의 권한을 담아서 보낼 수 있음. */
    public JwtAuthenticationToken(String token, String principal, String password,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.principal = principal;
        this.password = password;
    }
```

위의 `authorities` 를 통해 유저의 인가 설정을 한다.

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/9701eb51-c345-464f-8408-be56166c6768/Untitled.png)

`AbstractAuthenticationToken` 를 상속받으면 위 처럼 인가를 설정할 수 있도록 되어 있다. 

```java
@ResponseBody
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserFromToken() {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", jwtAuthenticationToken.getPrincipal());
        jsonMap.put("password", jwtAuthenticationToken.getPassword());

        return ResponseEntity.ok().body(jsonMap);
    }
```

컨트롤단에는 위 처럼 SecurityContextHolder에서 꺼내와서 사용할 수 있다. 

## 3. JwtProvider 의 토큰 파싱

```java
/**
     * JwtFilter에서 authentication를 받아오는데, authentication에는 토큰이 저장되어 있다.
     * 토큰을 사용하여 유저의 아이디와 권한을 가지고 오고
     * JwtAuthenticationToken에 토큰, 유저, 권한을 담아 보내준다.
     * (123은 다른 것도 담을 수 있기에 예시로 넣어놨다.)
     *
     * @param authentication the authentication request object.
     * @return JwtAuthenticationToken (유저의 정보를 담아서 보내준다.)
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = getToken(authentication);

        String username = jwtUtil.extractUsername(token);

        List<GrantedAuthority> authorities = extractUserGrade(token);

        return new JwtAuthenticationToken(token, username, "123", authorities);
    }
```

주석으로 달아뒀다.
우선 *`JwtFilter`* 에서 담아서 넘겨 준 `Authentication` 에서 토큰을 파싱해야한다.

```java
/**
     * authentication 에서 토큰을 파싱해온다.
     *
     * @param authentication JwtFilter에서 전달받은 인증 객체
     * @return 파싱된 토큰
     */
    private String getToken(Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;

        return jwtAuthenticationToken.getToken();
    }
```

토큰 파싱은 위 처럼 하면 된다. 
이후 `JwtUtil` 을 통하여서 원하는 정보들을 추출하고

```java
/**
     * 유저의 권한을 파싱해서 반환한다.
     *
     * @param token 파싱된 토큰
     * @return 유저의 권한
     */
    private List<GrantedAuthority> extractUserGrade(String token) {
        String userGrade = jwtUtil.extractUserGrade(token);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userGrade));

        return authorities;
    }
```

유저의 등급도 이런식으로 추출해서 보내주며 된다. 여기서 `SimpleGrantedAuthority` 은 스프링의 권한 객채 중 하나이다. 위와 같이 담아주면 되며, 권한을 여러개 담아주는 것도 가능하다.

```java
return new JwtAuthenticationToken(token, username, "123", authorities);
```

마지막으로 `JwtAuthenticationToken` 를 반환해주면 끝난다. 여기서 만약 넘겨줘야 하는 정보가 많다면 DTO를 사용하여 넘겨주는 방식을 고려하는 것이 좋을 것 같다. 하지만 JWT의 특성상 민감한 정보들을 담으면 안된다.

## 4. 결론

Provider에 대해서 공부할 수 있는 시간이었다. 어떠한 역할을 하고, 어떻게 사용해야 되는지에 대해서 이해할 수 있었다. 그리고 얼마나 중요한 로직들을 담고 있는지도 이해할 수 있었다.
구현이 조금 어려울 수 있으나, 차근차근 해보니 생각보다 엄청 어렵지는 않은 느낌이었다. 
작성하면서 시큐리티에 대한 이해가 많이 늘었다고 생각한다.
코드를 작성하면서 아쉬웠던 점은 `JwtAuthenticationToken` 에 아예 DTO로 유저의 정보를 넘기는 방안을 생각해보는게 좋겠다는 생각을 했다.
물론 너무 많은 정보를 넘기다 실수로 민감한 정보가 포함되면 안되겠지만…..
  
  </details>


<details>
<summary>JwtService 및 실제 사용</summary>
## 0. 개요

이제는 이전에 구현한 코드들로 jwt를 실제로 사용하는 서비스와 컨트롤단을 알아보려고 한다.
하나씩 살펴보도록 하겠다.

## 1. Service

우선 전체적인 로직이다.

```java
@Service
@Transactional
@RequiredArgsConstructor
public class JwtService implements UserLogin, AccessTokenRefresher {
    private static final String BEARER = "Bearer ";

    private final UserRepository userRepository;
    private final RefreshTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 유저 로그인 시 처리되는 서비스.
     *
     * @param username 유저의 아이디
     * @param password 유저의 비밀번호
     * @return 토큰이 담긴 DTO
     */
    @Override
    public JwtTokenDto login(String username, String password) {
        User user = userRepository.findUserByUsername(username);

        validPassword(user, password);

        String accessToken = jwtUtil.generateAccessToken(username, user.getUserGrade().getAuthority());
        String refreshToken = generateRefreshToken(username);

        return new JwtTokenDto(accessToken, refreshToken);
    }

    /**
     * 유저의 비밀번호 비교
     *
     * @param user     비밀번호를 비교할 유저
     * @param password 유저의 입력 패스워드
     * @throws UsernameNotFoundException 유저의 비밀번호가 틀릴 시 전송
     */
    private void validPassword(User user, String password) {
        if (!user.matchPassword(passwordEncoder, password))
            throw new UsernameNotFoundException("Login Failed");
    }

    /**
     * 리프래쉬 토큰을 저장하는 메소드
     * 리프래쉬 토큰을 캐쉬로만 보내는 것 뿐 아니라 DB에도 전송해야 하기에
     * 메소드를 따로 빼 두었음.
     *
     * @param username 유저의 이름
     * @return 생성된 RefreshToken
     */
    private String generateRefreshToken(String username) {
        String token = jwtUtil.generateRefreshToken(username);

        RefreshToken refreshToken = tokenRepository.findByUsername(username)
                .orElseGet(() -> new RefreshToken(username));
        refreshToken.registerToken(token);

        return token;
    }

    /**
     * 토큰이 만료되었을 때 RefreshToken 을 이용해서 새로 발급 받는 메소드
     *
     * @param refreshToken 유저의 RefreshToken
     * @return 새로운 Access Token
     */
    @Override
    public String refresh(String refreshToken) {
        refreshToken = parsingToken(refreshToken);

        User user = validateRefreshToken(refreshToken);

        return jwtUtil.generateAccessToken(user.getName(), user.getUserGrade().getAuthority());
    }

    /**
     * 리프래쉬 토큰을 캐쉬에서 파싱하는 메소드
     *
     * @param refreshToken 캐쉬에서 얻어온 RefreshToken
     * @return 파싱된 RefreshToken
     * @throws JwtTokenValidationException 전송된 토큰의 양식이 일치하지 않을 시 발생
     */
    private String parsingToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken) && !refreshToken.startsWith(BEARER))
            throw new JwtTokenValidationException("The form of the requested token is invalid.");

        return refreshToken.substring(7);
    }

    /**
     * 리프래쉬 토큰을 갱신하는 메소드
     * DB에 저장된 토큰과 일치하는지 확인함.
     *
     * @param refreshToken 파싱된 RefreshToken
     * @return RefreshToken에 담긴 User의 정보
     * @throws JwtTokenValidationException 전송된 토큰이 검증되지 않으면 발생
     */
    private User validateRefreshToken(String refreshToken) {
        String refreshTokenUsername = jwtUtil.extractUsername(refreshToken);

        if (!tokenRepository.existsByUsernameAndToken(refreshTokenUsername, refreshToken))
            throw new JwtTokenValidationException("This is not a normal token.");

        return userRepository.findUserByUsername(refreshTokenUsername);
    }
}
```

어려운 로직은 없다.
다만 위 코드에서 수정해야 하는 부분은, login 부분은 따로 빼는 것이 맞지 않을까? 라는 생각을 한다.
UserServcie가 따로 있고, 이 곳에 Login이 구현되는 것이 더 객체지향적인 코드라고 생각하지만, 
지금 내 프로젝트에는 UserService가 따로 없기 때문에 우선 요기다가 구현을 하기로 했다.

## 2. Login

```java
/**
     * 유저 로그인 시 처리되는 서비스.
     *
     * @param username 유저의 아이디
     * @param password 유저의 비밀번호
     * @return 토큰이 담긴 DTO
     */
    @Override
    public JwtTokenDto login(String username, String password) {
        User user = userRepository.findUserByUsername(username);

        validPassword(user, password);

        String accessToken = jwtUtil.generateAccessToken(username, user.getUserGrade().getAuthority());
        String refreshToken = generateRefreshToken(username);

        return new JwtTokenDto(accessToken, refreshToken);
    }
```

비밀번호를 비교하고, 유저를 가지고 오는 곳은 생략하도록 하겠다.

```java
/**
     * 리프래쉬 토큰을 저장하는 메소드
     * 리프래쉬 토큰을 캐쉬로만 보내는 것 뿐 아니라 DB에도 전송해야 하기에
     * 메소드를 따로 빼 두었음.
     *
     * @param username 유저의 이름
     * @return 생성된 RefreshToken
     */
    private String generateRefreshToken(String username) {
        String token = jwtUtil.generateRefreshToken(username);

        RefreshToken refreshToken = tokenRepository.findByUsername(username)
                .orElseGet(() -> new RefreshToken(username));
        refreshToken.registerToken(token);

        return token;
    }
```

리프레쉬 토큰은 db에 저장되어 있는 값이 같은지 없으면 DB에 새로 넣고, 있다면 기존의 refreshToken를 불러와서 사용한다. db에 있는지 비교하는 이유는 억세스 토큰을 마음대로 발행하는 것을 막기 위해서이다.

```java

**// JwtUtil 클래스**

/**
     * Access Token을 생성하는 메소드
     * Claims을 통해서 토큰에 담길 정보들을 담는다.
     * 이 곳에서는 유저의 아이디와 유저의 권한을 담고 있다.
     *
     * @param username  유저의 아이디
     * @param userGrade 유저의 권한
     * @return 생성된 Access Token
     */
    public String generateAccessToken(String username, String userGrade) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("userGrade", userGrade);

        return createToken(claims, accessTokenExpiration);
    }
```

토큰의 생성은

JwtUtil 클래스의 generateAccessToken를 이용하여 생성한다.

`JwtTokenDto`는 별거 없다.

```java
@Getter
public class JwtTokenDto {
    private final String accessToken;
    private final String refreshToken;

    public JwtTokenDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
```

이거 전부이다.

컨트롤단은

```java
@GetMapping("/login")
    public String login() {
        return "thymeleaf/login";
    }

    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<JwtTokenDto> login(String username, String password) {
        JwtTokenDto dto = userLogin.login(username, password);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }
```

이렇게 사용하고 있다.

## 3. 재발급

```java
/**
     * 토큰이 만료되었을 때 RefreshToken 을 이용해서 새로 발급 받는 메소드
     *
     * @param refreshToken 유저의 RefreshToken
     * @return 새로운 Access Token
     */
    @Override
    public String refresh(String refreshToken) {
        refreshToken = parsingToken(refreshToken);

        User user = validateRefreshToken(refreshToken);

        return jwtUtil.generateAccessToken(user.getName(), user.getUserGrade().getAuthority());
    }
```

위와 같이 사용하고 있다.

```java
/**
     * 리프래쉬 토큰을 캐쉬에서 파싱하는 메소드
     *
     * @param refreshToken 캐쉬에서 얻어온 RefreshToken
     * @return 파싱된 RefreshToken
     * @throws JwtTokenValidationException 전송된 토큰의 양식이 일치하지 않을 시 발생
     */
    private String parsingToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken) && !refreshToken.startsWith(BEARER))
            throw new JwtTokenValidationException("The form of the requested token is invalid.");

        return refreshToken.substring(7);
    }
```

이 메소드를 통해서 토큰을 파싱하고

```java
/**
     * 리프래쉬 토큰을 갱신하는 메소드
     * DB에 저장된 토큰과 일치하는지 확인함.
     *
     * @param refreshToken 파싱된 RefreshToken
     * @return RefreshToken에 담긴 User의 정보
     * @throws JwtTokenValidationException 전송된 토큰이 검증되지 않으면 발생
     */
    private User validateRefreshToken(String refreshToken) {
        String refreshTokenUsername = jwtUtil.extractUsername(refreshToken);

        if (!tokenRepository.existsByUsernameAndToken(refreshTokenUsername, refreshToken))
            throw new JwtTokenValidationException("This is not a normal token.");

        return userRepository.findUserByUsername(refreshTokenUsername);
    }
```

이렇게 검증하고 있다.

db에 존재하는지 확인하고, 없으면 익셉션을 발생시킨다.
그리고 있다면 db에서 `User` 를 찾아서 억세스 토큰을 새로 만들어 반환한다.

컨트롤단은

```java
@ResponseBody
    @GetMapping("/reissue")
    public ResponseEntity<String> reissue(HttpServletRequest request) {
        String refreshToken = request.getHeader(AUTHORIZATION);

        String newAccessToken = accessTokenRefresher.refresh(refreshToken);

        return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);
    }
```

이렇게 사용하고 있다.

## 4. HTML

우선 html에 대해 설명하기 전에, 나는 프론트엔드를 잘 하지 못한다.
검색해서 꾸역꾸역 쑤셔놓은 코드들이라 내가 제대로 잘 짰는지도 확인을 못하고 있다.
그래도, 작동은 하니까 우선 어던식으로 작동하는지를 올려본다.

## 4-1 Login Html

```java
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
</head>
<body>
<h2>Login</h2>

<form id="loginForm" th:action="@{/login}" method="post">
    <div>
        <label for="username">Username:</label>
        <input type="text" id="username" name="username">
    </div>

    <div>
        <label for="password">Password:</label>
        <input type="password" id="password" name="password">
    </div>

    <button type="submit">Login</button>
</form>

<script>
    document.getElementById('loginForm').addEventListener('submit', function (event) {
        event.preventDefault(); // 기본 폼 제출 동작 막기

        // 폼 데이터 가져오기
        const formData = new FormData(this);

        // 서버로 데이터 전송
        fetch('/login', {
            method: 'POST',
            body: formData
        })
            .then(function (response) {
                if (response.ok) {
                    return response.text();
                }
                throw new Error('로그인에 실패했습니다.');
            })
            .then(function (tokenDto) {
                const dto = JSON.parse(tokenDto);
                const accessToken = dto.accessToken;
                const refreshToken = dto.refreshToken;

                // 쿠키에 액세스 토큰 저장
                document.cookie = "accessToken=" + accessToken + "; path=/";

                // 쿠키에 리프레시 토큰 저장
                document.cookie = "refreshToken=" + refreshToken + "; path=/";

                // '/user'로 이동
                window.location.href = '/user';
                });
            })
</script>
</body>
</html>
```

우선 로그인을 하면 /login 으로 Post 요청을 보낸다. 

```java
// 서버로 데이터 전송
        fetch('/login', {
            method: 'POST',
            body: formData
        })
            .then(function (response) {
                if (response.ok) {
                    return response.text();
                }
                throw new Error('로그인에 실패했습니다.');
            })
            .then(function (tokenDto) {
                const dto = JSON.parse(tokenDto);
                const accessToken = dto.accessToken;
                const refreshToken = dto.refreshToken;

                // 쿠키에 액세스 토큰 저장
                document.cookie = "accessToken=" + accessToken + "; path=/";

                // 쿠키에 리프레시 토큰 저장
                document.cookie = "refreshToken=" + refreshToken + "; path=/";

                // '/user'로 이동
                window.location.href = '/user';
                });
            })
```

이후 접속에 성공하면 억세스 토큰과 리프레시 토큰을 각 쿠키에 저장하고 /user로 이동한다.

## 4-2 User Html

```java
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Welcome</title>
</head>
<body>
<h1 id="welcome"></h1>
<a href="/admin">Admin 페이지로 이동</a>

<script>
    const accessTokenCookieName = 'accessToken';

    // JWT 토큰 가져오기
    const accessToken = getCookie(accessTokenCookieName);

    // Info 엔드포인트에 GET 요청 보내기
    fetch('/info', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + accessToken
        }
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else if (response.status === 403) {
                return reissueTokenAndFetchInfo();
            } else {
                throw new Error('Request failed.');
            }
        })
        .then(json => {
            // 이름 정보 표시
            const welcomeElement = document.getElementById('welcome');
            welcomeElement.textContent = json.name + '님 어서오세요. 기본사이트입니다.' + json.password;
        })
        .catch(error => {
            console.error('Error:', error);
        });

    // 새로운 액세스 토큰 발급 및 Info 엔드포인트 재요청 함수
    function reissueTokenAndFetchInfo() {
        const refreshToken = getCookie('refreshToken');

        return fetch('/reissue', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + refreshToken,
            }
        })
            .then(response => {
                if (response.ok) {
                    return response.text();
                } else {
                    throw new Error('Failed to reissue token.');
                }
            })
            .then(newToken => {
                const newAccessToken = newToken;

                // 액세스 토큰을 쿠키에 저장
                setCookie(accessTokenCookieName, newAccessToken);

                console.log(newToken);

                // Info 엔드포인트 재요청
                return fetch('/info', {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + newAccessToken
                    }
                });
            })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Failed to fetch info after reissuing token.');
                }
            });
    }

    // 쿠키 가져오기
    function getCookie(cookieName) {
        const cookieString = document.cookie;
        const cookies = cookieString.split(';').map(cookie => cookie.trim());

        const targetCookie = cookies.find(cookie => cookie.startsWith(cookieName + '='));
        if (targetCookie) {
            return targetCookie.split('=')[1];
        }
        return null;
    }

    // 쿠키 설정하기
    function setCookie(cookieName, cookieValue) {
        document.cookie = cookieName + '=' + cookieValue + '; path=/';
    }
</script>

</body>
</html>
```

여기는 조금 더 길다. 재발급을 받는 로직이 포함되어 있어서 그렇다.
만약 /info에서 403이 반환되면 /reissue 로 이동하여 재발급 받는다.

```java
        .then(response => {
            if (response.ok) {
                return response.json();
            } else if (response.status === 403) {
                return reissueTokenAndFetchInfo();
            } else {
                throw new Error('Request failed.');
            }
        })
```

```java
return fetch('/reissue', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + refreshToken,
            }
        })
```

이때 헤더에는 리프래쉬 토큰을 보내주게 된다.
이후 정상적으로 억세스 토큰을 받아오면 다시 쿠키에 저장하고 다시 info를 통해 정보를 얻어온다.

```java
.then(newToken => {
                const newAccessToken = newToken;

                // 액세스 토큰을 쿠키에 저장
                setCookie(accessTokenCookieName, newAccessToken);

                console.log(newToken);

                // Info 엔드포인트 재요청
                return fetch('/info', {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + newAccessToken
                    }
                });
            })
```

## 5. 결론

길고 길었던 Jwt 공부가 끝났다.
정보는 정말 많았지만, 원리등이 나오는 곳이 많지 않았다.
다들 이렇게만 사용하면 된다. 이런 느낌으로 작성되어있는곳이 대부분이었다.
특히, html에서 어떻게 정보가 처리되는지에 대해 적혀있는곳이 정말 너무 적었다.
하지만 완성! 했고, 이 방법이 맞는지는 의문이 많이 든다.
우선 실무에서 더 사용을 해보아야 할 것이라고 생각이 든다.

  </details>
