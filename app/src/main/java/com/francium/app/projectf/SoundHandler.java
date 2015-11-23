package com.francium.app.projectf;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.francium.app.projectf.Configuration.E_SOUND;

import java.util.HashMap;

public class SoundHandler {

    int streamVolume;

    public SoundPool mSoundPool;

    public HashMap<Integer, Integer> mSoundPoolMap;

    Context mContext;

    public SoundHandler(Context context) {

        mContext = context;
        InitSounds();
    }

    private void InitSounds() {
        System.out.println("InitSounds");
        initSounds();
    }

    private void initSounds() {

        mSoundPool = new SoundPool(100, AudioManager.STREAM_MUSIC, 100);

        mSoundPoolMap = new HashMap<Integer, Integer>();

        AudioManager mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);

        loadSfx(R.raw.s_readygo, E_SOUND.READYGO.ordinal());
        loadSfx(R.raw.s_timeover, E_SOUND.TIMEOVER.ordinal());
        loadSfx(R.raw.s_cool, E_SOUND.COOL.ordinal());
        loadSfx(R.raw.s_disappear3, E_SOUND.DISAPPEAR3.ordinal());
        loadSfx(R.raw.s_disappear4, E_SOUND.DISAPPEAR4.ordinal());
        loadSfx(R.raw.s_disappear5, E_SOUND.DISAPPEAR5.ordinal());
        loadSfx(R.raw.s_slide, E_SOUND.SLIDE.ordinal());
        loadSfx(R.raw.s_super, E_SOUND.SUPER.ordinal());
        loadSfx(R.raw.s_fill, E_SOUND.FILL.ordinal());
        loadSfx(R.raw.s_specialitem, E_SOUND.SPECIALITEM.ordinal());
        loadSfx(R.raw.s_levelup, E_SOUND.LEVELUP.ordinal());
        loadSfx(R.raw.s_good, E_SOUND.GOOD.ordinal());
        loadSfx(R.raw.s_perfect, E_SOUND.PERFECT.ordinal());
        loadSfx(R.raw.s_lifeadd, E_SOUND.LIFEADD.ordinal());
        loadSfx(R.raw.s_lifedel, E_SOUND.LIFEDEL.ordinal());
        loadSfx(R.raw.s_invalid, E_SOUND.INVALID.ordinal());
    }

    private void loadSfx(int raw, int id) {

        mSoundPoolMap.put(id, mSoundPool.load(mContext, raw, id));
    }

    private void play(E_SOUND sound, int loop) {
        int id = sound.ordinal();
        mSoundPool.play(mSoundPoolMap.get(id), streamVolume, streamVolume, 1, loop, 1f);
    }

    public void play(E_SOUND sound) {
        play(sound, 0);
    }

}
