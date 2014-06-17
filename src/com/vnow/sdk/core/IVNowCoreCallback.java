/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\workspace\\vnow\\VNowSDKCore\\src\\com\\vnow\\sdk\\core\\IVNowCoreCallback.aidl
 */
package com.vnow.sdk.core;
public interface IVNowCoreCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.vnow.sdk.core.IVNowCoreCallback
{
private static final java.lang.String DESCRIPTOR = "com.vnow.sdk.core.IVNowCoreCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.vnow.sdk.core.IVNowCoreCallback interface,
 * generating a proxy if needed.
 */
public static com.vnow.sdk.core.IVNowCoreCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.vnow.sdk.core.IVNowCoreCallback))) {
return ((com.vnow.sdk.core.IVNowCoreCallback)iin);
}
return new com.vnow.sdk.core.IVNowCoreCallback.Stub.Proxy(obj);
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
case TRANSACTION_eventCallback:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.eventCallback(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.vnow.sdk.core.IVNowCoreCallback
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
@Override public void eventCallback(java.lang.String strEvent, java.lang.String strResult) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(strEvent);
_data.writeString(strResult);
mRemote.transact(Stub.TRANSACTION_eventCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_eventCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void eventCallback(java.lang.String strEvent, java.lang.String strResult) throws android.os.RemoteException;
}
