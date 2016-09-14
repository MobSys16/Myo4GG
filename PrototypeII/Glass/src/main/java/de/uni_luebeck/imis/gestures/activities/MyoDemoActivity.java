package de.uni_luebeck.imis.gestures.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import de.uni_luebeck.imis.gestures.R;
import de.uni_luebeck.imis.gestures.adapter.CustomPagerAdapter;
import de.uni_luebeck.imis.gestures.myo.MyoGlassService;

/**
 * @author Finn
 * @since 14.09.2016
 *
 * Class description...
 */
public class MyoDemoActivity extends Activity {

        /** Audio manager used to play system sound effects. */
    private AudioManager mAudioManager;

    /** Gesture detector used to present the options menu. */
    private GestureDetector mGestureDetector;

    /** Listener that listens to gestures */
    private final GestureDetector.BaseListener mBaseListener = new GestureDetector.BaseListener() {
        @Override
        public boolean onGesture(Gesture gesture) {
            gestureDetected(gesture);
            return true;
        }
    };

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
            mService.setActivityActive(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_myo_test);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new CustomPagerAdapter(this));

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);

        initMyo();
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
        if (gesture.equals(Gesture.SWIPE_LEFT)) {
            if (mPager.getCurrentItem() == 0) {
               // Do nothing
            } else {
                // Otherwise, select the previous step.
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        } else if (gesture.equals(Gesture.SWIPE_RIGHT)) {
            if (mPager.getCurrentItem() == 3) {
                // Do nothing
            } else {
                // Otherwise, select the next step.
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            }
        } else if (gesture.equals(Gesture.SWIPE_DOWN)) {
            startMenuActivity();
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
