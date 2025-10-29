package com.example.demo.mapper;

import com.example.demo.domain.MileageDailySummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 일별 마일리지 집계 Mapper 인터페이스
 * MyBatis를 사용한 일별 마일리지 집계 데이터 접근 계층
 */
@Mapper
public interface MileageDailySummaryMapper {

    /**
     * 특정 일자의 집계 정보 조회
     *
     * @param summaryDate 집계 일자
     * @return 일별 마일리지 집계 정보
     */
    MileageDailySummary findBySummaryDate(@Param("summaryDate") LocalDate summaryDate);

    /**
     * 기간별 집계 정보 목록 조회
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 일별 마일리지 집계 목록
     */
    List<MileageDailySummary> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 일별 집계 정보 등록
     *
     * @param summary 일별 마일리지 집계 정보
     * @return 등록된 행 수
     */
    int insert(MileageDailySummary summary);

    /**
     * 일별 집계 정보 업데이트
     *
     * @param summary 일별 마일리지 집계 정보
     * @return 업데이트된 행 수
     */
    int update(MileageDailySummary summary);

    /**
     * 일별 집계 정보 삭제
     *
     * @param summaryDate 집계 일자
     * @return 삭제된 행 수
     */
    int deleteBySummaryDate(@Param("summaryDate") LocalDate summaryDate);

    /**
     * 마일리지 히스토리로부터 특정 일자의 집계 데이터 생성
     *
     * @param summaryDate 집계 일자
     * @return 집계된 데이터
     */
    MileageDailySummary aggregateByDate(@Param("summaryDate") LocalDate summaryDate);

    /**
     * UPSERT 방식으로 일별 집계 정보 저장 (존재하면 업데이트, 없으면 삽입)
     *
     * @param summary 일별 마일리지 집계 정보
     * @return 처리된 행 수
     */
    int upsert(MileageDailySummary summary);
}
