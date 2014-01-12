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

import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.SerializerUtil;
import org.geometerplus.zlibrary.core.view.ZLView.Direction;
import org.geometerplus.zlibrary.core.view.ZLView.PageIndex;
import org.geometerplus.zlibrary.core.view.*;
import org.geometerplus.zlibrary.ui.android.view.AndroidFontUtil;

import android.app.Service;
import android.content.*;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class WidgetShadow implements ZLViewWidget, ServiceConnection {
	private Context myContext;
	private volatile WidgetInterface myInterface;
	private Runnable myOnBindAction;

	private static Runnable combined(final Runnable action0, final Runnable action1) {
		if (action0 == null) {
			return action1;
		}
		if (action1 == null) {
			return action0;
		}
		return new Runnable() {
			public void run() {
				action0.run();
				action1.run();
			}
		};
	}

	public synchronized void bindToService(Context context, Runnable onBindAction) {
		if (myInterface != null && myContext == context) {
			if (onBindAction != null) {
				onBindAction.run();
			}
		} else {
			myOnBindAction = combined(myOnBindAction, onBindAction);
			context.bindService(
				new Intent(context, FBReaderYotaService.class),
				this,
				Service.BIND_AUTO_CREATE
			);
			myContext = context;
		}
	}

	public synchronized void unbind() {
		if (myContext != null && myInterface != null) {
			try {
				myContext.unbindService(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			myInterface = null;
			myContext = null;
		}
	}

	public  void reset() {
		if (myInterface != null) {
			try {
				myInterface.reset();
			} catch (RemoteException e) {
			}
		}
	}
	
	public  void repaint() {
		if (myInterface != null) {
			try {
				myInterface.repaint();
			} catch (RemoteException e) {
			}
		}
	}
	
	public void onPreferencesUpdate(String book) {
		if (myInterface != null) {
			try {
				myInterface.onPreferencesUpdate(book);
			} catch (RemoteException e) {
			}
		}
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

	// method from ServiceConnection interface
	public synchronized void onServiceConnected(ComponentName name, IBinder service) {
		myInterface = WidgetInterface.Stub.asInterface(service);
		if (myOnBindAction != null) {
			myOnBindAction.run();
			myOnBindAction = null;
		}
	}

	// method from ServiceConnection interface
	public synchronized void onServiceDisconnected(ComponentName name) {
	}

}
