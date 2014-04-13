package com.abs192.mitfiles;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

public class LinkActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Uri data = this.getIntent().getData();
		URL url;
		try {
			url = new URL(data.getScheme(), data.getHost(), data.getPath());
		} catch (MalformedURLException e) {
			try {
				url = new URL("http://resource.mitfiles.com/");
			} catch (MalformedURLException e1) {
				url = null;
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

		String u = (url.toString());
		Intent i = new Intent(this, MainActivity.class);
		String force;
		if (checkIfOfflinePresent(u)) {
			force = "FOLDERFORCE";
		} else {
			force = "HOMEFORCE";
		}
		i.putExtra("FORCE", force);
		i.putExtra("url", u.replaceAll("%20", " "));
		startActivity(i);
		this.finish();
	}

	protected void onStart() {
		super.onStart();
		try {
			EasyTracker tracker = EasyTracker.getInstance(this);
			tracker.set(Fields.SCREEN_NAME, "Open Link");
			tracker.send(MapBuilder.createAppView().build());
		} catch (Exception e) {
			e.printStackTrace();
		}

	};

	private boolean checkIfOfflinePresent(String p) {
		File SDCardRoot = Environment.getExternalStorageDirectory();
		p = p.replaceAll("%20", " ");
		File f = new File(SDCardRoot, p.replace(
				"http://resource.mitfiles.com/", "/MITFILES/"));

		if (!f.isDirectory() && f.exists())
			return true;

		return false;
	}
}
