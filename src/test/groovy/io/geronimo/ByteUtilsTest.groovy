package io.geronimo

import spock.lang.Specification

class ByteUtilsTest extends Specification {
	
	def "equals() :: 바이트 배열 비교"() {
		
		expect:	
			ByteUtils.equals(array1, array2) == result	
		where:
			array1                   | array2                   || result	
			[21, -81, 100] as byte[] | [21, -81, 100] as byte[] || true
			null					 | null						|| true
			[21, -81, 100] as byte[] | [-81, 21, 100] as byte[] || false
			[21, -81, 100] as byte[] | null 					|| false
			null					 | [-81, 21, 100] as byte[] || false
			[21, -81, 100] as byte[] | [21, -81] as byte[]      || false
	}
	
	def "hexStringToBytes() :: Hex 문자열을 byte 배열로 변환"() {
		
		expect:	
			ByteUtils.hexStringToBytes(digit) == result	
		where:
			digit  || result
			//성공	
			'15AF' || [21, -81] as byte[]
			null   || null
	}
	
	def "hexStringToBytes() :: Hex 문자열을 byte 배열로 변환 - 예외처리"() {

		when: '16진수 문자열이 아님'			
			ByteUtils.hexStringToBytes('15AG')
		then:
			thrown(NumberFormatException)

		when: '글자 수 틀림 (문자열 2자리가 1개의 byte로 바뀌는데 문자열이 3개)'
			byte[] bytes = ByteUtils.hexStringToBytes('15A')
		then:
			bytes == null
	}
	
	def "printPrettyHex() 테스트"() {
		
		setup:
			byte[] bytes = [
				21, -81, 21, -81,
				21, -81, 21, -81,
				21, -81, 21, -81,
				21, -81, 21, -81,
				21, -81, 21, -81,
				21, -81, 21, -81
			]		
		
		when: 'String을 대문자로 변환하고 공백을 제거후 검증'
			def result = ByteUtils.printPrettyHex(bytes).toUpperCase().replace(" ", "")
		then:
			result == '15AF15AF15AF15AF15AF15AF15AF15AF\r\n15AF15AF15AF15AF'
	}
	
	def "toBytes() :: getLittleEndianInt와 같으나 출력되는 순서가 역순"() {
		
		/*
		  ex) getLittleEndianInt(n)	= [20, 30, 10, 40]
		   	  toBytes(n)			= [40, 10, 30, 20]
		  
	   	  데이터를 byte배열로 변환 (1*21) + (256*175) + (65536*100) + (16777216*2)
		   	  
		*/
		expect:
			ByteUtils.toBytes(value) == result
		where:
			value    		 || result
			40152853 		 || [2, 100, -81, 21] as byte[]
			40152853 as Long || [0, 0, 0, 0, 2, 100, -81, 21] as byte[]
	}
	
	def "toBytes() :: hexStringToBytes과 같으나 radix를 기준으로 변환"() {
		
		expect:
			ByteUtils.toBytes(digit, radix) == result
		where:
			digit    | radix || result
			'144025' | 8 	 || [100, 21] as byte[]
			'144025' | 10 	 || [-112, 25] as byte[]
			'144025' | 16 	 || [20, 64, 37] as byte[]			
			null 	 | 16 	 || null
	}
	
	def "toBytes() :: hexStringToBytes과 같으나 radix를 기준으로 변환 [예외: 부적절한 digit]"() {
		
		setup: '부적절한 digit'
			String digit = '144'
		
		when:
			ByteUtils.toBytes(digit, 16) == exception
		then:
			thrown(IllegalArgumentException)
	}
	
	def "toBytes() :: hexStringToBytes과 같으나 radix를 기준으로 변환 [예외: 부적절한 radix]"() {
		
		setup: '부적절한 radix'
			String radix = 2
		
		when:
			ByteUtils.toBytes('144025', 2) == exception
		then:
			thrown(IllegalArgumentException)
	}
	
	def "toBytesFromHexString() :: String을 byte[]배열로 변환 [testHexStringToBytes, toBytes와 같음]"() {
		
		expect:
			ByteUtils.toBytesFromHexString(value) == result
		where:
			value  || result
			'15AF' || [21, -81] as byte[]
			null   || null
	}

	def "toBytesFromHexString() :: String을 byte[]배열로 변환 예외 [문자 길이가 잘못됨]"() {
		
		setup: '부적절한 digit'
			String digit = '15A'
		
		when:
			ByteUtils.toBytesFromHexString(digit)
		then:
			thrown(IllegalArgumentException)
	}

	def "toBytesFromHexString() :: String을 byte[]배열로 변환 예외 [잘못된 16진수 문자열]"() {
		
		setup: '부적절한 digit'
			String digit = '15AG'
			
		when:
			ByteUtils.toBytesFromHexString(digit)
		then:
			thrown(NumberFormatException)
	}
	
	def "toHexString() :: byte 또는 byte배열을 16진수의 String으로 변환 [printPrettyHex와 같음]"() {
		
		setup:
			String result
		
		when:
			result = ByteUtils.toHexString(21 as byte)	
		then:
			result == '15'
			
		when:
			result = ByteUtils.toHexString([21, -81] as byte[]).toUpperCase()
		then:
			result == '15AF'

		when:
			result = ByteUtils.toHexString([21, -81] as byte[], 0, 2).toUpperCase()
		then:
			result == '15AF'
			
		when:
			result = ByteUtils.toHexString(null)
		then:
			result == null

		when:
			result = ByteUtils.toHexString(null, 0, 2)
		then:
			result == null
			
	}
	
	def "toInt() :: 4바이트 byte배열을 int형으로 변환"() {
		
		when:
			byte[] bytes = [2, 100, -81, 21]
			int result = ByteUtils.toInt(bytes)
		then:
			40152853 == result
	}
	
	def "toLong() :: 8바이트 byte배열을 long형으로 변환"() {
		
		when:
			byte[] bytes = [0, 0, 0, 0, 2, 100, -81, 21]
			long result = ByteUtils.toLong(bytes)
		then:
			Long.valueOf(40152853) == result
	}
	
	def "toString() :: byte배열을 String형으로 변환"() {
		
		setup:
			byte[] byteArray = [104, 101, 108, 108, 111]
		
		when:
			def result = ByteUtils.toString(byteArray)
		then:
			result == 'hello'	
			
		when:
			result = ByteUtils.toString(byteArray, 'UTF-8')
		then:
			result == 'hello'
			
		when:
			result = ByteUtils.toString(byteArray, 'invalid chartset')
		then:
			result == null
			
		when:
			result = ByteUtils.toString(null)
		then:
			final NullPointerException e2 = thrown()
	} 
	
	def "unsignedByte() :: byte를 int(unsigned)로 변환"() {
		
		when:
			int result = ByteUtils.unsignedByte(-81 as byte)
		then:
			result == 175		
	}
	
}
