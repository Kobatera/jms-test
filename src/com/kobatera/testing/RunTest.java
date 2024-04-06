package com.kobatera.testing;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


/**
 * Servlet that stores the test configuration and spawns the appropriate number of message generation
 * threads and the subscriber thread. 
 *
 */
 public class RunTest extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   
	private static Logger logger = Logger.getLogger(RunTest.class);

    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public RunTest() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("--> Entering");
		
		TestStatusObject tso = TestStatusObject.getInstance();
		
		int numThreads = 20;
		int numMessages = 500;
		int recvRun = 60000;
		int recvDelay = 10;
		int msgDelay = 100;
		String jmsImplementation;
		
		try {
			numThreads = Integer.parseInt(request.getParameter("numThreads"));
			numMessages = Integer.parseInt(request.getParameter("numMessages"));
			recvRun = Integer.parseInt(request.getParameter("recvRun"));
			recvDelay = Integer.parseInt(request.getParameter("recvDelay"));
			msgDelay = Integer.parseInt(request.getParameter("msgDelay"));
			jmsImplementation = request.getParameter("jmsType");
			
		}catch (Exception e){
			throw new ServletException("Invalid Parameters");
		}
		
		// Save the parameters in the status object
		tso.setMsgDelay(msgDelay);
		tso.setNumMessages(numMessages);
		tso.setNumThreads(numThreads);
		tso.setRecvRun(recvRun);
		tso.setRecvDelay(recvDelay);
		tso.setTestRunning(true);
		if (jmsImplementation.equals("Native")){
			tso.setNativeJMS(true);
		} else {
			tso.setNativeJMS(false);
		}

		// Startup the number of threads specified creating the number of messages
	    Thread[] msgGen = new Thread[numThreads] ;
		
		for (int i=0; i < numThreads; i++){
			msgGen[i] = new MessageGenerator(numMessages, msgDelay);
			msgGen[i].start();
		}


		// If they set a time to run the subscriber, start it up
		if (recvRun > 0){
			Thread msgRecv = new MessageReceiver(recvRun, recvDelay);
			msgRecv.start();
		}

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		out.println("<HTML>");
		out.println("<HEAD><TITLE>JMS Test Execution</TITLE></HEAD>");
		out.println("<BODY>");
		out.println("<h1>Test Started</h1> <br>");
		out.println("Number of publish threads: " + numThreads + "<br>");
		out.println("Number of publish messages per thread: " + numMessages + "<br>");
		out.println("Delay between message publish: " + msgDelay + "<br>");
		out.println("Subscriber run time: " + recvRun + "<br>");
		out.println("Message receive delay time: " + recvDelay + "<br>");
		out.println("JMS Appender implementation: " + jmsImplementation + "<br>");

		out.println("<br><br>");
		out.println("<form id='jmsstatus' method='post' action='TestStatus' >");
		out.println("<input type='submit' name='Submit' value='Check Status' />  </p>	</form>");
		out.println("</BODY></HTML>");
		
		logger.debug("--> Leaving");
	}   	  	    
}