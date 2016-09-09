package de.uni_luebeck.imis.gestures.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import de.uni_luebeck.imis.gestures.R;


/**
 * @author Finn Jacobsen
 * @since 05.09.2016
 *
 * This activity makes it possible to test gestures of the google glass touch pad and gestures made
 * with myo. The detected gesture will be displayed on the glass display. 
 *
 */
public class DetectGesturesActivity extends Activity {

    /** Audio manager used to play system sound effects. */
    private AudioManager mAudioManager;

    /** Listener that displays the options menu when the touchpad is tapped. */
    private final GestureDetector.BaseListener mBaseListener = new GestureDetector.BaseListener() {
        @Override
        public boolean onGesture(Gesture gesture) {
            gestureDetected(gesture);
            return true;
        }
    };

    /** Gesture detector used to present the options menu. */
    private GestureDetector mGestureDetector;

    /** RelativeLayout of instruction view */
    private RelativeLayout mRlInstructions;
    /** RelativeLayout of gesture not found view */
    private RelativeLayout mRlGestureNotFound;
    /** RelativeLayout of gesture found view */
    private RelativeLayout mRlGestureFound;

    /** TextView of detected gesture */
    private TextView mTvGesture;
    /** TextView of related function */
    private TextView mTvFunction;

    /** TextView of myo status */
    private TextView mTvMyoStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the layout of the activity
        setContentView(R.layout.detect_gestures);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);

        initComponents();
    }

    /**
     * Initialization of view components of the layout.
     */
    private void initComponents() {
        mRlInstructions = (RelativeLayout) findViewById(R.id.detect_gestures_rl_instruction);
        mRlGestureNotFound = (RelativeLayout) findViewById(R.id.detect_gestures_rl_gesture_not_found);
        mRlGestureFound = (RelativeLayout) findViewById(R.id.detect_gestures_rl_gesture_found);

        mTvGesture = (TextView) findViewById(R.id.detect_gestures_tv_gesture);
        mTvFunction = (TextView) findViewById(R.id.detect_gestures_tv_function);

        // This is called to make sure the instructions view is displayed first.
        showInstructions();
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector.onMotionEvent(event);
    }

    /**
     * Called if gesture is detected. Differentiation of gestures is performed here.
     *
     * @param gesture       that has been detected.
     */
    private void gestureDetected(Gesture gesture) {
        switch (gesture) {
            case TAP:
                showGesture("Tap", "Bestätigen");
                break;
            case TWO_TAP:
                showGesture("Tap (2 Finger)", null);
                break;
            case THREE_TAP:
                showGesture("Tap (3 Finger)", null);
                break;
            case LONG_PRESS:
                showGesture("Langer Tap", null);
                break;
            case TWO_LONG_PRESS:
                showGesture("Langer Tap (2 Finger)", null);
                break;
            case THREE_LONG_PRESS:
                showGesture("Langer Tap (3 Finger)", null);
                break;
            case SWIPE_LEFT:
                showGesture("Swipe zurück", null);
                break;
            case SWIPE_RIGHT:
                showGesture("Swipe vor", null);
                break;
            case SWIPE_DOWN:
                startMenuActivity();
                break;
            case SWIPE_UP:
                showGesture("Swipe hoch", null);
                break;
            case TWO_SWIPE_LEFT:
                showGesture("Swipe zurück (2 Finger)", null);
                break;
            case TWO_SWIPE_RIGHT:
                showGesture("Swipe vor (2 Finger)", null);
                break;
            case TWO_SWIPE_DOWN:
                showGesture("Swipe runter (2 Finger)", null);
                break;
            case TWO_SWIPE_UP:
                showGesture("Swipe hoch (2 Finger)", null);
                break;
            default:
                showGestureNotFound();
        }
    }

    /**
     * Displayes the instructions view and disables other views.
     */
    private void showInstructions() {
        mRlInstructions.setVisibility(View.VISIBLE);
        mRlGestureFound.setVisibility(View.GONE);
        mRlGestureNotFound.setVisibility(View.GONE);
    }

    /**
     * Displayes gesture not found view and disables other views.
     */
    private void showGestureNotFound() {
        mRlInstructions.setVisibility(View.GONE);
        mRlGestureFound.setVisibility(View.GONE);
        mRlGestureNotFound.setVisibility(View.VISIBLE);
    }

    /**
     * Displayes gesture found view and disables other views. Detected gesture is displayed
     * and if related function exist it is displayed too.
     *
     * @param gesture       that has been detected.
     * @param function      that is related to gesture. This parameter is optional and can be null.
     */
    private void showGesture(String gesture, String function) {
        mRlInstructions.setVisibility(View.GONE);
        mRlGestureFound.setVisibility(View.VISIBLE);
        mRlGestureNotFound.setVisibility(View.GONE);

        // set gesture
        mTvGesture.setText(gesture);
        // set related function, if one exists
        if (function != null) {
            mTvFunction.setText(function);
        } else {
            mTvFunction.setText("");
        }
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
