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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.io.IOException;

import me.donaldepignosis.pomodoro.R;

/**
 * Asynchronous task that also takes care of common needs, such as displaying progress,
 * authorization, exception handling, and notifying UI when operation succeeded.
 * 
 * @author Yaniv Inbar
 */
abstract class CommonAsyncTask extends AsyncTask<Void, Void, Boolean> {

  final TaskListFragment mFragment;
  final com.google.api.services.tasks.Tasks client;
  private final ListView listView;
  private final ImageButton refreshBtn;
  private final ProgressBar progressBar;

  CommonAsyncTask(TaskListFragment fragment) {
	mFragment = fragment;
    client = fragment.service;
    listView = (ListView) fragment.rootView.findViewById(R.id.list_task);
    refreshBtn = (ImageButton) fragment.rootView.findViewById(R.id.btn_add_task);
    progressBar = (ProgressBar) fragment.rootView.findViewById(R.id.progressBar1);
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    mFragment.numAsyncTasks++;
    refreshBtn.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
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
//      listView.onRefreshComplete();
        refreshBtn.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
    if (success) {
    	mFragment.refreshView();
    }
  }

  abstract protected void doInBackground() throws IOException;
}
