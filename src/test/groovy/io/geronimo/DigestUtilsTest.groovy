package io.geronimo

import spock.lang.Specification


class DigestUtilsTest extends Specification {

	def "encodeCharset() :: 입력받은 문자열을 charset을 기준으로 인코딩"() {

		/*when:
			String nil = DigestUtils.encodeCharset('DigestUtils', 'no charset')
		then:
			nil == null*/
		
		expect:
			DigestUtils.encodeCharset(str, charset) == result
		where:
			str 		  | charset  || result
			'DigestUtils' | 'no charset'  || null
			'DigestUtils' | 'UTF-8'  || 'DigestUtils'
			'DigestUtils' | 'EUC-KR' || 'DigestUtils'
			'spock 짱' 	  | 'EUC-KR' || 'spock 짱'
	}
	
	def "encodeBase64String() :: Base64를 이용하여 인코딩, 디코딩 [반환 값이 String]"() {
		
		expect:
			DigestUtils.encodeBase64String(param1) == encodedStr
			DigestUtils.decodeBase64String(param2) == decodedStr
		where:
			param1 		  		| encodedStr  	     | param2 			  		|| decodedStr
			'DigestUtils' 		| 'RGlnZXN0VXRpbHM=' | 'RGlnZXN0VXRpbHM='   	|| 'DigestUtils' 	
			'DigestUtils'.bytes | 'RGlnZXN0VXRpbHM=' | 'RGlnZXN0VXRpbHM='.bytes || 'DigestUtils' 	
			'' 			 		| '' 				 | ''      					|| '' 	
			''.bytes	 		| '' 				 | ''.bytes					|| '' 	
	}

	def "encodeBase64ByteArray() :: Base64를 이용하여 인코딩, 디코딩 [반환 값이 ByteArray]"() {
		
		expect:
			DigestUtils.encodeBase64ByteArray(param1) == encodedStr
			DigestUtils.decodeBase64ByteArray(param2) == decodedStr
		where:
			param1 		  		| encodedStr  	     	   | param2 			  	  || decodedStr
			'DigestUtils' 		| 'RGlnZXN0VXRpbHM='.bytes | 'RGlnZXN0VXRpbHM='   	  || 'DigestUtils'.bytes 	
			'DigestUtils'.bytes | 'RGlnZXN0VXRpbHM='.bytes | 'RGlnZXN0VXRpbHM='.bytes || 'DigestUtils'.bytes 	
			'' 			 		| ''.bytes 				   | ''      				  || ''.bytes
			''.bytes	 		| ''.bytes 				   | ''.bytes				  || ''.bytes
	}

	def "encodePassword() :: 문자열을 입력받은 알고리즘을 사용하여 암호화"() {
		
		/*when:
			String result = DigestUtils.encodePassword('password', null)
		then:
			result == null*/
		
		expect: '사용 가능한 알고리즘 -> MD5, SHA-1, SHA-256' 
				'알고리즘에 의해 암호화된 문자열의 길이 -> MD5: 32자, SHA-1: 40자, SHA-256: 64자'
			DigestUtils.encodePassword(pwd, algorithm) == result
		where:
			pwd  	       | algorithm                   || result
			'password-md5' | null                       || null
			'password-md5' | DigestUtils.Secure.MD5     || '3240482b9b3ef8c65e9405e52454124b'
			'password-md5' | DigestUtils.Secure.SHA_1   || 'ae8f480905c8fb3b5433475bb70e54916666b005'
			'password-md5' | DigestUtils.Secure.SHA_256 || 'd6b8548bdeab02463708da61302797b51727ac044517f306899a191621cca271'
	}
	
}
