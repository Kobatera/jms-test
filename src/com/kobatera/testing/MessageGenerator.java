package com.kobatera.testing;

import org.apache.log4j.Logger;

/**
 * This class instantiates the appropriate type of JMS appender being used for this test.  It then
 * executes a loop for the configured number of iterations publishing a message to the JMS queue.
 * There is a configured delay between publishing each message.  The message is a String, however it
 * is put on the queue as an ObjectMessage.  This is because the Log4j appender can only put ObjectMessage
 * types on the queue.  The TextMessage type should serialize faster, but is not supported by Log4j.
 *
 */
public class MessageGenerator extends Thread {
	private static Logger logger = Logger.getLogger(MessageGenerator.class);
	private int numMessages = 500;
	private int msgDelay = 100;
	private TestStatusObject testStatus;
	private ThreadStatusObject threadStatus;

	public MessageGenerator(int numMessages, int msgDelay) {
		super();
		this.numMessages = numMessages;
		this.msgDelay = msgDelay;

		testStatus = TestStatusObject.getInstance();
		threadStatus = new ThreadStatusObject();
		testStatus.addThreadStatus(this.getId(), threadStatus);
		logger.debug("Creating thread status object: " + this.getId());
	}

	public void run() {
		logger.debug("Starting Thread: " + this.getId());
		long startTime = System.currentTimeMillis();
		threadStatus.setThreadRunning(true);
		threadStatus.setStartTime(startTime);

		TestJMSAppender msgAppender = null;
		
		// use either a native JMS appender, or use Log4j JMS Appender
		if (testStatus.isNativeJMS()){
			try {
				msgAppender = new NativeJMSAppender();
	
			} catch (Exception e) {
				logger.error("Unable to create message publisher", e);
				logger.debug("Ending Thread: " + this.getId());
				return;
			}
		} else {
			msgAppender = new Log4jJMSAppender();
		}

		// Generate the messages
		for (int i = 1; i <= numMessages; i++) {
			msgAppender.publishMessage("Message submitted at: " + System.currentTimeMillis());
			threadStatus.setMessagesProcessed(i);

			try {
				Thread.sleep(msgDelay);
			} catch (Exception e) {
			}
		}

		// Close the JMS connection
		try {
			msgAppender.closeConnection();

		} catch (Exception e) {
			logger.error("Error closing JMS connection", e);
		}

		long endTime = System.currentTimeMillis();
		logger.debug("Ending Thread: " + this.getId());
		logger.info("Thread Execution Time: " + (endTime - startTime));

		threadStatus.setEndTime(endTime);
		threadStatus.setThreadRunning(false);
	}

}
