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
import org.apache.commons.lang3.CharUtils;

import java.util.Random;

/**
 * 랜덤한 문자열(영문, 한글)을 생성하는 기능을 제공한다.<br>
 * 랜덤한 문자열은 길이 지정, 특정 범위내의 문자들만을 생성하는 등의 부가적인 기능을 가진다.
 * 
 * @author tw.jang
 * @since 1.0.0
 */
@UtilityClass
public final class RandomUtils {
	
	private static final char[] ALPHAS = new char[] {'A', 'B', 'C', 'D', 'E',
		'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
		'S', 'T', 'U', 'X', 'Y', 'V', 'W', 'Z', 'a', 'b', 'c', 'd', 'e',
		'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
		's', 't', 'u', 'x', 'y', 'v', 'w', 'z' };

	private static final Random GENERATOR = new Random(System.currentTimeMillis());
	
	
	/** 랜덤한 문자열 생성에 사용 INT */
	private static final int RANDOM_ALPHABETIC_INT = 52;
	
	/** 랜덤한 한글문자열 생성에 사용 INT */
	private static final int RANDOM_STR_KR_INT = 11172;
	
	/** 랜덤한 한글문자열 생성에 사용 HEX */
	private static final int RANDOM_STR_KR_HEX = 0xAC00;
	
	/**
	 * 최소, 최대 자리수 사이의 랜덤한 문자열을 반환한다.<br><br>
	 *
	 * RandomUtils.getString(10, 15) = "jRTwRnLzSsOWC"
	 *
	 * @param minSize 최소 자리수
	 * @param maxSize 최대 자리수
	 * @return 최소, 최대 자리수 사이의 랜덤한 문자열
	 */
	public static String getString(int minSize, int maxSize) {
		Random generator = new Random(System.currentTimeMillis());
		int randomLength = generator.nextInt(maxSize - minSize) + minSize;

		return randomAlphabetic(randomLength);
	}

	/**
	 * 특정한 길이 만큼의 랜덤한 문자열을 반환한다.<br><br>
	 *
	 * RandomUtils.getString(8) = "ikwblpTL"
	 *
	 * @param count 원하는 문자열 길이
	 * @return 특정 길이 만큼의 랜덤한 문자열
	 */
	public static String getString(int count) {
		return randomAlphabetic(count);
	}
	
	private static String randomAlphabetic(int randomLength) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < randomLength; i++) {
			buf.append(ALPHAS[GENERATOR.nextInt(RANDOM_ALPHABETIC_INT)]);
		}
		return buf.toString();
	}

	/**
	 * 특정한 알파벳 사이의 지정된 길이만큼의 랜덤한 문자열을 반환한다.<br>
	 * ASCII CODE값으로 계산하므로 startChar가 소문자이면 endChar도 소문자이어야한다.<br><br>
	 *
	 * RandomUtils.getString(10, 'B', 'r') = FoOXjRmmMr
	 *
	 * @param count 원하는 문자열 길이
	 * @param startChar 시작 알파벳
	 * @param endChar 끝 알파벳
	 * @return 특정 알파벳 사이의 랜덤한 문자열
	 */
	public static String getString(int count, String startChar, String endChar) {
		
		int startInt = Integer.valueOf(CharUtils.toChar(startChar));
		int endInt = Integer.valueOf(CharUtils.toChar(endChar));

		int gap = endInt - startInt;
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < count; i++) {
			int chInt;
			do {
				chInt = GENERATOR.nextInt(gap + 1) + startInt;
			} while (!Character.toString((char) chInt).matches("^[a-zA-Z]$"));
			buf.append((char) chInt);
		}
		return buf.toString();
	}
	
	/**
	 * 특정한 길이만큼의 한글 문자열을 반환한다.<br><br>
	 *
	 * RandomUtils.getKorString(20) = 깠훉똷휚쁗탓쏾퀺즭릮탴욗텓뻍퍵삽툄향쁄쌪
	 *
	 * @param count 원하는 문자열 길이
	 * @return 특정 길이 만큼의 랜덤한 한글 문자열
	 */
	public static String getKorString(int count) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < count; i++) {
			buf.append((char) (GENERATOR.nextInt(RANDOM_STR_KR_INT) + RANDOM_STR_KR_HEX));
		}
		return buf.toString();
	}

	/**
	 * 특정한 길이만큼의 주어진 캐릭터 셋 문자열을 반환한다.<br><br>
	 *
	 * RandomUtils.getStringByCharset(20, "UTF-8") = lBiwHUIoFqweOFrtDokI
	 *
	 * @param count 원하는 문자열 길이
	 * @param charset 캐릭터 셋
	 * @return 특정한 길이만큼의 캐릭터 셋 문자열
	 */
	public static String getStringByCharset(int count, String charset) {
		String randomStr = getString(count);
		return DigestUtils.encodeCharset(randomStr, charset);
	}
	
}
