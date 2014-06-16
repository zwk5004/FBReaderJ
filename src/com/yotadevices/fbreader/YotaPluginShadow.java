/*
 * Copyright (C) 2007-2014 Geometer Plus <contact@geometerplus.com>
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

import java.util.*;

import android.content.*;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;

import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.options.Config;
import org.geometerplus.zlibrary.text.view.ZLTextFixedPosition;
import org.geometerplus.zlibrary.text.view.ZLTextPosition;
import org.geometerplus.fbreader.Paths;
import org.geometerplus.fbreader.book.*;
import org.geometerplus.android.fbreader.api.TextPosition;

public class YotaPluginShadow implements ServiceConnection {
	private Context myContext;
	private volatile YotaBitmapProvider myInterface;
	private final List<Runnable> myOnBindActions = new LinkedList<Runnable>();
	private final String myPackage;
	
	public YotaPluginShadow(String pack) {
		myPackage = pack;
	}

	private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
		}
	};

	public synchronized void bindToService(Context context, Runnable onBindAction) {
		if (myInterface != null && myContext == context) {
			if (onBindAction != null) {
				Config.Instance().runOnConnect(onBindAction);
			}
		} else {
			if (onBindAction != null) {
				myOnBindActions.add(onBindAction);
			}
			Intent i = new Intent("com.yotadevices.fbreader.YotaBitmapProvider");
			i.setPackage(myPackage);
			context.bindService(i, this, Context.BIND_AUTO_CREATE);
			myContext = context;
		}
	}

	public synchronized void unbind() {
		if (myContext != null && myInterface != null) {
			try {
				myContext.unregisterReceiver(myReceiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				myContext.unbindService(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			myInterface = null;
			myContext = null;
		}
	}

	public synchronized void setPath(Intent i) {
		if (myInterface != null) {
			try {
				myInterface.setPath(i);
			} catch (RemoteException e) {
			}
		}
	}

	public synchronized void turnPage(boolean forward) {
		if (myInterface != null) {
			try {
				myInterface.turnPage(forward);
			} catch (RemoteException e) {
			}
		}
	}
	
	public synchronized Bitmap getBitmap() {
		if (myInterface != null) {
			try {
				return myInterface.getBitmap();
			} catch (RemoteException e) {
			}
		}
		return null;
	}

	// method from ServiceConnection interface
	public synchronized void onServiceConnected(ComponentName name, IBinder service) {
		myInterface = YotaBitmapProvider.Stub.asInterface(service);
		while (!myOnBindActions.isEmpty()) {
			Config.Instance().runOnConnect(myOnBindActions.remove(0));
		}
		if (myContext != null) {
//			myContext.registerReceiver(myReceiver, new IntentFilter(LibraryService.BOOK_EVENT_ACTION));
//			myContext.registerReceiver(myReceiver, new IntentFilter(LibraryService.BUILD_EVENT_ACTION));
		}
	}

	// method from ServiceConnection interface
	public synchronized void onServiceDisconnected(ComponentName name) {
		myInterface = null;
	}
}
