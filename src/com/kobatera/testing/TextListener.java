package com.kobatera.testing;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

public class TextListener implements MessageListener {
	/**
	 * Casts the message to a TextMessage and displays its text.
	 * 
	 * @param message
	 *            the incoming message
	 */
	private static Logger logger = Logger.getLogger(TextListener.class);

	public void onMessage(Message message) {
		TextMessage msg = null;

		try {
			if (message instanceof TextMessage) {
				msg = (TextMessage) message;
				logger.debug("Reading message: " + msg.getText());
			} else {
				logger.debug("Message of wrong type: " + message.getClass().getName());
			}
		} catch (JMSException e) {
			logger.debug("JMSException in onMessage(): " + e.toString());
		} catch (Throwable t) {
			logger.debug("Exception in onMessage():" + t.getMessage());
		}
	}

}
