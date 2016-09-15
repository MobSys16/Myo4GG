package de.uni_luebeck.imis.gestures.myo.CustomGestures;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by naoki on 15/04/16.
 */
public class GestureDetectModel implements IGestureDetectModel{
    private final static Object LOCK = new Object();

    private String name = "";
    private IGestureDetectAction action;
    private GestureDetectMethod detectMethod;

    public GestureDetectModel(GestureDetectMethod method) {
        detectMethod = method;
    }

    @Override
    public void event(long time, byte[] emgData, byte[] imuData) {
        synchronized (LOCK) {
            //GestureDetectMethod.GestureState gestureState = detectMethod.getDetectGesture(emgData, imuData);
            MyoGesture gesture = detectMethod.getDetectGesture(emgData, imuData);
            if (gesture == MyoGesture.UNKNOWN){
                action();
            } else {
                Log.i("Gesture", "found gesture: " + gesture.name());
                action(gesture.name());
            }
        }
    }

    @Override
    public void setAction(IGestureDetectAction action) {
        this.action = action;
    }

    @Override
    public void action() {
        action.action("DETECT");
    }

    public void action(String message) {
        action.action(message);
    }


}
