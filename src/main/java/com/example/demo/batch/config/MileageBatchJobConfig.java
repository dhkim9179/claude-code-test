package com.example.demo.batch.config;

import com.example.demo.batch.tasklet.MileageAggregationTasklet;
import com.example.demo.batch.tasklet.MileageDataGenerationTasklet;
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
 * 마일리지 배치 Job 설정
 * 데이터 생성 및 일별 집계 배치 작업 정의
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MileageBatchJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final MileageDataGenerationTasklet dataGenerationTasklet;
    private final MileageAggregationTasklet aggregationTasklet;

    /**
     * 마일리지 데이터 생성 Job
     * 100,000명의 회원과 한 달간의 마일리지 히스토리 생성
     */
    @Bean
    public Job mileageDataGenerationJob() {
        return new JobBuilder("mileageDataGenerationJob", jobRepository)
            .start(mileageDataGenerationStep())
            .build();
    }

    /**
     * 마일리지 데이터 생성 Step
     */
    @Bean
    public Step mileageDataGenerationStep() {
        return new StepBuilder("mileageDataGenerationStep", jobRepository)
            .tasklet(dataGenerationTasklet, transactionManager)
            .build();
    }

    /**
     * 마일리지 일별 집계 Job
     * 마일리지 히스토리를 일별로 집계
     */
    @Bean
    public Job mileageAggregationJob() {
        return new JobBuilder("mileageAggregationJob", jobRepository)
            .start(mileageAggregationStep())
            .build();
    }

    /**
     * 마일리지 일별 집계 Step
     */
    @Bean
    public Step mileageAggregationStep() {
        return new StepBuilder("mileageAggregationStep", jobRepository)
            .tasklet(aggregationTasklet, transactionManager)
            .build();
    }

    /**
     * 마일리지 전체 처리 Job
     * 데이터 생성 후 집계까지 순차적으로 실행
     */
    @Bean
    public Job mileageFullProcessJob() {
        return new JobBuilder("mileageFullProcessJob", jobRepository)
            .start(mileageDataGenerationStep())
            .next(mileageAggregationStep())
            .build();
    }
}
