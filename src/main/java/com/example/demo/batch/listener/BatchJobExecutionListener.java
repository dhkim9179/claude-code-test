package com.example.demo.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 배치 Job 실행의 시작과 종료를 로깅하는 공통 리스너
 */
@Slf4j
@Component
public class BatchJobExecutionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("========================================");
        log.info("배치 작업 시작");
        log.info("========================================");
        log.info("Job 이름: {}", jobExecution.getJobInstance().getJobName());
        log.info("Job 실행 ID: {}", jobExecution.getId());
        log.info("Job Parameters: {}", jobExecution.getJobParameters());
        log.info("시작 시간: {}", jobExecution.getStartTime());
        log.info("========================================");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        LocalDateTime startTime = jobExecution.getStartTime();
        LocalDateTime endTime = jobExecution.getEndTime();
        Duration duration = Duration.between(startTime, endTime);

        log.info("========================================");
        log.info("배치 작업 종료");
        log.info("========================================");
        log.info("Job 이름: {}", jobExecution.getJobInstance().getJobName());
        log.info("Job 실행 ID: {}", jobExecution.getId());
        log.info("실행 상태: {}", jobExecution.getStatus());
        log.info("종료 상태: {}", jobExecution.getExitStatus().getExitCode());
        log.info("시작 시간: {}", startTime);
        log.info("종료 시간: {}", endTime);
        log.info("실행 시간: {}초 ({}ms)", duration.getSeconds(), duration.toMillis());

        // 실패한 경우 에러 정보 로깅
        if (!jobExecution.getAllFailureExceptions().isEmpty()) {
            log.error("========================================");
            log.error("배치 실행 중 발생한 오류:");
            jobExecution.getAllFailureExceptions().forEach(throwable -> {
                log.error("오류 메시지: {}", throwable.getMessage());
                log.error("오류 상세:", throwable);
            });
        }

        log.info("========================================");
    }
}
