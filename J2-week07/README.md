# J2-week07

## Spring Boot를 이용한 REST API 회원가입 구현

이번 주차에서는 Spring Boot를 활용하여 회원가입 API를 구현하고, REST API의 기본 구조와 요청·응답 처리 방식을 학습했다.

특히 **HTTP 메서드, 입력값 검증(Validation), 예외 처리(ExceptionHandler), 응답 형식 통일(RsData)**을 중심으로 안정적이고 일관성 있는 API를 구현하는 방법을 이해했다.

---

## 1. REST API와 HTTP 메서드

### REST API란?

REST API는 HTTP를 이용하여 클라이언트와 서버가 자원을 주고받을 수 있도록 설계한 API이다.

Spring Boot는 사용자에게 HTML 화면을 제공할 수도 있고, 프론트엔드 애플리케이션이 사용할 JSON 데이터를 제공할 수도 있다.

```text
사용자
  ↓
React 등 프론트엔드
  ↓ HTTP 요청
Spring Boot REST API
  ↓
Service → Repository → Database
  ↓
JSON 응답
```

- **Spring Boot 중심의 웹 애플리케이션:** Thymeleaf 등을 사용하여 서버에서 HTML을 생성할 수 있다.
- **SPA + REST API:** React 등의 프론트엔드가 화면을 담당하고, Spring Boot는 데이터 처리와 API 제공을 담당한다.

### HTTP 메서드

HTTP 메서드는 요청이 어떤 작업을 수행하는지 나타낸다.

| 메서드 | 역할 |
|---|---|
| GET | 데이터 조회 |
| POST | 데이터 생성 |
| PUT | 데이터 전체 수정 |
| PATCH | 데이터 부분 수정 |
| DELETE | 데이터 삭제 |

REST API에서는 URL을 동사보다는 **자원을 나타내는 명사 형태**로 작성하는 것이 일반적이다.

예를 들어 게시물을 삭제할 때 `GET /article/delete/1`보다는 `DELETE /articles/1`을 사용하는 것이 적절하다.

---

## 2. 회원가입 API 구현과 Postman

회원가입은 사용자가 입력한 아이디, 비밀번호, 닉네임 등을 검증한 후 회원 정보를 데이터베이스에 저장하는 과정이다.

이번 실습에서는 `ApiV1MemberController`를 생성하여 회원가입 요청을 처리했다.

### 주요 어노테이션

- `@RestController`: API 컨트롤러를 정의하고 반환 객체를 JSON 등의 응답 본문으로 변환할 수 있도록 한다.
- `@PostMapping`: POST 방식으로 전달된 요청을 처리한다.
- `@RequestBody`: JSON 요청 본문을 Java 객체로 변환하여 전달받는다.

회원가입 요청 예시:

```http
POST /api/v1/members
Content-Type: application/json
```

```json
{
  "username": "user1",
  "password": "1234",
  "nickname": "홍길동"
}
```

API 경로에 `/api/v1`과 같이 버전을 포함하면 향후 API가 변경되었을 때 기존 클라이언트와의 호환성을 관리하기 편리하다.

### Postman

Postman은 프론트엔드 없이도 API 요청을 보내고 응답을 확인할 수 있는 테스트 도구이다.

브라우저 주소창과 달리 GET, POST, PUT, PATCH, DELETE 등 다양한 HTTP 메서드를 선택하고 JSON 요청 본문이나 헤더를 설정할 수 있다.

따라서 회원가입 API를 개발하면서 요청 데이터가 올바르게 처리되는지 확인하는 데 활용할 수 있다.

---

## 3. Validation을 이용한 입력값 검증

회원가입에서는 아이디, 비밀번호, 닉네임 등 필수 입력값이 누락되지 않도록 확인해야 한다.

처음에는 `if`문으로 각 항목을 직접 검증했지만, Validation을 사용하면 반복적인 검증 코드를 줄일 수 있다.

**build.gradle**

```gradle
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

### 주요 어노테이션

- `@NotBlank`: 문자열이 null이거나 비어 있거나 공백만으로 이루어진 경우 허용하지 않는다.
- `@NotNull`: null 값을 허용하지 않는다.
- `@Min`: 숫자가 지정된 최솟값 이상이어야 한다.
- `@Valid`: 객체에 선언된 검증 규칙을 실행한다.

예를 들어 회원가입 요청 객체에 다음과 같이 적용할 수 있다.

```java
public record MemberJoinReqBody(
    @NotBlank String username,
    @NotBlank String password,
    @NotBlank String nickname
) {}
```

요청을 처리하는 메서드에서 `@Valid @RequestBody`를 사용하면 입력값 검증을 자동으로 수행할 수 있다.

검증에 실패하면 `MethodArgumentNotValidException`이 발생하며, 이를 예외 핸들러에서 공통으로 처리할 수 있다.

---

## 4. 예외 처리와 ExceptionHandler

### return과 throw의 차이

회원가입 과정에서는 필수 입력값 누락, 중복 아이디 등 다양한 예외 상황이 발생할 수 있다.

- **return:** 현재 메서드의 실행을 종료하고 결과를 반환한다.
- **throw:** 예외를 발생시켜 호출자 또는 예외 처리기로 제어를 전달한다.

`return`만으로 오류를 표현하면 메서드의 반환 타입에 제약을 받을 수 있다.

반면 `throw`를 사용하면 정상적인 처리 흐름과 예외 처리 흐름을 분리할 수 있다.

### 전역 예외 처리

Spring Boot에서는 `@ControllerAdvice`와 `@ExceptionHandler`를 이용하여 여러 컨트롤러에서 발생하는 예외를 공통으로 처리할 수 있다.

- `@ControllerAdvice`: 여러 컨트롤러에 적용되는 공통 예외 처리 기능을 정의한다.
- `@ExceptionHandler`: 지정한 예외가 발생했을 때 실행할 처리 메서드를 정의한다.
- `ResponseEntity`: 응답 본문과 HTTP 응답 상태 등을 함께 설정할 수 있다.

이번 실습에서는 사용자 정의 예외인 `GlobalException`, Validation 과정에서 발생하는 `MethodArgumentNotValidException`, 그 밖의 예외를 처리하는 방법을 학습했다.

예외가 발생했을 때 공통 처리기를 사용하면 컨트롤러마다 중복된 오류 처리 코드를 작성하지 않아도 된다.

---

## 5. RsData를 이용한 응답 형식 통일

API에서는 성공과 실패의 응답 구조를 일관되게 유지하는 것이 중요하다.

이번 실습에서는 `RsData`를 활용하여 응답을 다음 네 가지 항목으로 구성했다.

| 필드 | 의미 |
|---|---|
| `resultCode` | 애플리케이션에서 정의한 세부 처리 결과 |
| `statusCode` | HTTP 응답 상태에 해당하는 값 |
| `msg` | 처리 결과 메시지 |
| `data` | 실제 응답 데이터 |

### 성공 응답 예시

```json
{
  "resultCode": "200-1",
  "statusCode": 200,
  "msg": "회원가입이 완료되었습니다.",
  "data": {
    "id": 1,
    "username": "user1"
  }
}
```

### 실패 응답 예시

```json
{
  "resultCode": "400-1",
  "statusCode": 400,
  "msg": "아이디를 입력해주세요.",
  "data": {}
}
```

위 JSON은 응답 형식을 설명하기 위한 예시이다.

이처럼 성공과 실패의 응답 구조를 통일하면 프론트엔드에서 결과를 일관된 방식으로 처리할 수 있다.

또한 `GlobalException`과 Validation 예외를 공통 응답 형식으로 변환하면 API 사용자는 예외 유형과 관계없이 동일한 구조로 오류 정보를 전달받을 수 있다.

### 회원가입 API 처리 흐름

```text
클라이언트 회원가입 요청
          ↓
    @RestController
          ↓
     Validation 검사
          ↓
      Service 실행
          ↓
   회원 정보 DB 저장
          ↓
       RsData 응답

※ 예외 발생 시
   ExceptionHandler
          ↓
   공통 오류 응답 반환
```

---

## 핵심 정리

이번 주차에서는 Spring Boot를 이용하여 회원가입 API를 구현하면서, REST API 설계와 예외 처리의 중요성을 학습했다.

특히 다음 내용을 이해하는 것이 중요하다고 생각했다.

- **REST API:** HTTP 메서드와 자원 중심의 URL을 이용하여 클라이언트와 서버가 통신한다.
- **Postman:** 프론트엔드 없이 API 요청 및 응답을 테스트할 수 있다.
- **Validation:** 반복적인 입력값 검증 코드를 줄이고 검증 규칙을 선언적으로 적용한다.
- **ExceptionHandler:** 여러 종류의 예외를 공통으로 처리하여 중복 코드를 줄인다.
- **RsData:** 성공과 실패의 응답 형식을 통일하여 클라이언트의 처리 과정을 단순화한다.

결과적으로 이번 학습을 통해 REST API는 단순히 데이터를 반환하는 기능뿐 아니라, **입력값 검증과 예외 처리, 일관된 응답 구조까지 고려하여 설계해야 한다**는 점을 이해했다.~~~~