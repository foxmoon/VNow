package com.nyist.vnow.utils;

import android.util.Log;
import android.view.MotionEvent;

public class LogTag {
	public static boolean DEBUG = true;

	public static void tag(String message){
		if(DEBUG){
			Log.i("other", message);	
		}
	}
	
	public static void e(String methodName, Object msg){
		if(DEBUG){
			Log.e(methodName, "========" + msg + "=============");	
		}
	}
	
	public static void i(String methodName, String msg){
		if(DEBUG){
			Log.i(methodName, "========" + msg + "=============");	
		}
	}
	
	public static void w(String methodName, String msg){
		if(DEBUG){
			Log.w(methodName, "========" + msg + "=============");	
		}
	}
	public static void d(String methodName, String msg){
		if(DEBUG){
			Log.d(methodName, "========" + msg + "=============");	
		}
	}
	
	public static void sysout(Throwable e){
		if(DEBUG){
			if(e!=null){
				e.printStackTrace();
			}	
		}
	}

	public static void log(Object msg){
		if(DEBUG){
			System.out.println(msg);	
		}
	}
	
	public static void debugMotionEvent(String tag,MotionEvent ev){
		switch (ev.getAction()&MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			i(tag, "ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
			i(tag, "ACTION_MOVE");
			break;
		case MotionEvent.ACTION_CANCEL:
			i(tag, "ACTION_CANCEL");
			break;
		case MotionEvent.ACTION_UP:
			i(tag, "ACTION_UP");
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			//已经有一个点被触摸，第二个点之后的触摸，会是这个action
//			int index = (action& MotionEvent.ACTION_POINTER_INDEX_MASK)>>MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			int index = ev.getActionIndex();
			i(tag, "ACTION_POINTER_DOWN  index=="+index);
			
			break;
		case MotionEvent.ACTION_POINTER_UP:
			//当一个点up，依然还有一个点在触摸屏幕，则会触发这个事件。否则，触发UP
			int upIndex = ev.getActionIndex();
			i(tag, "ACTION_POINTER_DOWN  index=="+upIndex);
			break;
		default:
			break;
		}
	}
	
}
