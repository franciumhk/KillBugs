package com.francium.app.projectf;

public class TokenHandler {

    boolean[] mTokenInused;

    public TokenHandler() {
        mTokenInused = new boolean[Configuration.MAX_TOKEN];
        for (int i = 0; i < Configuration.MAX_TOKEN; i++) {
            mTokenInused[i] = false;
        }
    }

    synchronized int takeToken() {
        for (int i = 0; i < Configuration.MAX_TOKEN; i++) {
            if (!mTokenInused[i]) {
                /*Log.d("DEBUG",
                        "take token:"
                                + "[" + mTokenInused[0] + "]"
                                + "[" + mTokenInused[1] + "]"
                                + "[" + mTokenInused[2] + "]"
                                + "[" + mTokenInused[3] + "]"
                                + "[" + mTokenInused[4] + "]"
                                +  "[" + mTokenInused[5] + "]" );*/
                mTokenInused[i] = true;
                return i;
            }
        }
        /*Log.d("DEBUG",
                "take token:"
                        + "[" + mTokenInused[0] + "]"
                        + "[" + mTokenInused[1] + "]"
                        + "[" + mTokenInused[2] + "]"
                        + "[" + mTokenInused[3] + "]"
                        + "[" + mTokenInused[4] + "]"
                        +  "[" + mTokenInused[5] + "]" );*/
        return -1;
    }

    synchronized void freeToken(int token) {
        if (token >= 0 && token < Configuration.MAX_TOKEN) mTokenInused[token] = false;
        /*Log.d("DEBUG",
                "free token:"
                        + "[" + mTokenInused[0] + "]"
                        + "[" + mTokenInused[1] + "]"
                        + "[" + mTokenInused[2] + "]"
                        + "[" + mTokenInused[3] + "]"
                        + "[" + mTokenInused[4] + "]"
                        +  "[" + mTokenInused[5] + "]" );*/
    }
}
