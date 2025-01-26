package edu.commonwealthu.finalproject.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import edu.commonwealthu.finalproject.MainActivity;
import edu.commonwealthu.finalproject.R;
import edu.commonwealthu.finalproject.SoundManager;

/**
 * The transition fragment of the game. Allows player a choice of rooms to select
 */
public class TransitionFragment extends Fragment {
    MainActivity.RoomType[] types;
    Activity activity;
    SoundManager soundManager;      //For playing sounds effects

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_transition, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    /**
     * Initializes the fragment once the fragment is inflated
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        activity = getActivity();

        //Set up background
        ImageView background = activity.findViewById(R.id.transition_backgroundImage);
        InputStream stream;
        try {
            stream = requireContext().getAssets().open(
                    "room_bgs/bg_fight_transition.png");
            background.setImageDrawable(Drawable.createFromStream(stream, null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Set up sound manager
        soundManager = new SoundManager(getActivity());

        //Create 3 relative layouts, set visibility to gone on ones that don't exist
        RelativeLayout[] paths = new RelativeLayout[3];
        assert activity != null;
        paths[0] = activity.findViewById(R.id.path_0);
        paths[1] = activity.findViewById(R.id.path_1);
        paths[2] = activity.findViewById(R.id.path_2);
        ImageButton[] buttons = new ImageButton[3];
        buttons[0] = activity.findViewById(R.id.path_0_button);
        buttons[1] = activity.findViewById(R.id.path_1_button);
        buttons[2] = activity.findViewById(R.id.path_2_button);
        TextView[] roomNames = new TextView[3];
        roomNames[0] = activity.findViewById(R.id.path_0_name);
        roomNames[1] = activity.findViewById(R.id.path_1_name);
        roomNames[2] = activity.findViewById(R.id.path_2_name);

        //Generate number of rooms to create, between 1 and 3.
        //Set the buttons, their size, and their functionality.
        int pathCount = new Random().nextInt(3)+1;
        types = new MainActivity.RoomType[pathCount];   //Pass the list of room types
        int displayWidth = getResources().getDisplayMetrics().widthPixels;  //Screen width
        displayWidth = (displayWidth) / pathCount;         //Dynamic button width
        displayWidth = Math.min(displayWidth, 256);         //256 is native size
        for (int i = 0; i < pathCount; i++) {
            paths[i].setVisibility(View.VISIBLE);
            buttons[i].getLayoutParams().width = displayWidth;
            //noinspection SuspiciousNameCombination
            buttons[i].getLayoutParams().height = displayWidth;
            buttons[i].setScaleType(ImageView.ScaleType.FIT_XY);
            buttons[i].requestLayout();
            generateRoom(buttons[i], roomNames[i], i);
        }

        //Set up open inventory button
        ImageButton openInventory = activity.findViewById(R.id.transition_inventory);
        openInventory.setOnClickListener(event -> {
            soundManager.playClickSound();
            ((MainActivity)activity).openInventoryFragment();
        });
    }

    /**
     * Generates a room depending on chance weights
     * @param button The button to apply it to
     * @param roomName The room name to apply it to
     * @param index The index of the room in the array
     */
    private void generateRoom(ImageButton button, TextView roomName, int index) {
        //Generate the room type
        MainActivity.RoomType[] _types = {
                MainActivity.RoomType.TREASURE,
                MainActivity.RoomType.SHOP,
                MainActivity.RoomType.FIGHT
        };
        int[] weights = {
                15,         //% chance of treasure
                10,         //% chance of shop
                75          //% chance of fight
        };

        //Calculate sum of weights, done dynamically to leave room for future types
        int sum = 0;
        for (int weight : weights) {
            sum += weight;
        }
        //Algorithm to generate room type from weights
        int random = new Random().nextInt(sum);
        for (int i = 0; i < weights.length; i++) {
            if (random < weights[i]) {
                types[index] = _types[i];
                generateButtonAndName(button, roomName, types[index]);
                break;
            }
            random -= weights[i];
        }
    }

    /**
     * Generates the button and room name
     */
    private void generateButtonAndName(ImageButton button, TextView roomName,
                                     MainActivity.RoomType type) {
        InputStream stream;
        try {
            switch (type) {
                case TREASURE:
                    stream = requireContext().getAssets().open(
                            "room_icons/icon_treasure_room.png");
                    button.setImageDrawable(Drawable.createFromStream(stream, null));
                    button.setOnClickListener(event -> {
                        soundManager.playClickSound();
                        ((MainActivity) getActivity()).incrementRoom();
                        ((MainActivity) getActivity()).updateToolBar();
                        ((MainActivity) getActivity()).openTreasureFragment();
                    });
                    roomName.setText(R.string.treasure);
                    break;
                case SHOP:
                    stream = getContext().getAssets().open(
                            "room_icons/icon_shop_room.png");
                    button.setImageDrawable(Drawable.createFromStream(stream, null));
                    button.setOnClickListener(event -> {
                        soundManager.playClickSound();
                        ((MainActivity) getActivity()).incrementRoom();
                        ((MainActivity) getActivity()).updateToolBar();
                        ((MainActivity) getActivity()).openShopFragment();
                    });
                    roomName.setText(R.string.shop);
                    break;
                case FIGHT:
                    stream = getContext().getAssets().open(
                            "room_icons/icon_fight_room.png");
                    button.setImageDrawable(Drawable.createFromStream(stream, null));
                    button.setOnClickListener(event -> {
                        soundManager.playClickSound();
                        ((MainActivity) getActivity()).incrementRoom();
                        ((MainActivity) getActivity()).updateToolBar();
                        ((MainActivity) getActivity()).openBattleFragment();
                    });
                    roomName.setText(R.string.fight);
                    break;
            }
        } catch (IOException e) {   //This should never be reached
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        soundManager.release();
    }
}