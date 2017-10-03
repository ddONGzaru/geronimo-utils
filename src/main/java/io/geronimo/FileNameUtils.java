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

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * 파일 명, 파일 패스, 파일 확장자등에 관련한 편의 기능들을 제공한다.
 * 
 * @author tw.jang
 * @since 1.0.0
 */
@UtilityClass
public final class FileNameUtils {
	
	/**
	 * 파일 명으로부터 prefix를 제외한 패스를 반환한다.<br><br>
	 * 
	 * FileNameUtils.getPath("C:\a\b\c.txt") = "a\b\"<br> 
	 * FileNameUtils.getPath("~/a/b/c.txt") = "a/b/"<br> 
	 * FileNameUtils.getPath("a.txt") = ""<br> 
	 * FileNameUtils.getPath("a/b/c") = "a/b/"<br> 
	 * FileNameUtils.getPath("a/b/c/") = "a/b/c/" 
	 * 
	 * @param filename 파일 이름
	 * @return prefix를 제외한 파일 패스
	 */
	public static String getPath(String filename) {
		return FilenameUtils.getPath(filename);
	}
	
	/**
	 * 파일 명으로부터 prefix 및 마지막 디렉토리 구분자를 제외한 패스를 반환한다.<br><br>
	 * 
	 * FileNameUtils.getPath("C:\a\b\c.txt") = "a\b"<br> 
	 * FileNameUtils.getPath("~/a/b/c.txt") = "a/b"<br> 
	 * FileNameUtils.getPath("a.txt") = ""<br> 
	 * FileNameUtils.getPath("a/b/c") = "a/b"<br> 
	 * FileNameUtils.getPath("a/b/c/") = "a/b/c"
	 * 
	 * @param filename 파일 이름
	 * @return prefix 및 마지막 디렉토리 구분자를 제외한 파일 패스
	 */
	public static String getPathNoEndSeparator(String filename) {
		return FilenameUtils.getPathNoEndSeparator(filename);
	}
	
	/**
	 * 파일 명으로부터 패스를 반환한다.<br><br>
	 * 
	 * FileNameUtils.getFullPath("C:\a\b\c.txt") = "C:\a\b\"<br> 
	 * FileNameUtils.getFullPath("~/a/b/c.txt") = "~/a/b/"<br> 
	 * FileNameUtils.getFullPath("a.txt") = ""<br> 
	 * FileNameUtils.getFullPath("a/b/c") = "a/b/"<br> 
	 * FileNameUtils.getFullPath("a/b/c/") = "a/b/c/"<br>
	 * FileNameUtils.getFullPath("C:") = "C:"<br>
	 * FileNameUtils.getFullPath("C:\") = "C:\"<br>
	 * FileNameUtils.getFullPath("~") = "~/"<br>
	 * FileNameUtils.getFullPath("~/") = "~/"<br>
	 * FileNameUtils.getFullPath("~user") = "~user/"<br>
	 * FileNameUtils.getFullPath("~user/") = "~user/"
	 * 
	 * @param filename 파일 이름 
	 * @return 파일 패스
	 */
	public static String getFullPath(String filename) {
		return FilenameUtils.getFullPath(filename);
	}
	
	/**
	 * 파일 명으로부터 마지막 디렉토리 구분자를 제외한 패스를 반환한다.<br><br>
	 * 
	 * FileNameUtils.getFullPath("C:\a\b\c.txt") = "C:\a\b"<br> 
	 * FileNameUtils.getFullPath("~/a/b/c.txt") = "~/a/b"<br> 
	 * FileNameUtils.getFullPath("a.txt") = ""<br> 
	 * FileNameUtils.getFullPath("a/b/c") = "a/b"<br> 
	 * FileNameUtils.getFullPath("a/b/c/") = "a/b/c"<br>
	 * FileNameUtils.getFullPath("C:") = "C:"<br>
	 * FileNameUtils.getFullPath("C:\") = "C:\"<br>
	 * FileNameUtils.getFullPath("~") = "~"<br>
	 * FileNameUtils.getFullPath("~/") = "~"<br>
	 * FileNameUtils.getFullPath("~user") = "~user"<br>
	 * FileNameUtils.getFullPath("~user/") = "~user"
	 * 
	 * @param filename 파일 이름
	 * @return 마지막 디렉토리 구분자를 제외한 패스
	 */
	public static String getFullPathNoEndSeparator(String filename) {
		return FilenameUtils.getFullPathNoEndSeparator(filename);
	}
	
	/**
	 * 파일 이름으로부터 패스를 제외한 파일 명만 반환한다.<br><br>
	 * 
	 * FileNameUtils.getName("a/b/c.txt") = "c.txt"<br>
	 * FileNameUtils.getName("a.txt") = "a.txt"<br>
	 * FileNameUtils.getName("a/b/c") = "c"<br>
	 * FileNameUtils.getName("a/b/c/") = ""
	 * 
	 * @param filename 파일 이름
	 * @return 패스를 제외한 파일 명
	 */
	public static String getName(String filename) {
		return FilenameUtils.getName(filename);
	}

	/**
	 * 파일 이름으로부터 패스와 확장자를 제외한 파일 명만 반환한다.<br><br>
	 * 
	 * FileNameUtils.getBaseName("C:/a/b/c.txt") = "c"<br>
	 * FileNameUtils.getBaseName("a.txt") = "a"<br>
	 * FileNameUtils.getBaseName("a/b/c") = "c"<br>
	 * FileNameUtils.getBaseName("a/b/c/") = ""
	 * 
	 * @param filename 파일 이름
	 * @return 파일 이름으로부터 패스와 확장자를 제외한 파일 명 
	 */
	public static String getBaseName(String filename) {
		return FilenameUtils.getBaseName(filename);
	}
	
	/**
	 * 파일 이름으로부터 확장자를 반환한다.<br><br>
	 * 
	 * FileNameUtils.getExtension("foo.txt") = "txt"<br>
	 * FileNameUtils.getExtension("a/b/c.jpg") = "jpg"<br>
	 * FileNameUtils.getExtension("a/b.txt/c") = ""<br>
	 * FileNameUtils.getExtension("a/b/c") = ""
	 * 
	 * @param filename 파일 이름
	 * @return 파일의 확장자
	 */
	public static String getExtension(String filename) {
		return FilenameUtils.getExtension(filename);
	}
	
	/**
	 * 파일 이름으로부터 확장자를 제외한 파일 명만 반환한다.<br><br>
	 * 
	 * FileNameUtils.removeExtension("foo.txt") = "foo"<br>
	 * FileNameUtils.removeExtension("a/b/c.jpg") = "a/b/c"<br>
	 * FileNameUtils.removeExtension("a\b\c") = "a\b\c"<br>
	 * FileNameUtils.removeExtension("a.b\c") = "a.b\c"
	 * 
	 * @param filename 파일 이름
	 * @return 확장자를 제외한 파일 명
	 */
	public static String removeExtension(String filename) {
		return FilenameUtils.removeExtension(filename);
	}
	
	/**
	 * URI 문자열에서 URI 프로토콜을 제외한 부분을 얻어온다.
	 * 
	 * @param uri URI명
	 * @return uri 프로토콜을 제외한 경로명
	 */
	public static String getPathWithoutPrefix(String uri) {
		
		String result = "";
		
		if (uri.startsWith("file://") || uri.startsWith("sftp://")) {
			result =  uri.substring(7);
		} else if (uri.startsWith("ftp://") || uri.startsWith("smb://")) {
			result =  uri.substring(6);
		} 
		
		return result;
	}
	
    /**
     * 모든 구분자를 UNIX 계열 구분자인 '/'로 치환한다.
     * 
     * FileNameUtils.separatorsToUnix("file://c:/epapyrus\\hotfoler") = file://c:/epapyrus/hotfoler
     * FileNameUtils.separatorsToUnix("file://c:\\epapyrus/hotfoler") = file://c:/epapyrus/hotfoler
     * 
     * @param path  치환할 경로 (null은 무시)
     * @return 치환된 경로
     */
    public static String separatorsToUnix(String path) {
        return FilenameUtils.separatorsToUnix(path);
    }

    /**
     * 모든 구분자를 WINDOWS 계열 구분자인 '\'로 치환한다.
     * 
     * FileNameUtils.separatorsToWindows("file://c:/epapyrus\\hotfoler") = file:\\c:\epapyrus\hotfoler
     * FileNameUtils.separatorsToWindows("file://c:\\epapyrus/hotfoler") = file:\\c:\epapyrus\hotfoler
     * 
     * @param path  치환할 경로 (null은 무시)
     * @return 치환된 경로
     */
    public static String separatorsToWindows(String path) {
        return FilenameUtils.separatorsToWindows(path);
    }

    /**
     * 모든 구분자를 OS 설정에 따른 구분자로 치환한다.
     * 
     * FileNameUtils.separatorsToSystem("c:/epapyrus\\hotfoler") = c:\epapyrus\hotfoler
     * FileNameUtils.separatorsToSystem("c:\\epapyrus/hotfoler") = c:\epapyrus\hotfoler
     * 
     * @param path  치환할 경로 (null은 무시)
     * @return 치환된 경로
     */
    public static String separatorsToSystem(String path) {
        return FilenameUtils.separatorsToSystem(path);
    }
    
    public static String appendFileSeparator(String path) {
    	
    	if (!path.endsWith(File.separator)) {
    		path += File.separator;
		}
    	
    	return path;
    }
    
	public static String appendWindowsFileSeparator(String path) {
	    	
		path = separatorsToWindows(path);
		
	   	if (!path.endsWith("\\")) {
	   		path += "\\";
		}
	    	
	   	return path;
	}

	public static String appendUnixFileSeparator(String path) { 
		
		path = separatorsToUnix(path);
		
		if (!path.endsWith("/")) {
			path += "/";
		}
		
		return path;
	}
    
}
