package com.example.demo.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 마일리지 배치 자동 실행 Runner
 * 스프링 부트 시작 시 마일리지 데이터 생성 및 집계 배치를 자동으로 실행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MileageBatchRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job mileageFullProcessJob;

    @Override
    public void run(String... args) throws Exception {
        log.info("=".repeat(80));
        log.info("마일리지 배치 자동 실행 시작");
        log.info("=".repeat(80));

        try {
            // 고유한 JobParameters 생성 (같은 Job을 여러 번 실행하기 위해 timestamp 추가)
            JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

            // 마일리지 전체 처리 Job 실행 (데이터 생성 + 집계)
            jobLauncher.run(mileageFullProcessJob, jobParameters);

            log.info("=".repeat(80));
            log.info("마일리지 배치 자동 실행 완료");
            log.info("=".repeat(80));

        } catch (Exception e) {
            log.error("마일리지 배치 실행 중 오류 발생", e);
            throw e;
        }
    }
}
