package com.francium.app.projectf;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

public class DrawString {
    private IntBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    int vCount = 0;
    int textureId;

    public DrawString(GL10 gl) {
        initNumTextureBuffer();
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        this.textureId = textures[0];
    }

    private void initVertexBuffer(int mBitmapW, int mBitmapH, int positionX, int positionY) {
        vCount = 6;
        float deltaX = -positionX * 5f * Configuration.ADP_SIZE;
        float deltaY = -positionY * 5f * Configuration.ADP_SIZE;
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

    Bitmap genBitmap(String str, int fontSize, int mBitmapW, int mBitmapH, int color) {
        Bitmap bitmap = Bitmap.createBitmap(mBitmapW, mBitmapH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        paint.setTextSize(fontSize);
        paint.setColor(color);
        canvas.drawText(str, canvas.getWidth()/2 , canvas.getHeight()/2, paint);

        return bitmap;
    }

    public void draw(GL10 gl, String str, int fontSize, int positionX, int positionY, int color) {

        int mBitmapW = fontSize * str.length() * 4;
        int mBitmapH = fontSize * 4;

        Bitmap bmp = genBitmap(str, fontSize, mBitmapW, mBitmapH, color);
        bindTexture(gl, bmp);
        initVertexBuffer(mBitmapW, mBitmapH, positionX, positionY);

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
