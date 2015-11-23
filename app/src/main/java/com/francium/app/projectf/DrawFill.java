
package com.francium.app.projectf;

import javax.microedition.khronos.opengles.GL10;

public class DrawFill {

    DrawBugItem drawBugItem;
    public EventAction action;
    public DrawFill(DrawBugItem drawBugItem) {
        this.drawBugItem = drawBugItem;
        action = new ActionFill();
    }

    public void draw(GL10 gl, int witch, int col, int row) {
        ActionFill mAction = (ActionFill) action;
        gl.glPushMatrix();
        gl.glTranslatef(0f, mAction.getY() * Configuration.translateRatio, 0f);
        drawBugItem.draw(gl, witch, col, row);
        gl.glPopMatrix();
    }
}
