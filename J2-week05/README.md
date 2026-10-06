# J2-week05

## Spring Data JPA를 이용한 영속성

### 1. 영속성

기존에는 데이터를 메모리에 저장했기 때문에 Spring Boot를 종료하면 데이터가 사라졌다.

데이터를 DB에 저장하여 영속성을 부여하면 Spring Boot나 컴퓨터를 재부팅해도 데이터가 유지된다.

이번 주차에서는 Spring Data JPA를 이용하여 Java에서 MySQL의 데이터를 다루는 방법을 학습했다.

---

## 2. ORM과 Spring Data JPA

Spring Boot에서 발생한 데이터를 DB에 저장하려면 결국 INSERT, SELECT 등의 SQL이 실행되어야 한다.

하지만 매번 SQL을 직접 작성하는 대신 Java 코드로 DB 작업을 할 수 있는데, 이를 위해 ORM을 사용한다.

ORM을 사용하면 개발자가 직접 SQL을 작성하지 않아도 상황에 맞는 SQL을 만들어 실행해준다.

Spring Boot에서 DB를 다룰 때는 보통 Spring Data JPA를 사용한다.

### Spring Data JPA의 처리 구조

```text
Spring Data JPA
        ↓
       JPA
        ↓
    Hibernate
        ↓
   JDBC Driver
        ↓
  MySQL Driver
        ↓
      MySQL
```

즉, Java에서는 Spring Data JPA를 사용하지만 실제 데이터는 최종적으로 MySQL에 저장된다.

### JPA 의존성 추가

`build.gradle`

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
runtimeOnly 'com.mysql:mysql-connector-j'
```

### DB 연결 설정

`application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/surl_dev
    username: root
    password: lldj123414
    driver-class-name: com.mysql.cj.jdbc.Driver
```

이 설정을 통해 Spring Boot가 어떤 MySQL DB에 접속할지 지정할 수 있다.

---

## 3. Entity

DB에 저장할 데이터를 Java에서 객체로 다루기 위해 Entity를 사용한다.

이번 실습에서는 `Article` 클래스를 Entity로 만들었다.

```java
@Entity
public class Article {
    ...
}
```

주요 어노테이션

- `@Entity` : 해당 클래스를 JPA가 관리하도록 설정
- `@Id` : Primary Key로 사용할 필드 지정
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` : ID 값을 자동으로 증가시키도록 설정

### 테이블 자동 생성 및 수정

`application.yml`에 다음 설정을 추가했다.

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

`ddl-auto: update`를 사용하면 Entity의 구조를 기준으로 DB 테이블을 생성하거나 변경할 수 있다.

---

## 4. JPA가 실행하는 SQL 확인

Java 코드로 DB를 다루더라도 내부에서는 SQL이 실행된다.

따라서 JPA가 실제로 어떤 SQL을 실행하는지 확인할 수 있도록 로그 설정을 추가했다.

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      default_batch_fetch_size: 100
      format_sql: true
      highlight_sql: true
      use_sql_comments: true

logging:
  level:
    com.ll.demo03: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.orm.jdbc.bind: TRACE
    org.hibernate.orm.jdbc.extract: TRACE
    org.springframework.transaction.interceptor: TRACE
```

이를 통해 Repository를 이용해 데이터를 저장하거나 조회했을 때 실제로 실행되는 SQL을 콘솔에서 확인할 수 있다.

---

## 5. Bean과 Repository

### Bean

Bean은 개발자가 직접 `new`를 이용해서 객체를 만들지 않아도 Spring이 생성하고 관리해주는 객체이다.

Spring이 관리하는 객체는 필요한 곳에 주입받아 사용할 수 있다.

### ArticleRepository

```java
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
```

`JpaRepository`를 상속받으면 Article을 DB에 저장하거나 조회하고 삭제하는 등의 기능을 사용할 수 있다.

따라서 SQL의 INSERT문을 직접 작성하지 않고 다음과 같이 Java 코드로 데이터를 저장할 수 있다.

```java
articleRepository.save(
        Article.builder()
                .title("제목")
                .body("내용")
                .build()
);
```

실습에서는 `NotProd` 클래스에서 `ArticleRepository`를 주입받아 샘플 Article을 저장했다.

```java
@Profile("!prod")
@Configuration
@RequiredArgsConstructor
public class NotProd {

    private final ArticleRepository articleRepository;

    @Bean
    public ApplicationRunner initNotProd() {
        return args -> {
            articleRepository.save(
                    Article.builder()
                            .title("제목")
                            .body("내용")
                            .build()
            );
        };
    }
}
```

`@Profile("!prod")`를 사용하여 운영 환경이 아닐 때 실행되도록 설정했다.

---

# 이후 학습 내용 정리

실습은 Repository를 이용해 Article을 저장하는 부분까지 진행했고, 이후 내용은 강의를 통해 개념을 정리했다.

## Repository를 이용한 CRUD

`JpaRepository`에는 DB 데이터를 다룰 수 있는 여러 기능이 기본적으로 제공된다.

예를 들어

- `save()` : 데이터 저장
- `findById()` : ID를 이용한 데이터 조회
- `findAll()` : 전체 데이터 조회
- `count()` : 데이터 개수 확인
- `delete()` : 데이터 삭제

등을 사용할 수 있다.

필요한 조회 기능이 기본으로 제공되지 않는 경우에는 Repository에 규칙에 맞는 메서드를 정의해서 사용할 수도 있다.

---

## 영속성 컨텍스트와 Dirty Checking

JPA가 관리하고 있는 Entity의 값이 트랜잭션 안에서 변경되면 JPA가 변경된 내용을 확인하여 DB에 UPDATE를 실행할 수 있다.

이러한 변경 감지를 Dirty Checking이라고 한다.

따라서 JPA에서는 항상 UPDATE SQL을 직접 작성해야 하는 것이 아니라, 관리되고 있는 객체의 상태를 변경하는 방식으로 DB의 데이터가 수정될 수도 있다.

---

## Service와 Repository

프로젝트의 기능이 많아지면 Repository를 여러 곳에서 직접 사용하는 것보다 Service를 통해 사용하는 구조로 만들 수 있다.

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

각 부분의 역할을 간단히 정리하면

- Controller : 요청을 받음
- Service : 실제 기능에 필요한 로직 처리
- Repository : DB 접근

으로 나눌 수 있다.

---

## Transaction

여러 DB 작업을 하나의 작업 단위로 묶기 위해 Transaction을 사용한다.

Spring에서는 `@Transactional`을 이용할 수 있다.

```java
@Transactional
```

조회만 수행하는 경우에는 다음과 같이 사용할 수 있다.

```java
@Transactional(readOnly = true)
```

트랜잭션을 사용하면 여러 DB 작업을 하나의 단위로 관리하고, 작업 도중 문제가 발생했을 때 변경 내용을 되돌리는 롤백을 사용할 수 있다.

---

## 생성 날짜와 수정 날짜

게시물이나 회원 등의 데이터에는 생성된 시간과 마지막으로 수정된 시간을 저장해야 하는 경우가 많다.

JPA에서는 다음과 같은 기능을 이용해 날짜를 관리할 수 있다.

```java
@CreatedDate
private LocalDateTime createDate;

@LastModifiedDate
private LocalDateTime modifyDate;
```

이를 이용하면 Entity가 생성되거나 수정될 때 날짜를 관리할 수 있다.

---

## 공통 필드 분리

여러 Entity에서 `id`, `createDate`, `modifyDate`와 같은 필드가 반복될 수 있다.

이러한 공통 필드는 별도의 부모 클래스로 분리할 수 있다.

```text
BaseEntity
    ↓
BaseTime
    ↓
각 Entity
```

`@MappedSuperclass`를 이용하면 부모 클래스의 공통 필드를 Entity에서 상속받아 사용할 수 있다.

이를 통해 Entity마다 같은 필드를 반복해서 작성하는 것을 줄일 수 있다.

---

## Entity 사이의 관계

게시물에는 작성자가 존재하기 때문에 Article과 Member를 서로 연결해야 한다.

한 명의 회원이 여러 개의 게시물을 작성할 수 있으므로 다음과 같이 관계를 표현할 수 있다.

```java
@ManyToOne
private Member author;
```

Java에서는 `Member` 객체를 이용하여 작성자를 표현하고, DB에서는 회원의 ID를 이용해 서로 연결할 수 있다.

---

## Surl에 영속성 적용

기존 URL 단축 서비스에서는 Surl 데이터를 메모리에 저장했다.

이 방식은 프로그램을 종료하면 데이터가 사라지는 문제가 있다.

Surl에도 Entity, Repository, Service 구조를 적용하면 데이터를 MySQL에 저장할 수 있다.

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Spring Data JPA
    ↓
MySQL
```

결과적으로 URL 단축 서비스의 데이터도 메모리가 아니라 DB에 저장하여 프로그램을 재시작한 이후에도 유지할 수 있다.

---

# 정리

이번 주차에서는 기존에 SQL을 직접 작성하여 DB를 다루던 방식에서 Spring Data JPA를 이용하여 Java 코드로 DB를 다루는 방법을 학습했다.

특히 다음 내용을 이해하는 것이 중요하다고 생각했다.

- 영속성을 적용하면 프로그램을 재시작해도 데이터가 유지된다.
- ORM을 사용하면 SQL을 직접 작성하지 않고 Java 코드로 DB 작업을 할 수 있다.
- Entity는 Java 객체와 DB 테이블을 연결한다.
- Repository는 Entity의 저장과 조회 등 DB 접근을 담당한다.
- Service를 이용해 비즈니스 로직과 DB 접근을 분리할 수 있다.
- Transaction을 이용하여 여러 DB 작업을 하나의 단위로 관리할 수 있다.
- Dirty Checking을 이용해 Entity의 변경 사항을 DB에 반영할 수 있다.
- Entity 사이의 관계도 객체의 관계로 표현할 수 있다.
- 최종적으로 기존 메모리 기반 데이터를 DB에 저장하여 서비스에 영속성을 부여할 수 있다.