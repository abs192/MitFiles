package com.abs192.mitfiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.abs192.mitfiles.R;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

public class Cred extends SherlockActivity implements OnClickListener {

	ImageView fb_abs, fb_sev, fb_pvt, yt_sev, play;
	TextView tvMIT;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cred);
		try {

			tvMIT = (TextView) findViewById(R.id.mitfiles);

			fb_abs = (ImageView) findViewById(R.id.fb_abs);
			fb_sev = (ImageView) findViewById(R.id.fb_sev);
			fb_pvt = (ImageView) findViewById(R.id.fb_pvt);
			play = (ImageView) findViewById(R.id.play_abs);
			yt_sev = (ImageView) findViewById(R.id.yt_sev);

			fb_abs.setOnClickListener(this);
			fb_sev.setOnClickListener(this);
			fb_pvt.setOnClickListener(this);
			yt_sev.setOnClickListener(this);
			play.setOnClickListener(this);

			tvMIT.setOnClickListener(this);
			EasyTracker.getInstance(this).activityStart(this);

			ActionBar actionBar = getSupportActionBar();
			actionBar.setTitle("About");
			actionBar.setDisplayHomeAsUpEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);

		for (int i = 0; i < menu.size(); i++)
			menu.getItem(i).setVisible(false);

		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fb_abs:
			gotToFB(1);
			break;
		case R.id.fb_sev:
			gotToFB(2);
			break;
		case R.id.fb_pvt:
			gotToFB(3);
			break;
		case R.id.play_abs:
			goToPlay();
			break;
		case R.id.yt_sev:
			Intent i = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://www.youtube.com/user/pikle6893"));
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			break;
		case R.id.mitfiles:
			Intent i1 = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://resource.mitfiles.com"));
			i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i1);
			break;

		}
	}

	private void gotToFB(int i) {
		String id = " ";
		switch (i) {
		case 1:
			id = "1215773092";
			break;
		case 2:
			id = "1271412186";
			break;
		case 3:
			id = "100001399465046";
			break;
		}
		try {
			getPackageManager().getPackageInfo("com.facebook.katana", 0);
			Intent facebookPage = new Intent(Intent.ACTION_VIEW,
					Uri.parse("fb://profile/" + id));
			startActivity(facebookPage);
		} catch (Exception e) {
			Intent launchBrowser = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://facebook.com/" + id));
			startActivity(launchBrowser);
		}

	}

	private void goToPlay() {
		try {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://search?q=pub:Aditya+Shetty")));
		} catch (android.content.ActivityNotFoundException anfe) {
			startActivity(new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/developer?id=Aditya+Shetty")));
		} catch (Exception e) {
			startActivity(new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/developer?id=Aditya+Shetty")));

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;

		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onDestroy() {
		super.onStop();
		try {
			EasyTracker.getInstance(this).activityStop(this);
		} catch (Exception e) {

		}
	}

}
