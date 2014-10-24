package com.emtronics.sickbeardrunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.emtronics.pythoncommon.PythonServiceAIDL;
import com.emtronics.pythoncommon.PythonServiceListener;


public class PythonService extends Service implements PythonLib.CallBack  {

	private static String LOG = "PythonService";

	/** For showing and hiding our notification. */
	NotificationManager mNM;
	Notification notification;
	PendingIntent contentIntent;

	private List<PythonServiceListener> listeners = new ArrayList<PythonServiceListener>();

	PythonLib pythonLib;

	PythonThread mainThread;

	PythonService this_;

	String appPath;
	String appName;
	String appArgs;

	@Override
	public void onCreate() {
		this_ = this;
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);	

		/*
		notification = new Notification();
		notification.icon = R.drawable.sickbeardmaster;
		notification.tickerText = "Sickbeard starting up.";
		notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		
		startForeground(123, notification);
*/
		
		notification = new Notification();
		notification.icon = R.drawable.sickbeardmaster;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		Intent i=new Intent(this, PythonService.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		PendingIntent pi=PendingIntent.getActivity(this, 0,i, 0);
		notification.setLatestEventInfo(this, "Sickbeard Service Alive","",	pi);
		startForeground(123, notification);
		
		Intent notificationIntent = new Intent("com.emtronics.powerznb");
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (PythonUtils.DEBUG)  Log.d(LOG, "onStartCommand");

		return START_STICKY;
	}

	private final PythonServiceAIDL.Stub apiEndpoint = new PythonServiceAIDL.Stub() {

		@Override
		public int getLogSize() throws RemoteException {
			if (pythonLib!=null)
				return PythonLib.getLogSize();
			else 
				return 0;
		}

		@Override
		public int startThread(String path, String pyname, String args) throws RemoteException {
			appPath = path;
			appName = pyname;
			appArgs = args;

			startPythonThread();
			return 0;
		}

		@Override
		public int exit() throws RemoteException {
			mNM.cancel(1);
			System.exit(0);
			return 0;
		}

		@Override
		public String getLog(int arg0) throws RemoteException {
			if (pythonLib!=null)
				return PythonLib.getLog(arg0);
			else return "";
		}

		@Override
		public int keyboardInterrupt() throws RemoteException {
			if (PythonUtils.DEBUG) Log.d(LOG, "keyboardInterrupt");
			if (pythonLib!=null)
				PythonLib.keyboardInterrupt();
			return 0;
		}

		@Override
		public void addListener(PythonServiceListener listener)
				throws RemoteException {
			synchronized (listeners) {
				listeners.add(listener);
			}
		}

		@Override
		public void removeListener(PythonServiceListener listener)
				throws RemoteException {
			synchronized (listeners) {
				listeners.remove(listener);
			}
		}

		@Override
		public String getIPAddress() throws RemoteException {
			return PythonUtils.getIpAddr(this_) + ":8081";
		}

		@Override
		public int getThreadRunning() throws RemoteException {
			if (mainThread == null)
				return 0;
			else
				return 1;
		}

	};  

	private void startPythonThread()
	{
		if (PythonUtils.DEBUG) Log.d(LOG,"startThread");
		if (mainThread == null)
		{
			mainThread = new PythonThread();
			mainThread.start();   
		}
		else
			if (PythonUtils.DEBUG) Log.e(LOG,"startThread; Thread not null, not starting");

	}


	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		mNM.cancel(1);
	}



	@Override
	public IBinder onBind(Intent intent) {
		if (PythonService.class.getName().equals(intent.getAction())) {
			if (PythonUtils.DEBUG) Log.d(LOG, "Bound by intent " + intent);
			return apiEndpoint;
		} else {
			return null;
		}
	}
	class PythonThread extends Thread {

		public void run () {



			PythonUtils pu = new PythonUtils();
			try {

				if (pu.checkPythonLib(this_))
				{
					updateMessage("Extracting Python...");
					pu.doPythonLibUpdate(this_);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			String libdir = pu.getLibPath(this_).toString();


			if (pythonLib==null)
				pythonLib = new PythonLib(this_, libdir);

			//notification.when = System.currentTimeMillis();
			//notification.setLatestEventInfo(this_, "Sickbeard", "Running"  , contentIntent);
			//mNM.notify(1, notification);
			
			
			int restart;
			do{
				String[] args = appArgs.split(" ");
				updateMessage("Sickbeard Running...");
				restart = PythonLib.main(libdir,appPath,appName,args);
				Log.d(LOG,"THREAD ENDED restart=" + restart);
			}  while(restart==1);
			mNM.cancel(1);
			mainThread = null;
			updateMessage("Sickbeard Stopped");
			logUpdated();
		}
	}    


	private void updateMessage(String msg)
	{
		for (PythonServiceListener l: listeners)
		{
			try {
				l.updateMessage(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void logUpdated() {
		//if (PythonUtils.DEBUG) Log.d(LOG, "logUpdated");
		Iterator<PythonServiceListener> i = listeners.iterator();
		while (i.hasNext()) {
			PythonServiceListener l = i.next(); // must be called before you can call i.remove()
			try {
				l.updateLog();
			} catch (RemoteException e) {
				//Remove from list if error
				i.remove();
			}
		}
	}
}