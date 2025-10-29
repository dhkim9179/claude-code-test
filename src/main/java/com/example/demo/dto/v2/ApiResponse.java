package com.example.demo.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * API 응답 표준 래퍼 클래스 (v2)
 * 모든 v2 API 응답은 이 형식을 따름
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 성공 여부
     */
    private boolean success;

    /**
     * 응답 데이터
     */
    private T data;

    /**
     * 메타 정보 (페이징, 타임스탬프 등)
     */
    private Meta meta;

    /**
     * 에러 정보 (실패 시에만 포함)
     */
    private ErrorInfo error;

    /**
     * 성공 응답 생성
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .meta(Meta.builder()
                        .timestamp(LocalDateTime.now())
                        .build())
                .build();
    }

    /**
     * 성공 응답 생성 (메타 정보 포함)
     */
    public static <T> ApiResponse<T> success(T data, Meta meta) {
        meta.setTimestamp(LocalDateTime.now());
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .meta(meta)
                .build();
    }

    /**
     * 실패 응답 생성
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(ErrorInfo.builder()
                        .code(code)
                        .message(message)
                        .build())
                .meta(Meta.builder()
                        .timestamp(LocalDateTime.now())
                        .build())
                .build();
    }

    /**
     * 메타 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        private LocalDateTime timestamp;
        private PageInfo pageInfo;
        private String version;
    }

    /**
     * 페이징 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
    }

    /**
     * 에러 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorInfo {
        private String code;
        private String message;
        private String detail;
    }
}
