package com.example.demo.util;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * 날짜/시간 유틸리티 클래스
 * LocalDateTime, LocalDate, LocalTime을 활용한 다양한 날짜/시간 처리 기능 제공
 */
@Slf4j
public class DateUtil {

    // 기본 포맷터
    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DEFAULT_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter COMPACT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    public static final DateTimeFormatter COMPACT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

    // ==================== 현재 시간 관련 ====================

    /**
     * 현재 LocalDateTime 반환
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 현재 LocalDate 반환
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * 현재 LocalTime 반환
     */
    public static LocalTime currentTime() {
        return LocalTime.now();
    }

    /**
     * 현재 시간을 지정된 포맷으로 문자열 반환
     */
    public static String nowAsString() {
        return now().format(DEFAULT_DATE_TIME_FORMATTER);
    }

    /**
     * 현재 날짜를 지정된 포맷으로 문자열 반환
     */
    public static String todayAsString() {
        return today().format(DEFAULT_DATE_FORMATTER);
    }

    // ==================== 포맷팅 관련 ====================

    /**
     * LocalDateTime을 문자열로 변환 (기본 포맷: yyyy-MM-dd HH:mm:ss)
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_DATE_TIME_FORMATTER) : null;
    }

    /**
     * LocalDateTime을 지정된 포맷으로 변환
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || pattern == null) {
            return null;
        }
        try {
            return dateTime.format(DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            log.error("Failed to format LocalDateTime with pattern: {}", pattern, e);
            return null;
        }
    }

    /**
     * LocalDate를 문자열로 변환 (기본 포맷: yyyy-MM-dd)
     */
    public static String format(LocalDate date) {
        return date != null ? date.format(DEFAULT_DATE_FORMATTER) : null;
    }

    /**
     * LocalDate를 지정된 포맷으로 변환
     */
    public static String format(LocalDate date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        try {
            return date.format(DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            log.error("Failed to format LocalDate with pattern: {}", pattern, e);
            return null;
        }
    }

    /**
     * LocalTime을 문자열로 변환 (기본 포맷: HH:mm:ss)
     */
    public static String format(LocalTime time) {
        return time != null ? time.format(DEFAULT_TIME_FORMATTER) : null;
    }

    /**
     * LocalTime을 지정된 포맷으로 변환
     */
    public static String format(LocalTime time, String pattern) {
        if (time == null || pattern == null) {
            return null;
        }
        try {
            return time.format(DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            log.error("Failed to format LocalTime with pattern: {}", pattern, e);
            return null;
        }
    }

    // ==================== 파싱 관련 ====================

    /**
     * 문자열을 LocalDateTime으로 변환 (기본 포맷: yyyy-MM-dd HH:mm:ss)
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DEFAULT_DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("Failed to parse DateTime: {}", dateTimeStr, e);
            return null;
        }
    }

    /**
     * 문자열을 지정된 포맷으로 LocalDateTime 변환
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.isEmpty() || pattern == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            log.error("Failed to parse DateTime with pattern {}: {}", pattern, dateTimeStr, e);
            return null;
        }
    }

    /**
     * 문자열을 LocalDate로 변환 (기본 포맷: yyyy-MM-dd)
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DEFAULT_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("Failed to parse Date: {}", dateStr, e);
            return null;
        }
    }

    /**
     * 문자열을 지정된 포맷으로 LocalDate 변환
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty() || pattern == null) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            log.error("Failed to parse Date with pattern {}: {}", pattern, dateStr, e);
            return null;
        }
    }

    /**
     * 문자열을 LocalTime으로 변환 (기본 포맷: HH:mm:ss)
     */
    public static LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(timeStr, DEFAULT_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("Failed to parse Time: {}", timeStr, e);
            return null;
        }
    }

    /**
     * 문자열을 지정된 포맷으로 LocalTime 변환
     */
    public static LocalTime parseTime(String timeStr, String pattern) {
        if (timeStr == null || timeStr.isEmpty() || pattern == null) {
            return null;
        }
        try {
            return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            log.error("Failed to parse Time with pattern {}: {}", pattern, timeStr, e);
            return null;
        }
    }

    // ==================== 날짜 연산 관련 ====================

    /**
     * 날짜에 일수 더하기
     */
    public static LocalDate plusDays(LocalDate date, long days) {
        return date != null ? date.plusDays(days) : null;
    }

    /**
     * 날짜에 월수 더하기
     */
    public static LocalDate plusMonths(LocalDate date, long months) {
        return date != null ? date.plusMonths(months) : null;
    }

    /**
     * 날짜에 년수 더하기
     */
    public static LocalDate plusYears(LocalDate date, long years) {
        return date != null ? date.plusYears(years) : null;
    }

    /**
     * 날짜시간에 초 더하기
     */
    public static LocalDateTime plusSeconds(LocalDateTime dateTime, long seconds) {
        return dateTime != null ? dateTime.plusSeconds(seconds) : null;
    }

    /**
     * 날짜시간에 분 더하기
     */
    public static LocalDateTime plusMinutes(LocalDateTime dateTime, long minutes) {
        return dateTime != null ? dateTime.plusMinutes(minutes) : null;
    }

    /**
     * 날짜시간에 시간 더하기
     */
    public static LocalDateTime plusHours(LocalDateTime dateTime, long hours) {
        return dateTime != null ? dateTime.plusHours(hours) : null;
    }

    /**
     * 날짜시간에 일수 더하기
     */
    public static LocalDateTime plusDays(LocalDateTime dateTime, long days) {
        return dateTime != null ? dateTime.plusDays(days) : null;
    }

    /**
     * 날짜에 일수 빼기
     */
    public static LocalDate minusDays(LocalDate date, long days) {
        return date != null ? date.minusDays(days) : null;
    }

    /**
     * 날짜에 월수 빼기
     */
    public static LocalDate minusMonths(LocalDate date, long months) {
        return date != null ? date.minusMonths(months) : null;
    }

    /**
     * 날짜에 년수 빼기
     */
    public static LocalDate minusYears(LocalDate date, long years) {
        return date != null ? date.minusYears(years) : null;
    }

    /**
     * 날짜시간에 초 빼기
     */
    public static LocalDateTime minusSeconds(LocalDateTime dateTime, long seconds) {
        return dateTime != null ? dateTime.minusSeconds(seconds) : null;
    }

    /**
     * 날짜시간에 분 빼기
     */
    public static LocalDateTime minusMinutes(LocalDateTime dateTime, long minutes) {
        return dateTime != null ? dateTime.minusMinutes(minutes) : null;
    }

    /**
     * 날짜시간에 시간 빼기
     */
    public static LocalDateTime minusHours(LocalDateTime dateTime, long hours) {
        return dateTime != null ? dateTime.minusHours(hours) : null;
    }

    /**
     * 날짜시간에 일수 빼기
     */
    public static LocalDateTime minusDays(LocalDateTime dateTime, long days) {
        return dateTime != null ? dateTime.minusDays(days) : null;
    }

    // ==================== 날짜 비교 관련 ====================

    /**
     * 두 날짜가 같은지 비교
     */
    public static boolean isEqual(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.isEqual(date2);
    }

    /**
     * 첫 번째 날짜가 두 번째 날짜보다 이전인지 확인
     */
    public static boolean isBefore(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.isBefore(date2);
    }

    /**
     * 첫 번째 날짜가 두 번째 날짜보다 이후인지 확인
     */
    public static boolean isAfter(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.isAfter(date2);
    }

    /**
     * 두 날짜시간이 같은지 비교
     */
    public static boolean isEqual(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        if (dateTime1 == null || dateTime2 == null) {
            return false;
        }
        return dateTime1.isEqual(dateTime2);
    }

    /**
     * 첫 번째 날짜시간이 두 번째 날짜시간보다 이전인지 확인
     */
    public static boolean isBefore(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        if (dateTime1 == null || dateTime2 == null) {
            return false;
        }
        return dateTime1.isBefore(dateTime2);
    }

    /**
     * 첫 번째 날짜시간이 두 번째 날짜시간보다 이후인지 확인
     */
    public static boolean isAfter(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        if (dateTime1 == null || dateTime2 == null) {
            return false;
        }
        return dateTime1.isAfter(dateTime2);
    }

    // ==================== 날짜 차이 계산 ====================

    /**
     * 두 날짜 사이의 일수 차이 계산
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * 두 날짜 사이의 월수 차이 계산
     */
    public static long monthsBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.MONTHS.between(startDate, endDate);
    }

    /**
     * 두 날짜 사이의 년수 차이 계산
     */
    public static long yearsBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.YEARS.between(startDate, endDate);
    }

    /**
     * 두 날짜시간 사이의 초 차이 계산
     */
    public static long secondsBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            return 0;
        }
        return ChronoUnit.SECONDS.between(startDateTime, endDateTime);
    }

    /**
     * 두 날짜시간 사이의 분 차이 계산
     */
    public static long minutesBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(startDateTime, endDateTime);
    }

    /**
     * 두 날짜시간 사이의 시간 차이 계산
     */
    public static long hoursBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(startDateTime, endDateTime);
    }

    /**
     * 두 날짜시간 사이의 일수 차이 계산
     */
    public static long daysBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDateTime, endDateTime);
    }

    // ==================== 날짜 조정 관련 ====================

    /**
     * 해당 월의 첫째 날 반환
     */
    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        return date != null ? date.with(TemporalAdjusters.firstDayOfMonth()) : null;
    }

    /**
     * 해당 월의 마지막 날 반환
     */
    public static LocalDate getLastDayOfMonth(LocalDate date) {
        return date != null ? date.with(TemporalAdjusters.lastDayOfMonth()) : null;
    }

    /**
     * 해당 년도의 첫째 날 반환
     */
    public static LocalDate getFirstDayOfYear(LocalDate date) {
        return date != null ? date.with(TemporalAdjusters.firstDayOfYear()) : null;
    }

    /**
     * 해당 년도의 마지막 날 반환
     */
    public static LocalDate getLastDayOfYear(LocalDate date) {
        return date != null ? date.with(TemporalAdjusters.lastDayOfYear()) : null;
    }

    /**
     * 다음 월요일 반환
     */
    public static LocalDate getNextMonday(LocalDate date) {
        return date != null ? date.with(TemporalAdjusters.next(DayOfWeek.MONDAY)) : null;
    }

    /**
     * 이전 월요일 반환
     */
    public static LocalDate getPreviousMonday(LocalDate date) {
        return date != null ? date.with(TemporalAdjusters.previous(DayOfWeek.MONDAY)) : null;
    }

    /**
     * 하루의 시작 시간 (00:00:00) 반환
     */
    public static LocalDateTime getStartOfDay(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }

    /**
     * 하루의 종료 시간 (23:59:59) 반환
     */
    public static LocalDateTime getEndOfDay(LocalDate date) {
        return date != null ? date.atTime(23, 59, 59) : null;
    }

    // ==================== 변환 관련 ====================

    /**
     * LocalDate를 LocalDateTime으로 변환 (시작 시간 00:00:00)
     */
    public static LocalDateTime toLocalDateTime(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }

    /**
     * LocalDate를 LocalDateTime으로 변환 (지정된 시간)
     */
    public static LocalDateTime toLocalDateTime(LocalDate date, LocalTime time) {
        if (date == null || time == null) {
            return null;
        }
        return LocalDateTime.of(date, time);
    }

    /**
     * LocalDateTime을 LocalDate로 변환
     */
    public static LocalDate toLocalDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate() : null;
    }

    /**
     * LocalDateTime을 LocalTime으로 변환
     */
    public static LocalTime toLocalTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalTime() : null;
    }

    /**
     * LocalDateTime을 Date로 변환
     */
    public static Date toDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return Date.from(dateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * LocalDate를 Date로 변환
     */
    public static Date toDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return Date.from(date.atStartOfDay(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * Date를 LocalDateTime으로 변환
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE_ID);
    }

    /**
     * Date를 LocalDate로 변환
     */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDate();
    }

    /**
     * Timestamp를 LocalDateTime으로 변환
     */
    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), DEFAULT_ZONE_ID);
    }

    /**
     * LocalDateTime을 Timestamp로 변환
     */
    public static long toTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return 0;
        }
        return dateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    // ==================== 유틸리티 관련 ====================

    /**
     * 윤년 여부 확인
     */
    public static boolean isLeapYear(int year) {
        return Year.of(year).isLeap();
    }

    /**
     * 날짜의 윤년 여부 확인
     */
    public static boolean isLeapYear(LocalDate date) {
        return date != null && date.isLeapYear();
    }

    /**
     * 주말 여부 확인 (토요일, 일요일)
     */
    public static boolean isWeekend(LocalDate date) {
        if (date == null) {
            return false;
        }
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * 평일 여부 확인 (월요일~금요일)
     */
    public static boolean isWeekday(LocalDate date) {
        return date != null && !isWeekend(date);
    }

    /**
     * 해당 월의 일수 반환
     */
    public static int getLengthOfMonth(LocalDate date) {
        return date != null ? date.lengthOfMonth() : 0;
    }

    /**
     * 해당 년도의 일수 반환
     */
    public static int getLengthOfYear(LocalDate date) {
        return date != null ? date.lengthOfYear() : 0;
    }

    /**
     * 나이 계산 (만 나이)
     */
    public static int calculateAge(LocalDate birthDate) {
        return calculateAge(birthDate, LocalDate.now());
    }

    /**
     * 특정 기준일 기준 나이 계산
     */
    public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if (birthDate == null || currentDate == null) {
            return 0;
        }
        return Period.between(birthDate, currentDate).getYears();
    }
}
