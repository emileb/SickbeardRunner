package com.emtronics.pythoncommon;

import com.emtronics.pythoncommon.PythonServiceListener;

// Declare the interface.
interface PythonServiceAIDL {
    
  
  	int getLogSize();
    String getLog(int pos);
    
    int startThread(in String path, in String pyname,in String args );
    
    int getThreadRunning();
    
    int exit();
    
    int keyboardInterrupt();
    
    String getIPAddress();
    
    void addListener(PythonServiceListener listener);
	void removeListener(PythonServiceListener listener);
}