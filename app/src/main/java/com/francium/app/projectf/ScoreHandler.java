package com.francium.app.projectf;

import android.os.Message;

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
    float mBonusCnt = 0;
    int mComboCnt = 0;
    int mSuccessCnt = 0;

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
        mBonusCnt = 0;
        mComboCnt = 0;
        mOver3 = 0;
        mSuccessCnt = 0;
    }

    public void award(int clearNum) {
        int award = 0;
//        Log.d("ScoreDEBUG", "clearNum: " + clearNum);
        switch (clearNum) {
            case 3:
                award = 1;
                break;
            case 4:
                award = 2;
                break;
            case 5:
                award = 3;
                break;
            case 6:
                award = 4;
                break;
            default:
                if (clearNum > 6) {
                    award = 4;
                }
                else
                    award = 0;
                break;
        }
//        Log.d("ScoreDEBUG", "mComboCnt:" + mComboCnt + " mAwardRatio: " + mAwardRatio + " mSuccessCnt: " + mSuccessCnt + " award: " + award);
        awardScore(mComboCnt * ((mAwardRatio + 3) / 3) * ((mSuccessCnt + 3) / 3) * award);
        if (clearNum >= 3) {
            mSuccessCnt++;
            if (mSuccessCnt > Configuration.MAX_SUCCESSFUL_COUNT)
                mSuccessCnt = Configuration.MAX_SUCCESSFUL_COUNT;
        }
    }

    public void awardScore(double score) {
        if (score > Configuration.MAX_SINGLE_SCORE)
            score = Configuration.MAX_SINGLE_SCORE;
        mAwardScore = (int) score;
        mOwnScore += (int) score;
        isScoreUpdated = true;
//        Log.d("ScoreDEBUG", "Total Score:" + mOwnScore + " Unit Score: " + score);
    }

    public void increaseAwardRatio() {
        mAwardRatio++;
        if(mAwardRatio > Configuration.MAX_AWARD_RATIO)
            mAwardRatio = Configuration.MAX_AWARD_RATIO;
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
    }

    public void decreaseOwnHealth(int healthPoint) {
        mOwnHealthPoint -= healthPoint;
        if (mOwnHealthPoint <= 0) {
            mOwnHealthPoint = 0;
            mFinalOwnScore = mOwnScore;
            Message msg = new Message();
            msg.what = GameEngine.GAME_OVER;
            GameEngine.mHandler.sendMessage(msg);
        }
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
        mAttackPoint = 0;
        return temp;
    }

    public int getAward() {
        return mAwardScore;
    }

    public void resetCombo() {
        mComboCnt = 0;
    }

    public void increaseCombo() {
        mComboCnt++;
    }

    public void reset() {
        mAwardRatio = 0;
        mComboCnt = 0;
        mBonusCnt = 0;
        mSuccessCnt = 0;
//        Log.d("ScoreDEBUG", "reset score");
    }

    public void increaseBonusCnt(){
        mBonusCnt++;
    }

    public void checkBonus(int clearNum) {
        boolean giveSpecialItem = false;
        //Clear enough item
        if (clearNum > 3) {
            mOver3++;
            if ((mOver3 % Configuration.AWARD_MAX_COUNT) == 0) {
                giveSpecialItem = true;
            }
        }
        increaseCombo();
        // 3 or more combo
        if (mComboCnt % 3 == 0) {
            giveSpecialItem = true;
        }
        // Bonus
        if(mBonusCnt > Configuration.BONUS_MAX_COUNT){
            mBonusCnt -= Configuration.BONUS_MAX_COUNT;
            giveSpecialItem = true;
        }

        if(giveSpecialItem == true) {
            generateBonus();
        }
    }

    public void generateBonus() {
        Message msg = new Message();
        msg.what = GameEngine.GEN_SPECIAL_ITEM;
        GameEngine.mHandler.sendMessage(msg);
//        Log.d("ScoreDEBUG", "generateBonus = "+ "clearNum:" + clearNum + " mOver3: " + mOver3 + " mBonusCnt: " + mBonusCnt + " mComboCnt: " + mComboCnt);
    }

}
