package com.francium.app.projectf;

public class ActionSingleScore extends ActionBasic {

    int mDeltaY = 0;
    int mDelta = 1;
    int mCount = 0;

    public void run() {
        if (!mStop) {
            mCount++;

            if (mCount > 30) mStop = true;
            if (mCount < 20) mDeltaY += mDelta;
        }
    }

    public int getY() {
        return mDeltaY;
    }

    public void start() {
        mDeltaY = 0;
        mCount = 0;
        super.start();
    }

}

