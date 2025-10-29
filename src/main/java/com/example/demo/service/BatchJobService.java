package com.example.demo.service;

import com.example.demo.dto.JobExecutionRequest;
import com.example.demo.dto.JobExecutionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * 배치 Job 실행을 담당하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BatchJobService {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    /**
     * 배치 Job을 실행합니다.
     *
     * @param request Job 실행 요청 정보
     * @return Job 실행 결과
     */
    public JobExecutionResponse executeJob(JobExecutionRequest request) {
        try {
            // JobRegistry에서 Job을 가져옴
            Job job = jobRegistry.getJob(request.getJobId());

            // JobParameters 생성
            JobParameters jobParameters = createJobParameters(request.getJobParameters());

            // Job 실행
            log.info("Starting batch job: {} with parameters: {}", request.getJobId(), jobParameters);
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);

            // 실행 결과 반환
            return buildJobExecutionResponse(jobExecution, "Job execution started successfully");

        } catch (NoSuchJobException e) {
            log.error("Job not found: {}", request.getJobId(), e);
            return JobExecutionResponse.builder()
                    .jobName(request.getJobId())
                    .status("FAILED")
                    .exitCode("FAILED")
                    .message("Job not found: " + request.getJobId())
                    .exitMessage(e.getMessage())
                    .build();

        } catch (JobExecutionAlreadyRunningException e) {
            log.error("Job is already running: {}", request.getJobId(), e);
            return JobExecutionResponse.builder()
                    .jobName(request.getJobId())
                    .status("FAILED")
                    .exitCode("FAILED")
                    .message("Job is already running: " + request.getJobId())
                    .exitMessage(e.getMessage())
                    .build();

        } catch (JobRestartException e) {
            log.error("Job restart error: {}", request.getJobId(), e);
            return JobExecutionResponse.builder()
                    .jobName(request.getJobId())
                    .status("FAILED")
                    .exitCode("FAILED")
                    .message("Job restart error: " + request.getJobId())
                    .exitMessage(e.getMessage())
                    .build();

        } catch (JobInstanceAlreadyCompleteException e) {
            log.error("Job instance already complete: {}", request.getJobId(), e);
            return JobExecutionResponse.builder()
                    .jobName(request.getJobId())
                    .status("FAILED")
                    .exitCode("FAILED")
                    .message("Job instance already complete: " + request.getJobId())
                    .exitMessage(e.getMessage())
                    .build();

        } catch (JobParametersInvalidException e) {
            log.error("Invalid job parameters: {}", request.getJobId(), e);
            return JobExecutionResponse.builder()
                    .jobName(request.getJobId())
                    .status("FAILED")
                    .exitCode("FAILED")
                    .message("Invalid job parameters: " + request.getJobId())
                    .exitMessage(e.getMessage())
                    .build();

        } catch (Exception e) {
            log.error("Unexpected error executing job: {}", request.getJobId(), e);
            return JobExecutionResponse.builder()
                    .jobName(request.getJobId())
                    .status("FAILED")
                    .exitCode("FAILED")
                    .message("Unexpected error executing job: " + request.getJobId())
                    .exitMessage(e.getMessage())
                    .build();
        }
    }

    /**
     * JobParameters를 생성합니다.
     * 각 Job 실행을 고유하게 만들기 위해 timestamp를 추가합니다.
     *
     * @param params 사용자가 전달한 파라미터 맵
     * @return JobParameters 객체
     */
    private JobParameters createJobParameters(Map<String, String> params) {
        JobParametersBuilder builder = new JobParametersBuilder();

        // 사용자가 전달한 파라미터 추가
        if (params != null && !params.isEmpty()) {
            params.forEach(builder::addString);
        }

        // 각 실행을 고유하게 만들기 위한 timestamp 추가
        builder.addLong("timestamp", System.currentTimeMillis());

        return builder.toJobParameters();
    }

    /**
     * JobExecution 정보를 기반으로 응답 DTO를 생성합니다.
     *
     * @param jobExecution Job 실행 정보
     * @param message 응답 메시지
     * @return JobExecutionResponse 객체
     */
    private JobExecutionResponse buildJobExecutionResponse(JobExecution jobExecution, String message) {
        return JobExecutionResponse.builder()
                .executionId(jobExecution.getId())
                .jobName(jobExecution.getJobInstance().getJobName())
                .status(jobExecution.getStatus().name())
                .startTime(jobExecution.getStartTime() != null
                        ? LocalDateTime.ofInstant(jobExecution.getStartTime().toInstant(), ZoneId.systemDefault())
                        : null)
                .endTime(jobExecution.getEndTime() != null
                        ? LocalDateTime.ofInstant(jobExecution.getEndTime().toInstant(), ZoneId.systemDefault())
                        : null)
                .exitCode(jobExecution.getExitStatus().getExitCode())
                .exitMessage(jobExecution.getExitStatus().getExitDescription())
                .message(message)
                .build();
    }
}
