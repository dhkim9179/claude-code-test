# Jasypt 암호화 사용 가이드

## 개요

Jasypt(Java Simplified Encryption)는 Spring Boot 애플리케이션에서 민감한 정보(데이터베이스 비밀번호, API 키 등)를 암호화하여 안전하게 관리할 수 있도록 도와주는 라이브러리입니다.

## 설정 파일

### 1. build.gradle
```gradle
implementation 'com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5'
```

### 2. JasyptConfig.java
- 위치: `src/main/java/com/example/demo/config/JasyptConfig.java`
- Jasypt 암호화/복호화 설정을 담당하는 Bean 생성

### 3. application.yml
```yaml
jasypt:
  encryptor:
    bean: jasyptStringEncryptor
    password: ${JASYPT_ENCRYPTOR_PASSWORD:defaultPassword}
    algorithm: PBEWithMD5AndDES
    key-obtention-iterations: 1000
    pool-size: 1
    provider-name: SunJCE
    salt-generator-classname: org.jasypt.salt.RandomSaltGenerator
    string-output-type: base64
```

## 사용 방법

### 1. 암호화할 텍스트 생성

#### 방법 A: Gradle을 이용한 암호화 (권장)

프로젝트 루트에서 다음 명령어 실행:

```bash
# 의존성 다운로드
./gradlew build

# 암호화 실행
java -cp $(./gradlew dependencies --configuration runtimeClasspath | grep jasypt | awk '{print $NF}') \
  org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI \
  input="암호화할텍스트" \
  password="암호화키" \
  algorithm=PBEWithMD5AndDES
```

#### 방법 B: JAR 파일을 직접 다운로드하여 암호화

1. Jasypt JAR 다운로드:
```bash
wget https://repo1.maven.org/maven2/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar
```

2. 암호화 실행:
```bash
java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI \
  input="암호화할텍스트" \
  password="암호화키" \
  algorithm=PBEWithMD5AndDES
```

출력 예시:
```
----ENVIRONMENT-----------------
Runtime: Oracle Corporation Java HotSpot(TM) 64-Bit Server VM 21.0.1+12-LTS-29
----ARGUMENTS-------------------
algorithm: PBEWithMD5AndDES
input: 암호화할텍스트
password: 암호화키
----OUTPUT----------------------
aBcDeFgHiJkLmNoPqRsTuVwXyZ==
```

### 2. application.yml에 암호화된 값 설정

암호화된 값을 `ENC(암호화된문자열)` 형식으로 설정 파일에 추가:

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@localhost:1521:ORCL
    username: admin
    password: ENC(aBcDeFgHiJkLmNoPqRsTuVwXyZ==)  # 암호화된 비밀번호

# 다른 민감한 정보도 동일한 방식으로 암호화
api:
  secret-key: ENC(암호화된키)
```

### 3. 애플리케이션 실행

#### 방법 A: 환경변수로 암호화 키 전달 (권장)
```bash
export JASYPT_ENCRYPTOR_PASSWORD=암호화키
./gradlew bootRun
```

또는

```bash
JASYPT_ENCRYPTOR_PASSWORD=암호화키 java -jar demo-0.0.1-SNAPSHOT.jar
```

#### 방법 B: 실행 인자로 암호화 키 전달
```bash
./gradlew bootRun --args='--jasypt.encryptor.password=암호화키'
```

또는

```bash
java -jar demo-0.0.1-SNAPSHOT.jar --jasypt.encryptor.password=암호화키
```

## 복호화 테스트

암호화된 값이 올바르게 복호화되는지 테스트:

```bash
java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
  input="aBcDeFgHiJkLmNoPqRsTuVwXyZ==" \
  password="암호화키" \
  algorithm=PBEWithMD5AndDES
```

## 환경별 설정

각 환경(local, dev, prd)마다 다른 암호화 키를 사용할 수 있습니다:

### application-local.yml
```yaml
jasypt:
  encryptor:
    password: ${JASYPT_ENCRYPTOR_PASSWORD:localDefaultPassword}
```

### application-dev.yml
```yaml
jasypt:
  encryptor:
    password: ${JASYPT_ENCRYPTOR_PASSWORD:devDefaultPassword}
```

### application-prd.yml
```yaml
jasypt:
  encryptor:
    password: ${JASYPT_ENCRYPTOR_PASSWORD}  # 운영 환경에서는 반드시 환경변수로 전달
```

## 보안 권장사항

1. **암호화 키 관리**
   - 암호화 키는 절대 소스 코드에 포함하지 마세요
   - 환경변수나 외부 키 관리 시스템(AWS Secrets Manager, HashiCorp Vault 등)을 사용하세요
   - 환경별로 다른 암호화 키를 사용하세요

2. **알고리즘 선택**
   - 기본 알고리즘 `PBEWithMD5AndDES`는 간단한 용도에 적합
   - 더 강력한 보안이 필요한 경우 `PBEWITHHMACSHA512ANDAES_256` 사용 권장
   - Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy 필요할 수 있음

3. **설정 파일 보안**
   - `.gitignore`에 환경변수 파일 추가
   - 운영 환경 설정 파일은 별도로 관리

## 문제 해결

### 암호화 키 불일치 오류
```
Error creating bean with name 'dataSource': Failed to decrypt property
```
- 암호화할 때 사용한 키와 실행 시 전달한 키가 일치하는지 확인하세요

### 알고리즘 오류
```
EncryptionOperationNotPossibleException
```
- JCE Unlimited Strength Policy가 설치되어 있는지 확인하세요
- 알고리즘 이름이 정확한지 확인하세요

## 참고 자료

- [Jasypt 공식 문서](http://www.jasypt.org/)
- [jasypt-spring-boot GitHub](https://github.com/ulisesbocchio/jasypt-spring-boot)
