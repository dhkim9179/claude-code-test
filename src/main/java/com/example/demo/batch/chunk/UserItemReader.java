package com.example.demo.batch.chunk;

import com.example.demo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * User 데이터를 읽어오는 ItemReader 구현체
 *
 * ItemReader는 데이터 소스로부터 데이터를 읽어오는 역할을 합니다.
 * 파일, 데이터베이스, 메시지 큐 등 다양한 소스로부터 데이터를 읽을 수 있습니다.
 * 이 예제에서는 메모리에 있는 리스트에서 데이터를 읽어옵니다.
 */
@Slf4j
@Component
public class UserItemReader implements ItemReader<User> {

    /**
     * 읽을 데이터 목록
     */
    private final List<User> users;

    /**
     * 현재 읽은 데이터의 인덱스
     */
    private int currentIndex = 0;

    /**
     * 생성자: 샘플 데이터 초기화
     */
    public UserItemReader() {
        this.users = new ArrayList<>();
        // 샘플 사용자 데이터 생성
        users.add(new User(1L, "홍길동", "hong@example.com", true));
        users.add(new User(2L, "김철수", "kim@example.com", false));
        users.add(new User(3L, "이영희", "lee@example.com", true));
        users.add(new User(4L, "박민수", "park@example.com", true));
        users.add(new User(5L, "정수진", "jung@example.com", false));

        log.info("UserItemReader 초기화 완료: 총 {} 개의 사용자 데이터", users.size());
    }

    /**
     * 데이터를 한 건씩 읽어오는 메서드
     *
     * @return User 객체, 더 이상 읽을 데이터가 없으면 null 반환
     * @throws Exception 읽기 중 발생하는 예외
     */
    @Override
    public User read() throws Exception {
        // 모든 데이터를 읽었으면 null 반환하여 읽기 종료를 알림
        if (currentIndex >= users.size()) {
            log.info("모든 데이터 읽기 완료");
            return null;
        }

        // 현재 인덱스의 데이터를 읽고 인덱스 증가
        User user = users.get(currentIndex++);
        log.info("데이터 읽기: ID={}, Name={}", user.getId(), user.getName());

        return user;
    }
}
