package com.example.demo.controller;

import com.example.demo.dto.JobExecutionRequest;
import com.example.demo.dto.JobExecutionResponse;
import com.example.demo.service.BatchJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 배치 Job 실행 REST API Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchJobController {

    private final BatchJobService batchJobService;

    /**
     * 배치 Job을 실행하는 API
     *
     * POST /api/batch/execute
     *
     * Request Body:
     * {
     *   "jobId": "sampleJob",
     *   "jobParameters": {
     *     "param1": "value1",
     *     "param2": "value2"
     *   }
     * }
     *
     * Response:
     * {
     *   "executionId": 1,
     *   "jobName": "sampleJob",
     *   "status": "STARTED",
     *   "startTime": "2025-10-28T10:30:00",
     *   "endTime": null,
     *   "exitCode": "UNKNOWN",
     *   "exitMessage": null,
     *   "message": "Job execution started successfully"
     * }
     *
     * @param request Job 실행 요청 정보 (jobId, jobParameters)
     * @return Job 실행 결과
     */
    @PostMapping("/execute")
    public ResponseEntity<JobExecutionResponse> executeJob(@RequestBody JobExecutionRequest request) {
        log.info("Received job execution request: jobId={}, parameters={}",
                request.getJobId(), request.getJobParameters());

        // 입력 값 검증
        if (request.getJobId() == null || request.getJobId().trim().isEmpty()) {
            JobExecutionResponse errorResponse = JobExecutionResponse.builder()
                    .status("FAILED")
                    .exitCode("FAILED")
                    .message("Job ID is required")
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Job 실행
        JobExecutionResponse response = batchJobService.executeJob(request);

        // 실행 결과에 따라 HTTP 상태 코드 설정
        HttpStatus httpStatus = "FAILED".equals(response.getStatus())
                ? HttpStatus.INTERNAL_SERVER_ERROR
                : HttpStatus.OK;

        return ResponseEntity.status(httpStatus).body(response);
    }
}
