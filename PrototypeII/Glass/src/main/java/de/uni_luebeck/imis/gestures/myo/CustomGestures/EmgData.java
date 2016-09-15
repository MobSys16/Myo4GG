package de.uni_luebeck.imis.gestures.myo.CustomGestures;
import android.util.Log;

import java.util.ArrayList;
import java.util.StringTokenizer;

import de.uni_luebeck.imis.gestures.myo.CustomGestures.EmgCharacteristicData;

/**
 * Created by naoki on 15/04/09.
 *
 */
 
public class EmgData {
    private ArrayList<Double> emgData = new ArrayList<Double>();
    private static final int NUMBERS_IN_EACH_LINE = 8;
    public EmgData() {
    }

    public EmgData(EmgCharacteristicData characteristicData) {
        this.emgData = new ArrayList<Double>( characteristicData.getEmg8Data_abs().getEmgArray() );
    }

    public EmgData(ArrayList<Double> emgData) {
        this.emgData = emgData;
    }

    public String getLine() {
        StringBuilder return_SB = new StringBuilder();
        for (int i_emg_num = 0; i_emg_num < NUMBERS_IN_EACH_LINE; i_emg_num++) {
            return_SB.append(String.format("%f,", emgData.get(i_emg_num)));
        }
        return return_SB.toString();
    }

    public void setLine(String line) {
        ArrayList<Double> data = new ArrayList<Double>();
        StringTokenizer st = new StringTokenizer(line , ",");
        for (int index = 0; index < NUMBERS_IN_EACH_LINE; index++) {
            data.add(Double.parseDouble(st.nextToken()));
        }
        emgData = data;
    }

    public void addElement(double element) {
        emgData.add(element);
    }

    public void setElement(int index ,double element) {
        emgData.set(index,element);
    }

    public Double getElement(int index) {
        if (index < 0 || index > emgData.size() - 1) {
            return null;
        } else {
            return emgData.get(index);
        }
    }

    public ArrayList<Double> getEmgArray() {
        return this.emgData;
    }

    public float getSimilarity(EmgData compareData){
        byte[] maximum = {  Byte.MAX_VALUE,Byte.MAX_VALUE,Byte.MAX_VALUE,Byte.MAX_VALUE,
                            Byte.MAX_VALUE,Byte.MAX_VALUE,Byte.MAX_VALUE,Byte.MAX_VALUE,
                            Byte.MAX_VALUE,Byte.MAX_VALUE,Byte.MAX_VALUE,Byte.MAX_VALUE,
                            Byte.MAX_VALUE,Byte.MAX_VALUE,Byte.MAX_VALUE,Byte.MAX_VALUE
                            };
        byte[] minimum = {  Byte.MIN_VALUE,Byte.MIN_VALUE,Byte.MIN_VALUE,Byte.MIN_VALUE,
                Byte.MIN_VALUE,Byte.MIN_VALUE,Byte.MIN_VALUE,Byte.MIN_VALUE,
                Byte.MIN_VALUE,Byte.MIN_VALUE,Byte.MIN_VALUE,Byte.MIN_VALUE,
                Byte.MIN_VALUE,Byte.MIN_VALUE,Byte.MIN_VALUE,Byte.MIN_VALUE
        };
        EmgData maxData = new EmgCharacteristicData(maximum).getEmg8Data_abs();
        EmgData minData = new EmgCharacteristicData(minimum).getEmg8Data_abs();
        //Double range = maxData.getDistanceFrom(minData);
        Double range = 359.21;
        Double distance = getDistanceFrom(compareData);
        float mappedDistance = new Float(distance / range);
        Log.i("emgdata", "mappedDistance: " + mappedDistance + ". distance: " + distance + ". range: " + range);
        return mappedDistance;
    }

    public Double getDistanceFrom(EmgData baseData) {
        Double distance = 0.00;
        for (int i_element = 0; i_element < NUMBERS_IN_EACH_LINE; i_element++) {
            double delta = Math.abs(emgData.get(i_element) - baseData.getElement(i_element));
            distance += Math.pow(delta,2.0);
        }
        return Math.sqrt(distance);
    }

    public Double getInnerProductionTo(EmgData baseData) {
        Double val = 0.00;
        for (int i_element = 0; i_element < NUMBERS_IN_EACH_LINE; i_element++) {
            val += emgData.get(i_element) * baseData.getElement(i_element);
        }
        return val;
    }

    public Double getNorm(){
        Double norm = 0.00;
        for (int i_element = 0; i_element < NUMBERS_IN_EACH_LINE; i_element++) {
            norm += Math.pow( emgData.get(i_element) ,2.0);
        }
        return Math.sqrt(norm);
    }
}
