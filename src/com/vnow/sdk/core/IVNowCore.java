/*
 * This file is auto-generated. DO NOT MODIFY. Original file:
 * C:\\workspace\\vnow\\VNowSDKCore\\src\\com\\vnow\\sdk\\core\\IVNowCore.aidl
 */
package com.vnow.sdk.core;

/**
 * 调C的方法
 * 
 * @author harry
 * @version Creat on 2014-6-17上午9:39:55
 */
public interface IVNowCore extends android.os.IInterface
{
    /** Local-side IPC implementation stub class. */
    public static abstract class Stub extends android.os.Binder implements com.vnow.sdk.core.IVNowCore
    {
        private static final java.lang.String DESCRIPTOR = "com.vnow.sdk.core.IVNowCore";

        /** Construct the stub at attach it to the interface. */
        public Stub()
        {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an com.vnow.sdk.core.IVNowCore interface,
         * generating a proxy if needed.
         */
        public static com.vnow.sdk.core.IVNowCore asInterface(android.os.IBinder obj)
        {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof com.vnow.sdk.core.IVNowCore))) {
                return ((com.vnow.sdk.core.IVNowCore) iin);
            }
            return new com.vnow.sdk.core.IVNowCore.Stub.Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder()
        {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
        {
            switch (code)
            {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_stopService: {
                    data.enforceInterface(DESCRIPTOR);
                    this.stopService();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setEventCallback: {
                    data.enforceInterface(DESCRIPTOR);
                    com.vnow.sdk.core.IVNowCoreCallback _arg0;
                    _arg0 = com.vnow.sdk.core.IVNowCoreCallback.Stub.asInterface(data.readStrongBinder());
                    this.setEventCallback(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_dispatchApi: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    this.dispatchApi(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setRemoteVidSurface: {
                    data.enforceInterface(DESCRIPTOR);
                    android.view.Surface _arg0;
                    if ((0 != data.readInt())) {
                        _arg0 = android.view.Surface.CREATOR.createFromParcel(data);
                    }
                    else {
                        _arg0 = null;
                    }
                    this.setRemoteVidSurface(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_openLocalVideo: {
                    data.enforceInterface(DESCRIPTOR);
                    android.view.Surface _arg0;
                    if ((0 != data.readInt())) {
                        _arg0 = android.view.Surface.CREATOR.createFromParcel(data);
                    }
                    else {
                        _arg0 = null;
                    }
                    java.lang.String _arg1;
                    _arg1 = data.readString();
                    this.openLocalVideo(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_closeLocalVideo: {
                    data.enforceInterface(DESCRIPTOR);
                    this.closeLocalVideo();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_echoDelaySet: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    this.echoDelaySet(_arg0);
                    reply.writeNoException();
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements com.vnow.sdk.core.IVNowCore
        {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote)
            {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder()
            {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor()
            {
                return DESCRIPTOR;
            }

            @Override
            public void stopService() throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_stopService, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setEventCallback(com.vnow.sdk.core.IVNowCoreCallback callback) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder((((callback != null)) ? (callback.asBinder()) : (null)));
                    mRemote.transact(Stub.TRANSACTION_setEventCallback, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void dispatchApi(java.lang.String strCmd) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(strCmd);
                    mRemote.transact(Stub.TRANSACTION_dispatchApi, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setRemoteVidSurface(android.view.Surface remoteVideoSurface) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((remoteVideoSurface != null)) {
                        _data.writeInt(1);
                        remoteVideoSurface.writeToParcel(_data, 0);
                    }
                    else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_setRemoteVidSurface, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void openLocalVideo(android.view.Surface previewSurface, java.lang.String strParam) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((previewSurface != null)) {
                        _data.writeInt(1);
                        previewSurface.writeToParcel(_data, 0);
                    }
                    else {
                        _data.writeInt(0);
                    }
                    _data.writeString(strParam);
                    mRemote.transact(Stub.TRANSACTION_openLocalVideo, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void closeLocalVideo() throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_closeLocalVideo, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void echoDelaySet(java.lang.String strEchoDelayMs) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(strEchoDelayMs);
                    mRemote.transact(Stub.TRANSACTION_echoDelaySet, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_stopService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_setEventCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_dispatchApi = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
        static final int TRANSACTION_setRemoteVidSurface = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
        static final int TRANSACTION_openLocalVideo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
        static final int TRANSACTION_closeLocalVideo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
        static final int TRANSACTION_echoDelaySet = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    }

    public void stopService() throws android.os.RemoteException;

    public void setEventCallback(com.vnow.sdk.core.IVNowCoreCallback callback) throws android.os.RemoteException;

    // 传指令给c的方法
    public void dispatchApi(java.lang.String strCmd) throws android.os.RemoteException;

    // 让远程手机解析本地手机摄像头的图像
    public void setRemoteVidSurface(android.view.Surface remoteVideoSurface) throws android.os.RemoteException;

    // 打开本地视频
    public void openLocalVideo(android.view.Surface previewSurface, java.lang.String strParam) throws android.os.RemoteException;

    public void closeLocalVideo() throws android.os.RemoteException;

    public void echoDelaySet(java.lang.String strEchoDelayMs) throws android.os.RemoteException;
}
