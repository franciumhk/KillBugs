
package com.francium.app.projectf;

import javax.microedition.khronos.opengles.GL10;

public class DrawDisappear {

    DrawBugItem drawBugItem;

    public EventAction action;

    public DrawDisappear(DrawBugItem drawBugItem) {
        this.drawBugItem = drawBugItem;
        action = new ActionDisappear();
    }

    public void draw(GL10 gl, int witch, int col, int row) {
        if (!action.isRun()) return;
        if (0 == ((ActionDisappear) action).getCount() % 2) {
            gl.glPushMatrix();
            drawBugItem.draw(gl, witch, col, row);
            gl.glPopMatrix();
        }
    }

}
