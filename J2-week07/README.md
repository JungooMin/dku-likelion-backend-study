# J2-week07

## Spring Boot 회원가입 REST API 구현 및 예외 처리

이번 주차에서는 Spring Boot를 이용해 회원가입 API를 구현하고, 클라이언트의 요청을 검증하고 처리한 뒤 결과를 반환하는 과정을 학습했다.

특히 **REST API, Validation, ExceptionHandler, RsData**를 중심으로 API의 안정성과 응답 일관성을 높이는 방법을 공부했다.

---

## 1. REST API와 HTTP 메서드

### REST API란?

REST API는 HTTP를 활용해 클라이언트가 서버의 자원에 접근하고 작업을 요청하도록 설계하는 방식이다.

Spring Boot는 HTML 화면을 직접 생성할 수도 있지만, React와 같은 프론트엔드 애플리케이션에 JSON 데이터를 제공하는 API 서버로도 사용할 수 있다.

```text
클라이언트 (React, Postman 등)
           ↓ HTTP 요청
Spring Boot REST API
           ↓
Controller → Service → Repository
           ↓
        Database
           ↓
        JSON 응답
```

REST API에서는 일반적으로 URL을 자원 중심의 명사로 작성하고, HTTP 메서드로 수행할 작업을 구분한다.

| HTTP 메서드 | 역할 | 예시 |
|---|---|---|
| GET | 조회 | `GET /articles/1` |
| POST | 생성 | `POST /articles` |
| PUT / PATCH | 수정 | `PUT /articles/1` |
| DELETE | 삭제 | `DELETE /articles/1` |

예를 들어 `GET /article/delete/1`보다 `DELETE /articles/1`이 요청의 목적을 더 명확하게 표현한다.

### Postman

Postman은 API의 요청과 응답을 테스트하는 도구이다.

프론트엔드를 구현하지 않아도 HTTP 메서드, 요청 헤더, JSON 본문을 직접 설정하여 백엔드 API를 테스트할 수 있다.

---

## 2. 회원가입 API 구현

회원가입은 클라이언트가 전달한 회원 정보를 검증하고, 데이터베이스에 새로운 회원을 저장하는 기능이다.

이번 실습에서는 기존 `MemberController`에서 시작해 REST API 방식의 `ApiV1MemberController`를 구현했다.

### 주요 어노테이션

- `@RestController`: API 요청을 처리하고 객체를 JSON 등의 응답 본문으로 변환한다.
- `@RequestMapping`: 컨트롤러의 공통 URL 경로를 지정한다.
- `@PostMapping`: POST 방식의 요청을 처리한다.
- `@RequestBody`: JSON 요청 본문을 Java 객체로 변환한다.

회원가입 API 예시:

```http
POST /api/v1/members
Content-Type: application/json
```

```json
{
  "username": "user1",
  "password": "example-password",
  "nickname": "사용자1"
}
```

`/api/v1`처럼 API에 버전을 지정하면, 향후 API 구조를 변경할 때 기존 클라이언트와의 호환성을 관리하기 편리하다.

회원가입에 필요한 중복 아이디 확인과 데이터 저장 등의 비즈니스 로직은 `MemberService`에서 처리하고, Controller는 HTTP 요청과 응답을 담당하도록 역할을 구분했다.

---

## 3. Validation을 이용한 입력값 검증

회원가입에서는 아이디, 비밀번호, 닉네임 등의 필수 정보가 올바르게 입력되었는지 확인해야 한다.

처음에는 `if`문과 유틸리티 클래스 `Ut`를 이용해 직접 검증했지만, **Validation**을 적용하면 검증 규칙을 선언적으로 표현할 수 있다.

`build.gradle`에 다음 의존성을 추가했다.

```gradle
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

주요 어노테이션은 다음과 같다.

- `@NotBlank`: 문자열이 비어 있거나 공백만 있는 경우를 허용하지 않는다.
- `@NotNull`: null 값을 허용하지 않는다.
- `@Min`: 숫자의 최솟값을 제한한다.
- `@Valid`: 객체에 정의한 검증 규칙을 실행한다.

실습에서는 요청 객체의 필수 필드에 `@NotBlank`를 적용하고, Controller의 요청 매개변수에 `@Valid`를 추가했다.

이렇게 하면 입력값 검증 로직을 반복 작성할 필요가 줄어들며, 검증에 실패한 경우 `MethodArgumentNotValidException`을 통해 오류를 처리할 수 있다.

---

## 4. ExceptionHandler를 이용한 공통 예외 처리

### return과 throw의 차이

- **return:** 메서드 실행을 종료하고 결과를 반환한다.
- **throw:** 예외를 발생시켜 호출자 또는 예외 처리기로 제어를 전달한다.

일반적인 반환값으로 오류까지 표현하면 정상적인 결과와 실패 상황이 섞여 코드가 복잡해질 수 있다.

반면 `throw`를 사용하면 예외 상황을 정상적인 비즈니스 로직과 분리하여 처리할 수 있다.

### ExceptionHandler

`@ExceptionHandler`는 특정 예외가 발생했을 때 실행할 예외 처리 메서드를 지정하는 어노테이션이다.

여기에 `@ControllerAdvice`를 함께 사용하면 여러 Controller에 공통으로 적용되는 예외 처리 구조를 만들 수 있다.

이번 실습에서는 `GlobalExceptionHandler`를 통해 다음 예외를 처리하는 구조를 학습했다.

- `GlobalException`: 중복 아이디 등 비즈니스 규칙 위반
- `MethodArgumentNotValidException`: 입력값 검증 실패
- 기타 `Exception`: 예상하지 못한 서버 내부 오류

예외를 공통으로 처리하면 Controller마다 오류 처리 코드를 반복해서 작성하지 않아도 되고, API 응답 형식도 일관되게 유지할 수 있다.

또한 `ResponseEntity`를 이용하면 오류 메시지뿐 아니라 실제 HTTP 응답 상태도 함께 설정할 수 있다.

---

## 5. RsData를 활용한 응답 형식 통일

API에서는 성공했을 때와 실패했을 때 서로 다른 구조로 응답하면 프론트엔드에서 처리하기 복잡해질 수 있다.

이를 해결하기 위해 실습에서는 `RsData` 클래스를 활용하여 공통 응답 구조를 정의했다.

| 필드 | 역할 |
|---|---|
| `resultCode` | 애플리케이션의 세부 처리 결과 |
| `statusCode` | 처리 결과에 해당하는 상태 코드 |
| `msg` | 사용자 또는 클라이언트에 전달할 메시지 |
| `data` | 실제 응답 데이터 |

응답 형식 예시:

```json
{
  "resultCode": "200-1",
  "statusCode": 200,
  "msg": "회원가입이 완료되었습니다.",
  "data": {
    "username": "user1",
    "nickname": "사용자1"
  }
}
```

위 JSON은 구조를 설명하기 위한 예시이며 실제 응답 데이터는 구현에 따라 달라진다.

`RsData<T>`는 제네릭을 사용하므로 API마다 서로 다른 데이터 타입을 반환하면서 공통 응답 구조를 유지할 수 있다.

또한 요청용 객체와 응답용 객체를 분리하면 필요한 데이터만 선택적으로 전달할 수 있다. 특히 회원가입 응답에서는 비밀번호 같은 민감한 정보를 제외하는 것이 중요하다.

---

## 6. 회원가입 API 전체 처리 흐름

```text
클라이언트
   ↓ POST /api/v1/members
ApiV1MemberController
   ↓
@Valid를 통한 입력값 검증
   ↓
MemberService
   ├── 중복 아이디 검사
   └── 회원 정보 저장
   ↓
RsData로 결과 반환
   ↓
클라이언트에 JSON 응답

※ 예외 발생 시
   → GlobalExceptionHandler
   → 공통 오류 응답 반환
```

이 구조에서 Controller는 요청과 응답, Service는 비즈니스 로직, Validation은 입력값 검증, ExceptionHandler는 오류 처리를 담당한다.

각 기능의 역할을 분리하면 코드의 가독성과 유지보수성을 높일 수 있다.

---

## 핵심 정리

이번 주차에서는 Spring Boot를 활용해 회원가입 API를 구현하면서 다음 내용을 학습했다.

- **REST API:** HTTP 메서드와 자원 중심 URL을 활용한 요청 처리
- **Postman:** 프론트엔드 없이 API를 테스트하는 방법
- **Validation:** 어노테이션을 활용한 입력값 검증
- **ExceptionHandler:** 예외 상황을 공통으로 처리하는 구조
- **RsData:** 성공과 실패의 응답 형식을 일관되게 유지하는 방법

가장 중요하게 이해한 부분은 **Validation → 비즈니스 로직 → 예외 처리 → 공통 응답 반환**으로 이어지는 흐름이다.

단순히 회원가입 기능을 만드는 것을 넘어, 요청 데이터를 검증하고 오류를 일관된 방식으로 처리하여 프론트엔드와 연동하기 편리한 API를 설계하는 방법을 배웠다.