
package com.francium.app.projectf;


public class ActionSpecialItem extends ActionBasic {

    int mPicId = 1;
    int mTimeCnt = 0;

    public void run() {
        mTimeCnt++;
        if (1 != (mTimeCnt % 3)) return;
        mPicId++;
        if (mPicId > 7) mPicId = 1;
    }

    public int getPicId() {
        return mPicId;
    }
}


