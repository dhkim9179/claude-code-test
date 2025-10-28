package com.example.demo.batch.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

/**
 * 간단한 Tasklet 구현체
 *
 * Tasklet은 단일 작업을 수행하는 Spring Batch의 기본 처리 방식입니다.
 * Step에서 한 번만 실행되며, 주로 초기화, 정리 작업 등에 사용됩니다.
 */
@Slf4j
@Component
public class SimpleTasklet implements Tasklet {

    /**
     * Tasklet의 실제 실행 로직
     *
     * @param contribution Step의 실행 정보를 담고 있는 객체
     * @param chunkContext Chunk의 실행 컨텍스트 정보
     * @return RepeatStatus.FINISHED - 작업 완료
     *         RepeatStatus.CONTINUABLE - 작업 계속
     * @throws Exception 실행 중 발생하는 예외
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("=== SimpleTasklet 시작 ===");

        // 실제 비즈니스 로직 수행
        // 예: 임시 파일 삭제, 데이터베이스 초기화, 외부 API 호출 등
        log.info("Tasklet에서 단순 작업을 수행합니다.");
        log.info("데이터 정리 작업을 시뮬레이션합니다...");

        // 작업 시뮬레이션을 위한 대기
        Thread.sleep(1000);

        // Step의 실행 정보 기록
        contribution.incrementReadCount();
        contribution.incrementWriteCount(1);

        log.info("Tasklet 작업이 성공적으로 완료되었습니다.");
        log.info("=== SimpleTasklet 종료 ===");

        // FINISHED를 반환하여 작업 완료를 알림
        return RepeatStatus.FINISHED;
    }
}
