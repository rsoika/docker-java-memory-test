package com.soika.test;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("data")
public class TestResource {

    private static Logger logger = Logger.getLogger(TestResource.class.getName());

	/**
	 * Test GET returns some heavy String with 1M
	 * 
	 * @return
	 */
	@GET
	public Response message() {
		String result=getAlphaNumericString(1024*1024);
		logger.info("...request 1MB test resource...");
		return Response.ok(result).build();
	}

	// function to generate a random string of length n
	static String getAlphaNumericString(int n) {

		// chose a Character random from this String
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

		// create StringBuffer size of AlphaNumericString
		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {
			// generate a random number between
			// 0 to AlphaNumericString variable length
			int index = (int) (AlphaNumericString.length() * Math.random());
			// add Character one by one in end of sb
			sb.append(AlphaNumericString.charAt(index));
		}
		return sb.toString();
	}

}
