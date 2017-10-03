package io.geronimo

import spock.lang.Specification

import java.sql.Time
import java.sql.Timestamp
import java.text.SimpleDateFormat

class DateUtilsTest extends Specification {

	/*def "getNow()"() {
		
		when: 
			String datetime = DateUtils.now()
		then:
			datetime.size() == 19
			
		expect:
			DateUtils.getCurrentDateTime(dateFormat) == result
		where: 'datetime 비교는 매번 테스트가 실행될때마다 값이 바뀌므로 분(minute)까지만 측정하는 것을 전제로 함'
			dateFormat 					|| result
			'yyyy-MM-dd hh:mm' 			|| getCurDatetime('yyyy-MM-dd hh:mm')
			'yyyy년 MM월 dd일 hh시 mm분' || getCurDatetime('yyyy년 MM월 dd일 hh시 mm분')
	}
	
	def "getThisMonth()"() {
		
		when: 'getThisMonth()의 디폴트 포맷 -> yyyy-MM' 
			String datetime = DateUtils.getThisMonth()
		then:
			datetime.size() == 7
			datetime == getCurDatetime('yyyy-MM')
			 
		expect:
			DateUtils.getThisMonth(dateFormat) == result
		where: 
			dateFormat 	  || result
			'yyyy-MM' 	  || getCurDatetime('yyyy-MM')
			'yyyy년 MM월' || getCurDatetime('yyyy년 MM월')
	}
	
	def "getThisYear()"() {
		
		when: 'getThisYear()의 디폴트 포맷 -> yyyy'
			String datetime = DateUtils.getThisYear()
		then:
			datetime.size() == 4
	}
	
	def "getDayOfWeek() :: 입력받은 일자의 요일을 반환"() {
		
		when: 
			String day = DateUtils.getDayOfWeek('2014-10-7')
		then:
			day == '화'
			
		expect:
			DateUtils.getDayOfWeek(date, abbreviation, locale) == result
		where:
			date 		| abbreviation | locale    	  || result
			'2014-10-7' | true		   | Locale.US 	  || 'Tue'
			'2014-10-7' | false		   | Locale.US 	  || 'Tuesday'
			'2014-10-7' | true		   | Locale.KOREA || '화'
			'2014-10-7' | false		   | Locale.KOREA || '화요일'
	}
	
	def "getDays() :: 입력받은 두 날짜 사이의 일자를 계산"() {
		
		def dayDiff
		
		when:
			dayDiff = DateUtils.getDays(new GregorianCalendar(2014, 10, 7), new GregorianCalendar(2014, 10, 30))
		then:
			dayDiff == 23
			
		when:
			dayDiff = DateUtils.getDays('2014-10-7', '2014-10-30')
		then:
			dayDiff == 23
		
		expect:
			DateUtils.getDays(startDate, endDate, pattern) == result
		where:
			startDate 		| endDate 	      | pattern      	 || result
			'2014-10-7' 	| '2014-10-30' 	  | 'yyyy-MM-dd' 	 || 23
			'2014/10/7' 	| '2014/10/30' 	  | 'yyyy/MM/dd' 	 || 23
			'2014년10월7일' | '2014년10월30일' | 'yyyy년MM월dd일' || 23
	}
	
	def "equals() :: 입력받은 두 일자가 같은지 여부를 확인"() {
		
		expect:
			DateUtils.equals(date1, date2) == result
		where:
			date1 					| date2 	      	 	  || result
			new Date(1292252400000) | '2010-12-14'  		  || true
			new Date(1292252400000) | '2010-12-15'  	 	  || false
			new Date(1292252400000) | new Date(1292252400000) || true
			new Date(1292252400000) | new Date(1292252500000) || false
	}

	def "equals() :: 입력받은 두 일자가 같은지 여부를 확인 [pattern]"() {

		expect:
			DateUtils.equals(date1, date2, pattern) == result
		where:
			date1 					| date2 	    | pattern  	   || result
			new Date(1292252400000) | '2010/12/14'  | 'yyyy/MM/dd' || true
			new Date(1292252400000) | '2010/12/15'  | 'yyyy/MM/dd' || false
	}
	
	def "greaterThan() :: 입력받은 두 일자의 배치 여부를 확인"() {
		
		expect:
			DateUtils.greaterThan(date1, date2) == result
		where:
			date1 						 | date2 	      	 	   || result
			new Date(1292311593000) 	 | '2010-12-13'  		   || true   
			new Date(1292311593000) 	 | '2010-12-15'  	 	   || false  
			new Date(1292311593000) 	 | '2010-12-14' 		   || true  //Date(1292311593000) != 2010-12-14 00:00:00.0
			new Date(1292311593000) 	 | new Date(1092311533000) || true
			new Date(1292311593000) 	 | new Date(1392311593000) || false
			new Date(1292311593000) 	 | new Date(1292311593000) || false
			new Timestamp(1292311593000) | '2010-12-13 00:00:00.0' || true
			new Timestamp(1292311593000) | '2010-12-15 00:00:00.0' || false
			new Timestamp(1292311593000) | '2010-12-14 16:26:33.0' || false
			new Timestamp(1292311593000) | new Date(1092311533000) || true
			new Timestamp(1292311593000) | new Date(1392311593000) || false
			new Timestamp(1292311593000) | new Date(1292311593000) || false
	}

	def "greaterThan() :: 입력받은 두 일자의 배치 여부를 확인 [pattern]"() {
		
		expect:
			DateUtils.greaterThan(date1, date2, pattern) == result
		where:
			date1 						 | date2 	      	 	   | pattern 	  			 || result
			new Date(1292311593000) 	 | '2010/12/13'  		   | 'yyyy/MM/dd' 			 || true   
			new Date(1292311593000) 	 | '2010/12/15'  	 	   | 'yyyy/MM/dd' 			 || false  
			new Date(1292311593000) 	 | '2010/12/14' 		   | 'yyyy/MM/dd' 			 || true  //Date(1292311593000) != 2010-12-14 00:00:00.0
			new Timestamp(1292311593000) | '2010/12/13 00:00:00.0' | 'yyyy/MM/dd HH:mm:ss.S' || true
			new Timestamp(1292311593000) | '2010/12/15 00:00:00.0' | 'yyyy/MM/dd HH:mm:ss.S' || false
			new Timestamp(1292311593000) | '2010/12/14 16:26:33.0' | 'yyyy/MM/dd HH:mm:ss.S' || false
	}
	
	def "addDays() :: 입력받은 일자에 대해서 해당 일만큼 더한 일자를 반환"() {
		
		expect:
			DateUtils.addDays(date, days) == result
		where:
			date 		 | days || result
			'2014-10-07' | 7    || '2014-10-14'
			'2014-10-07' | -5   || '2014-10-02'
			'2014-10-07' | 0    || '2014-10-07'
	}

	def "addMonths() :: 입력받은 일자에 대해서 해당 개월수만큼 더한 일자를 반환"() {
		
		expect:
			DateUtils.addMonths(date, months) == result
		where:
			date 		 | months || result
			'2014-10-07' | 7      || '2015-05-07'
			'2014-10-07' | -4     || '2014-06-07'
			'2014-10-07' | 0      || '2014-10-07'
	}

	def "addYears() :: 입력받은 일자에 대해서 해당 년수만큼 더한 일자를 반환"() {
		
		expect:
			DateUtils.addYears(date, years) == result
		where:
			date 		 | years  || result
			'2014-10-07' | 2      || '2016-10-07'
			'2014-10-07' | -1     || '2013-10-07'
			'2014-10-07' | 0      || '2014-10-07'
	}

	def "addYearMonthDay() :: 입력된 일자에 대해서 년,월,일만큼 가감해서 계산한 일자를 반환"() {
		
		expect:
			DateUtils.addYearMonthDay(date, years, months, days) == result
		where:
			date 		 | years  | months | days || result
			'2014-10-07' | 2      | 2 	   | 2 	  || '2016-12-09'
			'2014-10-07' | -2     | 2 	   | -2	  || '2012-12-05'
			'2014-10-07' | 0      | 0 	   | 0 	  || '2014-10-07'
	}
	
	def "getFirstDateOfMonth() :: 입력일을 기준으로 이달 첫번째 일자를 반환"() {
		
		when:
			def result = DateUtils.getFirstDateOfMonth('2014-12-18')
		then:
			result == '2014-12-01'	
	}

	def "getLastDateOfMonth() :: 입력일을 기준으로 이달 마지막 일자를 반환"() {
		
		expect:
			DateUtils.getLastDateOfMonth(date) == result
		where:
			date 		 || result
			'2014-01-01' || '2014-01-31'
			'2014-02-02' || '2014-02-28'
			'2014-03-03' || '2014-03-31'
			'2014-04-04' || '2014-04-30'
			'2014-05-05' || '2014-05-31'
			'2014-06-06' || '2014-06-30'
			'2014-07-07' || '2014-07-31'
			'2014-08-08' || '2014-08-31'
			'2014-09-09' || '2014-09-30'
			'2014-10-10' || '2014-10-31'
			'2014-11-11' || '2014-11-30'
			'2014-12-12' || '2014-12-31'
	}
	
	def "getFirstDateOfPrevMonth() :: 입력일을 기준으로 전달의 첫번째 일자를 반환"() {
		
		when:
			def result = DateUtils.getFirstDateOfPrevMonth('2014-12-18')
		then:
			result == '2014-11-01'
	}
	
	def "getLastDateOfPrevMonth() :: 입력일을 기준으로 전달의 마지막 일자를 반환"() {
		
		expect:
			DateUtils.getLastDateOfPrevMonth(date) == result
		where:
			date 		 || result
			'2014-01-01' || '2013-12-31'
			'2014-02-02' || '2014-01-31'
			'2014-03-03' || '2014-02-28'
			'2014-04-04' || '2014-03-31'
			'2014-05-05' || '2014-04-30'
			'2014-06-06' || '2014-05-31'
			'2014-07-07' || '2014-06-30'
			'2014-08-08' || '2014-07-31'
			'2014-09-09' || '2014-08-31'
			'2014-10-10' || '2014-09-30'
			'2014-11-11' || '2014-10-31'
			'2014-12-12' || '2014-11-30'
	}
	
	def "isDate() :: 입력된 일자가 유효한 일자인지 체크"() {
		
		expect:
			DateUtils.isDate(date) == result
		where:
			date 		 || result
			'2014-01-01' || true
			'2014-02-31' || false
			''			 || false
			null		 || false
	}

	def "isDate() :: 입력된 일자가 유효한 일자인지 체크 [pattern]"() {
		
		expect:
			DateUtils.isDate(date, pattern) == result
		where:
			date 		 | pattern      || result
			'2014/01/01' | 'yyyy/MM/dd' || true
			'2014-02-31' | 'yyyy-MM-dd' || false
			'2014.13.31' | 'yyyy.MM.dd' || false
			''			 | 'yyyy/MM/dd' || false
			null		 | 'yyyy/MM/dd' || false
	}
	
	def "isTime() :: 입력된 시간이 유효한 시간인지 체크"() {
		
		expect:
			DateUtils.isTime(time) == result
		where:
			time 	|| result
			'11:31' || true
			'25:23' || false
			''		|| false
			null	|| false
	}

	def "isTime() :: 입력된 시간이 유효한 시간인지 체크 [pattern]"() {
		
		expect:
			DateUtils.isTime(time, pattern) == result
		where:
			time 	| pattern || result
			'11_31' | 'HH_mm' || true
			'25_23' | 'HH_mm' || false
			''		| 'HH_mm' || false
			null	| 'HH_mm' || false
	}
	
	def "convertStringToDate() :: 문자열을 java.util.Date 타입으로 변경"() {
		
		setup:
			def date = '2014-10-07'
			Date result
			
		when:
			result = DateUtils.convertStringToDate(date)
		then:
			result == new Date(114, 9, 7)	 	
				
		when:
			date = '2014/10/07 00:00:00'
			result = DateUtils.convertStringToDate(date, 'yyyy/MM/dd HH:mm:ss')
		then:
			result == new Date(114, 9, 7)	 	
	}

	def "convertDateToString() :: java.util.Date 타입을 문자열로 변경"() {
		
		setup:
			def date = new Date(1292311593557)
			def result
			
		when:
			result = DateUtils.convertDateToString(date)
		then:
			result == '2010-12-14'
				
		when:
			result = DateUtils.convertDateToString(date, 'yyyy/MM/dd HH:mm:ss')
		then:
			result == '2010/12/14 16:26:33'	 	
	}

	def "convertStringToSQLDate() :: 문자열을 java.sql.Date 타입으로 변환"() {
		
		setup:
			def date = '2014-10-07'
			java.sql.Date result
			
		when:
			result = DateUtils.convertStringToSQLDate(date)
		then:
			result == new java.sql.Date(114, 9, 7)	 	
				
		when:
			date = '2014/10/07 00:00:00'
			result = DateUtils.convertStringToSQLDate(date, 'yyyy/MM/dd HH:mm:ss')
		then:
			result == new java.sql.Date(114, 9, 7)	
	}

	def "convertStringToTimestamp() :: 문자열을 java.sql.Timestamp 타입으로 변환"() {
		
		setup:
			def date = '2014-10-07'
			Timestamp result
			
		when:
			result = DateUtils.convertStringToTimestamp(date)
		then:
			result == new Timestamp(114, 9, 7, 0, 0, 0, 0)	 	
				
		when:
			date = '2014/10/07 00:00:00'
			result = DateUtils.convertStringToTimestamp(date, 'yyyy/MM/dd HH:mm:ss')
		then:
			result == new Timestamp(114, 9, 7, 0, 0, 0, 0)	
	}
	
	def "convertTimestampToString() :: java.sql.Timestamp 타입의 일자를 문자열로 변환"() {
		
		setup:
			def date = new Timestamp(1292311593557)
			def result
			
		when:
			result = DateUtils.convertTimestampToString(date)
		then:
			result == '2010-12-14'
				
		when:
			result = DateUtils.convertTimestampToString(date, 'yyyy/MM/dd HH:mm:ss')
		then:
			result == '2010/12/14 16:26:33'

		when:
			result = DateUtils.convertTimestampToString(null, 'yyyy/MM/dd HH:mm:ss')
		then:
			result == ''
	}
	
	def "convertStringToCalender() :: 문자열을 java.util.Calendar 타입으로 변환"() {
		
		setup:
			def date = '20101214123412'
			Calendar result
			SimpleDateFormat dateFormat
			
		when:
			dateFormat = new SimpleDateFormat('yyyyMMddHHmmss')
			result = DateUtils.convertStringToCalender(date)
		then:
			date == dateFormat.format(result.getTime())
			
		when:			
			result = DateUtils.convertStringToCalender(null)
		then:
			result == null
		
		when:			
			result = DateUtils.convertStringToCalender('20101225')
		then:
			result == null
	}
	
	def "convertCalendarToString() :: java.util.Calendar타입의 일자를 문자열로 변환"() {
		
		setup:
			Calendar calendar = new GregorianCalendar(2010, 11, 14, 2, 34, 12)
			
		when:
			def result = DateUtils.convertCalendarToString(calendar)
		then:
			result == '20101214023412000'
	}

	def "convertStringToString() :: 사용자 정의 패턴에 맞게 들어온 문자열을 새로운 문자열로 변환"() {
	
		expect:
			DateUtils.convertStringToString(date, basePattern, wantedPattern) == result
		where:
			date 			   | basePattern 		| wantedPattern 	 || result
			'2014-10-07' 	   | 'yyyy-MM-dd' 		| 'yyyyMMdd' 		 || '20141007'	
			'2014/10/07' 	   | 'yyyy/MM/dd' 		| 'yyyy-MM-dd' 		 || '2014-10-07'	
			'20141007' 		   | 'yyyyMMdd' 		| 'yyyy.MM.dd' 		 || '2014.10.07'	
			'2014/10/07 12:12' | 'yyyy/MM/dd HH:mm' | 'yyyy-MM-dd HH.mm' || '2014-10-07 12.12'	
	}
	
	def "getMinutes() :: 입력된 두 일자 사이의 분을 계산하여 반환"() {
		
		setup:
			Calendar cal1 = new GregorianCalendar(2014, 10, 7, 12, 34, 12)
			Calendar cal2 = new GregorianCalendar(2014, 10, 7, 11, 28, 12)
			int result
			
		when:
			result = DateUtils.getMinutes(cal1, cal2) 
		then:
			result == -66
			
		when:
			result = DateUtils.getMinutes(cal2, cal1)
		then:
			result == 66

		when:
			result = DateUtils.getMinutes('20141007123412', '20141007112812')
		then:
			result == -66
			
		when:
			result = DateUtils.getMinutes('20141007112812', '20141007123412')
		then:
			result == 66
	}
	
	
	def "getYesterday() :: 어제 일자를 반환"() {
		
		setup:
			def date = new Date()
			date.setTime(date.getTime() - 1000 * 60 * 60 * 24)
			def cal = Calendar.getInstance()
			cal.setTime(date)
			def dateFormat
			def yesterday
			def result
			
		when:
			dateFormat = new SimpleDateFormat('yyyy-MM-dd')
			yesterday = dateFormat.format(cal.getTime())
			
			result = DateUtils.getYesterday()
		then:
			result == yesterday	
		
			
		when:
			dateFormat = new SimpleDateFormat('yyyy-MM-dd HH:mm')
			yesterday = dateFormat.format(cal.getTime())
			
			result = DateUtils.getYesterday('yyyy-MM-dd HH:mm')
		then:
			result == yesterday
	}
	
	def "getCalendar() :: 한국 시간대에 맞는java.util.Calendar 타입의 일자를 반환"() {
		
		when:
			Calendar calendar = DateUtils.getCalendar()
			
		then:
			calendar instanceof Calendar	
	}
	
	def "getDates() :: 두 일자 사이의 일자 목록을 반환"() {
		
		when:
			String[] dates = DateUtils.getDates('2010-12-17', '2010-12-20')
		then:
			dates == ['2010-12-17', '2010-12-18', '2010-12-19', '2010-12-20'] as String[]
			
		expect:
			DateUtils.getDates(startDate, endDate, pattern) == result
		where:
			startDate 	 | endDate 		| pattern 	   || result
			'2010/12/17' | '2010/12/20' | 'yyyy/MM/dd' || ['2010/12/17', '2010/12/18', '2010/12/19', '2010/12/20'] as String[]
			'2010-12-17' | '2010-12-20' | 'yyyy-MM-dd' || ['2010-12-17', '2010-12-18', '2010-12-19', '2010-12-20'] as String[]
	}
	
	def "getCurrentDateAsString() :: 현재 일자를 yyyy-MM-dd 패턴의 문자열로 반환"() {
		
		when:
			def result = DateUtils.getCurrentDateAsString()
		then:
			result == getCurDatetime('yyyy-MM-dd')	
	}
	
	def "getCurrentDateAsString() :: 현재 일자를 지정된 패턴의 문자열로 반환"() {
		
		when:
			def result = DateUtils.getCurrentDateAsString('yyyy/MM/dd')
		then:
			result == getCurDatetime('yyyy/MM/dd')
	}
	
	def "getCurrentDate() :: 현재 일자를 java.sql.Date 타입으로 반환"() {
		
		when:
			def result = DateUtils.getCurrentDate()
		then:
			result instanceof java.sql.Date
			result == new java.sql.Date((new java.util.Date()).getTime())
	}

	def "getCurrentTime() :: 현재 시각에 대한 Time 객체를 반환"() {
		
		when:
			def result = DateUtils.getCurrentTime()
		then:
			result instanceof Time
	}
	
	def "getCurrentTimeAsString() :: 현재 시각에 대한 Time 문자열을 반환"() {
		
		when:
			def result = DateUtils.getCurrentTimeAsString();
		then:
			result instanceof String
			result.size() == 8
	}

	def "getCurrentTimestamp() :: 현재 시각에 대한 Timestamp 객체를 반환"() {
		
		when:
			def result = DateUtils.getCurrentTimestamp();
		then:
			result instanceof Timestamp
	}
	
	def "getCurrentTimestampAsString() :: 현재 시각에 대한 Timestamp 문자열을 반환"() {
		
		when:
			def result = DateUtils.getCurrentTimestampAsString();
		then:
			result instanceof String
	}
	
	def "isLeapYear() :: 입력된 일자를 기준으로 해당년도가 윤년인지 여부를 반환"() {
		
		expect:
			DateUtils.isLeapYear(year) == result
		where:
			year 		 || result
			'2011-05-05' || false	
			'2012-05-05' || true
			2011 		 || false
			2012 		 || true	
			2000 		 || true	
	}
	
	def "convertStringToTimeMills() :: 문자열 date를 Mills 타입으로 변환"() {
		
		when:
			def result = DateUtils.convertStringToTimeMills('2013-11-28 09:39:20')
		then:
			result == 1385599160000	
	}
	
	def "convertCurrentTimeMillisToHexString() :: 시각에 대한 파라미터 값을 16진수로 변환하여 문자열로 반환"() {
		
		when:
			def result = DateUtils.convertTimeMillisToHexString(System.currentTimeMillis())
		then:
			result.substring(0, 10) == DateUtils.convertCurrentTimeMillisToHexString().substring(0, 10)	
	}
	
	def "convertDateToString() :: 16진수 문자 Date 값을 문자열 date로 반환"() {
		
		setup:
			def result
		
		when:
			result = DateUtils.convertHexDateToTimeMills('0148fafd74a2')
		then:
			result == '2014-10-11 01:56:27'	
			
		when:
			result = DateUtils.convertHexDateToTimeMills('0148fafd74a2', 'yyyy/MM/dd')
		then:
			result == '2014/10/11'
	}
	
	
	private String getCurDatetime(String format) {
		
		Calendar calendar = Calendar.getInstance()
		SimpleDateFormat dateFormat = new SimpleDateFormat(format)
		
		return dateFormat.format(calendar.getTime())
	}*/
}
