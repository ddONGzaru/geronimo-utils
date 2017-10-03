package io.geronimo

import spock.lang.Specification

/**
 * 입력된 일자로 부터 나이(실제 나이, 만 나이)를 알아내는 기능들을 제공하는 유틸 클래스의 JUnit 테스트 클래스
 * 
 * @author manasobi
 */
class AgeUtilsTest extends Specification {

	/**
	 * "yyyyMMdd" 문자열 형태로 입력된 생년월일과 기준일자를 바탕으로 만 나이를 구하는 메소드를 테스트한 성공케이스
	 */
	def "getAge()에 대한 성공 케이스"() {
		
		expect:
			AgeUtils.getAge(birthday, baseday) == age
		where:
			birthday   | baseday    || age
			
			// 테스트데이터작성 - 생일이 지나지 않은 경우
			'19740608' | '20140607' || 39
			
			// 테스트데이터작성 - 생일인이 경우
			'19740608' | '20140608' || 40
			
			// 테스트데이터작성 - 생일이 지난 경우
			'19740608' | '20140609' || 40
			
			// 테스트데이터작성 - age가 0일때
			'19740608' | '19740608' || 0
	}
	
	
	/**
	 "yyyyMMdd" 문자열 형태로 입력된 생년월일과 기준일자를 바탕으로 전통 나이를 구하는 메소드를 테스트한 성공케이스
     * 현재일자 기준 생일의 전후 구분 없이 현재년도 - 태어난 년도 + 1
	 */
	def "getKoreanAge()에 대한 성공 케이스"() {
		
		expect:
			AgeUtils.getKoreanAge(birthday, baseday) == age
		where:
			birthday   | baseday    || age
			'19740608' | '20140607' || 41
	}

}
