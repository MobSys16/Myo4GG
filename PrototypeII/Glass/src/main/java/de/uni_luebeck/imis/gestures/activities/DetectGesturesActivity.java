package de.uni_luebeck.imis.gestures.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import de.uni_luebeck.imis.gestures.R;
import de.uni_luebeck.imis.gestures.myo.CustomGestures.GestureDetectMethod;
import de.uni_luebeck.imis.gestures.myo.CustomGestures.GestureDetectModel;
import de.uni_luebeck.imis.gestures.myo.CustomGestures.GestureDetectModelManager;
import de.uni_luebeck.imis.gestures.myo.CustomGestures.GestureDetectSendResultAction;
import de.uni_luebeck.imis.gestures.myo.CustomGestures.GestureSaveMethod;
import de.uni_luebeck.imis.gestures.myo.CustomGestures.GestureSaveModel;
import de.uni_luebeck.imis.gestures.myo.CustomGestures.IGestureDetectModel;
import de.uni_luebeck.imis.gestures.myo.CustomGestures.MyoCallbackDelegate;
import de.uni_luebeck.imis.gestures.myo.CustomGestures.MyoCommandList;
import de.uni_luebeck.imis.gestures.myo.CustomGestures.MyoGattCallback;
import de.uni_luebeck.imis.gestures.myo.CustomGestures.MyoGesture;
import de.uni_luebeck.imis.gestures.myo.CustomGestures.MyoGestureListener;
import de.uni_luebeck.imis.gestures.myo.CustomGestures.MyoGestureManager;
import de.uni_luebeck.imis.gestures.myo.CustomGestures.NopModel;
import de.uni_luebeck.imis.gestures.myo.MyoGlassService;


/**
 * @author Finn Jacobsen
 * @since 05.09.2016
 *
 * This activity makes it possible to test gestures of the google glass touch pad and gestures made
 * with myo. The detected gesture will be displayed on the glass display. 
 *
 */
public class DetectGesturesActivity extends Activity  implements BluetoothAdapter.LeScanCallback, MyoGestureListener, MyoCallbackDelegate{

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

    /** Device Scanning Time (ms) */
    private static final long SCAN_PERIOD = 5000;

    /** Intent code for requesting Bluetooth enable */
    private static final int REQUEST_ENABLE_BT = 1;

    private static final String TAG = "BLE_Myo";

    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private TextView         emgDataText;
    private TextView         gestureText;

    private MyoGattCallback mMyoCallback;
    private MyoCommandList commandList = new MyoCommandList();

    private String deviceName;

    private GestureSaveModel saveModel;
    private GestureSaveMethod saveMethod;
    private GestureDetectModel detectModel;
    private GestureDetectMethod detectMethod;



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
        setContentView(R.layout.activity_detect_gestures);

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
        MyoGestureManager.getInstance().test();
        startNopModel();
        mHandler = new Handler();
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

            // Ensures Bluetooth is available on the device and it is enabled. If not,
            // displays a dialog requesting user permission to enable Bluetooth.
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                // Scanning Time out by Handler.
                // The device scanning needs high energy.
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBluetoothAdapter.stopLeScan(DetectGesturesActivity.this);
                    }
                }, SCAN_PERIOD);
                mBluetoothAdapter.startLeScan(this);
            }
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
                Log.v("tap","detected tap");
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
        mAudioManager.playSoundEffect(Sounds.DISMISSED);

        Intent intent = new Intent();
        intent.setClass(this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.v("gestures", device.getName());
        mMyoCallback = new MyoGattCallback(mHandler, emgDataText);
        mMyoCallback.setDelegate(this);
        mBluetoothGatt = device.connectGatt(this, false, mMyoCallback);
        mMyoCallback.setBluetoothGatt(mBluetoothGatt);

        mBluetoothAdapter.stopLeScan(this);
    }

    public void startDetecting(){

        if (mBluetoothGatt == null || !mMyoCallback.setMyoControlCommand(commandList.sendEmgAndImu())) {
            Log.d(TAG,"False EMG");
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startDetectModel();
                }
            }, 1000);
        }
    }

    public void startDetectModel() {
        IGestureDetectModel model = detectModel;
        model.setAction(new GestureDetectSendResultAction());
        GestureDetectModelManager.setCurrentModel(model);
    }

    public void startNopModel() {
        GestureDetectModelManager.setCurrentModel(new NopModel());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(DetectGesturesActivity.this);
                }
            }, SCAN_PERIOD);
            mBluetoothAdapter.startLeScan(this);
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        this.closeBLEGatt();
    }

    public void closeBLEGatt() {
        if (mBluetoothGatt == null) {
            return;
        }
        mMyoCallback.stopCallback();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    @Override
    public void onMyoGesture(MyoGesture gesture) {
        final MyoGesture threadGesture = gesture;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (threadGesture){
                    case TAP:
                        showGesture( "Bestätigen", null);
                        break;
                    case SWIPE_LEFT:
                        showGesture("Zurück", null);
                        break;
                    case SWIPE_RIGHT:
                        showGesture("Weiter", null);
                        break;
                    case SWIPE_DOWN:
                        showGesture("Abbrechen", null);
                        break;
                    case FRAME:
                        showGesture("Foto machen", null);
                        break;
                    case HOLD_HAND:
                        showGesture("Pause", null);
                        break;
                    case FIST:
                        showGesture("Stop", null);
                        break;
                    default:
                        showGestureNotFound();
                }
                Log.i(TAG, "onMyoGesture called. " + threadGesture.name());
            }
        });
    }

    @Override
    public void didSetCharacteristicCommand(BluetoothGattCharacteristic characteristicCommand) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setMyoStatus(true);
            }
        });
        detectMethod = new GestureDetectMethod();
        detectMethod.setListener(this);
        detectModel = new GestureDetectModel(detectMethod);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startDetecting();
            }
        }, 1000);
    }
}
