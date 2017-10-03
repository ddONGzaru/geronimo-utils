/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.geronimo;

import kr.re.nsri.aria.ARIACipher;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;

/**
 * DES방식을 이용한 암호화/복호화 기능 제공 Utils.
 * 
 * @author tw.jang
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public final class CryptoUtils {

    /** 암호화에 사용되는 알고리즘 - 기본 TripleDES */
    private static final String ALGORITHM = "DESede";

    private static final String ARIA_KEY = "emfladosxmvkzmtmxhdj";

    private static ARIACipher ariaCipher;

    /**
     * 암호화에 사용할 키를 생성한다. 생성된 키를 프로그램의 설정 파일등에 저장해서 계속해서 사용하면 된다.
     *
     * @return 자동 생성된 키를 Hex 문자열로 바꾼 값
     */
    public static String generateHexDESKey() {
    	
        byte[] rawKey;
        
        try {
            
        	KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            
        	SecretKey secretKey = keyGenerator.generateKey();
            
        	SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            
        	DESedeKeySpec desEdeSpec = (DESedeKeySpec) secretKeyFactory.getKeySpec(secretKey, DESedeKeySpec.class);

            rawKey = desEdeSpec.getKey();
            
        } catch (Exception e) {
        	
        	log.error("CryptoUtils-generateHexDESKey :: {}", e.getMessage());
        	return null;
        }
        
        return new String(Hex.encodeHex(rawKey));
    }

    /**
     * 암호화를 수행한다.
     *
     * @param keyHex generateHexKey 메소드에 의해 생성된 Hex 문자열화 된 키
     * @param data   암호화할 데이터 byte 배열
     * 
     * @return 암화화 된 데이터
     */
    public static byte[] encryptByDES(String keyHex, byte[] data) {
    	
        SecretKey key = getSecretDESKeyFromHex(keyHex);
        
        byte[] encryptedData;

        try {
        	
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            
            cipher.init(Cipher.ENCRYPT_MODE, key);
            
            encryptedData = cipher.doFinal(data);
            
        } catch (Exception e) {

            log.error("CryptoUtils-encryptByDES :: {}", e.getMessage());
            return null;
        }
        
        return encryptedData;
    }

    /**
     * 문자열 데이터를 받아서 암호화 한 뒤에 그 결과를 문자열로 바꾸어서 리턴한다.
     *
     * @param keyHex   generateHexKey 메소드에 의해 생성된 Hex 문자열화 된 키
     * @param data     암호화할 데이터 문자열
     * @param encoding 문자열 인코딩
     * 
     * @return 암호화 된 데이터를 문자열로 변환한 결과
     */
    public static String encryptByDES(String keyHex, String data, String encoding) {

        String encryptedString;
        
        try {
        	
            byte[] encryptedData = encryptByDES(keyHex, data.getBytes(encoding));
            
            encryptedString = new String(Hex.encodeHex(encryptedData));
            
        } catch (Exception e) {

            log.error("CryptoUtils-encryptByDES :: {}", e.getMessage());
            return null;
        }
        
        return encryptedString;
    }

    /**
     * 복호화를 수행한다.
     *
     * @param keyHex generateHexKey 메소드에 의해 생성된 Hex 문자열화 된 키
     * @param data   복호화할 바이트 문자열
     *             
     * @return 복호화된 데이터 byte 배열
     */
    public static byte[] decryptByDES(String keyHex, byte[] data) {
    	
        SecretKey key = getSecretDESKeyFromHex(keyHex);
        
        byte[] decryptedData = null;

        try {
        	
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            
            cipher.init(Cipher.DECRYPT_MODE, key);
            
            decryptedData = cipher.doFinal(data);

        } catch (Exception e) {

            log.error("CryptoUtils-decryptByDES :: {}", e.getMessage());
            return null;
        }

        return decryptedData;
    }

    /**
     * 암호화된 문자열을 받아 복호화하여, 원본 문자열을 반환한다.
     *
     * @param keyHex   generateHexKey 메소드에 의해 생성된 Hex 문자열화 된 키
     * @param data     복호화할 바이트 문자열
     * @param encoding 문자열 인코딩
     * 
     * @return 복호화된 문자열
     */
    public static String decryptByDES(String keyHex, String data, String encoding) {
    	
        String decryptedString = null;
        try {
        	
            byte[] unhexedData = Hex.decodeHex(data.toCharArray());
            
            byte[] decryptedData = decryptByDES(keyHex, unhexedData);
            
            decryptedString = new String(decryptedData, encoding);

        } catch (Exception e) {

            log.error("CryptoUtils-decryptByDES :: {}", e.getMessage());
            return null;
        }
        
        return decryptedString;
    }

    /**
     * hex 문자열화된 키에서 SecretKey 객체를 생성한다.
     *
     * @param keyHex generateHexKey 메소드에 의해 생성된 Hex 문자열화 된 키
     * 		
     * @return 생성된 SecretKey 객체
     */
    private static SecretKey getSecretDESKeyFromHex(String keyHex) {

    	SecretKey key = null;

        try {
        	
            byte[] keyBytes = Hex.decodeHex(keyHex.toCharArray());
            
            key = new SecretKeySpec(keyBytes, ALGORITHM);
            
        } catch (Exception e) {

            log.error("CryptoUtils-getSecretDESKeyFromHex :: {}", e.getMessage());
            return null;
        }
        
        return key;
    }

    public static String encryptByAria(String data) {

        ARIACipher ariaCipher = getARIACipher();

        try {
            return ariaCipher.encryptString(data);
        } catch (InvalidKeyException e) {
            log.error("CryptoUtils-encryptByAria :: {}", e.getMessage());
            return null;
        }

    }

    public static String decryptByAria(String data) {

        ARIACipher ariaCipher = getARIACipher();

        try {
            return ariaCipher.decryptString(data);
        } catch (InvalidKeyException e) {
            log.error("CryptoUtils-decryptByAria :: {}", e.getMessage());
            return null;
        }

    }

    private static ARIACipher getARIACipher() {

        if (ariaCipher == null) {
            try {
                ariaCipher = new ARIACipher(ARIA_KEY);
            } catch (InvalidKeyException e) {
                log.error("CryptoUtils-getARIACipher :: {}", e.getMessage());
                return null;
            }
        }

        return ariaCipher;
    }

}
