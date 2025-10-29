package com.example.demo.batch.config;

import com.example.demo.batch.chunk.UserItemProcessor;
import com.example.demo.batch.chunk.UserItemReader;
import com.example.demo.batch.chunk.UserItemWriter;
import com.example.demo.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Chunk 기반 배치 Job 설정
 *
 * Chunk 기반 처리는 대량의 데이터를 처리할 때 사용하는 방식입니다.
 * 데이터를 읽고(Read) -> 처리하고(Process) -> 쓰는(Write) 과정을
 * 지정된 크기의 청크 단위로 반복 수행합니다.
 *
 * 처리 흐름:
 * 1. ItemReader: chunkSize만큼 데이터를 읽음
 * 2. ItemProcessor: 읽은 데이터를 하나씩 처리
 * 3. ItemWriter: 처리된 데이터를 chunk 단위로 일괄 저장
 * 4. 트랜잭션 커밋
 * 5. 더 이상 읽을 데이터가 없을 때까지 1-4 반복
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ChunkJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final UserItemReader userItemReader;
    private final UserItemProcessor userItemProcessor;
    private final UserItemWriter userItemWriter;

    /**
     * Chunk 기반 Job 정의
     *
     * @return Job 인스턴스
     */
    @Bean
    public Job chunkJob() {
        log.info("chunkJob 빈 생성");
        return new JobBuilder("chunkJob", jobRepository)
                .start(chunkStep())  // Step 시작
                .build();
    }

    /**
     * Chunk 기반 Step 정의
     *
     * chunk(2): 한 번에 2개씩 데이터를 읽어서 처리
     * - Reader가 2개의 데이터를 읽음
     * - 각 데이터를 Processor로 처리
     * - 처리된 데이터를 Writer에 전달하여 일괄 저장
     * - 트랜잭션 커밋
     *
     * @return Step 인스턴스
     */
    @Bean
    public Step chunkStep() {
        log.info("chunkStep 빈 생성");
        return new StepBuilder("chunkStep", jobRepository)
                .<User, User>chunk(2, transactionManager)  // chunk 크기 설정: 2개씩 처리
                .reader(userItemReader)         // 데이터 읽기
                .processor(userItemProcessor)   // 데이터 처리
                .writer(userItemWriter)         // 데이터 쓰기
                .build();
    }
}
