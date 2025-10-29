package com.example.demo.batch.config;

import com.example.demo.batch.tasklet.SimpleTasklet;
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
 * Tasklet 기반 배치 Job 설정
 *
 * Tasklet은 단순한 작업을 처리하는데 적합하며,
 * 데이터를 읽고 처리하고 쓰는 반복 작업보다는
 * 한 번의 작업으로 끝나는 처리에 사용됩니다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class TaskletJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SimpleTasklet simpleTasklet;

    /**
     * Tasklet 기반 Job 정의
     *
     * @return Job 인스턴스
     */
    @Bean
    public Job taskletJob() {
        log.info("taskletJob 빈 생성");
        return new JobBuilder("taskletJob", jobRepository)
                .start(taskletStep())  // Step 시작
                .build();
    }

    /**
     * Tasklet을 실행하는 Step 정의
     *
     * Step은 Job의 실제 처리 단위입니다.
     *
     * @return Step 인스턴스
     */
    @Bean
    public Step taskletStep() {
        log.info("taskletStep 빈 생성");
        return new StepBuilder("taskletStep", jobRepository)
                .tasklet(simpleTasklet, transactionManager)  // Tasklet 설정
                .build();
    }
}
