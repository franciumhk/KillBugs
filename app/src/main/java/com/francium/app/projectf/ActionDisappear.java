
package com.francium.app.projectf;

import android.os.Bundle;
import android.os.Message;

public class ActionDisappear extends ActionBasic{

	int mCount = 0;
	int mTimeCnt = 0;

	public void run()
	{
		if (!mStop)
		{
			mTimeCnt++;
			if (1 == (mTimeCnt % 5)) return;		//??
			mCount--;
			if(0 == mCount)
			{
				mStop = true;
				sendMsg();
			}
		}
	}

	public void start()
	{
		if(0 == mCount) mCount = 10;
		super.start();
	}

	public int getCount()
	{
		return mCount;
	}

	public void sendMsg()
	{
		Bundle b = new Bundle();
		b.putInt("token", mToken);
		setToken(-1);
		Message msg = new Message();
	    msg.what = GameEngine.DISAPPEAR_END;
	    msg.setData(b);
	    GameEngine.mHandler.sendMessage(msg);
	}
}

