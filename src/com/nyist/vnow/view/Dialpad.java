package com.nyist.vnow.view;

import java.util.HashMap;
import java.util.Map;

import com.nyist.vnow.R;

import android.content.Context;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class Dialpad extends LinearLayout implements OnClickListener {
	
	private OnDialKeyListener onDialKeyListener;
	private Vibrator mVibrator;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int s = msg.arg1;
			switch (msg.what) {
			case 1:
				dispatchDialKeyEvent(s);
				break;

			default:
				break;
			}
		}
		
	};
	
	
	private static final Map<Integer, int[]> digitsButtons = new HashMap<Integer, int[]>(){
		private static final long serialVersionUID = -6640726621906396734L;

	{
		put(R.id.button0, new int[] {ToneGenerator.TONE_DTMF_0, KeyEvent.KEYCODE_0});
		put(R.id.button1, new int[] {ToneGenerator.TONE_DTMF_1, KeyEvent.KEYCODE_1});
		put(R.id.button2, new int[] {ToneGenerator.TONE_DTMF_2, KeyEvent.KEYCODE_2});
		put(R.id.button3, new int[] {ToneGenerator.TONE_DTMF_3, KeyEvent.KEYCODE_3});
		put(R.id.button4, new int[] {ToneGenerator.TONE_DTMF_4, KeyEvent.KEYCODE_4});
		put(R.id.button5, new int[] {ToneGenerator.TONE_DTMF_5, KeyEvent.KEYCODE_5});
		put(R.id.button6, new int[] {ToneGenerator.TONE_DTMF_6, KeyEvent.KEYCODE_6});
		put(R.id.button7, new int[] {ToneGenerator.TONE_DTMF_7, KeyEvent.KEYCODE_7});
		put(R.id.button8, new int[] {ToneGenerator.TONE_DTMF_8, KeyEvent.KEYCODE_8});
		put(R.id.button9, new int[] {ToneGenerator.TONE_DTMF_9, KeyEvent.KEYCODE_9});
		put(R.id.buttonpound, new int[] {ToneGenerator.TONE_DTMF_P, KeyEvent.KEYCODE_POUND});
		put(R.id.buttonstar, new int[] {ToneGenerator.TONE_DTMF_S, KeyEvent.KEYCODE_STAR});
	}};
	
	@SuppressWarnings("unused")
	private static final Map<Integer, String> digitsNames = new HashMap<Integer, String>(){
		private static final long serialVersionUID = 185911971590818960L;

	{
		put(R.id.button0, "0");
		put(R.id.button1, "1");
		put(R.id.button2, "2");
		put(R.id.button3, "3");
		put(R.id.button4, "4");
		put(R.id.button5, "5");
		put(R.id.button6, "6");
		put(R.id.button7, "7");
		put(R.id.button8, "8");
		put(R.id.button9, "9");
		put(R.id.buttonpound, "#");
		put(R.id.buttonstar, "*");
	}};

	/**
	 * Interface definition for a callback to be invoked when a tab is triggered
	 * by moving it beyond a target zone.
	 */
	public interface OnDialKeyListener {
		
		/**
		 * Called when the user make an action
		 * @param keyCode keyCode pressed
		 * @param dialTone corresponding dial tone
		 */
		void onTrigger(int keyCode, int dialTone);
	}
	
	public Dialpad(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		if (isInEditMode()) {
			return;
		}
		inflater.inflate(R.layout.dialpad, this, true);
	}
	
	public void setOnDialKeyListener(OnDialKeyListener listener) {
		onDialKeyListener = listener;
	}
	
	private void dispatchDialKeyEvent(int buttonId) {
		if (onDialKeyListener != null) {
			if(digitsButtons.containsKey(buttonId)) {
				int[] datas = digitsButtons.get(buttonId);
				onDialKeyListener.onTrigger(datas[1], datas[0]);
			}
			
		}
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (isInEditMode()) {
			return;
		}
		for(int buttonId : digitsButtons.keySet()) {
			Button button = (Button) findViewById(buttonId);
//			button.setText(digitsNames.get(buttonId));
			button.setOnClickListener(this);
		}
	}


	@Override
	public void onClick(View v) {
		final int view_id = v.getId();
		mVibrator.vibrate(50);
		Thread t = new Thread(){
			public void run(){
				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = view_id;
				mHandler.sendMessage(msg);
			}
		};
		t.start();
//		dispatchDialKeyEvent(view_id);
	}
}
