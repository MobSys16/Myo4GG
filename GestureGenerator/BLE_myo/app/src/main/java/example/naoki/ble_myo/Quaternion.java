package example.naoki.ble_myo;

import android.util.Log;

/**
 * Created by finnfahrenkrug on 13.09.16.
 */
public class Quaternion {
        private int x;
        private int y;
        private int z;
        private int w;
        Quaternion(int x, int y, int z, int w) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getZ() {
            return z;
        }

        public void setZ(int z) {
            this.z = z;
        }

        public int getW() {
            return w;
        }

        public void setW(int w) {
            this.w = w;
        }

        public float getSimilarity(Quaternion compareQuaternion){
            /*
            /// Scale values for unpacking IMU data
#define MYOHW_ORIENTATION_SCALE   16384.0f ///< See myohw_imu_data_t::orientation
#define MYOHW_ACCELEROMETER_SCALE 2048.0f  ///< See myohw_imu_data_t::accelerometer
#define MYOHW_GYROSCOPE_SCALE     16.0f    ///< See myohw_imu_data_t::gyroscope
             */
            Float QUATERNION_RANGE = 16384.0f;
            Float[] values = {  new Float(x),
                                new Float(y),
                                new Float(z),
                                new Float(w)};
            Float[] compareValues = {
                    new Float(compareQuaternion.x),
                    new Float(compareQuaternion.y),
                    new Float(compareQuaternion.z),
                    new Float(compareQuaternion.w)
            };
            Float[] deltaValues = {0.0f, 0.0f, 0.0f, 0.0f};
            for (int index = 0; index < values.length; index++){
                float difference = values[index] - compareValues[index];
                deltaValues[index] = Math.abs(difference);
            }
            float average = 0.0f;
            for (int index = 0; index < deltaValues.length; index++){
                average += deltaValues[index] / QUATERNION_RANGE;
            }
            average /= 4.0f;
            Log.i("datarecord", "ähnlichkeit orientation: " + average);
            return average;
        }
}
