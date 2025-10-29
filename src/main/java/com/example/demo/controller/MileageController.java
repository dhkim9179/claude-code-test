package com.example.demo.controller;

import com.example.demo.domain.Mileage;
import com.example.demo.domain.MileageDailySummary;
import com.example.demo.domain.MileageHistory;
import com.example.demo.service.MileageDailySummaryService;
import com.example.demo.service.MileageService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 마일리지 REST API 컨트롤러
 * 마일리지 조회 및 일별 집계 조회를 위한 API 제공
 */
@RestController
@RequestMapping("/api/mileage")
@RequiredArgsConstructor
public class MileageController {

    private final MileageService mileageService;
    private final MileageDailySummaryService summaryService;

    /**
     * 회원 마일리지 조회
     * GET /api/mileage/{memberId}
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<Mileage> getMileage(@PathVariable Long memberId) {
        Mileage mileage = mileageService.getMileage(memberId);
        if (mileage == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mileage);
    }

    /**
     * 회원 마일리지 이력 조회
     * GET /api/mileage/{memberId}/history
     */
    @GetMapping("/{memberId}/history")
    public ResponseEntity<List<MileageHistory>> getMileageHistory(@PathVariable Long memberId) {
        List<MileageHistory> histories = mileageService.getMileageHistory(memberId);
        return ResponseEntity.ok(histories);
    }

    /**
     * 특정 일자의 마일리지 집계 조회
     * GET /api/mileage/summary?date=2024-01-01
     */
    @GetMapping("/summary")
    public ResponseEntity<MileageDailySummary> getDailySummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        MileageDailySummary summary = summaryService.getDailySummary(date);
        if (summary == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(summary);
    }

    /**
     * 기간별 마일리지 집계 조회
     * GET /api/mileage/summary/range?startDate=2024-01-01&endDate=2024-01-31
     */
    @GetMapping("/summary/range")
    public ResponseEntity<List<MileageDailySummary>> getDailySummariesByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<MileageDailySummary> summaries = summaryService.getDailySummariesByDateRange(startDate, endDate);
        return ResponseEntity.ok(summaries);
    }

    /**
     * 최근 N일간의 마일리지 집계 조회
     * GET /api/mileage/summary/recent?days=7
     */
    @GetMapping("/summary/recent")
    public ResponseEntity<List<MileageDailySummary>> getRecentSummaries(
            @RequestParam(defaultValue = "7") int days) {
        List<MileageDailySummary> summaries = summaryService.getRecentSummaries(days);
        return ResponseEntity.ok(summaries);
    }

    /**
     * 기간별 전체 통계 조회
     * GET /api/mileage/statistics?startDate=2024-01-01&endDate=2024-01-31
     */
    @GetMapping("/statistics")
    public ResponseEntity<MileageDailySummary> getTotalStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        MileageDailySummary statistics = summaryService.getTotalStatistics(startDate, endDate);
        if (statistics == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(statistics);
    }

    /**
     * 특정 일자의 집계 재생성
     * POST /api/mileage/summary/aggregate?date=2024-01-01
     */
    @PostMapping("/summary/aggregate")
    public ResponseEntity<MileageDailySummary> aggregateDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        MileageDailySummary summary = summaryService.aggregateAndSave(date);
        if (summary == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(summary);
    }
}
