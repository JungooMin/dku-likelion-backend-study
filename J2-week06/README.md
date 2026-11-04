# J2-week06 | PostgreSQL부터 AWS·Terraform까지

> **학습 주제:** AWS와 Terraform을 활용한 인프라 구축 및 Spring Boot 서비스 배포  
> **학습 범위:** 1~12강 실습 및 학습 / 13~20강 개념 학습  
> **실습 진행:** PostgreSQL 연결, Spring Boot 실행, AWS CLI 설치, Terraform 초기화  
> **미실습:** AWS EC2 생성, 운영 서버 구성, 수동·자동 배포

## 1. 데이터베이스 전환과 개발 환경

### Spring Data JPA와 DBMS

Spring Boot에서는 Spring Data JPA를 사용해 Java 코드로 데이터베이스를 다룰 수 있다.

실제 데이터베이스 작업은 다음 구조로 처리된다.

```text
Service
   ↓
Spring Data JPA (Repository)
   ↓
JPA
   ↓
Hibernate
   ↓
JDBC
   ↓
JDBC Driver
   ↓
DBMS
```

- **Spring Data JPA:** Repository를 통한 데이터 접근 기능 제공
- **JPA:** Java 객체와 관계형 데이터베이스를 연결하기 위한 표준 명세
- **Hibernate:** JPA를 구현한 ORM 프레임워크
- **JDBC:** Java에서 데이터베이스에 접근하기 위한 API
- **JDBC Driver:** MySQL, PostgreSQL 등 DBMS별 통신 담당

이러한 구조 덕분에 MySQL에서 PostgreSQL로 변경하더라도 Service나 Repository 코드를 대부분 유지할 수 있다. 다만 DBMS마다 SQL 문법이나 데이터 타입이 다르므로 호환성 검증은 필요하다.

### PostgreSQL 연결 실습

Docker를 이용하여 PostgreSQL 컨테이너를 실행하고 DBeaver에서 `surl_dev` 데이터베이스를 생성했다.

PostgreSQL의 기본 포트는 `5432`, 기본 관리자 계정은 `postgres`이다.

DBMS에 연결하려면 해당 JDBC 드라이버가 필요하다.

**build.gradle**

```gradle
runtimeOnly 'com.mysql:mysql-connector-j'
runtimeOnly 'org.postgresql:postgresql'
```

**application.yml**

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/surl_dev
    username: postgres
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
```

DB 연결 URL과 JDBC 드라이버를 일치시켜야 하며, 비밀번호는 환경변수로 관리할 수 있다.

또한 다음 설정을 사용하면 JPA Entity를 기준으로 데이터베이스 테이블을 생성하거나 변경할 수 있다.

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

Spring Boot 실행 후 초기 데이터 생성 코드가 실행되면서 `member`, `article`, `surl` 테이블에 샘플 데이터가 저장되는 것을 확인했다.

**PostgreSQL 조회 예시**

```sql
SELECT datname FROM pg_database;

SELECT tablename
FROM pg_catalog.pg_tables
WHERE schemaname = 'public';

SELECT * FROM member;
SELECT * FROM article;
SELECT * FROM surl;
```

### H2 데이터베이스

H2는 Java 기반의 경량 DBMS로, 별도의 DB 서버를 실행하지 않아도 사용할 수 있어 개발과 테스트에 적합하다.

```yaml
spring:
  datasource:
    url: jdbc:h2:./db_dev;MODE=MySQL
    username: sa
    password:
    driver-class-name: org.h2.Driver
```

- **파일 모드:** 데이터를 파일에 저장하여 재실행 후에도 유지 가능
- **메모리 모드:** 메모리에 데이터를 저장하며 DB 종료 시 데이터가 사라짐
- **호환 모드:** 일부 MySQL SQL 문법을 지원

H2 Console을 활성화했다면 `http://localhost:8070/h2-console`에서 데이터베이스에 접근할 수 있다.

H2는 테스트에 편리하지만 실제 운영 DBMS와 동작이 다를 수 있으므로 주의해야 한다.

### Docker 재시작 정책

```bash
docker run --restart unless-stopped ...
```

`unless-stopped`는 컨테이너가 비정상 종료되거나 Docker가 재시작되었을 때 컨테이너를 자동으로 다시 실행한다.

단, 사용자가 명시적으로 중지한 컨테이너는 자동 재시작하지 않는다.

---

## 2. 개발·테스트·운영 환경 분리

Spring Boot에서는 Profile을 사용하여 실행 환경마다 서로 다른 설정을 적용할 수 있다.

| 환경 | 설정 파일 | 역할 |
|---|---|---|
| 개발 (`dev`) | `application-dev.yml` | 로컬 DB 및 디버깅 설정 |
| 테스트 (`test`) | `application-test.yml` | 테스트 전용 DB 및 환경 |
| 운영 (`prod`) | `application-prod.yml` | 운영 DB와 실제 서비스 설정 |

운영 환경에서는 불필요한 로그 출력을 줄여 성능 저하와 민감한 정보 노출을 방지해야 한다.

```yaml
logging:
  level:
    com.ll.demo03: INFO
    org.hibernate.SQL: INFO
    org.hibernate.orm.jdbc.bind: INFO
    org.hibernate.orm.jdbc.extract: INFO
```

`System.out.println()`보다 `log.debug()` 등의 로깅 기능을 사용하면 환경별 로그 수준에 따라 출력 여부를 제어할 수 있다.

또한 테스트 코드에 `@ActiveProfiles("test")`를 지정하면 운영 환경과 분리된 테스트 설정을 적용할 수 있다.

### 비밀정보 관리

데이터베이스 비밀번호, JWT 비밀키, AWS 액세스 키 등은 GitHub에 공개하지 않아야 한다.

이번 실습에서는 JWT 비밀키를 `application-secret.yml`에 설정하고 `.gitignore`를 통해 Git 추적 대상에서 제외했다.

```gitignore
src/main/resources/application-secret.yml
```

운영 환경에서는 환경변수나 GitHub Actions Secrets 등을 활용해 민감한 정보를 관리할 수 있다.

---

## 3. Fly.io 배포와 도메인 관리

Fly.io에서는 애플리케이션과 PostgreSQL 데이터베이스를 별도로 운영할 수 있다.

이렇게 DB를 분리하면 애플리케이션이 재배포되어도 데이터베이스의 데이터를 유지할 수 있다.

### Fly.io PostgreSQL 연결

Fly.io 내부 네트워크에 있는 DB에 로컬 DBeaver로 접근하려면 프록시를 사용할 수 있다.

```bash
fly proxy 5434:5432 -a DB_APP_NAME
```

위 명령어는 로컬 `5434` 포트와 Fly.io의 원격 DB 프록시 포트 `5432`를 연결한다.

이때 DBeaver에서 사용하는 `127.0.0.1`은 원격 서버의 주소가 아니라 프록시가 실행 중인 **내 컴퓨터의 주소**이다.

Fly.io PostgreSQL 생성 실습은 진행했으며, 생성한 클라우드 DB는 비용 관리를 위해 삭제했다.

### 도메인과 DNS

개발 환경에서는 `localhost`를 사용하지만 실제 운영 환경에서는 사용자가 접근할 수 있는 도메인이 필요하다.

- **DNS:** 도메인 이름을 IP 주소 등과 연결
- **A 레코드:** 도메인을 IPv4 주소에 연결
- **CNAME:** 도메인을 다른 도메인 이름에 연결
- **서브도메인:** 하나의 도메인으로 여러 서비스를 구분

하나의 도메인만 구매하더라도 `www.example.com`, `api.example.com`처럼 서브도메인을 이용하여 여러 서비스를 운영할 수 있다.

GitHub Actions를 활용하면 코드 Push를 계기로 빌드와 배포 작업을 자동으로 실행할 수 있다.

---

## 4. AWS 기초: IAM, EC2, VPC

AWS는 서버, 네트워크, 데이터베이스, 저장소 등 다양한 클라우드 서비스를 제공한다.

대표적인 서비스는 다음과 같다.

| 서비스 | 역할 |
|---|---|
| IAM | 사용자 및 접근 권한 관리 |
| EC2 | 가상 서버 제공 |
| VPC | 가상 네트워크 구성 |
| RDS | 관리형 관계형 데이터베이스 |
| S3 | 객체 스토리지 |

### IAM

IAM은 AWS 리소스에 접근하는 사용자와 권한을 관리하는 서비스이다.

- **User:** AWS에 접근하는 사용자
- **Policy:** 특정 작업에 대한 허용·거부 권한을 정의한 문서
- **Role:** 필요한 권한을 임시로 부여받아 사용할 수 있는 역할

루트 계정은 강력한 권한을 가지므로 평소 사용을 최소화하고, 일반적인 작업에서는 IAM 사용자나 역할을 사용하는 것이 안전하다.

특히 필요한 권한만 부여하는 **최소 권한 원칙**이 중요하다.

### AWS 리소스 관리 방법

AWS 리소스는 여러 방식으로 관리할 수 있다.

- **AWS 콘솔:** 웹 화면에서 직접 리소스 생성 및 관리
- **AWS CLI:** 명령어를 이용한 리소스 관리 및 자동화
- **Terraform:** 인프라를 코드로 정의하고 선언적으로 관리

Terraform은 AWS Provider를 통해 AWS API를 호출하여 리소스를 관리한다.

---

## 5. AWS CLI 설치 및 설정

AWS CLI는 터미널에서 AWS 리소스를 관리할 수 있도록 해주는 명령줄 도구이다.

Windows PowerShell에서 다음 명령어로 설치 여부를 확인했다.

```powershell
aws --version
```

IAM 액세스 키를 사용하는 경우 다음 명령어로 AWS 인증 정보를 설정할 수 있다.

```powershell
aws configure
```

주요 설정 항목은 다음과 같다.

```text
AWS Access Key ID
AWS Secret Access Key
Default region name: ap-northeast-2
Default output format: json
```

`ap-northeast-2`는 AWS 서울 리전이다.

인증 정보 확인에는 다음 명령어를 사용할 수 있다.

```powershell
aws configure list
aws sts get-caller-identity
```

실습에서는 Windows에 AWS CLI를 설치하고 명령어가 정상적으로 실행되는 것을 확인했다.

액세스 키와 시크릿 키는 비밀번호와 같은 민감한 인증 정보이므로 GitHub에 업로드하면 안 된다.

---

## 6. Terraform과 IaC

Terraform은 **IaC(Infrastructure as Code)** 도구로, 서버와 네트워크 등의 인프라를 코드로 정의하고 관리할 수 있다.

AWS 콘솔에서 직접 리소스를 생성하는 대신 `.tf` 파일에 원하는 인프라 상태를 작성한다.

Terraform은 실제 인프라와 코드에 정의된 상태를 비교하여 필요한 작업을 계획하고 적용한다.

### Terraform 프로젝트 실습

이번 실습에서는 `terrafom-project-1` 폴더를 생성하고 AWS Provider를 설정했다.

**main.tf**

```hcl
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0"
    }
  }
}

provider "aws" {
  region = "ap-northeast-2"
}

resource "aws_vpc" "example" {
  cidr_block = "10.0.0.0/16"

  tags = {
    Name = "example"
  }
}
```

위 코드는 서울 리전에 `10.0.0.0/16` CIDR 블록을 사용하는 VPC를 정의한 것이다.

### Terraform 주요 명령어

| 명령어 | 역할 |
|---|---|
| `terraform init` | Provider 설치 및 작업 디렉터리 초기화 |
| `terraform plan` | 인프라 변경 계획 확인 |
| `terraform apply` | 실제 클라우드 리소스 생성 및 변경 |
| `terraform destroy` | Terraform이 관리하는 리소스 삭제 |

이번 실습에서는 `terraform init`을 실행하여 AWS Provider **v4.67.0**을 설치하고 초기화를 완료했다.

```text
Terraform has been successfully initialized!
```

`terraform plan`, `terraform apply`, `terraform destroy`의 역할은 학습했지만 실제 AWS 리소스 생성과 삭제는 진행하지 않았다.

`terraform apply`는 실제 AWS 리소스를 생성하므로 비용이 발생할 수 있다.

### Terraform 파일 관리

- `.tf`: 인프라 설정 코드
- `.terraform/`: Provider 및 모듈 관련 파일
- `.terraform.lock.hcl`: 선택된 Provider 버전 정보
- `terraform.tfstate`: Terraform이 관리하는 인프라 상태 정보

GitHub에는 `.tf`와 `.terraform.lock.hcl`을 업로드하고, `.terraform/`, `*.tfstate*` 및 비밀정보가 포함된 파일은 제외해야 한다.

---

# 이후 강의 핵심 개념 (13~20강 · 미실습)

이후 강의는 실제 AWS 리소스를 생성하거나 서버에 배포하지 않고, 주요 기술의 역할과 전체 실습 과정을 중심으로 학습했다.

## 7. EC2 자동 구축과 Docker 이미지 관리

### Terraform으로 EC2 생성

EC2는 AWS에서 제공하는 가상 서버 서비스이다.

Terraform에서는 EC2 인스턴스의 운영체제, 서버 사양, 네트워크 및 보안 설정을 코드로 정의할 수 있다.

- **AMI:** EC2에서 사용할 운영체제 및 소프트웨어 이미지
- **Instance Type:** CPU, 메모리 등 서버 사양
- **Security Group:** 서버의 네트워크 접근 제어
- **User Data:** EC2 최초 실행 시 초기 설정을 자동화하는 스크립트

Terraform으로 인프라를 정의하면 동일한 서버 환경을 반복적으로 구성할 수 있다.

### Docker와 GitHub Container Registry

Docker는 애플리케이션과 필요한 실행 환경을 이미지로 패키징하고 컨테이너로 실행하는 기술이다.

GHCR(GitHub Container Registry)은 Docker 이미지를 저장하고 배포할 수 있는 GitHub의 컨테이너 레지스트리이다.

```text
Spring Boot 프로젝트
        ↓
Docker 이미지 빌드
        ↓
GHCR에 이미지 업로드
        ↓
EC2에서 이미지 다운로드
        ↓
Docker 컨테이너 실행
```

비공개 이미지를 다운로드하려면 적절한 패키지 읽기 권한으로 인증해야 한다.

또한 JWT 비밀키나 DB 비밀번호 같은 민감한 정보는 Docker 이미지에 직접 포함하지 않고, 실행 시 안전하게 주입하는 것이 좋다.

---

## 8. MySQL, Redis, Nginx Proxy Manager

### MySQL

EC2에서 Docker를 이용해 MySQL을 실행하면 서버 내부에 데이터베이스 환경을 구성할 수 있다.

MySQL은 `사용자명@호스트` 형태로 계정을 구분하므로 접속 위치에 따라 서로 다른 권한을 설정할 수 있다.

운영 환경에서는 모든 권한을 가진 root 계정보다 필요한 권한만 부여한 전용 계정을 사용하는 것이 안전하다.

### Redis

Redis는 메모리 기반 데이터 저장소로, 캐시와 세션 관리 등에 활용할 수 있다.

Spring Boot의 세션을 애플리케이션 내부에만 저장하면 서버가 교체될 때 로그인 상태가 사라질 수 있다.

Redis를 외부 세션 저장소로 사용하면 여러 서버가 세션을 공유할 수 있어 배포 중에도 로그인 상태를 유지하는 데 도움이 된다.

단, Spring Session 등의 추가 설정이 필요하다.

### Nginx Proxy Manager

Nginx는 클라이언트의 요청을 내부 서버로 전달하는 Reverse Proxy 역할을 할 수 있다.

Nginx Proxy Manager는 이러한 설정을 웹 화면에서 관리할 수 있도록 해주는 도구이다.

주요 기능은 다음과 같다.

- 도메인에 따라 요청을 서로 다른 서비스로 전달
- 외부 요청을 내부 애플리케이션 포트로 연결
- HTTPS 및 SSL 인증서 관리
- 하나의 서버에서 여러 서비스 운영

---

## 9. Spring Boot 수동 배포와 자동 배포

### 수동 배포

수동 배포는 개발자가 직접 서버에 접속하여 애플리케이션을 배포하는 방식이다.

```text
EC2 서버 접속
      ↓
GitHub 코드 다운로드
      ↓
운영 환경 설정
      ↓
Docker 이미지 빌드
      ↓
컨테이너 실행
      ↓
로그 및 서비스 상태 확인
```

수동 배포에서는 환경변수, DB 연결 정보, Docker 설정 등이 올바르지 않으면 배포가 실패할 수 있다.

`docker logs`를 이용하면 컨테이너에서 발생한 오류를 확인할 수 있다.

수동 배포는 구조를 이해하기 쉽지만 반복 작업이 많고, 기존 컨테이너를 먼저 중지하면 서비스가 일시적으로 중단될 수 있다.

### GitHub Actions를 활용한 CI/CD

CI/CD는 코드 변경 이후 빌드, 테스트, 배포 과정을 자동화하는 방식이다.

- **CI(Continuous Integration):** 코드 통합 후 빌드 및 테스트 자동화
- **CD(Continuous Delivery/Deployment):** 배포 준비 또는 운영 환경 배포 자동화

GitHub Actions에서는 `.github/workflows/deploy.yml`에 자동화 과정을 정의할 수 있다.

```text
GitHub에 코드 Push
        ↓
GitHub Actions 실행
        ↓
Docker 이미지 빌드
        ↓
GHCR에 이미지 업로드
        ↓
EC2에 배포 명령 전달
        ↓
새 컨테이너 실행
```

이 방식을 사용하면 개발자가 매번 EC2에 직접 접속해 배포할 필요가 줄어든다.

### 무중단 배포

무중단 배포는 새로운 버전을 배포할 때 서비스가 중단되지 않도록 하는 방식이다.

강의에서는 두 개의 Docker 컨테이너를 번갈아 사용하는 배포 구조를 학습했다.

```text
기존 컨테이너 A 실행
        ↓
새 컨테이너 B 실행
        ↓
B의 Health Check
        ↓
요청을 B로 전환
        ↓
기존 컨테이너 A 종료
```

이 과정에서 `zero_downtime_deploy.py`와 `/actuator/health`를 이용해 새 컨테이너의 정상 작동 여부를 확인한 후 트래픽을 전환한다.

이를 통해 기존 컨테이너를 바로 종료하는 방식보다 배포 중 서비스 중단 위험을 줄일 수 있다.

---

## 10. 전체 서비스 배포 구조

이번 강의에서 학습한 기술들은 다음과 같이 연결된다.

```text
개발자
   ↓ Git Push
GitHub Actions (CI/CD)
   ↓
Docker 이미지 빌드
   ↓
GHCR (이미지 저장소)
   ↓
AWS EC2
   ├── Spring Boot 컨테이너
   ├── MySQL (데이터 영속성)
   ├── Redis (세션 관리)
   └── Nginx Proxy Manager (Reverse Proxy)
                ↑
            사용자 요청

Terraform → AWS 인프라 생성 및 관리
IAM → AWS 리소스 접근 권한 관리
VPC → AWS 네트워크 환경 구성
```

Terraform은 EC2와 네트워크 등의 인프라를 생성하고 관리한다.

GitHub Actions는 코드 변경 이후 Docker 이미지 빌드와 배포 과정을 자동화한다.

EC2에서는 Docker를 이용하여 Spring Boot와 관련 서비스를 실행하며, Nginx Proxy Manager는 외부 요청을 내부 애플리케이션으로 전달한다.

MySQL은 서비스 데이터를 영속적으로 저장하고, Redis는 여러 애플리케이션 인스턴스 간 세션 공유에 활용할 수 있다.

---

# 핵심 정리

이번 주차에서는 기존 Spring Boot 애플리케이션을 실제 클라우드 서버에서 운영하기 위해 필요한 데이터베이스, 인프라, 배포 기술들을 학습했다.

특히 다음 내용을 이해하는 것이 중요하다고 생각했다.

- **Spring Data JPA**를 사용하면 DBMS 변경 시 애플리케이션 코드의 수정 범위를 줄일 수 있다.
- **개발·테스트·운영 환경 분리**를 통해 각 환경에 맞는 DB 및 로그 설정을 적용해야 한다.
- **AWS IAM, VPC, EC2**는 각각 접근 권한 관리, 네트워크 구성, 서버 실행을 담당한다.
- **AWS CLI**를 이용하면 AWS 리소스를 명령어로 관리할 수 있다.
- **Terraform**은 인프라를 코드로 정의하여 반복적으로 관리할 수 있는 IaC 도구이다.
- **Docker와 GHCR**을 이용하면 애플리케이션을 이미지 형태로 관리하고 배포할 수 있다.
- **Redis**는 여러 서버 간 세션 공유에 활용할 수 있다.
- **Nginx Proxy Manager**는 외부 요청을 적절한 내부 서비스로 전달한다.
- **GitHub Actions**는 빌드 및 배포 과정을 자동화할 수 있다.
- **무중단 배포**에서는 새로운 컨테이너가 정상적으로 실행된 후 트래픽을 전환하는 과정이 중요하다.

이번 실습에서는 **PostgreSQL 연결 및 Spring Boot 실행, AWS CLI 설치, Terraform 프로젝트 초기화까지 진행**했다.

이후 EC2 생성과 Docker 기반 서비스 배포, Redis 및 Nginx 설정, CI/CD와 무중단 배포는 개념과 전체적인 실습 절차를 중심으로 학습했다.

실제 AWS 리소스를 생성할 경우 비용이 발생할 수 있으므로, 실습 이후 불필요한 리소스를 정리하고 사용 요금을 확인하는 것이 중요하다.