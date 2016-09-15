package de.uni_luebeck.imis.gestures.myo.CustomGestures;
/**
 * Created by naoki on 15/04/13.
 * 
 * This class is List of Myo Commands, allowing to
 * [https://github.com/thalmiclabs/myo-bluetooth].
 *
 */
 
public class MyoCommandList {
    private byte[] send_bytes_data;

    public byte[] sendUnsetData() {
        byte command_data = (byte) 0x01;
        byte payload_data = (byte) 3;
        byte emg_mode     = (byte) 0x00;
        byte imu_mode     = (byte) 0x00;
        byte class_mode   = (byte) 0x00;
        send_bytes_data   =
                new byte[]{command_data, payload_data, emg_mode, imu_mode, class_mode};

        return send_bytes_data;
    }

    public byte[] sendVibration3() {
        byte command_vibrate = (byte) 0x03;
        byte payload_vibrate = (byte) 1;
        byte vibrate_type = (byte) 0x03;
        send_bytes_data = new byte[]{command_vibrate, payload_vibrate, vibrate_type};

        return send_bytes_data;
    }

    /*
    /// IMU modes.
typedef enum {
    myohw_imu_mode_none        = 0x00, ///< Do not send IMU data or events.
    myohw_imu_mode_send_data   = 0x01, ///< Send IMU data streams (accelerometer, gyroscope, and orientation).
    myohw_imu_mode_send_events = 0x02, ///< Send motion events detected by the IMU (e.g. taps).
    myohw_imu_mode_send_all    = 0x03, ///< Send both IMU data streams and motion events.
    myohw_imu_mode_send_raw    = 0x04, ///< Send raw IMU data streams.
} myohw_imu_mode_t;



    /// EMG modes.
    typedef enum {
        myohw_emg_mode_none         = 0x00, ///< Do not send EMG data.
                myohw_emg_mode_send_emg     = 0x02, ///< Send filtered EMG data.
                myohw_emg_mode_send_emg_raw = 0x03, ///< Send raw (unfiltered) EMG data.
    } myohw_emg_mode_t;*/

    public byte[] sendEmgAndImu() {
        byte command_data = (byte) 0x01;
        byte payload_data = (byte) 3;
        byte emg_mode     = (byte) 0x02;
        byte imu_mode     = (byte) 0x01;
        byte class_mode   = (byte) 0x00;
        send_bytes_data   =
                new byte[]{command_data, payload_data, emg_mode, imu_mode, class_mode};

        return send_bytes_data;
    }

    public byte[] sendEmgOnly() {
        byte command_data = (byte) 0x01;
        byte payload_data = (byte) 3;
        byte emg_mode     = (byte) 0x02;
        byte imu_mode     = (byte) 0x00;
        byte class_mode   = (byte) 0x00;
        send_bytes_data   =
                new byte[]{command_data, payload_data, emg_mode, imu_mode, class_mode};

        return send_bytes_data;
    }

    public byte[] sendUnLock() {
        byte command_unlock = (byte) 0x0a;
        byte payload_unlock = (byte) 1;
        byte unlock_type = (byte) 0x01;
        send_bytes_data = new byte[]{command_unlock, payload_unlock, unlock_type};

        return send_bytes_data;
    }

    public byte[] sendUnSleep() {
        byte command_sleep_mode = (byte) 0x09;
        byte payload_unlock = (byte) 1;
        byte never_sleep = (byte) 1;
        send_bytes_data = new byte[]{command_sleep_mode, payload_unlock, never_sleep};

        return send_bytes_data;
    }

    public byte[] sendNormalSleep() {
        byte command_sleep_mode = (byte) 0x09;
        byte payload_unlock = (byte) 1;
        byte normal_sleep = (byte) 0;
        send_bytes_data = new byte[]{command_sleep_mode, payload_unlock, normal_sleep};

        return send_bytes_data;
    }
}
