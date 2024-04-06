package com.kobatera.testing;

import java.io.IOException;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * This class looks more complicated than the Log4jJMSAppender, but it really just contains the 
 * same items that are in the Log4j configuration file.  The big difference in performance comes 
 * from the fact that each thread is creating its own session with the JMS service so the messages 
 * are published in parallel rather than synchronized through a single object
 *
 */
public class NativeJMSAppender implements TestJMSAppender {
	private TopicConnection conn = null;
	private TopicSession session = null;
	private Topic topic = null;
	private TopicPublisher publisher = null;
	private ObjectMessage omsg = null;
	private Properties jmsProperties = null; 

	private static Logger logger = Logger.getLogger(NativeJMSAppender.class);

	NativeJMSAppender() throws JMSException, NamingException, IOException {
		logger.debug("--> Entering");

		jmsProperties = JMSConfiguration.getInstance().getPropertiesObject();
		
		if (logger.isDebugEnabled()){
			logger.debug("ConnectionFactory: " + jmsProperties.getProperty("ConnectionFactory"));
			logger.debug("TopicBindingName: " + jmsProperties.getProperty("TopicBindingName"));
			logger.debug("ProviderURL: " + jmsProperties.getProperty("ProviderURL"));
			logger.debug("InitialContextFactoryName: " + jmsProperties.getProperty("InitialContextFactoryName"));
			logger.debug("TopicConnectionFactoryBindingName: " + jmsProperties.getProperty("TopicConnectionFactoryBindingName"));
		}
		
		InitialContext iniCtx = new InitialContext();
		iniCtx.addToEnvironment("Context.INITIAL_CONTEXT_FACTORY", jmsProperties.getProperty("InitialContextFactoryName"));
		iniCtx.addToEnvironment("Context.PROVIDER_URL", jmsProperties.getProperty("ProviderURL"));
		
		Object tmp = iniCtx.lookup(jmsProperties.getProperty("ConnectionFactory"));
		TopicConnectionFactory tcf = (TopicConnectionFactory) tmp;
		topic = (Topic) iniCtx.lookup(jmsProperties.getProperty("TopicBindingName"));
		
		conn = tcf.createTopicConnection();
		session = conn.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
		publisher = session.createPublisher(topic);
		conn.start();
		omsg = session.createObjectMessage();

		logger.debug("--> Leaving");
	}

	public void publishMessage(String msg)  {
		logger.debug("--> Entering");

		try {
			// Using an Object message for this appender because that is the only type
			// that log4j puts into the JMS queue.  Using the same type here so that 
			// performance differences aren't because of the message type. 
			// Serialization of the Object type is slower than using other types.
			omsg.setObject(msg);
			publisher.publish(omsg);

		} catch (Exception e){
			logger.error("Unable to publish message", e);
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
