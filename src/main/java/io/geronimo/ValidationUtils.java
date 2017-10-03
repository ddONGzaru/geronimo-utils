package io.geronimo;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DESC : 정규 표현식을 활용한 주민번호,법인번호,사업자,외국인 등록번호등의 유효성 체크와 전화,휴대전화,이메일,카드번호 등의 포맷 유효성 체크 기능을 제공한다.<br><br>
 * 
 * @author tw.jang
 * @since 1.0.0
 */
@UtilityClass
public final class ValidationUtils {
	
	/**
	 * 입력된 주민등록번호가 유효한 주민등록번호인지 검증한다.<br><br>
	 * 
	 * ValidationUtils.isResidentRegNumber("871224-1237613") = true
	 *
	 * @param regno (xxxxxx-xxxxxxx) 포맷의 주민등록번호
	 * @return 주민등록번호가 유효하면 true를 그렇지 않으면 false를 반환
	 */
	public static boolean isResidentRegNumber(String regno) {
		
		String pattern = "^([0-9]{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12][0-9]|3[01]))-([1|2|3|4][0-9]{6})$";
		
		if (!isRegexPatternMatch(regno, pattern)) {
			return false;
		}
		String replaceno = regno.replace("-", "");

		int sum = 0;
		int last = replaceno.charAt(12) - 0x30;
		String bases = "234567892345";

		for (int index = 0; index < 12; index++) {
			if (replaceno.substring(index, index + 1).isEmpty()) {
				return false;
			}
			sum += (replaceno.charAt(index) - 0x30) * (bases.charAt(index) - 0x30);
		}

		int mod = sum % 11;
		return ((11 - mod) % 10 == last) ? true : false;
	}

	/**
	 * 입력된 법인등록번호가 유효한 법인등록번호인지 검증한다.<br><br>
	 * 
	 * ValidationUtils.isIncorpCertNumber("110111-0398556") = true
	 *
	 * @param corpNumber (xxxxxx-xxxxxxx) 포맷의 법인등록번호
	 * @return 법인등록번호가 유효하면 true를 그렇지 않으면 false를 반환
	 */
	public static boolean isIncorpCertNumber(String corpNumber) {
		String pattern = "^((\\d{6})-(\\d{7}))$";

		if (!isRegexPatternMatch(corpNumber, pattern)) {
			return false;
		}
		String replaceno = corpNumber.replace("-", "");

		int checkSum = 0;

		for (int index = 0; index < 12; index++) {
			checkSum += (Character.getNumericValue(replaceno.charAt(index)) * ((index % 2 == 0) ? 1 : 2));
		}

		if ((10 - (checkSum % 10)) % 10 == Character.getNumericValue(replaceno.charAt(12))) {
			return true;			
		} else {
			return false;			
		}
	}

	/**
	 * 입력된 사업자등록번호가 유효한 사업자등록번호인지 검증한다.<br><br>
	 * 
	 * ValidationUtils.isBizRegNumber("110-81-28774") = true
	 *
	 * @param bizNumber (xxx-xx-xxxxx) 포맷의 사업자등록번호
	 * @return 사업자등록번호가 유효하면 true를 그렇지 않으면 false를 반환
	 */
	public static boolean isBizRegNumber(String bizNumber) {
		String pattern = "^((\\d{3})-(\\d{2})-(\\d{5}))$";

		if (!isRegexPatternMatch(bizNumber, pattern)) {
			return false;
		}
		String replaceno = bizNumber.replace("-", "");

		int checkSum = 0;
		int[] checkDigit = {1, 3, 7, 1, 3, 7, 1, 3, 5};

		for (int i = 0; i < 9; i++) {
			checkSum += (Character.getNumericValue(replaceno.charAt(i)) * checkDigit[i]);
		}

		checkSum += (Character.getNumericValue(replaceno.charAt(8)) * 5) / 10;

		if ((10 - (checkSum % 10)) % 10 == Character.getNumericValue(replaceno.charAt(9))) {
			return true;			
		} else {
			return false;			
		}
	}

	/**
	 * 입력된 전화번호가 유효한 전화번호인지 검증한다.<br><br>
	 * 
	 * ValidationUtils.isTelephoneNumber("0505-123-1231") = true
	 *
	 * @param phoneNumber 전화번호
	 * @return 전화번호가 유효하면 true를 그렇지 않으면 false를 반환
	 */
	public static boolean isTelephoneNumber(String phoneNumber) {
		String pattern = "^\\d{2,4}-\\d{3,4}-\\d{4}$";
		return isRegexPatternMatch(phoneNumber, pattern) ? true : false;
	}

	/**
	 * 입력된 핸드폰 번호가 유효한 핸드폰 번호인지 검증한다.<br><br>
	 * 
	 * ValidationUtils.isCellphoneNumber("018-1231-0912")
	 *
	 * @param cellPhoneNumber 핸드폰 번호
	 * @return 핸드폰 번호가 유효하면 true를 그렇지 않으면 false를 반환
	 */
	public static boolean isCellphoneNumber(String cellPhoneNumber) {
		String pattern = "^(01(0|1|6|7|8|9))-\\d{3,4}-\\d{4}$";
		return isRegexPatternMatch(cellPhoneNumber, pattern) ? true : false;
	}

	/**
	 * 입력된 이메일 주소가 유효한 이메일 주소인지 검증한다.
	 *
	 * @param email 이메일 주소
	 * @return 이메일 주소가 유효하면 true를 그렇지 않으면 false를 반환
	 */
	public static boolean isEmailAddress(String email) {
		String pattern = "([\\w-\\.]+)@((?:[\\w]+\\.)+)([a-zA-Z]{2,4})$";
		return isRegexPatternMatch(email, pattern) ? true : false;
	}

	/**
	 * 입력된 카드번호가 유효한 카드번호인지 검증한다.<br><br>
	 * 
	 * ValidationUtils.isCardNumber("4009-1311-1234-4321") = true
	 *
	 * @param cardNumber 카드번호
	 * @return 카드번호의 자릿수가 유효하면 true를 그렇지 않으면 false를 반환
	 */
	public static boolean isCardNumber(String cardNumber) {
		String pattern = "^\\d{4}[\\s\\-]?\\d{4}[\\s\\-]?\\d{4}[\\s\\-]?\\d{4}$";
		return isRegexPatternMatch(cardNumber, pattern) ? true : false;
	}

	/**
	 * 문자열의 길이가 최소, 최대 길이 사이에 존재하는지 체크한다.<br><br>
	 * 
	 * ValidationUtils.isRangeLength("ePapyrus Java Test", 10, 20) = true
	 *
	 * @param str 체크할 문자열
	 * @param min 최소 길이
	 * @param max 최대 길이
	 * @return 문자열이 최소, 최대 길이 사이에 존재하면 true를 그렇지 않으면 false를 반환
	 */
	public static boolean isRangeLength(String str, int min, int max) {
		return (str.length() >= min && str.length() <= max) ? true : false;
	}

	/**
	 * 문자열의 길이가 byte 단위로 계산했을때 최소, 최대 길이 사이에 존재하는지 체크한다.<br>
	 * 참고로, 한글은 UTF-8 인코딩에서 한 글자당 3Byte로 취급된다.<br>
	 * 2Byte로 한글을 처리하기 위해서는 euc-kr 인코딩을 사용해야 한다.<br><br>
	 * 
	 * ValidationUtils.isRangeByteLength("가나다라마", 10, 14) = false
	 *
	 * @param str 체크할 문자열
	 * @param min 최소 길이
	 * @param max 최대 길이
	 * @return byte 단위로 계산된 문자열의 길이가 최소, 최대 길이 사이에 존재하면 true를 그렇지 않으면 false를 반환
	 */
	public static boolean isRangeByteLength(String str, int min, int max) {
		return (StringUtils.getByteLength(str) >= min && StringUtils.getByteLength(str) <= max) ? true : false;
	}

	
	private static String regexMetaCharEscape(String orgPattern) {
		return orgPattern.replaceAll("([\\[\\\\\\^\\$\\.\\|\\?\\*\\+\\(\\)])", "\\\\$1");
	}

	/**
	 * 사용자가 지정한 포맷에 맞는 글자가 들어왔는지 체크한다. #은 숫자를 S는 문자를 표현한다.<br><br>
	 *
	 * ValidationUtils.isUserFormat("123,456", "###,###") = true<br>
	 * ValidationUtils.isUserFormat("123-456", "###-###") = true<br>
	 * ValidationUtils.isUserFormat("123-456", "###-##S") = true
	 *
	 * @param str 체크할 문자열
	 * @param pattern 사용자 정의 패턴
	 * @return 사용자 정의 패턴에 맞는 글자이면 true를 그렇지 않으면 false를 반환
	 */
	public static boolean isUserFormat(String str, String pattern) {
		String metaChange = regexMetaCharEscape(pattern);
		String regexChange = metaChange.replaceAll("#", "\\\\d").replaceAll("S", "[a-zA-Z]");
		return str.matches(regexChange);
	}

	/**
	 * 전체 문자열이 입력된 정규식 패턴에 맞는지 체크한다.<br><br>
	 * 
	 * ValidationUtils.isRegexPatternMatch("aaaaab", "a*b")=true<br>
	 * ValidationUtils.isRegexPatternMatch("aaaaab", "a*b") = true<br>
	 * ValidationUtils.isRegexPatternMatch("cabbbb", "a*b") = false
	 *
	 * @param str 체크할 문자열
	 * @param pattern 정규식 패턴
	 * @return 문자열이 정규식 패턴에 맞으면 true를 그렇지 않으면 false를 반환
	 */
	public static boolean isRegexPatternMatch(String str, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/**
	 * 문자열이 입력된 정규식 패턴에 맞는지 체크한다. *는 전체 문자를 표현한다.<br><br>
	 *
	 * ValidationUtils.isPatternMatching("abc-def', "*-*") 	= true<br>
	 * ValidationUtils.isPatternMatching("abc", "*-*") 	    = false
	 *
	 * @param str 체크할 문자열
	 * @param pattern 정규식 패턴
	 * @return 문자열이 입력된 정규식 패턴에 일치하면 true를 그렇지 않으면 false를 반환
	 */
	public static boolean isPatternMatching(String str, String pattern) {
		// if url has wild key, i.e. "*", convert it to ".*" so that we can
		// perform regex matching
		if (pattern.indexOf('*') >= 0) {
			pattern = pattern.replaceAll("\\*", ".*");
		}

		pattern = "^" + pattern + "$";

		return Pattern.matches(pattern, str);
	}

	/**
	 * 입력된 문자열이 주어진 필터 패턴에 맞는 문자열인지 확인한다.<br>
	 * s-special character k-한글 e-영어 n-숫자<br><br>
	 * 
	 * ValidationUtils.isPatternInclude("asdf@5456", "s") = true<br>
	 * ValidationUtils.isPatternInclude("-", "s") = true<br>
	 * ValidationUtils.isPatternInclude("한", "k") = true<br>
	 * ValidationUtils.isPatternInclude("123가32", "k") = true<br>
	 * ValidationUtils.isPatternInclude("asdfsdfsdf", "e") = true<br>
	 * ValidationUtils.isPatternInclude("asdfs1dfsdf", "e") = true<br>
	 * ValidationUtils.isPatternInclude("123123123", "n") = true<br>
	 * ValidationUtils.isPatternInclude("asdfs1dfsdf", "n") = true
	 *
	 * @param str 체크할 문자열
	 * @param param 필터 패턴
	 * @return 입력된 문자열이 주어진 필터 패턴에 일치하면 true를 그렇지 않으면 false를 반환
	 */
	public static boolean isPatternInclude(String str, String param) {

		if (param.indexOf("s") >= 0) {
			return isRegexPatternMatch(str, ".*[~!@\\#$%<>^&*\\()\\-=+_\\'].*");
		}
		if (param.indexOf("k") >= 0) {
			return isRegexPatternMatch(str, ".*[ㄱ-ㅎ|ㅏ-ㅣ|가-힣].*");
		}
		if (param.indexOf("e") >= 0) {
			return isRegexPatternMatch(str, ".*[a-zA-Z].*");
		}
		if (param.indexOf("n") >= 0) {
			return isRegexPatternMatch(str, ".*\\d.*");
		}
		return true;
	}

	/**
	 * 전체 문자열 중에 일부 문자열이 패턴에 맞는지 체크한다.<br><br>
	 *
	 * ValidationUtils.isRegexPatternInclude("cabbbb", "a*b")) = true
	 *
	 * @param str 체크할 문자열
	 * @param pattern 정규식 패턴
	 * @return 전체 문자열 중에 일부 문자열이 패턴에 일치하면 true를 그렇지 앟으면 false를 반환
	 */
	public static boolean isRegexPatternInclude(String str, String pattern) {
		return isRegexPatternMatch(str, ".*" + pattern + ".*");
	}
}
