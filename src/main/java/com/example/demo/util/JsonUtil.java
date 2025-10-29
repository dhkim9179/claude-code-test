package com.example.demo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * JSON 유틸리티 클래스
 * Jackson ObjectMapper를 사용하여 JSON 직렬화/역직렬화 기능 제공
 */
@Slf4j
public class JsonUtil {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        // Java 8 날짜/시간 타입 지원
        objectMapper.registerModule(new JavaTimeModule());
        // 날짜를 타임스탬프가 아닌 ISO-8601 형식으로 직렬화
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 알 수 없는 속성이 있어도 무시하고 역직렬화
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // null 값은 직렬화하지 않음
        objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
    }

    /**
     * ObjectMapper 인스턴스 반환
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * 객체를 JSON 문자열로 변환
     *
     * @param object 변환할 객체
     * @return JSON 문자열, 변환 실패 시 null
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to JSON: {}", object.getClass().getName(), e);
            return null;
        }
    }

    /**
     * 객체를 예쁘게 포맷된 JSON 문자열로 변환
     *
     * @param object 변환할 객체
     * @return 포맷된 JSON 문자열, 변환 실패 시 null
     */
    public static String toPrettyJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to pretty JSON: {}", object.getClass().getName(), e);
            return null;
        }
    }

    /**
     * JSON 문자열을 객체로 변환
     *
     * @param json  JSON 문자열
     * @param clazz 변환할 클래스 타입
     * @param <T>   반환 타입
     * @return 변환된 객체, 변환 실패 시 null
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert JSON to object: {}", clazz.getName(), e);
            return null;
        }
    }

    /**
     * JSON 문자열을 TypeReference를 사용하여 제네릭 타입으로 변환
     * 예: List<User>, Map<String, Object> 등
     *
     * @param json          JSON 문자열
     * @param typeReference 타입 참조
     * @param <T>           반환 타입
     * @return 변환된 객체, 변환 실패 시 null
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert JSON to object with TypeReference", e);
            return null;
        }
    }

    /**
     * JSON 문자열을 List로 변환
     *
     * @param json  JSON 문자열
     * @param clazz 리스트 요소의 클래스 타입
     * @param <T>   리스트 요소 타입
     * @return 변환된 리스트, 변환 실패 시 null
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            log.error("Failed to convert JSON to List: {}", clazz.getName(), e);
            return null;
        }
    }

    /**
     * JSON 문자열을 Map으로 변환
     *
     * @param json JSON 문자열
     * @return 변환된 Map, 변환 실패 시 null
     */
    public static Map<String, Object> fromJsonToMap(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Failed to convert JSON to Map", e);
            return null;
        }
    }

    /**
     * 객체를 다른 타입의 객체로 변환 (JSON을 거쳐서)
     *
     * @param object 원본 객체
     * @param clazz  대상 클래스 타입
     * @param <T>    반환 타입
     * @return 변환된 객체, 변환 실패 시 null
     */
    public static <T> T convertValue(Object object, Class<T> clazz) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.convertValue(object, clazz);
        } catch (IllegalArgumentException e) {
            log.error("Failed to convert value to: {}", clazz.getName(), e);
            return null;
        }
    }

    /**
     * 객체를 다른 타입의 객체로 변환 (TypeReference 사용)
     *
     * @param object        원본 객체
     * @param typeReference 타입 참조
     * @param <T>           반환 타입
     * @return 변환된 객체, 변환 실패 시 null
     */
    public static <T> T convertValue(Object object, TypeReference<T> typeReference) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.convertValue(object, typeReference);
        } catch (IllegalArgumentException e) {
            log.error("Failed to convert value with TypeReference", e);
            return null;
        }
    }

    /**
     * JSON 문자열 유효성 검사
     *
     * @param json JSON 문자열
     * @return 유효한 JSON이면 true, 그렇지 않으면 false
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }
        try {
            objectMapper.readTree(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 두 객체를 JSON으로 비교하여 동일한지 확인
     *
     * @param obj1 첫 번째 객체
     * @param obj2 두 번째 객체
     * @return JSON 표현이 동일하면 true, 그렇지 않으면 false
     */
    public static boolean isJsonEqual(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        String json1 = toJson(obj1);
        String json2 = toJson(obj2);
        return json1 != null && json1.equals(json2);
    }

    /**
     * 객체를 깊은 복사 (Deep Copy)
     *
     * @param object 복사할 객체
     * @param clazz  객체 클래스 타입
     * @param <T>    반환 타입
     * @return 복사된 객체, 복사 실패 시 null
     */
    public static <T> T deepCopy(T object, Class<T> clazz) {
        if (object == null) {
            return null;
        }
        String json = toJson(object);
        return fromJson(json, clazz);
    }
}
