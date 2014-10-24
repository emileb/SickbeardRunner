
package com.emtronics.sickbeardrunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class PythonUtils {
	static boolean DEBUG = false;

	String LOG = "PythonUtils";
	public File getLibPath(Context ctx){
		return ctx.getFilesDir();
	}

	public boolean checkPythonLib(Context ctx) throws IOException, InterruptedException, ExecutionException
	{
		File libdir = getLibPath(ctx);

		boolean doExtract = false;

		String disk_version = null;
		String this_version =  Integer.toString(getVersionCode(ctx));
		File versionFile = new File (libdir,".version");

		if (versionFile.exists())
		{
			try{
				BufferedReader  reader = new BufferedReader(new FileReader(versionFile));
				disk_version = reader.readLine();
				reader.close();

				if (disk_version != null)
				{
					if (this_version.contentEquals(disk_version))
					{
						if (DEBUG) Log.d(LOG,"Versions match");
					}
					else
						doExtract = true;
				}
				else
					doExtract = true;
			}
			catch (IOException e)
			{
				if (DEBUG)Log.e(LOG,"Error reading version file: " + e.toString());
				doExtract = true;
			}
		}
		else
			doExtract = true;

		if (DEBUG) Log.d(LOG,"disk_version = " + disk_version);
		if (DEBUG) Log.d(LOG,"this_version = " + this_version);

		return doExtract;
	}

	public  int getVersionCode(Context ctx)
	{
		try{
			PackageInfo pinfo = ctx.getPackageManager().getPackageInfo("com.emtronics.sickbeardrunner", 0 );
			return pinfo.versionCode;
		} catch( PackageManager.NameNotFoundException e ){
			return -1;
		}
	}
	
	public void doPythonLibUpdate(Context ctx) throws IOException
	{
		unzipAsset(ctx, "python-core.zip", getLibPath(ctx).toString());
		File versionFile = new File (getLibPath(ctx),".version");
		String this_version = Integer.toString(getVersionCode(ctx));
		 //Wait till finised
		FileOutputStream os = new FileOutputStream(versionFile);
		os.write(this_version.getBytes());
		os.close();
	}
	
	
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}

	public void unzipAsset(Context ctx,String zipname,String dest) throws IOException
	{

		if (DEBUG) Log.d( LOG,"unzipAsset: file = " + zipname + " Dest = " + dest);

		File dest_path_file = new File(dest);
		File zipfile = new File(dest_path_file,zipname);

		AssetManager assetManager = ctx.getAssets();


		InputStream in = null;
		OutputStream out = null;
		in = assetManager.open(zipname);

		dest_path_file.mkdirs();
		out =  new FileOutputStream(zipfile);


		copyFile(in, out);
		in.close();
		in = null;
		out.flush();
		out.close();
		out = null;

		ZipFile zipFile;
		if (DEBUG) Log.d( LOG,"UNZIPPING");


		zipFile = new ZipFile(zipfile);
		Enumeration entries = zipFile.entries();
		while(entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry)entries.nextElement();
			if(entry.isDirectory()) {
				(new File(dest_path_file, entry.getName())).mkdirs();
				continue;
			}
			if (DEBUG) Log.d( LOG,"Extracting file: " + entry.getName());
			(new File(dest_path_file, entry.getName())).getParentFile().mkdirs();
			in = zipFile.getInputStream(entry);
			out =  new FileOutputStream(new File(dest_path_file, entry.getName()));
			copyFile(in,out);
		}
		//zipFile.close();



		zipfile.delete();
	}

	static public String getIpAddr(Context ctx) {
		WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();

		String ipString = String.format(
				"%d.%d.%d.%d",
				(ip & 0xff),
				(ip >> 8 & 0xff),
				(ip >> 16 & 0xff),
				(ip >> 24 & 0xff));

		return ipString;
	}
}
