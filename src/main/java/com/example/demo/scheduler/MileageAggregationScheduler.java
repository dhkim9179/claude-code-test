package com.example.demo.scheduler;

import com.example.demo.domain.MileageDailySummary;
import com.example.demo.service.MileageDailySummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 마일리지 집계 스케줄러
 * 정기적으로 마일리지 일별 집계를 수행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MileageAggregationScheduler {

    private final MileageDailySummaryService summaryService;

    /**
     * 일별 마일리지 집계 스케줄러
     * 매일 자정(00:00)에 전날 데이터를 집계
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void aggregateDailyMileage() {
        log.info("=".repeat(80));
        log.info("마일리지 일별 집계 스케줄러 시작");
        log.info("=".repeat(80));

        try {
            // 전날 데이터 집계
            LocalDate yesterday = LocalDate.now().minusDays(1);

            log.info("집계 대상 일자: {}", yesterday);
            MileageDailySummary summary = summaryService.aggregateAndSave(yesterday);

            if (summary != null) {
                log.info("마일리지 일별 집계 완료");
                log.info("  - 일자: {}", summary.getSummaryDate());
                log.info("  - 적립: {}건 / {}원", summary.getTotalEarnCount(), summary.getTotalEarnAmount());
                log.info("  - 사용: {}건 / {}원", summary.getTotalUseCount(), summary.getTotalUseAmount());
                log.info("  - 순증감: {}원", summary.getNetAmount());
            } else {
                log.warn("집계할 데이터가 없습니다 - 일자: {}", yesterday);
            }

        } catch (Exception e) {
            log.error("마일리지 일별 집계 중 오류 발생", e);
        }

        log.info("=".repeat(80));
        log.info("마일리지 일별 집계 스케줄러 종료");
        log.info("=".repeat(80));
    }

    /**
     * 1시간마다 당일 데이터 재집계 (실시간 집계용)
     * 매시간 정각에 실행
     */
    @Scheduled(cron = "0 0 * * * *")
    public void aggregateTodayMileage() {
        log.info("마일리지 당일 집계 스케줄러 시작");

        try {
            LocalDate today = LocalDate.now();

            MileageDailySummary summary = summaryService.aggregateAndSave(today);

            if (summary != null) {
                log.info("마일리지 당일 집계 완료 - 일자: {}, 적립: {}건/{}원, 사용: {}건/{}원",
                    today,
                    summary.getTotalEarnCount(),
                    summary.getTotalEarnAmount(),
                    summary.getTotalUseCount(),
                    summary.getTotalUseAmount()
                );
            } else {
                log.info("당일 집계할 데이터가 없습니다 - 일자: {}", today);
            }

        } catch (Exception e) {
            log.error("마일리지 당일 집계 중 오류 발생", e);
        }
    }

    /**
     * 매주 월요일 오전 2시에 지난주 데이터 재집계 (데이터 검증용)
     */
    @Scheduled(cron = "0 0 2 * * MON")
    public void aggregateLastWeek() {
        log.info("=".repeat(80));
        log.info("마일리지 주간 재집계 스케줄러 시작");
        log.info("=".repeat(80));

        try {
            LocalDate endDate = LocalDate.now().minusDays(1);
            LocalDate startDate = endDate.minusDays(6);

            log.info("재집계 기간: {} ~ {}", startDate, endDate);
            int aggregatedDays = summaryService.aggregateByDateRange(startDate, endDate);

            log.info("마일리지 주간 재집계 완료 - 집계 일수: {}일", aggregatedDays);

        } catch (Exception e) {
            log.error("마일리지 주간 재집계 중 오류 발생", e);
        }

        log.info("=".repeat(80));
        log.info("마일리지 주간 재집계 스케줄러 종료");
        log.info("=".repeat(80));
    }
}
