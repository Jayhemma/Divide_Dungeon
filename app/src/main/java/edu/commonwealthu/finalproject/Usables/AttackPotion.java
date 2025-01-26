package edu.commonwealthu.finalproject.Usables;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;

import edu.commonwealthu.finalproject.GlobalModifiers;
import edu.commonwealthu.finalproject.R;

public class AttackPotion implements InvObject{
    private final String itemName;
    private final String itemDescription;
    private Drawable sprite;
    private final int buyPrice = 100;

    @SuppressLint("UseCompatLoadingForDrawables")
    public AttackPotion(Context context) {
        itemName = context.getString(R.string.attack_potion);
        itemDescription = context.getString(R.string.attack_potion_desc);
        try {
            InputStream stream = context.getAssets().open("usables/usable_potion_attack" +
                    ".png");
            sprite = Drawable.createFromStream(stream, null);
        } catch (IOException e) {
            //If no drawables are found
            sprite = context.getResources().getDrawable(R.drawable.debug);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Drawable getSprite() {
        return sprite;
    }
    @Override
    public String getName() {
        return itemName;
    }
    @Override
    public String getDescription() {
        return itemDescription;
    }
    @Override
    public int getBuyPrice() {
        return buyPrice;
    }

    /**
     * Will inflict 50% more damage to the current enemy.
     */
    @Override
    public void use() {
        GlobalModifiers.tempPower = true;
    }
}

