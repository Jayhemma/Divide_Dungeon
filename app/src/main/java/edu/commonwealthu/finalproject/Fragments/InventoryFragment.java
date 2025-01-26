package edu.commonwealthu.finalproject.Fragments;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;

import edu.commonwealthu.finalproject.Player.Inventory;
import edu.commonwealthu.finalproject.MainActivity;
import edu.commonwealthu.finalproject.Player.Player;
import edu.commonwealthu.finalproject.R;
import edu.commonwealthu.finalproject.SoundManager;
import edu.commonwealthu.finalproject.WeaponDialog;


/**
 * The inventory fragment, allows player to manage their items.
 * @author Justin Aul
 */
public class InventoryFragment extends Fragment {
    Activity activity;
    ImageButton[] inventorySlot = new ImageButton[6];
    Inventory playerInventory;                  //Player's inventory
    int indexSelected = -1;                     //Index of selected item
    MainActivity.RoomType type;                 //Holds the type of room the player is in
    Player player;

    SoundManager soundManager;      //For playing sounds effects

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_inventory, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    /**
     * Initializes the inventory after the fragment is inflated
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        activity = getActivity();
        player = ((MainActivity) requireContext()).getPlayer();
        assert player != null;
        playerInventory = player.getInventory();
        type = ((MainActivity) requireContext()).getType();

        //Set up background
        ImageView background = activity.findViewById(R.id.main_backgroundImage);
        InputStream stream;
        try {
            stream = requireContext().getAssets().open("room_bgs/bg_inventory.png");
            background.setImageDrawable(Drawable.createFromStream(stream, null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Set up sound manager
        soundManager = new SoundManager(activity);

        //Set up the inventory slots
        initInventorySlots();

        //Set up the description text
        activity.findViewById(R.id.inventory_description).setVisibility(View.INVISIBLE);

        //Set up use/sell button text
        TextView utilityText = activity.findViewById(R.id.inventory_use_or_sell_text);
        if (type == MainActivity.RoomType.FIGHT) {utilityText.setText(R.string.use);}
        if (type == MainActivity.RoomType.SHOP) {utilityText.setText(R.string.sell);}

        //Set up the show weapons button
        ImageButton showWeaponsButton = activity.findViewById(R.id.inventory_show_weapons);
        showWeaponsButton.setOnClickListener(event -> {
            soundManager.playClickSound();
            showWeapons();
        });


        //Set up the exit button
        ImageButton exitButton = activity.findViewById(R.id.inventory_exit);
        exitButton.setOnClickListener(event -> {
            soundManager.playClickSound();
            ((MainActivity)activity).closeInventory();
        });
    }

    /**
     * Initializes the inventory slots. Displays images and sets event listeners.
     */
    private void initInventorySlots() {
        int displayWidth = getResources().getDisplayMetrics().widthPixels;  //Screen width
        displayWidth = (displayWidth - 50) / 4;
        //Since it is a square frame, width = height
        //noinspection SuspiciousNameCombination
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(displayWidth,
                displayWidth);
        inventorySlot[0] = activity.findViewById(R.id.inventory_slot_0);
        inventorySlot[1] = activity.findViewById(R.id.inventory_slot_1);
        inventorySlot[2] = activity.findViewById(R.id.inventory_slot_2);
        inventorySlot[3] = activity.findViewById(R.id.inventory_slot_3);
        inventorySlot[4] = activity.findViewById(R.id.inventory_slot_4);
        inventorySlot[5] = activity.findViewById(R.id.inventory_slot_5);
        for (int i = 0; i < 6; i++) {
            int _i = i;      //Not really sure why I need to proxy this, but it works
            inventorySlot[i].setOnClickListener(event-> {       //Set selected index here
                soundManager.playClickSound();
                indexSelected = _i;
                slotSelected();});
            inventorySlot[i].setLayoutParams(parms);
            inventorySlot[i].setScaleType(ImageView.ScaleType.FIT_XY);
            if (playerInventory.getItem(i) != null) {
                inventorySlot[i].setImageDrawable(playerInventory.getItem(i).getSprite());
            }
            else {inventorySlot[i].setVisibility(View.INVISIBLE);}
            inventorySlot[i].requestLayout();
        }
    }

    /**
     * If a slot is selected, activate the buy button and set the description block
     */
    @SuppressLint("SetTextI18n")
    private void slotSelected() {
        if ((indexSelected != -1) && (playerInventory.getItem(indexSelected)) != null) {
            //Change description text
            TextView description = activity.findViewById(R.id.inventory_description);
            description.setVisibility(View.VISIBLE);
            description.setText(playerInventory.getItem(indexSelected).getDescription());

            //Add an outline or highlight or something in the background attribute
            //or maybe a pulse animation?

            //Change button display
            ImageButton useButton = activity.findViewById(R.id.inventory_use_or_sell);
            useButton.setImageDrawable(getDrawable(requireContext(), R.drawable.button_long));
            TextView utilityText = activity.findViewById(R.id.inventory_use_or_sell_text);
            utilityText.setTextColor(ContextCompat.getColor(requireContext(),
                    R.color.text_color));
            if (type == MainActivity.RoomType.FIGHT) {
                utilityText.setText(            //Change button text
                        getString(R.string.use) + " " + playerInventory.
                                getItem(indexSelected).getName());

                useButton.setOnClickListener(event -> {
                    soundManager.playClickSound();
                    playerInventory.useItem(indexSelected);
                    utilityText.setText(getString(R.string.use));
                    refreshButton(useButton, description);
                });
            }
            if (type == MainActivity.RoomType.SHOP) {
                String sellString = getString(R.string.sell) + " " +    //Change button text
                        playerInventory.getItem(indexSelected).getName() + ": " +
                        playerInventory.getItem(indexSelected).getBuyPrice() + "C";
                if (sellString.length()>=20) {              //Account for large string
                    utilityText.setTextSize(40);
                } else { utilityText.setTextSize(50);}
                utilityText.setText(sellString);

                useButton.setOnClickListener(event -> {
                    soundManager.playClickSound();
                    player.addCoins(playerInventory.sellItem(indexSelected));
                    ((MainActivity) requireActivity()).updateToolBar();
                    utilityText.setText(getString(R.string.sell));
                    refreshButton(useButton, description);
                });
            }
        }
    }

    /**
     * Refreshes the inventory slots. Turns the use button off, and clears description
     * @param _useButton The button to use the object
     * @param _description The description block
     */
    private void refreshButton(ImageButton _useButton, TextView _description) {
        initInventorySlots();   //Refresh items display
        _useButton.setImageDrawable(
                getDrawable(requireContext(), R.drawable.button_long_inactive));
        TextView utilityText = activity.findViewById(R.id.inventory_use_or_sell_text);
        utilityText.setTextColor(ContextCompat.getColor(requireContext(),
                R.color.text_inactive));        //I know this process is awkward, this
                                                //text color was a last minute addition
        _description.setVisibility(View.INVISIBLE);
        indexSelected = -1;
    }

    /**
     * Shows the weapons dialog.
     */
    private void showWeapons() {
        WeaponDialog wd = new WeaponDialog(activity, player, type);
        wd.showWeapons();
    }

    /**
     * Releases the sound manager.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        soundManager.release();
    }
}
