package me.donaldepignosis.pomodoro.dacer.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;

import me.donaldepignosis.pomodoro.R;
import me.donaldepignosis.pomodoro.dacer.settinghelper.SettingUtility;
import me.donaldepignosis.pomodoro.simplepomodoro.MainActivity;


/**
 * Created by Dacer on 10/3/13.
 * For Mi phone's. Prevent phone from sleeping.
 * Need to destroy it manually.
 * And auto play tick by setting.
 */
public class WakeLockService extends Service {
    private PowerManager.WakeLock wl;
    private MediaPlayer mediaPlayer;
    
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate(){
//        LogRecorder.record("---WakelockService started---");
        init();
    }

    public void onDestroy (){
//        LogRecorder.record("---WakelockService destroyed---");
        if(wl.isHeld()){
            wakeRelase();
        }
        stopTick();
    }

    private void init(){
        //Wake Lock
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SimplePomodoro");
        wakeLock();


        //Put service to foreground
        String tickerText = "";
        Intent notificationIntent = new Intent(this, MainActivity.class);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setTicker(tickerText).setWhen(System.currentTimeMillis())
                .setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, 0))
                .setContentTitle(getString(R.string.sp_is_running))
                .setContentText(getString(R.string.click_to_return));
        startForeground(22, builder.build());


        //Play tick and disable Ticking in Break Duration
        if(SettingUtility.isTick() && !SettingUtility.isBreakRunning()){
            playTick();
        }
//        if (SettingUtility.isBreakRunning()){
//        	stopSelf();
//        }
    }

    private void wakeLock(){
        wl.acquire();
//        LogRecorder.record("---Wake Locked---");
    }

    private void wakeRelase(){
        wl.release();
//        LogRecorder.record("---Wake Released---");
    }

    private void playTick() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tick);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void stopTick() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
