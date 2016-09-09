package de.uni_luebeck.imis.gestures.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import de.uni_luebeck.imis.gestures.R;
import de.uni_luebeck.imis.gestures.model.Gestures;
import de.uni_luebeck.imis.gestures.model.Mode;

/**
 * @author Finn Jacobsen
 * @since 04.07.2016
 *
 * This activity contains the whole evaluation part of the app.
 *
 * The evaluation is separated in three different parts. The first part includes the instruction
 * for the user. Only text is displayed in this part. The second part includes detection of the
 * touch pad for different gestures and a feedback. And in the third part the user should make the
 * gestures by hand so again only text is displayed. The difference in these parts is the purpose
 * of the touch pad. To differ the tasks the touch pad should accomplish there are four different
 * modes. Every single view that is displayed needs the information, which mode is used.
 *
 * The layouts are build as xml-layouts without programmatically and dynamic changes in the
 * activity to preserve the clearness of the code. For each view an own layout is created. This
 * may not be the most efficient way but helps to provide cluttering the activity.
 */
public class EvaluationActivity extends Activity {

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

    private Mode mCurrentMode = Mode.INSTRUCTIONS;
//    private Gesture mCurrentGesture = Gesture.SWIPE_LEFT;
    private int mNumberOfCurrentView = 0;
    private int mNumberOfCurrentGesture = 0;

    private ImageView ivCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.evaluate_welcome);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);

        initComponents();
    }

    /**
     * Initialization of view components of the layout
     */
    private void initComponents() {
        ivCircle = (ImageView) findViewById(R.id.evaluate_gesture_iv_circle);
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
                if (gesture.equals(Gestures.getGestureByNumber(mNumberOfCurrentGesture))) {
                    correctGestureDetected();
                } else {
                    wrongGestureDetected();
                }
                break;
            case MAKE_GESTURE_WITH_HANDS:
                if (gesture.equals(Gesture.TAP)) {
                    mAudioManager.playSoundEffect(Sounds.TAP);
                    nextView();
                } else if (gesture.equals(Gesture.SWIPE_DOWN)) {
                    mAudioManager.playSoundEffect(Sounds.DISMISSED);
                    finish();
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
                setContentView(R.layout.evaluate_instructions_1);
                break;
            case 1:
                setContentView(R.layout.evaluate_action_1);
                mCurrentMode = Mode.MAKE_GESTURE_WITH_HANDS;
                break;
            case 2:
                setContentView(R.layout.evaluate_action_2);
                mCurrentMode = Mode.MAKE_GESTURE_WITH_HANDS;
                break;
            case 3:
                setContentView(R.layout.evaluate_action_3);
                mCurrentMode = Mode.MAKE_GESTURE_WITH_HANDS;
                break;
            case 4:
                setContentView(R.layout.evaluate_action_4);
                mCurrentMode = Mode.MAKE_GESTURE_WITH_HANDS;
                break;
            case 5:
                setContentView(R.layout.evaluate_action_5);
                mCurrentMode = Mode.MAKE_GESTURE_WITH_HANDS;
                break;
            case 6:
                setContentView(R.layout.evaluate_instructions_2);
                break;
            case 7:
                setContentView(R.layout.evaluate_instructions_3);
                mCurrentMode = Mode.INSTRUCTIONS;
                break;
            case 8:
                setContentView(R.layout.evaluate_gesture_1);
                mCurrentMode = Mode.DETECT_TOUCHPAD_GESTURE;
                break;
            case 9:
                setContentView(R.layout.evaluate_gesture_1_hands);
                mCurrentMode = Mode.MAKE_GESTURE_WITH_HANDS;
                break;
            case 10:
                setContentView(R.layout.evaluate_gesture_2);
                mCurrentMode = Mode.DETECT_TOUCHPAD_GESTURE;
                break;
            case 11:
                setContentView(R.layout.evaluate_gesture_2_hands);
                mCurrentMode = Mode.MAKE_GESTURE_WITH_HANDS;
                break;
            case 12:
                setContentView(R.layout.evaluate_gesture_3);
                mCurrentMode = Mode.DETECT_TOUCHPAD_GESTURE;
                break;
            case 13:
                setContentView(R.layout.evaluate_gesture_3_hands);
                mCurrentMode = Mode.MAKE_GESTURE_WITH_HANDS;
                break;
            case 14:
                setContentView(R.layout.evaluate_gesture_4);
                mCurrentMode = Mode.DETECT_TOUCHPAD_GESTURE;
                break;
            case 15:
                setContentView(R.layout.evaluate_gesture_4_hands);
                mCurrentMode = Mode.MAKE_GESTURE_WITH_HANDS;
                break;
            case 16:
                setContentView(R.layout.evaluate_gesture_5);
                mCurrentMode = Mode.DETECT_TOUCHPAD_GESTURE;
                break;
            case 17:
                setContentView(R.layout.evaluate_gesture_5_hands);
                mCurrentMode = Mode.MAKE_GESTURE_WITH_HANDS;
                break;
            case 18:
                setContentView(R.layout.evaluate_gesture_6);
                mCurrentMode = Mode.DETECT_TOUCHPAD_GESTURE;
                break;
            case 19:
                setContentView(R.layout.evaluate_gesture_6_hands);
                mCurrentMode = Mode.MAKE_GESTURE_WITH_HANDS;
                break;
            case 20:
                setContentView(R.layout.evaluate_gesture_7);
                mCurrentMode = Mode.DETECT_TOUCHPAD_GESTURE;
                break;
            case 21:
                setContentView(R.layout.evaluate_gesture_7_hands);
                mCurrentMode = Mode.MAKE_GESTURE_WITH_HANDS;
                break;
            case 22:
                setContentView(R.layout.evaluate_gesture_8);
                mCurrentMode = Mode.DETECT_TOUCHPAD_GESTURE;
                break;
            case 23:
                setContentView(R.layout.evaluate_gesture_8_hands);
                mCurrentMode = Mode.MAKE_GESTURE_WITH_HANDS;
                break;
            case 24:
                setContentView(R.layout.evaluate_gesture_9);
                mCurrentMode = Mode.DETECT_TOUCHPAD_GESTURE;
                break;
            case 25:
                setContentView(R.layout.evaluate_gesture_9_hands);
                mCurrentMode = Mode.MAKE_GESTURE_WITH_HANDS;
                break;
            case 26:
                setContentView(R.layout.evaluate_gesture_10);
                mCurrentMode = Mode.DETECT_TOUCHPAD_GESTURE;
                break;
            case 27:
                setContentView(R.layout.evaluate_gesture_10_hands);
                mCurrentMode = Mode.MAKE_GESTURE_WITH_HANDS;
                break;
            case 28:
                setContentView(R.layout.evaluate_instructions_4);
                mCurrentMode = Mode.INSTRUCTIONS;
                break;
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
        ivCircle.setImageResource(R.drawable.circle_sucess);

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
        ivCircle.setImageResource(R.drawable.circle_error);

        // Play sound to indicate to wrong gesture
        mAudioManager.playSoundEffect(Sounds.ERROR);
    }

    /**
     * This method is called after the last view of the evaluation was displayed to go back to
     * the start menu. This is needed, because the menu was closed after choosing the evaluation.
     */
    private void startMenuActivity() {
        mAudioManager.playSoundEffect(Sounds.TAP);

        Intent intent = new Intent();
        intent.setClass(this, StartActivity.class);
        startActivity(intent);
        finish();
    }
}
