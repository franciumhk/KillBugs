
package com.francium.app.projectf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

public class DrawAutoTip {

    private IntBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    int vCount = 0;
    int[] textureId;

    public EventAction action;

    public DrawAutoTip(int[] textureId) {
        this.textureId = textureId;
        action = new ActionAutoTip();
    }

    private void initVertexBuffer(int col, int row) {

        vCount = 6;
        int w = 32 * Configuration.ADP_SIZE;
        int h = 32 * Configuration.ADP_SIZE;
        int deltaX = ((col - 3) * 2 * w);
        int deltaY = ((row - 3) * 2 * h);
        int vertices[] = new int[]
                {
                        -w + deltaX, h + deltaY, 0,
                        -w + deltaX, -h + deltaY, 0,
                        w + deltaX, -h + deltaY, 0,
                        w + deltaX, -h + deltaY, 0,
                        w + deltaX, h + deltaY, 0,
                        -w + deltaX, h + deltaY, 0
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

    public void draw(GL10 gl, int col, int row) {
        if (!action.isRun()) return;
        ActionAutoTip mAction = (ActionAutoTip) action;
        int picId = mAction.getPicId();
        initVertexBuffer(col, row);
        initTextureBuffer();

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
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId[picId - 1]);

        gl.glDrawArrays
                (
                        GL10.GL_TRIANGLES,
                        0,
                        vCount
                );
        gl.glDisable(GL10.GL_TEXTURE_2D);
    }
}

