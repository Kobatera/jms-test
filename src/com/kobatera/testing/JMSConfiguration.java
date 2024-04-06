package com.kobatera.testing;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class JMSConfiguration {
	private static JMSConfiguration jmsConfig = null;
	private static Logger logger = Logger.getLogger(JMSConfiguration.class);
	private Properties props = new Properties();

	private JMSConfiguration() throws IOException{
		ReadConfiguration();
	}

	public static JMSConfiguration getInstance() throws IOException {
		if (jmsConfig == null) {
			jmsConfig = new JMSConfiguration();
		}
		return jmsConfig;
	}

	private void ReadConfiguration() throws IOException {
		logger.debug("--> Entering");
		
		String fileLocation = System.getProperty("JMSConfigFile");
		if(fileLocation == null || fileLocation.equals("")){
			fileLocation = "jms-test.properties";
		}
		
		try {
			props.load(new FileInputStream(fileLocation));
			
		} catch (IOException ioe){
			logger.error("Unable to find the JMS config file: " + fileLocation);
			throw ioe;
		}
		
		logger.debug("--> Leaving");
	}

	public Properties getPropertiesObject() {
		return props;
	}

}
