package edu.commonwealthu.finalproject.Fragments;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;
import static edu.commonwealthu.finalproject.Player.Problem.type.*;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import edu.commonwealthu.finalproject.MainActivity;
import edu.commonwealthu.finalproject.Player.Problem;
import edu.commonwealthu.finalproject.Player.Weapon;
import edu.commonwealthu.finalproject.R;
import edu.commonwealthu.finalproject.SoundManager;
import edu.commonwealthu.finalproject.WeaponDialog;

/**
 * The treasure fragment of the game. Generates a new weapon for the player to replace
 */
public class TreasureFragment extends Fragment {
    Weapon newWep;                  //The new generated weapon
    SoundManager soundManager;      //For playing sounds effects

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_treasure, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    /**
     * Initializes the fragment once it is inflated
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        //Set up background
        ImageView background = getActivity().findViewById(R.id.treasure_backgroundImage);
        InputStream stream;
        try {
            stream = requireContext().getAssets().open(
                    "room_bgs/bg_fight.png");
            background.setImageDrawable(Drawable.createFromStream(stream, null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Set up sound manager
        soundManager = new SoundManager(getActivity());

        ImageView weaponView = view.findViewById(R.id.new_weapon);
        ImageButton replaceButton = view.findViewById(R.id.wep_replace_button);
        TextView replaceText = view.findViewById(R.id.wep_replace_button_text);
        TextView weaponDesc = view.findViewById(R.id.weapon_description);
        newWep = generateWeapon();          //Generate new weapon

        //Set up the treasure chest button
        ImageButton treasureChest = view.findViewById(R.id.treasure_button);
        treasureChest.setOnClickListener(Event -> {
            soundManager.playClickSound();
            //Make the treasure visible
            weaponView.setVisibility(View.VISIBLE);
            replaceButton.setVisibility(View.VISIBLE);
            replaceText.setVisibility(View.VISIBLE);

            //Set up the weapon description
            weaponDesc.setText(newWep.getDescription());

            //Change treasure sprite
            treasureChest.setImageDrawable(getDrawable(getActivity(),
                    R.drawable.chest_opened));
        });

        //Set up ImageView of the new weapon
        weaponView.setImageDrawable(newWep.getSprite());

        //Set up replace button, which inflates weapon dialog;
        replaceButton.setOnClickListener(Event ->{
            soundManager.playClickSound();
            WeaponDialog dialog = new WeaponDialog(getActivity(),
                    ((MainActivity)getActivity()).getPlayer(), MainActivity.RoomType.TREASURE);
            dialog.passWeapon(newWep);
            dialog.showWeapons();
        });

        //Set up exit button
        ImageButton exitButton = view.findViewById(R.id.treasure_exit_button);
        exitButton.setOnClickListener(Event -> {
            soundManager.playClickSound();
            ((MainActivity)getActivity()).openTransitionFragment();
        });

    }

    /**
     * Generates a new weapon for the player to replace
     * @return The new weapon
     */
    private Weapon generateWeapon() {
        int difficulty = ((MainActivity)getActivity()).getRoomNum(); //Base damage
        double upperBound= 10 + ((.5) * difficulty) + ((0.03) * difficulty * difficulty);
        //Damage will be generated between x and (10 + 0.5x + 0.03x^3)
        difficulty = ThreadLocalRandom.current().nextInt(difficulty, (int)upperBound);
        difficulty+=2; //Just to ensure that the problem generation can generate factors

        //Depending on the weapon type, increase the problem difficulty / max damage
        //Reminder that for weapons, the max damage directly correlates to difficulty
        int problemTypeIndex = ThreadLocalRandom.current().nextInt(4);
        Problem.type type;
        switch(problemTypeIndex){
            case 0:
                type = SUBTRACT;
                break;
            case 1:
                type = MULTIPLY;
                difficulty = (int)(difficulty * 1.5);
                break;
            case 2:
                type = DIVIDE;
                difficulty = (int)(difficulty * 1.5);
                break;
            default:
                type = ADD;
                break;
        }
        return new Weapon(type, difficulty);
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
