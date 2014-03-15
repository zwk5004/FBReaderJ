package com.yotadevices.fbreader;

import android.graphics.Bitmap;
import android.content.Intent;
// Declare the interface.

interface YotaBitmapProvider {
	void setPath(in Intent i);
	void turnPage(boolean forward);
	Bitmap getBitmap();
}
