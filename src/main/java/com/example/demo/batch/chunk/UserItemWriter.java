package com.example.demo.batch.chunk;

import com.example.demo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * User 데이터를 쓰는 ItemWriter 구현체
 *
 * ItemWriter는 처리된 데이터를 최종 목적지에 쓰는 역할을 합니다.
 * 데이터베이스, 파일, 메시지 큐 등 다양한 목적지에 데이터를 쓸 수 있습니다.
 * 이 예제에서는 로그로 출력하는 간단한 구현을 보여줍니다.
 *
 * Chunk 단위로 데이터를 받아서 일괄 처리합니다.
 */
@Slf4j
@Component
public class UserItemWriter implements ItemWriter<User> {

    /**
     * 처리된 데이터를 쓰는 메서드
     *
     * Chunk 단위로 여러 개의 아이템을 한 번에 받아서 처리합니다.
     * 트랜잭션 범위 내에서 실행되므로, 실패 시 전체 Chunk가 롤백됩니다.
     *
     * @param chunk 쓸 데이터 청크 (여러 개의 User 객체 포함)
     * @throws Exception 쓰기 중 발생하는 예외
     */
    @Override
    public void write(Chunk<? extends User> chunk) throws Exception {
        log.info("=== 데이터 쓰기 시작 (Chunk 크기: {}) ===", chunk.size());

        // Chunk에 포함된 모든 아이템을 처리
        for (User user : chunk) {
            // 실제로는 데이터베이스에 저장하거나 파일에 쓰는 작업을 수행
            // 이 예제에서는 로그로 출력
            log.info("데이터 저장: ID={}, Name={}, Email={}, Active={}",
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.isActive());

            // 실제 데이터베이스 저장 예시 (주석 처리)
            // userRepository.save(user);
        }

        log.info("=== 데이터 쓰기 완료: {} 건 처리됨 ===", chunk.size());
    }
}
