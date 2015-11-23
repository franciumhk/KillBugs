
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

public class DrawTime {

    int mMaxTime = 0;

    private IntBuffer mNumVertexBuffer;
    private FloatBuffer mNumTextureBuffer;
    int vCount = 0;
    int numTextureId;

    final int mBitmapW = 80 * 12;
    final int mBitmapH = 80 * 3;
    final int mFontSize = 24 * 2;

    float textureRatio;

    public DrawTime(GL10 gl, int maxTime) {
        this.mMaxTime = maxTime;
        initNumTextureBuffer();
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        this.numTextureId = textures[0];
    }

    private void initNumVertexBuffer() {
        vCount = 6;
        float deltaX = -64 * 2.5f * Configuration.ADP_SIZE;
        float deltaY = -64 * 5f * Configuration.ADP_SIZE;
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

        mNumVertexBuffer = vbb.asIntBuffer();
        mNumVertexBuffer.put(vertices);
        mNumVertexBuffer.position(0);
        return;
    }

    private void initNumTextureBuffer() {
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

        mNumTextureBuffer = cbb.asFloatBuffer();
        mNumTextureBuffer.put(textureCoors);
        mNumTextureBuffer.position(0);
        return;
    }

    private void bindTexture(GL10 gl, Bitmap bmp) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, numTextureId);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
        bmp.recycle();
    }

    Bitmap genBitmap(int timeLeft) {

        Bitmap bitmap = Bitmap.createBitmap(mBitmapW, mBitmapH, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        paint.setTextSize(mFontSize);
        paint.setColor(Color.BLACK);
        String str = "Time Left: " + Integer.toString(timeLeft);
        canvas.drawText(str, canvas.getWidth() / 2, canvas.getHeight() / 2, paint);

        return bitmap;
    }

    public void drawNumber(GL10 gl, int timeLeft) {
        if (timeLeft < 0) return;
        Bitmap bmp = genBitmap(timeLeft);
        bindTexture(gl, bmp);
        initNumVertexBuffer();

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glVertexPointer
                (
                        3,
                        GL10.GL_FIXED,
                        0,
                        mNumVertexBuffer
                );

        gl.glEnable(GL10.GL_TEXTURE_2D);

        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glTexCoordPointer
                (
                        2,
                        GL10.GL_FLOAT,
                        0,
                        mNumTextureBuffer
                );
        gl.glBindTexture(GL10.GL_TEXTURE_2D, numTextureId);

        gl.glDrawArrays
                (
                        GL10.GL_TRIANGLES,
                        0,
                        vCount
                );
        gl.glDisable(GL10.GL_TEXTURE_2D);

    }

    public void draw(GL10 gl, int timeLeft) {
        drawNumber(gl, timeLeft);
    }
}


