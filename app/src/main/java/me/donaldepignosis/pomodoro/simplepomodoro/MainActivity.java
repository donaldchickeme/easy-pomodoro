package me.donaldepignosis.pomodoro.simplepomodoro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.viewpagerindicator.LinePageIndicator;

import me.donaldepignosis.pomodoro.R;
import me.donaldepignosis.pomodoro.dacer.adapters.MyPagerAdapter;
import me.donaldepignosis.pomodoro.dacer.settinghelper.SettingUtility;
import me.donaldepignosis.pomodoro.dacer.utils.GlobalContext;
import me.donaldepignosis.pomodoro.dacer.utils.MyUtils;

/**
 * Author:dacer
 * Date  :Jul 17, 2013
 */
public class MainActivity extends FragmentActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GlobalContext.setActivity(this);
		initNowRunningType();
		setTheme(SettingUtility.getTheme());
    	final Window win = getWindow();
		if(SettingUtility.isLightsOn()){
        	win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
		setContentView(R.layout.activity_main);
		
		CustomViewPager pager = (CustomViewPager)findViewById(R.id.pager);
		pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		LinePageIndicator mIndicator = (LinePageIndicator)findViewById(R.id.indicator);
		if(SettingUtility.isLightTheme()){
			mIndicator.setBackgroundColor(Color.WHITE);
		}
        mIndicator.setViewPager(pager);
        mIndicator.setCurrentItem(1);
        //for long break
        MyUtils.deleteContinueTimes(this);
    
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
    		    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
    		    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
        	return false;
        }else if (keyCode == KeyEvent.KEYCODE_MENU) { 
    	   	startActivity(new Intent(MainActivity.this, SettingActivity.class));
    	   	finish();
    	   	return false;
    	} 
        return false;
    }
	
	private void initNowRunningType(){
		long finishTime = SettingUtility.getFinishTimeInMills();
		long nowTime = MyUtils.getCurrentUTCInMIlls();
		int runningType = SettingUtility.getRunningType();
		if((finishTime > nowTime) &&(runningType != SettingUtility.NONE_RUNNING)){
			if(runningType == SettingUtility.POMO_RUNNING){
				startActivity(new Intent(MainActivity.this, PomoRunningActivity.class));
			}else if(runningType == SettingUtility.BREAK_RUNNING){
				startActivity(new Intent(MainActivity.this, BreakActivity.class));
			}
			finish();
		}
	}

}
