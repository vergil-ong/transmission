package com.ong.http.helpers;

import java.io.IOException;
import java.net.SocketException;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPRetryHandler implements HttpRequestRetryHandler {

	private static Logger logger = LoggerFactory.getLogger(HTTPRetryHandler.class);

	private final int MAX_RETRY_COUNT = 3;

	public static HTTPRetryHandler newInstance() {
		return new HTTPRetryHandler();
	}

	public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
		if (executionCount > this.MAX_RETRY_COUNT) {
			return false;
		}
		if ((exception instanceof SocketException)) {
			logger.warn("HTTP适配器，第{}次重试，重试原因：Connection reset", new Object[] { Integer.valueOf(executionCount) });
			return true;
		}
		return true;
	}

}
