package de.uni_luebeck.imis.gestures.myo.CustomGestures;
/**
 * Created by naoki on 15/04/16.
 */
public interface IGestureDetectModel {
    public void event(long eventTime,byte[] emgData, byte[] imuData);
    public void setAction(IGestureDetectAction action);
    public void action();
}
