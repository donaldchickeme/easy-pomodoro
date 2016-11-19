package me.donaldepignosis.pomodoro.dacer.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import me.donaldepignosis.pomodoro.R;
import me.donaldepignosis.pomodoro.dacer.service.WakeLockService;
import me.donaldepignosis.pomodoro.dacer.settinghelper.SettingUtility;
import me.donaldepignosis.pomodoro.simplepomodoro.MainActivity;

/**
 * Author:dacer
 * Date  :Jul 17, 2013
 */
public class SetMyAlarmManager {
	
	
	//Set a alarm to start service at the time after "min" minute.
	public static void schedulService(Context mContext,int min, Class<?> cls){  
		PendingIntent mAlarmSender = PendingIntent.getService(mContext,  
                0, new Intent(mContext, cls), 0);  
        long finishTimeInMills = MyUtils.getCurrentUTCInMIlls() + min*60000;
        // Schedule the alarm!  
        AlarmManager am = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);  
        
        if(SettingUtility.isXiaomiMode()||(SettingUtility.isTick()&& SettingUtility.isPomoRunning())){
        	am.set(AlarmManager.RTC, finishTimeInMills, mAlarmSender);
        	mContext.startService(new Intent(mContext, WakeLockService.class));
        }else{
            am.set(AlarmManager.RTC_WAKEUP, finishTimeInMills, mAlarmSender);
            MyNotification mn = new MyNotification(mContext);
            mn.showSimpleNotification(mContext.getString(R.string.sp_is_running),
            		mContext.getString(R.string.click_to_return), true,
            		MainActivity.class);
        }
        SettingUtility.setFinishTimeInMills(finishTimeInMills);
		
    }  
	
	public static void stopschedulService(Context mContext, Class<?> cls){ 
			MyNotification n = new MyNotification(mContext);
			n.cancelNotification();
			MyUtils.autoStopWakelockService(mContext);
	        PendingIntent mAlarmSender = PendingIntent.getService(mContext,  
	                0, new Intent(mContext, cls), 0);  
	        // cancel the alarm.  
	        AlarmManager am = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);  
	        am.cancel(mAlarmSender);  
	        SettingUtility.setRunningType(SettingUtility.NONE_RUNNING);
	        SettingUtility.setFinishTimeInMills(0);
	    }  
}
