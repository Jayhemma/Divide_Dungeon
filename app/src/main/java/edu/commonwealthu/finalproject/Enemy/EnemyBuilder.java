package edu.commonwealthu.finalproject.Enemy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import edu.commonwealthu.finalproject.GlobalModifiers;
import edu.commonwealthu.finalproject.R;

/**
 * Class that is utilized by the enemy class to generate enemy's stats and visual display.
 * @author Justin Aul
 */
public class EnemyBuilder {
    private final Context context;
    private final int difficulty;           //Modifies the enemy's stats
    private final int bodyType;             //Determines which sprite banks to use

    /**
     * Constructor for the enemy builder.
     * @param _context The context of the activity
     * @param _difficulty The difficulty of the room
     */
    public EnemyBuilder(Context _context, int _difficulty) {
        difficulty = _difficulty;
        context = _context;
        bodyType = ThreadLocalRandom.current().nextInt(2);
    }

    /**
     * Generates the enemy's reward.
     * @return The enemy's reward
     */
    public int genReward() {
        return ThreadLocalRandom.current().nextInt(difficulty, 3 * difficulty);
    }

    /**
     * Returns the Drawable of the resource representing the enemy's lower sprite.
     * There is a different set of resources for each body type, decided by the
     * bodyType variable.
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public Drawable genLowerSprite() {
        ArrayList<Drawable> spritePath = new ArrayList<>();
        //Adds all available skin tones from bodyType selected
        try {
            InputStream stream;
            switch (bodyType) {
                case 0:
                    stream = context.getAssets().open("enemy/enemy_lower_0_var_0.png");
                    spritePath.add(Drawable.createFromStream(stream, null));
                    stream = context.getAssets().open("enemy/enemy_lower_0_var_1.png");
                    spritePath.add(Drawable.createFromStream(stream, null));
                    stream = context.getAssets().open("enemy/enemy_lower_0_var_2.png");
                    spritePath.add(Drawable.createFromStream(stream, null));
                    break;
                case 1:
                    stream = context.getAssets().open("enemy/enemy_lower_1_var_0.png");
                    spritePath.add(Drawable.createFromStream(stream, null));
                    stream = context.getAssets().open("enemy/enemy_lower_1_var_1.png");
                    spritePath.add(Drawable.createFromStream(stream, null));
                    stream = context.getAssets().open("enemy/enemy_lower_1_var_2.png");
                    spritePath.add(Drawable.createFromStream(stream, null));
                    break;
            }
            return spritePath.get(ThreadLocalRandom.current().nextInt(spritePath.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //If no drawables are found
        return context.getResources().getDrawable(R.drawable.debug);
    }

    /**
     * Returns the Drawable of the resource representing the enemy's upper sprite.
     * There is a different set of resources for each body type, decided by the
     * bodyType variable.
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public Drawable genUpperSprite() {
        ArrayList<Drawable> spritePath = new ArrayList<>();
        //I'm opting to use a switch statement here to leave room for adding more types
        try {
            InputStream stream;
            switch (bodyType) {
                case 0:
                    stream = context.getAssets().open("enemy/enemy_upper_0_var_0.png");
                    spritePath.add(Drawable.createFromStream(stream, null));
                    stream = context.getAssets().open("enemy/enemy_upper_0_var_1.png");
                    spritePath.add(Drawable.createFromStream(stream, null));
                    stream = context.getAssets().open("enemy/enemy_upper_0_var_2.png");
                    spritePath.add(Drawable.createFromStream(stream, null));
                    break;
                case 1:
                    stream = context.getAssets().open("enemy/enemy_upper_1_var_0.png");
                    spritePath.add(Drawable.createFromStream(stream, null));
                    stream = context.getAssets().open("enemy/enemy_upper_1_var_1.png");
                    spritePath.add(Drawable.createFromStream(stream, null));
                    break;
            }
            return spritePath.get(ThreadLocalRandom.current().nextInt(spritePath.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //If no drawables are found
        return context.getResources().getDrawable(R.drawable.debug);
    }

    /**
     * Generates the enemy's hat.
     * @return A drawable of the enemy's hat
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public Drawable genHat() {
        ArrayList<Drawable> spritePath = new ArrayList<>();
        try {
            InputStream stream;
            stream = context.getAssets().open("enemy/hat0.png");
            spritePath.add(Drawable.createFromStream(stream, null));
            stream = context.getAssets().open("enemy/hat1.png");
            spritePath.add(Drawable.createFromStream(stream, null));
            stream = context.getAssets().open("enemy/hat2.png");
            spritePath.add(Drawable.createFromStream(stream, null));
            stream = context.getAssets().open("enemy/hat3.png");
            spritePath.add(Drawable.createFromStream(stream, null));
            return spritePath.get(ThreadLocalRandom.current().nextInt(spritePath.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //If no drawables are found
        return context.getResources().getDrawable(R.drawable.debug);
    }

    /**
     * Generates the enemy's attack speed.
     * @return The attack speed in seconds
     */
    public int genSpeed() {
        int retInt = 30; //Initialize at 30 seconds
        retInt += (GlobalModifiers.numTimerUpgrades * 5); //Add permanent timer upgrades

        //Every 2 floors, the enemy may be up to 1 second faster
        //Linear difficulty increase (29 - x/2)
        int decreaseSpeed = 1 + difficulty/2;
        //Note that I'm keeping this as an int, because I want automatic rounding.

        //The decrease speed is the maximum amount that may be decreased from cooldown
        //Lowest speed amt is 5 seconds
        return Math.max(5, (retInt - ThreadLocalRandom.current().nextInt(decreaseSpeed)));
    }

    /**
     * Generates the enemy's health.
     * @return The enemy's health
     */
    public int genHealth() {
        //Exponentially increasing health Max: 20+4.5x+(0.1x^2)
        double scaler = 2.5 * difficulty + (0.1 * difficulty * difficulty);
        return 20 + 2 * difficulty + ThreadLocalRandom.current().nextInt((int)scaler);
    }

    /**
     * Generates the enemy's name, using a txt document to generate the names.
     * @return The enemy's name
     */
    public String genName() {
        Resources res = context.getResources();
        InputStream inputStream = res.openRawResource(R.raw.enemy_names);
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream))) {

            //Fetch a name type for each type of name
            String title = getTitle(reader);
            String firstName = getFirstName(reader);
            String lastName = getLastName(reader);
            String occupation = getOccupation(reader);

            //Build the name randomly, each component has a 1/2 chance to appear.
            String retString = "";
            if (ThreadLocalRandom.current().nextBoolean()) {retString+=title + " ";}
            retString+=firstName;
            if (ThreadLocalRandom.current().nextBoolean()) {retString+=" "+lastName;}
            if (ThreadLocalRandom.current().nextBoolean()) {retString+=" the "+ occupation;}
            return retString;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper method for generating the enemy title
     */
    private String getTitle (BufferedReader reader) throws IOException {
        String line;
        ArrayList<String> titles = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            //End of the list reached
            if (line.equals("FIRST_NAMES")) {
                break;
            }
            //Ignore header and line breaks.
            if ((!line.equals("TITLES")) && (!line.isEmpty())) {
                titles.add(line);
            }
        }
        //Get a random index
        int nameIndex = ThreadLocalRandom.current().nextInt(titles.size());
        return titles.get(nameIndex);
    }

    /**
     * Helper method for generating the enemy first name
     */
    private String getFirstName(BufferedReader reader) throws IOException {
        String line;
        ArrayList<String> firstNames = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            //End of the firstname list reached
            if (line.equals("LAST_NAMES")) {
                break;
            }
            //Ignore header and line breaks.
            if ((!line.equals("FIRST_NAMES")) && (!line.isEmpty())) {
                firstNames.add(line);
            }
        }
        //Get a random first name index
        int nameIndex = ThreadLocalRandom.current().nextInt(firstNames.size());
        return firstNames.get(nameIndex);
    }

    /**
     * Helper method for generating the enemy last name
     */
    private String getLastName(BufferedReader reader) throws IOException {
        String line;
        ArrayList<String> lastNames = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            //End of the lastname list reached
            if (line.equals("OCCUPATION")) {
                break;
            }
            //Ignore lastname header and line breaks.
            if ((!line.equals("LAST_NAMES")) && (!line.isEmpty())) {
                lastNames.add(line);
            }
        }
        //Get a random name index
        int nameIndex = ThreadLocalRandom.current().nextInt(lastNames.size());
        return lastNames.get(nameIndex);
    }

    /**
     * Helper method for generating the enemy occupation
     */
    private String getOccupation(BufferedReader reader) throws IOException {
        String line;
        ArrayList<String> occupations = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            //Ignore lastname header and line breaks.
            if ((!line.equals("OCCUPATION")) && (!line.isEmpty())) {
                occupations.add(line);
            }
        }
        //Get a random name index
        int nameIndex = ThreadLocalRandom.current().nextInt(occupations.size());
        return occupations.get(nameIndex);
    }
}
