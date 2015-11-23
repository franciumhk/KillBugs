package com.francium.app.projectf;

public class ActionAutoTip extends ActionBasic{

	int mPicId = 1;
	int mTimeCnt = 0;

	public void run()
	{
		if(mStop) return;
		mTimeCnt++;
		if (1 != (mTimeCnt % 4))
			return;
		mPicId++;
		if (mPicId > 4) mPicId = 1;
	}
	
	public int getPicId()
	{
		return mPicId;
	}			
}

