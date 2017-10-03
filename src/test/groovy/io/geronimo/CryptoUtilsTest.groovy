package io.geronimo

import spock.lang.Shared
import spock.lang.Specification

class CryptoUtilsTest extends Specification {

	@Shared	String key
	
	def "generateHexDESKey() :: DESKey에 대한 유효성 체크"() {
		
		when:
			key = CryptoUtils.generateHexDESKey()
		then:
			!key.isEmpty()
			key.size() == 48
	}
	
	def "encryptByDES() :: return -> byte[]"() {
		
		setup:
			byte[] result
			def byteArray = [10, 20, 15, 40] as byte[]

		when:
			result = CryptoUtils.encryptByDES(key, byteArray)
		then:
			result instanceof byte[]
			
		when:
			result = CryptoUtils.encryptByDES(key, null)
		then:
			result == null
		when:
			result = CryptoUtils.encryptByDES(null, byteArray)
		then:
			result == null
	}
	
	def "encryptByDES() :: return -> String"() {
		
		setup:
			String result
			def data = 'CryptoUtils TEST'
			def charset = 'UTF-8'

		when:
			result = CryptoUtils.encryptByDES(key, data, charset)
		then:
			result instanceof String
			result.size() == 48
			
		when:
			result = CryptoUtils.encryptByDES(key, null)
		then:
			result == null
			
		when:
			result = CryptoUtils.encryptByDES(null, data, charset)
		then:
			result == null
	}
	
	def "암호화 후 복호화 수행하여 작업 성공여부 확인"() {
		
		setup:
			def data = 'manasobi CryptoUtils TEST'
			def charset = 'UTF-8'
		
		when:
			def encrtypedData = CryptoUtils.encryptByDES(key, data, charset)
			def decryptedData = CryptoUtils.decryptByDES(key, encrtypedData, charset)
		then:
			data == decryptedData
		
		when:
			def encrtypedBytes = CryptoUtils.encryptByDES(key, data.bytes)
			def decryptedBytes = CryptoUtils.decryptByDES(key, encrtypedBytes)
		then:
			data.bytes == decryptedBytes
	}
	
}
