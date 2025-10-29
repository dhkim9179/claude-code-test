package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 마일리지 이력 도메인 객체
 * 마일리지 적립/사용 이력을 관리
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MileageHistory {
    /**
     * 이력 ID (Primary Key, Auto Increment)
     */
    private Long id;

    /**
     * 회원 ID (Foreign Key)
     */
    private Long mileageMemberId;

    /**
     * 마일리지 유형 (EARN: 적립, USE: 사용, EXPIRE: 소멸)
     */
    private String type;

    /**
     * 마일리지 금액 (적립은 양수, 사용은 음수)
     */
    private Integer amount;

    /**
     * 설명
     */
    private String description;

    /**
     * 생성일시
     */
    private LocalDateTime createDate;
}
