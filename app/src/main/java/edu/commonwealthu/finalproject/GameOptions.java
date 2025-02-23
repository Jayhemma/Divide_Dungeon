package edu.commonwealthu.finalproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

/**
 * Handles the game options and inflates options menu
 * @author Justin Aul
 */
public class GameOptions {
    private static boolean micActivated = false;
    private static boolean soundEnabled = true;

    //In a future build, add an about/how to play button and an exit app button

    public static void toggleSound() {
        soundEnabled = !soundEnabled;
    }

    /**
     * Returns the sound status. Public due to calling in SoundManager
     */
    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * Toggles the mic on or off. Public due to calling in BattleFragment
     */
    public static void toggleMic() {micActivated = !micActivated;}

    /**
     * Returns the mic status.
     */
    public static boolean isMicActivated() {return micActivated;
    }

    /**
     * Clears the save data.
     */
    private void clearSaveData(Activity _activity) {
        ((MainActivity)_activity).clearSaveData();
    }

    /**
     * Inflates the options menu and initializes the game options buttons on creation.
     * @param _activity Is used to pass the activity to clearSaveData.
     */
    @SuppressLint("SetTextI18n")
    public GameOptions(Activity _activity) {
        //I could separate the inflation from the constructor, but the only time the
        //constructor would be called is if the menu should also be inflated.
        SoundManager soundManager = new SoundManager(_activity);

        LayoutInflater inflater = _activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_options, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        //Set the window behind the background image to be transparent
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Toggle Mic Button
        TextView micTextview = dialogView.findViewById(R.id.mic_text);
        micTextview.setText(toggleButtonText(micActivated) + " Mic");
        ImageButton toggleMic = dialogView.findViewById(R.id.toggle_mic);
        toggleMic.setOnClickListener(event -> {
            soundManager.playClickSound();
            toggleMic();
            micTextview.setText(toggleButtonText(micActivated) + " Mic");
        });

        //Toggle Sound Button
        TextView soundTextview = dialogView.findViewById(R.id.sound_text);
        soundTextview.setText(toggleButtonText(soundEnabled) + " Sound");
        ImageButton toggleSound = dialogView.findViewById(R.id.toggle_sound);
        toggleSound.setOnClickListener(event -> {
            soundManager.playClickSound();
            toggleSound();
            soundTextview.setText(toggleButtonText(soundEnabled) + " Sound");
        });

        //Clear Data Button
        //Make a confirm button in a later build, but this functions with caution
        ImageButton clearData = dialogView.findViewById(R.id.clear_data);
        clearData.setOnClickListener(event -> {
            soundManager.playClickSound();
            clearSaveData(_activity);
        });

        //Return to title button
        ImageButton returnButton = dialogView.findViewById(R.id.return_to_title);
        returnButton.setOnClickListener(event -> {
            soundManager.playClickSound();
            soundManager.release();
            ((MainActivity) _activity).returnToTitle();
            dialog.dismiss();
        });

        //Exit Button
        ImageButton exitButton = dialogView.findViewById(R.id.exit_options);
        exitButton.setOnClickListener(event -> {
            soundManager.playClickSound();
            soundManager.release();
            dialog.dismiss();
        });

        //Instructions Button
        ImageButton instructionsButton = dialogView.findViewById(R.id.instructions_button);
        instructionsButton.setOnClickListener(event -> {
            soundManager.playClickSound();
            soundManager.release();
            new InstructionDialog(_activity);
            dialog.dismiss();
        });


        dialog.show();
    }

    /**
     * Toggles the button text
     * @param condition The condition to check
     * @return "Disable" or "Enable" depending on the condition.
     */
    private String toggleButtonText(boolean condition) {
        String buttonText;
        if (condition) {
            buttonText = "Disable";
        } else {
            buttonText = "Enable";
        }
        return buttonText;
    }
}