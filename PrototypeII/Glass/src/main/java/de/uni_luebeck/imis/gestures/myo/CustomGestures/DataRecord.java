package de.uni_luebeck.imis.gestures.myo.CustomGestures;

import java.util.ArrayList;
import android.util.Log;
/**
 * Created by finnfahrenkrug on 13.09.16.
 */
public class DataRecord {
    private byte[] emgData;
    private byte[] imuData;
    private Quaternion orientation;
    private int[] accelerometerValues = {0,0,0};
    private int[] gyroscopeValues = {0,0,0};

    public DataRecord(byte[] emgData, byte[] imuData) {
        this.emgData = emgData;
        this.imuData = imuData;
        ArrayList<Integer> imuValues = new ArrayList<Integer>();

        ByteReader br = new ByteReader();
        br.setByteData(imuData);
        for (int index = 0; index < 10; index++) {
            Integer number = new Integer(br.getShort());
            imuValues.add(number);
        }
        int x = imuValues.get(0);
        int y = imuValues.get(1);
        int z = imuValues.get(2);
        int w = imuValues.get(3);
        this.orientation = new Quaternion(x, y, z, w);
        for (int index = 4; index < 7; index++) {
            accelerometerValues[index - 4] = imuValues.get(index);
        }
        for (int index = 7; index < 10; index++) {
            gyroscopeValues[index - 7] = imuValues.get(index);
        }
    }

    public byte[] getEmgData() {
        return emgData;
    }

    public void setEmgData(byte[] emgData) {
        this.emgData = emgData;
    }

    public byte[] getImuData() {
        return imuData;
    }

    public void setImuData(byte[] imuData) {
        this.imuData = imuData;
    }

    public Quaternion getOrientation() {
        return orientation;
    }

    public void setOrientation(Quaternion orientation) {
        this.orientation = orientation;
    }

    public int[] getAccelerometerValues() {
        return accelerometerValues;
    }

    public void setAccelerometerValues(int[] accelerometerValues) {
        this.accelerometerValues = accelerometerValues;
    }

    public int[] getGyroscopeValues() {
        return gyroscopeValues;
    }

    public void setGyroscopeValues(int[] gyroscopeValues) {
        this.gyroscopeValues = gyroscopeValues;
    }

    public boolean isSimilar(float threshold, DataRecord compareRecord){
        float sum = 0.0f;
        sum += 0.5f * getGyroscopeSimilarity(compareRecord.getGyroscopeValues());
        sum += 0.5f * getAccelerometerSimilarity(compareRecord.getAccelerometerValues());
        sum += 0.5f * orientation.getSimilarity(compareRecord.getOrientation());
        sum += 1.5 * getEMGSimilarity(compareRecord.getEmgData());
        sum /= 3.0f;
        Log.i("datarecord", "ähnlichkeit Ähnlichkeitsdurchschnitt: " + sum + ". Threshold: " + threshold);
        return isSmallerThanThreshold(sum, threshold);
    }

    public float getGyroscopeSimilarity(int[] compareValues){
            /*
            /// Scale values for unpacking IMU data
#define MYOHW_ORIENTATION_SCALE   16384.0f ///< See myohw_imu_data_t::orientation
#define MYOHW_ACCELEROMETER_SCALE 2048.0f  ///< See myohw_imu_data_t::accelerometer
#define MYOHW_GYROSCOPE_SCALE     16.0f    ///< See myohw_imu_data_t::gyroscope
             */
        Float GYROSCOPE_RANGE = 4000.0f;
        Float[] deltaValues = {0.0f, 0.0f, 0.0f};
        for (int index = 0; index < gyroscopeValues.length; index++){
            float difference = gyroscopeValues[index] - compareValues[index];
            deltaValues[index] = Math.abs(difference);
        }
        float average = 0.0f;
        for (int index = 0; index < deltaValues.length; index++){
            average += deltaValues[index] / GYROSCOPE_RANGE;
        }
        average /= (float) deltaValues.length;
        Log.i("datarecord", "ähnlichkeit gyroskop: " + average);
        return average;
    }

    public float getAccelerometerSimilarity(int[] compareValues){
            /*
            /// Scale values for unpacking IMU data
#define MYOHW_ORIENTATION_SCALE   16384.0f ///< See myohw_imu_data_t::orientation
#define MYOHW_ACCELEROMETER_SCALE 2048.0f  ///< See myohw_imu_data_t::accelerometer
#define MYOHW_GYROSCOPE_SCALE     16.0f    ///< See myohw_imu_data_t::gyroscope
             */
        Float ACCELEROMETER_RANGE = 2048.0f;
        Float[] deltaValues = {0.0f, 0.0f, 0.0f};
        for (int index = 0; index < accelerometerValues.length; index++){
            float difference = accelerometerValues[index] - compareValues[index];
            deltaValues[index] = Math.abs(difference);
        }
        float average = 0.0f;
        for (int index = 0; index < deltaValues.length; index++){
            average += deltaValues[index] / ACCELEROMETER_RANGE;
        }
        average /= (float) deltaValues.length;
        Log.i("datarecord", "ähnlichkeit accelerometer: " + average);
        return average;
    }

    public float getEMGSimilarity(byte[] compareEmgData){
        EmgCharacteristicData compareCharacteristic = new EmgCharacteristicData(compareEmgData);
        EmgData compareData = compareCharacteristic.getEmg8Data_abs();
        EmgCharacteristicData characteristic = new EmgCharacteristicData(emgData);
        EmgData data = characteristic.getEmg8Data_abs();
        Float distance = new Float(data.getSimilarity(compareData));
        Log.i("datarecord", "ähnlichkeit emg: " + distance);
        return distance;
        //return isSmallerThanThreshold( distance, threshold);
    }

    public static boolean isSmallerThanThreshold(float value, float threshold) {
        return threshold > value;
    }
}
