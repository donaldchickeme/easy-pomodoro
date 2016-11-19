package me.donaldepignosis.pomodoro.dacer.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.donaldepignosis.pomodoro.dacer.google.task.TaskListFragment;
import me.donaldepignosis.pomodoro.simplepomodoro.MainFragment;
import me.donaldepignosis.pomodoro.simplepomodoro.RecordFragment;

/**
 * Author:dacer
 * Date  :Jul 17, 2013
 */
public class MyPagerAdapter extends FragmentPagerAdapter {

	
	public MyPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		if (position == 1) {
			Fragment fragment = new MainFragment();
			return fragment;
		} else if (position == 2) {
			Fragment fragment = new RecordFragment();
			return fragment;
		} else {
			Fragment fragment = new TaskListFragment();
			return fragment;
		}

	}

	@Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return "Title";
    }

}
