package com.example.demo.service;

import com.example.demo.domain.MileageDailySummary;
import com.example.demo.mapper.MileageDailySummaryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 마일리지 일별 집계 서비스
 * 일 단위 마일리지 적립 및 사용 금액 집계 비즈니스 로직 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MileageDailySummaryService {

    private final MileageDailySummaryMapper summaryMapper;

    /**
     * 특정 일자의 집계 정보 조회
     *
     * @param summaryDate 집계 일자
     * @return 일별 마일리지 집계 정보
     */
    public MileageDailySummary getDailySummary(LocalDate summaryDate) {
        log.info("일별 집계 조회 - 일자: {}", summaryDate);
        MileageDailySummary summary = summaryMapper.findBySummaryDate(summaryDate);
        log.info("일별 집계 조회 완료: {}", summary);
        return summary;
    }

    /**
     * 기간별 집계 정보 목록 조회
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 일별 마일리지 집계 목록
     */
    public List<MileageDailySummary> getDailySummariesByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("기간별 집계 조회 - 시작일: {}, 종료일: {}", startDate, endDate);
        List<MileageDailySummary> summaries = summaryMapper.findByDateRange(startDate, endDate);
        log.info("기간별 집계 조회 완료 - 건수: {}", summaries.size());
        return summaries;
    }

    /**
     * 특정 일자의 마일리지 집계 생성
     * 마일리지 히스토리로부터 집계 데이터를 생성하여 저장
     *
     * @param summaryDate 집계 일자
     * @return 생성된 집계 정보
     */
    @Transactional
    public MileageDailySummary aggregateAndSave(LocalDate summaryDate) {
        log.info("마일리지 집계 시작 - 일자: {}", summaryDate);

        // 마일리지 히스토리로부터 집계
        MileageDailySummary summary = summaryMapper.aggregateByDate(summaryDate);

        if (summary == null || summary.getSummaryDate() == null) {
            log.warn("집계할 데이터가 없습니다 - 일자: {}", summaryDate);
            return null;
        }

        // UPSERT 방식으로 저장
        summaryMapper.upsert(summary);

        log.info("마일리지 집계 완료 - 일자: {}, 적립: {}건/{}원, 사용: {}건/{}원",
            summaryDate,
            summary.getTotalEarnCount(),
            summary.getTotalEarnAmount(),
            summary.getTotalUseCount(),
            summary.getTotalUseAmount()
        );

        return summary;
    }

    /**
     * 기간별 마일리지 집계 일괄 생성
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 집계 완료 일수
     */
    @Transactional
    public int aggregateByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("기간별 마일리지 집계 시작 - 시작일: {}, 종료일: {}", startDate, endDate);

        int aggregatedDays = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            MileageDailySummary summary = aggregateAndSave(currentDate);
            if (summary != null) {
                aggregatedDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        log.info("기간별 마일리지 집계 완료 - 집계 일수: {}일", aggregatedDays);
        return aggregatedDays;
    }

    /**
     * 일별 집계 정보 직접 저장
     *
     * @param summary 일별 마일리지 집계 정보
     */
    @Transactional
    public void saveDailySummary(MileageDailySummary summary) {
        log.info("일별 집계 저장 시작: {}", summary.getSummaryDate());

        if (summary.getCreateDate() == null) {
            summary.setCreateDate(LocalDateTime.now());
        }
        if (summary.getUpdateDate() == null) {
            summary.setUpdateDate(LocalDateTime.now());
        }

        summaryMapper.insert(summary);
        log.info("일별 집계 저장 완료: {}", summary.getSummaryDate());
    }

    /**
     * 일별 집계 정보 업데이트
     *
     * @param summary 일별 마일리지 집계 정보
     */
    @Transactional
    public void updateDailySummary(MileageDailySummary summary) {
        log.info("일별 집계 업데이트 시작: {}", summary.getSummaryDate());

        summary.setUpdateDate(LocalDateTime.now());
        summaryMapper.update(summary);

        log.info("일별 집계 업데이트 완료: {}", summary.getSummaryDate());
    }

    /**
     * 일별 집계 정보 삭제
     *
     * @param summaryDate 집계 일자
     */
    @Transactional
    public void deleteDailySummary(LocalDate summaryDate) {
        log.info("일별 집계 삭제 시작: {}", summaryDate);
        summaryMapper.deleteBySummaryDate(summaryDate);
        log.info("일별 집계 삭제 완료: {}", summaryDate);
    }

    /**
     * 최근 N일간의 집계 정보 조회
     *
     * @param days 최근 일수
     * @return 일별 마일리지 집계 목록
     */
    public List<MileageDailySummary> getRecentSummaries(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        log.info("최근 {}일 집계 조회 - 시작일: {}, 종료일: {}", days, startDate, endDate);
        return getDailySummariesByDateRange(startDate, endDate);
    }

    /**
     * 전체 통계 조회 (기간 내 합계)
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 전체 통계 정보
     */
    public MileageDailySummary getTotalStatistics(LocalDate startDate, LocalDate endDate) {
        log.info("전체 통계 조회 - 시작일: {}, 종료일: {}", startDate, endDate);

        List<MileageDailySummary> summaries = summaryMapper.findByDateRange(startDate, endDate);

        if (summaries.isEmpty()) {
            log.warn("전체 통계 조회 실패 - 데이터 없음");
            return null;
        }

        // 기간 내 합계 계산
        long totalEarnAmount = summaries.stream().mapToLong(MileageDailySummary::getTotalEarnAmount).sum();
        long totalEarnCount = summaries.stream().mapToLong(MileageDailySummary::getTotalEarnCount).sum();
        long totalUseAmount = summaries.stream().mapToLong(MileageDailySummary::getTotalUseAmount).sum();
        long totalUseCount = summaries.stream().mapToLong(MileageDailySummary::getTotalUseCount).sum();
        long netAmount = summaries.stream().mapToLong(MileageDailySummary::getNetAmount).sum();

        MileageDailySummary totalStats = MileageDailySummary.builder()
            .summaryDate(startDate) // 대표 일자
            .totalEarnAmount(totalEarnAmount)
            .totalEarnCount(totalEarnCount)
            .totalUseAmount(totalUseAmount)
            .totalUseCount(totalUseCount)
            .netAmount(netAmount)
            .build();

        log.info("전체 통계 조회 완료 - 적립: {}건/{}원, 사용: {}건/{}원",
            totalEarnCount, totalEarnAmount, totalUseCount, totalUseAmount);

        return totalStats;
    }
}
