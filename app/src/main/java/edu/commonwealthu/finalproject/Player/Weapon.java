package edu.commonwealthu.finalproject.Player;


import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import edu.commonwealthu.finalproject.R;

/**
 * Class that represents a weapon.
 * @author Justin Aul
 */
public class Weapon {
    int maxDamage;
    Problem.type problemType;
    Problem problem;
    Drawable sprite;
    String description;
    //Make an effect class? If I have time to implement weapon effects

    /**
     * Explicit constructor
     * @param _problemType Type of problem to generate
     * @param _maxDamage Maximum damage the weapon can deal
     */
    public Weapon(Problem.type _problemType, int _maxDamage) {
        problemType = _problemType;
        maxDamage = _maxDamage;
        sprite = genSprite();
        description = genDescription();
    }

    /**
     * Returns the description of the weapon
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Generates the description of the weapon
     * @return The description
     */
    private String genDescription() {
        Context context = Player.getContext();
        String retString = context.getString(R.string.math_type);
        switch (problemType) {
            case ADD:
                retString = retString + context.getString(R.string.addition);
                break;
            case SUBTRACT:
                retString += context.getString(R.string.subtraction);
                break;
            case MULTIPLY:
                retString += context.getString(R.string.multiplication);
                break;
            case DIVIDE:
                retString = retString + context.getString(R.string.division);
                break;
        }
        retString = retString + (context.getString(R.string.maximum_damage) + maxDamage);
        return retString;
    }

    /**
     * Returns the sprite of the weapon
     * @return The sprite
     */
    public Drawable getSprite() {
        return sprite;
    }

    /**
     * Generates the sprite of the weapon
     * @return The sprite
     */
    private Drawable genSprite() {
        ArrayList<Drawable> spritePath = new ArrayList<>();

        //Paths of all potential weapons
        InputStream stream;
        Context context = Player.getContext();
        try {
            stream = context.getAssets().open("weapons/player_weapon_0.png");
            spritePath.add(Drawable.createFromStream(stream, null));
            stream = context.getAssets().open("weapons/player_weapon_1.png");
            spritePath.add(Drawable.createFromStream(stream, null));
            stream = context.getAssets().open("weapons/player_weapon_2.png");
            spritePath.add(Drawable.createFromStream(stream, null));
            stream = context.getAssets().open("weapons/player_weapon_3.png");
            spritePath.add(Drawable.createFromStream(stream, null));
            stream = context.getAssets().open("weapons/player_weapon_4.png");
            spritePath.add(Drawable.createFromStream(stream, null));
            stream = context.getAssets().open("weapons/player_weapon_5.png");
            spritePath.add(Drawable.createFromStream(stream, null));
            return spritePath.get(ThreadLocalRandom.current().nextInt(spritePath.size()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a new problem for the weapon
     */
    public void genProblem() {
        problem = new Problem(problemType, maxDamage);
    }


    /**
     * For use in display on ui buttons. Generates a new problem and returns a string
     * representation
     * @return A string representation of the problem
     */
    public String getProblem() {
        if (problem == null) {genProblem();}
        return problem.getProblem();
    }

    /**
     * Checks if the given answer is correct
     * @param _answer The answer to check
     * @return True if the answer is correct, false otherwise
     */
    public boolean isSolution(int _answer) {
        return _answer == problem.getAnswer();
    }

}
