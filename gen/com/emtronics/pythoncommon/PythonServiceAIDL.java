/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\ANDROID\\workspace\\SickbeardRunner\\aidl\\com\\emtronics\\pythoncommon\\PythonServiceAIDL.aidl
 */
package com.emtronics.pythoncommon;
// Declare the interface.

public interface PythonServiceAIDL extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.emtronics.pythoncommon.PythonServiceAIDL
{
private static final java.lang.String DESCRIPTOR = "com.emtronics.pythoncommon.PythonServiceAIDL";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.emtronics.pythoncommon.PythonServiceAIDL interface,
 * generating a proxy if needed.
 */
public static com.emtronics.pythoncommon.PythonServiceAIDL asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.emtronics.pythoncommon.PythonServiceAIDL))) {
return ((com.emtronics.pythoncommon.PythonServiceAIDL)iin);
}
return new com.emtronics.pythoncommon.PythonServiceAIDL.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getLogSize:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getLogSize();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getLog:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _result = this.getLog(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_startThread:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
int _result = this.startThread(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getThreadRunning:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getThreadRunning();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_exit:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.exit();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_keyboardInterrupt:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.keyboardInterrupt();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getIPAddress:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getIPAddress();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_addListener:
{
data.enforceInterface(DESCRIPTOR);
com.emtronics.pythoncommon.PythonServiceListener _arg0;
_arg0 = com.emtronics.pythoncommon.PythonServiceListener.Stub.asInterface(data.readStrongBinder());
this.addListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_removeListener:
{
data.enforceInterface(DESCRIPTOR);
com.emtronics.pythoncommon.PythonServiceListener _arg0;
_arg0 = com.emtronics.pythoncommon.PythonServiceListener.Stub.asInterface(data.readStrongBinder());
this.removeListener(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.emtronics.pythoncommon.PythonServiceAIDL
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public int getLogSize() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLogSize, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getLog(int pos) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(pos);
mRemote.transact(Stub.TRANSACTION_getLog, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int startThread(java.lang.String path, java.lang.String pyname, java.lang.String args) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(path);
_data.writeString(pyname);
_data.writeString(args);
mRemote.transact(Stub.TRANSACTION_startThread, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getThreadRunning() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getThreadRunning, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int exit() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_exit, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int keyboardInterrupt() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_keyboardInterrupt, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getIPAddress() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getIPAddress, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void addListener(com.emtronics.pythoncommon.PythonServiceListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_addListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void removeListener(com.emtronics.pythoncommon.PythonServiceListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_removeListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getLogSize = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getLog = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_startThread = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getThreadRunning = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_exit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_keyboardInterrupt = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getIPAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_addListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_removeListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
}
public int getLogSize() throws android.os.RemoteException;
public java.lang.String getLog(int pos) throws android.os.RemoteException;
public int startThread(java.lang.String path, java.lang.String pyname, java.lang.String args) throws android.os.RemoteException;
public int getThreadRunning() throws android.os.RemoteException;
public int exit() throws android.os.RemoteException;
public int keyboardInterrupt() throws android.os.RemoteException;
public java.lang.String getIPAddress() throws android.os.RemoteException;
public void addListener(com.emtronics.pythoncommon.PythonServiceListener listener) throws android.os.RemoteException;
public void removeListener(com.emtronics.pythoncommon.PythonServiceListener listener) throws android.os.RemoteException;
}
