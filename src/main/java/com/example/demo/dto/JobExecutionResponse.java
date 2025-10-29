package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 배치 Job 실행 결과 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobExecutionResponse {

    /**
     * Job 실행 ID
     */
    private Long executionId;

    /**
     * Job 이름
     */
    private String jobName;

    /**
     * Job 실행 상태 (STARTING, STARTED, COMPLETED, FAILED 등)
     */
    private String status;

    /**
     * Job 시작 시간
     */
    private LocalDateTime startTime;

    /**
     * Job 종료 시간
     */
    private LocalDateTime endTime;

    /**
     * 종료 상태 코드 (COMPLETED, FAILED 등)
     */
    private String exitCode;

    /**
     * 종료 메시지
     */
    private String exitMessage;

    /**
     * API 응답 메시지
     */
    private String message;
}
