package com.abs192.mitfiles.frags;

import java.util.Random;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abs192.mitfiles.Cred;
import com.abs192.mitfiles.R;
import com.abs192.mitfiles.misc.logtheshit;
import com.actionbarsherlock.app.SherlockFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

public class FragSettings extends SherlockFragment implements OnClickListener {

	TextView branch, page;
	RelativeLayout clear, about, rate, notifyRel;

	public FragSettings() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.settings, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		try {

			branch = (TextView) getView().findViewById(R.id.answerBranch);
			page = (TextView) getView().findViewById(R.id.answerPage);
			about = (RelativeLayout) getView().findViewById(R.id.aboutRel);
			clear = (RelativeLayout) getView().findViewById(R.id.clearRel);
			rate = (RelativeLayout) getView().findViewById(R.id.rateRel);

			branch.setOnClickListener(this);
			page.setOnClickListener(this);
			about.setOnClickListener(this);
			clear.setOnClickListener(this);
			rate.setOnClickListener(this);

			EasyTracker tracker = EasyTracker
					.getInstance(getSherlockActivity());
			tracker.set(Fields.SCREEN_NAME, "Fragment Settings");
			tracker.send(MapBuilder.createAppView().build());

			loadData();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void loadData() {

		logtheshit l = new logtheshit(getSherlockActivity());
		String set = l.getBranch();
		int a = l.getDefaultPage();
		if (set != null) {
			setBranch(set);
			setPage(a);
		}

	}

	private void setPage(int a) {
		String[] arr = getSherlockActivity().getResources().getStringArray(
				R.array.page);
		try {
			page.setText(arr[a]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setBranch(String a) {
		branch.setText(a);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.answerBranch:
			branch();
			break;
		case R.id.answerPage:
			page();
			break;

		case R.id.clearRel:
			areYouSureDialog();
			break;

		case R.id.aboutRel:
			Intent credIntent = new Intent(getSherlockActivity(), Cred.class);
			credIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(credIntent);
			break;

		case R.id.rateRel:
			rate();
			break;
		}
	}

	private void rate() {
		try {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id="
							+ getSherlockActivity().getPackageName())));
		} catch (android.content.ActivityNotFoundException anfe) {
			startActivity(new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/details?id=com.abs192.mitfiles")));
		} catch (Exception e) {
			startActivity(new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/details?id=com.abs192.mitfiles")));

		}
	}

	private void areYouSureDialog() {
		final Context context = getSherlockActivity();
		logtheshit l = new logtheshit(context);

		if (!l.isLoginDataEmpty()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					getSherlockActivity());

			builder.setTitle("Clear Login Details");
			builder.setMessage("Are you sure you want to delete this data? \nReg. No.:\t"
					+ l.getUser() + "\nDoB:\t" + l.getPass());

			builder.setPositiveButton("Yes, Delete",
					new AlertDialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							new logtheshit(context).clearLoginData();
							String clear[] = { "Cleared!", "Deleted" };
							String c = clear[new Random().nextInt(clear.length)];
							Toast.makeText(context, c, Toast.LENGTH_SHORT)
									.show();
						}
					});

			builder.setNegativeButton("No", new AlertDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {

					String c = "Cancelled";
					Toast.makeText(context, c, Toast.LENGTH_SHORT).show();
					arg0.dismiss();
				}
			});
			builder.create();
			builder.show();
		} else {
			String clear[] = { "No data found", "Already clear" };
			String c = clear[new Random().nextInt(clear.length)];
			Toast.makeText(context, c, Toast.LENGTH_SHORT).show();
		}
	}

	private void branch() {
		try {

			// pull a dialog to choose
			AlertDialog.Builder builder = new AlertDialog.Builder(
					getSherlockActivity());
			builder.setTitle("Department: ");
			builder.setCancelable(true);
			builder.setItems(getResources().getStringArray(R.array.branch),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {

							String c = getResources().getStringArray(
									R.array.branch)[item];
							branch.setText(c);
							new logtheshit(getSherlockActivity()).setBranch(c);
							return;
						}
					});
			AlertDialog alert = builder.create();
			alert.show();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private void page() {
		try {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					getSherlockActivity());
			builder.setTitle("Default Start-up Page: ");
			builder.setCancelable(true);
			builder.setItems(getResources().getStringArray(R.array.page),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {

							String c = getResources().getStringArray(
									R.array.page)[item];
							page.setText(c);
							new logtheshit(getSherlockActivity())
									.setDefaultPage(c);
							return;
						}
					});
			AlertDialog alert = builder.create();
			alert.show();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

}
