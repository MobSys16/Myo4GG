package example.naoki.ble_myo;

import  com.google.gson.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import  java.io.FileWriter;
import  java.io.FileReader;
import  java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Environment;
import  android.util.Log;
/**
 * Created by finnfahrenkrug on 12.09.16.
 */
public class MyoGestureManager {
    private static final String TAG = "MyoPoseData";
    private static MyoGestureManager instance = null;
    private ArrayList<byte[]> emgDataRecords = new ArrayList<>();
    private ArrayList<byte[]> imuDataRecords = new ArrayList<>();
    private ArrayList<DataRecord> myoDataRecords = new ArrayList<>();
    private LimitedQueue<MyoGesture> lastGestures = new LimitedQueue<>(5);
    protected MyoGestureManager(){
        super();
        //test();
        /*
        try {
            FileReader emgFr = new FileReader(getEMGTargetFile());
            BufferedReader emgBr = new BufferedReader(emgFr);
            FileReader imuFR = new FileReader(getIMUTargetFile());
            BufferedReader imuBR = new BufferedReader(imuFR);
            String emgLine;
            String imuLine;
            while((emgLine = emgBr.readLine()) != null && (imuLine = imuBR.readLine()) != null){
                byte[] emgData = hexLineToByteArray(emgLine);
                byte[] imuData = hexLineToByteArray(imuLine);
                add(emgData, imuData);
            }
            emgBr.close();
            imuBR.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
    public static MyoGestureManager getInstance() {
        if(instance == null) {
            instance = new MyoGestureManager();
        }
        return instance;
    }

    public void add(byte[] emgData, byte[] imuData){
        Log.i(TAG, "adding new data");
        ByteReader br = new ByteReader();
        br.setByteData(emgData);
        Log.i(TAG, "added emg: " + br.getByteDataString());
        br.setByteData(imuData);
        Log.i(TAG, "added imu: " + br.getByteDataString());
        emgDataRecords.add(emgData);
        imuDataRecords.add(imuData);
        DataRecord record = new DataRecord(emgData, imuData);
        myoDataRecords.add(record);
        if (myoDataRecords.size() == 7) {
            Log.e("gestures", "saving");
            safe();
        }
    }

    public void deleteSavedGestures() {
        File emgFile = getEMGTargetFile();
        try {
            emgFile.delete();
        } catch (Exception e){
            e.printStackTrace();
        }
        File imuFile = getIMUTargetFile();
        try {
            imuFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        myoDataRecords.clear();
        emgDataRecords.clear();
        imuDataRecords.clear();
        Log.i("gestures", "cleared gestures all");
    }

    public void safe() {
        Log.i(TAG, "saving: " + getEMGFileName());
        Log.i(TAG, "saving: " + getIMUFileName());
        StringBuilder sr = new StringBuilder();
        File file = getEMGTargetFile();
        for (byte[] data : emgDataRecords){
            sr.append(bytesToHex(data));
            sr.append("\n");
        }
        try {
            file.getParentFile().mkdirs();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sr.toString());
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sr = new StringBuilder();
        for (byte[] data : imuDataRecords){
            sr.append(bytesToHex(data));
            sr.append("\n");
        }
        try {
            file = getIMUTargetFile();
            file.getParentFile().mkdirs();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sr.toString());
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getBaseDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    private String getEMGFileName() {
        return getBaseDirectory() + "/myoData/emg.dat";
    }

    private String getIMUFileName(){
        return getBaseDirectory() + "/myoData/imu.dat";
    }

    private File getEMGTargetFile() {
        return new File(getEMGFileName());
    }

    private File getIMUTargetFile() {
        return new File(getIMUFileName());
    }

    public void test() {
        Log.i("add", "testing");
        String imu = "D8 3A 5F 06 D9 FB 02 E8 62 00 D6 01 FF 07 FF FF 04 00 F3 FF \n" +
                "65 3B 0D 04 5A FE 91 E8 00 00 13 01 2B 08 04 00 08 00 F1 FF \n" +
                "7B 3A 3F 06 E7 FB 19 E7 56 00 C7 01 E2 07 10 00 04 00 F7 FF \n" +
                "DD 3A CA 04 3C FE 68 E7 FE FF 0E 01 21 08 0C 00 F8 FF DB FF \n" +
                "C0 3A F5 06 CE FA 26 E8 92 00 10 02 EE 07 F7 FF F1 FF EF FF \n" +
                "6B 39 9C F4 03 06 D7 E6 C0 FF BD FC 73 07 10 00 12 00 13 00 \n" +
                "A8 39 7D F5 32 08 A2 E7 2D FF AC FC 69 07 14 00 F7 FF ED FF";
        String emg = "F6 1C EF 02 0E 14 F8 F7 0A 29 2F 18 FD 09 05 03 \n" +
                "0B 1A E3 F1 FF 04 04 03 FA FA E3 FA F9 F6 FD FC \n" +
                "F8 F3 00 FD FA FD FE 00 FE 01 02 0C 03 FB 00 FD \n" +
                "00 03 04 02 03 17 00 00 00 FE FA 02 F8 E6 FD FE \n" +
                "F9 F5 08 0A 06 F7 FC FB FD F8 01 FF FB FF FE 00 \n" +
                "00 22 29 F8 FA FF FF FF FD F0 F6 0B F7 FD FC FC \n" +
                "FF FC 05 FD 03 FE FF 00 FF 01 04 02 00 F3 FC FF";
        String[] imuSplitted = imu.split(" \n");
        String[] emgSplitted = emg.split(" \n");
        if (imuSplitted.length == emgSplitted.length ){
            for (int index = 0; index < imuSplitted.length; index ++) {
                String emgLine = emgSplitted[index];
                String imuLine = imuSplitted[index];
                byte[] emgData = hexLineToByteArray(emgLine);
                byte[] imuData = hexLineToByteArray(imuLine);
                add(emgData, imuData);
            }
        }
    }

    public static byte[] hexLineToByteArray(String s) {
        s.trim();
        String pattern="[\\s]";
        String replace="";
        Pattern p= Pattern.compile(pattern);
        Matcher m=p.matcher(s);
        s = m.replaceAll(replace);
        Log.e(TAG, "string is: " + s);
        return hexStringToByteArray(s);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        Log.e(TAG, "length: " + len);
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public boolean isEmpty(){
        return imuDataRecords.isEmpty() && emgDataRecords.isEmpty();
    }

    public int findPose(byte[] emgData, byte[] imuData){
        DataRecord record = new DataRecord(emgData, imuData);
        for (int index = 0; index < myoDataRecords.size(); index++){
            DataRecord savedRecord = myoDataRecords.get(index);
            if (savedRecord.isSimilar(0.07f, record)){
                return index;
            }
        }
        return -1;
    }

    public  MyoGesture getPose(byte[] emgData, byte[] imuData){
        int index = findPose(emgData, imuData);
        MyoGesture gesture;
        switch (index) {
            case 0 :
                gesture =  MyoGesture.FRAME;
                break;
            case 1 :
                gesture =  MyoGesture.HOLD_HAND;
                break;
            case 2:
                gesture = MyoGesture.FIST;
                break;
            case 3:
                gesture =  MyoGesture.SWIPE_DOWN;
                break;
            case 4:
                gesture =  MyoGesture.TAP;
                break;
            case 5:
                gesture =  MyoGesture.SWIPE_RIGHT;
                break;
            case 6:
                gesture = MyoGesture.SWIPE_LEFT;
                break;
            default:
                gesture =  MyoGesture.UNKNOWN;
        }
        lastGestures.add(gesture);
        return gesture;
    }

    public MyoGesture getAveragedGesture(int threshold, byte[] emgData, byte[] imuData) {
        getPose(emgData, imuData);
        if (threshold > lastGestures.size()){
            threshold = lastGestures.size();
        }
        for (MyoGesture gesture :  MyoGesture.values()) {
            if (numberOf(gesture) >= threshold){
                return gesture;
            }
        }
        return MyoGesture.UNKNOWN;
    }

    public  int numberOf(MyoGesture testGesture){
        int counter = 0;
        for (MyoGesture savedGesture : lastGestures){
            if (savedGesture == testGesture){
                counter++;
            }
        }
        Log.i("gestures", "did counted: " + counter + "of gesture: " +testGesture.name());
        return  counter;
    }
}
