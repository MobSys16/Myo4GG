package de.uni_luebeck.imis.gestures.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import de.uni_luebeck.imis.gestures.R;
import de.uni_luebeck.imis.gestures.myo.MyoGlassService;


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

    private MyoGlassService mService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyoGlassService.MBinder binder = ((MyoGlassService.MBinder)service);
            mService = binder.getService();

            // Let the service know that the activity is showing. Used by the service to trigger
            // the appropriate foreground or background events.
            mService.setActivityActive(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    private RelativeLayout mRlInstructions;
    private RelativeLayout mRlGestureNotFound;
    private RelativeLayout mRlGestureFound;

    private TextView mTvGesture;
    private TextView mTvFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detect_gestures);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);

        initComponents();
        initMyo();
    }

    /**
     * Initialization of view components of the layout
     */
    private void initComponents() {
        mRlInstructions = (RelativeLayout) findViewById(R.id.detect_gestures_rl_instruction);
        mRlGestureNotFound = (RelativeLayout) findViewById(R.id.detect_gestures_rl_gesture_not_found);
        mRlGestureFound = (RelativeLayout) findViewById(R.id.detect_gestures_rl_gesture_found);

        mTvGesture = (TextView) findViewById(R.id.detect_gestures_tv_gesture);
        mTvFunction = (TextView) findViewById(R.id.detect_gestures_tv_function);

        showInstructions();
    }

    private void initMyo() {
        // Bind to the ConnectionService so that we can communicate with it directly.
        Intent intent = new Intent(this, MyoGlassService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        // Start the ConnectionService normally so it outlives the activity. This allows it to
        // listen for Myo pose events when the activity isn't running.
        startService(new Intent(this, MyoGlassService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mService != null) {
            mService.setActivityActive(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mService != null) {
            mService.setActivityActive(false);
        }
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector.onMotionEvent(event);
    }

    private void gestureDetected(Gesture gesture) {
        switch (gesture) {
            case TAP:
                showGesture("Tap", "Bestätigen");
                break;
//            case TWO_TAP:
//                break;
            case THREE_TAP:
                break;
            case LONG_PRESS:
                break;
            case TWO_LONG_PRESS:
                break;
            case THREE_LONG_PRESS:
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
                break;
            case TWO_SWIPE_RIGHT:
                break;
            case TWO_SWIPE_DOWN:
                break;
            case TWO_SWIPE_UP:
                break;
            default:
                showGestureNotFound();
        }
    }

    private void showInstructions() {
        mRlInstructions.setVisibility(View.VISIBLE);
        mRlGestureFound.setVisibility(View.GONE);
        mRlGestureNotFound.setVisibility(View.GONE);
    }

    private void showGestureNotFound() {
        mRlInstructions.setVisibility(View.GONE);
        mRlGestureFound.setVisibility(View.GONE);
        mRlGestureNotFound.setVisibility(View.VISIBLE);
    }

    private void showGesture(String gesture, String function) {
        mRlInstructions.setVisibility(View.GONE);
        mRlGestureFound.setVisibility(View.VISIBLE);
        mRlGestureNotFound.setVisibility(View.GONE);

        mTvGesture.setText(gesture);
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
        mAudioManager.playSoundEffect(Sounds.DISALLOWED);

        Intent intent = new Intent();
        intent.setClass(this, StartActivity.class);
        startActivity(intent);
        finish();
    }

}
