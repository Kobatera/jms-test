package com.kobatera.testing;

import javax.jms.JMSException;

import org.apache.log4j.Logger;

/**
 * This is a very simple class because all of the JMS connection information is in the 
 * Log4j configuration file.  This is also where the performance issue with the Log4j appender is seen.  
 * Each thread refers to the single instance of the Log4j JMS appender, and access to write to that 
 * appender is synchronized.
 *
 */
public class Log4jJMSAppender implements TestJMSAppender {
	private static Logger logger = Logger.getLogger(Log4jJMSAppender.class);
	private static Logger jmsLogger = Logger.getLogger("JMSTopicAppender");

	public void publishMessage(String myMessage) {
		logger.debug("--> Entering");

		jmsLogger.info(myMessage);

		logger.debug("--> Leaving");
	}
	
	public void closeConnection() throws JMSException{
		// Implemented for the interface
	}

}
