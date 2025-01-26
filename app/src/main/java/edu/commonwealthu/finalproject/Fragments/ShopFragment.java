package edu.commonwealthu.finalproject.Fragments;

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
import edu.commonwealthu.finalproject.Player.Player;
import edu.commonwealthu.finalproject.R;
import edu.commonwealthu.finalproject.SoundManager;
import edu.commonwealthu.finalproject.Usables.Apple;
import edu.commonwealthu.finalproject.Usables.AttackPotion;
import edu.commonwealthu.finalproject.Usables.HealthPotion;
import edu.commonwealthu.finalproject.Usables.InvObject;
import edu.commonwealthu.finalproject.Usables.SlowPotion;

/**
 * The shop fragment, allows player to buy items.
 * @author Justin Aul
 */
public class ShopFragment extends Fragment {
    InvObject[] itemsForSale;                   //Array of items for sale
    RelativeLayout[] layouts;                   //Array of the containers
    ImageButton[] itemButtons;                  //Array of the buttons
    TextView[] itemPrices;                      //Array of the prices
    int indexSelected = -1;                     //Index of selected item
    Player player;
    TextView description;                       //The shopkeeper's dialog
    View currentView;

    SoundManager soundManager;      //For playing sounds effects

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_shop, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    /**
     * Initializes the shop fragment once the fragment is inflated
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        //Set up background
        ImageView background = requireActivity().findViewById(R.id.shop_backgroundImage);
        InputStream stream;
        try {
            stream = requireContext().getAssets().open(
                    "room_bgs/bg_shop.png");
            background.setImageDrawable(Drawable.createFromStream(stream, null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Set up sound manager
        soundManager = new SoundManager(getActivity());

        currentView = view;
        player = ((MainActivity) requireContext()).getPlayer();

        //Get references to the relative layouts
        layouts = new RelativeLayout[]{
                currentView.findViewById(R.id.item_layout_0),
                currentView.findViewById(R.id.item_layout_1),
                currentView.findViewById(R.id.item_layout_2)
        };

        //Get references to the buttons
        itemButtons = new ImageButton[]{
                currentView.findViewById(R.id.item_button_0),
                currentView.findViewById(R.id.item_button_1),
                currentView.findViewById(R.id.item_button_2)
        };

        //Get references to the text views
        description = currentView.findViewById(R.id.shop_description);
        itemPrices = new TextView[]{
                currentView.findViewById(R.id.item_price_0),
                currentView.findViewById(R.id.item_price_1),
                currentView.findViewById(R.id.item_price_2)

        };

        //If items weren't already generated
        if (itemsForSale == null) {
             itemsForSale = new InvObject[3];
            for (int i = 0; i < itemsForSale.length; i++) {
                //Populate the items for sale
                itemsForSale[i] = generateItemForSale();
            }
        }

        //Display the items
        displayItems();

        //Set up inventory button
        ImageButton inventoryButton = currentView.findViewById(R.id.inventory_button);
        inventoryButton.setOnClickListener(event -> {
            soundManager.playClickSound();
            ((MainActivity) requireActivity()).openInventoryFragment();
        });

        //Set up exit button
        ImageButton exitButton = currentView.findViewById(R.id.exit_button);
        exitButton.setOnClickListener(event -> {
            soundManager.playClickSound();
            ((MainActivity) requireActivity()).openTransitionFragment();
        });
    }

    /**
     * Displays the items for sale.
     */
    private void displayItems() {
        //Dynamically set the size of the items
        int displayWidth = getResources().getDisplayMetrics().widthPixels;  //Screen width
        displayWidth = (displayWidth - 50) / 4;
        RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(displayWidth,
                displayWidth);
        for (int i = 0; i < itemsForSale.length; i++) {
            if (itemsForSale[i] == null) {              //If slot is bought
                layouts[i].setVisibility(View.INVISIBLE);    //Make spot invisible
                continue;
            }
            //Else Set up the buttons and costs
            itemPrices[i].setText(String.valueOf(itemsForSale[i].getBuyPrice()));
            itemButtons[i].setImageDrawable(itemsForSale[i].getSprite());
            itemButtons[i].setLayoutParams(parms);
            int _i = i;       //Again, not sure why event listeners require this proxy
            itemButtons[i].setOnClickListener(event -> {
                soundManager.playClickSound();
                indexSelected = _i;
                setDescription();       //Sets the shop keeper's dialog
                //Do some highlighted effect or animation
                toggleBuyButton();      //Tests if the player has enough money
            });
        }
    }

    /**
     * If the player has enough money to buy the selected button, enable the buy button
     */
    private void toggleBuyButton() {
        ImageButton buyButton = currentView.findViewById(R.id.item_buy_button);

        //Test if the index is in bounds.
        if(indexSelected == -1) {
            buyButton.setImageDrawable(
                    getResources().getDrawable(R.drawable.button_medium_inactive));
            buyButton.setOnClickListener(Event -> {});      //Do nothing
            return;
        }

        //Test if the player has enough money
        if(player.getCoins() >= itemsForSale[indexSelected].getBuyPrice()) {
            buyButton.setImageDrawable(
                    getResources().getDrawable(R.drawable.button_medium));
            buyButton.setOnClickListener(Event -> {
                soundManager.playClickSound();
                buySelected();
            });
        }
        //If not, disable the button
        else {
            buyButton.setImageDrawable(
                    getResources().getDrawable(R.drawable.button_medium_inactive));
            buyButton.setOnClickListener(Event -> soundManager.playClickSound());
            //Do nothing
        }
    }

    /**
     * Buys the selected item. Removes coins from the player and removes the item from
     * the shop.
     */
    private void buySelected() {
        if(player.getInventory().isFull()) {
            description.setText(R.string.shop_full);
        }
        else {
            description.setText(R.string.item_sold);
            player.getInventory().addItem(itemsForSale[indexSelected]);
            player.removeCoins(itemsForSale[indexSelected].getBuyPrice());
            ((MainActivity) requireActivity()).updateToolBar();
            itemsForSale[indexSelected] = null;
            indexSelected = -1;
            displayItems();             //Update buttons
            toggleBuyButton();
        }
    }

    //Make a function that updates the description text, but makes it invisible for a
    //moment. Do this with animation in a later build

    /**
     * Sets the description text to the selected item's name and description.
     */
    private void setDescription() {
        if(indexSelected != -1) {
            String itemName = itemsForSale[indexSelected].getName();
            String itemDescription = itemsForSale[indexSelected].getDescription();
            description.setText(itemName + ":\n" + itemDescription);
        }
    }

    /**
     * Generates an item for sale.
     * @return The item for sale
     */
    private InvObject generateItemForSale() {
        Context context = getContext();
        assert context != null;
        InvObject[] itemPool = {                //Pool of items that could be in shop
                new Apple(context),
                new SlowPotion(context),
                new HealthPotion(context),
                new AttackPotion(context)
        };
        int indexToReturn = new Random().nextInt(itemPool.length);
        return itemPool[indexToReturn];
    }

    /**
     * Releases the sound manager when the fragment is detached.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        soundManager.release();
    }
}
