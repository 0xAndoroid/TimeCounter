package xyz.andoroid.timecounter.model;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

public class MusicUtils {
    private MediaPlayer player;

    public MusicUtils(Context c, int song) {
        player = MediaPlayer.create(c, song);
    }

    public void play(final int duration) {
        player.start();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(duration);
                }catch ( InterruptedException ignored) {}
                System.out.println("stop");
                player.stop();
                try {
                    player.prepare();
                } catch (IOException ignored) {}
            }
        });
        thread.start();
    }
}
