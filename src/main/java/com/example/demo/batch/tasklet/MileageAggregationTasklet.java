package com.example.demo.batch.tasklet;

import com.example.demo.domain.MileageDailySummary;
import com.example.demo.mapper.MileageDailySummaryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 마일리지 일별 집계 Tasklet
 * 마일리지 히스토리로부터 일별 적립/사용 금액을 집계
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MileageAggregationTasklet implements Tasklet {

    private final MileageDailySummaryMapper summaryMapper;

    private static final int DAYS_IN_MONTH = 30;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("마일리지 일별 집계 시작");
        long startTime = System.currentTimeMillis();

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(DAYS_IN_MONTH - 1);

        int aggregatedDays = 0;
        long totalTransactions = 0;

        // 일별로 집계
        for (int day = 0; day < DAYS_IN_MONTH; day++) {
            LocalDate targetDate = startDate.plusDays(day);

            // 해당 일자의 집계 데이터 생성
            MileageDailySummary summary = summaryMapper.aggregateByDate(targetDate);

            if (summary != null && summary.getSummaryDate() != null) {
                // UPSERT 방식으로 저장 (존재하면 업데이트, 없으면 삽입)
                summaryMapper.upsert(summary);

                aggregatedDays++;
                long dayTotal = summary.getTotalEarnCount() + summary.getTotalUseCount();
                totalTransactions += dayTotal;

                log.info("일별 집계 완료: {} - 적립: {}건/{}원, 사용: {}건/{}원",
                    targetDate,
                    summary.getTotalEarnCount(),
                    summary.getTotalEarnAmount(),
                    summary.getTotalUseCount(),
                    summary.getTotalUseAmount()
                );
            } else {
                log.warn("집계 데이터 없음: {}", targetDate);
            }
        }

        long endTime = System.currentTimeMillis();
        log.info("마일리지 일별 집계 완료 - 집계 일수: {}일, 총 거래 건수: {}건, 소요 시간: {}ms",
            aggregatedDays, totalTransactions, (endTime - startTime));

        return RepeatStatus.FINISHED;
    }
}
