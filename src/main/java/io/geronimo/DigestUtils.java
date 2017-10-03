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


import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;

/**
 * 해당하는 문자열에 대해서 characterset 또는 base64 등 기능을 사용해 인코딩/디코딩 하는 기능을 제공한다.
 * 
 * @author tw.jang
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public final class DigestUtils {
	
	/**
	 * 입력받은 문자열을 charsetName을 기준으로 인코딩한다.
	 * 
	 * @param str         인코딩할 문자열
	 * @param charsetName 인코딩시 사용할 캐릭터셋
	 * @return 인코딩된 문자열
	 */
	public static String encodeCharset(String str, String charsetName) {

		String result;

		try {
			result = new String(str.getBytes(charsetName), charsetName);
		} catch (Exception e) {
			log.error("DigestUtils-encodeCharset :: {}", e.getMessage());
			return null;
		}

		return result;
	}
	
	/**
	 * 입력받은 문자열을 Base64를 이용하여 인코딩한다.
	 * 
	 * @param str	    Base64 인코딩할 문자열
	 * @return Base64로 인코딩된 문자열
	 */
	public static String encodeBase64String(String str) {
		return org.apache.commons.codec.binary.Base64.encodeBase64String(str.getBytes());
	}
	
	/**
	 * 입력받은 바이너리 데이터를 Base64를 이용하여 인코딩한다.
	 * 
	 * @param binaryData Base64 인코딩할 binary data
	 * @return Base64로 인코딩된 문자열
	 */
	public static String encodeBase64String(byte[] binaryData) {
		return org.apache.commons.codec.binary.Base64.encodeBase64String(binaryData);
	}
	
	/**
	 * 입력받은 문자열을 Base64를 이용하여 인코딩한다.
	 * 
	 * @param str       Base64 인코딩할 문자열
	 * @return Base64로 인코딩된 byte array
	 */
	public static byte[] encodeBase64ByteArray(String str) {
		return org.apache.commons.codec.binary.Base64.encodeBase64(str.getBytes());
	}
	
	/**
	 * 입력받은 바이너리 데이터를 Base64를 이용하여 인코딩한다.
	 * 
	 * @param binaryData Base64 인코딩할 binary data
	 * @return Base64로 인코딩된 byte array
	 */
	public static byte[] encodeBase64ByteArray(byte[] binaryData) {
		return org.apache.commons.codec.binary.Base64.encodeBase64(binaryData);
	}
	
	/**
	 * 입력받은 문자열을 Base64를 이용하여 디코딩한다.
	 * 
	 * @param str Base64 디코딩할 문자열
	 * @return Base64로 디코딩된 문자열
	 */
	public static String decodeBase64String(String str) {
		return new String(org.apache.commons.codec.binary.Base64.decodeBase64(str));
	}
	
	/**
	 * 입력받은 binary data를 Base64를 이용하여 디코딩한다.
	 * 
	 * @param binaryData Base64 디코딩할 binary data
	 * @return Base64로 디코딩된 문자열
	 */
	public static String decodeBase64String(byte[] binaryData) {
		return new String(org.apache.commons.codec.binary.Base64.decodeBase64(binaryData));
	}
	
	/**
	 * 입력받은 문자열을 Base64를 이용하여 디코딩한다.
	 * 
	 * @param str Base64 디코딩할 문자열
	 * @return Base64로 디코딩된 byte array
	 */
	public static byte[] decodeBase64ByteArray(String str) {
		return org.apache.commons.codec.binary.Base64.decodeBase64(str);
	} 
	
	/**
	 * 입력받은 binary data를 Base64를 이용하여 디코딩한다.
	 * 
	 * @param binaryData Base64 디코딩할 binary data
	 * @return Base64로 디코딩된 byte array
	 */
	public static byte[] decodeBase64ByteArray(byte[] binaryData) {
		return org.apache.commons.codec.binary.Base64.decodeBase64(binaryData);
	}

	private static final int HEX_FF = 0xff;
	private static final int HEX_10 = 0x10;
	private static final int HEX = 16;
	
	public enum Secure {
		
		MD5("md5"),
		SHA_1("sha-1"),
		SHA_256("sha-256");
		
		private String algorithm;
		
		private Secure(String algorithm) {
			this.algorithm = algorithm;
		}

		public String getAlgorithm() {
			return algorithm;
		}
	}
	
	/**
	 * 패스워드를 입력받은 알고리즘을 사용하여 암호화한다.<br>
	 * 사용 가능한 알고리즘 - "MD5", "SHA-1", "SHA-256"<br>
	 * 알고리즘에 의해 암호화된 문자열의 길이 - MD5: 32자, SHA-1: 40자, SHA-256: 64자
	 * 
	 * @param password   암호화할 문자열
	 * @param secure  암호화시 사용할 알고리즘
	 * @return 알고리즘에 의해 암호화된 문자열
	 */
	public static String encodePassword(String password, Secure secure) {

		byte[] unencodedPassword = password.getBytes();

		MessageDigest md;

		try {
			md = MessageDigest.getInstance(secure.getAlgorithm());
		} catch (Exception e) {
			log.error("DigestUtils-encodePassword :: {}", e.getMessage());
			return null;
		}

		md.reset();
		md.update(unencodedPassword);

		byte[] encodedPassword = md.digest();

		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < encodedPassword.length; i++) {
			if (((int) encodedPassword[i] & HEX_FF) < HEX_10) {
				sb.append("0");
			}

			sb.append(Long.toString((int) encodedPassword[i] & HEX_FF, HEX));
		}

		return sb.toString();
	}
	
}
