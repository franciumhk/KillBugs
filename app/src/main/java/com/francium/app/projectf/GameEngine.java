package com.francium.app.projectf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.francium.app.projectf.Configuration.E_SCENARIO;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class GameEngine {

    Context mContext;
    public static E_SCENARIO mScene;

    static TokenHandler mToken;

    static int mBugItemPic[][];
    static int mPicBak[][];
    static int mEffect[][];
    static int mDisappearToken[][];

    static int mSingleScoreW = 0;
    static int mSingleScoreH = 0;

    int gameBackgroundTextureId;
    int resultBackgroundTextureId;
    int[] bugTextureId = new int[Configuration.NUMBER_OF_BUG];
    int[] tipsTextureId = new int[4];
    int tip2TextureId;
    int timeBarTextureId;

    static int previousExchangeCol1 = 0;
    static int previousExchangeRow1 = 0;
    static int previousExchangeCol2 = 0;
    static int previousExchangeRow2 = 0;
    static int previousToken = 0;
    static boolean reverseDone = false;
    static boolean isBusy = false;

    static int mAutoTipTimer = 0;

    static public DrawBackGround drawGameBackGround;
    static public DrawBackGround drawResultBackGround;
    static public DrawBugItem drawBugItem;
    static ArrayList<DrawExchange> mDrawExchangeList = new ArrayList<DrawExchange>();
    static ArrayList<DrawDisappear> mDrawDisappearList = new ArrayList<DrawDisappear>();
    static public DrawFill drawFill;
    static public DrawSingleScore drawSingleScore;
    static public DrawAutoTip drawAutoTip;
    static public DrawSpecialBugItem drawSpecialItem;
    static public DrawTime drawTime;
    static public DrawString drawString;

    public static ScoreHandler mScoreHandler;
    public static SoundHandler mSoundHandler;
    public static TimeHandler mTimeHandler;

    public static boolean mIsAutoTip = false;

    public static boolean mUpdatePeer = false;
    public static boolean mFinalScore = false;

    static private Random randomGenerator = new Random(System.currentTimeMillis());
    static private long mRandomSeed = 0;
    ArrayList<EventAction> mActionEventList = new ArrayList<EventAction>();

    static final int EFT_NONE = 0;
    static final int EFT_NORMAL = 1;
    static final int EFT_EXCHANGE = 2;
    static final int EFT_FILL = 3;
    static final int EFT_AUTOTIP = 4;
    static final int EFT_DISAPPEAR = 5;
    static final int SPECIAL_ITEM = 10;

    public GameEngine(Context context) {
        mScene = E_SCENARIO.GAME;
        mContext = context;
        mScoreHandler = new ScoreHandler();
        mSoundHandler = new SoundHandler(context);
        mTimeHandler = new TimeHandler();
        mBugItemPic = new int[(int) Configuration.GRID_NUM][(int) Configuration.GRID_NUM];
        mPicBak = new int[(int) Configuration.GRID_NUM][(int) Configuration.GRID_NUM];
        mEffect = new int[(int) Configuration.GRID_NUM][(int) Configuration.GRID_NUM];
        mDisappearToken = new int[(int) Configuration.GRID_NUM][(int) Configuration.GRID_NUM];
        mToken = new TokenHandler();
        init();
    }

    public static void randomizeItem(){
        for (int i = 0; i < (int) Configuration.GRID_NUM; i++) {
            for (int j = 0; j < (int) Configuration.GRID_NUM; j++) {
                mBugItemPic[i][j] = getRandom();
                while (isInLine(mBugItemPic, i, j)) {
                    mBugItemPic[i][j] = getRandom();
                }
                mEffect[i][j] = EFT_NORMAL;
                mDisappearToken[i][j] = -1;
            }
        }
    }

    public static void init() {
        mScoreHandler.init();
        mTimeHandler.reset();
        previousExchangeCol1 = 0;
        previousExchangeRow1 = 0;
        previousExchangeCol2 = 0;
        previousExchangeRow2 = 0;
        initRandom(mRandomSeed);
        randomizeItem();
    }

    public static void setRandomSeed(long seed) {
        mRandomSeed = seed;
    }

    public static boolean GetIsBusy() {
        return isBusy;
    }

    public static int takeToken() {
        return mToken.takeToken();
    }

    public static void freeToken(int token) {
        mToken.freeToken(token);
    }

    static void initRandom(long seed) {
        randomGenerator = new Random(seed);
    }

    static int getRandom() {
        int data = randomGenerator.nextInt((int)Configuration.GRID_NUM) + 1;
        return data;
    }

    public static boolean isInLineX(int pic[][], int col, int row) {
        int picId = pic[col][row];
        if (0 == col) {
            if (picId == pic[col + 1][row] && picId == pic[col + 2][row]) {
                return true;
            }
        } else if (1 == col) {
            if ((picId == pic[col - 1][row] && picId == pic[col + 1][row])
                    || (picId == pic[col + 1][row] && picId == pic[col + 2][row])) {
                return true;
            }
        } else if (col > 1 && col < (Configuration.GRID_NUM - 2)) {
            if ((picId == pic[col - 2][row] && picId == pic[col - 1][row])
                    || (picId == pic[col - 1][row] && picId == pic[col + 1][row])
                    || (picId == pic[col + 1][row] && picId == pic[col + 2][row])) {
                return true;
            }
        } else if ((Configuration.GRID_NUM - 2) == col) {
            if ((picId == pic[col - 2][row] && picId == pic[col - 1][row])
                    || (picId == pic[col - 1][row] && picId == pic[col + 1][row])) {
                return true;
            }
        } else if ((Configuration.GRID_NUM - 1) == col) {
            if (picId == pic[col - 1][row] && picId == pic[col - 2][row]) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInLineY(int pic[][], int col, int row) {
        int picId = pic[col][row];
        if (0 == row) {
            if (picId == pic[col][row + 1] && picId == pic[col][row + 2]) {
                return true;
            }
        } else if (1 == row) {
            if ((picId == pic[col][row - 1] && picId == pic[col][row + 1])
                    || (picId == pic[col][row + 1] && picId == pic[col][row + 2])) {
                return true;
            }
        } else if (row > 1 && row < (Configuration.GRID_NUM - 2)) {
            if ((picId == pic[col][row - 2] && picId == pic[col][row - 1])
                    || (picId == pic[col][row - 1] && picId == pic[col][row + 1])
                    || (picId == pic[col][row + 1] && picId == pic[col][row + 2])) {
                return true;
            }
        } else if ((Configuration.GRID_NUM - 2) == row) {
            if ((picId == pic[col][row - 2] && picId == pic[col][row - 1])
                    || (picId == pic[col][row - 1] && picId == pic[col][row + 1])) {
                return true;
            }
        } else if ((Configuration.GRID_NUM - 1) == row) {
            if (picId == pic[col][row - 1] && picId == pic[col][row - 2]) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInLine(int pic[][], int col, int row) {
        return isInLineX(pic, col, row) || isInLineY(pic, col, row);
    }

    static void debugLOG(String msg, int[][] matrix) {

        Log.d("DEBUG", msg + ":");
        Log.d("DEBUG",
                "[" + matrix[0][6] + "]"
                        + "[" + matrix[1][6] + "]"
                        + "[" + matrix[2][6] + "]"
                        + "[" + matrix[3][6] + "]"
                        + "[" + matrix[4][6] + "]"
                        + "[" + matrix[5][6] + "]"
                        + "[" + matrix[6][6] + "]"
        );
        Log.d("DEBUG",
                "[" + matrix[0][5] + "]"
                        + "[" + matrix[1][5] + "]"
                        + "[" + matrix[2][5] + "]"
                        + "[" + matrix[3][5] + "]"
                        + "[" + matrix[4][5] + "]"
                        + "[" + matrix[5][5] + "]"
                        + "[" + matrix[6][5] + "]"
        );
        Log.d("DEBUG",
                "[" + matrix[0][4] + "]"
                        + "[" + matrix[1][4] + "]"
                        + "[" + matrix[2][4] + "]"
                        + "[" + matrix[3][4] + "]"
                        + "[" + matrix[4][4] + "]"
                        + "[" + matrix[5][4] + "]"
                        + "[" + matrix[6][4] + "]"
        );
        Log.d("DEBUG",
                "[" + matrix[0][3] + "]"
                        + "[" + matrix[1][3] + "]"
                        + "[" + matrix[2][3] + "]"
                        + "[" + matrix[3][3] + "]"
                        + "[" + matrix[4][3] + "]"
                        + "[" + matrix[5][3] + "]"
                        + "[" + matrix[6][3] + "]"
        );
        Log.d("DEBUG",
                "[" + matrix[0][2] + "]"
                        + "[" + matrix[1][2] + "]"
                        + "[" + matrix[2][2] + "]"
                        + "[" + matrix[3][2] + "]"
                        + "[" + matrix[4][2] + "]"
                        + "[" + matrix[5][2] + "]"
                        + "[" + matrix[6][2] + "]"
        );
        Log.d("DEBUG",
                "[" + matrix[0][1] + "]"
                        + "[" + matrix[1][1] + "]"
                        + "[" + matrix[2][1] + "]"
                        + "[" + matrix[3][1] + "]"
                        + "[" + matrix[4][1] + "]"
                        + "[" + matrix[5][1] + "]"
                        + "[" + matrix[6][1] + "]"
        );
        Log.d("DEBUG",
                "[" + matrix[0][0] + "]"
                        + "[" + matrix[1][0] + "]"
                        + "[" + matrix[2][0] + "]"
                        + "[" + matrix[3][0] + "]"
                        + "[" + matrix[4][0] + "]"
                        + "[" + matrix[5][0] + "]"
                        + "[" + matrix[6][0] + "]"
        );
        Log.d("DEBUG", "=============================");

    }
    static void reverseExchange(){
        mSoundHandler.play(Configuration.E_SOUND.INVALID);
        Log.d("DEBUG", "reverseExchange");
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putInt("token", previousToken);
        b.putInt("col1", previousExchangeCol2);
        b.putInt("row1", previousExchangeRow2);
        Log.d("DEBUG", "Exchange: " + "(" + previousExchangeCol2 + ", " + previousExchangeRow2 + "), (" + previousExchangeCol1 + ", " + previousExchangeRow1 + ")");
        b.putInt("col2", previousExchangeCol1);
        b.putInt("row2", previousExchangeRow1);
        msg.what = GameEngine.EXCHANGE_START;
        msg.setData(b);
        GameEngine.mHandler.sendMessage(msg);
        reverseDone = true;
    }

    static void markDisappear(int token) {
        if (reverseDone == true) {
            reverseDone = false;
            return;
        }
        if (-1 == token) return;
        int markCount = 0;
        for (int i = 0; i < (int) Configuration.GRID_NUM; i++) {
            for (int j = 0; j < (int) Configuration.GRID_NUM; j++) {
                if (isInLine(mBugItemPic, i, j) && -1 == mDisappearToken[i][j]) {
                    setSingleScorePosition(i, j);
                    mEffect[i][j] = EFT_DISAPPEAR;
                    mDisappearToken[i][j] = token;
                    markCount++;
                    if (mBugItemPic[i][j] == Configuration.BUG_ID_ATTACK_1){
                        mScoreHandler.increaseAttackPoint(Configuration.ATTACK_POWER_1);
                    }
                    else if (mBugItemPic[i][j] == Configuration.BUG_ID_ATTACK_2){
                        mScoreHandler.increaseAttackPoint(Configuration.ATTACK_POWER_2);
                    }
                    else if (mBugItemPic[i][j] == Configuration.BUG_ID_ATTACK_3){
                        mScoreHandler.increaseAttackPoint(Configuration.ATTACK_POWER_3);
                    }
                    else if (mBugItemPic[i][j] == Configuration.BUG_ID_ATTACK_4){
                        mScoreHandler.increaseAttackPoint(Configuration.ATTACK_POWER_4);
                    }
                    else if (mBugItemPic[i][j] == Configuration.BUG_ID_HEAL) {
                        mScoreHandler.increaseOwnHealth(Configuration.HEAL_POWER);
                    }
                    else if (mBugItemPic[i][j] == Configuration.BUG_ID_AWARD) {
                        mScoreHandler.increaseAwardRatio();
                    }
                    else if (mBugItemPic[i][j] == Configuration.BUG_ID_BONUS) {
                        mScoreHandler.increaseBonusCnt();
                    }
                }
            }
        }
//        Log.d("DEBUG", "disappear markCount = " + markCount);
        if (markCount > 0) {
            if (3 == markCount)
                mSoundHandler.play(Configuration.E_SOUND.DISAPPEAR3);
            else if (4 == markCount)
                mSoundHandler.play(Configuration.E_SOUND.DISAPPEAR4);
            else if (markCount >= 5)
                mSoundHandler.play(Configuration.E_SOUND.DISAPPEAR5);
            DrawDisappear drawDisappear = getDrawDisappear(token);
            if (drawDisappear != null) {
                drawDisappear.action.setToken(token);
                drawDisappear.action.start();
                drawSingleScore.action.start();
            }
            mScoreHandler.checkBonus(markCount);
        } else {
//            Log.d("DEBUG", "free token!~ markDisappear");
            mScoreHandler.reset();
            reverseExchange();
            freeToken(token);
        }
    }

    static int clearPic(int token) {
        int clearCount = 0;
        if (-1 == token) return 0;
        for (int i = 0; i < (int) Configuration.GRID_NUM; i++) {
            for (int j = 0; j < (int) Configuration.GRID_NUM; j++) {
                if (EFT_DISAPPEAR == mEffect[i][j] && (token == mDisappearToken[i][j])) {
                    mBugItemPic[i][j] = 0;
                    mEffect[i][j] = EFT_NORMAL;
                    mDisappearToken[i][j] = -1;
                    clearCount++;
                }
            }
        }
        return clearCount;
    }

    static boolean isNeedClear() {
        for (int i = 0; i < (int) Configuration.GRID_NUM; i++) {
            for (int j = 0; j < (int) Configuration.GRID_NUM; j++) {
                if (isInLine(mBugItemPic, i, j) && (-1 == mDisappearToken[i][j])) {
                    return true;
                }
            }
        }
        return false;
    }

    static void fillGrid(int col, int row) {
        if (0 == mBugItemPic[col][row]) {
            for (int i = row; i < (int) Configuration.GRID_NUM; i++) {
                mEffect[col][i] = EFT_FILL;
            }
        }
    }

    static void markFill() {
        for (int i = 0; i < (int) Configuration.GRID_NUM; i++) {
            for (int j = 0; j < (int) Configuration.GRID_NUM; j++) {
                fillGrid(i, j);
            }
        }
        fillMethod();
        drawFill.action.start();
        //mSoundHandler.play(E_SOUND.FILL);
    }

    static void unMark(int mark) {
        for (int i = 0; i < (int) Configuration.GRID_NUM; i++) {
            for (int j = 0; j < (int) Configuration.GRID_NUM; j++) {
                if (mark == mEffect[i][j]) mEffect[i][j] = EFT_NORMAL;
            }
        }
    }

    static void unMarkDisappear(int token) {
        for (int i = 0; i < (int) Configuration.GRID_NUM; i++) {
            for (int j = 0; j < (int) Configuration.GRID_NUM; j++) {
                if ((token == mDisappearToken[i][j]) && (EFT_DISAPPEAR == mEffect[i][j])) {
                    mEffect[i][j] = EFT_NORMAL;
                }
                if (token == mDisappearToken[i][j]) {
                    mDisappearToken[i][j] = -1;
                }

            }
        }
    }

    static boolean isNeedFill() {
        for (int i = 0; i < (int) Configuration.GRID_NUM; i++) {
            for (int j = 0; j < (int) Configuration.GRID_NUM; j++) {
                if (0 == mBugItemPic[i][j]) {
                    return true;
                }
            }
        }
        return false;
    }

    static void fillMethod() {
        for (int i = 0; i < (int) Configuration.GRID_NUM; i++) {
            for (int j = 0; j < (int) Configuration.GRID_NUM; j++) {
                if (0 == mBugItemPic[i][j]) {
                    if (j < (int) Configuration.GRID_NUM - 1) {

                        mBugItemPic[i][j] = mBugItemPic[i][j + 1];
                        mBugItemPic[i][j + 1] = 0;
                    } else {

                        mBugItemPic[i][j] = getRandom();
                    }
                }
            }
        }
    }

    static void exchange(int pic[][], int col1, int row1, int col2, int row2) {

        if (col1 < 0 || col1 > (Configuration.GRID_NUM - 1)) return;
        if (col2 < 0 || col2 > (Configuration.GRID_NUM - 1)) return;
        if (row1 < 0 || row1 > (Configuration.GRID_NUM - 1)) return;
        if (row2 < 0 || row2 > (Configuration.GRID_NUM - 1)) return;
        int picId = pic[col1][row1];
        pic[col1][row1] = pic[col2][row2];
        pic[col2][row2] = picId;
//        //Make no possible move
//        for (int i = 0; i < 7; i++) {
//            for (int j = 0; j < 7; j++) {
//                pic[i][j] = ((i + j) % 7) + 1;
//            }
//        }
    }

    static void setSingleScorePosition(int col, int row) {
        if (drawSingleScore.action.isRun()) return;
        mSingleScoreW = col;
        mSingleScoreH = row;
    }

    void actionRegister(EventAction action) {
        if (action != null) mActionEventList.add(action);
    }

    static boolean autoTipMethod(int col, int row) {
        for (int i = 0; i < (int) Configuration.GRID_NUM; i++) {
            for (int j = 0; j < (int) Configuration.GRID_NUM; j++) {
                exchange(mPicBak, i, j, i - 1, j);
                if (isInLine(mPicBak, i, j)) return true;
                exchange(mPicBak, i - 1, j, i, j);

                exchange(mPicBak, i, j, i + 1, j);
                if (isInLine(mPicBak, i, j)) return true;
                exchange(mPicBak, i + 1, j, i, j);

                exchange(mPicBak, i, j, i, j - 1);
                if (isInLine(mPicBak, i, j)) return true;
                exchange(mPicBak, i, j - 1, i, j);

                exchange(mPicBak, i, j, i, j + 1);
                if (isInLine(mPicBak, i, j)) return true;
                exchange(mPicBak, i, j + 1, i, j);
            }
        }
        return false;
    }

    void autoTip() {
        for (int i = 0; i < (int) Configuration.GRID_NUM; i++) {
            for (int j = 0; j < (int) Configuration.GRID_NUM; j++) {
                mPicBak[i][j] = mBugItemPic[i][j];
            }
        }

        for (int i = 1; i < (int) Configuration.GRID_NUM - 1; i++) {
            for (int j = 1; j < (int) Configuration.GRID_NUM - 1; j++) {
                if (autoTipMethod(i, j)) {
                    markAutoTip();
                    return;
                }
            }
        }
    }

    static void checkPossibleMove(){
        for (int i = 0; i < (int) Configuration.GRID_NUM; i++) {
            for (int j = 0; j < (int) Configuration.GRID_NUM; j++) {
                mPicBak[i][j] = mBugItemPic[i][j];
            }
        }
        for (int i = 1; i < (int) Configuration.GRID_NUM - 1; i++) {
            for (int j = 1; j < (int) Configuration.GRID_NUM - 1; j++) {
                if (autoTipMethod(i, j)) {
                    return;
                }
            }
        }
        randomizeItem();
        Message msg = new Message();
        msg.what = GameEngine.FILL_END;
        GameEngine.mHandler.sendMessage(msg);
    }

    static void markAutoTip() {
        boolean isAutoTip = false;
        for (int i = 0; i < (int) Configuration.GRID_NUM; i++) {
            for (int j = 0; j < (int) Configuration.GRID_NUM; j++) {
                if (isInLine(mPicBak, i, j)) {
                    mEffect[i][j] = EFT_AUTOTIP;
                    isAutoTip = true;
                }
            }
        }
        if (isAutoTip) {
            drawAutoTip.action.start();
        }
    }

    static void clearAutoTip() {
        unMark(EFT_AUTOTIP);
        mIsAutoTip = false;
        mAutoTipTimer = 0;
    }

    static int getPicId(int col, int row) {
        int pic = mBugItemPic[col][row];
        if (iSpecialBugItem(col, row)) {
            ActionSpecialItem mAction;
            mAction = (ActionSpecialItem) drawSpecialItem.action;
            pic = mAction.getPicId();
        }
        return pic;
    }

    static boolean isNormalEFT(int col, int row) {
        if (EFT_NORMAL == mEffect[col][row]) return true;
        else return false;
    }

    public static boolean iSpecialBugItem(int col, int row) {
        if (SPECIAL_ITEM == mBugItemPic[col][row]) return true;
        else return false;
    }

    static void markSpecialBugItem(int token, int col, int row) {
        if (iSpecialBugItem(col, row)) {
            mSoundHandler.play(Configuration.E_SOUND.SPECIALITEM);
            markSpecialItem(token, col, row);
        } else {
            freeToken(token);
        }
    }

    static void markSpecialItem(int token, int col, int row) {
        if (-1 == token) return;
        if (iSpecialBugItem(col, row)) {
            ActionSpecialItem mAction = (ActionSpecialItem) drawSpecialItem.action;
            int picId = mAction.getPicId();
            mBugItemPic[col][row] = EFT_DISAPPEAR + token;
            for (int i = 0; i < (int) Configuration.GRID_NUM; i++) {
                for (int j = 0; j < (int) Configuration.GRID_NUM; j++) {
                    if (picId == mBugItemPic[i][j] && -1 == mDisappearToken[i][j]) {
                        mEffect[i][j] = EFT_DISAPPEAR;
                        mDisappearToken[i][j] = token;
                    }
                }
            }
            DrawDisappear drawDisappear = getDrawDisappear(token);
            if (drawDisappear != null) {
                drawDisappear.action.setToken(token);
                drawDisappear.action.start();
            }
        }
    }

    static void genSpecialBugItem() {
        int cnt = 0;
        int data = (int) (Math.random() * 1000);
        data = (data % ((int) Configuration.GRID_NUM * (int) Configuration.GRID_NUM));
        int x = 0;
        int y = 0;
        x = data / (int) Configuration.GRID_NUM;
        y = data % (int) Configuration.GRID_NUM;

        while (!isNormalEFT(x, y)) {
            data = (data++) % ((int) Configuration.GRID_NUM * (int) Configuration.GRID_NUM);
            x = data / (int) Configuration.GRID_NUM;
            y = data % (int) Configuration.GRID_NUM;
            cnt++;
            if (cnt > 10) break;
        }
        mBugItemPic[x][y] = SPECIAL_ITEM;
    }

    public void initTexture(GL10 gl) {
        gameBackgroundTextureId = initTexture(gl, R.drawable.background);
        resultBackgroundTextureId = initTexture(gl, R.drawable.result);
        tip2TextureId = initTexture(gl, R.drawable.word2);
        tipsTextureId[0] = initTexture(gl, R.drawable.tip01);
        tipsTextureId[1] = initTexture(gl, R.drawable.tip02);
        tipsTextureId[2] = initTexture(gl, R.drawable.tip03);
        tipsTextureId[3] = initTexture(gl, R.drawable.tip04);
        timeBarTextureId = initTexture(gl, R.drawable.time);

        bugTextureId[Configuration.BUG_ID_BLANK] = initTexture(gl, R.drawable.bug_blank);
        bugTextureId[Configuration.BUG_ID_GREEN] = initTexture(gl, R.drawable.b1);
        bugTextureId[Configuration.BUG_ID_YELLOW] = initTexture(gl, R.drawable.b2);
        bugTextureId[Configuration.BUG_ID_RED] = initTexture(gl, R.drawable.b3);
        bugTextureId[Configuration.BUG_ID_WHITE] = initTexture(gl, R.drawable.b4);
        bugTextureId[Configuration.BUG_ID_BLUE] = initTexture(gl, R.drawable.b5);
        bugTextureId[Configuration.BUG_ID_CYAN] = initTexture(gl, R.drawable.b6);
        bugTextureId[Configuration.BUG_ID_PURPLE] = initTexture(gl, R.drawable.b7);

    }

    public void initDraw(GL10 gl) {
        drawResultBackGround = new DrawBackGround(resultBackgroundTextureId);
        drawGameBackGround = new DrawBackGround(gameBackgroundTextureId);
        drawBugItem = new DrawBugItem(bugTextureId);
        drawFill = new DrawFill(drawBugItem);
        drawSingleScore = new DrawSingleScore(gl);
        drawTime = new DrawTime(gl, Configuration.MAX_TIME);
        drawAutoTip = new DrawAutoTip(tipsTextureId);
        drawSpecialItem = new DrawSpecialBugItem(bugTextureId);
        drawString = new DrawString(gl);

        actionRegister(drawFill.action);
        actionRegister(drawSingleScore.action);
        actionRegister(drawAutoTip.action);
        actionRegister(drawSpecialItem.action);

        initExchangeList();
        initDisappearList();
    }

    void initExchangeList() {
        DrawExchange drawExchange;
        for (int i = 0; i < Configuration.MAX_TOKEN; i++) {
            drawExchange = new DrawExchange(drawBugItem);
            actionRegister(drawExchange.action);
            mDrawExchangeList.add(drawExchange);
        }
    }

    void initDisappearList() {
        DrawDisappear drawDisappear;
        for (int i = 0; i < Configuration.MAX_TOKEN; i++) {
            drawDisappear = new DrawDisappear(drawBugItem);
            actionRegister(drawDisappear.action);
            mDrawDisappearList.add(drawDisappear);
        }
    }

    static DrawExchange getDrawExchange(int token) {
        if (-1 == token) return null;
        return mDrawExchangeList.get(token);
    }

    static DrawDisappear getDrawDisappear(int token) {
        if (-1 == token) return null;
        return mDrawDisappearList.get(token);
    }

    void drawExchangeRun(GL10 gl) {
        DrawExchange drawExchange;
        ActionExchange mAction;
        for (int i = 0; i < Configuration.MAX_TOKEN; i++) {
            drawExchange = mDrawExchangeList.get(i);
            mAction = (ActionExchange) drawExchange.action;
            if (mAction.isRun()) {
                drawExchange.draw(gl);
            }
        }
    }

    void drawDisappeareRun(GL10 gl, int col, int row) {
        int token = mDisappearToken[col][row];
        if (-1 == token) return;
        DrawDisappear drawDisappear = mDrawDisappearList.get(token);
        if (drawDisappear != null) {
            ActionDisappear mAction = (ActionDisappear) drawDisappear.action;
            if (mAction.isRun()) drawDisappear.draw(gl, getPicId(col, row), col, row);
        }
    }

    private int initTexture(GL10 gl, int drawableId) {
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        int currTextureId = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, currTextureId);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        InputStream is = mContext.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle();
        return currTextureId;
    }

    public static final int EXCHANGE_START = 1;
    public static final int EXCHANGE_END = 2;
    public static final int GAME_START = 3;
    public static final int DISAPPEAR_END = 4;
    public static final int FILL_END = 5;
    public static final int SCREEN_TOUCH = 6;
    public static final int GEN_SPECIAL_ITEM = 7;
    public static final int GAME_OVER = 10;

    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EXCHANGE_START: {
                    isBusy = true;
                    mSoundHandler.play(Configuration.E_SOUND.SLIDE);
                    clearAutoTip();
                    Bundle b = msg.getData();
                    int token = b.getInt("token");
                    int col1 = b.getInt("col1");
                    int col2 = b.getInt("col2");
                    int row1 = b.getInt("row1");
                    int row2 = b.getInt("row2");
                    previousToken = token;
                    previousExchangeCol1 = col1;
                    previousExchangeRow1 = row1;
                    previousExchangeCol2 = col2;
                    previousExchangeRow2 = row2;
                    mEffect[col1][row1] = EFT_EXCHANGE;
                    mEffect[col2][row2] = EFT_NONE;
                    setSingleScorePosition(col1, row1);
                    int pic1 = getPicId(col1, row1);
                    int pic2 = getPicId(col2, row2);
                    DrawExchange drawExchange = getDrawExchange(token);
                    if (drawExchange != null) {
                        drawExchange.init(token, pic1, col1, row1, pic2, col2, row2);
                    }
                    mScoreHandler.resetCombo();
                    break;
                }
                case EXCHANGE_END: {
                    Bundle b = msg.getData();
                    int token = b.getInt("token");
                    int col1 = b.getInt("col1");
                    int col2 = b.getInt("col2");
                    int row1 = b.getInt("row1");
                    int row2 = b.getInt("row2");
                    exchange(mBugItemPic, col1, row1, col2, row2);
                    mEffect[col1][row1] = EFT_NORMAL;
                    mEffect[col2][row2] = EFT_NORMAL;
                    markDisappear(token);
                    isBusy = false;
                    break;
                }
                case GAME_START:{
                    mScene = E_SCENARIO.GAME;
                    mTimeHandler.start();
                    checkPossibleMove();
                    break;
                }
                case DISAPPEAR_END: {
                    Bundle b = msg.getData();
                    int token = b.getInt("token");
                    int clearCnt = clearPic(token);
                    unMarkDisappear(token);
                    Log.d("DEBUG", "award: " + clearCnt);
                    mScoreHandler.award(clearCnt);
                    freeToken(token);
                    markFill();
                    break;
                }
                case FILL_END: {
                    unMark(EFT_FILL);
                    if (isNeedFill()) {
                        markFill();
                        clearAutoTip();
                        return;
                    } else {
                        if (isNeedClear()) {
                            int token = takeToken();
                            markDisappear(token);
                            clearAutoTip();
                            return;
                        }
                    }
                    clearAutoTip();
                    checkPossibleMove();
                    if (mScoreHandler.isScoreUpdated == true)
                        mUpdatePeer = true;
                    break;
                }
                case SCREEN_TOUCH:
                    Bundle b = msg.getData();
                    int token = b.getInt("token");
                    int col = b.getInt("col1");
                    int row = b.getInt("row1");
                    markSpecialBugItem(token, col, row);
                    break;
                case GEN_SPECIAL_ITEM:
                    genSpecialBugItem();
                    break;
                case GAME_OVER: {
                    mSoundHandler.play(Configuration.E_SOUND.END);
                    mScoreHandler.setFinalOwnScore(mScoreHandler.getOwnScore());
                    mScoreHandler.setFinalOwnHealthPoint(mScoreHandler.getOwnHealthPoint());
                    mFinalScore = true;
                    mScene = E_SCENARIO.RESULT;
                    break;
                }
            }
        }
    };

    public void run() {
        mAutoTipTimer++;
        if (mAutoTipTimer > Configuration.AUTOTIP_DELAY) {
            if (!mIsAutoTip) {
                mIsAutoTip = true;
                autoTip();
            }
        }
        EventAction action = null;
        for (int i = 0; i < mActionEventList.size(); i++) {
            action = mActionEventList.get(i);
            action.run();
        }
    }

    public void drawResultScene(GL10 gl) {
        String score = "----";
        String healthPoint = "----";
        String result = "Wait...";
        boolean isWin = false;
        int resultColor = Color.GRAY;
        if (MainActivity.mMultiplayer == true) {
            if (mScoreHandler.getFinalPeerScore() >= 0) {
                score = Integer.toString(mScoreHandler.getFinalPeerScore());
                healthPoint = Integer.toString(mScoreHandler.getFinalPeerHealthPoint());
                if (mScoreHandler.getFinalPeerHealthPoint() > 0) {
                    if (mScoreHandler.getFinalOwnHealthPoint() > 0) {
                        if (mScoreHandler.getFinalOwnScore() >= mScoreHandler.getFinalPeerScore()) {
                            isWin = true;
                        }
                    }
                } else {
                    isWin = true;
                }
                if (isWin) {
                    result = "WIN!";
                    resultColor = Color.RED;
                } else {
                    result = "LOSE";
                    resultColor = Color.GRAY;
                }
            }
            drawString.draw(gl,
                    result,
                    120,
                    40,
                    -50,
                    resultColor
            );
            drawString.draw(gl,
                    "Enemy:" ,
                    60,
                    40,
                    -30,
                    Configuration.COLOR_PEER_RESULT
            );
            drawString.draw(gl,
                    "Score: " + score,
                    60,
                    40,
                    -20,
                    Configuration.COLOR_PEER_RESULT
            );
            drawString.draw(gl,
                    "HP: " + healthPoint,
                    60,
                    40,
                    -10,
                    Configuration.COLOR_PEER_RESULT
            );
            drawString.draw(gl,
                    "You:",
                    60,
                    40,
                    10,
                    Configuration.COLOR_OWN_RESULT
            );
            drawString.draw(gl,
                    "Score: " +Integer.toString(mScoreHandler.getFinalOwnScore()),
                    60,
                    40,
                    20,
                    Configuration.COLOR_OWN_RESULT
            );
            drawString.draw(gl,
                    "HP: " +Integer.toString(mScoreHandler.getFinalOwnHealthPoint()),
                    60,
                    40,
                    30,
                    Configuration.COLOR_OWN_RESULT
            );
        }
        else {
            drawString.draw(gl,
                    "SCORE:",
                    60,
                    40,
                    -10,
                    Configuration.COLOR_OWN_RESULT
            );
            drawString.draw(gl,
                    Integer.toString(mScoreHandler.getFinalOwnScore()),
                    60,
                    40,
                    0,
                    Configuration.COLOR_OWN_RESULT
            );
        }
        drawResultBackGround.draw(gl);
    }

    public void drawGameScene(GL10 gl) {
        int topLine1Height = -64;
        int topLine2Height = -56;

        int bottomLine1Height = 64;

        int col1Width = 40;
        int col2Width = 10;
        int fontSize = 28;
        if (MainActivity.mMultiplayer) {
            drawString.draw(gl,
                    "P2 HP: " + Integer.toString(mScoreHandler.getPeerHealthPoint()),
                    fontSize,
                    col1Width,
                    topLine2Height,
                    Configuration.COLOR_PEER_SCORE
            );
            drawString.draw(gl,
                    "Score: " + Integer.toString(mScoreHandler.getPeerScore()),
                    fontSize,
                    col2Width,
                    topLine2Height,
                    Configuration.COLOR_PEER_SCORE
            );
            drawString.draw(gl,
                    "P1 HP: " + Integer.toString(mScoreHandler.getOwnHealthPoint()),
                    fontSize,
                    col1Width,
                    topLine1Height,
                    Configuration.COLOR_OWN_SCORE
            );
            drawString.draw(gl,
                    "Score: " + Integer.toString(mScoreHandler.getOwnScore()),
                    fontSize,
                    col2Width,
                    topLine1Height,
                    Configuration.COLOR_OWN_SCORE
            );
        }
        else {
            drawString.draw(gl,
                    "Score: " + Integer.toString(mScoreHandler.getOwnScore()),
                    fontSize,
                    col1Width,
                    topLine1Height,
                    Configuration.COLOR_OWN_SCORE
            );
        }
        drawString.draw(gl,
                "Time Left: " + Integer.toString(mTimeHandler.getTimeLeft()),
                fontSize,
                col1Width,
                bottomLine1Height,
                Color.BLACK
        );
        for (int i = 0; i < (int) Configuration.GRID_NUM; i++) {
            for (int j = 0; j < (int) Configuration.GRID_NUM; j++) {
                switch (mEffect[i][j]) {
                    case EFT_NORMAL:
                        if (iSpecialBugItem(i, j))
                            drawSpecialItem.draw(gl, i, j);
                        else
                            drawBugItem.draw(gl, getPicId(i, j), i, j);
                        break;
                    case EFT_EXCHANGE:
                        drawExchangeRun(gl);
                        break;
                    case EFT_FILL:
                        drawFill.draw(gl, getPicId(i, j), i, j);
                        drawSingleScore.draw(gl, mSingleScoreW, mSingleScoreH, mScoreHandler.getAward());
                        break;
                    case EFT_AUTOTIP:
                        drawAutoTip.draw(gl, i, j);
                        drawBugItem.draw(gl, getPicId(i, j), i, j);
                        break;
                    case EFT_DISAPPEAR:
                        drawDisappeareRun(gl, i, j);
                        break;
                    default:
                        break;
                }
            }
        }
        drawGameBackGround.draw(gl);
    }

}
