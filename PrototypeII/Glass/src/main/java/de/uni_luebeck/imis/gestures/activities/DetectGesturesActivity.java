package de.uni_luebeck.imis.gestures.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
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

import org.w3c.dom.Text;

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

    /** MyoGlassService provides connection to myo */
    private MyoGlassService mService;

    /** If connection to MyoGlassService was successful, onServiceConncted will be called */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyoGlassService.MBinder binder = ((MyoGlassService.MBinder)service);
            mService = binder.getService();

            // Let the service know that the activity is showing. Used by the service to trigger
            // the appropriate foreground or background events. The DetectGesturesActivity has to
            // be delivered here to allow the MyoGlassService to send the status of the myo
            // connection back to the activity.
            mService.setActivityActive(true, DetectGesturesActivity.this);

            // check if myo is already connected
            setMyoStatus(mService.isAttachedToAnyMyo());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

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
        initMyo();
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

        mTvMyoStatus = (TextView) findViewById(R.id.detect_gestures_tv_myo_status);

        // This is called to make sure the instructions view is displayed first.
        showInstructions();
    }

    /**
     * Initialization of myo service.
     */
    private void initMyo() {
        // Bind to the ConnectionService so that we can communicate with it directly.
        Intent intent = new Intent(this, MyoGlassService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        // Start the ConnectionService normally so it outlives the activity. This allows it to
        // listen for Myo pose events when the activity isn't running.
        startService(new Intent(this, MyoGlassService.class));
    }

    /**
     * Displayes status of myo in the upper left corner of the layout.
     *
     * @param connectedToMyo    true, if myo is connected
     */
    public void setMyoStatus(boolean connectedToMyo) {
        if (connectedToMyo) {
            mTvMyoStatus.setText(" verbunden");
            mTvMyoStatus.setTextColor(Color.parseColor("#2CAA3B"));
        } else {
            mTvMyoStatus.setText(" nicht verbunden");
            mTvMyoStatus.setTextColor(Color.parseColor("#FF0000"));
        }
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
            mService.setActivityActive(true, DetectGesturesActivity.this);
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
        mAudioManager.playSoundEffect(Sounds.DISALLOWED);

        Intent intent = new Intent();
        intent.setClass(this, StartActivity.class);
        startActivity(intent);
        finish();
    }

}
