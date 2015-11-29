package com.francium.app.projectf;

import android.os.Message;
import android.util.Log;

public class ScoreHandler {
    int mOwnScore = 0;
    int mPeerScore = 0;
    int mAwardScore = 0;

    int mFinalPeerScore = 0;
    int mFinalOwnScore = 0;

    int mOwnHealthPoint = Configuration.MAX_HEALTH_POINT;
    int mFinalOwnHealthPoint = 0;
    int mPeerHealthPoint = 0;
    int mFinalPeerHealthPoint = 0;

    int mAttackPoint = 0;

    float mAwardRatio = 0;
    int mContinueCnt = 0;
    int mComboCnt = 0;

    int mOver3 = 0;
    public static boolean isScoreUpdated = false;

    public ScoreHandler() {
        init();
    }

    public void init() {
        mAwardScore = 0;
        mAttackPoint = 0;

        mPeerScore = 0;
        mFinalPeerScore = -1;
        mFinalPeerHealthPoint = Configuration.MAX_HEALTH_POINT;
        mPeerHealthPoint = Configuration.MAX_HEALTH_POINT;

        mOwnScore = 0;
        mFinalOwnScore = 0;
        mFinalOwnHealthPoint = Configuration.MAX_HEALTH_POINT;
        mOwnHealthPoint = Configuration.MAX_HEALTH_POINT;

        mAwardRatio = 0;
        mContinueCnt = 0;
        mComboCnt = 0;

        mOver3 = 0;
    }

    public void award(int clearNum) {
        int award = 0;
//        Log.d("DEBUG", "clearNum: " + clearNum);
        switch (clearNum) {
            case 3:
                award = 1;
                break;
            case 4:
                award = 5;
                break;
            case 5:
                award = 10;
                break;
            case 6:
                award = 20;
                break;
            case 7:
                award = 40;
                break;
            case 8:
                award = 80;
                break;
            case 9:
                award = 160;
                break;
            case 10:
                award = 320;
                break;
            case 11:
                award = 640;
                break;
            default:
                if (clearNum > 11) {
                    award = 640 * clearNum;
                }
                break;
        }
        awardScore(Math.pow((double) mComboCnt, 2) * mAwardRatio * award);
    }

    public void awardScore(double score) {
        mAwardScore = (int) score;
        mOwnScore += (int) score;
        isScoreUpdated = true;
    }

    public void setPeerScore(int score) {
        mPeerScore = score;
    }

    public void setOwnScore(int score) {
        mOwnScore = score;
    }

    public int getPeerScore() {
        return mPeerScore;
    }

    public int getOwnScore() {
        return mOwnScore;
    }

    public void setFinalPeerScore(int score) {
        mFinalPeerScore = score;
    }

    public void setFinalOwnScore(int score) {
        mFinalOwnScore = score;
    }

    public int getFinalPeerScore() {
        return mFinalPeerScore;
    }

    public int getFinalOwnScore() {
        return mFinalOwnScore;
    }

    public void increaseOwnHealth(int healthPoint) {
        mOwnHealthPoint += healthPoint;
        if (mOwnHealthPoint > Configuration.MAX_HEALTH_POINT)
            mOwnHealthPoint = Configuration.MAX_HEALTH_POINT;
        Log.d("DEBUG", "increaseOwnHealth: " + mOwnHealthPoint);
    }

    public void decreaseOwnHealth(int healthPoint) {
        mOwnHealthPoint -= healthPoint;
        if (mOwnHealthPoint <= 0) {
            mOwnHealthPoint = 0;
            Message msg = new Message();
            msg.what = GameEngine.GAME_OVER;
            GameEngine.mHandler.sendMessage(msg);
        }
        Log.d("DEBUG", "decreaseOwnHealth: " + mOwnHealthPoint);
    }

    public void setPeerHealthPoint(int healthPoint) {
        mPeerHealthPoint = healthPoint;
    }

    public int getPeerHealthPoint() {
        return mPeerHealthPoint;
    }

    public void setFinalPeerHealthPoint(int healthPoint) {
        mFinalPeerHealthPoint = healthPoint;
    }

    public int getFinalPeerHealthPoint() {
        return mFinalPeerHealthPoint;
    }

    public int getOwnHealthPoint() {
        return mOwnHealthPoint;
    }

    public void setFinalOwnHealthPoint(int healthPoint) {
        mFinalOwnHealthPoint = healthPoint;
    }

    public int getFinalOwnHealthPoint() {
        return mFinalOwnHealthPoint;
    }

    public void increaseAttackPoint(int attackPoint) {
        mAttackPoint += attackPoint;
    }

    public int getAttackPoint() {
        int temp = mAttackPoint;
        Log.d("DEBUG", "Attack: " + temp);
        mAttackPoint = 0;
        return temp;
    }
    public int getAward() {
        return mAwardScore;
    }

    public int getContinueCnt() {
        return mContinueCnt;
    }

    public void resetCombo() {
        mComboCnt = 0;
    }

    public void increaseCombo() {
        mComboCnt++;
    }

    public void reset() {
        mAwardRatio = 0;
        mContinueCnt = 0;
    }

    public void increase() {
        mAwardRatio++;
        mContinueCnt++;
    }

    public void increase(int clearNum) {
        mAwardRatio += (float) clearNum / 5;
    }

    public void calcTotal(int clearNum) {
        if (clearNum > 3) {
            mOver3++;
            if (0 == (mOver3 % Configuration.AWARD_MAX_COUNT)) {
                Message msg = new Message();
                msg.what = GameEngine.GEN_SPECIAL_ITEM;
                GameEngine.mHandler.sendMessage(msg);
            }
        }
        if (mComboCnt > 2) {
            Message msg = new Message();
            msg.what = GameEngine.GEN_SPECIAL_ITEM;
            GameEngine.mHandler.sendMessage(msg);
            mComboCnt = 0;
        }
//        Log.d("DEBUG", "combo: " + mComboCnt);
    }

}
