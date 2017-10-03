package io.geronimo;


import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * 대기시간(sleep)을 지정하기 위해서 사용되는 Thread.sleep은 checkedException이다.<br>
 * 불필요한 try-catch를 생략하기위해 runtimeException으로 감싸는 wrapper 클래스를 제공한다.<br><br>
 *
 * NTP, UDP 시간 싱크를 위한 유틸리티를 제공한다.
 * 
 * @author tw.jang
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public final class TimeUtils {

	public static final int DEFAULT_TIMEOUT = 3000;

	public static final String[] NTP_HOSTS = new String[] { "kr.pool.ntp.org", "time.nist.gov", "time.nuri.net", "time.bora.net" };

	private static TimeInfo timeInfo;

	public static void resetCurrentTime() {

		for (String host: NTP_HOSTS) {

			NTPUDPClient client = new NTPUDPClient();
			client.setDefaultTimeout(DEFAULT_TIMEOUT);

			try {
				TimeInfo info = client.getTime(InetAddress.getByName(host));

				if (info != null) {
					info.computeDetails();
					timeInfo = info;
					break;
				}
			} catch (IOException e) {
				log.error("TimeUtils-resetCurrentTime :: {}", e.getMessage());
			} finally {
				client.close();
			}
		}

		log.debug("TimeUtils-resetCurrentTime :: timeInfo -> {}", timeInfo);
	}

	public static long computeTimeGap() {
		if (timeInfo == null) {
			resetCurrentTime();
		}

		return timeInfo.getOffset() + timeInfo.getDelay();
	}

	public static long currentTimeMillis() {
		return System.currentTimeMillis() + computeTimeGap();
	}

	public static void sleep(long time, TimeUnit timeUnit) {
		
		long sleepTime = TimeUnit.MILLISECONDS.convert(time, timeUnit);
		
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			log.error("TimeUtils-sleep :: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
