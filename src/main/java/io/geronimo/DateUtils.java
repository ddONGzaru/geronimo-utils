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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * JSR-310을 사용하여 날짜, 시간 및 요일 계산, 유효성 체크와 포맷 변경 등의 기능을 제공한다.
 *
 * @author tw.jang
 * @since 1.0.0
 */

public final class DateUtils {
	
	/**
	 * 현재 날짜, 시간을 조회하여 문자열 형태로 반환한다.<br>
	 *
	 * @return (yyyy-MM-dd HH:mm:ss) 포맷으로 구성된 현재 날짜와 시간
	 */
	public static String now() {
		return now(Pattern.DATE_TIME);
	}

	/**
	 * 현재 날짜, 시간을 조회을 조회하고, pattern 형태의 포맷으로 문자열을 반환한다.
	 * <p>
	 * DateUtils.getNow("yyyy년 MM월 dd일 hh시 mm분 ss초") = "2012년 04월 12일 20시 41분 50초"<br>
	 * DateUtils.getNow("yyyy-MM-dd HH:mm:ss") = "2012-04-12 20:41:50"
	 * </p>
	 * @param pattern 날짜 및 시간에 대한 포맷
	 * @return patter 포맷 형태로 구성된 현재 날짜와 시간
	 */
	public static String now(Pattern pattern) {
		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.getFormatter());
		return dateTime.format(formatter);
	}

	/**
	 * 입력받은 두 날짜 사이의 일자를 계산한다.<br>
	 * -startDate와 endDate는 patter의 포맷형식을 꼭 따라야 한다.<br><br>
	 *
	 * DateUtils.getDays("2010-11-24", "2010-12-30", "yyyy-MM-dd") = 36
	 *
	 * @param startDate 시작일
	 * @param endDate 종료일
	 * @param pattern 날짜 포맷
	 * @return 두 날짜 사이의 일자
	 */
	public static int getDays(String startDate, String endDate, Pattern pattern) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.getFormatter());

		LocalDate localStartDate = LocalDate.parse(startDate, formatter);
		LocalDate localEndDate = LocalDate.parse(endDate, formatter);

		return (int) ChronoUnit.DAYS.between(localStartDate, localEndDate);
	}

	/**
	 * 입력받은 두 날짜 사이의 일자를 계산한다.<br><br>
	 *
	 * DateUtils.getDays("2010-11-24", "2010-12-30") = 36
	 *
	 * @param startDate (yyyy-MM-dd) 포맷 형태의 시작일
	 * @param endDate (yyyy-MM-dd) 포맷 형태의 종료일
	 * @return 두 날짜 사이의 일자
	 */
	public static int getDays(String startDate, String endDate) {
		return getDays(startDate, endDate, Pattern.DATE);
	}

	public static int getDays(LocalDate startDate, LocalDate endDate) {
		return (int) ChronoUnit.DAYS.between(startDate, endDate);
	}

	public static int getDays(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		return (int) ChronoUnit.DAYS.between(startDateTime, endDateTime);
	}

	/**
	 * 입력받은 두 일자가 같은지 여부를 확인한다.<br><br>
	 *
	 * DateUtils.equals(new LocalDate(1292252400000l), new LocalDate(1292252400000l)) = true
	 *
	 * @param date1 LocalDate형의 일자
	 * @param date2 LocalDate형의 일자
	 * @return 입력받은 두 일자가 같으면 true를 그렇지않으면 false를 반환.
	 */
	public static boolean equals(LocalDate date1, LocalDate date2) {
		return date1.isEqual(date2);
	}

	/**
	 * 입력받은 두 일자가 같은지 여부를 확인한다.<br><br>
	 *
	 * DateUtils.equals(new Date(1292252400000l), "2010-12-14") = true
	 *
	 * @param date LocalDate형의 일자
	 * @param dateStr (yyyy-MM-dd) 포맷형태의 일자
	 * @return 일치하면 true를 그렇지않으면 false를 반환
	 */
	public static boolean equals(LocalDate date, String dateStr) {
		return equals(date, dateStr, Pattern.DATE);
	}

	/**
	 * 입력받은 두 일자가 같은지 여부를 확인한다.<br><br>
	 *
	 * DateUtils.equals(new LocalDate(1292252400000l), "2010/12/14", "yyyy/MM/dd") = true
	 *
	 * @param date Date형의 일자
	 * @param dateStr 포맷형태의 일자
	 * @param pattern 날짜 포맷
	 * @return 입력받은 두 일자가 같으면 true를 그렇지않으면 false를 반환.
	 */
	public static boolean equals(LocalDate date, String dateStr, Pattern pattern) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.getFormatter());
		LocalDate parsedDate = LocalDate.parse(dateStr, formatter);

		return equals(date, parsedDate);
	}

    /**
     * 입력된 일자를 기준으로 해당년도가 윤년인지 여부를 반환한다.
     * 
     * @param  inputDate (yyyy-MM-dd) 형식의 일자
     * @return 윤년이면 true를 그렇지 않으면 false를 반환
     */
	public static boolean isLeapYear(String inputDate) {
		return isLeapYear(Integer.parseInt(inputDate.substring(0, 4)));
	}
    
    /**
     * 정수형태로 입력된 년도를 기준으로 해당년도가 윤년인지 여부를 반환한다.
     * 
     * @param year 년도
     * @return year이 윤년이면 true를 그렇지 않으면 false를 반환
     */
    public static boolean isLeapYear(int year) {
    	return LocalDate.of(year, 1, 1).isLeapYear();
    }
    
	public static LocalDateTime toLocalDateTime(String dateTime, Pattern pattern) {

		if (StringUtils.isEmpty(dateTime)) {
			return null;
		}

		switch (pattern) {

			case DATE_FLAT:
				return DateUtils.toLocalDate(dateTime, Pattern.DATE_FLAT).atTime(0, 0, 0);

			case DATE:
				return DateUtils.toLocalDate(dateTime, Pattern.DATE).atTime(0, 0, 0);

			case DATE_SLASH:
				return DateUtils.toLocalDate(dateTime, Pattern.DATE_SLASH).atTime(0, 0, 0);

			case DATE_KR:
				return DateUtils.toLocalDate(dateTime, Pattern.DATE_KR).atTime(0, 0, 0);
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.getFormatter());

		return LocalDateTime.parse(dateTime, formatter);
	}

	public static LocalDateTime toLocalDateTime(String dateTime) {
		return toLocalDateTime(dateTime, Pattern.DATE);
	}

	public static LocalDateTime toLocalDateTime(long timeMillis) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), ZoneId.systemDefault());
	}

	public static LocalDateTime toLocalDateTime(long timeMillis, ZoneId zoneId) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), zoneId);
	}

	public static LocalDate toLocalDate(String date, Pattern pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.getFormatter());
		return LocalDate.parse(date, formatter);
	}

	public static LocalDate toLocalDate(String date) {
		return toLocalDate(date, Pattern.DATE);
	}

	public static Timestamp toTimestamp(String dateTime, Pattern pattern) {
		return Timestamp.valueOf(toLocalDateTime(dateTime, pattern));
	}

	public static Timestamp toTimestamp(String dateTime) {
		return Timestamp.valueOf(toLocalDateTime(dateTime, Pattern.DATE_TIME));
	}

	public static Timestamp toTimestamp(long timeMillis) {
		return Timestamp.valueOf(toLocalDateTime(timeMillis));
	}

	public static Date toDate(String dateTime, Pattern pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern.getFormatter());

		Date date = null;

		try {
			date = sdf.parse(dateTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}

	public static Date toDate(String dateTime) {
    	return toDate(dateTime, Pattern.DATE_TIME);
	}

	public static Date toDate(LocalDateTime localDateTime) {
		return toDate(localDateTime, ZoneId.systemDefault());
	}

	public static Date toDate(LocalDateTime localDateTime, ZoneId zoneId) {
		ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
		return Date.from(zonedDateTime.toInstant());
	}

	public static Date toDate(Timestamp timestamp) {
		return new Date(timestamp.getTime());
	}

	public static long toMillis(LocalDateTime dateTime, ZoneId zoneId) {
		ZonedDateTime zdt = dateTime.atZone(zoneId);
		return zdt.toInstant().toEpochMilli();
	}

	public static long toMillis(LocalDateTime dateTime) {
		ZonedDateTime zdt = dateTime.atZone(ZoneId.systemDefault());
		return zdt.toInstant().toEpochMilli();
	}

	public static long toMillis(String dateTime) {
		return toTimestamp(dateTime).getTime();
	}

	public static long toMillis(String dateTime, Pattern pattern) {
		return toTimestamp(dateTime, pattern).getTime();
	}

	public static String toString(LocalDateTime dateTime, Pattern pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.getFormatter());
		return dateTime.format(formatter);
	}

	public static String toString(LocalDateTime dateTime) {
		return toString(dateTime, Pattern.DATE_TIME);
	}

	public static String toString(Timestamp dateTime, Pattern pattern) {
		return toString(dateTime.toLocalDateTime(), pattern);
	}

	public static String toString(Timestamp timestamp) {
		return toString(timestamp.toLocalDateTime());
	}

	public static String toString(LocalDate date, Pattern pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.getFormatter());
		return date.format(formatter);
	}

	public static String toString(LocalDate date) {
		return toString(date, Pattern.DATE);
	}

	public static String toString(LocalTime time, Pattern pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.getFormatter());
		return time.format(formatter);
	}

	public static String toString(LocalTime time) {
		return toString(time, Pattern.TIME);
	}

	public enum Pattern {

		TIMESTAMP("yyyy-MM-dd HH:mm:ss.SSS"),

		DATE_TIME("yyyy-MM-dd HH:mm:ss"),
		DATE_TIME_FLAT("yyyyMMddHHmmss"),
		DATE_TIME_MM("yyyy-MM-dd HH:mm"),
		DATE_TIME_MM_FLAT("yyyyMMddHHmm"),
		DATE_TIME_KR("yyyy년MM월dd일 HH시mm분ss초"),
		DATE_TIME_KR_MM("yyyy년MM월dd일 HH시mm분"),

		DATE("yyyy-MM-dd"),
		DATE_FLAT("yyyyMMdd"),
		DATE_SLASH("yyyy/MM/dd"),
		DATE_KR("yyyy년MM월dd일"),

		TIME("HH:mm:ss"),
		TIME_FLAT("HHmmss"),
		TIME_MM("HH:mm"),
		TIME_MM_FLAT("HHmm");

		private final String formatter;

		Pattern(String formatter) {
			this.formatter = formatter;
		}

		public String getFormatter() {
			return formatter;
		}
	}

	public static Date addYears(Date date, int amount) {
    	return org.apache.commons.lang3.time.DateUtils.addYears(date, amount);
	}

	public static Date addMonths(Date date, int amount) {
    	return org.apache.commons.lang3.time.DateUtils.addMonths(date, amount);
	}

	public static Date addWeeks(final Date date, final int amount) {

		return org.apache.commons.lang3.time.DateUtils.addWeeks(date, amount);
	}

	public static Date addDays(Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.addDays(date, amount);
	}

	public static Date addHours(final Date date, final int amount) {
		return org.apache.commons.lang3.time.DateUtils.addHours(date, amount);
	}

	public static Date addMinutes(final Date date, final int amount) {
		return org.apache.commons.lang3.time.DateUtils.addMinutes(date, amount);
	}

	public static Date addSeconds(final Date date, final int amount) {
		return org.apache.commons.lang3.time.DateUtils.addSeconds(date, amount);
	}

	public static Date addMilliseconds(final Date date, final int amount) {
		return org.apache.commons.lang3.time.DateUtils.addMilliseconds(date, amount);
	}

}
