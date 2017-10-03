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

/**
 * 입력된 일자로 부터 나이(실제 나이, 만 나이)를 알아내는 기능들을 제공한다.
 * 
 * @author tw.jang
 * @since 1.0.0
 *
 */
@UtilityClass
public final class AgeUtils {
	
    /**
     * "yyyyMMdd" 문자열 형태로 입력된 생년월일과 기준일자를 바탕으로 만 나이를 구한다.<br><br>
     * 
     * AgeUtils.getFullAge("19740608", "20090607") // 생일이 지나지 않은 경우, age = 2009 - 1974 - 1 = 34<br>
     * AgeUtils.getFullAge("19740608", "20090608") // 생일인이 경우, age = 2009 - 1974 = 35<br>
     * AgeUtils.getFullAge("19740608", "20090609") // 생일이 지난 경우, age = 2009 - 1974 = 35
     * 
     * @param birthdate 생년월일
     * @param baseDate 기준일자
     * @return 만 나이
     */
    public static int getAge(String birthdate, String baseDate) {
    	
    	int age = Integer.parseInt(baseDate.substring(0, 4)) - Integer.parseInt(birthdate.substring(0, 4));

		if (age > 0) {
			
    		int birthDay = Integer.valueOf(birthdate.substring(4, 8));
    		int baseDay = Integer.valueOf(baseDate.substring(4, 8));   		
    		
    		if (birthDay > baseDay) {
    			age--;    			
    		}
		}

		return age;
    }
    
    /**
     * "yyyyMMdd" 문자열 형태로 입력된 생년월일과 기준일자를 바탕으로 전통 나이를 구한다.<br>
     * 현재일자 기준 생일의 전후 구분 없이 현재년도 - 태어난 년도 + 1<br><br>
     * 
     * AgeUtils.getAge("19740608", "20090607"); // age = 2009 - 1974 + 1 = 36
     * 
     * @param birthdate 생년월일
     * @param baseDate 기준일자
     * @return 한국식 나이
     */
    public static int getKoreanAge(String birthdate, String baseDate) {    	
   		return Integer.parseInt(baseDate.substring(0, 4)) - Integer.parseInt(birthdate.substring(0, 4)) + 1;    	
    }

}
