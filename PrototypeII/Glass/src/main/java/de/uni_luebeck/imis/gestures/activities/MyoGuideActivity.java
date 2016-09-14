package de.uni_luebeck.imis.gestures.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import de.uni_luebeck.imis.gestures.R;
import de.uni_luebeck.imis.gestures.model.Gestures;
import de.uni_luebeck.imis.gestures.model.Mode;

/**
 * @author Finn Jacobsen
 * @since 09.09.2016
 *
 * This activity contains a guide to show which myo gestures exist.
 */
public class MyoGuideActivity extends Activity {

    /** Audio manager used to play system sound effects. */
    private AudioManager mAudioManager;

    /** Gesture detector used to present the options menu. */
    private GestureDetector mGestureDetector;

    /** Listener that listens to gestures. */
    private final GestureDetector.BaseListener mBaseListener = new GestureDetector.BaseListener() {
        @Override
        public boolean onGesture(Gesture gesture) {
            gestureDetected(gesture);
            return true;
        }
    };

    private Mode mCurrentMode = Mode.INSTRUCTIONS;
    private int mNumberOfCurrentView = 0;
    private int mNumberOfCurrentGesture = 0;

    private ImageView mIvCircle;
    private TextView mTvGesture;
    private TextView mTvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the layout of the activity
        setContentView(R.layout.activity_myo_guide_instructions);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);

        initComponents();
    }

    /**
     * Initialization of view components of the layout.
     */
    private void initComponents() {
        mIvCircle = (ImageView) findViewById(R.id.myo_guide_iv_circle);
        mTvGesture = (TextView) findViewById(R.id.myo_guide_tv_gesture);
        mTvDescription = (TextView) findViewById(R.id.myo_guide_tv_description);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector.onMotionEvent(event);
    }

    /**
     * This activity is called if a gesture has been detected. It is considered here,
     * which mode is active. Each mode has different results:
     *
     * INSTRUCTIONS Mode:
     * Only a tap for continuing and a swype down to close the app is supported.
     *
     * DETECT TOUCHPAD GESTURE Mode:
     * The detected gesture is compared to the wanted gesture.
     *
     * MAKE GESTURE WITH HAND Mode:
     * Only a tap for continuing and a swype down to close the app is supported.
     *
     * TOUCHPAD DISABLED Mode:
     * The touchpad is inactive to provide accidental gestures while waiting for
     * feedback/the next view
     *
     * @param gesture       that has been detected
     */
    private void gestureDetected(Gesture gesture) {
        switch (mCurrentMode) {
            case INSTRUCTIONS:
                if (gesture.equals(Gesture.TAP)) {
                    mAudioManager.playSoundEffect(Sounds.TAP);
                    nextView();
                } else if (gesture.equals(Gesture.SWIPE_DOWN)) {
                    mAudioManager.playSoundEffect(Sounds.DISMISSED);
                    finish();
                }
                break;
            case DETECT_TOUCHPAD_GESTURE:
                if (gesture.equals(Gestures.getGestureByNumber(mNumberOfCurrentGesture, this))) {
                    correctGestureDetected();
                } else {
                    wrongGestureDetected();
                }
                break;
            case TOUCHPAD_DISABLED:
                break;
        }
    }

    /**
     * In this method the layout and the mode for each view is set. The method is called if
     * a new view will be displayed.
     */
    private void nextView() {
        switch (mNumberOfCurrentView) {
            case 0:
                setContentView(R.layout.activity_myo_guide);
                initComponents();
                mTvGesture.setText("Zurück");
                mTvDescription.setText("Führe eine Wischbewegung mit der flachen Hand nach links aus.");

                mCurrentMode = Mode.DETECT_TOUCHPAD_GESTURE;
                break;
            case 1:
                setContentView(R.layout.activity_myo_guide);
                mTvGesture.setText("Vor");
                mTvDescription.setText("Führe eine Wischbewegung mit der flachen Hand nach rechts aus.");

                mCurrentMode = Mode.DETECT_TOUCHPAD_GESTURE;
                break;
            //TODO: Alle 7 Gesten einbauen
            default:
                startMenuActivity();
        }

        // Increase the number of current view after new view was loaded.
        mNumberOfCurrentView++;
        initComponents();
    }

    /**
     * This method is called if the expected gesture was detected. A feedback for the right
     * gesture will be given and after 2 seconds the next view will be shown.
     */
    private void correctGestureDetected() {
        mIvCircle.setImageResource(R.drawable.circle_sucess);

        // Disable touchpad
        mCurrentMode = Mode.TOUCHPAD_DISABLED;
        // change view after 2 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextView();
            }
        }, 2000);

        // Play sound to indicate to successful gesture
        mAudioManager.playSoundEffect(Sounds.SUCCESS);

        // Increase the number of current gesture
        mNumberOfCurrentGesture++;
    }

    /**
     * this method is called if a wrong gesture is detected. A red cross is displayed and
     * gives the user visual feedback.
     */
    private void wrongGestureDetected() {
        mIvCircle.setImageResource(R.drawable.circle_error);

        // Play sound to indicate to wrong gesture
        mAudioManager.playSoundEffect(Sounds.ERROR);
    }

    /**
     * This method is called after the last view of the evaluation was displayed to go back to
     * the start menu. This is needed, because the menu was closed after choosing the evaluation.
     */
    private void startMenuActivity() {
        mAudioManager.playSoundEffect(Sounds.DISMISSED);

        Intent intent = new Intent();
        intent.setClass(this, StartActivity.class);
        startActivity(intent);
        finish();
    }
}
