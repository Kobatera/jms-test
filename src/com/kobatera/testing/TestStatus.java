package com.kobatera.testing;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is a servlet that displays the status of the currently running test.  
 * When the test is complete, it displays a button to run another test.
 *
 */
 public class TestStatus extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public TestStatus() {
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
		TestStatusObject tso = TestStatusObject.getInstance();

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		out.println("<HTML>");
		out.println("<HEAD><TITLE>JMS Test Status</TITLE></HEAD>");
		out.println("<BODY>");
		out.println("<h1>Test Status</h1> <br>");
		out.println("Number of publish threads: " + tso.getNumThreads() + "<br>");
		out.println("Number of publish messages per thread: " + tso.getNumMessages() + "<br>");
		out.println("Delay between message publish: " + tso.getMsgDelay() + "<br>");
		out.println("Subscriber run time: " + tso.getRecvRun() + "<br>");
		out.println("Message receive delay time: " + tso.getRecvDelay() + "<br>");
		if (tso.isNativeJMS()){
			out.println("JMS Appender Implementation: Native <br>");
		} else {
			out.println("JMS Appender Implementation: Log4j <br>");
		}

		out.println("<br><br>");
		out.println("<h2>Thread Status</h2> <br>");

		HashMap<Long, ThreadStatusObject> threadStatusMap = tso.getThreadStatusMap();
		Set<Long> threadStatusKeys = threadStatusMap.keySet();
		Iterator<Long> tsi = threadStatusKeys.iterator();
		
		int stopped = 0;
		long runTime = 0;
		long longestRun = 0;
		
		while (tsi.hasNext()){
			Long threadId = tsi.next();
			ThreadStatusObject threadStatus = threadStatusMap.get(threadId);
			
			out.println("&nbsp;Thread ID: " + threadId.toString()+"<br>");
			out.println("&nbsp;&nbsp;Thread Start Time: "+ threadStatus.getStartTime()+"<br>");
			out.println("&nbsp;&nbsp;Messages Processed: "+ threadStatus.getMessagesProcessed()+"<br>");
			if (threadStatus.isThreadRunning()){
				runTime = System.currentTimeMillis()-threadStatus.getStartTime();
				out.println("&nbsp;&nbsp;Time Running: "+ runTime +"<br>");
			} else {
				runTime = threadStatus.getEndTime()-threadStatus.getStartTime();
				stopped = stopped + 1;
				out.println("&nbsp;&nbsp;Execution Time: "+ runTime +"<br>");
			}
			out.println("<br>");
			if (runTime > longestRun){
				longestRun = runTime;
			}
		}
		
		// if all of the threads are stopped, set the test status object indicator to stopped
		if (stopped >= tso.getNumThreads()){
			tso.setTestRunning(false);
		}
		
		out.println("<br><br>");
		out.println("<strong>Longest Run Time: "+ longestRun +"</strong><br>");
		out.println("<br><br>");
		out.println("<form id='jmsstatus' method='post' action='TestStatus' >");
		
		if (tso.isTestRunning()){
			out.println("<input type='submit' name='Refresh' value='Refresh' />  </p>	</form>");
		} else {
			out.println("<a href='/jms-test/'>New Test</a></p>");
		}
		
		out.println("</BODY></HTML>");

	}   	  	    
}