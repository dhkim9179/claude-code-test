package com.example.demo.batch.tasklet;

import com.example.demo.mapper.MileageHistoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 일별 마일리지 적립/사용 집계 Tasklet
 * 전일자 마일리지 적립/사용 내역을 집계하여 리포트를 생성합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyMileageAggregationTasklet implements Tasklet {

    private final MileageHistoryMapper mileageHistoryMapper;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("=== Daily Mileage Aggregation Batch Started ===");

        // Job Parameters에서 targetDate 가져오기 (없으면 전일자 사용)
        String targetDateStr = chunkContext.getStepContext()
                .getJobParameters()
                .getOrDefault("targetDate", LocalDate.now().minusDays(1).toString())
                .toString();

        LocalDate targetDate = LocalDate.parse(targetDateStr);
        log.info("Target Date for Aggregation: {}", targetDate.format(DateTimeFormatter.ISO_DATE));

        // TODO: 실제 집계 로직 구현
        // 1. 전일자 마일리지 적립 내역 조회 (EARN)
        // 2. 전일자 마일리지 사용 내역 조회 (USE)
        // 3. 전일자 마일리지 만료 내역 조회 (EXPIRE)
        // 4. 집계 결과를 별도 테이블에 저장 또는 리포트 생성

        // 현재는 로그만 출력
        log.info("Aggregating mileage data for date: {}", targetDate);
        log.info("TODO: Implement actual aggregation logic");

        // 집계 결과 샘플 로그
        log.info("Summary:");
        log.info("  - Total EARN: TBD");
        log.info("  - Total USE: TBD");
        log.info("  - Total EXPIRE: TBD");

        log.info("=== Daily Mileage Aggregation Batch Completed ===");

        return RepeatStatus.FINISHED;
    }
}
