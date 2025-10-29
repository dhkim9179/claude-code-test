package com.example.demo.service;

import com.example.demo.domain.Mileage;
import com.example.demo.domain.MileageHistory;
import com.example.demo.mapper.MileageHistoryMapper;
import com.example.demo.mapper.MileageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 마일리지 서비스
 * MyBatis Mapper를 사용한 마일리지 비즈니스 로직 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MileageService {

    private final MileageMapper mileageMapper;
    private final MileageHistoryMapper mileageHistoryMapper;

    /**
     * 회원의 마일리지 조회
     *
     * @param memberId 회원 ID
     * @return 마일리지 정보
     */
    public Mileage getMileage(Long memberId) {
        log.info("조회 시작 - 회원 ID: {}", memberId);
        Mileage mileage = mileageMapper.findByMemberId(memberId);
        log.info("조회 완료 - 마일리지: {}", mileage);
        return mileage;
    }

    /**
     * 회원의 마일리지 이력 조회
     *
     * @param memberId 회원 ID
     * @return 마일리지 이력 목록
     */
    public List<MileageHistory> getMileageHistory(Long memberId) {
        log.info("이력 조회 시작 - 회원 ID: {}", memberId);
        List<MileageHistory> histories = mileageHistoryMapper.findByMemberId(memberId);
        log.info("이력 조회 완료 - 건수: {}", histories.size());
        return histories;
    }

    /**
     * 마일리지 적립
     * 마일리지 잔액 증가 및 이력 기록
     *
     * @param memberId 회원 ID
     * @param amount 적립 금액
     * @param description 적립 사유
     */
    @Transactional
    public void earnMileage(Long memberId, Integer amount, String description) {
        log.info("마일리지 적립 시작 - 회원 ID: {}, 금액: {}, 사유: {}", memberId, amount, description);

        // 1. 마일리지 잔액 증가
        int updatedRows = mileageMapper.increaseBalance(memberId, amount.longValue());
        if (updatedRows == 0) {
            throw new RuntimeException("마일리지 적립 실패 - 회원을 찾을 수 없습니다: " + memberId);
        }

        // 2. 이력 기록
        MileageHistory history = new MileageHistory();
        history.setMileageMemberId(memberId);
        history.setType("EARN");
        history.setAmount(amount);
        history.setDescription(description);
        history.setCreateDate(LocalDateTime.now());

        mileageHistoryMapper.insert(history);
        log.info("마일리지 적립 완료 - 회원 ID: {}, 금액: {}", memberId, amount);
    }

    /**
     * 마일리지 사용
     * 마일리지 잔액 차감 및 이력 기록
     *
     * @param memberId 회원 ID
     * @param amount 사용 금액
     * @param description 사용 사유
     */
    @Transactional
    public void useMileage(Long memberId, Integer amount, String description) {
        log.info("마일리지 사용 시작 - 회원 ID: {}, 금액: {}, 사유: {}", memberId, amount, description);

        // 1. 현재 잔액 확인
        Mileage mileage = mileageMapper.findByMemberId(memberId);
        if (mileage == null) {
            throw new RuntimeException("마일리지 사용 실패 - 회원을 찾을 수 없습니다: " + memberId);
        }

        if (mileage.getBalance() < amount) {
            throw new RuntimeException("마일리지 사용 실패 - 잔액 부족: " + mileage.getBalance());
        }

        // 2. 마일리지 잔액 차감
        int updatedRows = mileageMapper.decreaseBalance(memberId, amount.longValue());
        if (updatedRows == 0) {
            throw new RuntimeException("마일리지 사용 실패 - 잔액이 부족합니다");
        }

        // 3. 이력 기록 (사용 금액은 음수로 저장)
        MileageHistory history = new MileageHistory();
        history.setMileageMemberId(memberId);
        history.setType("USE");
        history.setAmount(-amount);
        history.setDescription(description);
        history.setCreateDate(LocalDateTime.now());

        mileageHistoryMapper.insert(history);
        log.info("마일리지 사용 완료 - 회원 ID: {}, 금액: {}", memberId, amount);
    }

    /**
     * 신규 회원 마일리지 생성
     *
     * @param memberId 회원 ID
     */
    @Transactional
    public void createMileage(Long memberId) {
        log.info("마일리지 생성 시작 - 회원 ID: {}", memberId);

        Mileage mileage = new Mileage();
        mileage.setMemberId(memberId);
        mileage.setBalance(0L);
        mileage.setCreateDate(LocalDateTime.now());

        mileageMapper.insert(mileage);
        log.info("마일리지 생성 완료 - 회원 ID: {}", memberId);
    }

    /**
     * 유형별 마일리지 이력 조회
     *
     * @param memberId 회원 ID
     * @param type 마일리지 유형 (EARN, USE, EXPIRE)
     * @return 마일리지 이력 목록
     */
    public List<MileageHistory> getMileageHistoryByType(Long memberId, String type) {
        log.info("유형별 이력 조회 - 회원 ID: {}, 유형: {}", memberId, type);
        List<MileageHistory> histories = mileageHistoryMapper.findByMemberIdAndType(memberId, type);
        log.info("유형별 이력 조회 완료 - 건수: {}", histories.size());
        return histories;
    }

    /**
     * 마일리지 이력 총 건수 조회
     *
     * @param memberId 회원 ID
     * @return 이력 총 건수
     */
    public int getMileageHistoryCount(Long memberId) {
        return mileageHistoryMapper.countByMemberId(memberId);
    }
}
