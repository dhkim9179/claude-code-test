# Spring Batch 예제 프로젝트

이 프로젝트는 Spring Batch의 두 가지 주요 처리 방식을 보여주는 예제입니다.

## 프로젝트 구조

```
src/main/java/com/example/demo/
├── DemoApplication.java                    # Spring Boot 메인 애플리케이션
├── domain/
│   └── User.java                           # 사용자 도메인 모델
├── batch/
│   ├── config/
│   │   ├── TaskletJobConfig.java          # Tasklet 기반 Job 설정
│   │   └── ChunkJobConfig.java            # Chunk 기반 Job 설정
│   ├── tasklet/
│   │   └── SimpleTasklet.java             # Tasklet 구현체
│   └── chunk/
│       ├── UserItemReader.java            # ItemReader 구현체
│       ├── UserItemProcessor.java         # ItemProcessor 구현체
│       └── UserItemWriter.java            # ItemWriter 구현체
```

## 예제 설명

### 1. Tasklet 예제 (TaskletJobConfig)

**패키지**: `com.example.demo.batch.tasklet`

**설명**:
- Tasklet은 단일 작업을 수행하는 가장 간단한 배치 처리 방식입니다.
- 주로 초기화, 정리, 단순 작업에 사용됩니다.
- Step에서 한 번만 실행됩니다.

**주요 클래스**:
- `SimpleTasklet`: 단순 작업을 수행하는 Tasklet 구현체
- `TaskletJobConfig`: Tasklet을 사용하는 Job과 Step 정의

**실행 흐름**:
```
Job 시작 → Step 시작 → Tasklet 실행 → Step 종료 → Job 종료
```

### 2. Chunk 기반 예제 (ChunkJobConfig)

**패키지**: `com.example.demo.batch.chunk`

**설명**:
- Chunk 기반 처리는 대량의 데이터를 효율적으로 처리하는 방식입니다.
- 데이터를 읽고(Read) → 처리하고(Process) → 쓰는(Write) 과정을 반복합니다.
- 지정된 크기의 청크 단위로 트랜잭션을 관리합니다.

**주요 클래스**:
- `UserItemReader`: 사용자 데이터를 읽어오는 Reader (메모리 기반 예제)
- `UserItemProcessor`: 읽어온 데이터를 가공하는 Processor
  - 비활성 사용자 필터링
  - 사용자 이름을 대문자로 변환
- `UserItemWriter`: 처리된 데이터를 저장하는 Writer (로그 출력)
- `ChunkJobConfig`: Chunk 기반 Job과 Step 정의 (chunk size: 2)

**실행 흐름**:
```
Job 시작
  → Step 시작
    → Reader로 2개 데이터 읽기
    → 각 데이터를 Processor로 처리 (필터링, 변환)
    → 처리된 데이터를 Writer로 일괄 저장
    → 트랜잭션 커밋
    → 더 이상 읽을 데이터가 없을 때까지 반복
  → Step 종료
→ Job 종료
```

## 설정 파일

### application.properties
```properties
# 배치 Job 자동 실행 비활성화
spring.batch.job.enabled=false

# H2 데이터베이스 설정 (배치 메타데이터 저장)
spring.datasource.url=jdbc:h2:mem:batchdb
spring.datasource.username=sa
spring.datasource.password=

# H2 Console 활성화
spring.h2.console.enabled=true

# Spring Batch 스키마 자동 생성
spring.batch.jdbc.initialize-schema=always
```

## Job 실행 방법

### 1. 프로그래밍 방식으로 실행

```java
@Autowired
private JobLauncher jobLauncher;

@Autowired
private Job taskletJob;  // 또는 chunkJob

public void runBatch() throws Exception {
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();

    jobLauncher.run(taskletJob, jobParameters);
}
```

### 2. 커맨드라인으로 실행

```bash
# Tasklet Job 실행
./gradlew bootRun --args='--spring.batch.job.enabled=true --spring.batch.job.names=taskletJob'

# Chunk Job 실행
./gradlew bootRun --args='--spring.batch.job.enabled=true --spring.batch.job.names=chunkJob'
```

## 주요 개념

### Tasklet vs Chunk

| 구분 | Tasklet | Chunk |
|------|---------|-------|
| 사용 시기 | 단순 작업, 단일 처리 | 대량 데이터 처리 |
| 트랜잭션 | Step 단위 | Chunk 단위 |
| 반복 | 한 번 실행 | 데이터가 있는 동안 반복 |
| 복잡도 | 낮음 | 높음 |
| 예시 | 파일 삭제, 초기화 | ETL, 대용량 데이터 변환 |

### ItemReader
- 데이터 소스로부터 데이터를 읽어오는 인터페이스
- 지원 타입: 파일, 데이터베이스, JMS, 메모리 등
- null 반환 시 읽기 종료

### ItemProcessor
- 읽어온 데이터를 가공하는 인터페이스
- 데이터 변환, 검증, 필터링 수행
- null 반환 시 해당 데이터는 Writer로 전달되지 않음 (필터링)

### ItemWriter
- 처리된 데이터를 최종 목적지에 쓰는 인터페이스
- Chunk 단위로 데이터를 일괄 처리
- 트랜잭션 범위 내에서 실행

## 참고사항

- Spring Batch는 메타데이터를 데이터베이스에 저장합니다.
- Job 실행 이력은 `BATCH_JOB_INSTANCE`, `BATCH_JOB_EXECUTION` 테이블에 저장됩니다.
- H2 Console에서 메타데이터를 확인할 수 있습니다: http://localhost:8080/h2-console
