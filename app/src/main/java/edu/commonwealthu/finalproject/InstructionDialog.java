package edu.commonwealthu.finalproject;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class InstructionDialog {
    Activity activity;
    SoundManager soundManager;      //For playing sounds effects

    private int page;
    private Drawable[] images;      //Visual display of each page
    private String[] instructions;  //Text display of each page

    /**
     * Creates a new instruction dialog
     * @param _activity
     */
    public InstructionDialog(Activity _activity) {
        activity = _activity;
        showInstructions(); //Automatically call the show instructions
    }

    private void showInstructions() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_instructions, null);

        soundManager = new SoundManager(activity);

        page = 0; //Begin on page 0

        //Add images and instruction texts to their arrays and set the displays to
        //first index
        images = new Drawable[]{
                activity.getDrawable(R.drawable.instruction_page_0),
                activity.getDrawable(R.drawable.instruction_page_1),
                activity.getDrawable(R.drawable.instruction_page_2),
                activity.getDrawable(R.drawable.instruction_page_3),
                activity.getDrawable(R.drawable.instruction_page_4)
        };
        ImageView imageView = dialogView.findViewById(R.id.instruction_image);
        imageView.setImageDrawable(images[0]);

        instructions = new String[]{
                activity.getString(R.string.instruction_text_0),
                activity.getString(R.string.instruction_text_1),
                activity.getString(R.string.instruction_text_2),
                activity.getString(R.string.instruction_text_3),
                activity.getString(R.string.instruction_text_4)
        };
        TextView instructionsText = dialogView.findViewById(R.id.instructions_text);
        instructionsText.setText(instructions[page]);

        //Handle next and previous buttons
        ImageButton prevButton = dialogView.findViewById(R.id.prev_button);
        ImageButton nextButton = dialogView.findViewById(R.id.next_button);
        prevButton.setOnClickListener(event ->{
            soundManager.playClickSound();
            changePage(false, dialogView);
        });
        nextButton.setOnClickListener(event ->{
            soundManager.playClickSound();
            changePage(true, dialogView);
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        //Set the window behind the background image to be transparent
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Create exit button
        ImageButton exitButton =
                dialogView.findViewById(R.id.close_button);
        exitButton.setOnClickListener(event -> {
            soundManager.playClickSound();
            soundManager.release();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void changePage(boolean next, View view) {
        //Based on the next boolean, increase or decrease the page count
        page = next ? page + 1 : page - 1;
        if (page < 0) { page = 0; }
        if (page > images.length - 1) { page = images.length - 1; }

        //Set new image
        ImageView imageView = view.findViewById(R.id.instruction_image);
        imageView.setImageDrawable(images[page]);

        //Set new instructions text
        TextView instructionsText = view.findViewById(R.id.instructions_text);
        instructionsText.setText(instructions[page]);
    }

}
