package de.uni_luebeck.imis.gestures.myo.CustomGestures;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by naoki on 15/04/17.
 */
public class GestureSaveMethod {
    private final static String TAG = "Myo_compare";
    private final static String FileNameEMG = "compareEMGData.dat";
    private final  static  String FileNameIMU = "compareIMUData.dat";

    private final static int COMPARE_NUM = 0;
    private final static int SAVE_DATA_LENGTH = 5;
    private final static int AVERAGING_LENGTH = 10;

    private ArrayList<EmgCharacteristicData> rawEmgDataList = new ArrayList<EmgCharacteristicData>();
    private ArrayList<EmgData> maxEmgDataList = new ArrayList<EmgData>();
    private ArrayList<EmgData> compareEMGGesture = new ArrayList<EmgData>();

    private SaveState saveState = SaveState.Ready;

    private int dataCounter = 0;
    private int gestureCounter = 0;

    public GestureSaveMethod() {
        MyoDataFileReader emgFileReader = new MyoDataFileReader(TAG,FileNameEMG);
        MyoDataFileReader imuFileReader = new MyoDataFileReader(TAG, FileNameIMU);
        if (emgFileReader.loadEMG().size() == 3) {
            compareEMGGesture = emgFileReader.loadEMG();
            saveState = SaveState.Have_Saved;
        }
    }

    public enum SaveState {
        Ready,
        Not_Saved,
        Now_Saving,
        Have_Saved,
    }

    public void addData(byte[] emgData, byte[] imuData) {
        //MyoGestureManager manager = MyoGestureManager.getInstance();
        //manager.add(emgData, imuData);
        rawEmgDataList.add(new EmgCharacteristicData(emgData));
        dataCounter++;
        if (dataCounter % SAVE_DATA_LENGTH == 0) {
            EmgData dataEmgMax = new EmgData();
            int count = 0;
            for (EmgCharacteristicData emg16Temp : rawEmgDataList) {
                EmgData emg8Temp = emg16Temp.getEmg8Data_abs();
                if (count == 0) {
                    dataEmgMax = emg8Temp;
                } else {
                    for (int i_element = 0; i_element < 8; i_element++) {
                        if (emg8Temp.getElement(i_element) > dataEmgMax.getElement(i_element)) {
                            dataEmgMax.setElement(i_element, emg8Temp.getElement(i_element));
                        }
                    }
                }
                count++;
            }
            if (rawEmgDataList.size() < SAVE_DATA_LENGTH) {
                Log.e("GestureDetect", "Small rawData : " + rawEmgDataList.size());
            }
            maxEmgDataList.add(dataEmgMax);
            rawEmgDataList = new ArrayList<EmgCharacteristicData>();
        }
        if (dataCounter == SAVE_DATA_LENGTH * AVERAGING_LENGTH) {
            saveState = SaveState.Not_Saved;
            makeCompareData();
            gestureCount();
            dataCounter = 0;
        }
    }

    private void gestureCount() {
        gestureCounter++;
        if (gestureCounter == COMPARE_NUM) {
            saveState = SaveState.Have_Saved;
            gestureCounter = 0;
            MyoDataFileReader emgFileReader = new MyoDataFileReader(TAG,FileNameEMG);
            emgFileReader.saveEMGMAX(getCompareEmgDataList());
        }
    }

    private void makeCompareData() {
        EmgData tempData  = new EmgData();
        // Get each Max EMG-elements of maxDataList
        int count = 0;
        for (EmgData emg8Temp : maxEmgDataList) {
            if (count == 0) {
                tempData = emg8Temp;
            } else {
                for (int i_element = 0; i_element < 8; i_element++) {
                    if (emg8Temp.getElement(i_element) > tempData.getElement(i_element)) {
                        tempData.setElement(i_element, emg8Temp.getElement(i_element));
                    }
                }
            }
            count++;
        }
        count = 0;

        // Averaging EMG-elements of maxDataList
/*        int count = 0;
        for (EmgData emg8Temp : maxDataList) {
            if (count == 0) {
                tempData = emg8Temp;
            } else {
                for (int i_element = 0; i_element < 8; i_element++) {
                    tempData.setElement(i_element, tempData.getElement(i_element) + emg8Temp.getElement(i_element));
                }
            }
            count++;
        }
        for (int i_element = 0; i_element < 8; i_element++) {
            tempData.setElement(i_element, tempData.getElement(i_element)/maxDataList.size());
        }
*/
        if (maxEmgDataList.size() < AVERAGING_LENGTH) {
            Log.e("GestureDetect", "Small aveData : " + maxEmgDataList.size());
        }
        compareEMGGesture.add(tempData);
        maxEmgDataList = new ArrayList<EmgData>();
        Log.e("Save", "Saving");
    }

    public SaveState getSaveState() {
        return saveState;
    }

    public void setState(SaveState state) {
        saveState = state;
    }

    public int getGestureCounter() {
        return gestureCounter;
    }

    public ArrayList<EmgData> getCompareEmgDataList() {
        return compareEMGGesture;
    }
}
