package shetj.me.base.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

import timber.log.Timber;

/**
 * 重力感应
 */
public class ScreenSwitchUtils {

    private volatile static ScreenSwitchUtils mInstance;
    private SensorManager sm;
    private OrientationSensorListener listener;
    private Sensor sensor;
    private int oldState = 0;

    public class ChangeOrientationHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 888) {
                int orientation = msg.arg1;
                if (orientation > 45 && orientation < 135) {
                    if (oldState != 1) {
                        oldState = 1;
                        Timber.i("横屏翻转: ");
                    }
                } else if (orientation > 135 && orientation < 225) {
                    if (oldState != 2) {
                        oldState = 2;
                        Timber.i("竖屏翻转: ");
                    }
                } else if (orientation > 225 && orientation < 315) {
                    if (oldState != 3) {
                        oldState = 3;
                        Timber.i("横屏: ");
                    }
                } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                    if (oldState != 4) {
                        oldState = 4;
                        Timber.i("竖屏: ");
                    }
                }
            }
            super.handleMessage(msg);
        }
    }

    /**
     * 返回ScreenSwitchUtils单例
     **/
    public static ScreenSwitchUtils init(Context context) {
        if (mInstance == null) {
            synchronized (ScreenSwitchUtils.class) {
                if (mInstance == null) {
                    mInstance = new ScreenSwitchUtils(context);
                }
            }
        }
        return mInstance;
    }

    private ScreenSwitchUtils(Context context) {
        // 注册重力感应器,监听屏幕旋转
        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener = new OrientationSensorListener(new ChangeOrientationHandler());
    }

    /**
     * 开始监听
     */
    public void start() {
        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
    }


    /**
     * 停止监听
     */
    public void stop() {
        sm.unregisterListener(listener);
    }

    /**
     * 重力感应监听者
     */
    public class OrientationSensorListener implements SensorEventListener {
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;
        public static final int ORIENTATION_UNKNOWN = -1;
        private ChangeOrientationHandler rotateHandler;

        public OrientationSensorListener(ChangeOrientationHandler handler) {
            rotateHandler = handler;
        }

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;
            if (magnitude * 4 >= Z * Z) {
                // 屏幕旋转时
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - (int) Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
            if (rotateHandler != null) {
                rotateHandler.obtainMessage(888, orientation, 0).sendToTarget();
            }
        }
    }
}