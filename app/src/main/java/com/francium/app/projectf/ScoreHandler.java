package com.francium.app.projectf;

import android.os.Message;

public class ScoreHandler {
    int mOwnScore = 0;
    int mPeerScore = 0;
    int mAwardScore = 0;

    int mFinalPeerScore = 0;
    int mFinalOwnScore = 0;

    float mAwardRatio = 0;
    int mContinueCnt = 0;
    int mComboCnt = 0;

    int mOver3 = 0;
    public static boolean isScoreUpdated = false;

    public ScoreHandler() {
        init();
    }

    public void init() {
        mOwnScore = 0;
        mPeerScore = 0;
        mAwardScore = 0;

        mFinalPeerScore = -1;
        mFinalOwnScore = 0;

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
        awardScore(Math.pow((double)mComboCnt,2) * mAwardRatio * award);
    }

    public void awardScore(double score) {
        mAwardScore = (int) score;
        mOwnScore += (int) score;
        isScoreUpdated = true;
    }

    public void setPeerScore(int score){
        mPeerScore = score;
    }

    public void setOwnScore(int score){
        mOwnScore = score;
    }

    public int getPeerScore(){
        return mPeerScore;
    }

    public int getOwnScore() {
        return mOwnScore;
    }


    public void setFinalPeerScore(int score){
        mFinalPeerScore = score;
    }

    public void setFinalOwnScore(int score){
        mFinalOwnScore = score;
    }

    public int getFinalPeerScore(){
        return mFinalPeerScore;
    }

    public int getFinalOwnScore() {
        return mFinalOwnScore;
    }

    public int getAward() {
        return mAwardScore;
    }

    public int getContinueCnt() {
        return mContinueCnt;
    }

    public void resetCombo(){
        mComboCnt = 0;
    }

    public void increaseCombo(){
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
        if (mComboCnt > 2){
            Message msg = new Message();
            msg.what = GameEngine.GEN_SPECIAL_ITEM;
            GameEngine.mHandler.sendMessage(msg);
            mComboCnt = 0;
        }
//        Log.d("DEBUG", "combo: " + mComboCnt);
    }

}
