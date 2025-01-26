package edu.commonwealthu.finalproject;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import edu.commonwealthu.finalproject.Player.Player;
import edu.commonwealthu.finalproject.Player.Weapon;

/**
 * Creates a dialog for viewing/swapping weapons
 * @author Justin Aul
 */
public class WeaponDialog {
    Activity activity;
    Player player;
    int indexSelected = -1;
    MainActivity.RoomType type;
    Weapon[] weapons;
    Weapon newWeapon;                           //For use in the treasure room

    SoundManager soundManager;      //For playing sounds effects

    /**
     * Creates a new weapon dialog
     */
    public WeaponDialog(Activity _activity, Player _player, MainActivity.RoomType _type) {
        activity = _activity;
        player = _player;
        type = _type;
        weapons = player.getWeaponList();
    }

    /**
     * Shows the weapon dialog
     */
    public void showWeapons() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_show_weapons, null);

        soundManager = new SoundManager(activity);

        ImageButton replaceButton = dialogView.findViewById(R.id.replace_button);

        //Display the weapons buttons
        ImageButton[] weaponButtons = new ImageButton[3];
        weaponButtons[0] = dialogView.findViewById(R.id.weapon_slot_0);
        weaponButtons[1] = dialogView.findViewById(R.id.weapon_slot_1);
        weaponButtons[2] = dialogView.findViewById(R.id.weapon_slot_2);
        for (int i = 0; i < 3; i++) {
            weaponButtons[i].setImageDrawable(weapons[i].getSprite());
            int _i = i;
            weaponButtons[i].setOnClickListener(event -> {
                soundManager.playClickSound();
                indexSelected = _i;
                updateDescription(dialogView);  //Set description, also select animation?
                //Activate the replace button if the room type is treasure
                if (type == MainActivity.RoomType.TREASURE) {
                    replaceButton.setImageDrawable(activity.getDrawable(R.drawable.button_medium));
                }
            });
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        //Set the window behind the background image to be transparent
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Functionality for replace button
        //If the room type is treasure, show the replace button
        if (type == MainActivity.RoomType.TREASURE) {
            dialogView.findViewById(R.id.replace_button_root).setVisibility(View.VISIBLE);
            replaceButton.setOnClickListener(event -> {
                soundManager.playClickSound();
                replaceWeapon();
                soundManager.release();
                dialog.dismiss();
                ((MainActivity) activity).openTransitionFragment();
            });
        }

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

    /**
     * Passes a weapon to the dialog. Called from the treasure fragment
     * @param _weapon The weapon to pass
     */
    public void passWeapon(Weapon _weapon) {
        newWeapon = _weapon;
    }

    /**
     * Replaces the weapon in the player's inventory with the stored passed weapon.
     */
    private void replaceWeapon() {
        if (newWeapon != null) {
            player.replaceWeapon(indexSelected, newWeapon);
        }
    }

    /**
     * Updates the description of the weapon
     * @param view
     */
    private void updateDescription(View view) {
        TextView description = view.findViewById(R.id.show_weapons_description);
        description.setText(weapons[indexSelected].getDescription());
    }
}
