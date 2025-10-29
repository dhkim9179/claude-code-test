package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 일별 마일리지 집계 도메인 객체
 * 일 단위로 마일리지 적립 및 사용 금액을 집계
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MileageDailySummary {
    /**
     * 집계 일자 (Primary Key)
     */
    private LocalDate summaryDate;

    /**
     * 총 적립 금액
     */
    private Long totalEarnAmount;

    /**
     * 총 적립 건수
     */
    private Long totalEarnCount;

    /**
     * 총 사용 금액
     */
    private Long totalUseAmount;

    /**
     * 총 사용 건수
     */
    private Long totalUseCount;

    /**
     * 순 증감액 (적립 - 사용)
     */
    private Long netAmount;

    /**
     * 생성일시
     */
    private LocalDateTime createDate;

    /**
     * 수정일시
     */
    private LocalDateTime updateDate;
}
