package com.example.demo.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 배치 Step 실행의 시작과 종료를 로깅하는 공통 리스너
 */
@Slf4j
@Component
public class BatchStepExecutionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("----------------------------------------");
        log.info("Step 시작: {}", stepExecution.getStepName());
        log.info("Step 실행 ID: {}", stepExecution.getId());
        log.info("Step 시작 시간: {}", stepExecution.getStartTime());
        log.info("----------------------------------------");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        LocalDateTime startTime = stepExecution.getStartTime();
        LocalDateTime endTime = stepExecution.getEndTime();
        Duration duration = Duration.between(startTime, endTime);

        log.info("----------------------------------------");
        log.info("Step 종료: {}", stepExecution.getStepName());
        log.info("Step 실행 ID: {}", stepExecution.getId());
        log.info("Step 상태: {}", stepExecution.getStatus());
        log.info("종료 코드: {}", stepExecution.getExitStatus().getExitCode());
        log.info("읽은 항목 수: {}", stepExecution.getReadCount());
        log.info("쓴 항목 수: {}", stepExecution.getWriteCount());
        log.info("커밋 횟수: {}", stepExecution.getCommitCount());
        log.info("롤백 횟수: {}", stepExecution.getRollbackCount());
        log.info("필터된 항목 수: {}", stepExecution.getFilterCount());
        log.info("스킵된 항목 수: {} (읽기 실패: {}, 처리 실패: {}, 쓰기 실패: {})",
                stepExecution.getReadSkipCount() + stepExecution.getProcessSkipCount() + stepExecution.getWriteSkipCount(),
                stepExecution.getReadSkipCount(),
                stepExecution.getProcessSkipCount(),
                stepExecution.getWriteSkipCount());
        log.info("Step 실행 시간: {}초 ({}ms)", duration.getSeconds(), duration.toMillis());

        // 실패한 경우 에러 정보 로깅
        if (!stepExecution.getFailureExceptions().isEmpty()) {
            log.error("----------------------------------------");
            log.error("Step 실행 중 발생한 오류:");
            stepExecution.getFailureExceptions().forEach(throwable -> {
                log.error("오류 메시지: {}", throwable.getMessage());
                log.error("오류 상세:", throwable);
            });
        }

        log.info("----------------------------------------");

        return stepExecution.getExitStatus();
    }
}
