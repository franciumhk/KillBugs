package com.francium.app.projectf;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

import com.francium.app.projectf.Configuration.E_SCENARIO;

public class TouchHandler {
    Context mContext;

    float mPreviousX = 0.0f;
    float mPreviousY = 0.0f;

    int mWidth = 0;
    int mHeight = 0;
    int mGridX = 0;
    int mGridY = 0;
    int mStep = 0;
    int mYStart = 0;

    enum TOUCH_DIRECTION {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        INVALID
    }

    TOUCH_DIRECTION mMoveDirection = TOUCH_DIRECTION.INVALID;

    public TouchHandler(Context context, int width, int height) {
        mContext = context;
        mWidth = width;
        mHeight = height;
        mStep = (int) (width / Configuration.GRID_NUM);
        mYStart = (mHeight - mWidth) / 2;
    }

    public boolean touchMenuView(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                raiseTouchMenuViewEvent();
                break;
        }
        return true;
    }

    public boolean touchResultView(MotionEvent e) {
//        switch (e.getAction()) {
//            case MotionEvent.ACTION_UP:
//                raiseTouchResultViewEvent();
//                break;
//        }
        return true;
    }

    public boolean touchGameView(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mGridX = -1;
                mGridY = -1;
                mMoveDirection = TOUCH_DIRECTION.INVALID;
                if (x >= 0 && x < mStep) mGridX = 0;
                else if (x >= mStep && x < 2 * mStep) mGridX = 1;
                else if (x >= 2 * mStep && x < 3 * mStep) mGridX = 2;
                else if (x >= 3 * mStep && x < 4 * mStep) mGridX = 3;
                else if (x >= 4 * mStep && x < 5 * mStep) mGridX = 4;
                else if (x >= 5 * mStep && x < 6 * mStep) mGridX = 5;
                else if (x >= 6 * mStep && x < 7 * mStep) mGridX = 6;

                if (y >= mYStart && y < mYStart + mStep) mGridY = 6;
                else if (y >= mYStart + mStep && y < mYStart + 2 * mStep) mGridY = 5;
                else if (y >= mYStart + 2 * mStep && y < mYStart + 3 * mStep) mGridY = 4;
                else if (y >= mYStart + 3 * mStep && y < mYStart + 4 * mStep) mGridY = 3;
                else if (y >= mYStart + 4 * mStep && y < mYStart + 5 * mStep) mGridY = 2;
                else if (y >= mYStart + 5 * mStep && y < mYStart + 6 * mStep) mGridY = 1;
                else if (y >= mYStart + 6 * mStep && y < mYStart + 7 * mStep) mGridY = 0;
                break;
            case MotionEvent.ACTION_UP:
                raiseTouchGameViewEvent();
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;
                float dx = x - mPreviousX;
                if (Math.abs(dy) > Math.abs(dx)) {
                    if (dy > Configuration.MOVE_THRESDHOLDER) mMoveDirection = TOUCH_DIRECTION.DOWN;
                    else if (dy < -Configuration.MOVE_THRESDHOLDER)
                        mMoveDirection = TOUCH_DIRECTION.UP;
                } else {
                    if (dx > Configuration.MOVE_THRESDHOLDER)
                        mMoveDirection = TOUCH_DIRECTION.RIGHT;
                    else if (dx < -Configuration.MOVE_THRESDHOLDER)
                        mMoveDirection = TOUCH_DIRECTION.LEFT;
                }
        }
        mPreviousY = y;
        mPreviousX = x;
        return true;
    }

    int getGridX() {
        return mGridX;
    }

    int getGridY() {
        return mGridY;
    }

    TOUCH_DIRECTION getDirection() {
        return mMoveDirection;
    }

    int getNeighborX() {
        int neighborX = mGridX;
        if (mMoveDirection == TOUCH_DIRECTION.LEFT) neighborX--;
        else if (mMoveDirection == TOUCH_DIRECTION.RIGHT) neighborX++;
        return neighborX;
    }

    int getNeighborY() {
        int neighborY = mGridY;
        if (mMoveDirection == TOUCH_DIRECTION.DOWN) neighborY--;
        else if (mMoveDirection == TOUCH_DIRECTION.UP) neighborY++;
        return neighborY;
    }

    boolean isValidTouchMove() {

        if (-1 == mGridX || -1 == mGridY) return false;
        if ((0 == mGridX && mMoveDirection == TOUCH_DIRECTION.LEFT)
                || (6 == mGridX && mMoveDirection == TOUCH_DIRECTION.RIGHT)
                || (0 == mGridY && mMoveDirection == TOUCH_DIRECTION.DOWN)
                || (6 == mGridY && mMoveDirection == TOUCH_DIRECTION.UP)
                || mMoveDirection == TOUCH_DIRECTION.INVALID) {
            return false;
        }
        return true;
    }

    void raiseTouchMenuViewEvent() {
        GameEngine.mScoreHandler.init();
        GameEngine.mTimeHandler.reset();
        GameEngine.init();
        Message msg = new Message();
        msg.what = GameEngine.GAME_START;
        GameEngine.mHandler.sendMessage(msg);
    }

    void raiseTouchResultViewEvent() {
        GameEngine.mScene = E_SCENARIO.GAME;
        GameEngine.mScoreHandler.init();
        GameEngine.mTimeHandler.reset();
        GameEngine.init();
        Message msg = new Message();
        msg.what = GameEngine.GAME_START;
        GameEngine.mHandler.sendMessage(msg);
    }

    void raiseTouchGameViewEvent() {
        Log.d("DEBUG", "Take Token~! raiseTouchGameViewEvent");
        int token = GameEngine.takeToken();
        Log.d("DEBUG", "raiseTouchGameViewEvent token: " + token);
        if (-1 == token) return;
        Message msg = new Message();
        Bundle b = new Bundle();
        int x = getGridX();
        int y = getGridY();
        if (!(x >= 0 && x < (int) Configuration.GRID_NUM)) return;
        if (!(y >= 0 && y < (int) Configuration.GRID_NUM)) return;
        b.putInt("token", token);
        b.putInt("col1", x);
        b.putInt("row1", y);
        if (isValidTouchMove()) {
            Log.d("DEBUG", "Exchange: " + "(" + x + ", " + y + "), (" + getNeighborX() + ", " + getNeighborY() + ")");
            b.putInt("col2", getNeighborX());
            b.putInt("row2", getNeighborY());
            msg.what = GameEngine.EXCHANGE_START;
        } else {
            msg.what = GameEngine.SCREEN_TOUCH;
        }
        msg.setData(b);
        GameEngine.mHandler.sendMessage(msg);
        System.out.println("touch grid(" + x + "," + y + ")");
    }

}
