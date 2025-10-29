# 마일리지 배치 데이터 생성 및 집계 시스템

## 개요

이 프로젝트는 Spring Batch를 활용하여 대량의 마일리지 데이터를 생성하고 일별로 집계하는 배치 시스템입니다.

## 주요 기능

### 1. 마일리지 데이터 생성
- **회원 수**: 1,000명
- **마일리지 잔액**: 0 ~ 100,000원 사이 골고루 분포
- **기간**: 오늘로부터 한 달 이전 (30일)
- **일별 거래 건수**: 10,000건 (고정)
- **총 예상 거래 건수**: 약 30만 건 (10,000건 × 30일)

### 2. 마일리지 일별 집계
- 일 단위로 마일리지 적립/사용 금액 및 건수 집계
- 순 증감액 계산 (적립 - 사용)
- 집계 데이터를 별도 테이블에 저장

### 3. 마일리지 집계 스케줄러
- **매일 자정(00:00)**: 전날 데이터 집계
- **매시간 정각**: 당일 데이터 실시간 재집계
- **매주 월요일 오전 2시**: 지난주 데이터 재집계 (검증용)

## 데이터베이스 스키마

### MILEAGE 테이블
회원의 현재 마일리지 잔액 정보

```sql
CREATE TABLE MILEAGE (
    MEMBER_ID BIGINT PRIMARY KEY,      -- 회원 ID
    BALANCE BIGINT NOT NULL,           -- 마일리지 잔액
    CREATE_DATE TIMESTAMP NOT NULL,    -- 생성일시
    UPDATE_DATE TIMESTAMP NOT NULL     -- 수정일시
);
```

### MILEAGE_HISTORY 테이블
마일리지 적립/사용 이력

```sql
CREATE TABLE MILEAGE_HISTORY (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 이력 ID
    MILEAGE_MEMBER_ID BIGINT NOT NULL,     -- 회원 ID
    TYPE VARCHAR(20) NOT NULL,             -- 유형 (EARN: 적립, USE: 사용)
    AMOUNT INTEGER NOT NULL,               -- 마일리지 금액
    DESCRIPTION VARCHAR(500),              -- 설명
    CREATE_DATE TIMESTAMP NOT NULL         -- 생성일시
);
```

### MILEAGE_DAILY_SUMMARY 테이블
일별 마일리지 집계

```sql
CREATE TABLE MILEAGE_DAILY_SUMMARY (
    SUMMARY_DATE DATE PRIMARY KEY,         -- 집계 일자
    TOTAL_EARN_AMOUNT BIGINT NOT NULL,     -- 총 적립 금액
    TOTAL_EARN_COUNT BIGINT NOT NULL,      -- 총 적립 건수
    TOTAL_USE_AMOUNT BIGINT NOT NULL,      -- 총 사용 금액
    TOTAL_USE_COUNT BIGINT NOT NULL,       -- 총 사용 건수
    NET_AMOUNT BIGINT NOT NULL,            -- 순 증감액
    CREATE_DATE TIMESTAMP NOT NULL,        -- 생성일시
    UPDATE_DATE TIMESTAMP NOT NULL         -- 수정일시
);
```

## 배치 작업 구성

### 1. mileageFullProcessJob
전체 프로세스를 실행하는 메인 Job (자동 실행)
- **Step 1**: mileageDataGenerationStep - 데이터 생성
- **Step 2**: mileageAggregationStep - 일별 집계

### 2. mileageDataGenerationJob
회원 및 마일리지 히스토리 데이터만 생성

### 3. mileageAggregationJob
마일리지 히스토리를 기반으로 일별 집계만 수행

## 실행 방법

### 자동 실행 (기본)
스프링 부트 애플리케이션 시작 시 자동으로 배치가 실행됩니다.

```bash
./gradlew bootRun
```

### 스케줄러 자동 실행
애플리케이션이 실행되면 다음 스케줄러가 자동으로 활성화됩니다:

1. **일별 집계 (매일 00:00)**: 전날 마일리지 거래 집계
2. **실시간 집계 (매시간 정각)**: 당일 마일리지 거래 집계
3. **주간 재집계 (매주 월요일 02:00)**: 지난주 데이터 검증 및 재집계

### 수동 실행
특정 Job만 실행하고 싶은 경우, `MileageBatchRunner`를 비활성화하고 다음과 같이 실행:

```bash
# 데이터 생성만 실행
./gradlew bootRun --args='--spring.batch.job.names=mileageDataGenerationJob'

# 집계만 실행
./gradlew bootRun --args='--spring.batch.job.names=mileageAggregationJob'

# 전체 실행
./gradlew bootRun --args='--spring.batch.job.names=mileageFullProcessJob'
```

## 성능 고려사항

### 1. 배치 Insert 사용
- 1,000건씩 묶어서 배치 insert 수행
- 단건 insert 대비 약 10배 이상 성능 향상

### 2. 인덱스 최적화
- `MILEAGE_HISTORY` 테이블에 조회 성능 향상을 위한 인덱스 생성
  - `IDX_MILEAGE_HISTORY_MEMBER_DATE`: 회원별 날짜 조회
  - `IDX_MILEAGE_HISTORY_CREATE_DATE`: 날짜별 집계 조회

### 3. 트랜잭션 관리
- 배치 크기만큼만 트랜잭션 유지하여 메모리 효율성 확보

## 서비스 레이어

### MileageDailySummaryService
일별 집계 데이터 관리

주요 메서드:
- `getDailySummary(LocalDate)`: 특정 일자 집계 조회
- `getDailySummariesByDateRange(LocalDate, LocalDate)`: 기간별 집계 조회
- `aggregateAndSave(LocalDate)`: 특정 일자 집계 생성
- `aggregateByDateRange(LocalDate, LocalDate)`: 기간별 집계 생성
- `getTotalStatistics(LocalDate, LocalDate)`: 기간 내 전체 통계

### MileageService
마일리지 관리 (기존)

주요 메서드:
- `getMileage(Long)`: 회원 마일리지 조회
- `getMileageHistory(Long)`: 회원 마일리지 이력 조회
- `earnMileage(Long, Integer, String)`: 마일리지 적립
- `useMileage(Long, Integer, String)`: 마일리지 사용

## 예상 실행 시간

하드웨어 사양에 따라 다르지만, 대략적인 실행 시간:

- **데이터 생성**: 약 10~30초
  - 회원 1,000명 생성: 약 1초 미만
  - 마일리지 히스토리 30만건 생성: 약 10~30초

- **일별 집계**: 약 5~15초
  - 30일치 데이터 집계: 약 5~15초

- **스케줄러 집계**: 약 1~3초 (일별 1만건 기준)

## 데이터 확인

### H2 Console 접속
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:batchdb
Username: sa
Password: (공백)
```

### 조회 쿼리 예제

```sql
-- 회원 수 확인
SELECT COUNT(*) FROM MILEAGE;

-- 총 마일리지 이력 건수
SELECT COUNT(*) FROM MILEAGE_HISTORY;

-- 일별 집계 조회
SELECT * FROM MILEAGE_DAILY_SUMMARY ORDER BY SUMMARY_DATE;

-- 특정 회원의 마일리지 잔액 및 이력
SELECT m.MEMBER_ID, m.BALANCE,
       COUNT(mh.ID) as HISTORY_COUNT,
       SUM(CASE WHEN mh.TYPE = 'EARN' THEN mh.AMOUNT ELSE 0 END) as TOTAL_EARN,
       SUM(CASE WHEN mh.TYPE = 'USE' THEN mh.AMOUNT ELSE 0 END) as TOTAL_USE
FROM MILEAGE m
LEFT JOIN MILEAGE_HISTORY mh ON m.MEMBER_ID = mh.MILEAGE_MEMBER_ID
WHERE m.MEMBER_ID = 1
GROUP BY m.MEMBER_ID, m.BALANCE;

-- 일별 거래 건수 조회
SELECT CAST(CREATE_DATE AS DATE) as TRADE_DATE,
       COUNT(*) as TOTAL_COUNT,
       SUM(CASE WHEN TYPE = 'EARN' THEN 1 ELSE 0 END) as EARN_COUNT,
       SUM(CASE WHEN TYPE = 'USE' THEN 1 ELSE 0 END) as USE_COUNT
FROM MILEAGE_HISTORY
GROUP BY CAST(CREATE_DATE AS DATE)
ORDER BY TRADE_DATE;
```

## 주요 파일 구조

```
src/main/java/com/example/demo/
├── domain/
│   ├── Mileage.java                    # 마일리지 엔티티
│   ├── MileageHistory.java             # 마일리지 이력 엔티티
│   └── MileageDailySummary.java        # 일별 집계 엔티티
├── mapper/
│   ├── MileageMapper.java              # 마일리지 Mapper
│   ├── MileageHistoryMapper.java       # 마일리지 이력 Mapper
│   └── MileageDailySummaryMapper.java  # 일별 집계 Mapper
├── batch/
│   ├── config/
│   │   └── MileageBatchJobConfig.java  # 배치 Job 설정
│   └── tasklet/
│       ├── MileageDataGenerationTasklet.java  # 데이터 생성 Tasklet
│       └── MileageAggregationTasklet.java     # 집계 Tasklet
├── service/
│   ├── MileageService.java                    # 마일리지 서비스
│   └── MileageDailySummaryService.java        # 일별 집계 서비스
├── scheduler/
│   └── MileageAggregationScheduler.java       # 마일리지 집계 스케줄러
├── controller/
│   └── MileageController.java                 # 마일리지 REST API
└── runner/
    └── MileageBatchRunner.java         # 배치 자동 실행 Runner

src/main/resources/
├── schema.sql                          # 테이블 스키마 정의
├── mybatis/mapper/
│   ├── MileageMapper.xml               # 마일리지 Mapper XML
│   ├── MileageHistoryMapper.xml        # 마일리지 이력 Mapper XML
│   └── MileageDailySummaryMapper.xml   # 일별 집계 Mapper XML
└── application.properties              # 애플리케이션 설정
```

## 문제 해결

### OutOfMemoryError 발생 시
JVM 힙 메모리 증가:
```bash
./gradlew bootRun -Dorg.gradle.jvmargs=-Xmx4g
```

### 배치 실행 속도가 느린 경우
1. `BATCH_SIZE` 값 조정 (MileageDataGenerationTasklet.java)
2. 일별 거래 건수 조정 (`MIN_DAILY_TRANSACTIONS`, `MAX_DAILY_TRANSACTIONS`)

### 자동 실행 비활성화
`MileageBatchRunner.java`에서 `@Component` 어노테이션 제거 또는 주석 처리
