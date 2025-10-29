package com.example.demo.mapper;

import com.example.demo.domain.Mileage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 마일리지 Mapper 인터페이스
 * MyBatis를 사용한 마일리지 데이터 접근 계층
 */
@Mapper
public interface MileageMapper {

    /**
     * 회원 ID로 마일리지 조회
     *
     * @param memberId 회원 ID
     * @return 마일리지 정보
     */
    Mileage findByMemberId(@Param("memberId") Long memberId);

    /**
     * 마일리지 신규 등록
     *
     * @param mileage 마일리지 정보
     * @return 등록된 행 수
     */
    int insert(Mileage mileage);

    /**
     * 마일리지 잔액 업데이트
     *
     * @param memberId 회원 ID
     * @param balance 새로운 잔액
     * @return 업데이트된 행 수
     */
    int updateBalance(@Param("memberId") Long memberId, @Param("balance") Long balance);

    /**
     * 마일리지 정보 삭제
     *
     * @param memberId 회원 ID
     * @return 삭제된 행 수
     */
    int deleteByMemberId(@Param("memberId") Long memberId);

    /**
     * 마일리지 잔액 증가 (적립)
     *
     * @param memberId 회원 ID
     * @param amount 증가할 금액
     * @return 업데이트된 행 수
     */
    int increaseBalance(@Param("memberId") Long memberId, @Param("amount") Long amount);

    /**
     * 마일리지 잔액 감소 (사용)
     *
     * @param memberId 회원 ID
     * @param amount 감소할 금액
     * @return 업데이트된 행 수
     */
    int decreaseBalance(@Param("memberId") Long memberId, @Param("amount") Long amount);

    /**
     * 마일리지 배치 등록
     *
     * @param mileageList 마일리지 정보 목록
     * @return 등록된 행 수
     */
    int batchInsert(@Param("list") java.util.List<Mileage> mileageList);
}
