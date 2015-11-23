
package com.francium.app.projectf;

import android.os.Bundle;
import android.os.Message;

public class ActionExchange extends ActionBasic {
    int mCol1 = 0;
    int mCol2 = 0;
    int mRow1 = 0;
    int mRow2 = 0;

    int mDeltaX1 = 0;
    int mDeltaY1 = 0;
    int mDeltaX2 = 0;
    int mDeltaY2 = 0;

    int mStep = 10;
    boolean mDirectionX = true;
    boolean mDirectionY = true;
    boolean mNeedMoveX = false;
    boolean mNeedMoveY = false;

    public void init(int token, int col1, int row1, int col2, int row2) {
        super.setToken(token);
        mNeedMoveX = false;
        mNeedMoveY = false;
        mCol1 = col1;
        mCol2 = col2;
        mRow1 = row1;
        mRow2 = row2;

        if (col1 == col2) {
            mDeltaX1 = 0;
            mDeltaX2 = 0;
        } else {

            mNeedMoveX = true;
            if (col1 > col2) {
                mDirectionX = true;
                mDeltaX1 = 40;
            } else {
                mDirectionX = false;
                mDeltaX1 = -40;
            }
        }
        if (row1 == row2) {
            mDeltaY1 = 0;
            mDeltaY2 = 0;
        } else {

            mNeedMoveY = true;
            if (row1 > row2) {
                mDirectionY = true;
                mDeltaY1 = 40;
            } else {
                mDirectionY = false;
                mDeltaY1 = -40;
            }
        }
        mStop = false;
    }

    public void start() {
        mStop = false;
    }

    public void run() {
        if (mStop) {
            return;
        }
        if (mNeedMoveX) {
            if (mDeltaX1 >= 50) {
                mDirectionX = true;
                mStop = true;
            } else if (mDeltaX1 <= -50) {
                mDirectionX = false;
                mStop = true;
            }
            if (mDirectionX) {
                mDeltaX1 -= mStep;
                mDeltaX2 = -mDeltaX1;
            } else {
                mDeltaX1 += mStep;
                mDeltaX2 = -mDeltaX1;
            }
        }

        if (mNeedMoveY) {
            if (mDeltaY1 >= 50) {
                mDirectionY = true;
                mStop = true;
            } else if (mDeltaY1 <= -50) {
                mDirectionY = false;
                mStop = true;
            }
            if (mDirectionY) {
                mDeltaY1 -= mStep;
                mDeltaY2 = -mDeltaY1;
            } else {
                mDeltaY1 += mStep;
                mDeltaY2 = -mDeltaY1;
            }
        }

        if (mStop) {
            sendMsg();
        }
    }

    public float getX1() {
        float delta = 0;

        if (mCol1 > mCol2) {
            delta = -0.5f;
        } else if (mCol1 < mCol2) {
            delta = 0.5f;
        }
        return delta + mDeltaX1 / 100.0f;
    }

    public float getX2() {
        float delta = 0;

        if (mCol1 > mCol2) {
            delta = 0.5f;
        } else if (mCol1 < mCol2) {
            delta = -0.5f;
        }
        return delta + mDeltaX2 / 100.0f;
    }

    public float getY1() {
        float delta = 0;

        if (mRow1 > mRow2) {
            delta = -0.5f;
        } else if (mRow1 < mRow2) {
            delta = 0.5f;
        }
        return delta + mDeltaY1 / 100.0f;
    }

    public float getY2() {
        float delta = 0;

        if (mRow1 > mRow2) {
            delta = 0.5f;
        } else if (mRow1 < mRow2) {
            delta = -0.5f;
        }
        return delta + mDeltaY2 / 100.0f;
    }

    public void sendMsg() {
        Bundle b = new Bundle();
        b.putInt("token", mToken);
        setToken(-1);
        b.putInt("col1", mCol1);
        b.putInt("row1", mRow1);
        b.putInt("col2", mCol2);
        b.putInt("row2", mRow2);
        Message msg = new Message();
        msg.what = GameEngine.EXCHANGE_END;
        msg.setData(b);
        GameEngine.mHandler.sendMessage(msg);
    }

}
