package com.example.mathwiz;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by Callum on 1/05/2018.
 */

class Background {

    private static MediaPlayer mediaPlayer;

    static void run(Runnable runnable){
        Thread thread = new Thread(runnable);
        thread.start();
    }

    static void playBackgroundMusic(Context context){
        mediaPlayer = MediaPlayer.create(context, R.raw.background_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    static void stopBackgroundMusic(Context context){
        mediaPlayer.stop();
    }
}
