
package com.francium.app.projectf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

public class DrawBackGround {

    private IntBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    int vCount = 0;
    int textureId;

    public DrawBackGround(int textureId) {
        this.textureId = textureId;
    }

    private void initVertexBuffer() {

        vCount = 6;

        int w = (Configuration.REAL_WIDTH / 2) * Configuration.ADP_SIZE;
        int h = (Configuration.REAL_HEIGHT / 2) * Configuration.ADP_SIZE;
        int vertices[] = new int[]
                {
                        -w, h, 0,
                        -w, -h, 0,
                        w, -h, 0,
                        w, -h, 0,
                        w, h, 0,
                        -w, h, 0
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

    public void draw(GL10 gl) {
        initVertexBuffer();
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
