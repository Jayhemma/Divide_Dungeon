package edu.commonwealthu.finalproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import edu.commonwealthu.finalproject.Fragments.BattleFragment
import edu.commonwealthu.finalproject.Fragments.InventoryFragment
import edu.commonwealthu.finalproject.Fragments.ShopFragment
import edu.commonwealthu.finalproject.Fragments.TitleFragment
import edu.commonwealthu.finalproject.Fragments.TransitionFragment
import edu.commonwealthu.finalproject.Fragments.TreasureFragment
import edu.commonwealthu.finalproject.Player.Inventory
import edu.commonwealthu.finalproject.Player.Player
import edu.commonwealthu.finalproject.Usables.*

/**
 * The main activity of the game.
 */
class MainActivity : AppCompatActivity() {

    //The player object will be global, and will hold all the player stats such as health,
    //coins, and inventory.
    var player : Player? = null

    var runningTokenReward = 0
    var roomNum : Int = 1                   //Difficulty will scale with room number
                                            //and is incremented by the TransitionFragment
    enum class RoomType {                   //RoomType determines the behavior of the
        FIGHT, SHOP, TREASURE, TITLE        //player's inventory
    }
    var type : RoomType = RoomType.FIGHT    //Initialize on fight, for first time Title
                                            //opening. See returnToTitle()

    private var PERMANENT_UPGRADES = "upgradesID"     //Keys used to save upgrades
    private var UPGRADE_LIST = "upgradeList"
    private var SETTINGS = "settingsID"               //Keys used to save settings
    private var SETTINGS_MIC = "settingsMic"
    private var SETTINGS_SOUND = "settingsSound"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        //Restore permanent upgrades and settings
        var preferences = getSharedPreferences(PERMANENT_UPGRADES, MODE_PRIVATE)
        if (preferences != null) {  //Upgrade data exists
            val upgrades = IntArray(GlobalModifiers.getNumOfUpgrades())
            for (i in upgrades.indices) {
                upgrades[i] = preferences.getInt(UPGRADE_LIST + i, 0)
            }
            GlobalModifiers.restorePermanentModifiers(upgrades)
        } else {
            GlobalModifiers.clearData()
        }
        preferences = getSharedPreferences(SETTINGS, MODE_PRIVATE)
        if (preferences != null) {
            //Mic is disabled
            if (!preferences.getBoolean(SETTINGS_MIC, true)) {GameOptions.toggleMic()}
            //If sound is disabled
            if (!preferences.getBoolean(SETTINGS_SOUND, true)) {GameOptions
                .toggleSound()}
        }

        //Initialize button to open settings
        val settingsButton: ImageButton = this.findViewById(R.id.menu_options)
        settingsButton.setOnClickListener {GameOptions(this)}


        //Start on the title screen
        returnToTitle()

        //Disable the functionality of the back button
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}   //Do nothing
        })
    }

    /**
     * Sets the room type.
     */
    private fun setRoomType(_type : RoomType) {
        type = _type
    }

    /**
     * Increments the room number.
     */
    fun incrementRoom() {
        roomNum++
    }

    /**
     * Returns to the title screen.
     */
    fun returnToTitle() {
        if(type != RoomType.TITLE){
            setRoomType(RoomType.TITLE)
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, TitleFragment())
                commit()
                }
        }

        //Create the toolbar that displays room number, coins, and health.
        val titleText :TextView = this.findViewById(R.id.display_name)
        titleText.text = getString(R.string.app_name)     //Set the title to game name
        val heartList : LinearLayout = this.findViewById(R.id.heart_list)
        heartList.visibility = View.GONE
        val roomsCoins : LinearLayout = this.findViewById(R.id.menu_room_coins)
        roomsCoins.visibility = View.GONE
    }

    /**
     * Adds the token reward to the running token reward.
     */
    fun addToTokenReward(amount : Int) {
        runningTokenReward += amount
    }

    /*
     * Opens the battle fragment. Called by the TitleFragment and TransitionFragment.
     */
    fun openBattleFragment() {
        setRoomType(RoomType.FIGHT)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, BattleFragment())
            commit()
        }
    }

    /**
     * Opens the treasure fragment.
     */
    fun openTreasureFragment() {
        setRoomType(RoomType.TREASURE)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, TreasureFragment())
            commit()
        }
    }

    /**
     * Opens the shop fragment.
     */
    fun openShopFragment() {
        setRoomType(RoomType.SHOP)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, ShopFragment())
            commit()
        }
    }

    /**
     * Opens the transition fragment.
     */
    fun openTransitionFragment() {
        setRoomType(RoomType.FIGHT)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, TransitionFragment())
            commit()
        }
    }

    /*
     * Closes the inventory fragment. Called by the inventory fragment. Returns to last
     * opened fragment.
     */
    fun closeInventory() {
        supportFragmentManager.popBackStack()
    }

    /*
     * Opens the inventory fragment. Called by the BattleFragment, TransitionFragment, and
     * ShopFragment.
     */
    fun openInventoryFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, InventoryFragment())
            addToBackStack(null)            //Allows return to last opened screen.
            commit()
        }
    }

    /*
     * Initializes a new game, called by the title fragment.
     */
     fun newGame() {
        player = Player(this)
        runningTokenReward = 0
        val _player : Player = player!!
        val inventory : Inventory = _player.inventory
        inventory.addItem(Apple(this))
        GlobalModifiers.refreshInstanceModifiers()

        //Reset room number to 1
        roomNum = 1
        //Initialize the toolbar for gameplay
        newGameToolbar()
        //Add fade animation here
        openBattleFragment()
    }

    /*
     * Initializes the toolbar for gameplay. Used by the newGame() method.
     */
    private fun newGameToolbar() {
        val titleText :TextView = this.findViewById(R.id.display_name)
        titleText.text = ""    //Erase name display
        val heartList : LinearLayout = this.findViewById(R.id.heart_list)
        heartList.visibility = View.VISIBLE
        val roomsCoins : LinearLayout = this.findViewById(R.id.menu_room_coins)
        roomsCoins.visibility = View.VISIBLE

        updateToolBar()
    }

    /**
     * Updates the toolbar with the current room number, coins, and health.
     */
    @SuppressLint("SetTextI18n")
    fun updateToolBar() {
        val _player : Player = player!!
        val playerCoins = _player.coins
        val roomText : TextView = this.findViewById(R.id.menu_display_room)
        roomText.text = getString(R.string.room) + " " +roomNum
        val coinsText : TextView = this.findViewById(R.id.menu_display_coins)
        coinsText.text = getString(R.string.coins) + " " +playerCoins

        val heartList : LinearLayout = this.findViewById(R.id.heart_list)
        heartList.removeAllViews()              //Delete hearts before creating more.
        val factor = this.resources.displayMetrics.density //Get density for params
        val params : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,(factor*32).toInt())
        params.setMargins(5,0,0,0)
        var currentHealth : Int = _player.health
        for (i in 1 .. GlobalModifiers.playerMaxHealth) {
            val heartPicture = ImageView(this)
            heartPicture.layoutParams = params
            if (currentHealth > 0) {
                heartPicture.setImageResource(R.drawable.heart)
                currentHealth--
            } else {
                heartPicture.setImageResource(R.drawable.heart_empty)
            }
            heartList.addView(heartPicture)
        }
    }

    /**
     * When app is vulnerable to being destroyed, save the data
     */
    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveData()
    }

    /**
     * Save the game
     */
    fun saveData() {
        val permanentValues = GlobalModifiers.getPermanentModifiers()
        var preferences = getSharedPreferences(PERMANENT_UPGRADES, MODE_PRIVATE)
        var editor = preferences.edit()
        for(i in permanentValues.indices) {
            editor.putInt(UPGRADE_LIST+i, permanentValues[i])
        }
        editor.apply()

        preferences = getSharedPreferences(SETTINGS, MODE_PRIVATE)
        editor = preferences.edit()
        editor.putBoolean(SETTINGS_MIC, GameOptions.isMicActivated())
        editor.putBoolean(SETTINGS_SOUND, GameOptions.isSoundEnabled())
        editor.apply()
    }

    /**
     * Clears save data
     */
    fun clearSaveData() {
        val preferences = getSharedPreferences(PERMANENT_UPGRADES, MODE_PRIVATE)
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
        GlobalModifiers.clearData()
        returnToTitle()
    }
}