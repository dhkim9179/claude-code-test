package com.example.demo.controller.v2;

import com.example.demo.domain.Mileage;
import com.example.demo.domain.MileageDailySummary;
import com.example.demo.domain.MileageHistory;
import com.example.demo.dto.v2.ApiResponse;
import com.example.demo.service.MileageDailySummaryService;
import com.example.demo.service.MileageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 마일리지 REST API 컨트롤러 v2
 * 개선사항:
 * - 표준화된 응답 형식 (ApiResponse 래퍼)
 * - 더 나은 에러 처리
 * - 메타 정보 포함 (타임스탬프, 버전 정보)
 * - 페이징 지원 (향후 확장 가능)
 */
@Slf4j
@RestController
@RequestMapping("/api/v2/mileage")
@RequiredArgsConstructor
public class MileageControllerV2 {

    private final MileageService mileageService;
    private final MileageDailySummaryService summaryService;

    /**
     * 회원 마일리지 조회
     * GET /api/v2/mileage/{memberId}
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Mileage>> getMileage(@PathVariable Long memberId) {
        log.debug("Fetching mileage for member: {}", memberId);

        Mileage mileage = mileageService.getMileage(memberId);
        if (mileage == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("MILEAGE_NOT_FOUND", "해당 회원의 마일리지 정보를 찾을 수 없습니다."));
        }

        return ResponseEntity.ok(ApiResponse.success(mileage));
    }

    /**
     * 회원 마일리지 이력 조회
     * GET /api/v2/mileage/{memberId}/history
     */
    @GetMapping("/{memberId}/history")
    public ResponseEntity<ApiResponse<List<MileageHistory>>> getMileageHistory(
            @PathVariable Long memberId,
            @RequestParam(required = false) String type) {
        log.debug("Fetching mileage history for member: {}, type: {}", memberId, type);

        List<MileageHistory> histories;
        if (type != null) {
            histories = mileageService.getMileageHistoryByType(memberId, type);
        } else {
            histories = mileageService.getMileageHistory(memberId);
        }

        ApiResponse.Meta meta = ApiResponse.Meta.builder()
                .version("v2")
                .build();

        return ResponseEntity.ok(ApiResponse.success(histories, meta));
    }

    /**
     * 특정 일자의 마일리지 집계 조회
     * GET /api/v2/mileage/summary?date=2024-01-01
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<MileageDailySummary>> getDailySummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.debug("Fetching daily summary for date: {}", date);

        MileageDailySummary summary = summaryService.getDailySummary(date);
        if (summary == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("SUMMARY_NOT_FOUND", "해당 일자의 집계 정보를 찾을 수 없습니다."));
        }

        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * 기간별 마일리지 집계 조회
     * GET /api/v2/mileage/summary/range?startDate=2024-01-01&endDate=2024-01-31
     */
    @GetMapping("/summary/range")
    public ResponseEntity<ApiResponse<List<MileageDailySummary>>> getDailySummariesByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("Fetching summaries for range: {} to {}", startDate, endDate);

        // 날짜 범위 검증
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_DATE_RANGE", "시작일이 종료일보다 늦을 수 없습니다."));
        }

        List<MileageDailySummary> summaries = summaryService.getDailySummariesByDateRange(startDate, endDate);

        ApiResponse.Meta meta = ApiResponse.Meta.builder()
                .version("v2")
                .build();

        return ResponseEntity.ok(ApiResponse.success(summaries, meta));
    }

    /**
     * 최근 N일간의 마일리지 집계 조회
     * GET /api/v2/mileage/summary/recent?days=7
     */
    @GetMapping("/summary/recent")
    public ResponseEntity<ApiResponse<List<MileageDailySummary>>> getRecentSummaries(
            @RequestParam(defaultValue = "7") int days) {
        log.debug("Fetching recent summaries for {} days", days);

        if (days < 1 || days > 365) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_DAYS", "조회 일수는 1일 이상 365일 이하여야 합니다."));
        }

        List<MileageDailySummary> summaries = summaryService.getRecentSummaries(days);
        return ResponseEntity.ok(ApiResponse.success(summaries));
    }

    /**
     * 기간별 전체 통계 조회
     * GET /api/v2/mileage/statistics?startDate=2024-01-01&endDate=2024-01-31
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<MileageDailySummary>> getTotalStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("Fetching total statistics for range: {} to {}", startDate, endDate);

        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_DATE_RANGE", "시작일이 종료일보다 늦을 수 없습니다."));
        }

        MileageDailySummary statistics = summaryService.getTotalStatistics(startDate, endDate);
        if (statistics == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("STATISTICS_NOT_FOUND", "해당 기간의 통계 정보를 찾을 수 없습니다."));
        }

        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    /**
     * 특정 일자의 집계 재생성
     * POST /api/v2/mileage/summary/aggregate?date=2024-01-01
     */
    @PostMapping("/summary/aggregate")
    public ResponseEntity<ApiResponse<MileageDailySummary>> aggregateDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Aggregating data for date: {}", date);

        // 미래 날짜 검증
        if (date.isAfter(LocalDate.now())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_DATE", "미래 날짜의 집계는 생성할 수 없습니다."));
        }

        MileageDailySummary summary = summaryService.aggregateAndSave(date);
        if (summary == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("AGGREGATION_FAILED", "집계 생성에 실패했습니다."));
        }

        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
