package io.geronimo

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class FileUtilsTest extends Specification {
	
	@Rule
	TemporaryFolder tempFolder = new TemporaryFolder();
	
	def "byteCountToDisplaySize() :: byte단위의 파일 사이즈를 읽기 쉽도록 EB, PB, TB, GB, MB, KB, bytes 단위로 파싱하여 반환"() {
		
		setup:
			File tempFile = tempFolder.newFile('temp_file')
			tempFile << 'FileUtils byteCountToDisplaySize()'
		
		when:
			String fileSize = FileUtils.byteCountToDisplaySize(tempFile)
		then:
			fileSize == '34 bytes'
			
		expect:
			FileUtils.byteCountToDisplaySize(size) == result
		where:
			size 	  || result
			1024768   || '1000 KB'
			3000 	  || '2 KB'
			456789000 || '435 MB'	
	}

	/*def "makeZipFile() :: 대상 String 파일들을 ZIP FILE로 생성"() {

		setup:
		String zipFile = "/report/working/20151119_report.zip"
		String[] targetFiles = ["/report/working/impression_log_report-2015111900.csv", "/report/working/impression_log_report-2015111901.csv", "/report/working/impression_log_report-2015111902.csv"]

		when:
		FileUtils.Result result = FileUtils.makeZipFile(zipFile, targetFiles)
		then:
		result == FileUtils.Result.SUCCESS
	}

	def "makeZipFile() :: 대상 FILE객체 파일들을 ZIP FILE로 생성"() {

		setup:
		String zipFile = "/report/working/20151119_report.zip"
		File[] targetFiles = [new File("/report/working/impression_log_report-2015111902.csv"), new File("/report/working/impression_log_report-2015111901.csv"), new File("/report/working/impression_log_report-2015111900.csv")]

		when:
		FileUtils.Result result = FileUtils.makeZipFile(zipFile, targetFiles)
		then:
		result == FileUtils.Result.SUCCESS
	}

	def "listFilesByWildcard() :: 지정한 와일드카드가 포함된 파일들에 대해 File 타입의 배열로 가져온다."() {

		setup:
		String dir = "/report/working/"
		String[] wildcards = ["*20151119*.csv"]
		boolean recursive = false;
		File[] orgFiles =  [new File("/report/working/impression_log_report-2015111902.csv"), new File("/report/working/impression_log_report-2015111901.csv"), new File("/report/working/impression_log_report-2015111900.csv")]

		when:
		File[] files = FileUtils.listFilesByWildcard(dir, wildcards, recursive)
		then:
		orgFiles.size() == files.size()
	}*/

//
	
	/*def "cleanDir() :: 루트 디렉토리 내의 파일 및 하위 디렉토리를 삭제 [루트 디렉토리는 삭제하지 않음]"() {
		
		setup:
		
			new File("$TestUtils.resultsetDir/utils/temp.dat").createNewFile()
		
			def cleanDirPath = "$TestUtils.resultsetDir/utils/"
			def cleanDir = new File(cleanDirPath)
			
			cleanDir.eachFileRecurse(FileType.FILES) {
				it.delete()
			}
			
			def fileNames = ['cleanDir_1', 'cleanDir_2', 'cleanDir_3']
			
			fileNames.each {
				def file = new File("$cleanDir/$it" + ".txt")
				file << 'cleanDir() 테스트를 위한 더미 파일'
			}
			
		when:
			Status cleanResult = FileUtils.cleanDir(cleanDir)
		then:
			cleanResult == Status.SUCCESS
			cleanDir.list().size() == 0
			
			new File("$TestUtils.resultsetDir/utils/temp.dat").createNewFile()
			
		expect:
			FileUtils.cleanDir(dir) == result			
		where:
			dir 							 		  || result
			new File('C:/test/cleanDir/notExistsDir') || Status.FAIL // 존재하지 않는 디렉토리
			new File('C:/test/cleanDir/notDir.txt')   || Status.FAIL // 존재하지 않는 파일
			'C:/test/cleanDir/notExistsDir' 		  || Status.FAIL // 존재하지 않는 디렉토리
			'C:/test/cleanDir/notDir.txt' 			  || Status.FAIL // 존재하지 않는 파일
	}*/
	
	/*def cleanupSpec() {
		
		def fileNames = ['crypto/readme', 'filegateway/readme', 'pdftools/readme', 'utils/readme']
		
		fileNames.each {
			def file = new File("$TestUtils.resultsetDir/$it")
			file << 'git에서 빈 폴더가 commit되지 않는 관계로 폴더 생성을 위한 더미 파일을 추가.'
		}
	}*/

}



/*
def f1= new File('File1.txt')
f1 << 'abcdefg'*/