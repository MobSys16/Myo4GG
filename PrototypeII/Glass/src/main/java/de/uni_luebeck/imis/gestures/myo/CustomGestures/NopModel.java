package de.uni_luebeck.imis.gestures.myo.CustomGestures;
/**
 * Created by naoki on 15/04/16.
 */
public class NopModel implements IGestureDetectModel{
    @Override
    public void event(long time, byte[] emgData, byte[] imuData) {
    }

    @Override
    public void setAction(IGestureDetectAction action) {
    }


    @Override
    public void action() {

    }
}
