package com.emtronics.sickbeardrunner;

import android.util.Log;
import com.emtronics.sickbeardrunner.R;

public class PythonLib {

	static CallBack callBackInterface;
	
	public PythonLib(CallBack cb,String privatePath)
	{
		callBackInterface = cb;
		System.loadLibrary("python2.7");
		System.loadLibrary("main");

		System.load(privatePath + "/lib/python2.7/site-packages/OpenSSL/rand.so");
		System.load(privatePath + "/lib/python2.7/site-packages/OpenSSL/SSL.so");
		System.load(privatePath + "/lib/python2.7/site-packages/OpenSSL/crypto.so");
		System.load(privatePath + "/lib/python2.7/lib-dynload/_ssl.so");


		System.load(privatePath + "/lib/python2.7/lib-dynload/_io.so");
		System.load(privatePath + "/lib/python2.7/lib-dynload/unicodedata.so");

		try {
			System.loadLibrary("sqlite3");
			System.load(privatePath + "/lib/python2.7/lib-dynload/_sqlite3.so");
		} catch(UnsatisfiedLinkError e) {
		}
	}

	static native public int main(String private_path, String app_path, String app_name,String[] args);

	static native public String getLog(int pos);
	static native public int getLogSize();
	static native public int keyboardInterrupt();

	public static void callBack(int info)
	{
		callBackInterface.logUpdated();
	}

	interface CallBack
	{
		void logUpdated();
	}
	
}
