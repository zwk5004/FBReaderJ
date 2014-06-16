/*
 * Copyright (C) 2007-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package com.yotadevices.fbreader;

import android.app.Service;
import android.content.*;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import org.geometerplus.zlibrary.core.view.ZLView.Direction;
import org.geometerplus.zlibrary.core.view.ZLView.PageIndex;
import org.geometerplus.zlibrary.core.view.*;
import org.geometerplus.zlibrary.ui.android.view.AndroidFontUtil;

import org.geometerplus.fbreader.book.Book;

import org.geometerplus.android.fbreader.api.FBReaderIntents;

public class WidgetShadow implements ZLViewWidget {
	private Context myContext;
	
	public WidgetShadow(Context c) {
		myContext = c;
	}
	
	public void start() {
		Intent i = new Intent(myContext, FBReaderYotaService.class);
		myContext.startService(i);
	}


	public  void reset() {
		Intent i = new Intent(myContext, FBReaderYotaService.class);
		i.putExtra(FBReaderYotaService.WIDGET_ACTION, FBReaderYotaService.RESET);
		myContext.startService(i);
	}

	public  void repaint() {
		Intent i = new Intent(myContext, FBReaderYotaService.class);
		i.putExtra(FBReaderYotaService.WIDGET_ACTION, FBReaderYotaService.REPAINT);
		myContext.startService(i);
	}

	public void onPreferencesUpdate(Book book) {
		Intent i = new Intent(myContext, FBReaderYotaService.class);
		i.putExtra(FBReaderYotaService.WIDGET_ACTION, FBReaderYotaService.ON_PREFERENCE_UPDATE);
		FBReaderIntents.putBookExtra(i, book);
		myContext.startService(i);
	}

	public void stopForeground() {
		Intent i = new Intent(myContext, FBReaderYotaService.class);
		i.putExtra(FBReaderYotaService.WIDGET_ACTION, FBReaderYotaService.STOP_FOREGROUND);
		myContext.startService(i);
	}
	
	public void startForeground() {
		Intent i = new Intent(myContext, FBReaderYotaService.class);
		i.putExtra(FBReaderYotaService.WIDGET_ACTION, FBReaderYotaService.START_FOREGROUND);
		myContext.startService(i);
	}
	
	

	@Override
	public void startManualScrolling(int x, int y, Direction direction) {
		// TODO Auto-generated method stub
		Log.e("Widget", "startManualScrolling");
	}
	@Override
	public void scrollManuallyTo(int x, int y) {
		// TODO Auto-generated method stub
		Log.e("Widget", "scrollManuallyTo");
	}
	@Override
	public void startAnimatedScrolling(PageIndex pageIndex, int x, int y,
			Direction direction, int speed) {
		// TODO Auto-generated method stub
		Log.e("Widget", "startAnimatedScrolling1");
	}
	@Override
	public void startAnimatedScrolling(PageIndex pageIndex,
			Direction direction, int speed) {
		// TODO Auto-generated method stub
		Log.e("Widget", "startAnimatedScrolling2");
	}
	@Override
	public void startAnimatedScrolling(int x, int y, int speed) {
		// TODO Auto-generated method stub
		Log.e("Widget", "startAnimatedScrolling3");
	}
}
