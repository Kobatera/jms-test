package com.kobatera.testing;

import javax.jms.JMSException;

public interface TestJMSAppender {
	public void publishMessage(String msg);
	public void closeConnection() throws JMSException;
}
