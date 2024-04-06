# jms-test
Java code for testing JMS throughput

The code was created to conduct a throughput comparison of using Log4j as a JMS appender vs. using native Java API's.  The description of the test can be found in this [blog post](https://kobatera.com/f/log4j-jms-appender-performance-results).

Our testing approach was to create a web application that spawned a number of threads and wrote to a JMS queue using one of two methods, Log4j JMS appender or straight Java APIs.  We ran the application using a varying number of threads and varying delays between writes to the JMS queue.  This code was written and run on the following configuration:

- Sun Java 1.6.0_27 64 bit
- JBoss AS 7.0.2 with JVM parameters  -server -Xms2048m -Xmx2048m -XX:MaxPermSize=256m
- Red Hat Linux 5 64 bit - kernel version  2.6.18-308.7.1.el5
- 4 Intel Xeon X6550 2.0GHz 8 core processors - 32 cores total with 32 GB RAM
- The JMS queue was in the same JBoss instance where the web application was running.  It was a persistent queue using a file store with maximum size of 50 MB.  This queue was created using the HornetQ JMS implementation version 2.2.7 that is packaged with JBoss 7.0.2.
- Log4j version 1.2.16

The application consists of a servlet to configure the test parameters and start the test, a message generator, a message receiver, some status objects, and a test status servlet.  Each test execution requires that several parameters be configured from the application's main page.

The main classes are:

**RunTest** - Servlet that saves the test configuration and spawns the appropriate number of message generation threads and the subscriber thread.

**MessageGenerator** - extends the thread class.  This class instantiates the appropriate type of JMS appender being used for this test.  It then executes a loop for the configured number of iterations publishing a message to the JMS queue. There is a configured delay between publishing each message.  The message is a String, however it is put on the queue as an ObjectMessage.  This is because the Log4j appender can only put ObjectMessage types on the queue and we wanted to be consistent to reduce the number of variables.  The TextMessage type should serialize faster, but is not supported by the Log4j JMS appender.

**Log4jJMSAppender** - this is a very simple class because all of the JMS connection information is in the Log4j configuration file.  This is also where the performance issue with the Log4j appender is seen.  Each thread refers to the single instance of the Log4j JMS appender, and access to write to that appender is synchronized.  The message that is put on the queue is an ObjectMessage and is of type org.apache.log4j.spi.LoggingEvent.

**NativeJMSAppender** - this class looks more complicated than the Log4jJMSAppender, but it really just contains the same items that are in the Log4j configuration file.  The big difference in performance comes from the fact that each thread is creating its own session with the JMS service so the messages are published in parallel rather than synchronized through a single object.

**MessageReceiver** - this extends the thread class.  It subscribes to the test topic and pulls messages off of the topic.  It sleeps between each message for a configurable delay to simulate message processing time.  It runs for the configured duration of time.

**TestStatus** - this is a servlet that displays the status of the currently running test.  When the test is complete, it displays a button to run another test. 
