package com.example.demo.batch.config;

import com.example.demo.batch.tasklet.DailyMileageAggregationTasklet;
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
 * 일별 마일리지 적립/사용 집계 배치 Job 설정
 * 매일 오전 9시에 전일자 마일리지 적립/사용 내역을 집계합니다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DailyMileageAggregationJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DailyMileageAggregationTasklet dailyMileageAggregationTasklet;

    /**
     * 일별 마일리지 집계 Job
     * Job 이름: dailyMileageAggregationJob
     */
    @Bean
    public Job dailyMileageAggregationJob() {
        log.info("Configuring dailyMileageAggregationJob");
        return new JobBuilder("dailyMileageAggregationJob", jobRepository)
                .start(dailyMileageAggregationStep())
                .build();
    }

    /**
     * 일별 마일리지 집계 Step
     */
    @Bean
    public Step dailyMileageAggregationStep() {
        return new StepBuilder("dailyMileageAggregationStep", jobRepository)
                .tasklet(dailyMileageAggregationTasklet, transactionManager)
                .build();
    }
}
