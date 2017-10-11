package io.geronimo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Pattern;


/**
 * 숫자 관련 계산, 검색, 변환 및 유효성 체크 기능을 제공한다.<br>
 * 
 * @author tw.jang
 * @since 1.0.0
 */
public final class NumberUtils {
	
	/** #,##0.0 */
	public static final String CURRENCY_BELOWTHEDECIMAL1 = "#,##0.0";
	/** #,##0.00 */
	public static final String CURRENCY_BELOWTHEDECIMAL2 = "#,##0.00";
	/** #,##0.000 */
	public static final String CURRENCY_BELOWTHEDECIMAL3 = "#,##0.000";
	/** #,##0.0000 */
	public static final String CURRENCY_BELOWTHEDECIMAL4 = "#,##0.0000";
	/** #,##0.00000 */
	public static final String CURRENCY_BELOWTHEDECIMAL5 = "#,##0.00000";
	/** #,##0 */
	public static final String CURRENCY_NO_DECIMALPOINT = "#,##0";

	public static final String NO_EFFCT_FORMAT = "#";

	/**
	 * 입력받은 문자열이 양수, 음수, 정수, 실수 인지 값을 받아 옳고 그름을 체크한다.<br><br>
	 *
	 * NumberUtils.checkNumberType("+1234", "positive") = true<br>
	 * NumberUtils.checkNumberType("1234", "positive") = true<br>
	 * NumberUtils.checkNumberType("0.1234", "positive") = true<br>
	 * NumberUtils.checkNumberType("-1234.12", "negative") = true<br>
	 * NumberUtils.checkNumberType("-0.1234", "negative") = true<br>
	 * NumberUtils.checkNumberType("1234", "whole") = true<br>
	 * NumberUtils.checkNumberType("-1234", "whole") = true<br>
	 * NumberUtils.checkNumberType("-1234.123", "real") = true<br>
	 * NumberUtils.checkNumberType("1.34", "real") = true	  
	 *
	 * @param str 체크할 문자열
	 * @param check 체크 타입 positive, negative, whole, real number
	 * @return boolean 문자열이 체크 타입과 일치하면 true를 반환
	 */
	public static boolean checkNumberType(String str, String check) {
		String positivePattern = "^[+]?([1-9]\\d*|[1-9]\\d*\\.\\d*|0?\\.\\d*[1-9]\\d*)$";
		String negativePattern = "^-([1-9]\\d*|[1-9]\\d*\\.\\d*|0?\\.\\d*[1-9]\\d*)$";
		String wholePattern = "^[+-]?[1-9]\\d*$";
		String realPattern = "^[+-]?([1-9]\\d*\\.\\d*|0?\\.\\d*[1-9]\\d*)$";

		if (check.equals("positive")) {
			return Pattern.matches(positivePattern, str);
		} else if (check.equals("negative")) {
			return Pattern.matches(negativePattern, str);
		} else if (check.equals("whole")) {
			return Pattern.matches(wholePattern, str);
		} else if (check.equals("real")) {
			return Pattern.matches(realPattern, str);
		}
		
		return false;
	}

	/**
	 * double 형태의 숫자를 받아서, 주어진 10진수 포맷의 String으로 변환한다.<br>
	 * - Java에서의 double 한계에 유의한다. <br><br>
	 *  
	 * NumberUtils.formatNumber(5277095325298.2523, "###,###.##") = "5,277,095,325,298.25"<br>
	 * NumberUtils.formatNumber(5277095325298.2523, "###,###.####") = "5,277,095,325,298.252"<br>
	 * NumberUtils.formatNumber(12345.67d, "###,###.#") = "12,345.7"
	 * 
	 * @param doubleValue 변환할 double 값
	 * @param format 변환을 위한 10진수 포맷
	 * @return 변환된 문자열
	 */
	public static String formatNumber(double doubleValue, String format) {
		DecimalFormat decimalformat = new DecimalFormat(format);
		return decimalformat.format(doubleValue);
	}

	/**
	 * int 형태의 숫자를 받아서, 주어진 10진수 포맷의 String으로 변환한다.<br>
	 * - Java에서의 int 한계에 유의한다.<br><br>
	 * 
	 * NumberUtils.formatNumber(1023412, "###,###,###") = "1,023,412";<br>
	 * NumberUtils.formatNumber(1023412123, "###,###") = "1,023,412,123";<br>
	 * NumberUtils.formatNumber(1023412123, "##,##") = "10,23,41,21,23";<br>
	 * NumberUtils.formatNumber(1023412123, "##.##") = "1023412123";<br>
	 *
	 * @param intValue 변환할 int 값
	 * @param format 변환을 위한 10진수 포맷
	 * @return 변환된 문자열
	 */
	public static String formatNumber(int intValue, String format) {
		DecimalFormat decimalformat = new DecimalFormat(format);
		return decimalformat.format(intValue);
	}

	/**
	 * long 형태의 숫자를 받아서, 주어진 10진수 포맷의 String으로 변환한다.	<br>
	 * 	- Java에서의 long 한계에 유의한다.<br><br>

	 * NumberUtils.formatNumber(12345L, "###,###") ="12,345"
	 * 
	 * @param longValue 변환할 long 값
	 * @param format 변환을 위한 10진수 포맷
	 * @return 변환된 문자열
	 */
	public static String formatNumber(long longValue, String format) {
		DecimalFormat decimalformat = new DecimalFormat(format);
		return decimalformat.format(longValue);
	}

	/**
	 * 소스 숫자 문자열을 해당 포맷형식의 숫자 문자열로 변환한다.<br>
	 * 화폐 표시에 사용하는 ,와 소수점을 표시하는 .을 제외한 다른 문자가 포함되어 있으면 안된다.<br><br>
	 * 
	 * NumberUtil.formatNumber("1234567", "#,##0.000") = "1,234,567.000"<br>
	 * NumberUtil.formatNumber("1", "#,##0.000") = "1.000"<br>
	 * NumberUtil.formatNumber("1", "#,#00.000") = "01.000"<br>
	 * 
	 * @param str 변환할 문자열
	 * @param format 변환을 위한 10진수 포맷
	 * @return 변환된 문자열
	 */
	public static String formatNumber(String str, String format) {
		DecimalFormat df = new DecimalFormat();
		df.applyPattern(format);
		Number sourceNumber = null;
		try {
			sourceNumber = df.parse(StringUtils.removeChar(str, ','));
		} catch (ParseException e) {
			return "ParseException - ,와 . 이외의 다른 문자는 사용 불가";
		}
		return df.format(sourceNumber);
	}

	/**
	 * 특정한 Locale에 맞는 통화 표기를 가져온다.<br><br>
	 *
	 * NumberUtils.formatNumberByLocale(3527900, Locale.KOREA) = "￦3,527,900";<br>
	 * NumberUtils.formatNumberByLocale(3527900, Locale.US) = "$3,527,900.00";
	 *
	 * @param intValue 변환할 숫자
	 * @param locale 통화표기에 사용될 locale
	 * @return 통화표기 및 ,가 적용된 문자열
	 */
	public static String formatNumberByLocale(int intValue, Locale locale) {
		NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
		return nf.format(intValue);
	}

}
