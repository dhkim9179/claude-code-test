# MyBatis Mapper 예제

이 문서는 마일리지 테이블을 기반으로 한 MyBatis Mapper 예제를 설명합니다.

## 📋 목차
- [데이터베이스 테이블 구조](#데이터베이스-테이블-구조)
- [프로젝트 구조](#프로젝트-구조)
- [주요 구성 요소](#주요-구성-요소)
- [사용 예제](#사용-예제)
- [설정](#설정)

## 데이터베이스 테이블 구조

### MILEAGE 테이블
회원의 마일리지 잔액 정보를 저장합니다.

```sql
CREATE TABLE MILEAGE (
    member_id BIGINT UNSIGNED PRIMARY KEY,
    balance BIGINT UNSIGNED NOT NULL,
    create_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date DATETIME
);
```

### MILEAGE_HISTORY 테이블
마일리지 적립/사용 이력을 저장합니다.

```sql
CREATE TABLE MILEAGE_HISTORY (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    MILEAGE_member_id BIGINT UNSIGNED NOT NULL,
    type VARCHAR(45) NOT NULL,
    amount INT NOT NULL,
    description VARCHAR(45) NOT NULL,
    create_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MILEAGE_member_id) REFERENCES MILEAGE(member_id)
);
```

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── domain/              # 도메인 객체
│   │   │   ├── Mileage.java
│   │   │   └── MileageHistory.java
│   │   ├── mapper/              # MyBatis Mapper 인터페이스
│   │   │   ├── MileageMapper.java
│   │   │   └── MileageHistoryMapper.java
│   │   └── service/             # 서비스 레이어
│   │       └── MileageService.java
│   └── resources/
│       ├── mybatis/mapper/      # MyBatis XML Mapper
│       │   ├── MileageMapper.xml
│       │   └── MileageHistoryMapper.xml
│       └── application.properties
```

## 주요 구성 요소

### 1. 도메인 객체

#### Mileage.java
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mileage {
    private Long memberId;
    private Long balance;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}
```

#### MileageHistory.java
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MileageHistory {
    private Long id;
    private Long mileageMemberId;
    private String type;           // EARN, USE, EXPIRE
    private Integer amount;
    private String description;
    private LocalDateTime createDate;
}
```

### 2. Mapper 인터페이스

#### MileageMapper.java
주요 메서드:
- `findByMemberId(Long memberId)` - 회원 ID로 마일리지 조회
- `insert(Mileage mileage)` - 마일리지 신규 등록
- `updateBalance(Long memberId, Long balance)` - 마일리지 잔액 업데이트
- `increaseBalance(Long memberId, Long amount)` - 마일리지 적립
- `decreaseBalance(Long memberId, Long amount)` - 마일리지 사용

#### MileageHistoryMapper.java
주요 메서드:
- `findById(Long id)` - ID로 이력 조회
- `findByMemberId(Long memberId)` - 회원의 전체 이력 조회
- `findByMemberIdAndType(Long memberId, String type)` - 유형별 이력 조회
- `findAllWithPaging(int offset, int limit)` - 페이징 이력 조회
- `insert(MileageHistory history)` - 이력 등록
- `countByMemberId(Long memberId)` - 회원의 이력 총 건수

### 3. XML Mapper

#### MileageMapper.xml 주요 쿼리

**마일리지 조회**
```xml
<select id="findByMemberId" resultMap="MileageResultMap">
    SELECT member_id, balance, create_date, update_date
    FROM MILEAGE
    WHERE member_id = #{memberId}
</select>
```

**마일리지 잔액 증가 (적립)**
```xml
<update id="increaseBalance">
    UPDATE MILEAGE
    SET balance = balance + #{amount},
        update_date = CURRENT_TIMESTAMP
    WHERE member_id = #{memberId}
</update>
```

**마일리지 잔액 감소 (사용)**
```xml
<update id="decreaseBalance">
    UPDATE MILEAGE
    SET balance = balance - #{amount},
        update_date = CURRENT_TIMESTAMP
    WHERE member_id = #{memberId}
      AND balance >= #{amount}
</update>
```

#### MileageHistoryMapper.xml 주요 쿼리

**이력 조회 (페이징)**
```xml
<select id="findAllWithPaging" resultMap="MileageHistoryResultMap">
    SELECT id, MILEAGE_member_id, type, amount, description, create_date
    FROM MILEAGE_HISTORY
    ORDER BY create_date DESC
    OFFSET #{offset} ROWS
    FETCH NEXT #{limit} ROWS ONLY
</select>
```

**이력 등록 (Oracle Sequence 사용)**
```xml
<insert id="insert">
    <selectKey keyProperty="id" resultType="long" order="BEFORE">
        SELECT MILEAGE_HISTORY_SEQ.NEXTVAL FROM DUAL
    </selectKey>
    INSERT INTO MILEAGE_HISTORY (...)
    VALUES (...)
</insert>
```

### 4. 서비스 레이어

`MileageService.java`는 MyBatis Mapper를 활용한 비즈니스 로직을 구현합니다.

주요 메서드:
- `getMileage(Long memberId)` - 마일리지 조회
- `earnMileage(Long memberId, Integer amount, String description)` - 마일리지 적립
- `useMileage(Long memberId, Integer amount, String description)` - 마일리지 사용
- `createMileage(Long memberId)` - 신규 회원 마일리지 생성
- `getMileageHistory(Long memberId)` - 마일리지 이력 조회

## 사용 예제

### 1. 신규 회원 마일리지 생성
```java
@Autowired
private MileageService mileageService;

// 회원 ID 1000번으로 마일리지 계정 생성
mileageService.createMileage(1000L);
```

### 2. 마일리지 적립
```java
// 회원 1000번에게 1000 마일리지 적립
mileageService.earnMileage(1000L, 1000, "구매 적립");
```

### 3. 마일리지 사용
```java
// 회원 1000번이 500 마일리지 사용
mileageService.useMileage(1000L, 500, "상품 구매");
```

### 4. 마일리지 조회
```java
Mileage mileage = mileageService.getMileage(1000L);
System.out.println("현재 잔액: " + mileage.getBalance());
```

### 5. 마일리지 이력 조회
```java
List<MileageHistory> histories = mileageService.getMileageHistory(1000L);
for (MileageHistory history : histories) {
    System.out.println(
        "유형: " + history.getType() +
        ", 금액: " + history.getAmount() +
        ", 사유: " + history.getDescription()
    );
}
```

### 6. 유형별 이력 조회
```java
// 적립 이력만 조회
List<MileageHistory> earnHistories =
    mileageService.getMileageHistoryByType(1000L, "EARN");

// 사용 이력만 조회
List<MileageHistory> useHistories =
    mileageService.getMileageHistoryByType(1000L, "USE");
```

### 7. Mapper 직접 사용
```java
@Autowired
private MileageMapper mileageMapper;

@Autowired
private MileageHistoryMapper mileageHistoryMapper;

// 마일리지 조회
Mileage mileage = mileageMapper.findByMemberId(1000L);

// 마일리지 잔액 업데이트
mileageMapper.updateBalance(1000L, 5000L);

// 이력 조회 (페이징)
List<MileageHistory> histories =
    mileageHistoryMapper.findAllWithPaging(0, 10);

// 이력 건수 조회
int count = mileageHistoryMapper.countByMemberId(1000L);
```

## 설정

### application.properties
```properties
# MyBatis Configuration
mybatis.mapper-locations=classpath:mybatis/mapper/**/*.xml
mybatis.type-aliases-package=com.example.demo.domain
mybatis.configuration.map-underscore-to-camel-case=true
mybatis.configuration.log-impl=org.apache.ibatis.logging.slf4j.Slf4jImpl
```

### 설정 설명
- **mapper-locations**: XML Mapper 파일 위치 지정
- **type-aliases-package**: 도메인 객체 패키지 별칭 설정
- **map-underscore-to-camel-case**: DB의 snake_case를 Java의 camelCase로 자동 변환
- **log-impl**: SQL 로그 출력 설정

## MyBatis 주요 기능 활용

### 1. ResultMap
컬럼명과 필드명이 다를 때 매핑 규칙을 정의합니다.
```xml
<resultMap id="MileageResultMap" type="com.example.demo.domain.Mileage">
    <id property="memberId" column="member_id"/>
    <result property="balance" column="balance"/>
    <result property="createDate" column="create_date"/>
    <result property="updateDate" column="update_date"/>
</resultMap>
```

### 2. @Param 어노테이션
여러 개의 파라미터를 전달할 때 사용합니다.
```java
int updateBalance(@Param("memberId") Long memberId, @Param("balance") Long balance);
```

### 3. 동적 SQL (필요시 활용 가능)
조건에 따라 동적으로 SQL을 생성할 수 있습니다.
```xml
<select id="findByConditions" resultMap="MileageHistoryResultMap">
    SELECT * FROM MILEAGE_HISTORY
    WHERE 1=1
    <if test="memberId != null">
        AND MILEAGE_member_id = #{memberId}
    </if>
    <if test="type != null">
        AND type = #{type}
    </if>
</select>
```

### 4. Oracle Sequence 활용
Auto Increment ID를 Oracle Sequence로 구현합니다.
```xml
<selectKey keyProperty="id" resultType="long" order="BEFORE">
    SELECT MILEAGE_HISTORY_SEQ.NEXTVAL FROM DUAL
</selectKey>
```

## 참고사항

1. **트랜잭션 관리**: 서비스 레이어에서 `@Transactional` 어노테이션을 사용하여 트랜잭션을 관리합니다.

2. **로깅**: Lombok의 `@Slf4j`를 활용하여 로깅을 구현합니다.

3. **예외 처리**: 잔액 부족, 회원 미존재 등의 예외 상황을 적절히 처리합니다.

4. **Oracle vs H2**:
   - 현재 프로젝트는 H2 데이터베이스를 사용합니다.
   - Oracle을 사용하려면 `application.properties`의 데이터소스 설정을 변경하세요.

5. **데이터베이스 초기화**:
   - 테이블 생성 스크립트를 `src/main/resources/schema.sql`에 작성하면 자동 실행됩니다.
   - Sequence 생성도 필요합니다: `CREATE SEQUENCE MILEAGE_HISTORY_SEQ START WITH 1;`

## 추가 개발 가능 항목

- 마일리지 만료 배치 Job 추가
- 마일리지 통계 조회 기능
- 마일리지 선물하기 기능
- 마일리지 환불 기능
- 페이징 처리 개선 (PageHelper 활용)
