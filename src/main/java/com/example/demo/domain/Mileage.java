package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 마일리지 도메인 객체
 * 회원의 마일리지 잔액 정보를 관리
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mileage {
    /**
     * 회원 ID (Primary Key)
     */
    private Long memberId;

    /**
     * 마일리지 잔액
     */
    private Long balance;

    /**
     * 생성일시
     */
    private LocalDateTime createDate;

    /**
     * 수정일시
     */
    private LocalDateTime updateDate;
}
