package com.clubmycab;

public class ChatBubble {
	public boolean left;
	public String message;
	public long timeStamp;
	public String sendername;

	public ChatBubble(boolean left, String message, long timeStamp, String sname) {
		super();
		this.left = left;
		this.message = message;
		this.timeStamp = timeStamp;
		this.sendername = sname;
	}
}