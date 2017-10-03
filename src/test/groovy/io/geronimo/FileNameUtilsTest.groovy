package io.geronimo

import spock.lang.Specification

class FileNameUtilsTest extends Specification {

	/*def "getPath() :: 파일 명으로부터 prefix를 제외한 패스를 반환"() {
		
		expect:
			FileNameUtils.getPath(fileName) == result
		where:
			fileName 				   || result
			null 					   || null
			''				   	   	   || ''
			'C:/a/b/c.txt'			   || 'a/b/'
			'C:\\a\\b\\c.txt'		   || 'a\\b\\'
			'~/a/b/c.txt'			   || 'a/b/'
			'a/b/c.txt'				   || 'a/b/'
			'a/b/c' 				   || 'a/b/'
			'a/b/c/' 				   || 'a/b/c/'
	}

	def "getPathNoEndSeparator() :: 파일 명으로부터 prefix 및 마지막 디렉토리 구분자를 제외한 패스를 반환"() {
		
		expect:
			FileNameUtils.getPathNoEndSeparator(fileName) == result
		where:
			fileName 				   || result
			null 					   || null
			''				   	   	   || ''
			'C:/a/b/c.txt'			   || 'a/b'
			'C:\\a\\b\\c.txt'		   || 'a\\b'
			'~/a/b/c.txt'			   || 'a/b'
			'a/b/c.txt'				   || 'a/b'
			'a/b/c' 				   || 'a/b'
			'a/b/c/' 				   || 'a/b/c'
	}

	def "getFullPath() :: 파일 명으로부터 prefix 및 마지막 디렉토리 구분자를 제외한 패스를 반환"() {
		
		expect:
			FileNameUtils.getFullPath(fileName) == result
		where:
			fileName 				   || result
			null 					   || null
			''				   	   	   || ''
			'C:/a/b/c.txt'			   || 'C:/a/b/'
			'C:\\a\\b\\c.txt'		   || 'C:\\a\\b\\'
			'~/a/b/c.txt'			   || '~/a/b/'
			'a/b/c.txt'				   || 'a/b/'
			'a/b/c' 				   || 'a/b/'
			'a/b/c/' 				   || 'a/b/c/'
	}

	def "getFullPathNoEndSeparator() :: 파일 명으로부터 prefix 및 마지막 디렉토리 구분자를 제외한 패스를 반환"() {
		
		expect:
			FileNameUtils.getFullPathNoEndSeparator(fileName) == result
		where:
			fileName 				   || result
			null 					   || null
			''				   	   	   || ''
			'C:/a/b/c.txt'			   || 'C:/a/b'
			'C:\\a\\b\\c.txt'		   || 'C:\\a\\b'
			'~/a/b/c.txt'			   || '~/a/b'
			'a/b/c.txt'				   || 'a/b'
			'a/b/c' 				   || 'a/b'
			'a/b/c/' 				   || 'a/b/c'
	}

	def "getName() :: 파일 이름으로부터 패스를 제외한 파일 명만 반환"() {
		
		expect:
			FileNameUtils.getName(fileName) == result
		where:
			fileName 				   || result
			null 					   || null
			''				   	   	   || ''
			'C:/a/b/c.txt'			   || 'c.txt'
			'C:\\a\\b\\c.txt'		   || 'c.txt'
			'~/a/b/c.txt'			   || 'c.txt'
			'a/b/c.txt'				   || 'c.txt'
	}

	def "getBaseName() :: 파일 이름으로부터 패스와 확장자를 제외한 파일 명만 반환"() {
		
		expect:
			FileNameUtils.getBaseName(fileName) == result
		where:
			fileName 				   || result
			null 					   || null
			''				   	   	   || ''
			'C:/a/b/c.txt'			   || 'c'
			'C:\\a\\b\\c.txt'		   || 'c'
			'/a/b/c'			   	   || 'c'
			'/a/b/c/'				   || ''
	}

	def "getExtension() :: 파일 이름으로부터 확장자를 반환"() {
		
		expect:
			FileNameUtils.getExtension(fileName) == result
		where:
			fileName 				   || result
			null 					   || null
			''				   	   	   || ''
			'foo.txt'			   	   || 'txt'
			'a/b/c.jpg'		   		   || 'jpg'
			'C:/a/b/c.bmp'		   	   || 'bmp'
			'C:\\a\\b\\c.png'	   	   || 'png'
			'a/b.txt/c'			   	   || ''
			'a/b/c'				   	   || ''
	}
	
	def "removeExtension() :: 파일 이름으로부터 확장자를 제외한 파일 명만 반환"() {
		
		expect:
			FileNameUtils.removeExtension(fileName) == result
		where:
			fileName 				   || result
			null 					   || null
			''				   	   	   || ''
			'foo.txt'			   	   || 'foo'
			'a/b/c.jpg'		   		   || 'a/b/c'
			'C:/a/b/c.bmp'		   	   || 'C:/a/b/c'
			'C:\\a\\b\\c.png'	   	   || 'C:\\a\\b\\c'
			'a/b.txt/c'			   	   || 'a/b.txt/c'
			'a/b/c'				   	   || 'a/b/c'
	}

	def "getPathWithoutPrefix() :: URI 문자열에서 URI 프로토콜을 제외한 부분을 반환"() {

		when:
			FileNameUtils.getPathWithoutPrefix(null)
		then:
			final NullPointerException exception = thrown()
		
		expect:
			FileNameUtils.getPathWithoutPrefix(fileName) == result
		where:
			fileName 		   || result
			''				   || ''
			'file://C:/Users/' || 'C:/Users/'
			'ftp://C:/Users/'  || 'C:/Users/'
			'sftp://C:/Users/' || 'C:/Users/'
			'smb://C:/Users/'  || 'C:/Users/'
	}
	
	def "separatorsToUnix() :: 모든 구분자를 UNIX 계열 구분자인 '/'로 치환"() {
		
		expect:
			FileNameUtils.separatorsToUnix(fileName) == result
		where:
			fileName 		   			|| result
			null 						|| null
			''				   			|| ''
			'file://C:/Users/' 			|| 'file://C:/Users/'
			'C:\\temp\\foo.bar\\README' || 'C:/temp/foo.bar/README'
			'C:\\temp/README.txt' 		|| 'C:/temp/README.txt'
			'C:/temp\\README.txt' 		|| 'C:/temp/README.txt'
	}

	def "separatorsToWindows() :: 모든 구분자를 WINDOWS 계열 구분자인 '\'로 치환"() {
		
		expect:
			FileNameUtils.separatorsToWindows(fileName) == result
		where:
			fileName 		   			|| result
			null 						|| null
			''				   			|| ''
			'file://C:/Users/' 			|| 'file:\\\\C:\\Users\\'
			'C:\\temp\\foo.bar\\README' || 'C:\\temp\\foo.bar\\README'
			'C:\\temp/README.txt' 		|| 'C:\\temp\\README.txt'
			'C:/temp\\README.txt' 		|| 'C:\\temp\\README.txt'
	}
	
	def "separatorsToSystem() :: 모든 구분자를 OS 설정에 따른 구분자로 치환"() {
		
		expect:
			FileNameUtils.separatorsToSystem(fileName) == result
		where:
			fileName 		   			|| result
			null 						|| null
			''				   			|| ''
			'file://C:/Users/' 			|| 'file:\\\\C:\\Users\\'
			'C:\\temp\\foo.bar\\README' || 'C:\\temp\\foo.bar\\README'
			'C:\\temp/README.txt' 		|| 'C:\\temp\\README.txt'
			'C:/temp\\README.txt' 		|| 'C:\\temp\\README.txt'
	}

	def "appendUnixFileSeparator() ::  경로 가장뒤에 Unix계열 구분자(/)를 추가"() {
		
		when:
			FileNameUtils.appendUnixFileSeparator(null)
		then:
			final NullPointerException exception = thrown()
		
		expect:
			FileNameUtils.appendUnixFileSeparator(fileName) == result
		where:
			fileName 		   || result
			''				   || '/'
			'file.txt' 		   || 'file.txt/'
			'file://C:/Users/' || 'file://C:/Users/'
			'C:/foo\\' 		   || 'C:/foo/'
			'ftp:\\\\hello'    || 'ftp://hello/'
	}

	def "appendWindowsFileSeparator() :: 경로 가장뒤에 Windos계열 구분자(\\)를 추가"() {
		
		when:
			FileNameUtils.appendWindowsFileSeparator(null)
		then:
			thrown(NullPointerException)
				
		expect:
			FileNameUtils.appendWindowsFileSeparator(fileName) == result
		where:
			fileName 		   || result
			''				   || '\\'
			'file.txt' 		   || 'file.txt\\'
			'file://C:/Users/' || 'file:\\\\C:\\Users\\'
			'C:/foo\\' 		   || 'C:\\foo\\'
			'ftp:\\\\hello'    || 'ftp:\\\\hello\\'
	}
	
	def "appendFileSeparator() :: 경로 가장뒤에 OS 설정에 따른 구분자를 추가"() {
		
		when:
			FileNameUtils.appendFileSeparator(null)
		then:
			thrown(NullPointerException)
				
		expect:
			FileNameUtils.appendFileSeparator(fileName) == result
		where:
			fileName 		   || result
			''				   || '\\'
			'file.txt' 		   || 'file.txt\\'
			'file://C:/Users/' || 'file://C:/Users/\\'
			'C:/foo\\' 		   || 'C:/foo\\'
			'ftp:\\\\hello'    || 'ftp:\\\\hello\\'
	}*/
	
}

