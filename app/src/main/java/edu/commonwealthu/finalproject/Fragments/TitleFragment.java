package edu.commonwealthu.finalproject.Fragments;

import static java.lang.Math.pow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import edu.commonwealthu.finalproject.GlobalModifiers;
import edu.commonwealthu.finalproject.MainActivity;
import edu.commonwealthu.finalproject.R;
import edu.commonwealthu.finalproject.SoundManager;

/**
 * The title fragment of the game.
 * @author Justin Aul
 */
public class TitleFragment  extends Fragment {
    Activity activity;
    enum Upgrade {HEALTH, COINS, POWER, TIMER}
    SoundManager soundManager;      //For playing sounds effects

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_title, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    /**
     * Initializes the title fragment once the fragment is inflated
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        activity = getActivity();
        assert activity != null;

        //Set up sound manager
        soundManager = new SoundManager(getActivity());

        //Set up start button
        ImageButton startButton = activity.findViewById(R.id.title_play_button);
        startButton.setOnClickListener(event -> {
            soundManager.playClickSound();
            ((MainActivity)activity).newGame();
        });
        //Make the elements fade, before fading to black? Later build

        //Set up background
        ImageView background = activity.findViewById(R.id.main_backgroundImage);
        InputStream stream;
        try {
            stream = requireContext().getAssets().open("room_bgs/bg_title.png");
            background.setImageDrawable(Drawable.createFromStream(stream, null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Set up shop button
        ImageButton shopButton = activity.findViewById(R.id.title_shop_button);
        shopButton.setOnClickListener(event -> {
            soundManager.playClickSound();
            showShopDialog();
        });

        //Display token count under the shop button
        updateTokenCount();
    }

    /**
     * Updates the token count in the title screen
     */
    @SuppressLint("SetTextI18n")
    private void updateTokenCount() {
        TextView tokenCount = activity.findViewById(R.id.title_shop_coins);
        tokenCount.setText(getString(R.string.tokens) + GlobalModifiers.upgradeToken);
    }

    /**
     * Shows the shop dialog
     */
    private void showShopDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_upgrade_shop, null);

        updateButtons(dialogView);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        //Set the window behind the background image to be transparent
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

        //Create exit button
        ImageButton exitButton =
                dialogView.findViewById(R.id.upgrade_exit_button);
        exitButton.setOnClickListener(event -> {
            soundManager.playClickSound();
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * Updates the buttons in the shop dialog. Called when the player buys an upgrade
     * or opens the shop dialog.
     * @param view The view to update
     */
    @SuppressLint("SetTextI18n")
    private void updateButtons(View view) {
        //Display token count
        TextView tokenCount = view.findViewById(R.id.dialog_upgrade_tokens);
        tokenCount.setText(getString(R.string.tokens) + GlobalModifiers.upgradeToken);

        //Upgrade health
        TextView healthPrice = view.findViewById(R.id.upgrade_max_health_price);
        ImageButton healthButton =
                view.findViewById(R.id.upgrade_max_health_button);
        generateButton(healthPrice, healthButton, view, Upgrade.HEALTH);

        //Upgrade starting coins
        TextView coinsPrice = view.findViewById(R.id.upgrade_start_coins_price);
        ImageButton coinButton =
                view.findViewById(R.id.upgrade_start_coins_button);
        generateButton(coinsPrice, coinButton, view, Upgrade.COINS);

        //Upgrade attack
        TextView powerPrice = view.findViewById(R.id.upgrade_damage_price);
        ImageButton powerButton =
                view.findViewById(R.id.upgrade_damage_button);
        generateButton(powerPrice, powerButton, view, Upgrade.POWER);

        //Upgrade timer
        TextView timerPrice = view.findViewById(R.id.upgrade_timer_add_price);
        ImageButton timerButton =
                view.findViewById(R.id.upgrade_timer_add_button);
        generateButton(timerPrice, timerButton, view, Upgrade.TIMER);

    }

    /**
     * Helper method to generate the buttons for the shop dialog.
     * @param textBox The text box to display the price
     * @param button The button to display
     * @param view The view to update
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void generateButton(TextView textBox, ImageButton button,
                                View view, Upgrade type) {
        int price = generatePrices(type);
        textBox.setText(String.valueOf(price));
        if (GlobalModifiers.upgradeToken >= price) {
            button.setOnClickListener(event -> {
                soundManager.playClickSound();
                buyUpgrade(type, price, view);
            });
            button.setImageDrawable(activity.getDrawable(R.drawable.button_short));
            textBox.setTextColor(ContextCompat.getColor(requireContext(),
                    R.color.text_color));
        } else {
            button.setImageDrawable(activity.getDrawable(R.drawable.button_short_inactive));
            //Do nothing when pressed
            button.setOnClickListener(event -> soundManager.playClickSound());
            textBox.setTextColor(ContextCompat.getColor(requireContext(),
                    R.color.text_inactive));
        }
    }

    /**
     * Generates the prices for the upgrades depending on how many have been bought
     * previously
     * @param upgrade The upgrade to generate the price for
     * @return The price of the upgrade
     */
    private int generatePrices(Upgrade upgrade) {
        switch (upgrade) {
            case HEALTH:
                return (int)(150 * pow(3, GlobalModifiers.playerMaxHealth-3));
            case COINS:
                return (int)(50 * pow(1.5, GlobalModifiers.startCoinsUpgrades));
            case POWER:
                return (int)(75 * pow(1.9, GlobalModifiers.numAttackUpgrades));
            case TIMER:
                return (int)(100 * pow(2.5, GlobalModifiers.numTimerUpgrades));
            default:
                return 0;
        }
    }

    /**
     * Handles the buying of an upgrade
     * @param upgrade The upgrade to buy
     * @param price The price of the upgrade
     */
    private void buyUpgrade(Upgrade upgrade, int price, View view) {
        switch (upgrade) {
            case HEALTH:
                GlobalModifiers.playerMaxHealth++;
                break;
            case COINS:
                GlobalModifiers.startCoinsUpgrades++;
                break;
            case POWER:
                GlobalModifiers.numAttackUpgrades++;
                break;
            case TIMER:
                GlobalModifiers.numTimerUpgrades++;
                break;
        }
        GlobalModifiers.upgradeToken -= price;
        updateTokenCount();
        updateButtons(view);
    }

    /**
     * Releases the sound manager when the fragment is detached
     */
    @Override
    public void onDetach() {
        super.onDetach();
        soundManager.release();
    }
}
