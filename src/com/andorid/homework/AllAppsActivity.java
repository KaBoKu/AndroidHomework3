package com.andorid.homework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


import com.android.homework.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class AllAppsActivity extends ListActivity {
	private PackageManager packageManager = null;
	private List<ApplicationInfo> applist = null;
	private ApplicationAdapter listadaptor = null;
	private HashMap<ApplicationInfo, Float > appRating= null;
	private SharedPreferences sh = null;
	private SharedPreferences.Editor preferencesEditor = null;
	public static final String MY_PREFERENCES = "myPreferences";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		packageManager = getPackageManager();
		new LoadApplications().execute();
		
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = true;

		switch (item.getItemId()) {
		case R.id.menu_about: {
			displayAboutDialog();

			break;
		}
		default: {
			result = super.onOptionsItemSelected(item);

			break;
		}
		}

		return result;
	}
	
	
	
	private void displayAboutDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.about_title));
		
		builder.setItems(new CharSequence[]
	            {getString(R.string.sort_lex), getString(R.string.sortuj_lex_desc), getString(R.string.sort_ranked)
				, getString(R.string.sort_ranked_desc)},
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    // The 'which' argument contains the index position
	                    // of the selected item
	                	sh = getApplication().getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
	                	preferencesEditor = sh.edit();
	                    switch (which) {
	                        case 0:
	                        	listadaptor.sortLex();
	                        	preferencesEditor.putInt("sort_type", 1);
	                        	preferencesEditor.commit();
	                        	dialog.cancel();
	                            break;
	                        case 1:
	                            listadaptor.sortLexDesc();
	                            preferencesEditor.putInt("sort_type", 2);
	                            preferencesEditor.commit();
	                            dialog.cancel();
	                            break;
	                        case 2:
	                            listadaptor.sortRating();
	                            preferencesEditor.putInt("sort_type", 3);
	                            preferencesEditor.commit();
	                            dialog.cancel();
	                            break;
	                        case 3:
	                            listadaptor.sortRaingDesc();
	                            preferencesEditor.putInt("sort_type", 4);
	                            preferencesEditor.commit();
	                            dialog.cancel();
	                            break;
	                    }
	                }
	            });
		
		builder.create().show();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		ApplicationInfo app = applist.get(position);
		try {
			Intent intent = packageManager
					.getLaunchIntentForPackage(app.packageName);
			if (null != intent) {
				startActivity(intent);
			}
		} catch (ActivityNotFoundException e) {
			Toast.makeText(AllAppsActivity.this, e.getMessage(),
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(AllAppsActivity.this, e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}

	private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
		ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
		for (ApplicationInfo info : list) {
			try {
				if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
					applist.add(info);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return applist;
	}

	private class LoadApplications extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress = null;
		
		@Override
		protected Void doInBackground(Void... params) {
			applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
			listadaptor = new ApplicationAdapter(AllAppsActivity.this,
					R.layout.snippet_list_row, applist);

			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			
			sh = getApplicationContext().getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
			int sortOrder = sh.getInt("sort_type", 0);
			switch(sortOrder){
			case 1:
				listadaptor.sortLex();
				break;
			case 2:
				listadaptor.sortLexDesc();
				break;
				
			case 3:
				listadaptor.sortRating();
				break;
				
			case 4:
				listadaptor.sortRaingDesc();
				break;
				
			default:
			}
			
			setListAdapter(listadaptor);
			progress.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(AllAppsActivity.this, null,
					"£adujemy!!");
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}
}