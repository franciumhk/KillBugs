package com.francium.app.projectf;

import android.os.Message;

public class TimeHandler {

    long mMaxTime = 0;
    long mTimeLeft = 0;
    long mTimeUsed = 0;
    long mStartTime;
    boolean mStop = true;

    public TimeHandler() {
        mMaxTime = Configuration.MAX_TIME;
        mTimeLeft = Configuration.MAX_TIME;
        mStop = true;
    }

    public void reset() {
        mMaxTime = Configuration.MAX_TIME;
        mTimeLeft = Configuration.MAX_TIME;
        mTimeUsed = 0;
    }

    public void start() {
        if (mTimeLeft > 0) {
            mStartTime = System.currentTimeMillis() / 1000 - mTimeUsed;
            mStop = false;
        }
    }

    public void pause() {
        if (mTimeLeft > 0) {
            mTimeUsed = System.currentTimeMillis() / 1000 - mStartTime;
            mStop = true;
        }
    }

    public void resume() {
        if (mTimeLeft > 0) {
            mStartTime = System.currentTimeMillis() / 1000 - mTimeUsed;
            mStop = false;
        }
    }

    public int getTimeLeft() {
        if (!mStop) {
            mTimeUsed = System.currentTimeMillis() / 1000 - mStartTime;
            mTimeLeft = mMaxTime - mTimeUsed;
            if (mTimeLeft <= 0) {
                mStop = true;
                Message msg = new Message();
                msg.what = GameEngine.GAME_OVER;
                GameEngine.mHandler.sendMessage(msg);
            }

        }
        return (int) mTimeLeft;
    }

}
