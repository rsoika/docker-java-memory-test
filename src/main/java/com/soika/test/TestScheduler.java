package com.soika.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;

/**
 * The TestScheduler gerneates some internal traefik by just callong the rest
 * api endpoint
 * 
 */
@Startup
@Singleton
@LocalBean
public class TestScheduler {

	@Resource
	javax.ejb.TimerService timerService;

	private static Logger logger = Logger.getLogger(TestScheduler.class.getName());

	/**
	 * Initialize Timer
	 */
	@PostConstruct
	public void init() {

		logger.info("Starting TestScheduler - initalDelay=" + 10000 + "ms  inverval=" + 500 + "ms ....");
		// Registering a non-persistent Timer Service.
		final TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(""); // empty info string indicates no JSESSIONID!
		timerConfig.setPersistent(false);
		timerService.createIntervalTimer(10000, 500, timerConfig);

	}

	/**
	 * This method calling the rest api endpoint
	 * 
	 * @throws ArchiveException
	 */
	@Timeout
	public void run(Timer timer) {
		try {
			long l=System.currentTimeMillis();
			URL url = new URL("http://localhost:8080/kubernetes-memory-test/api/data");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			int status = con.getResponseCode();
			// Finally, let's read the response of the request
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();
			logger.info("GET response=" + status +"  completed in " + (System.currentTimeMillis()-l) + "ms");
		} catch (IOException e) {
			// something went wrong
			e.printStackTrace();
		}
	}

}
