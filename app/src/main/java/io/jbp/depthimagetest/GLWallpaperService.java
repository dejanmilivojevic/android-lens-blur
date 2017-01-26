/*
 * Copyright (C) 2011-2012 Learn OpenGL ES
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jbp.depthimagetest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public abstract class GLWallpaperService extends WallpaperService {

	public class GLEngine extends Engine implements SensorEventListener {
		class GLWallpaperSurfaceView extends GLSurfaceView {

			GLWallpaperSurfaceView(Context context) {
				super(context);

				Log.d(TAG, "GLWallpaperSurfaceView(" + context + ")");
			}

			@Override
			public SurfaceHolder getHolder() {
				Log.d(TAG, "getHolder(): returning " + getSurfaceHolder());

				return getSurfaceHolder();
			}

			public void onDestroy() {
				Log.d(TAG, "onDestroy()");

				super.onDetachedFromWindow();
			}

		}

        private static final String TAG = "GLEngine";

        private GLWallpaperSurfaceView glSurfaceView;
        private boolean rendererHasBeenSet;


        private DepthRenderer renderer;
        private SensorManager sensors;
        private Sensor rotationSensor;

        private void startSensors()
        {
            if (rotationSensor != null)
                sensors.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        private void stopSensors()
        {
            sensors.unregisterListener(this);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {

        }

        @Override
        public void onSensorChanged(SensorEvent event)
        {
            if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR ||
                    event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
                processRot(event);
        }

        private void processRot(SensorEvent ev)
        {
            float x = ev.values[0];
            float y = ev.values[1];
            float z = ev.values[2];

            if (renderer != null)
                renderer.setSkew(y * 4f, x * -4f);
        }

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			Log.d(TAG, "onCreate(" + surfaceHolder + ")");

			super.onCreate(surfaceHolder);

			glSurfaceView = new GLWallpaperSurfaceView(GLWallpaperService.this);

            sensors = (SensorManager) getSystemService(SENSOR_SERVICE);
            rotationSensor = sensors.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

            for (Sensor s : sensors.getSensorList(Sensor.TYPE_ALL))
            {
                Log.v(TAG, "sensor " + s.getName() + " vendor " + s.getVendor() + " type " + s.getType());
            }

            startSensors();
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			Log.d(TAG, "onVisibilityChanged(" + visible + ")");

			super.onVisibilityChanged(visible);

			if (rendererHasBeenSet) {
				if (visible) {
					glSurfaceView.onResume();
                    startSensors();
				} else {
					glSurfaceView.onPause();
                    stopSensors();
				}
			}
		}

		@Override
		public void onDestroy() {
			Log.d(TAG, "onDestroy()");

			super.onDestroy();
			glSurfaceView.onDestroy();
		}

		protected void setRenderer(Renderer renderer) {
			Log.d(TAG, "setRenderer(" + renderer + ")");

			glSurfaceView.setRenderer(renderer);
			rendererHasBeenSet = true;

            this.renderer = (DepthRenderer) renderer;
		}

		protected void setEGLContextClientVersion(int version) {
			Log.d(TAG, "setEGLContextClientVersion(" + version + ")");

			glSurfaceView.setEGLContextClientVersion(version);
		}
	}
}
