package com.kobatera.testing;

import org.apache.log4j.Logger;

/**
 *  It subscribes to the test topic and pulls messages off of the topic.  It sleeps in between
 *  each message for a configurable delay to simulate message processing time.  It runs for the 
 *  configured duration of time.
 *
 */
public class MessageReceiver extends Thread {
	private static Logger logger = Logger.getLogger(MessageReceiver.class);
	private int timeToReceive = 0;
	private int recvDelay = 0;

	public MessageReceiver(int timeToReceive, int recvDelay) {
		super();
		this.timeToReceive = timeToReceive;
		this.recvDelay = recvDelay;
	}

	public void run() {
		logger.debug("Starting Thread: " + this.getId());
		long startTime = System.currentTimeMillis();

		DurableTopicRecvClient client = new DurableTopicRecvClient();

		// Set up the connection
		try {
			client.setupConnection();
			client.startConnection();

		} catch (Exception e) {
			logger.error(e);
		}

		// Loop through receiving messages 
		while ((System.currentTimeMillis() - startTime) < timeToReceive)
			try {
				client.receiveMessage();

			} catch (Exception e) {
				logger.error(e);
			}

			try {
				Thread.sleep(recvDelay);
			} catch (Exception e) {
		}

		// Close the connection 
		try {
			client.closeConnection();

		} catch (Exception e) {
			logger.error(e);
		}

		logger.debug("Ending Thread: " + this.getId());
		logger.info("Thread Execution Time: " + (System.currentTimeMillis() - startTime));
	}
}
