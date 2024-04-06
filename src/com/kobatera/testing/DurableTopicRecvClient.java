package com.kobatera.testing;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/**
 * This class sets up a durable topic session to a jms topic.  You can either receive
 * each message manually by calling receiveMessage, or start up a TextListener by calling
 * useListener.  Don't do both.  Call setupConnection, optionally useListener, then startConnection.
 * If getting messages manually, call receiveMessage.
 */
public class DurableTopicRecvClient {
	private TopicConnection conn = null;
	private TopicSession session = null;
	private Topic topic = null;		
	private TopicSubscriber subscriber = null;
	private TextListener topicListener = null;
	private ObjectMessage omsg = null;
	
	private static Logger logger = Logger.getLogger(DurableTopicRecvClient.class);

	public void setupConnection() throws JMSException, NamingException {
		logger.debug("--> Entering");

		InitialContext iniCtx = new InitialContext();
		Object tmp = iniCtx.lookup("ConnectionFactory");
		TopicConnectionFactory tcf = (TopicConnectionFactory) tmp;
		conn = tcf.createTopicConnection("john", "needle");
		conn.setClientID("jms-testsub1");
		topic = (Topic) iniCtx.lookup("topic/test");
		session = conn.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
		subscriber = session.createDurableSubscriber(topic, "jms-testsub1");
		
		logger.debug("--> Leaving");
	}

	public void useListener() throws JMSException, NamingException {
		logger.debug("--> Entering");

		// Set up a Textlistener
        topicListener = new TextListener();
        subscriber.setMessageListener(topicListener);

		logger.debug("--> Leaving");
	}

	public void startConnection() throws JMSException, NamingException {
		logger.debug("--> Entering");

		conn.start();

		logger.debug("--> Leaving");
	}

	public void receiveMessage() throws JMSException, NamingException {
		logger.debug("--> Entering");
		// Setup the pub/sub connection, session

		// Wait up to 5 seconds for the message
		omsg = (ObjectMessage) subscriber.receive(5000);
		
		if (logger.isDebugEnabled()){
			if (omsg == null) {
				logger.debug("Timed out waiting for msg");

			} else {
				Object theMessage = omsg.getObject();
				
				// If the message was put on the queue using log4j, the Object class will be LoggingEvent.  
				// Otherwise it is a String.
				if (theMessage.getClass().getName().equals("org.apache.log4j.spi.LoggingEvent")){
					LoggingEvent le = (LoggingEvent) theMessage;
					logger.debug("Recieved Message: " + le.getMessage());
					
				} else {
					logger.debug("Recieved Message: " + omsg.getObject());
				}
			}
		}
		
		logger.debug("--> Leaving");
	}

	public void closeConnection() throws JMSException {
		logger.debug("--> Entering");
		conn.stop();
		session.close();
		conn.close();
		logger.debug("--> Leaving");
	}

}
