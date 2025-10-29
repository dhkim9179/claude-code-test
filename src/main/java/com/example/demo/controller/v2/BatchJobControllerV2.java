package com.example.demo.controller.v2;

import com.example.demo.dto.JobExecutionRequest;
import com.example.demo.dto.JobExecutionResponse;
import com.example.demo.dto.v2.ApiResponse;
import com.example.demo.service.BatchJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 배치 Job 실행 REST API Controller v2
 * 개선사항:
 * - 표준화된 응답 형식 (ApiResponse 래퍼)
 * - 더 나은 에러 처리 및 검증
 * - 메타 정보 포함
 * - Job 목록 조회 기능 추가 (향후 구현 가능)
 */
@Slf4j
@RestController
@RequestMapping("/api/v2/batch")
@RequiredArgsConstructor
public class BatchJobControllerV2 {

    private final BatchJobService batchJobService;

    /**
     * 배치 Job을 실행하는 API
     *
     * POST /api/v2/batch/execute
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
     * Response (성공):
     * {
     *   "success": true,
     *   "data": {
     *     "executionId": 1,
     *     "jobName": "sampleJob",
     *     "status": "STARTED",
     *     "startTime": "2025-10-28T10:30:00",
     *     "endTime": null,
     *     "exitCode": "UNKNOWN",
     *     "exitMessage": null,
     *     "message": "Job execution started successfully"
     *   },
     *   "meta": {
     *     "timestamp": "2025-10-29T10:30:00",
     *     "version": "v2"
     *   }
     * }
     *
     * Response (실패):
     * {
     *   "success": false,
     *   "error": {
     *     "code": "JOB_EXECUTION_FAILED",
     *     "message": "Job not found: invalidJob"
     *   },
     *   "meta": {
     *     "timestamp": "2025-10-29T10:30:00"
     *   }
     * }
     *
     * @param request Job 실행 요청 정보 (jobId, jobParameters)
     * @return Job 실행 결과
     */
    @PostMapping("/execute")
    public ResponseEntity<ApiResponse<JobExecutionResponse>> executeJob(@RequestBody @Valid JobExecutionRequest request) {
        log.info("Received job execution request (v2): jobId={}, parameters={}",
                request.getJobId(), request.getJobParameters());

        // 입력 값 검증
        if (request.getJobId() == null || request.getJobId().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_REQUEST", "Job ID는 필수입니다."));
        }

        // Job 실행
        JobExecutionResponse response = batchJobService.executeJob(request);

        // 실패 여부 확인
        if ("FAILED".equals(response.getStatus())) {
            log.error("Job execution failed: {}", response.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("JOB_EXECUTION_FAILED", response.getMessage()));
        }

        // 성공 응답
        ApiResponse.Meta meta = ApiResponse.Meta.builder()
                .version("v2")
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, meta));
    }

    /**
     * 배치 Job 상태 조회 (향후 구현 예정)
     * GET /api/v2/batch/status/{executionId}
     *
     * @param executionId Job 실행 ID
     * @return Job 실행 상태
     */
    @GetMapping("/status/{executionId}")
    public ResponseEntity<ApiResponse<String>> getJobStatus(@PathVariable Long executionId) {
        log.info("Fetching job status for executionId: {}", executionId);

        // TODO: JobExplorer를 사용하여 실행 상태 조회 구현
        return ResponseEntity.ok(
                ApiResponse.error("NOT_IMPLEMENTED", "이 기능은 향후 구현 예정입니다.")
        );
    }
}
