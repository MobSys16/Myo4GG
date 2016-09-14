package example.naoki.ble_myo;
import android.util.Log;

import java.util.ArrayList;
import android.os.Handler;
import android.os.HandlerThread;
import java.util.logging.LogRecord;

/**
 * Created by naoki on 15/04/17.
 */

public class GestureDetectMethod {
    private final static int COMPARE_NUM = 7;
    private final static int STREAM_DATA_LENGTH = 5;
    private final static Double THRESHOLD = 0.01;
    private final static int PAUSE_MILLISECONDS = 250;
    private boolean isPausing = false;
    //private final ArrayList<EmgData> compareGesture;

    private int streamCount = 0;
    //private int imuStreamCount = 0;
    private EmgData streamingMaxData;
    private Double detect_distance;
    private int detect_Num;
   // private int actionCounter = 0;
    private MyoGestureListener listener;
    private NumberSmoother numberSmoother = new NumberSmoother();

    public enum GestureState {
        No_Gesture,
        Gesture_1,
        Gesture_2,
        Gesture_3
    }

    private GestureState getEnum(int i_gesture) {
        switch (i_gesture) {
            case 0:
                return GestureState.Gesture_1;
            case 1:
                return GestureState.Gesture_2;
            case 2:
                return GestureState.Gesture_3;
            default:
                return GestureState.No_Gesture;
        }
    }

    public MyoGesture getDetectGesture(byte[] emgData, byte[] imuData) {
        MyoGestureManager manager = MyoGestureManager.getInstance();
        MyoGesture pose =  manager.getAveragedGesture(2, emgData, imuData);
        if (pose != MyoGesture.UNKNOWN){
            if (listener != null) {
                if (!isPausing){
                    listener.onMyoGesture(pose);
                    isPausing = true;
                    HandlerThread handlerThread = new HandlerThread("HandlerThread");
                    handlerThread.start();
                    Handler handler;
                    handler = new Handler(handlerThread.getLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isPausing = false;
                        }
                    }, PAUSE_MILLISECONDS);
                }
            }
        }
        return pose;
        /*
        EmgData streamData = new EmgData(new EmgCharacteristicData(emgData));
        streamCount++;
        //imuStreamCount++;
        if (streamCount == 1){
            streamingMaxData = streamData;
        } else {
            for (int index = 0; index < 8; index++) {
                if (streamData.getElement(index) > streamingMaxData.getElement(index)) {
                    streamingMaxData.setElement(index, streamData.getElement(index));
                }
            }
            if (streamCount == STREAM_DATA_LENGTH){
                detect_distance = getThreshold();
                detect_Num = -1;
                for (int i_gesture = 0;i_gesture < COMPARE_NUM ;i_gesture++) {
                    EmgData compData = compareGesture.get(i_gesture);
                    double distance = distanceCalculation(streamingMaxData, compData);
                    if (detect_distance > distance) {
                        detect_distance = distance;
                        detect_Num = i_gesture;
                    }
                }
                numberSmoother.addArray(detect_Num);
                streamCount = 0;
            }
        }
        return getEnum(numberSmoother.getSmoothingNumber());*/
    }

    private double getThreshold() {
        return THRESHOLD;
//        return 0.9;
    }

	// 2 vectors distance devied from each vectors norm.
    private double distanceCalculation(EmgData streamData, EmgData compareData){
        double return_val = streamData.getDistanceFrom(compareData)/streamData.getNorm()/compareData.getNorm();
        return return_val;
    }

	// Mathematical [sin] value of 2 vectors' inner angle.
    private double distanceCalculation_sin(EmgData streamData, EmgData compareData){
        double return_val = streamData.getInnerProductionTo(compareData)/streamData.getNorm()/compareData.getNorm();
        return return_val;
    }

	// Mathematical [cos] value of 2 vectors' inner angle from low of cosines.
    private double distanceCalculation_cos(EmgData streamData, EmgData compareData){
        double streamNorm  = streamData.getNorm();
        double compareNorm = compareData.getNorm();
        double distance    = streamData.getDistanceFrom(compareData);
        return (Math.pow(streamNorm,2.0)+Math.pow(compareNorm,2.0)-Math.pow(distance,2.0))/streamNorm/compareNorm/2;
    }

    public MyoGestureListener getListener() {
        return listener;
    }

    public void setListener(MyoGestureListener listener) {
        this.listener = listener;
    }
}
