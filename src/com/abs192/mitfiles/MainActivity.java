package com.abs192.mitfiles;

import java.util.ArrayList;
import java.util.Stack;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.abs192.mitfiles.frags.FragFolder;
import com.abs192.mitfiles.frags.FragHome;
import com.abs192.mitfiles.frags.FragSettings;
import com.abs192.mitfiles.frags.FragWebsis;
import com.abs192.mitfiles.misc.NavDrawerItem;
import com.abs192.mitfiles.misc.NavDrawerListAdapter;
import com.abs192.mitfiles.misc.logtheshit;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;

public class MainActivity extends SherlockFragmentActivity {

	private String[] drawerListItems = { "Files", "Websis", "Offline MITFILES",
			"Settings" };
	private int[] drawerIconItems = { R.drawable.ic_home, R.drawable.ic_mu,
			R.drawable.ic_my_folder, R.drawable.ic_settings };
	String data = "null";

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private CharSequence mTitle;
	private CharSequence mDrawerTitle;
	private ActionBarDrawerToggle mDrawerToggle;
	private static int position;
	Stack<Integer> stackFrags;
	private NavDrawerListAdapter adapter;
	private ArrayList<NavDrawerItem> navDrawerItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		GoogleAnalytics.getInstance(this).setDryRun(true);

		stackFrags = new Stack<Integer>();

		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		mDrawerLayout.setScrimColor(Color.parseColor("#ccbebebe"));

		int rdrawer = 0;
		if (Build.VERSION.SDK_INT >= 11)
			rdrawer = R.layout.nav_drawer_list;
		else
			rdrawer = R.layout.drawer_list_item_old;

		// mDrawerList.setAdapter(new ArrayAdapter<String>(this, rdrawer,
		// drawerListItems));

		navDrawerItems = new ArrayList<NavDrawerItem>();
		for (int i = 0; i < drawerListItems.length; i++) {
			navDrawerItems.add(new NavDrawerItem(drawerListItems[i],
					drawerIconItems[i]));
		}
		adapter = new NavDrawerListAdapter(this, rdrawer, navDrawerItems);
		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		// enable ActionBar app icon to behave as action to toggle nav drawer

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {

			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		change();
	}

	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

	public void change() {
		Bundle b = getIntent().getExtras();
		if (b == null) {
			Log.i("MAIN TAG", "bundle null");
			int a = new logtheshit(getApplicationContext()).getDefaultPage();
			selectItem(a);
		} else {
			String a = b.getString("FORCE");
			Log.i("MAIN TAG", "force " + a);
			data = b.getString("url");
			Log.i("MAIN TAG", "data= " + data);
			if (a != null && a.equals("FOLDERFORCE")) {
				selectItem(2);
			} else if (a != null && a.equals("HOMEFORCE")) {
				selectItem(0);
			}
		}
		invalidateOptionsMenu();
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			invalidateOptionsMenu();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	public static void setPosition(int pos) {
		position = pos;
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.refresh).setVisible(!drawerOpen && position == 0);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.

		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.refresh:
			try {
				((FragHome) getSupportFragmentManager().findFragmentByTag(
						"HOME")).refresh();
			} catch (Exception e) {
				e.printStackTrace();

			}
			return true;
		case android.R.id.home:

			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class DrawerItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			if (pos == position)
				mDrawerLayout.closeDrawer(mDrawerList);
			else
				selectItem(pos);
		}
	}

	public void selectItem(int position) {
		String TAG = "HOME";
		setPosition(position);
		pushhhinStack(position);
		Log.i("selectTAG", "stack:" + stackFrags.toString());
		Fragment fragment;

		switch (position) {
		case 0:
			fragment = new FragHome();
			TAG = "HOME";
			break;
		case 1:
			fragment = new FragWebsis();
			TAG = "WEBSIS";
			break;

		case 2:
			fragment = new FragFolder();
			TAG = "FOLDER";
			break;
		case 3:
			fragment = new FragSettings();
			TAG = "SETTINGS";
			break;
		default:
			fragment = null;
			TAG = "NULL";
			break;
		}

		Bundle args = new Bundle();
		Log.i("SELECT ITEM DATA", data);
		args.putString("url", data);
		fragment.setArguments(args);

		FragmentManager fragmentManager = getSupportFragmentManager();

		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment, TAG).commit();

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(drawerListItems[position]);
		mDrawerLayout.closeDrawer(mDrawerList);

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {

			if (mDrawerLayout.isDrawerOpen(mDrawerList))
				mDrawerLayout.closeDrawer(mDrawerList);
			else
				mDrawerLayout.openDrawer(mDrawerList);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mDrawerLayout.isDrawerOpen(mDrawerList))
				mDrawerLayout.closeDrawer(mDrawerList);
			else
				back();
			return true;

		} else {

			return super.onKeyDown(keyCode, event);
		}
	}

	private void pushhhinStack(int pos) {

		while (stackFrags.contains(pos))
			stackFrags.removeElement(pos);

		stackFrags.push(pos);

	}

	private void back() {
		Log.i("BackTAG", "start" + stackFrags.toString());
		try {
			if (stackFrags.size() == 1) {
				finish();
			} else {
				stackFrags.pop();
				int pos = stackFrags.pop();
				selectItem(pos);
			}
		} catch (Exception e) {
			finish();
		}
		Log.i("BackTAG", "end" + stackFrags.toString());
	}

}
