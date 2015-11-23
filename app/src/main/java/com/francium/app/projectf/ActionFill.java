
package com.francium.app.projectf;

import android.os.Message;

public class ActionFill extends ActionBasic {
    int mDeltaY;
    int mStep = 25;

    public ActionFill() {
        mStop = false;
        mDeltaY = 100;
    }

    public void run() {

        if (!mStop) {
            mDeltaY -= mStep;
            if (mDeltaY <= 0) {
                mStop = true;
            }
            if (mStop) {
                sendMsg();
            }
        }
    }

    public float getY() {
        return mDeltaY / 100.0f;
    }

    public void start() {
        mDeltaY = 100;
        super.start();
    }

    public void sendMsg() {
        Message msg = new Message();
        msg.what = GameEngine.FILL_END;
        GameEngine.mHandler.sendMessage(msg);
    }
}

