/*
 * This code is in the public domain.
 */

package com.yotadevices.fbreader;

interface WidgetInterface {
	void reset();
	void repaint();
	void onPreferencesUpdate(String book);
	void startForeground();
	void stopForeground();
}
