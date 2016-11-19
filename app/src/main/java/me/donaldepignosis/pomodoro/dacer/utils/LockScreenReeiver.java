package me.donaldepignosis.pomodoro.dacer.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.donaldepignosis.pomodoro.dacer.settinghelper.SettingUtility;
import me.donaldepignosis.pomodoro.simplepomodoro.BreakActivity;
import me.donaldepignosis.pomodoro.simplepomodoro.PomoRunningActivity;

/**
 * Author:dacer
 * Date  :Mar 8, 2014
 */
public class LockScreenReeiver extends BroadcastReceiver{
	@Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null)
            return;
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
        	if(SettingUtility.isPomoRunning()){
        		Intent i = new Intent(context,PomoRunningActivity.class);
            	i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	context.startActivity(i);
        	}else if(SettingUtility.isBreakRunning()){
        		Intent i = new Intent(context,BreakActivity.class);
            	i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	context.startActivity(i);
        	}
        }
    }
}
