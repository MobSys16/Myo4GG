package de.uni_luebeck.imis.gestures.myo.CustomGestures;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by finnfahrenkrug on 09.09.16.
 */
public interface MyoCallbackDelegate {
    public void didSetCharacteristicCommand(BluetoothGattCharacteristic characteristicCommand);
}
