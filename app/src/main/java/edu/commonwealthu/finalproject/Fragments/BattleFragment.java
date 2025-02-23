package edu.commonwealthu.finalproject.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.commonwealthu.finalproject.Enemy.Enemy;
import edu.commonwealthu.finalproject.GameOptions;
import edu.commonwealthu.finalproject.GlobalModifiers;
import edu.commonwealthu.finalproject.MainActivity;
import edu.commonwealthu.finalproject.Player.Player;
import edu.commonwealthu.finalproject.R;
import edu.commonwealthu.finalproject.SoundManager;

/**
 * Fragment that plays the battle system of the game. The main gameplay loop.
 * @author Justin Aul
 */
public class BattleFragment extends Fragment {

    Activity activity;
    Player player;
    Enemy enemy;
    TextView timer;
    int attackTimer;                //The time between enemy attacks
    Handler timerHandler;
    int displayTimer = -5;          //For use in displaying the timer
    int difficulty;                 //The same as the room number
    ProgressBar healthBar;
    ImageView[] weaponSprites;
    TextView[] weaponTexts;

    boolean slowedCondition = false;//Extracted from the GlobalModifiers class
                                    //In honesty I don't remember why I made this separate
                                    //I think it was done prior to making
                                    //GlobalModifiers a static class.

    SoundManager soundManager;      //For playing sounds effects

    //For getting numeral inputs from the mic
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private SpeechRecognizer speechRecognizer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_game, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    /**
     * Initializes the Battle Fragment after the view is inflated.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        activity = getActivity();
        difficulty = ((MainActivity) requireContext()).getRoomNum();
        player = ((MainActivity) requireContext()).getPlayer();

        //Set up sound manager
        soundManager = new SoundManager(activity);

        //Set up background
        ImageView background = activity.findViewById(R.id.fight_backgroundImage);
        InputStream stream;
        try {
            stream = requireContext().getAssets().open(
                    "room_bgs/bg_fight.png");
            background.setImageDrawable(Drawable.createFromStream(stream, null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Only create an enemy if one doesn't exist
        if (enemy == null) {
            enemy = new Enemy(activity, difficulty);
        }

        initAttackTimer();                      //Begin attack timer
        initDisplayTimer();                     //Display attack timer
        initEnemyHealthName();                  //Display name and health bar
        initEnemyDisplay();                     //Display enemy
        initAnswerBox();                        //Initialize answer box
        initSetQuestions();                     //Generate questions

        //Set up open inventory button
        ImageButton openInventory = activity.findViewById(R.id.open_inventory);
        openInventory.setOnClickListener(event -> {
            soundManager.playClickSound();
            ((MainActivity) activity).openInventoryFragment();
                });


        //Check if mic is enabled
        System.out.println(GameOptions.isMicActivated());
        if (GameOptions.isMicActivated()) {
            //Check and request audio permissions first
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{
                        Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            } else {
                initSpeechRecognizer();
            }
        }
    }

    /**
     * Initializes speech recognition.
     */
    private void initSpeechRecognizer() {
        //Set up speech recognition
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            //These are unused
            @Override
            public void onBeginningOfSpeech() {}
            @Override
            public void onRmsChanged(float num) {}
            @Override
            public void onBufferReceived(byte[] buffer) {}
            @Override
            public void onPartialResults(Bundle bundle) {}
            @Override
            public void onEvent(int i, Bundle bundle) {}
            @Override

            public void onError(int i) {
                //Begin listening again
                beginListening();
            }

            @Override
            public void onEndOfSpeech() {
                //Begin listening again
                beginListening();
            }

            @Override
            public void onReadyForSpeech(Bundle params) {
                //Display icon when mic is activated
                ImageView micIcon = activity.findViewById(R.id.mic_icon);
                micIcon.setVisibility(View.VISIBLE);
            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches =
                        bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String spokenText = matches.get(0);
                    String numerals = convertToNum(spokenText);
                    EditText answerBox = activity.findViewById(R.id.answer_input);
                    answerBox.setText(numerals);
                }
            }
        });
        //Begin speech recognition
        beginListening();
    }

    /**
     * Starts listening for speech.
     */
    private void beginListening() {
        if (!GameOptions.isMicActivated()) {//If mic is disabled
            ImageView micIcon = activity.findViewById(R.id.mic_icon);
            micIcon.setVisibility(View.GONE);
            return;
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        speechRecognizer.startListening(intent);
    }

    /**
     * Converts text to numbers
     * @param text The text to convert
     * @return The converted text
     */
    private String convertToNum(String text) {
        //Remove commas from the text
        text = text.replaceAll(",", "");

        //Pair word representations of numbers to their numeral equivalents
        //And any common misheard numbers
        Map<String, String> numberWords = new HashMap<>();
        numberWords.put("zero", "0");
        numberWords.put("one", "1");
        numberWords.put("two", "2");
        numberWords.put("too", "2");
        numberWords.put("three", "3");
        numberWords.put("four", "4");
        numberWords.put("thor", "4");
        numberWords.put("or", "4");
        numberWords.put("five", "5");
        numberWords.put("six", "6");
        numberWords.put("sex", "6");
        numberWords.put("seven", "7");
        numberWords.put("eight", "8");
        numberWords.put("nine", "9");

        //Build the result string
        StringBuilder numerals = new StringBuilder();
        String[] words = text.toLowerCase().split("\\s+"); //Split the text into words

        for (String word : words) {
            if (numberWords.containsKey(word)) {
                //Convert word representations
                numerals.append(numberWords.get(word)).append("");
            } else if (word.matches("\\d+")) {
                //Append numeric digits directly
                numerals.append(word).append("");
            }
        }
        return numerals.toString().trim();
    }

    /**
     * Initializes the attack timer. For game functionality.
     */
    private void initAttackTimer() {
        if (displayTimer == -5) {
            displayTimer = enemy.attackInterval();
            displayTimer += (GlobalModifiers.numTimerUpgrades * 5); //Permanent timer upgrades
        }
        attackTimer = displayTimer * 1000;
        timerHandler = new Handler();
        timerHandler.postDelayed(attackRunnable, attackTimer);
    }

    /**
     * Initializes the display timer. For visual purposes
     */
    private void initDisplayTimer() {
        timer = activity.findViewById(R.id.attack_timer);
        timer.setText(String.valueOf(displayTimer));
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    /**
     * Initializes the enemy name and health bar.
     */
    private void initEnemyHealthName() {
        //Display name and health bar
        TextView name = activity.findViewById(R.id.enemy_name);
        name.setText(enemy.getName());
        healthBar = activity.findViewById(R.id.enemy_health);
        healthBar.setMax(enemy.getMaxHealth());
        healthBar.setProgress(enemy.getHealth());
    }

    /**
     * Initializes the enemy display.
     */
    private void initEnemyDisplay() {
        ImageView enemyBodyBottom = activity.findViewById(R.id.display_enemy_bottom);
        ImageView enemyBodyTop = activity.findViewById(R.id.display_enemy_top);
        ImageView enemyHat = activity.findViewById(R.id.display_enemy_hat);
        Drawable[] enemyStack = enemy.getSprites();
        enemyBodyBottom.setImageDrawable(enemyStack[0]);
        enemyBodyTop.setImageDrawable(enemyStack[1]);
        enemyHat.setImageDrawable(enemyStack[2]);
    }

    /**
     * Initializes the answer box.
     */
    private void initAnswerBox() {
        //Answer Box
        EditText answerBox = activity.findViewById(R.id.answer_input);
        TextWatcher textWatcher = new TextWatcher() {
            //Unused
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            //Calls the text changed test method
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new Handler(Looper.getMainLooper()).postDelayed(() ->
                        answerCheck(answerBox), 500);
                }
            //Unused
            @Override
            public void afterTextChanged(Editable s) {
                // this function is called after text is edited
            }
        };
        answerBox.addTextChangedListener(textWatcher);
    }

    /**
     * An initialization method for setting the weapons. Generates the questions and
     * sets the sprites.
     */
    private void initSetQuestions() {
        weaponSprites = new ImageView[3];
        weaponSprites[0] = activity.findViewById(R.id.weapon_0_sprite);
        weaponSprites[1] = activity.findViewById(R.id.weapon_1_sprite);
        weaponSprites[2] = activity.findViewById(R.id.weapon_2_sprite);
        weaponTexts = new TextView[3];
        weaponTexts[0] = activity.findViewById(R.id.weapon_0_text);
        weaponTexts[1] = activity.findViewById(R.id.weapon_1_text);
        weaponTexts[2] = activity.findViewById(R.id.weapon_2_text);
        for(int i = 0; i < 3; i++) {
            player.getWeaponList()[i].getProblem();
            weaponSprites[i].setImageDrawable(player.getWeaponList()[i].getSprite());
            weaponTexts[i].setText(player.getWeaponList()[i].getProblem());
        }
    }

    /**
     * Runnable for the attack timer for gameplay purposes
     */
    private final Runnable attackRunnable = new Runnable() {
        @Override
        public void run() {
            int _attackTimer = attackTimer;
            if (GlobalModifiers.tempSlow) {_attackTimer *= 1.5;}
            player.takeDamage();
            ((MainActivity)activity).updateToolBar();
            timerHandler.postDelayed(this, _attackTimer);
            ////Check if player is dead, which handles cancelling timers
            if(player.isDead()) {gameOver();}
        }
    };

    /**
     * Runnable for the timer display. Modifiers will be applied on this timer since it
     * runs every second.
     */
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            //If a slow condition is added, restart the timers.
            if((!slowedCondition)&&(GlobalModifiers.tempSlow)) {
                slowedCondition = true;
                timerHandler.removeCallbacks(attackRunnable);
                timerHandler.removeCallbacks(timerRunnable);
                displayTimer=0;                             //Refreshes the timer below
                timerHandler.postDelayed(attackRunnable, (int)(attackTimer * 1.5));
                System.out.println("Enemy slowed!");
            }

            displayTimer--;
            if (displayTimer <= 0) {
                displayTimer = enemy.attackInterval();
                displayTimer += (GlobalModifiers.numTimerUpgrades * 5);
                if (GlobalModifiers.tempSlow) {displayTimer *= 1.5;}
            }
            timer = activity.findViewById(R.id.attack_timer);
            timer.setText(String.valueOf(displayTimer));
            timerHandler.postDelayed(this, 1000);
        }
    };

    /**
     * Checks if the answer input is correct, and damages enemy if so.
     * @param _answerBox The answer box to check
     */
    private void answerCheck(EditText _answerBox) {
        int damage = 0; //If an answer is found, pass to a takeDamage method
        if (_answerBox.getText().toString().isEmpty()) {return;} //When box is empty, cancel
        int userAnswer = Integer.parseInt(_answerBox.getText().toString());
        for(int i = 0; i < player.getWeaponList().length; i++) {
            if(player.getWeaponList()[i].isSolution(userAnswer)){
                damage += userAnswer;
                player.getWeaponList()[i].genProblem();
                weaponTexts[i].setText(player.getWeaponList()[i].getProblem());
            }
        }
        if (damage > 0) {
            //Make a timed handler do this, like 1/4 a second and play animation
            //Also, make text red if damage is less than 0, green if greater than 0?
            solutionFound(_answerBox);
            enemyTakeDamage(damage);
        }
    }

    /**
     * Clears the answer box and hides the keyboard
     * @param _answerBox The answer box to clear
     */
    private void solutionFound(EditText _answerBox) {
        _answerBox.setText("");
        InputMethodManager inputManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(Objects.requireNonNull(
                            activity.getCurrentFocus()).getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            _answerBox.clearFocus();
        }
    }

    /**
     * Deal damage to the enemy and updates the health bar
     * @param _damage The amount of damage to deal
     */
    private void enemyTakeDamage(int _damage) {
        //Play the damage sound
        soundManager.playAttackSound();

        int damage = _damage;
        if (GlobalModifiers.tempPower) {    //Boost from power potion
            damage =(int)(damage * 1.5);
        }
        damage = GlobalModifiers.numAttackUpgrades != 0 ?
                (int)(damage * (GlobalModifiers.numAttackUpgrades * 1.2)) : damage;
        //Play an animation
        enemy.takeDamage(damage);
        healthBar.setProgress(enemy.getHealth());
        if (enemy.isDead()) {enemyDefeated();}
    }

    /**
     * Called when the player is defeated.
     */
    @SuppressLint("SetTextI18n")
    private void gameOver() {
        //Stop timers
        timerHandler.removeCallbacks(attackRunnable);
        timerHandler.removeCallbacks(timerRunnable);

        //Make a popup, display rooms traveled and tokens won
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_game_over, null);

        TextView roomsTraveled = dialogView.findViewById(R.id.dialog_game_over_rooms);
        roomsTraveled.setText(getString(R.string.rooms_passed) + (difficulty - 1));
        TextView tokensEarned = dialogView.findViewById(R.id.dialog_game_over_tokens);
        tokensEarned.setText(getString(R.string.tokens_earned) + ((MainActivity)activity)
                .getRunningTokenReward());

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        //Set the window behind the background image to be transparent
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

        //Update stats, these do nothing right now but could be used later
        if (difficulty > GlobalModifiers.highestRoom) {
            GlobalModifiers.highestRoom = difficulty;
        }

        //Disable dialog from being able to be closed without pressing the exit button
        //Prevents soft-locking the app
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        //Set up exit button
        ImageButton exitButton = dialogView.findViewById(R.id.dialog_upgrade_exit_button);
        exitButton.setOnClickListener(event -> {
            ((MainActivity)activity).returnToTitle();
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * Called when the enemy is defeated.
     */
    private void enemyDefeated() {
        //Animate the enemy fading a future build

        //Disable inventory button (Prevents crashing if opening after death)
        ImageButton openInventory = activity.findViewById(R.id.open_inventory);
        openInventory.setOnClickListener(Event -> {});  //Do nothing

        //So keyboard can't be opened after death
        EditText answerBox = activity.findViewById(R.id.answer_input);
        answerBox.setFocusable(false);

        //Hide all unused elements
        new Handler(Looper.getMainLooper()).postDelayed(this::hideElements, 1000);

        //Stop timers
        timerHandler.removeCallbacks(attackRunnable);
        timerHandler.removeCallbacks(timerRunnable);

        //Reset temporary modifiers
        GlobalModifiers.refreshInstanceModifiers();

        //Add player coins
        player.addCoins(enemy.getReward());
        ((MainActivity)activity).updateToolBar();

        //Calculate token reward, scales exponentially
        int tokenReward = difficulty + (int)(difficulty * difficulty * 0.05);
        GlobalModifiers.upgradeToken += tokenReward;

        //For displaying tokens when game ends
        ((MainActivity)activity).addToTokenReward(tokenReward);

        //Display the reward on screen, maybe use the same textview as damage and just
        //animate it going up the screen?
        //This won't be in the current build, same functionality can be achieved with
        //toolbar stats

        new Handler(Looper.getMainLooper()).postDelayed(this::moveToTransition, 1000);
    }

    /**
     * Moves to the transition fragment.
     */
    private void moveToTransition() {
        ((MainActivity)activity).openTransitionFragment();
    }

    /**
     * Hides all elements that are not needed when enemy is defeated.
     */
    private void hideElements() {
        ImageView enemyBodyBottom = activity.findViewById(R.id.display_enemy_bottom);
        ImageView enemyBodyTop = activity.findViewById(R.id.display_enemy_top);
        enemyBodyBottom.setVisibility(View.GONE);
        enemyBodyTop.setVisibility(View.GONE);
        ImageView enemyHat = activity.findViewById(R.id.display_enemy_hat);
        enemyHat.setVisibility(View.GONE);
        for(int i = 0; i < 3; i++) {
            weaponSprites[i].setVisibility(View.GONE);
            weaponTexts[i].setVisibility(View.GONE);
        }
        timer.setVisibility(View.GONE);
        //I think this lagging behind could look cool
        //healthBar.setVisibility(View.GONE);
        //TextView name = activity.findViewById(R.id.enemy_name);
        //name.setVisibility(View.GONE);
        ImageButton openInventory = activity.findViewById(R.id.open_inventory);
        openInventory.setVisibility(View.GONE);
        TextView inventoryText = activity.findViewById(R.id.open_inventory_text);
        inventoryText.setVisibility(View.GONE);
        EditText answerBox = activity.findViewById(R.id.answer_input);
        answerBox.clearFocus();
        answerBox.setVisibility(View.GONE);
    }

    /**
     * Requests permission to use the microphone.
     * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initSpeechRecognizer();
            }
            else {
                //Disable the mic if permission is denied
                if (GameOptions.isMicActivated()) {GameOptions.toggleMic();}
            }
        }
    }

    /**
     * Called when the fragment is stopped. Stops the timers, destroys the
     * speechRecognizer, and releases the sound manager.
     */
    @Override
    public void onStop() {
        super.onStop();
        timerHandler.removeCallbacks(attackRunnable);
        timerHandler.removeCallbacks(timerRunnable);
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        //muteSystemSounds(false);
        soundManager.release();
    }
}