package com.example.demo.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jasypt 암호화 설정 클래스
 *
 * 민감한 정보(DB 비밀번호, API 키 등)를 암호화하여 properties/yml 파일에 저장할 수 있도록 합니다.
 *
 * 사용법:
 * 1. 암호화할 값 생성:
 *    java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI
 *         input="암호화할텍스트" password="암호화키" algorithm=PBEWithMD5AndDES
 *
 * 2. application.yml에 암호화된 값 설정:
 *    spring:
 *      datasource:
 *        password: ENC(암호화된문자열)
 *
 * 3. 애플리케이션 실행 시 암호화 키 전달:
 *    java -jar app.jar --jasypt.encryptor.password=암호화키
 *    또는 환경변수: JASYPT_ENCRYPTOR_PASSWORD=암호화키
 */
@Configuration
public class JasyptConfig {

    @Value("${jasypt.encryptor.password:defaultPassword}")
    private String encryptorPassword;

    @Value("${jasypt.encryptor.algorithm:PBEWithMD5AndDES}")
    private String algorithm;

    @Value("${jasypt.encryptor.key-obtention-iterations:1000}")
    private String keyObtentionIterations;

    @Value("${jasypt.encryptor.pool-size:1}")
    private String poolSize;

    @Value("${jasypt.encryptor.provider-name:SunJCE}")
    private String providerName;

    @Value("${jasypt.encryptor.salt-generator-classname:org.jasypt.salt.RandomSaltGenerator}")
    private String saltGeneratorClassname;

    @Value("${jasypt.encryptor.string-output-type:base64}")
    private String stringOutputType;

    /**
     * Jasypt String Encryptor Bean 생성
     *
     * @return StringEncryptor 인스턴스
     */
    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        config.setPassword(encryptorPassword);
        config.setAlgorithm(algorithm);
        config.setKeyObtentionIterations(keyObtentionIterations);
        config.setPoolSize(poolSize);
        config.setProviderName(providerName);
        config.setSaltGeneratorClassName(saltGeneratorClassname);
        config.setStringOutputType(stringOutputType);

        encryptor.setConfig(config);

        return encryptor;
    }
}
