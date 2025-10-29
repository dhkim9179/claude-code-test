# 변경 사항 요약

## 1. 자동 실행 배치 제거
- **파일 삭제**: `src/main/java/com/example/demo/runner/MileageBatchRunner.java`
- **설명**: 애플리케이션 시작 시 자동으로 실행되던 마일리지 배치 작업을 제거했습니다.
- **영향**: 이제 배치 작업은 API를 통해서만 수동으로 실행할 수 있습니다.

## 2. BatchJobService 오류 수정
- **신규 파일**: `src/main/java/com/example/demo/config/BatchConfig.java`
- **내용**: `JobRegistryBeanPostProcessor` 빈 추가
- **설명**: JobRegistry에 Job 빈들을 자동으로 등록하도록 설정하여 BatchJobService가 정상적으로 작동하도록 수정했습니다.
- **해결된 문제**: BatchJobService에서 `jobRegistry.getJob()` 호출 시 NoSuchJobException 발생 방지

## 3. 데이터베이스 쿼리 최적화

### 3.1 인덱스 추가
- **파일**: `src/main/resources/schema.sql`
- **추가된 인덱스**:
  - `IDX_MILEAGE_HISTORY_MEMBER_TYPE_DATE` on `(MILEAGE_MEMBER_ID, TYPE, CREATE_DATE)`
- **효과**: `findByMemberIdAndType` 쿼리 성능 향상 (풀 스캔 방지)

### 3.2 쿼리 개선
- **파일**: `src/main/resources/mybatis/mapper/MileageDailySummaryMapper.xml`
- **변경 내용**: `aggregateByDate` 쿼리 최적화
  - **Before**: `WHERE CAST(create_date AS DATE) = #{summaryDate}` (인덱스 사용 불가)
  - **After**: `WHERE create_date >= #{summaryDate} AND create_date < DATEADD('DAY', 1, #{summaryDate})` (인덱스 사용 가능)
- **효과**: create_date 인덱스를 활용하여 풀 테이블 스캔 방지

## 4. API 버저닝 구현

### 4.1 기존 API 유지 (하위 호환성)
- `/api/mileage/*` - 기존 엔드포인트 유지 (deprecated)
- `/api/batch/*` - 기존 엔드포인트 유지 (deprecated)

### 4.2 v1 API
- **패키지**: `com.example.demo.controller.v1`
- **클래스**:
  - `MileageControllerV1` - `/api/v1/mileage/*`
  - `BatchJobControllerV1` - `/api/v1/batch/*`
- **특징**: 기존 API와 동일한 기능, deprecated 표시

### 4.3 v2 API (개선된 버전)
- **패키지**: `com.example.demo.controller.v2`
- **클래스**:
  - `MileageControllerV2` - `/api/v2/mileage/*`
  - `BatchJobControllerV2` - `/api/v2/batch/*`
- **신규 DTO**: `com.example.demo.dto.v2.ApiResponse`

#### v2 주요 개선사항:
1. **표준화된 응답 형식**
   ```json
   {
     "success": true,
     "data": { ... },
     "meta": {
       "timestamp": "2025-10-29T10:30:00",
       "version": "v2",
       "pageInfo": { ... }
     },
     "error": null
   }
   ```

2. **향상된 에러 처리**
   - 명확한 에러 코드 (MILEAGE_NOT_FOUND, INVALID_DATE_RANGE 등)
   - 상세한 에러 메시지 (한글)
   - 적절한 HTTP 상태 코드

3. **추가 검증 로직**
   - 날짜 범위 검증 (startDate > endDate 체크)
   - 조회 일수 제한 (1~365일)
   - 미래 날짜 집계 방지

4. **새로운 기능**
   - 마일리지 이력 조회 시 타입 필터링 지원 (`?type=EARN` or `?type=USE`)
   - 향후 페이징 지원을 위한 구조 마련

5. **로깅 개선**
   - 모든 API 호출에 대한 디버그 로깅
   - 에러 상황에 대한 명확한 로그

### 4.4 v3 준비
- v2의 구조를 기반으로 향후 v3 API를 쉽게 추가할 수 있는 확장 가능한 구조

## 5. 파일 구조 변경

```
src/main/java/com/example/demo/
├── config/
│   └── BatchConfig.java (신규)
├── controller/
│   ├── MileageController.java (deprecated)
│   ├── BatchJobController.java (deprecated)
│   ├── v1/
│   │   ├── MileageControllerV1.java (신규)
│   │   └── BatchJobControllerV1.java (신규)
│   └── v2/
│       ├── MileageControllerV2.java (신규)
│       └── BatchJobControllerV2.java (신규)
├── dto/
│   └── v2/
│       └── ApiResponse.java (신규)
└── runner/
    └── MileageBatchRunner.java (삭제됨)

src/main/resources/
├── schema.sql (인덱스 추가)
└── mybatis/mapper/
    └── MileageDailySummaryMapper.xml (쿼리 최적화)
```

## 6. API 엔드포인트 요약

### 마일리지 API
| 버전 | 엔드포인트 | 설명 |
|------|-----------|------|
| Legacy | `/api/mileage/*` | 하위 호환성 유지 (deprecated) |
| v1 | `/api/v1/mileage/*` | 기존 기능 (deprecated) |
| v2 | `/api/v2/mileage/*` | 개선된 응답 형식 및 검증 (권장) |

### 배치 API
| 버전 | 엔드포인트 | 설명 |
|------|-----------|------|
| Legacy | `/api/batch/*` | 하위 호환성 유지 (deprecated) |
| v1 | `/api/v1/batch/*` | 기존 기능 (deprecated) |
| v2 | `/api/v2/batch/*` | 개선된 응답 형식 (권장) |

## 7. 권장 사항

1. **기존 클라이언트**: 당분간 기존 `/api/*` 엔드포인트 사용 가능
2. **신규 개발**: `/api/v2/*` 엔드포인트 사용 권장
3. **마이그레이션 계획**:
   - Phase 1: v2 API 테스트 및 검증
   - Phase 2: 클라이언트를 v2로 마이그레이션
   - Phase 3: v1 및 legacy API 제거 (향후 결정)

## 8. 테스트 필요 항목

1. BatchJobService가 정상적으로 Job을 찾아 실행하는지 확인
2. 데이터베이스 인덱스가 제대로 생성되는지 확인
3. 최적화된 쿼리가 인덱스를 사용하는지 실행 계획 확인
4. v2 API의 모든 엔드포인트 정상 작동 확인
5. 에러 케이스에 대한 적절한 응답 확인
