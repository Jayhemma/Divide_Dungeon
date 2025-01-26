package edu.commonwealthu.finalproject.Player;

import static edu.commonwealthu.finalproject.Player.Problem.type.*;

import android.content.Context;

import edu.commonwealthu.finalproject.GlobalModifiers;
import edu.commonwealthu.finalproject.MainActivity;

/**
 * The Player class. Holds the player's health, coins, weapons, and inventory.
 * @author Justin Aul
 */
public class Player {
    private int health;
    private int coins;
    private Weapon[] weaponList = new Weapon[3];
    private Inventory playerInventory;
    static Context context;

    /**
     * Constructor for Player.
     * @param _context The context of the activity
     */
    public Player(Context _context) {
        context = _context;
        health = GlobalModifiers.playerMaxHealth;
        coins = GlobalModifiers.startCoinsUpgrades * 10;
        weaponList[0] = new Weapon(ADD, 7);
        weaponList[1] = new Weapon(SUBTRACT, 15);
        weaponList[2] = new Weapon(ADD, 13);
        playerInventory = new Inventory();
    }

    /**
     * Getter for context.
     */
    public static Context getContext(){
        return context;
    }

    /**
     * Getter for health.
     */
    public int getHealth() {
        return health;
    }

    public int getCoins() {
        return coins;
    }

    /**
     * Getter for weaponList.
     */
    public Weapon[] getWeaponList() {
        return weaponList;
    }

    /**
     * Reduces health by 1.
     */
    public void takeDamage() {
        if (health > 0) {health--;}
    }

    /**
     * Adds health to the player.
     * @param _amount The amount of health to add
     */
    public void addHealth(int _amount) {
        health += _amount;
        if (health > GlobalModifiers.playerMaxHealth) {
            health = GlobalModifiers.playerMaxHealth;
        }
        ((MainActivity)context).updateToolBar();
    }

    /**
     * Getter for player inventory
     * @return The player's inventory
     */
    public Inventory getInventory() {
        return playerInventory;
    }

    /**
     * Returns true if the player's health is 0
     * @return True if the player's health is 0
     */
    public boolean isDead() {
        return health <= 0;
    }

    /**
     * Method to replace weapons for the player
     * @param index The index of the weapon to replace
     * @param weapon The weapon to add
     */
    public void replaceWeapon(int index, Weapon weapon) {
        weaponList[index] = weapon;
    }

    /**
     * Method to add coins to the player.
     * @param _amount The amount of coins to add
     */
    public void addCoins(int _amount) {
        coins += _amount;
    }

    /**
     * Method to remove coins from the player.
     * @param _amount The amount of coins to remove
     */
    public void removeCoins(int _amount) {
        coins -= _amount;
    }
}
