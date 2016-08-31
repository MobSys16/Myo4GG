package de.uni_luebeck.imis.gestures.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import de.uni_luebeck.imis.gestures.R;

/**
 * @author Finn Jacobsen
 * @since 30.06.2016.
 */
public class StartActivity extends Activity {
    /**
     * Handler used to post requests to start new activities so that the menu closing animation
     * works properly.
     */
    private final Handler mHandler = new Handler();

//    /** Listener that displays the options menu when the touchpad is tapped. */
//    private final GestureDetector.BaseListener mBaseListener = new GestureDetector.BaseListener() {
//        @Override
//        public boolean onGesture(Gesture gesture) {
//            if (gesture == Gesture.TAP) {
//                mAudioManager.playSoundEffect(Sounds.TAP);
//                openOptionsMenu();
//                return true;
//            } else {
//                return false;
//            }
//        }
//    };

    /** Audio manager used to play system sound effects. */
    private AudioManager mAudioManager;

//    /** Gesture detector used to present the options menu. */
//    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);

    }

    /**
     * Opens options menu when main windows is attached to the window manager to make sure the
     * activity has already been started. Options menu could not be started in onCreate.
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        openOptionsMenu();
    }

//    @Override
//    public boolean onGenericMotionEvent(MotionEvent event) {
//        return mGestureDetector.onMotionEvent(event);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        finish();
    }

    /**
     * The act of starting an activity here is wrapped inside a posted {@code Runnable} to avoid
     * animation problems between the closing menu and the new activity. The post ensures that the
     * menu gets the chance to slide down off the screen before the activity is started.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The startXXX() methods start a new activity, and if we call them directly here then
        // the new activity will start without giving the menu a chance to slide back down first.
        // By posting the calls to a handler instead, they will be processed on an upcoming pass
        // through the message queue, after the animation has completed, which results in a
        // smoother transition between activities.
        switch (item.getItemId()) {
            case R.id.evaluation:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startEvaluation();
                    }
                });
                return true;

            case R.id.detect_gestures:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startDetectGestures();
                    }
                });
                return true;

            default:
                return false;
        }
    }

    /**
     * Starts the main game activity, and finishes this activity so that the user is not returned
     * to the splash screen when they exit.
     */
    private void startEvaluation() {
        mAudioManager.playSoundEffect(Sounds.TAP);

        Intent intent = new Intent();
        intent.setClass(this, EvaluationActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Starts the switch background activity with preview of samples, and finishes this activity
     * so that the user is not returned to the splash screen when they exit.
     */
    private void startDetectGestures() {
        mAudioManager.playSoundEffect(Sounds.DISALLOWED);
    }
}
