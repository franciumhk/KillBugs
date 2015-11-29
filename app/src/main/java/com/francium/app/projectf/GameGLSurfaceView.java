package com.francium.app.projectf;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.francium.app.projectf.Configuration.E_SCENARIO;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameGLSurfaceView extends GLSurfaceView {
    public Thread mThread = new Thread();
    public static boolean isSurfaceViewthreadRunning = true;
    private SceneRenderer mRenderer;
    Context mContext;
//    static boolean m_bThreadRun = false;
    static GameEngine gameEngine;
    TouchHandler touchHandler;

    public GameGLSurfaceView(Context context)
    {
        this(context, null);
    }
    public GameGLSurfaceView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }
    public GameGLSurfaceView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs);
        mRenderer = new SceneRenderer(context);
        setRenderer(mRenderer);
//        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//        Log.d("DEBUG", "onStartRendering");
        onInitRendering();
    }

    public void onInitRendering(){
//        if (!m_bThreadRun) {
//            m_bThreadRun = true;
        gameEngine = new GameEngine(mContext);
        isSurfaceViewthreadRunning = true;

        mThread = new Thread() {
            public void run() {
                while (isSurfaceViewthreadRunning) {
                    try {
                        gameEngine.run();
                        requestRender();
                        Thread.sleep(Configuration.DELAY_MS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mThread.start();
//        }
    }

    public void onStartRendering() {
        isSurfaceViewthreadRunning = true;
    }

    public void onStopRendering(){
        isSurfaceViewthreadRunning = false;
        mThread.interrupt();
//        m_bThreadRun = false;
        mThread = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (touchHandler != null) {
            if (GameEngine.mScene == E_SCENARIO.GAME) {
                if (GameEngine.GetIsBusy() == true)
                     return true;
                touchHandler.touchGameView(e);
            } else if (GameEngine.mScene == E_SCENARIO.MENU) {
//                touchHandler.touchMenuView(e);
            } else if (GameEngine.mScene == E_SCENARIO.RESULT) {
                touchHandler.touchResultView(e);
            }
        }
        return true;
    }

    private class SceneRenderer implements GLSurfaceView.Renderer {

        public SceneRenderer(Context context)
        {
            mContext = context;
        }

        public void onDrawFrame(GL10 gl) {
            gl.glShadeModel(GL10.GL_SMOOTH);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glTranslatef(0f, 0f, -10f);

            if (GameEngine.mScene == E_SCENARIO.GAME) {
                gameEngine.drawGameScene(gl);
            } else if (GameEngine.mScene == E_SCENARIO.MENU) {
//                gameEngine.drawMenuScene(gl);
            } else if (GameEngine.mScene == E_SCENARIO.RESULT) {
                gameEngine.drawResultScene(gl);
            }
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {

            Configuration.REAL_WIDTH = width;
            Configuration.REAL_HEIGHT = height;
            Configuration.translateRatio = (float) width / height;
            Configuration.screentRatio = (float) width / height;
            Configuration.ADP_SIZE = Configuration.UNIT_SIZE * Configuration.VIEW_HEIGHT / height * width / Configuration.VIEW_WIDTH;

//            Log.d("DEBUG", "REAL_WIDTH:" + Configuration.REAL_WIDTH);
//            Log.d("DEBUG", "REAL_HEIGHT:" + Configuration.REAL_HEIGHT);
//            Log.d("DEBUG", "translateRatio:" + Configuration.translateRatio);
//            Log.d("DEBUG", "screentRatio:" + Configuration.screentRatio);
//            Log.d("DEBUG", "ADP_SIZE:" + Configuration.ADP_SIZE);

            touchHandler = new TouchHandler(mContext, width, height);
            gl.glViewport(0, 0, width, height);
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();

            gl.glOrthof(-Configuration.screentRatio * Configuration.GRID_NUM / 2,
                    Configuration.screentRatio * Configuration.GRID_NUM / 2,
                    -1 * Configuration.GRID_NUM / 2,
                    1 * Configuration.GRID_NUM / 2, 10, 100);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//            Log.d("DEBUG", "onSurfaceCreated");
            gl.glDisable(GL10.GL_DITHER);
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
//            gl.glClearColor(0, 0, 0, 0);
            gl.glShadeModel(GL10.GL_SMOOTH);
            gl.glEnable(GL10.GL_DEPTH_TEST);

            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//            gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
            gl.glEnable(GL10.GL_ALPHA_TEST);
            gl.glAlphaFunc(GL10.GL_GREATER, 0.1f);

            gameEngine.initTexture(gl);
            gameEngine.initDraw(gl);

            Message msg = new Message();
            msg.what = GameEngine.GAME_START;
            GameEngine.mHandler.sendMessage(msg);
        }

    }

}
