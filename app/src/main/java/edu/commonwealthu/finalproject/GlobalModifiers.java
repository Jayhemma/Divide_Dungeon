package edu.commonwealthu.finalproject;

import java.util.Arrays;

/**
 * Class that holds all the conditions of game mutators.
 */
public class GlobalModifiers {
    //Instance upgrades
    public static boolean tempSlow = false;         //Slows down the enemy attacks by 1.5
    public static boolean tempPower = false;        //Increases the player damage by 1.25

    //Permanent upgrades
    public static int upgradeToken = 0;           //Tokens used to buy upgrades
    public static int playerMaxHealth = 3;          //The player's max health
    public static int startCoinsUpgrades = 0;       //Coins the player starts with
    public static int numAttackUpgrades = 0;        //Number of attack upgrades the player has
    public static int numTimerUpgrades = 0;         //Number of timer upgrades the player has
    public static int numShopDiscounts = 0;         //Number of discount upgrades

    //Weapon effects if I have time to implement them

    //Stats? Maybe add more in a later build and a screen to view them
    public static int highestRoom = 0;

    /**
     * Sets all temporary modifiers to their default value
     */
    public static void refreshInstanceModifiers() {
        tempSlow = false;
        tempPower = false;
    }

    /**
     * Returns an array of all permanent modifiers
     * @return An array of all permanent modifiers
     */
    public static int[] getPermanentModifiers() {
        return new int[]{
            upgradeToken,
            playerMaxHealth,
            startCoinsUpgrades,
            numAttackUpgrades,
            numTimerUpgrades,
            numShopDiscounts
                //Add stats here if I choose to implement them
        };
    }

    /**
     * Returns the number of permanent modifiers
     */
    public static int getNumOfUpgrades() {
        return getPermanentModifiers().length;
    }

    /**
     * Restores all permanent modifiers to their default values
     * @param upgrades
     */
    public static void restorePermanentModifiers(int[] upgrades) {
        upgradeToken = upgrades[0];
        playerMaxHealth = upgrades[1];
        startCoinsUpgrades = upgrades[2];
        numAttackUpgrades = upgrades[3];
        numTimerUpgrades = upgrades[4];
        numShopDiscounts = upgrades[5];
        //Add stats here if I choose to implement them
    }

    /**
     * Resets all permanent modifiers to their default values
     */
    public static void clearData() {
        upgradeToken = 0;
        playerMaxHealth = 3;
        startCoinsUpgrades = 0;
        numAttackUpgrades = 0;
        numTimerUpgrades = 0;
        numShopDiscounts = 0;
    }

}
