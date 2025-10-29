package com.example.demo.mapper;

import com.example.demo.domain.MileageHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 마일리지 이력 Mapper 인터페이스
 * MyBatis를 사용한 마일리지 이력 데이터 접근 계층
 */
@Mapper
public interface MileageHistoryMapper {

    /**
     * 마일리지 이력 ID로 조회
     *
     * @param id 이력 ID
     * @return 마일리지 이력 정보
     */
    MileageHistory findById(@Param("id") Long id);

    /**
     * 회원 ID로 마일리지 이력 목록 조회
     *
     * @param memberId 회원 ID
     * @return 마일리지 이력 목록
     */
    List<MileageHistory> findByMemberId(@Param("memberId") Long memberId);

    /**
     * 회원 ID와 유형으로 마일리지 이력 목록 조회
     *
     * @param memberId 회원 ID
     * @param type 마일리지 유형
     * @return 마일리지 이력 목록
     */
    List<MileageHistory> findByMemberIdAndType(@Param("memberId") Long memberId, @Param("type") String type);

    /**
     * 마일리지 이력 전체 조회 (페이징)
     *
     * @param offset 시작 위치
     * @param limit 조회 개수
     * @return 마일리지 이력 목록
     */
    List<MileageHistory> findAllWithPaging(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 마일리지 이력 등록
     *
     * @param history 마일리지 이력 정보
     * @return 등록된 행 수
     */
    int insert(MileageHistory history);

    /**
     * 마일리지 이력 삭제
     *
     * @param id 이력 ID
     * @return 삭제된 행 수
     */
    int deleteById(@Param("id") Long id);

    /**
     * 회원의 전체 마일리지 이력 수 조회
     *
     * @param memberId 회원 ID
     * @return 이력 개수
     */
    int countByMemberId(@Param("memberId") Long memberId);

    /**
     * 마일리지 이력 배치 등록
     *
     * @param historyList 마일리지 이력 목록
     * @return 등록된 행 수
     */
    int batchInsert(@Param("list") List<MileageHistory> historyList);
}
