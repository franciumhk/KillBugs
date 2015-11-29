
package com.francium.app.projectf;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

public class DrawSingleScore {
    final int mBitmapW = 128;
    final int mBitmapH = 32;
    final int mFontSize = 24;

    private IntBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    public EventAction action;

    int vCount = 0;
    int textureId;

    public DrawSingleScore(GL10 gl) {
        initTextureBuffer();
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        this.textureId = textures[0];
        action = new ActionSingleScore();
    }

    private void initVertexBuffer(int col, int row, float x, float y) {
        if (col == 0) col = 1;
        vCount = 6;
        float deltaX = ((col - 3) * 64 * Configuration.ADP_SIZE);
        float deltaY = (((float) row - 3 + y) * 64 * Configuration.ADP_SIZE);
        int vertices[] = new int[]
                {
                        -mBitmapW / 2 * Configuration.ADP_SIZE + (int) deltaX, mBitmapH / 2 * Configuration.ADP_SIZE + (int) deltaY, 0,
                        -mBitmapW / 2 * Configuration.ADP_SIZE + (int) deltaX, -mBitmapH / 2 * Configuration.ADP_SIZE + (int) deltaY, 0,
                        mBitmapW / 2 * Configuration.ADP_SIZE + (int) deltaX, -mBitmapH / 2 * Configuration.ADP_SIZE + (int) deltaY, 0,
                        mBitmapW / 2 * Configuration.ADP_SIZE + (int) deltaX, -mBitmapH / 2 * Configuration.ADP_SIZE + (int) deltaY, 0,
                        mBitmapW / 2 * Configuration.ADP_SIZE + (int) deltaX, mBitmapH / 2 * Configuration.ADP_SIZE + (int) deltaY, 0,
                        -mBitmapW / 2 * Configuration.ADP_SIZE + (int) deltaX, mBitmapH / 2 * Configuration.ADP_SIZE + (int) deltaY, 0
                };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());

        mVertexBuffer = vbb.asIntBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
        return;
    }

    private void initTextureBuffer() {
        float textureCoors[] = new float[]
                {
                        0, 0,
                        0, 1,
                        1, 1,
                        1, 1,
                        1, 0,
                        0, 0
                };

        ByteBuffer cbb = ByteBuffer.allocateDirect(textureCoors.length * 4);
        cbb.order(ByteOrder.nativeOrder());

        mTextureBuffer = cbb.asFloatBuffer();
        mTextureBuffer.put(textureCoors);
        mTextureBuffer.position(0);
        return;
    }

    private void bindTexture(GL10 gl, Bitmap bmp) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
        bmp.recycle();
    }

    Bitmap genBitmap(int score) {

        Bitmap bitmap = Bitmap.createBitmap(mBitmapW, mBitmapH, Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);

        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        paint.setTextSize(mFontSize);
        paint.setColor(Configuration.COLOR_UNIT_SCORE);
        String str = Integer.toString(score);
        canvas.drawText(str, 20, 28, paint);

        return bitmap;
    }

    public void draw(GL10 gl, int col, int row, int score) {
        if (!action.isRun()) return;
        Bitmap bmp = genBitmap(score);
        bindTexture(gl, bmp);
        ActionSingleScore mAction = (ActionSingleScore) action;
        initVertexBuffer(col, row, 0, mAction.getY() / 30.0f);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glVertexPointer
                (
                        3,
                        GL10.GL_FIXED,
                        0,
                        mVertexBuffer
                );

        gl.glEnable(GL10.GL_TEXTURE_2D);

        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glTexCoordPointer
                (
                        2,
                        GL10.GL_FLOAT,
                        0,
                        mTextureBuffer
                );
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

        gl.glDrawArrays
                (
                        GL10.GL_TRIANGLES,
                        0,
                        vCount
                );
        gl.glDisable(GL10.GL_TEXTURE_2D);

    }

}
