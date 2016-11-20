package me.donaldepignosis.pomodoro.simplepomodoro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.donaldepignosis.pomodoro.R;
import me.donaldepignosis.pomodoro.dacer.settinghelper.SettingUtility;
import me.donaldepignosis.pomodoro.dacer.utils.MyPomoRecorder;
import me.donaldepignosis.pomodoro.dacer.views.WeekCirView;

/**
 * Author:dacer
 * Date  :Jul 17, 2013
 */
public class RecordFragment extends Fragment {
	private static final String KEY_CONTENT = "MainFragment:Content";
	private String mContent = "???";

	private MyPomoRecorder recorder;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
        recorder = new MyPomoRecorder(getActivity());
//        processThread();
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_record, container,
				false);
		initFont(rootView);
		initCirView(rootView);
		
		ImageButton settingButton = (ImageButton)rootView.findViewById(R.id.btn_setting);

		if(SettingUtility.isLightTheme()){
			settingButton.setImageResource(R.drawable.ic_settings_black_48dp);
			settingButton.setBackgroundColor(Color.parseColor("#ffffff"));
		}else{
			settingButton.setImageResource(R.drawable.ic_settings_white_48dp);
			settingButton.setBackgroundColor(Color.parseColor("#000000"));
		}
		settingButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
			 	intent.setClass(getActivity(), SettingActivity.class);
			 	intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			 	startActivity(intent);
			 	getActivity().finish();
			 	getActivity().overridePendingTransition(0, 0);
			}
	    });
		getThisMonthPomoNum(2013, 1);
		
		return rootView;
	}

	private void initCirView(View rootView){
		WeekCirView wcView = (WeekCirView)rootView.findViewById(R.id.weekCirView);
		
		int[] weekPomo = recorder.getPomoOfThisWeek();
		wcView.setAllColor(Color.parseColor("#cccccc"));
		
		wcView.setIndexColor(recorder.getWeekend(), Color.parseColor("#FFBB33"));
		
		float[] weekPercent = new float[7];
		for(int i=0; i<7; i++){
			weekPercent[i] = (float)weekPomo[i]/SettingUtility.getDailyGoal();
		}
		wcView.setEveryPercent(weekPercent);
	}
	
	private void initFont(View rootView){
        TextView tv_total_text = (TextView) rootView.findViewById(R.id.tv_total_text);
        TextView tv_today_text = (TextView) rootView.findViewById(R.id.tv_today_text);
        TextView tv_today = (TextView) rootView.findViewById(R.id.tv_today);
        TextView tv_total = (TextView) rootView.findViewById(R.id.tv_total_num);
        TextView tv_title = (TextView)rootView.findViewById(R.id.tv_title_record);
        Typeface roboto = Typeface.createFromAsset(getActivity()
                        .getAssets(), "fonts/Roboto-Thin.ttf");
        tv_today.setTypeface(roboto);
        tv_total.setTypeface(roboto);
        tv_title.setTypeface(roboto);
        tv_today_text.setTypeface(roboto);
        tv_total_text.setTypeface(roboto);
        
		if(SettingUtility.isLightTheme()){
		        tv_today.setTextColor(Color.BLACK);
		            tv_total.setTextColor(Color.BLACK);
		            tv_title.setTextColor(Color.BLACK);
		            tv_today_text.setTextColor(Color.BLACK);
		            tv_total_text.setTextColor(Color.BLACK);
		            rootView.setBackgroundColor(Color.WHITE);
		}
		tv_total.setText(": "+
		                String.valueOf(recorder.getTotalPomo()));
		tv_today.setText(": "+
		                String.valueOf(recorder.getTodayPomo()));
		}
			
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }

	private List<Integer> getThisMonthPomoNum(int year, int month){
		List<Integer> result = new ArrayList<Integer>();
		List<Long> startTimeList = recorder.getMonthUndeletedPomosStartTime(2013, 1);
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		for (int i=0; i<c.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
			result.add(0);
		}
		long oneDayInSec = 24*60*60;
		for (long startTime : startTimeList){
			int i = (int) ((startTime-c.getTimeInMillis()/1000)/(oneDayInSec));
			result.add(i, result.get(i)+1);
		}
		return result;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		recorder.close();
	}
	
	
    //DELETE
    private ProgressDialog pd;
    private Handler handler = new Handler() {

        @Override

        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            pd.dismiss();

        }

    };


    private void processThread() {
        pd = ProgressDialog.show(getActivity(), "请稍等",
                "正在随机创建2年内番茄记录,共1000个", true);
        new Thread() {
            @Override

            public void run() {
                //随机创建2年内的番茄记录
                for (int i = 0; i < 1000; i++) {

                    long startTime = System.currentTimeMillis() / 1000 -
                            (long) (Math.random() * (365 * 24 * 60 * 60) * 2);
                    recorder.putPomodoro(i+" ge", "", MyPomoRecorder.PomoType.POMODORO, startTime,startTime+25*60);
                }
                handler.sendEmptyMessage(0);

            }


        }.start();
    }
//DELETE-------
}
