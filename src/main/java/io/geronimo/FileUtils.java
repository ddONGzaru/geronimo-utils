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

import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 파일 및 디렉토리 복사, 삭제, 초기화 등에 관련한 편의 기능들을 제공한다.<br>
 * 
 * @author tw.jang
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public final class FileUtils {

	private static Result buildFailResult(String errMsg) {

		Result result = Result.FAIL;
		result.setMessage(errMsg);
		
		return result;
	}
	
	/**
     * byte단위의 파일 사이즈를 읽기 쉽도록 EB, PB, TB, GB, MB, KB, bytes 단위로 파싱하여 반환한다.
     * 
     * @param file 사이즈 측정에 사용될 파일
     * @return EB, PB, TB, GB, MB, KB, bytes 단위의 파일 사이즈
     */
    public static String byteCountToDisplaySize(File file) {
    	return org.apache.commons.io.FileUtils.byteCountToDisplaySize(file.length());    	
    }
	
	/**
     * byte단위의 파일 사이즈를 읽기 쉽도록 EB, PB, TB, GB, MB, KB, bytes 단위로 파싱하여 반환한다.
     * 
     * @param size bytes 단위의 파일 크기
     * @return EB, PB, TB, GB, MB, KB, bytes 단위의 파일 사이즈
     */
    public static String byteCountToDisplaySize(long size) {
    	return org.apache.commons.io.FileUtils.byteCountToDisplaySize(size);    	
    }

	/**
	 * 루트 디렉토리 내의 파일 및 하위 디렉토리를 삭제한다. 루트 디렉토리는 삭제하지 않는다.
	 * 
	 * @param dir 루트 디렉토리 File
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result cleanDir(File dir) {

		if (notExistsDir(dir)) {
			return buildFailResult(dir + "가 존재하지 않습니다.");
		}

		if (isNotDir(dir)) {
			return buildFailResult(dir + "는 디렉토리가 아닙니다.");
		}

		try {
			org.apache.commons.io.FileUtils.cleanDirectory(dir);
			return Result.SUCCESS;
		} catch (Exception e) {
			return buildFailResult(e.getMessage());
		}
	}

	/**
	 * 루트 디렉토리 내의 파일 및 하위 디렉토리를 삭제한다. 루트 디렉토리는 삭제하지 않는다.
	 * 
	 * @param dir 루트 디렉토리 명
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result cleanDir(String dir) {
		return cleanDir(new File(dir));
	}

	/**
	 * 원본 디렉토리를 대상 디렉토리명으로 복사한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDir(File srcDir, File destDir) {
		return copyDir(srcDir, destDir, null, true);

	}

	/**
	 * 원본디렉토리를 대상 디렉토리명으로 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDir(File srcDir, File destDir, boolean preserveFileDate) {
		return copyDir(srcDir, destDir, null, true);
		
	}

	/**
	 * 원본디렉토리를 대상 디렉토리명으로 복사한다. fileDir가 'file'이면 파일만 'directory'면 디렉토리만 복사한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param fileOrDir 파일인지 디렉토리인지를 결정하는 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDir(File srcDir, File destDir, String fileOrDir) {
		return copyDir(srcDir, destDir, fileOrDir, true);
	}

	/**
	 * 원본디렉토리를 대상 디렉토리명으로 복사한다. fileOrDir가 'file'이면 파일만 'directory'면 디렉토리만 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param fileOrDir 파일인지 디렉토리인지를 결정하는 플래그
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDir(File srcDir, File destDir, String fileOrDir, boolean preserveFileDate) {

		if (notExistsDir(srcDir)) {
			return buildFailResult(srcDir + "가 존재하지 않습니다.");
		}

		if (isNotDir(srcDir)) {
			return buildFailResult(srcDir + "는 디렉토리가 아닙니다.");
		}
		
		if (notExistsDir(destDir)) {
			
			Result result = createDir(destDir.getAbsolutePath());
			
			if (result == Result.FAIL) {
				return buildFailResult(destDir + " 디렉토리 생성 중에 에러가 발생하였습니다.");
			}
		}
		
		if (isNotDir(destDir)) {
			return buildFailResult(destDir + "는 디렉토리가 아닙니다.");
		}
		
		if (fileOrDir.isEmpty()) {
			
			try {
				org.apache.commons.io.FileUtils.copyDirectory(srcDir, destDir, preserveFileDate);
			} catch (Exception e) {
				return buildFailResult(e.getMessage());
			}
			
		} else if (fileOrDir.equalsIgnoreCase("file")) {
			
			try {
				org.apache.commons.io.FileUtils.copyDirectory(srcDir, destDir, FileFileFilter.FILE, preserveFileDate);
			} catch (Exception e) {
				return buildFailResult(e.getMessage());
			}
			
		} else if (fileOrDir.equalsIgnoreCase("dir")) {
			
			try {
				org.apache.commons.io.FileUtils.copyDirectory(srcDir, destDir, DirectoryFileFilter.DIRECTORY, preserveFileDate);
			} catch (Exception e) {
				return buildFailResult(e.getMessage());
			}
			
		} else {
			return buildFailResult(fileOrDir + "은 지원하지 않는 타입입니다.");
		}

		return Result.SUCCESS;
	}

	/**
	 * 원본 디렉토리를 대상 디렉토리명으로 복사한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDir(String srcDir, String destDir) {
		return copyDir(new File(srcDir), new File(destDir), true);
	}

	/**
	 * 원본디렉토리를 대상 디렉토리명으로 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDir(String srcDir, String destDir, boolean preserveFileDate) {
		return copyDir(new File(srcDir), new File(destDir), preserveFileDate);
	}

	/**
	 * 원본디렉토리를 대상 디렉토리명으로 복사한다. fileDir가 'file'이면 파일만 'directory'면 디렉토리만 복사한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param fileOrDir 파일인지 디렉토리인지를 결정하는 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDir(String srcDir, String destDir, String fileOrDir) {
		return copyDir(new File(srcDir), new File(destDir), fileOrDir, true);
	}

	/**
	 * 원본디렉토리를 대상 디렉토리명으로 복사한다. fileOrDir가 'file'이면 파일만 'directory'면 디렉토리만 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param fileOrDir 파일인지 디렉토리인지를 결정하는 플래그 "file" 또는 "directory"를 사용
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDir(String srcDir, String destDir, String fileOrDir, boolean preserveFileDate) {
		return copyDir(new File(srcDir), new File(destDir), fileOrDir, preserveFileDate);
	}

	/**
	 * 원본디렉토리를 대상 디렉토리명으로 복사한다. 입력된 파일 확장자랑 일치하는 파일들만 복사한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param extList 파일 확장자들
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDirAfterCheckFileExt(File srcDir, File destDir, String... extList) {
		return copyDirAfterCheckFileExt(srcDir, destDir, true, extList);
	}

	/**
	 * 원본디렉토리를 대상 디렉토리명으로 복사한다. 입력된 파일 확장자랑 일치하는 파일들만 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param extList 파일 확장자들
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDirAfterCheckFileExt(File srcDir, File destDir, boolean preserveFileDate, String... extList) {

		Result result = Result.EMPTY;
		
		if (notExistsDir(srcDir)) {
			return buildFailResult(srcDir + "가 존재하지 않습니다.");
		}

		if (isNotDir(srcDir)) {
			return buildFailResult(srcDir + "는 디렉토리가 아닙니다.");
		}
		
		if (notExistsDir(destDir)) {
			
			result = createDir(destDir.getAbsolutePath());
			
			if (result == Result.FAIL) {
				return buildFailResult(destDir + " 디렉토리 생성 중에 에러가 발생하였습니다.");
			}
		}
		
		if (isNotDir(destDir)) {
			return buildFailResult(destDir + "는 디렉토리가 아닙니다.");
		}
		
		IOFileFilter suffixFilters = new SuffixFileFilter(extList, IOCase.INSENSITIVE);
		FileFilter filter = FileFilterUtils.or(DirectoryFileFilter.DIRECTORY, suffixFilters);
		
		try {
			org.apache.commons.io.FileUtils.copyDirectory(srcDir, destDir, filter, preserveFileDate);
		} catch (Exception e) {
			return buildFailResult(e.getMessage());
		}

		return Result.SUCCESS;
	}

	/**
	 * 원본디렉토리를 대상 디렉토리명으로 복사한다. 입력된 파일 확장자랑 일치하는 파일들만 복사한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param extList 파일 확장자들
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDirAfterCheckFileExt(String srcDir, String destDir, String... extList) {
		return copyDirAfterCheckFileExt(srcDir, destDir, true, extList);
	}

	/**
	 * 원본디렉토리를 대상 디렉토리명으로 복사한다. 입력된 파일 확장자랑 일치하는 파일들만 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param extList 파일 확장자들
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDirAfterCheckFileExt(String srcDir, String destDir, boolean preserveFileDate, String... extList) {
		return copyDirAfterCheckFileExt(new File(srcDir), new File(destDir), preserveFileDate, extList);
	}

	/**
	 * 원본 디렉토리를 대상 디렉토리의 하위로 복사한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDirToDir(File srcDir, File destDir) {
		return copyDirToDir(srcDir, destDir, true);
	}

	/**
	 * 원본 디렉토리를 대상 디렉토리의 하위로 복사한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDirToDir(String srcDir, String destDir) {
		return copyDirToDir(new File(srcDir), new File(destDir), true);
	}
	
	/**
	 * 원본 디렉토리를 대상 디렉토리의 하위로 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param preserveFileDate 수정일 설정 플래그           
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDirToDir(File srcDir, File destDir, boolean preserveFileDate) {

		Result result = Result.EMPTY;
		
		if (notExistsDir(srcDir)) {
			return buildFailResult(srcDir + "가 존재하지 않습니다.");
		}

		if (isNotDir(srcDir)) {
			return buildFailResult(srcDir + "는 디렉토리가 아닙니다.");
		}
		
		if (notExistsDir(destDir)) {
			
			result = createDir(destDir.getAbsolutePath());
			
			if (result == Result.FAIL) {
				return buildFailResult(destDir + " 디렉토리 생성 중에 에러가 발생하였습니다.");
			}
		}
		
		if (isNotDir(destDir)) {
			return buildFailResult(destDir + "는 디렉토리가 아닙니다.");
		}
		
		destDir = new File(destDir, srcDir.getName());

		try {
			
			result = copyDir(srcDir, destDir, preserveFileDate);
			
			if (result == Result.FAIL) {
				return buildFailResult(srcDir.getName() + " 디렉토리 복사 중에 에러가 발생하였습니다."	);
			}
			
		} catch (Exception e) {
			return buildFailResult(e.getMessage());
		}

		return Result.SUCCESS;
	}

	/**
	 * 원본 디렉토리를 대상 디렉토리의 하위로 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param preserveFileDate 수정일 설정 플래그           
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDirToDir(String srcDir, String destDir, boolean preserveFileDate) {
		return copyDirToDir(new File(srcDir), new File(destDir), preserveFileDate);
	}

	/**
	 * 원본 파일을 대상 파일명으로 복사한다
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyFile(File srcFile, File destFile) {
		return copyFile(srcFile, destFile, true);
	}

	/**
	 * 원본 파일을 대상 파일명으로 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyFile(File srcFile, File destFile, boolean preserveFileDate) {

		if (notExistsFile(srcFile)) {
			return buildFailResult(srcFile.getName() + "가 존재하지 않습니다.");
		}

		if (isNotFile(srcFile)) {
			return buildFailResult(srcFile.getName() + "는 디렉토리가 아닙니다.");
		}
		
		if (existsFile(destFile)) {
			
			Result result = deleteFile(destFile);
			
			if (result == Result.FAIL) {
				return buildFailResult(destFile.getName() + "을 삭제하는 도중에 에러가 발생하였습니다.");
			}
		}

		try {
			org.apache.commons.io.FileUtils.copyFile(srcFile, destFile,	preserveFileDate);
		} catch (IOException e) {
			
			String errorMsg = e.getMessage();
			
			if (errorMsg.contains("same")) {
				return Result.SUCCESS;
			} else {
				return buildFailResult(e.getMessage());
			}
		}

		return Result.SUCCESS;
	}

	/**
	 * 원본 파일을 대상 파일명으로 복사한다
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyFile(String srcFile, String destFile) {
		return copyFile(new File(srcFile), new File(destFile), true);
	}

	/**
	 * 원본 파일을 대상 파일명으로 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyFile(String srcFile, String destFile, boolean preserveFileDate) {
		return copyFile(new File(srcFile), new File(destFile), preserveFileDate);
	}

	/**
	 * 파일을 대상 디렉토리에 복사한다.
	 * 
	 * @param srcFile 원본 파일
	 * @param destDir 대상 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyFileToDir(File srcFile, File destDir) {
		return copyFileToDir(srcFile, destDir, true);
	}

	/**
	 * 파일을 대상 디렉토리에 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcFile 원본 파일
	 * @param destDir 대상 디렉토리
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyFileToDir(File srcFile, File destDir, boolean preserveFileDate) {

		if (!srcFile.exists()) {
			return buildFailResult( srcFile + "가 존재하지 않습니다.");
		}

		if (notExistsDir(destDir)) {
			
			Result result = createDir(destDir.getAbsolutePath());
			
			if (result == Result.FAIL) {
				return buildFailResult(destDir + " 디렉토리 생성 중에 에러가 발생하였습니다.");
			}
		}
		
		if (isNotDir(destDir)) {
			return buildFailResult(destDir + "는 디렉토리가 아닙니다.");
		}
		
		try {
			org.apache.commons.io.FileUtils.copyFileToDirectory(srcFile, destDir, preserveFileDate);
		} catch (Exception e) {
			return buildFailResult(e.getMessage());
		}

		return Result.SUCCESS;
	}

	/**
	 * 파일을 대상 디렉토리에 복사한다.
	 * 
	 * @param srcFile 원본 파일
	 * @param destDir 대상 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyFileToDir(String srcFile, String destDir) {
		return copyFileToDir(new File(srcFile), new File(destDir), true);
	}

	/**
	 * 파일을 대상 디렉토리에 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcFile 원본 파일
	 * @param destDir 대상 디렉토리
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyFileToDir(String srcFile, String destDir, boolean preserveFileDate) {
		return copyFileToDir(new File(srcFile), new File(destDir), preserveFileDate);
	}

	/**
	 * 주어진 디렉토리명으로 디렉토리를 생성한다.
	 * 
	 * @param dirPath 생성할 디렉토리 명
	 * @return enum 타입의 Result로 작업 결과를 반환
	 */
	public static Result createDir(String dirPath) {
		
		try {
			org.apache.commons.io.FileUtils.forceMkdir(new File(dirPath));
		} catch (IOException e) {
			return buildFailResult("directory 생성에 실패하였습니다.");
		}
		
		return Result.SUCCESS;
	}

	/**
	 * 해당 디렉토리 및 하위 파일을 삭제한다.
	 * 
	 * @param targetDir 삭제할 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result deleteDir(File targetDir) {

		Result result = Result.EMPTY;
		
		if (!targetDir.exists()) {
			return buildFailResult(targetDir	+ "가 존재하지 않습니다.");
		}

		if (isNotDir(targetDir)) {
			return buildFailResult(targetDir + "는(은) 디렉토리가 아닙니다.");
		}

		try {
			org.apache.commons.io.FileUtils.deleteDirectory(targetDir);
		} catch (Exception e) {
			return buildFailResult(e.getMessage());
		}

		return Result.SUCCESS;
	}

	/**
	 * 해당 디렉토리 및 하위 파일을 삭제한다.
	 * 
	 * @param targetDir 삭제할 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result deleteDir(String targetDir) {
		return deleteDir(new File(targetDir));
	}

	/**
	 * 해당파일을 삭제한다.
	 * 
	 * @param targetFile 삭제할 File
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result deleteFile(File targetFile) {

		if (!targetFile.exists()) {
			return buildFailResult(targetFile	+ "가 존재하지 않습니다.");
		}

		if (isNotFile(targetFile)) {
			return buildFailResult(targetFile	+ "은 파일이 아닙니다.");
		}

		try {
			org.apache.commons.io.FileUtils.forceDelete(targetFile);
		} catch (Exception e) {
			return buildFailResult(e.getMessage());
		}

		return Result.SUCCESS;
	}

	/**
	 * 해당파일을 삭제한다.
	 * 
	 * @param targetFile 삭제할 File
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result deleteFile(String targetFile) {
		return deleteFile(new File(targetFile));
	}

	/**
	 * 해당 디렉토리의 존재유무를 확인한다.
	 * 
	 * @param dir 존재유무를 확인할 디렉토리
	 * @return 존재유무에 대한 결과
	 */
	public static boolean existsDir(File dir) {
		return (dir.exists() && isDir(dir)) ? true : false;
	}

	/**
	 * 해당 디렉토리의 존재유무를 확인한다.
	 * 
	 * @param dir 존재유무를 확인할 디렉토리
	 * @return 존재유무에 대한 결과
	 */
	public static boolean existsDir(String dir) {
		return existsDir(new File(dir));
	}

	/**
	 * 해당 파일의 존재유무를 확인한다.
	 * 
	 * @param file 존재유무를 확인할 파일
	 * @return 존재유무에 대한 결과
	 */
	public static boolean existsFile(File file) {
		return (file.exists() && isFile(file)) ? true : false; 
	}

	/**
	 * 해당 파일의 존재유무를 확인한다.
	 * 
	 * @param file 존재유무를 확인할 파일
	 * @return 존재유무에 대한 결과
	 */
	public static boolean existsFile(String file) {
		return existsFile(new File(file));
	}
	
	/**
	 * 해당 디렉토리의 존재유무를 확인한다.
	 * 
	 * @param dir 존재유무를 확인할 디렉토리
	 * @return 존재유무에 대한 결과
	 */
	public static boolean notExistsDir(File dir) {
		return !existsDir(dir);
	}
	
	/**
	 * 해당 디렉토리의 존재유무를 확인한다.
	 * 
	 * @param dir 존재유무를 확인할 디렉토리
	 * @return 존재유무에 대한 결과
	 */
	public static boolean notExistsDir(String dir) {
		return !existsDir(new File(dir));
	}

	/**
	 * 해당 파일의 존재유무를 확인한다.
	 * 
	 * @param file 존재유무를 확인할 파일
	 * @return 존재유무에 대한 결과
	 */
	public static boolean notExistsFile(File file) {
		return !existsFile(file);
	}

	/**
	 * 해당 파일의 존재유무를 확인한다.
	 * 
	 * @param file 존재유무를 확인할 파일
	 * @return 존재유무에 대한 결과
	 */
	public static boolean notExistsFile(String file) {
		return !existsFile(new File(file));
	}

	/**
     * Java가 실행되는 현재 폴더 경로를 반환한다.
     * 
     * @return 현재 폴더 경로
     */
    public static String getCurrentDir() {
        return System.getProperty("user.dir");
    }

	/**
     * Java가 실행되는 현재 폴더 경로를 File 객체로 반환한다.
     * 
     * @return 현재 위치 경로를 가진 File 객체.
     */
    public static File getCurrentDirAsFile() {
        return new File(getUserHomeDir());
    }

	/**
	 * 해당 디렉토리의 부모 디렉토리명을 반환한다.
	 * 
	 * @param file 디렉토리명
	 * @return 현재 디렉토리에 대한 부모 디렉토리 명
	 */
	public static String getParenet(String file) {
		return new File(file).getParent();
	}

	/**
     * system temporary directory 경로를 반환한다.
     * 
     * @return system temporary directory 경로
     */
    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }

	/**
     * system temporary directory 경로를 File 객체로 반환한다.
     * 
     * @return system temporary directory 경로를 가진 File 객체. 
     */
    public static File getTempDirAsFile() {
        return new File(getTempDir());
    }
    
    /**
     * srcFile에 대한 tempFile 경로를 구한다. 
     * 예) C:/srcFile.txt - C:/srcFile_temp.txt
     * 
     * @param srcFile 대상 파일.
     * @return srcFile명에 _temp를 더한 파일명
     */
    public static String getTempFilePath(String srcFile) {		
		return FileNameUtils.removeExtension(srcFile) + "_temp." + FileNameUtils.getExtension(srcFile);
	}

	/**
     * user's home directory 경로를 반환한다.
     * 
     * @return user's home directory 경로
     */
    public static String getUserHomeDir() {
        return System.getProperty("user.home");
    }
	
	/**
     * user's home directory 경로를 File 객체로 반환한다.
     * 
     * @return user's home directory 경로를 가진 File 객체.
     */
    public static File getUserHomeDirAsFile() {
        return new File(getUserHomeDir());
    }
	
	/**
	 * 해당 target이 디렉토리인지를 체크한다.
	 * 
	 * @param dir 디렉토리
	 * @return 해당 파일이 디렉토리이면 true, 아니면 false
	 */
	public static boolean isDir(File dir) {
		return (!dir.isDirectory()) ? false : true;
	}
	
	/**
	 * 해당 target이 디렉토리인지를 체크한다.
	 * 
	 * @param dir 디렉토리
	 * @return 해당 파일이 디렉토리이면 true, 아니면 false
	 */
	public static boolean isDir(String dir) {
		return isDir(new File(dir));
	}

	/**
	 * 해당 target이 파일인지를 체크한다.
	 * 
	 * @param file 파일
	 * @return 해당 파일이 파일이면 true, 아니면 false
	 */
	public static boolean isFile(File file) {
		return (!file.isFile()) ? false : true;
	}
	
	/**
	 * 해당 target이 파일인지를 체크한다.
	 * 
	 * @param file 파일
	 * @return 해당 파일이 파일이면 true, 아니면 false
	 */
	public static boolean isFile(String file) {
		return isFile(new File(file));
	}

	/**
	 * 해당 target이 디렉토리가 아닌지를 체크한다.
	 * 
	 * @param dir 디렉토리
	 * @return 해당 파일이 디렉토리가 아니면 true, 아니면 false
	 */
	public static boolean isNotDir(File dir) {
		return (!dir.isDirectory()) ? true : false;
	}
	
	/**
	 * 해당 target이 디렉토리가 아닌지를 체크한다.
	 * 
	 * @param dir 디렉토리
	 * @return 해당 파일이 디렉토리가 아니면 true, 아니면 false
	 */
	public static boolean isNotDir(String dir) {
		return isNotDir(new File(dir));
	}

	/**
	 * 해당 target이 파일이 아닌지를 체크한다.
	 * 
	 * @param file 파일
	 * @return 해당 파일이 파일이 아니면 true, 아니면 false
	 */
	public static boolean isNotFile(File file) {
		return (!file.isFile()) ? true : false;
	}
	
	/**
	 * 해당 target이 파일이 아닌지를 체크한다.
	 * 
	 * @param file 파일
	 * @return 해당 파일이 파일이 아니면 true, 아니면 false
	 */
	public static boolean isNotFile(String file) {
		return isNotFile(new File(file));
	}
	
	/**
	 * 지정한 디렉토리 하위의 디렉토리명과 파일명을 가져온다. 
	 * 
	 * @param dir 디렉토리
	 * @param includeRootDir 가장 상위의 디렉토리명을 포함할지를 결정. (true면 최상위 폴더 포함)
	 * @return 디렉토리 하위의 디렉토리명과 파일명에 대한 리스트
	 */
	public static List<String> listFileAndDirNames(String dir, boolean includeRootDir) {
		
		List<File> dirs = (List<File>) org.apache.commons.io.FileUtils.listFilesAndDirs(new File(dir), 
				FileFileFilter.FILE, DirectoryFileFilter.DIRECTORY);
		
		List<String> dirNameList = new ArrayList<String>();
		
		int index = 0;
		
		for (File dirUnit : dirs) {
			
			if (index++ == 0 && !includeRootDir) {
				continue;
			}
			
			dirNameList.add(dirUnit.getAbsolutePath());			
			index++;
		}
		
		return dirNameList;
	}
	
	/**
	 * 지정한 디렉토리 하위의 디렉토리명과 파일명을 File 타입의 배열로 가져온다. 
	 * 
	 * @param dir 디렉토리
	 * @param includeRootDir 가장 상위의 디렉토리명을 포함할지를 결정. (true면 최상위 폴더 포함)
	 * @return 디렉토리 하위의 디렉토리명과 파일명에 대한 File 타입의 배열
	 */
	public static File[] listFilesAndDirs(String dir, boolean includeRootDir) {
		
		List<File> filesAndDirs = (List<File>) org.apache.commons.io.FileUtils.listFilesAndDirs(new File(dir), 
				FileFileFilter.FILE, DirectoryFileFilter.DIRECTORY);
		
		List<File> filesAndDirsList = new ArrayList<File>();
		
		int index = 0;
		
		for (File fileOrDir : filesAndDirs) {
			
			if (index++ == 0 && !includeRootDir) {
				continue;
			}
			
			filesAndDirsList.add(fileOrDir);			
			index++;
		}
		
		return filesAndDirsList.toArray(new File[filesAndDirsList.size()]);
		
	}
	
	/**
	 * 지정한 디렉토리 하위의 디렉토리명을 가져온다. 
	 * 
	 * @param dir 디렉토리
	 * @param includeRootDir 가장 상위의 디렉토리명을 포함할지를 결정. (true면 최상위 폴더 포함)
	 * @return 디렉토리 하위의 디렉토리명에 대한 리스트
	 */
	public static List<String> listDirNames(String dir, boolean includeRootDir) {
		
		List<File> dirs = (List<File>) org.apache.commons.io.FileUtils.listFilesAndDirs(new File(dir), 
				new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY);
		
		List<String> dirNameList = new ArrayList<String>();
		
		int index = 0;
		
		for (File dirUnit : dirs) {
			
			if (index++ == 0 && !includeRootDir) {
				continue;
			}
			
			dirNameList.add(dirUnit.getAbsolutePath());			
			index++;
		}
		
		return dirNameList;		
	}
	
	/**
	 * 지정한 디렉토리 하위의 디렉토리명을 File 타입의 배열로 가져온다. 
	 * 
	 * @param dir 디렉토리
	 * @param includeRootDir 가장 상위의 디렉토리명을 포함할지를 결정. (true면 최상위 폴더 포함)
	 * @return 디렉토리 하위의 디렉토리명에 대한 File 타입의 배열
	 */
	public static File[] listDirs(String dir, boolean includeRootDir) {
		
		List<File> dirs = (List<File>) org.apache.commons.io.FileUtils.listFilesAndDirs(new File(dir), 
				new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY);
		
		List<File> dirList = new ArrayList<File>();
		
		int index = 0;
		
		for (File dirUnit : dirs) {
			
			if (index++ == 0 && !includeRootDir) {
				continue;
			}
			
			dirList.add(dirUnit);			
			index++;
		}
		
		return dirList.toArray(new File[dirList.size()]);
	}
	
	/**
	 * 지정한 디렉토리 하위의 파일들을 File 타입의 배열로 가져온다. 
	 * 
	 * @param dir 디렉토리
	 * @param recursive 하위 폴더 내부의 파일들에 대한 탐색 유무
	 * @return 디렉토리 하위의 파일에 대한 File 타입의 배열
	 */
	public static File[] listFiles(String dir, boolean recursive) {
		
		return org.apache.commons.io.FileUtils.convertFileCollectionToFileArray(
                org.apache.commons.io.FileUtils.listFiles(new File(dir), null, recursive));
	}
	
	/**
	 * 지정한 디렉토리 하위의 파일명들에 대한 리스트를 가져온다.
	 * 
	 * @param dir 디렉토리
	 * @param recursive 하위 폴더 내부의 파일들에 대한 탐색 유무
	 * @return 디렉토리 하위의 파일들에 대한 파일명 리스트
	 */
	public static List<String> listFileNames(String dir, boolean recursive) {
		
		Collection<File> files;
		
		try {
			files = org.apache.commons.io.FileUtils.listFiles(new File(dir), null, recursive);
		} catch (Exception e) {
			log.error("FileUtils-listFileNames :: {}", e.getMessage());
			return null;
		}
		
		List<String> fileNamesList = new ArrayList<String>();
		
		for (File file : files) {
			fileNamesList.add(file.getAbsolutePath());
		}
		
		return fileNamesList;
	}
	
	/**
	 * 지정한 파일 확장자로 구성된 파일들에 대한 리스트를 가져온다.
	 * 
	 * @param dir 디렉토리 
	 * @param recursive 하위 폴더 내부의 파일들에 대한 작업 유무
	 * @param extList 포함시킬 확장자 리스트
	 * @return 지정한 파일 확장자로 구성된 파일들에 대한 리스트
	 */
	public static List<String> listFilenamesIncludeExt(String dir, boolean recursive, String... extList) {
		
		List<File> extFilterList = (List<File>) org.apache.commons.io.FileUtils.listFiles(new File(dir), extList,  recursive);
		
		List<String> resultList = new ArrayList<String>();
		
		for (File file : extFilterList) {
			resultList.add(file.getAbsolutePath());
		}
		
		return resultList;		
	}

	/**
	 * 지정한 파일 확장자로 구성된 파일들을 File 타입의 배열로 가져온다.
	 * 
	 * @param dir 디렉토리 
	 * @param recursive 하위 폴더 내부의 파일들에 대한 작업 유무
	 * @param extList 포함시킬 확장자 리스트
	 * @return 지정한 파일 확장자로 구성된 파일들에 대한 File 타입의 배열
	 */
	public static File[] listFilesIncludeExt(String dir, boolean recursive, String... extList) {
		
		Collection<File> resultFiles = org.apache.commons.io.FileUtils.listFiles(new File(dir), extList,  recursive);
		
		return org.apache.commons.io.FileUtils.convertFileCollectionToFileArray(resultFiles);		
	}
	
	/**
	 * 지정한 파일 확장자를 제외한 파일들의 파일명 리스트를 가져온다.
	 * 
	 * @param dir 디렉토리 
	 * @param recursive 하위 폴더 내부의 파일들에 대한 작업 유무
	 * @param extList 포함시킬 확장자 리스트
	 * @return 지정한 파일 확장자를 제외한 파일들의 파일명 리스트
	 */
	public static List<String> listFilenamesExcludeExt(String dir, boolean recursive, String... extList) {
		
		IOFileFilter suffixFileFilters = new SuffixFileFilter(extList, IOCase.INSENSITIVE);
		IOFileFilter excludeExtFilter = FileFilterUtils.notFileFilter(FileFilterUtils.or(suffixFileFilters));
    	
    	List<File> resultFiles = (List<File>) org.apache.commons.io.FileUtils.listFiles(new File(dir), excludeExtFilter, TrueFileFilter.INSTANCE);
		
		List<String> resultList = new ArrayList<String>();
		
		for (File file : resultFiles) {
			resultList.add(file.getAbsolutePath());
		}
		
		return resultList;		
	}

	/**
	 * 지정한 파일 확장자를 제외한 파일들을 File 타입의 배열로 가져온다.
	 * 
	 * @param dir 디렉토리 
	 * @param recursive 하위 폴더 내부의 파일들에 대한 작업 유무
	 * @param extList 포함시킬 확장자 리스트
	 * @return 지정한 파일 확장자를 제외한 파일들에 대한 File 타입의 배열
	 */
	public static File[] listFilesExcludeExt(String dir, boolean recursive, String... extList) {
		
		IOFileFilter suffixFileFilters = new SuffixFileFilter(extList, IOCase.INSENSITIVE);
		IOFileFilter excludeExtFilter = FileFilterUtils.notFileFilter(FileFilterUtils.or(suffixFileFilters));
    	
		Collection<File> resultFiles = org.apache.commons.io.FileUtils.listFiles(new File(dir), excludeExtFilter, TrueFileFilter.INSTANCE);

		return org.apache.commons.io.FileUtils.convertFileCollectionToFileArray(resultFiles);		
	}
	
	/**
	 * 지정한 와일드카드가 포함된 파일들에 대한 파일명의 리스트를 가져온다.<br>
	 * 예) 와일드 카드: "test*.*" - test로 시작되는 파일 전부
	 * 
	 * @param dir 디렉토리
	 * @param wildcards 포함시킬 와일드카드
	 * @param recursive 하위 폴더 내부의 파일들에 대한 작업 유무
	 * @return 지정한 와일드카드가 포함된 파일들에 대한 파일명의 리스트
	 */
	public static List<String> listFilenamesByWildcard(String dir, String[] wildcards, boolean recursive) {
		
		IOFileFilter wildcardFileFileter = new WildcardFileFilter(wildcards, IOCase.INSENSITIVE);
		
		IOFileFilter recursiveDirFilter = null;
		
		if (recursive) {
			recursiveDirFilter = TrueFileFilter.INSTANCE;
		}
		
		Collection<File> resultFiles = org.apache.commons.io.FileUtils.listFiles(new File(dir), wildcardFileFileter, recursiveDirFilter);
		
		List<String> resultList = new ArrayList<String>();
		
		for (File file : resultFiles) {
			resultList.add(file.getAbsolutePath());
		}
		
		return resultList;		
	}

	/**
	 * 지정한 와일드카드가 포함된 파일들에 대해 File 타입의 배열로 가져온다.<br>
	 * 예) 와일드 카드: "test*.*" - test로 시작되는 파일 전부
	 * 
	 * @param dir 디렉토리
	 * @param wildcards 포함시킬 와일드카드
	 * @param recursive 하위 폴더 내부의 파일들에 대한 작업 유무
	 * @return 지정한 와일드카드가 포함된 파일들에 대해 File 타입의 배열
	 */
	public static File[] listFilesByWildcard(String dir, String[] wildcards, boolean recursive) {
		
		
		IOFileFilter wildcardFileFileter = new WildcardFileFilter(wildcards, IOCase.INSENSITIVE);
		
		IOFileFilter recursiveDirFilter = null;
		
		if (recursive) {
			recursiveDirFilter = TrueFileFilter.INSTANCE;
		}
		
		Collection<File> resultFiles = org.apache.commons.io.FileUtils.listFiles(new File(dir), wildcardFileFileter, recursiveDirFilter);
		
		return org.apache.commons.io.FileUtils.convertFileCollectionToFileArray(resultFiles);		
	}
	
	/**
	 * 지정한 와일드카드를 포함하지않는 파일들에 대한 파일명의 리스트를 가져온다.<br>
	 * 예) 와일드 카드: "test*.*" - test로 시작되는 파일 전부 제외
	 * 
	 * @param dir 디렉토리
	 * @param wildcards 제외시킬 와일드카드
	 * @param recursive 하위 폴더 내부의 파일들에 대한 작업 유무
	 * @return 지정한 와일드카드에 포함되지않는 파일들에 대한 파일명의 리스트
	 */
	public static List<String> listExcludeFilenamesByWildcard(String dir, String[] wildcards, boolean recursive) {
		
		IOFileFilter wildcardExcludeFileFileter = new WildcardExcludeFileFilter(wildcards, IOCase.INSENSITIVE);
		
		IOFileFilter recursiveDirFilter = null;
		
		if (recursive) {
			recursiveDirFilter = TrueFileFilter.INSTANCE;
		}
		
		Collection<File> resultFiles = org.apache.commons.io.FileUtils.listFiles(new File(dir), wildcardExcludeFileFileter, recursiveDirFilter);
		
		List<String> resultList = new ArrayList<String>();
		
		for (File file : resultFiles) {
			resultList.add(file.getAbsolutePath());
		}
		
		return resultList;		
	}
	
	/**
	 * 지정한 와일드카드를 포함하지않는 파일들에 대해 File 타입의 배열로 가져온다.<br>
	 * 예) 와일드 카드: "test*.*" - test로 시작되는 파일 전부 제외
	 * 
	 * @param dir 디렉토리
	 * @param wildcards 제외시킬 와일드카드
	 * @param recursive 하위 폴더 내부의 파일들에 대한 작업 유무
	 * @return 지정한 와일드카드에 포함되지않는 파일들에 대해 File 타입의 배열
	 */
	public static File[] listExcludeFilesByWildcard(String dir, String[] wildcards, boolean recursive) {
		
		
		IOFileFilter wildcardExcludeFileFileter = new WildcardExcludeFileFilter(wildcards, IOCase.INSENSITIVE);
		
		IOFileFilter recursiveDirFilter = null;
		
		if (recursive) {
			recursiveDirFilter = TrueFileFilter.INSTANCE;
		}
		
		Collection<File> resultFiles = org.apache.commons.io.FileUtils.listFiles(new File(dir), wildcardExcludeFileFileter, recursiveDirFilter);
		
		return org.apache.commons.io.FileUtils.convertFileCollectionToFileArray(resultFiles);		
	}
	
	/**
	 * 원본 디렉토리가 대상 디렉토리 명으로 이동한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveDir(File srcDir, File destDir) {
		return moveDir(srcDir, destDir, true);
	}
	
	/**
	 * 원본 디렉토리가 대상 디렉토리 명으로 이동한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveDir(String srcDir, String destDir) {
		return moveDir(srcDir, destDir, true);
	}
	
	/**
	 * 원본 디렉토리가 대상 디렉토리 명으로 이동한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveDir(File srcDir, File destDir, boolean preserveFileDate) {

		Result result = Result.EMPTY;
		
		if (!srcDir.exists()) {
			return buildFailResult(srcDir + "가 존재하지 않습니다.");
		}

		if (isNotDir(srcDir)) {
			return buildFailResult(srcDir + "는 디렉토리가 아닙니다.");
		}

		if (destDir.exists()) {
			
			result = deleteDir(destDir);
			
			if (result == Result.FAIL) {
				return buildFailResult(destDir + " 디렉토리 삭제중에 에러가 발생하였습니다.");
			}
		}
		
		result = copyDir(srcDir, destDir, preserveFileDate);
		
		if (result == Result.FAIL) {
			return buildFailResult(srcDir + " 디렉토리 복사중에 에러가 발생하였습니다.");
		}
            
		result = deleteDir(srcDir);
		
		if (result == Result.FAIL) {
			return buildFailResult(srcDir + " 디렉토리 삭제중에 에러가 발생하였습니다.");
		}
		
		return Result.SUCCESS;
	}
	
	/**
	 * 원본 디렉토리가 대상 디렉토리 명으로 이동한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param preserveFileDate 수정일 설정 플래그            
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveDir(String srcDir, String destDir, boolean preserveFileDate) {
		return moveDir(new File(srcDir), new File(destDir), preserveFileDate);
	}

	/**
	 * 원본 디렉토리가 대상 디렉토리 하위로 이동한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveDirToDir(File srcDir, File destDir) {
		return moveDirToDir(srcDir, destDir, true);
	}

	/**
	 * 원본 디렉토리가 대상 디렉토리 하위로 이동한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveDirToDir(String srcDir, String destDir) {
		return moveDirToDir(new File(srcDir), new File(destDir), true);
	}
	
	/**
	 * 원본 디렉토리가 대상 디렉토리 하위로 이동한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveDirToDir(File srcDir, File destDir, boolean preserveFileDate) {

		Result result = Result.EMPTY;
		
		if (isNotDir(srcDir)) {
			return buildFailResult(srcDir + "는 디렉토리가 아닙니다.");
		}
		
		if (notExistsDir(destDir)) {
			
			result = createDir(destDir.getAbsolutePath());
			
			if (result == Result.FAIL) {
				return buildFailResult(destDir + " 디렉토리 생성 중에 에러가 발생하였습니다.");
			}
		}
		
		if (isNotDir(destDir)) {
			return buildFailResult(destDir + "는 디렉토리가 아닙니다.");
		}
		
		
		String subDir = destDir.getAbsolutePath() + "/" + srcDir.getName();
		File destSubDir = new File(subDir);

		if (destSubDir.exists() && destSubDir.isDirectory()) {
			
			result = deleteDir(destSubDir);
			
			if (result == Result.FAIL) {
				return buildFailResult(destDir + " 디렉토리 삭제중에 에러가 발생하였습니다.");
			}
		}

		if (FileUtils.notExistsDir(subDir)) {
			
			result = createDir(subDir);
			
			if (result == Result.FAIL) {
				return buildFailResult(destDir + " 디렉토리 생성 중에 에러가 발생하였습니다.");
			}
		}
		
		result = moveDir(srcDir, destSubDir, preserveFileDate);
		
		if (result == Result.FAIL) {
			return buildFailResult(srcDir + " 이동 중에 에러가 발생하였습니다.");
		}

		return Result.SUCCESS;
	}

	/**
	 * 원본 디렉토리가 대상 디렉토리 하위로 이동한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveDirToDir(String srcDir, String destDir, boolean preserveFileDate) {
		return moveDirToDir(new File(srcDir), new File(destDir), preserveFileDate);
	}
	
	/**
	 * 원본 파일을 대상 파일 명으로 이동한다.
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveFile(File srcFile, File destFile) {
		return moveFile(srcFile, destFile, true);
	}
	
	/**
	 * 원본 파일을 대상 파일 명으로 이동한다.
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveFile(String srcFile, String destFile) {
		return moveFile(new File(srcFile), new File(destFile), true);
	}
	
	/**
	 * 원본 파일을 대상 파일 명으로 이동한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveFile(File srcFile, File destFile, boolean preserveFileDate) {

		Result result;

		if (!srcFile.exists()) {
			return buildFailResult(srcFile + "가 존재하지 않습니다.");
		}

		if (isNotFile(srcFile)) {
			return buildFailResult(srcFile + "는 파일이 아닙니다.");
		}

		if (destFile.exists()) {
			
			result = deleteFile(destFile);
			
			if (result == Result.FAIL) {
				return buildFailResult(destFile + " 파일 삭제중에 에러가 발생하였습니다.");
			}
		}
		
		result = copyFile(srcFile, destFile, preserveFileDate);
		
		if (result == Result.FAIL) {
			return buildFailResult(srcFile + " 파일 복사중에 에러가 발생하였습니다.");
		}
            
		result = deleteFile(srcFile);
		
		if (result == Result.FAIL) {
			return buildFailResult(srcFile + " 파일 삭제중에 에러가 발생하였습니다.");
		}
		
		return Result.SUCCESS;
	}
	
	/**
	 * 원본 파일을 대상 파일 명으로 이동한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveFile(String srcFile, String destFile, boolean preserveFileDate) {
		return moveFile(new File(srcFile), new File(destFile), preserveFileDate);
	}
	
	/**
	 * 원본 파일을 대상 디렉토리 하위로 이동한다.
	 * 
	 * @param srcFile 원본 파일
	 * @param destDir 대상 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveFileToDir(File srcFile, File destDir) {
		return moveFileToDir(srcFile, destDir, true);
	}
	
	/**
	 * 원본 파일을 대상 디렉토리 하위로 이동한다.
	 * 
	 * @param srcFile 원본 파일
	 * @param destDir 대상 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveFileToDir(String srcFile, String destDir) {
		return moveFileToDir(new File(srcFile), new File(destDir), true);
	}
	
	/**
	 * 원본 파일을 대상 디렉토리 하위로 이동한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcFile 원본 파일
	 * @param destDir 대상 디렉토리
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveFileToDir(File srcFile, File destDir, boolean preserveFileDate) {

		Result result;
		
		if (isNotFile(srcFile)) {
			return buildFailResult(srcFile + "은 파일이 아닙니다.");
		}
		
		if (FileUtils.notExistsDir(destDir)) {
			
			result = createDir(destDir.getAbsolutePath());
			
			if (result == Result.FAIL) {
				return buildFailResult(destDir + " 디렉토리 생성 중에 에러가 발생하였습니다.");
			}
		}

		if (isNotDir(destDir)) {
			return buildFailResult(srcFile + "는 디렉토리가 아닙니다.");
		}
		
		String destFile = destDir + File.separator + srcFile.getName();
		
		result = moveFile(srcFile, new File(destFile), preserveFileDate);

		if (result == Result.FAIL) {
			return buildFailResult(srcFile + " 이동 중에 에러가 발생하였습니다.");
		}
		
		return Result.SUCCESS;
	}
	
	/**
	 * 원본 파일을 대상 디렉토리 하위로 이동한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcFile 원본 파일
	 * @param destDir 대상 디렉토리
	 * @param preserveFileDate 수정일 설정 플래그            
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveFileToDir(String srcFile, String destDir, boolean preserveFileDate) {
		return moveFileToDir(new File(srcFile), new File(destDir), preserveFileDate);
	}
	
	

	/**
	 * 대상 파일을 FileInputStream으로 반환한다.<br>
	 * 
	 * @param file 대상 파일
	 * @return 대상 파일에 대한 FileInputStream
	 */
	public static FileInputStream openInputStream(File file) {

		if (file.exists()) {

			if (file.isDirectory()) {
				return null;
			}

			if (!file.canRead()) {
				return null;
			}

		} else {
			return null;
		}

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return fis;
	}
	
	/**
	 * 대상 파일을 FileInputStream으로 반환한다.<br>
	 * 
	 * @param filePath 대상 파일 경로
	 * @return 대상 파일에 대한 FileInputStream
	 */
	public static FileInputStream openInputStream(String filePath) {
		return openInputStream(new File(filePath));
	}

	/**
	 * 대상 파일을 outputStream으로 반환한다.
	 * 
	 * @param file 대상 파일
	 * @return 대상 파일에 대한 FileOutputStream
	 */
	public static FileOutputStream openOutputStream(File file) {
		return openOutputStream(file, false);
	}
	
	/**
	 * 대상 파일을 outputStream으로 반환한다. append가 true이면 출력시 기존 파일의 내용 마지막에 덧붙인다.
	 * 
	 * @param file   대상 파일
	 * @param append 기존 파일에 내용을 덧붙일지에 대한 플래그
	 * @return 대상 파일에 대한 FileOutputStream
	 */
	public static FileOutputStream openOutputStream(File file, boolean append) {

		if (file.exists()) {

			if (file.isDirectory()) {
				return null;
			}
			if (!file.canWrite()) {
				return null;
			}
		} else {
			File parent = file.getParentFile();
			if (parent != null) {
				if (!parent.mkdirs() && !parent.isDirectory()) {
					return null;
				}
			}
		}

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file, append);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fos;
	}
	
	/**
	 * 대상 파일을 outputStream으로 반환한다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @return 대상 파일에 대한 FileOutputStream
	 */
	public static FileOutputStream openOutputStream(String filePath) {
		return openOutputStream(new File(filePath), false);
	}
	
	/**
	 * 대상 파일을 outputStream으로 반환한다. append가 true이면 출력시 기존 파일의 내용 마지막에 덧붙인다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @param append   기존 파일에 내용을 덧붙일지에 대한 플래그
	 * @return 대상 파일에 대한 FileOutputStream
	 */
	public static FileOutputStream openOutputStream(String filePath, boolean append) {
		return openOutputStream(new File(filePath), append);		
	}
	
	/**
	 * 대상 파일을 FileReader로 반환한다.
	 * 
	 * @param file 대상 파일
	 * @return 대상 파일에 대한 FileReader
	 */
	public static FileReader openReader(File file) {
		
		if (file.exists()) {

			if (file.isDirectory()) {
				return null;
			}

			if (!file.canRead()) {
				return null;
			}

		} else {
			return null;
		}

		FileReader fileReader = null;

		try {
			fileReader = new FileReader(file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fileReader;
	}
	
	/**
	 * 대상 파일을 FileReader로 반환한다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @return 대상 파일에 대한 FieReader
	 */
	public static FileReader openReader(String filePath) {
		return openReader(new File(filePath));
	}
    
    /**
	 * 대상 파일을 FileWriter로 반환한다.
	 * 
	 * @param file 대상 파일
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static FileWriter openWriter(File file) { 
		return openWriter(file, false);
	}
    
    /**
	 * 대상 파일을 FileWriter로 반환한다. append가 true이면 출력시 기존 파일의 내용 마지막에 덧붙인다.
	 * 
	 * @param file 대상 파일
	 * @param append 기존 파일에 내용을 덧붙일지에 대한 플래그
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static FileWriter openWriter(File file, boolean append) {

		if (file.exists()) {

			if (file.isDirectory()) {
				return null;
			}
			if (!file.canWrite()) {
				return null;
			}
		} else {
			
			File parent = file.getParentFile();
			
			if (parent != null) {
				
				if (!parent.mkdirs() && !parent.isDirectory()) {
					return null;
				}
			}
		}

		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter(file, append);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileWriter;
	}
    
    /**
	 * 대상 파일을 FileWriter로 반환한다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static FileWriter openWriter(String filePath) { 
		return openWriter(new File(filePath));
	}
    
    /**
	 * 대상 파일을 FileWriter로 반환한다. append가 true이면 출력시 기존 파일의 내용 마지막에 덧붙인다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @param append 기존 파일에 내용을 덧붙일지에 대한 플래그
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static FileWriter openWriter(String filePath, boolean append) { 
		return openWriter(new File(filePath), append);
	}
	
	/**
	 * 대상 파일을 BufferedWriter로 반환한다.
	 * 
	 * @param file 대상 파일
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static BufferedWriter openBufferWriter(File file) { 
		return openBufferWriter(file, "UTF-8", false);
	}
	
	/**
	 * 대상 파일을 BufferedWriter로 반환한다.
	 * 
	 * @param file 대상 파일
	 * @param charSet 인코딩에 사용할 캐릭터셋
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static BufferedWriter openBufferWriter(File file, String charSet) { 
		return openBufferWriter(file, charSet, false);
	}
    
    /**
	 * 대상 파일을 BufferedWriter로 반환한다. append가 true이면 출력시 기존 파일의 내용 마지막에 덧붙인다.
	 * 
	 * @param file 대상 파일
	 * @param charSet 인코딩에 사용할 캐릭터셋
	 * @param append 기존 파일에 내용을 덧붙일지에 대한 플래그
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static BufferedWriter openBufferWriter(File file, String charSet, boolean append) {

		if (file.exists()) {

			if (file.isDirectory()) {
				return null;
			}
			
			if (!file.canWrite()) {
				return null;
			}
			
		} else {
			
			File parent = file.getParentFile();
			
			if (parent != null) {
				
				if (!parent.mkdirs() && !parent.isDirectory()) {
					return null;
				}
			}
		}
		
		BufferedWriter bufferedWriter = null; 
		
		try {
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charSet));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bufferedWriter;
	}
    
    /**
	 * 대상 파일을 BufferedWriter로 반환한다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static BufferedWriter openBufferWriter(String filePath) { 
		return openBufferWriter(new File(filePath), "UTF-8", false);
	}
	
	/**
	 * 대상 파일을 BufferedWriter로 반환한다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @param charSet 인코딩에 사용할 캐릭터셋
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static BufferedWriter openBufferWriter(String filePath, String charSet) { 
		return openBufferWriter(new File(filePath), charSet, false);
	}
    
    /**
	 * 대상 파일을 BufferedWriter로 반환한다. append가 true이면 출력시 기존 파일의 내용 마지막에 덧붙인다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @param append 기존 파일에 내용을 덧붙일지에 대한 플래그
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static BufferedWriter openBufferWriter(String filePath, boolean append) { 
		return openBufferWriter(new File(filePath), "UTF-8", append);
	}
	
	/**
	 * 대상 파일을 BufferedWriter로 반환한다. append가 true이면 출력시 기존 파일의 내용 마지막에 덧붙인다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @param charSet 인코딩에 사용할 캐릭터셋
	 * @param append 기존 파일에 내용을 덧붙일지에 대한 플래그
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static BufferedWriter openBufferWriter(String filePath, String charSet, boolean append) { 
		return openBufferWriter(new File(filePath), charSet, append);
	}
    
    /**
	 * 파일을 읽어 들인 후 바이트 배열로 반환한다.<br>
	 * 
	 * @param file 대상 파일
	 * @return 원본 파일의 byte[]
	 */
	public static byte[] readFileToByteArray(File file) {

		InputStream in = null;

		try {
			in = openInputStream(file);
			return IOUtils.toByteArray(in, file.length());
		} catch (Exception e) {
			return null;
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
    
    /**
	 * 파일을 읽어 들인 후 바이트 배열로 반환한다.<br>
	 * 
	 * @param file 대상 파일 경로
	 * @return 원본 파일의 byte[]
	 */
	public static byte[] readFileToByteArray(String file) {
		return readFileToByteArray(new File(file));
	}
	
    /**
     * 파일을 읽어 들인 후 지정한 charset으로 인토딩한 문자열을 반환한다.
     * 
     * @param file 대상 파일 경로
     * @param encoding 인코딩할 charset
     * @return 파일 내용을 지정한 charset으로 인코딩한 문자열
     */
    public static String readFileToString(File file, Charset encoding) {
        
    	InputStream in = null;
        in = openInputStream(file);
        
        try {
			return IOUtils.toString(in, Charsets.toCharset(encoding));
		} catch (Exception e) {
			return null;
		} finally {
			IOUtils.closeQuietly(in);
		}
    }
    
    /**
     * 파일을 읽어 들인 후 지정한 charset으로 인토딩한 문자열을 반환한다.
     * 
     * @param file 대상 파일 경로
     * @param encoding 인코딩할 charset 문자열
     * @return 파일 내용을 지정한 charset으로 인코딩한 문자열
     */
    public static String readFileToString(File file, String encoding) {
    	return readFileToString(file, Charsets.toCharset(encoding));
    }

    /**
     * 파일을 읽어 들인 후 문자열을 반환한다.
     * 
     * @param file 대상 파일 경로
     * @return 파일 내용을 지정한 charset으로 인코딩한 문자열
     */
    public static String readFileToString(File file) {
        return readFileToString(file, Charset.defaultCharset());
    }
    
    /**
	 * 원본 파일명을 대상 파일 명으로 변경한다.<br>
	 * 파일변경후 원본 파일 삭제
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result rename(String srcFile, String destFile) {
		
		Result result;
		
		if (!existsFile(srcFile)) {
			return buildFailResult(srcFile + "가 존재하지 않습니다.");
		}
		
		if (existsFile(destFile)) {
			
			result = deleteFile(destFile);

			if (result == Result.FAIL) {
				return buildFailResult(destFile + " 삭제 중에 에러가 발생하였습니다");
			}
		}
		
		File file = new File(srcFile);
		
		if (!file.renameTo(new File(destFile))) {
			
			result = copyFile(srcFile, destFile, false);

			if (result == Result.SUCCESS) {
				
				result = deleteFile(srcFile);
				
				if (result == Result.FAIL) {
					deleteFile(destFile);
				}
			}

			return result;
			
		} else {
			
			return Result.SUCCESS;
		}
	}

	/**
	 * 대상 경로의 폴더에 전체권한을 부여한다.
	 * @param paths 대상 폴더 경로
	 * @throws IOException
	 */
	public static void setAllPermission(String ... paths) throws IOException {
		for (String localPath : paths) {
			Path path = Paths.get(localPath);
			Set<PosixFilePermission> perms = new HashSet<>();
			perms.add(PosixFilePermission.OWNER_READ);
			perms.add(PosixFilePermission.OWNER_WRITE);
			perms.add(PosixFilePermission.OWNER_EXECUTE);

			perms.add(PosixFilePermission.GROUP_READ);
			perms.add(PosixFilePermission.GROUP_WRITE);
			perms.add(PosixFilePermission.GROUP_EXECUTE);

			perms.add(PosixFilePermission.OTHERS_READ);
			perms.add(PosixFilePermission.OTHERS_WRITE);
			perms.add(PosixFilePermission.OTHERS_EXECUTE);
			Files.setPosixFilePermissions(path, perms);

		}
	}

	/**
	 * URL에서 파일명을 추출한다.
	 * @param url
	 * @return
	 */
	public static String getFilenameByUrl(String url) {
		return url.substring(url.lastIndexOf("/") + 1, url.length());
	}

    /**
     * 대상 String 파일들을 ZIP FILE로 생성
     * @param zipFile 생성될 Zipfile FULL PATH
     * @param targetFiles ZipFile 대상
     * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
     */
    public static Result makeZipFile(String zipFile, List<String> targetFiles) {

        byte[] buffer = new byte[1024];

        try{

        	if (CollectionUtils.isNotEmpty(targetFiles)) {

        		@Cleanup FileOutputStream fos = new FileOutputStream( zipFile);
				@Cleanup ZipOutputStream zos = new ZipOutputStream(fos);

				for (String path : targetFiles) {

					File srcFile = new File( path);

					if (notExistsFile(srcFile)) {
						return buildFailResult(srcFile.getName() + "가 존재하지 않습니다.");
					}

					FileInputStream fis = new FileInputStream(srcFile);
					zos.putNextEntry(new ZipEntry( srcFile.getName()));

					int len;
					while ((len = fis.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}

					zos.closeEntry();
					fis.close();
				}
				zos.close();

            } else {
                buildFailResult("FILE 이 없습니다.");
            }

        } catch(IOException ex){
            ex.printStackTrace();
            return buildFailResult(ex.getMessage());
        }
        return Result.SUCCESS;
    }

    /**
     * 대상 FILE객체 파일들을 ZIP FILE로 생성
     * @param zipFile 생성될 Zipfile FULL PATH
     * @param targetFiles ZipFile 대상
     * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
     */
    public static Result makeZipFile( String zipFile, File[] targetFiles){

        byte[] buffer = new byte[1024];
        try{
            if (targetFiles != null && targetFiles.length > 0) {

                FileOutputStream fos = new FileOutputStream( zipFile);
                ZipOutputStream zos = new ZipOutputStream(fos);

                for ( File srcFile : targetFiles){

                    if (notExistsFile(srcFile)) {
                        return buildFailResult(srcFile.getName() + "가 존재하지 않습니다.");
                    }

                    FileInputStream fis = new FileInputStream(srcFile);
                    zos.putNextEntry(new ZipEntry( srcFile.getName()));

                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }

                    zos.closeEntry();
                    fis.close();
                }
                zos.close();

            } else {
                buildFailResult("FILE 이 없습니다.");
            }

        } catch(IOException ex){
            ex.printStackTrace();
            return buildFailResult(ex.getMessage());
        }
        return Result.SUCCESS;
    }

	public enum Result {

		SUCCESS     ("Success", "RESULT_SUCCESS"),
		FAIL        ("Fail",    "RESULT_FAIL"),
		EMPTY       ("Empty",   "RESULT_EMPTY");

		@Getter
		private String code;

		@Getter @Setter
		private String message;

		Result(String code, String message) {
			this.code = code;
			this.message = message;
		}

	}

	public class WildcardExcludeFileFilter extends AbstractFileFilter implements Serializable {

		private static final long serialVersionUID = 8206674584073808869L;

		private final String[] wildcards;

		private final IOCase caseSensitivity;

		public WildcardExcludeFileFilter(String wildcard) {
			this(wildcard, null);
		}

		public WildcardExcludeFileFilter(String wildcard, IOCase caseSensitivity) {
			if (wildcard == null) {
				throw new IllegalArgumentException("The wildcard must not be null");
			}
			this.wildcards = new String[] { wildcard };
			this.caseSensitivity = caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity;
		}

		public WildcardExcludeFileFilter(String[] wildcards) {
			this(wildcards, null);
		}

		public WildcardExcludeFileFilter(String[] wildcards, IOCase caseSensitivity) {
			if (wildcards == null) {
				throw new IllegalArgumentException("The wildcard array must not be null");
			}
			this.wildcards = new String[wildcards.length];
			System.arraycopy(wildcards, 0, this.wildcards, 0, wildcards.length);
			this.caseSensitivity = caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity;
		}

		public WildcardExcludeFileFilter(List<String> wildcards) {
			this(wildcards, null);
		}

		public WildcardExcludeFileFilter(List<String> wildcards, IOCase caseSensitivity) {
			if (wildcards == null) {
				throw new IllegalArgumentException("The wildcard list must not be null");
			}
			this.wildcards = wildcards.toArray(new String[wildcards.size()]);
			this.caseSensitivity = caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity;
		}

		@Override
		public boolean accept(File dir, String name) {
			for (String wildcard : wildcards) {
				if (FilenameUtils.wildcardMatch(name, wildcard, caseSensitivity)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean accept(File file) {
			String name = file.getName();
			for (String wildcard : wildcards) {
				if (FilenameUtils.wildcardMatch(name, wildcard, caseSensitivity)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			buffer.append(super.toString());
			buffer.append("(");
			if (wildcards != null) {
				for (int i = 0; i < wildcards.length; i++) {
					if (i > 0) {
						buffer.append(",");
					}
					buffer.append(wildcards[i]);
				}
			}
			buffer.append(")");
			return buffer.toString();
		}

	}

	public static void main(String[] args) {
		
		List<String> list = listExcludeFilenamesByWildcard("c:/test", new String[] {"seq-*.*"}, false);
		
		for (String str : list) {
			System.out.println(str);			
		}
		
		File[] files = listExcludeFilesByWildcard("c:/test", new String[] {"seq-*.*"}, false); 
		
		for (File file : files) {
			System.out.println(file.getAbsolutePath());			
			System.out.println(file.getName());			
		}
		
	}
}
