package com.example.demo.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Batch 설정
 * Job을 JobRegistry에 자동 등록하기 위한 설정
 */
@Configuration
public class BatchConfig {

    /**
     * JobRegistryBeanPostProcessor를 통해 모든 Job 빈을 JobRegistry에 자동 등록
     * 이를 통해 BatchJobService에서 jobRegistry.getJob()으로 Job을 동적으로 조회 가능
     */
    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        return postProcessor;
    }
}
