# J2-week03

## 학습 주제
Docker와 fly.io를 이용한 Spring Boot 애플리케이션 배포 및 GitHub Actions를 활용한 CI/CD 구축

---

## 1. Docker

Java로 만든 애플리케이션은 JVM 환경에서 실행된다.  
따라서 서버에 애플리케이션만 복사한다고 바로 실행할 수 있는 것이 아니라, JVM과 같이 애플리케이션 실행에 필요한 환경이 서버에 준비되어 있어야 한다.

Docker를 사용하면 애플리케이션과 실행에 필요한 환경을 함께 Docker Image로 구성할 수 있다.  
이를 통해 서버마다 필요한 환경을 직접 설정해야 하는 작업을 줄이고, 동일한 환경에서 애플리케이션을 실행할 수 있다.

### Docker의 주요 구성 요소

- **Dockerfile**
    - Docker Image를 만들기 위한 설정 파일이다.
    - 이미지 생성에 필요한 명령을 순서대로 작성한다.

- **Docker Image**
    - 애플리케이션의 실행 파일과 실행에 필요한 환경을 포함할 수 있다.
    - Container를 생성하기 위한 템플릿 역할을 한다.

- **Container**
    - Docker Image를 기반으로 실제 실행되는 환경이다.

관계를 간단하게 표현하면 다음과 같다.

```text
Dockerfile → Docker Image → Container
   레시피       밀키트          요리
```

하나의 Dockerfile로 여러 개의 Image를 만들 수 있고, 하나의 Image를 기반으로 여러 개의 Container를 생성할 수 있다.

---

## 2. Docker Image

Docker Image의 이름은 다음과 같이 Repository 이름과 Tag의 조합으로 구성된다.

```text
Repository:Tag
```

Tag를 생략하면 기본적으로 `latest`가 사용된다.

예시:

```text
nginx-1:latest
```

Dockerfile을 이용하여 이미지를 생성할 수 있다.

```bash
docker build -t nginx-1 .
```

생성된 이미지는 다음 명령으로 확인할 수 있다.

```bash
docker images
```

---

## 3. Docker Container와 Port

Docker Image를 실행하면 Container가 생성된다.

예시:

```bash
docker run -d --name nginx-1-1 -p 80:80 nginx-1
```

`-p` 옵션은 Host와 Container의 포트를 연결한다.

```text
-p HOST_PORT:CONTAINER_PORT
```

따라서

```text
-p 80:80
```

은 Host의 80번 포트와 Container의 80번 포트를 연결한다는 의미이다.

Container 관련 명령어:

```bash
# 실행 중인 Container 확인
docker ps

# 정지된 Container까지 모두 확인
docker ps -a

# Container 삭제
docker rm -f 컨테이너이름
```

---

## 4. Web Server

HTML과 같은 웹 문서를 브라우저에 전달하기 위해서는 Web Server가 필요하다.

이번 실습에서는 대표적인 Web Server인 **NGINX**를 사용하였다.

```text
Client
   ↓ HTTP Request
Web Server (NGINX)
   ↓
HTML Response
```

---

## 5. fly.io를 이용한 배포

서버는 일반적인 컴퓨터와 비슷하지만 외부에서 접근할 수 있도록 공인 IP가 필요하다.

fly.io에서는 하나의 프로그램 또는 서비스를 **App**이라는 단위로 관리한다.

Spring Boot 프로젝트를 fly.io에 배포하기 위해서는 주요하게 다음 요소가 필요하다.

```text
Spring Boot Project
├── Dockerfile
└── fly.toml
```

### fly.io App 생성

프로젝트 폴더에서 다음 명령을 실행한다.

```bash
fly launch --no-deploy
```

이 과정에서 fly.io App이 생성되고 프로젝트에 `fly.toml` 파일이 생성된다.

`fly.toml`은 fly.io App의 설정을 관리하는 파일이다.

### 배포

```bash
fly deploy
```

배포 과정에서는 프로젝트 파일을 기반으로 Docker Image가 만들어지고, 해당 Image를 기반으로 Container가 실행된다.

전체적인 흐름은 다음과 같다.

```text
Spring Boot Project
        ↓
Dockerfile
        ↓
Docker Image
        ↓
fly.io
        ↓
Container
        ↓
서비스 실행
```

---

## 6. Spring Boot 애플리케이션 배포

이번 실습에서는 Java 21 기반 Spring Boot 프로젝트를 Docker Image로 만들어 fly.io에 배포하였다.

Spring Boot 프로젝트에 Dockerfile을 작성하고,

```bash
fly launch --no-deploy
```

를 통해 fly.io App과 `fly.toml`을 생성한 뒤,

```bash
fly deploy
```

를 이용하여 서버에 배포하였다.

소스코드가 변경된 경우 다시 `fly deploy`를 실행하면 변경된 내용을 서버에 반영할 수 있다.

---

## 7. GitHub Actions와 CI/CD

매번 직접 `fly deploy` 명령을 실행하지 않고 GitHub에 코드를 Push했을 때 자동으로 배포되도록 **GitHub Actions**를 사용할 수 있다.

이와 같이 코드 변경부터 배포까지의 과정을 자동화하는 것을 **CI/CD**라고 한다.

이번 프로젝트에서는 다음 위치에 Workflow 파일을 작성하였다.

```text
.github/
└── workflows/
    └── deploy.yml
```

전체적인 자동 배포 흐름은 다음과 같다.

```text
코드 수정
   ↓
Git Commit
   ↓
GitHub Push
   ↓
GitHub Actions 실행
   ↓
fly.io 배포
```

따라서 CI/CD 구축 이후에는 매번 직접 `fly deploy`를 실행하지 않아도 GitHub의 main 브랜치에 Push하면 자동으로 배포가 진행된다.

---

## 정리

이번 학습을 통해 Dockerfile, Docker Image, Container의 관계를 이해하고 Docker를 이용하여 애플리케이션 실행 환경을 구성하는 방법을 학습하였다.

또한 Spring Boot 애플리케이션을 fly.io에 배포하고, GitHub Actions를 연동하여 GitHub에 Push했을 때 자동으로 배포되는 CI/CD 구조를 실습하였다.