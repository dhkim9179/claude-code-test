package com.example.demo.batch.chunk;

import com.example.demo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * User 데이터를 처리하는 ItemProcessor 구현체
 *
 * ItemProcessor는 읽어온 데이터를 가공하는 역할을 합니다.
 * 데이터 변환, 검증, 필터링 등의 비즈니스 로직을 수행합니다.
 * null을 반환하면 해당 데이터는 Writer로 전달되지 않고 필터링됩니다.
 */
@Slf4j
@Component
public class UserItemProcessor implements ItemProcessor<User, User> {

    /**
     * 데이터 처리 메서드
     *
     * 이 예제에서는 다음 작업을 수행합니다:
     * 1. 사용자 이름을 대문자로 변환
     * 2. 비활성 사용자는 필터링 (null 반환)
     * 3. 활성 사용자만 다음 단계로 전달
     *
     * @param user 처리할 사용자 객체
     * @return 처리된 사용자 객체, 필터링할 경우 null
     * @throws Exception 처리 중 발생하는 예외
     */
    @Override
    public User process(User user) throws Exception {
        log.info("데이터 처리 시작: ID={}, Name={}, Active={}",
                user.getId(), user.getName(), user.isActive());

        // 비활성 사용자는 필터링 (null 반환)
        if (!user.isActive()) {
            log.info("비활성 사용자 필터링: ID={}, Name={}", user.getId(), user.getName());
            return null;  // null 반환 시 Writer로 전달되지 않음
        }

        // 사용자 이름을 대문자로 변환하여 새로운 User 객체 생성
        User processedUser = new User(
                user.getId(),
                user.getName().toUpperCase(),  // 이름을 대문자로 변환
                user.getEmail(),
                user.isActive()
        );

        log.info("데이터 처리 완료: ID={}, 변환된 Name={}",
                processedUser.getId(), processedUser.getName());

        return processedUser;
    }
}
