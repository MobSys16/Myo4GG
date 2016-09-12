package de.uni_luebeck.imis.gestures.model;

import android.app.Activity;
import android.content.Context;

import com.google.android.glass.touchpad.Gesture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uni_luebeck.imis.gestures.activities.EvaluationActivity;
import de.uni_luebeck.imis.gestures.activities.MyoGuideActivty;

/**
 * This class builds a list of gestures that can be reached from every activity. The list
 * represents the order of gestures used in the evaluation.
 *
 * The EvaluationActivity uses a counter for the current used gesture that is synchronous to the
 * position of the gesture in the list of gestures in this class. If the EvaluationActivity
 * needs a new gesture it could be requested here by its number in the list.
 *
 * @author Finn Jacobsen
 * @since 04.07.2016.
 */
public class Gestures {
//    public static final String EVALUATION = "Evaluation";
//    public static final String MYO_GUIDE = "Myo Guide";

    private static List<Gesture> mGesturesOfEvaluation = Arrays.asList(
            Gesture.SWIPE_LEFT,
            Gesture.SWIPE_RIGHT,
            Gesture.SWIPE_DOWN,
            Gesture.SWIPE_UP,
            Gesture.TAP,
            Gesture.LONG_PRESS,
            Gesture.TWO_TAP,
            Gesture.THREE_TAP,
            Gesture.TWO_LONG_PRESS,
            Gesture.TWO_SWIPE_LEFT);

    //TODO: CustomGestures ergänzen, bzw Gestures durch CustomGestures ersetzen
    private static List<Gesture> mGesturesOfMyoGuide = Arrays.asList(
            // MyoGesture.FRAME
            // MyoGesture.HOLD_HAND
            // MyoGesture.FIST
            Gesture.SWIPE_DOWN,     // MyoGesture.SWIPE_DOWN,
            Gesture.TAP,            // MyoGesture.TAP,
            Gesture.SWIPE_RIGHT,    // MyoGesture.SWIPE_RIGHT,
            Gesture.SWIPE_LEFT);    // MyoGesture.SWIPE_LEFT);

    /**
     * Getter for returning a specific gesture of gesture list. Number of gesture represents
     * the position of the gesture in the list.
     *
     * @param numberOfGesture   that represents position of gesture in the list.
     * @return      gesture
     */
    public static Gesture getGestureByNumber(int numberOfGesture, Activity activity) {
        if (activity instanceof EvaluationActivity) {
            return mGesturesOfEvaluation.get(numberOfGesture);
        } else if (activity instanceof MyoGuideActivty) {
            return mGesturesOfMyoGuide.get(numberOfGesture);
        } else {
            return null;
        }
    }
}
