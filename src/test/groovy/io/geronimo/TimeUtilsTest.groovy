package io.geronimo

import org.springframework.util.StopWatch
import spock.lang.Specification

import java.util.concurrent.TimeUnit

class TimeUtilsTest extends Specification {

	def "sleep()"() {
		
		setup:
			StopWatch stopWatch = new StopWatch()
			int sleepTime = 1000
		
		when:
			stopWatch.start()
			TimeUtils.sleep(1, TimeUnit.SECONDS)
			stopWatch.stop()
		then:
			sleepTime <= stopWatch.totalTimeMillis 	
	}
}
