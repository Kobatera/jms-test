package com.kobatera.testing;

import java.util.HashMap;

/**
 * @author ejc126 Singleton class to track the status of the current test
 *         execution
 */
public class TestStatusObject {
	private boolean testRunning = false;
	private boolean nativeJMS = true;
	private int numThreads = 20;
	private int numMessages = 500;
	private int recvRun = 0;
	private int recvDelay = 0;
	private int msgDelay = 100;
	private HashMap<Long, ThreadStatusObject> threadStatusMap = null;

	private static TestStatusObject tso;

	private TestStatusObject() {
	}

	public static TestStatusObject getInstance() {
		if (tso == null) {
			tso = new TestStatusObject();
		}
		return tso;
	}

	public void addThreadStatus(long threadId, ThreadStatusObject threadStatus) {
		// Store a reference to the thread status object keyed on thread id.
		threadStatusMap.put(new Long(threadId), threadStatus);
	}

	public ThreadStatusObject getThreadStatus(long threadId) {
		return threadStatusMap.get(new Long(threadId));
	}

	public HashMap<Long, ThreadStatusObject> getThreadStatusMap() {
		return threadStatusMap;
	}

	public int getNumThreads() {
		return numThreads;
	}

	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;

		// If this is a new test, clear out the ThreadStatus Map
		threadStatusMap = new HashMap<Long, ThreadStatusObject>(numThreads);
	}

	public int getNumMessages() {
		return numMessages;
	}

	public void setNumMessages(int numMessages) {
		this.numMessages = numMessages;
	}

	public int getRecvRun() {
		return recvRun;
	}

	public void setRecvRun(int recvRun) {
		this.recvRun = recvRun;
	}

	public int getMsgDelay() {
		return msgDelay;
	}

	public void setMsgDelay(int msgDelay) {
		this.msgDelay = msgDelay;
	}

	public boolean isTestRunning() {
		return testRunning;
	}

	public void setTestRunning(boolean testRunning) {
		this.testRunning = testRunning;

	}

	public boolean isNativeJMS() {
		return nativeJMS;
	}

	public void setNativeJMS(boolean nativeJMS) {
		this.nativeJMS = nativeJMS;
	}

	public int getRecvDelay() {
		return recvDelay;
	}

	public void setRecvDelay(int recvDelay) {
		this.recvDelay = recvDelay;
	}

}
