package edu.commonwealthu.finalproject.Usables;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;

import edu.commonwealthu.finalproject.MainActivity;
import edu.commonwealthu.finalproject.Player.Player;
import edu.commonwealthu.finalproject.R;

public class Apple implements InvObject {
    private final String itemName;
    private final String itemDescription;
    private  Drawable sprite;
    private final int buyPrice = 20;
    private Context context;

    @SuppressLint("UseCompatLoadingForDrawables")
    public Apple(Context _context) {
        context = _context;
        itemName = context.getString(R.string.apple);
        itemDescription = context.getString(R.string.apple_desc);
        try {
            InputStream stream = context.getAssets().open("usables/usable_apple.png");
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
     * Will heal the player by 1 point.
     */
    @Override
    public void use() {
        Player player = ((MainActivity)context).getPlayer();
        assert player != null;
        player.addHealth(1);
    }
}
