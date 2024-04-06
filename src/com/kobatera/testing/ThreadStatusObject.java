package com.kobatera.testing;

public class ThreadStatusObject {
	private boolean threadRunning;
	private long startTime;
	private long endTime;
	private int messagesProcessed;
	
	public boolean isThreadRunning() {
		return threadRunning;
	}
	public void setThreadRunning(boolean threadRunning) {
		this.threadRunning = threadRunning;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public int getMessagesProcessed() {
		return messagesProcessed;
	}
	public void setMessagesProcessed(int messagesProcessed) {
		this.messagesProcessed = messagesProcessed;
	}

}
