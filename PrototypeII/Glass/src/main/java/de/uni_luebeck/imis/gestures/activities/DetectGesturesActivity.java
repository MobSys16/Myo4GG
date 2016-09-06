package de.uni_luebeck.imis.gestures.activities;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.ImageView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.evaluate_welcome);  //TODO: change to detct_gestures_layout

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);

        initComponents();
    }

    /**
     * Initialization of view components of the layout
     */
    private void initComponents() {

    }

    private void gestureDetected(Gesture gesture) {
        switch (gesture) {
            case TAP:
            case TWO_TAP:
                break;
            case LONG_PRESS:
                break;
            default:
                //TODO: Geste nicht erkannt anzeigen
        }
    }

}
