package edu.commonwealthu.finalproject.Player;

import edu.commonwealthu.finalproject.Usables.InvObject;

/**
 * The inventory of the player.
 * @author Justin Aul
 */
public class Inventory {
    InvObject[] playerInventory;

    public Inventory() {
        playerInventory = new InvObject[6];
    }

    /**
     * Adds an item to the inventory. If the inventory is full, returns false.
     * @param item The item to add.
     * @return True if the item was added, false otherwise.
     */
    public boolean addItem(InvObject item) {
        for (int i = 0; i < playerInventory.length; i++) {
            if (playerInventory[i] == null) {
                playerInventory[i] = item;
                return true;
            }
        }
        return false;
    }

    /**
     * Removes an item from the inventory. If the index is out of bounds, returns false.
     * @param index The index to remove from
     * @return True or false depending on whether the item was removed.
     */
    public boolean removeItem(int index) {
        if (index < 0 || index >= playerInventory.length) {
            return false;
        }
        playerInventory[index] = null;
        return true;
    }

    /**
     * Checks if the inventory is full.
     * @return True if the inventory is full, false otherwise.
     */
    public boolean isFull() {
        for (InvObject invObject : playerInventory) {
            if (invObject == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Replaces an item in the inventory.
     * @param index The index to replace
     * @param item The item to replace with
     */
    public void replaceItem(int index, InvObject item) {
        if (index < 0 || index >= playerInventory.length) {
            return;
        }
        playerInventory[index] = item;
    }

    /**
     * Gets an item from the inventory.
     * @param index The index to fetch from
     * @return The item at that index, or null if the index is out of bounds
     */
    public InvObject getItem(int index) {
        if (index < 0 || index >= playerInventory.length) {
            return null;
        }
        return playerInventory[index];
    }

    /**
     * Uses an item in the inventory.
     * @param index The index to use
     */
    public void useItem(int index) {
        if (index < 0 || index >= playerInventory.length) {
            return;
        }
        playerInventory[index].use();
        removeItem(index);
    }

    /**
     * Sells an item in the inventory and returns the price of that item
     * @param index The index of the item to sell
     * @return The price of the item
     */
    public int sellItem(int index) {
        if (index < 0 || index >= playerInventory.length) {
            return 0;
        }
        int price = playerInventory[index].getBuyPrice();
        removeItem(index);
        return price;
    }
}
