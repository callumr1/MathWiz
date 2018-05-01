package com.example.mathwiz;

/**
 * Created by Callum on 1/05/2018.
 */

public class Background {
    static void run(Runnable runnable){
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
