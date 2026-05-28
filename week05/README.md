# Week 05

이번 주차에는 웹 서비스가 동작하는 기본 구조를 학습하고, Spring Boot를 이용하여 상품 정보를 관리하는 CRUD API를 구현해보았습니다.
또한 Postman과 H2 Database를 활용하여 직접 API 요청과 데이터 저장 과정도 확인해보았습니다.

---

## 웹 동작 원리

웹은 사용자의 요청(Request)과 서버의 응답(Response)으로 동작합니다.
브라우저와 같은 클라이언트가 서버에 요청을 보내면, 서버는 해당 요청을 처리한 뒤 결과를 다시 반환합니다.

웹 통신에서는 HTTP 프로토콜을 사용하며, URL을 통해 원하는 자원의 위치를 지정할 수 있습니다.

예시 흐름:

```text
Client → Request → Server
Client ← Response ← Server
```

또한 HTTP는 상태를 유지하지 않는 Stateless 방식이기 때문에, 로그인 정보 유지 등을 위해 쿠키와 세션을 사용합니다.

* 쿠키 : 브라우저에 저장되는 데이터
* 세션 : 서버에서 사용자 상태를 관리하는 방식

네트워크 기초 개념으로는 다음 내용을 학습하였습니다.

* IP : 컴퓨터를 구별하기 위한 주소
* Port : 하나의 서버 안에서 서비스를 구분하는 번호
* DNS : 도메인 주소를 실제 IP 주소로 변환하는 시스템

---

## Spring Boot 프로젝트 생성

Spring Initializr를 사용하여 Gradle 기반 Spring Boot 프로젝트를 생성하였습니다.

프로젝트 설정은 다음과 같습니다.

* Language : Java
* Java Version : 17
* Packaging : Jar
* Spring Boot : 4.0.6

사용한 주요 Dependency는 다음과 같습니다.

* Spring Web
* Spring Data JPA
* H2 Database

---

## Spring Boot 구조 이해

실습에서는 Controller → Service → Repository → Database 구조를 사용하여 API를 구현하였습니다.

```text
Client
 → Controller
 → Service
 → Repository
 → H2 Database
```

각 계층의 역할은 다음과 같습니다.

* Controller : 클라이언트 요청 처리
* Service : 비즈니스 로직 수행
* Repository : 데이터베이스 접근
* Entity : 데이터베이스 테이블과 연결되는 객체

특히 Repository에서는 `JpaRepository`를 활용하여 별도의 SQL 작성 없이 CRUD 기능을 구현할 수 있었습니다.

---

## Product CRUD API 구현

상품 정보를 생성(Create), 조회(Read), 수정(Update), 삭제(Delete)하는 API를 구현하였습니다.

| 기능    | Method | URL                  |
| ----- | ------ | -------------------- |
| 상품 생성 | POST   | `/api/products`      |
| 상품 조회 | GET    | `/api/products/{id}` |
| 상품 수정 | PUT    | `/api/products/{id}` |
| 상품 삭제 | DELETE | `/api/products/{id}` |

요청 데이터 예시:

```json
{
  "name": "상품명",
  "price": 10000
}
```

---

## API 테스트 및 데이터 확인

Postman을 사용하여 각 API 요청을 테스트하였습니다.

* POST 요청으로 상품 데이터 저장
* GET 요청으로 특정 상품 조회
* PUT 요청으로 상품 정보 수정
* DELETE 요청으로 데이터 삭제

또한 H2 Console에 접속하여 실제 데이터가 저장되는 과정도 확인하였습니다.

```text
http://localhost:8080/h2-console
```

---

## 실습 과정에서 어려웠던 점

### `javax.persistence` 오류

강의 코드와 동일하게 `javax.persistence`를 사용했지만 실행 과정에서 오류가 발생하였습니다.

찾아보니 최신 Spring Boot 환경에서는 `jakarta.persistence` 패키지를 사용해야 했고, import 문을 수정하여 문제를 해결할 수 있었습니다.

### JPA 기본 생성자

처음에는 Entity 클래스에 기본 생성자가 왜 필요한지 이해되지 않았습니다.

하지만 JPA가 데이터베이스 데이터를 객체로 변환하는 과정에서 기본 생성자를 사용한다는 점을 알게 되었고, Entity 클래스에 기본 생성자를 추가하여 해결할 수 있었습니다.

### 개발 환경 차이

강의에서는 Java 11과 Spring Boot 2 버전을 사용했지만, 실제 실습 환경은 Java 17과 Spring Boot 4 버전이었습니다.

이 때문에 일부 코드와 패키지 구조가 달라 오류가 발생했고, 직접 수정하며 버전 차이에 적응하는 과정을 경험할 수 있었습니다.

---

## 느낀 점

이번 실습에서는 단순히 코드를 따라 작성하는 것보다, 요청이 Controller를 거쳐 Database까지 전달되고 다시 응답으로 돌아오는 전체 흐름을 이해하려고 노력했습니다.

특히 Spring Boot 프로젝트 생성, Gradle 설정, 라이브러리 문제 해결 과정에서 예상보다 많은 오류가 발생했는데, 이를 직접 해결하면서 개발 환경 설정 역시 중요한 과정이라는 점을 느낄 수 있었습니다.

아직 모든 코드를 완벽하게 이해한 것은 아니지만, 웹 API가 어떤 구조로 동작하는지 이전보다 훨씬 구체적으로 이해할 수 있었습니다.
