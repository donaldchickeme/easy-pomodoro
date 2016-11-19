package me.donaldepignosis.pomodoro.simplepomodoro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import me.donaldepignosis.pomodoro.R;
import me.donaldepignosis.pomodoro.dacer.service.BreakFinishService;
import me.donaldepignosis.pomodoro.dacer.settinghelper.SettingUtility;
import me.donaldepignosis.pomodoro.dacer.utils.GlobalContext;
import me.donaldepignosis.pomodoro.dacer.utils.MyScreenLocker;
import me.donaldepignosis.pomodoro.dacer.utils.MyUtils;
import me.donaldepignosis.pomodoro.dacer.utils.SetMyAlarmManager;


/**
 * Author:dacer
 * Date  :Jul 17, 2013
 */
public class BreakActivity extends Activity {
	private ProgressBar mProgressBar;
	private TextView tvTime;
	private int maxBreakDuration;
	private int longBreakDuration;
	TextView longBreakTV;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GlobalContext.setActivity(this);
		//preference
		final Window win = getWindow();
		Typeface roboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
		maxBreakDuration = SettingUtility.getBreakDuration();
        longBreakDuration = SettingUtility.getLongBreakDuration();
        if(SettingUtility.isFastMode()){
        	MyScreenLocker locker = new MyScreenLocker(this);
            locker.myLockNow();
        }
        if(SettingUtility.isLightsOn()){
        	win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        setTheme(SettingUtility.getTheme());
		setContentView(R.layout.activity_break);
		
      if(MyUtils.getContinueTimes(this)%4 == 0 && MyUtils.getContinueTimes(this) != 0){
    	TextView longBreakTV = (TextView)findViewById(R.id.tv_long_break);
    	longBreakTV.setTypeface(roboto);
    	longBreakTV.setVisibility(View.VISIBLE);
    	maxBreakDuration = longBreakDuration; // Long break
    }
        tvTime = (TextView)findViewById(R.id.tv_time);
		mProgressBar = (ProgressBar)findViewById(R.id.pb_time);
		
		tvTime.setTypeface(roboto);
		showContinueView();
 		  
		  win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
		    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//		    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

//  	startService(new Intent(this,ScreenLockerService.class));
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
//		stopService(new Intent(this,ScreenLockerService.class));
	}

	@Override
	protected void onPause(){
		super.onPause();
        SettingUtility.setRunningType(SettingUtility.BREAK_RUNNING);
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//exit confirm dialog
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        		AlertDialog d = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.stop_pomodoro))
                .setMessage(getString(R.string.do_you_wish_to_stop))
                .setPositiveButton(R.string.running_in_background, new OnClickListener() {
        			
        			@Override
        			public void onClick(DialogInterface dialog, int which) {
        				// TODO Auto-generated method stub
        				Intent intent = new Intent(Intent.ACTION_MAIN);
//        				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        				intent.addCategory(Intent.CATEGORY_HOME);
        				BreakActivity.this.startActivity(intent);
        			}
        		})
                .setNegativeButton(R.string.stop, new OnClickListener() {
        			
        			@Override
        			public void onClick(DialogInterface dialog, int which) {
        				// TODO Auto-generated method stub
        				SetMyAlarmManager.stopschedulService(
        						BreakActivity.this, BreakFinishService.class);
        				startActivity(new Intent(BreakActivity.this,
        						MainActivity.class));
        				finish();
        			}
        		})
              	.create();
        		d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        		d.show();
        	return false;
        }
        return false;
    }
	
	
	private void showContinueView(){
		mProgressBar.setMax(maxBreakDuration * 60);
		int nowProcess = 0;
		final long leftTimeInMills;
		
		long finishTime = SettingUtility.getFinishTimeInMills();
		long nowTime = MyUtils.getCurrentUTCInMIlls();
		int runningType = SettingUtility.getRunningType();
		if((finishTime > nowTime) &&(runningType == SettingUtility.BREAK_RUNNING)){
			leftTimeInMills = finishTime - nowTime + 1000;
			nowProcess = (int) (maxBreakDuration*60 - leftTimeInMills/1000);
			mProgressBar.setProgress(nowProcess);
		}else {
	        SettingUtility.setRunningType(SettingUtility.BREAK_RUNNING);
			SetMyAlarmManager.schedulService(this,
					maxBreakDuration, 
					BreakFinishService.class);
			leftTimeInMills = (long)maxBreakDuration*60*1000+1000;
		}

		
		SettingUtility.setRunningType(SettingUtility.BREAK_RUNNING);
		new CountDownTimer(leftTimeInMills, 1000) {
       	 	int min, sec, passedSec;
	        String secStr; 
 		    @Override
			public void onTick(long millisUntilFinished) {
 	            min = (int) (millisUntilFinished / 60000);
 	            sec = (int) ((millisUntilFinished - min *60000)/1000);
 	            if (sec < 10){
 	            	secStr = "0"+String.valueOf(sec);
 	            }else{
 	            	secStr = String.valueOf(sec);
 	            }
 	            tvTime.setText(min+":"+secStr);
 	           passedSec = (int) (maxBreakDuration*60 - (millisUntilFinished/1000));
 	            mProgressBar.setProgress(passedSec);
 		    	
 		    }

 		    @Override
			public void onFinish() {
 		    	finish();
 		    }
 		  }.start();
	}
}
