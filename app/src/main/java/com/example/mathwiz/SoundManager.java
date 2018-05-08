package com.example.mathwiz;

import android.content.Context;
import android.media.SoundPool;

/**
 * Created by Callum on 4/04/2018.
 * Repository: https://github.com/callumr1/FoleyApp/blob/master/app/src/main/java/com/example/foleyapp/SoundManager.java
 */

class SoundManager {
    private SoundPool soundPool;
    private Context context;

    SoundManager(Context context){
        this.context = context;
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(10);
        soundPool = builder.build();
    }

    int addSound(int resourceID){
        return soundPool.load(context, resourceID, 1);
    }

    void play(int soundID, int loop){
        soundPool.play(soundID, 1, 1, 1, loop, 1);
    }
}
