/*
 * Copyright (c) 2012 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package me.donaldepignosis.pomodoro.dacer.google.task;

import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.io.IOException;

import me.donaldepignosis.pomodoro.dacer.settinghelper.SettingUtility;
import me.donaldepignosis.pomodoro.dacer.utils.GlobalContext;

/**
 * Asynchronous task that also takes care of common needs, such as displaying progress,
 * authorization, exception handling, and notifying UI when operation succeeded.
 * 
 * @author Yaniv Inbar
 * Modified by Dacer
 */
public class FragmentAsyncTask extends AsyncTask<Void, Void, Boolean> {

  final TaskListFragment mFragment;
  final com.google.api.services.tasks.Tasks client;
//  private final ProgressBar progressBar;

  FragmentAsyncTask(TaskListFragment fragment) {
	mFragment = fragment;
    client = fragment.service;
//    progressBar = (ProgressBar) fragment.rootView.findViewById(R.id.progressBar1);
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    mFragment.numAsyncTasks++;
//    progressBar.setVisibility(View.VISIBLE);
  }

  @Override
  protected final Boolean doInBackground(Void... ignored) {
    try {
      doInBackground();
      return true;
    } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
    	mFragment.showGooglePlayServicesAvailabilityErrorDialog(
          availabilityException.getConnectionStatusCode());
    } catch (UserRecoverableAuthIOException userRecoverableException) {
    	mFragment.startActivityForResult(
          userRecoverableException.getIntent(), TaskListFragment.REQUEST_AUTHORIZATION);
    } catch (IOException e) {
      Utils.logAndShow(mFragment.getActivity(), TaskListFragment.TAG, e);
    }
    return false;
  }

  @Override
  protected final void onPostExecute(Boolean success) {
    super.onPostExecute(success);
    if (0 == --mFragment.numAsyncTasks) {
    	mFragment.listView.onRefreshComplete();
//        progressBar.setVisibility(View.GONE);
    }
    if (success) {
    	mFragment.refreshView();
    }
  }

  private void doInBackground() throws IOException{
	  TaskUtils.updateDB(SettingUtility.getTaskListId(), GlobalContext.getInstance(),client);
  }

  static void run(TaskListFragment listFragment) {
    new FragmentAsyncTask(listFragment).execute();
  }
  
}
