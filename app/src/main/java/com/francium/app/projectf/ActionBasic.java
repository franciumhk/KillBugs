package com.francium.app.projectf;

public class ActionBasic implements EventAction{
	public int mToken = 0;
	public boolean mStop = true;

	public boolean isRun() {
		return !mStop;
	}

	public void run() {}

	public void start() {
		mStop = false;
	}

	public void end() {
		mStop = true;
	}
	
	public void setToken(int token)
	{
		mToken = token;
	}
	
	public int getToken()
	{
		return mToken;
	}

	public void sendMsg() {}
}
