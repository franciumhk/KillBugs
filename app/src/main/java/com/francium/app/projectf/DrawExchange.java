
package com.francium.app.projectf;

import javax.microedition.khronos.opengles.GL10;

public class DrawExchange {

    int mToken = 0;
    DrawBugItem drawBugItem;
    int mWitch1 = 0;
    int mWitch2 = 0;

    int mCol1 = 0;
    int mCol2 = 0;
    int mRow1 = 0;
    int mRow2 = 0;

    public EventAction action;

    public DrawExchange(DrawBugItem drawBugItem) {
        this.drawBugItem = drawBugItem;
        mWitch1 = 0;
        mWitch2 = 0;
        mCol1 = 0;
        mCol2 = 0;
        mRow1 = 0;
        mRow2 = 0;
        action = new ActionExchange();
    }

    public void init(int token, int witch1, int col1, int row1, int witch2, int col2, int row2) {
        mToken = token;
        mWitch1 = witch1;
        mWitch2 = witch2;
        mCol1 = col1;
        mCol2 = col2;
        mRow1 = row1;
        mRow2 = row2;
        ((ActionExchange) action).init(token, col1, row1, col2, row2);
        action.start();
    }

    public void draw(GL10 gl) {
        ActionExchange mAction = (ActionExchange) action;
        if (!action.isRun()) return;
        gl.glPushMatrix();
        gl.glTranslatef(mAction.getX1() * Configuration.translateRatio, mAction.getY1() * Configuration.translateRatio, 0f);
        drawBugItem.draw(gl, mWitch1, mCol1, mRow1);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(mAction.getX2() * Configuration.translateRatio, mAction.getY2() * Configuration.translateRatio, 0f);
        drawBugItem.draw(gl, mWitch2, mCol2, mRow2);
        gl.glPopMatrix();
    }
}
