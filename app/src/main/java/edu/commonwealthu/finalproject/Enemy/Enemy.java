package edu.commonwealthu.finalproject.Enemy;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Represents an enemy in the game.
 * @author Justin Aul
 */
public class Enemy implements Serializable {
    private final String name;
    private final int maxHealth;
    private int health;
    private final int attackInterval;
    private final int reward;
    private final Drawable bottomLayer;
    private final Drawable topLayer;
    private final Drawable hat;

    /**
     * Constructor that initializes all the enemy's stats
     * @param context The context of the activity
     * @param difficulty The difficulty of the room
     */
    public Enemy(Context context, int difficulty) {
        EnemyBuilder builder = new EnemyBuilder(context, difficulty);

        //Generate enemy stats
        name = builder.genName();
        health = builder.genHealth();
        maxHealth = health;
        attackInterval = builder.genSpeed();
        reward = builder.genReward();

        //Generate enemy sprite paths
        bottomLayer = builder.genLowerSprite();
        topLayer = builder.genUpperSprite();
        hat = builder.genHat();
    }

    /**
     * Gets the reward for defeating the enemy
     */
    public int getReward() {
        return reward;
    }

    /**
     * Gets the enemy's sprites
     */
    public Drawable[] getSprites() {
        return new Drawable[]{bottomLayer, topLayer, hat};
    }

    /**
     * Gets the enemy's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the enemy's health
     */
    public int getHealth() {
        return health;
    }

    /**
     * Gets the enemy's max health
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Checks if the enemy is dead
     */
    public boolean isDead() {
        return (health==0);
    }

    /**
     * Gets the enemy's attack interval
     */
    public int attackInterval() {
        return attackInterval;
    }

    /**
     * Takes damage from the enemy
     */
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {health = 0;} //Ensure health doesn't go beneath 0.
    }
}