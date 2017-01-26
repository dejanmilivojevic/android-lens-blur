/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.content.SharedPreferences;
import android.util.Base64;
import android.view.SurfaceHolder;

public class BasicGLWallpaper extends GLWallpaperService {

    private static final String TAG = "MainActivity";

	@Override
	public Engine onCreateEngine() {
		return new BasicGLEngine();
	}


	class BasicGLEngine extends GLWallpaperService.GLEngine {

        private DepthRenderer renderer;

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			setEGLContextClientVersion(2);

			Base64Wrapper wrapper = new Base64Wrapper(){
				@Override
				public byte[] decode(byte[] data)
				{
					if(data != null)
						return Base64.decode(data, Base64.DEFAULT);
					else
						return null;
				}

				@Override
				public byte[] encode(byte[] data)
				{
					if(data != null)
						return Base64.encode(data, Base64.DEFAULT);
					else
						return null;
				}
			};

			SharedPreferences settings = getSharedPreferences("DisplayImage", 0);
			String stringArray = settings.getString("myByteArray", null);
			byte[] image = Base64.decode(stringArray, Base64.DEFAULT);

			JPEG jpgImg = new JPEG(image, wrapper);
			DepthImage img = DepthImage.loadImage(jpgImg);
            renderer = new DepthRenderer(img.getColourBitmap(), img.getDepthBitmap(), getResources());
			setRenderer(renderer);
		}
	}
}
