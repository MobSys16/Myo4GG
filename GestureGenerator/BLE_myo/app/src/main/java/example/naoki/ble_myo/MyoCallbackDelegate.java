package example.naoki.ble_myo;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by finnfahrenkrug on 09.09.16.
 */
public interface MyoCallbackDelegate {
    public void didSetCharacteristicCommand(BluetoothGattCharacteristic characteristicCommand);
}
