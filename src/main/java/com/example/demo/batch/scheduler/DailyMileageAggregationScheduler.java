package com.example.demo.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 일별 마일리지 집계 배치 스케줄러
 * 매일 오전 9시에 자동으로 배치를 실행합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyMileageAggregationScheduler {

    private final JobLauncher jobLauncher;
    private final Job dailyMileageAggregationJob;

    /**
     * 매일 오전 9시에 일별 마일리지 집계 배치를 실행합니다.
     * Cron 표현식: "0 0 9 * * *" (초 분 시 일 월 요일)
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void executeDailyMileageAggregation() {
        try {
            log.info("=== Scheduled Daily Mileage Aggregation Batch Triggered ===");
            log.info("Execution Time: {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            // 전일자를 집계 대상으로 설정
            LocalDate targetDate = LocalDate.now().minusDays(1);

            // JobParameters 생성 (타임스탬프로 유니크하게 유지)
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("targetDate", targetDate.toString())
                    .addString("executionType", "scheduled")
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            // 배치 Job 실행
            jobLauncher.run(dailyMileageAggregationJob, jobParameters);

            log.info("=== Scheduled Daily Mileage Aggregation Batch Completed Successfully ===");

        } catch (Exception e) {
            log.error("Failed to execute scheduled daily mileage aggregation batch", e);
            throw new RuntimeException("Scheduled batch execution failed", e);
        }
    }
}
