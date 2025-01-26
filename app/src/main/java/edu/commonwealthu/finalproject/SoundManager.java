package edu.commonwealthu.finalproject;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Provides game-related sound effects to an activity.
 * @author Justin Aul
 */
public class SoundManager {
    private SoundPool soundPool;
    private final int clickSound;  // UI element is pressed
    private final int attackSound[];    //Holds the damage sound effects

    /**
     * Initializes a new sound manager for a given context.
     */
    public SoundManager(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder().setMaxStreams(1)
                .setAudioAttributes(audioAttributes).build();


        attackSound = new int[2];
        attackSound[0] = soundPool.load(context, R.raw.attack_0, 1);
        attackSound[1] = soundPool.load(context, R.raw.attack_1, 1);
        //Create a damage taken sound effect, probably wont be in this build
        clickSound = soundPool.load(context, R.raw.click, 1);
    }

    /**
     * Releases all memory and resources used by the SoundPool.
     */
    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    public void playClickSound() {
        play(clickSound);
    }
    public void playAttackSound() {
        //Random between the two sounds
        play(attackSound[ThreadLocalRandom.current().nextInt(0,2)]);
    }

    /**
     * Plays a sound specified by its resource ID.
     */
    private void play(int id) {
        if (soundPool != null && GameOptions.isSoundEnabled()) {
            soundPool.play(id, 1, 1, 0, 0, 1);
        }
    }
}
