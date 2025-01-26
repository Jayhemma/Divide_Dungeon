package edu.commonwealthu.finalproject.Usables;

import android.graphics.drawable.Drawable;

/**
 * Interface for all inventory objects.
 */
public interface InvObject {
    String itemName = "";
    String itemDescription = "";
    Drawable sprite = null;
    int buyPrice = 0;
    
    Drawable getSprite();
    String getName();
    String getDescription();
    int getBuyPrice();
    void use();
}
