package de.uni_luebeck.imis.gestures.model;

import com.google.android.glass.touchpad.Gesture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Finn Jacobsen
 * @since 04.07.2016.
 */
public class Gestures {
    private static List<Gesture> mGestures = Arrays.asList(
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

    /**
     *
     * @param numberOfGesture
     * @return
     */
    public static Gesture getGestureByNumber (int numberOfGesture) {
        // TODO: Falsche Zahlen abfangen (kleiner 0, größer mGestures.size())
        return mGestures.get(numberOfGesture);
    }
}
