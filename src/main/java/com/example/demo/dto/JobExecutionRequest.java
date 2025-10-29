package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 배치 Job 실행 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobExecutionRequest {

    /**
     * 실행할 Job의 ID (Job 이름)
     */
    private String jobId;

    /**
     * Job 실행시 전달할 파라미터
     * Key-Value 형태로 전달
     */
    private Map<String, String> jobParameters;
}
