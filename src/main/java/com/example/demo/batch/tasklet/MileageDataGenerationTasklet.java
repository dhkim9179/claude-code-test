package com.example.demo.batch.tasklet;

import com.example.demo.domain.Mileage;
import com.example.demo.domain.MileageHistory;
import com.example.demo.mapper.MileageHistoryMapper;
import com.example.demo.mapper.MileageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 마일리지 테스트 데이터 생성 Tasklet
 * 100,000명의 회원과 한 달간의 마일리지 히스토리 데이터를 생성
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MileageDataGenerationTasklet implements Tasklet {

    private final MileageMapper mileageMapper;
    private final MileageHistoryMapper mileageHistoryMapper;

    // 생성할 회원 수
    private static final int TOTAL_MEMBERS = 100_000;
    // 최대 마일리지 잔액
    private static final int MAX_BALANCE = 100_000;
    // 한 달 기간 (일)
    private static final int DAYS_IN_MONTH = 30;
    // 일 최소 거래 건수
    private static final int MIN_DAILY_TRANSACTIONS = 400_000;
    // 일 최대 거래 건수
    private static final int MAX_DAILY_TRANSACTIONS = 1_000_000;
    // 배치 크기 (한번에 insert할 데이터 수)
    private static final int BATCH_SIZE = 1000;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("마일리지 데이터 생성 시작");
        long startTime = System.currentTimeMillis();

        // 1. 회원 및 마일리지 잔액 생성
        log.info("1단계: {}명의 회원 마일리지 잔액 생성 시작", TOTAL_MEMBERS);
        Map<Long, Long> memberBalances = generateMemberBalances();
        log.info("회원 마일리지 잔액 생성 완료");

        // 2. 마일리지 히스토리 생성 (잔액과 일치하도록)
        log.info("2단계: 마일리지 히스토리 생성 시작");
        generateMileageHistories(memberBalances);
        log.info("마일리지 히스토리 생성 완료");

        long endTime = System.currentTimeMillis();
        log.info("마일리지 데이터 생성 완료 (소요 시간: {}ms)", (endTime - startTime));

        return RepeatStatus.FINISHED;
    }

    /**
     * 회원별 마일리지 잔액 생성 및 DB 저장
     * 0 ~ MAX_BALANCE 사이의 값을 골고루 분포
     */
    private Map<Long, Long> generateMemberBalances() {
        Map<Long, Long> memberBalances = new HashMap<>();
        List<Mileage> mileageBatch = new ArrayList<>(BATCH_SIZE);
        LocalDateTime now = LocalDateTime.now();

        for (long memberId = 1; memberId <= TOTAL_MEMBERS; memberId++) {
            // 0 ~ MAX_BALANCE 사이의 값을 골고루 분포시키기 위해 memberId를 이용
            long balance = (memberId * MAX_BALANCE / TOTAL_MEMBERS) % (MAX_BALANCE + 1);
            memberBalances.put(memberId, balance);

            Mileage mileage = new Mileage(memberId, balance, now, now);
            mileageBatch.add(mileage);

            // BATCH_SIZE마다 DB에 저장
            if (mileageBatch.size() >= BATCH_SIZE) {
                mileageMapper.batchInsert(mileageBatch);
                mileageBatch.clear();

                if (memberId % 10_000 == 0) {
                    log.info("회원 생성 진행 중: {}/{}", memberId, TOTAL_MEMBERS);
                }
            }
        }

        // 남은 데이터 저장
        if (!mileageBatch.isEmpty()) {
            mileageMapper.batchInsert(mileageBatch);
        }

        return memberBalances;
    }

    /**
     * 마일리지 히스토리 생성
     * 각 회원의 현재 잔액과 일치하도록 히스토리를 생성
     */
    private void generateMileageHistories(Map<Long, Long> memberBalances) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(DAYS_IN_MONTH - 1);

        // 일별로 처리
        for (int day = 0; day < DAYS_IN_MONTH; day++) {
            LocalDate currentDate = startDate.plusDays(day);
            log.info("마일리지 히스토리 생성 중: {} ({}/{}일)", currentDate, day + 1, DAYS_IN_MONTH);

            // 해당 일자의 거래 건수 결정 (랜덤)
            int dailyTransactions = ThreadLocalRandom.current().nextInt(MIN_DAILY_TRANSACTIONS, MAX_DAILY_TRANSACTIONS + 1);

            generateDailyHistories(currentDate, dailyTransactions, memberBalances, day == DAYS_IN_MONTH - 1);
        }
    }

    /**
     * 특정 일자의 마일리지 히스토리 생성
     */
    private void generateDailyHistories(LocalDate date, int transactionCount, Map<Long, Long> memberBalances, boolean isLastDay) {
        List<MileageHistory> historyBatch = new ArrayList<>(BATCH_SIZE);
        Random random = new Random();

        // 각 회원별로 이번 날짜까지 누적해야 할 금액 추적
        Map<Long, Long> memberAccumulated = new HashMap<>();

        for (int i = 0; i < transactionCount; i++) {
            // 랜덤하게 회원 선택
            long memberId = ThreadLocalRandom.current().nextLong(1, TOTAL_MEMBERS + 1);

            // 해당 회원의 목표 잔액
            long targetBalance = memberBalances.get(memberId);

            // 해당 회원의 현재까지 누적 금액
            long accumulated = memberAccumulated.getOrDefault(memberId, 0L);

            // EARN (적립) 또는 USE (사용) 결정
            String type;
            int amount;

            if (isLastDay && accumulated < targetBalance) {
                // 마지막 날이고 아직 목표에 도달하지 못한 경우, 부족한 만큼 적립
                type = "EARN";
                amount = (int) Math.min(targetBalance - accumulated, 10000);
            } else if (accumulated >= targetBalance) {
                // 이미 목표 금액에 도달했다면 사용만 가능
                type = "USE";
                amount = -(ThreadLocalRandom.current().nextInt(100, 5001));
            } else {
                // 일반적인 경우: 70% 적립, 30% 사용
                if (random.nextDouble() < 0.7) {
                    type = "EARN";
                    amount = ThreadLocalRandom.current().nextInt(100, 10001);
                } else {
                    type = "USE";
                    amount = -(ThreadLocalRandom.current().nextInt(100, 5001));
                }
            }

            // 누적 금액 업데이트
            memberAccumulated.put(memberId, accumulated + amount);

            // 랜덤한 시간 생성 (해당 날짜의 00:00:00 ~ 23:59:59)
            LocalDateTime createDate = LocalDateTime.of(
                date,
                LocalTime.of(
                    ThreadLocalRandom.current().nextInt(0, 24),
                    ThreadLocalRandom.current().nextInt(0, 60),
                    ThreadLocalRandom.current().nextInt(0, 60)
                )
            );

            MileageHistory history = new MileageHistory(
                null,
                memberId,
                type,
                amount,
                type.equals("EARN") ? "마일리지 적립" : "마일리지 사용",
                createDate
            );

            historyBatch.add(history);

            // BATCH_SIZE마다 DB에 저장
            if (historyBatch.size() >= BATCH_SIZE) {
                mileageHistoryMapper.batchInsert(historyBatch);
                historyBatch.clear();
            }
        }

        // 남은 데이터 저장
        if (!historyBatch.isEmpty()) {
            mileageHistoryMapper.batchInsert(historyBatch);
        }

        // 마지막 날이면 각 회원의 목표 잔액과 실제 누적 금액 맞추기
        if (isLastDay) {
            adjustFinalBalances(memberAccumulated, memberBalances);
        }
    }

    /**
     * 마지막 날 각 회원의 잔액을 목표 금액에 맞춤
     */
    private void adjustFinalBalances(Map<Long, Long> memberAccumulated, Map<Long, Long> memberBalances) {
        log.info("최종 잔액 조정 시작");
        List<MileageHistory> adjustmentBatch = new ArrayList<>(BATCH_SIZE);
        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<Long, Long> entry : memberAccumulated.entrySet()) {
            long memberId = entry.getKey();
            long accumulated = entry.getValue();
            long targetBalance = memberBalances.get(memberId);
            long difference = targetBalance - accumulated;

            if (difference != 0) {
                String type = difference > 0 ? "EARN" : "USE";
                int amount = (int) Math.abs(difference);

                MileageHistory adjustment = new MileageHistory(
                    null,
                    memberId,
                    type,
                    type.equals("EARN") ? amount : -amount,
                    "잔액 조정",
                    now
                );

                adjustmentBatch.add(adjustment);

                if (adjustmentBatch.size() >= BATCH_SIZE) {
                    mileageHistoryMapper.batchInsert(adjustmentBatch);
                    adjustmentBatch.clear();
                }
            }
        }

        // 한 번도 거래가 없었던 회원들의 잔액 조정
        for (Map.Entry<Long, Long> entry : memberBalances.entrySet()) {
            long memberId = entry.getKey();
            if (!memberAccumulated.containsKey(memberId)) {
                long targetBalance = entry.getValue();

                if (targetBalance > 0) {
                    MileageHistory adjustment = new MileageHistory(
                        null,
                        memberId,
                        "EARN",
                        (int) targetBalance,
                        "초기 적립",
                        now
                    );

                    adjustmentBatch.add(adjustment);

                    if (adjustmentBatch.size() >= BATCH_SIZE) {
                        mileageHistoryMapper.batchInsert(adjustmentBatch);
                        adjustmentBatch.clear();
                    }
                }
            }
        }

        // 남은 데이터 저장
        if (!adjustmentBatch.isEmpty()) {
            mileageHistoryMapper.batchInsert(adjustmentBatch);
        }

        log.info("최종 잔액 조정 완료");
    }
}
