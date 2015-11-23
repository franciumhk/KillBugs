package com.francium.app.projectf;

import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

public class GameActivity extends AppCompatActivity {
//    GameGLSurfaceView mGLSurfaceView;
    private MediaPlayer mp;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("DEBUG", "onCreate");
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
//        mGLSurfaceView = new GameGLSurfaceView(this);
//        setContentView(mGLSurfaceView);
//        mGLSurfaceView.requestFocus();
//        mGLSurfaceView.setFocusableInTouchMode(true);
    }

    @Override
    protected void onStart() {
        Log.d("DEBUG", "onStart");
        super.onStart();
        GameEngine.mScoreHandler.init();
        GameEngine.mTimeHandler.reset();
        GameEngine.init();
//        if (mp == null) {
//            mp = MediaPlayer.create(this, R.raw.s_background);
//            mp.setLooping(true);
//            mp.start();
//        }
    }

    @Override
    protected void onStop() {
        Log.d("DEBUG", "onStop");
        GameEngine.mTimeHandler.pause();
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.d("DEBUG", "onResume");
        super.onResume();
//        mGLSurfaceView.onResume();
//        mp.start();
        GameEngine.mTimeHandler.resume();
    }

    @Override
    protected void onPause() {
        Log.d("DEBUG", "onPause");
        super.onPause();
//        mGLSurfaceView.onPause();
//        mp.pause();
        GameEngine.mTimeHandler.pause();
    }

    @Override
    public void onDestroy() {
        Log.d("DEBUG", "onDestroy");
        super.onDestroy();
//        mp.stop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish(); // or do something else
    }
}
