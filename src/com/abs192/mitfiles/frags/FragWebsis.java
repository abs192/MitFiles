package com.abs192.mitfiles.frags;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.abs192.mitfiles.R;
import com.abs192.mitfiles.ResultActivity;
import com.abs192.mitfiles.misc.InternetCheck;
import com.abs192.mitfiles.misc.logtheshit;
import com.actionbarsherlock.app.SherlockFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

public class FragWebsis extends SherlockFragment implements OnClickListener,
		OnCheckedChangeListener {

	EditText user, pass;
	Button submit;
	ImageView xU, xP;
	CheckBox cb;

	public FragWebsis() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.att, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		logtheshit l = new logtheshit(getSherlockActivity());

		try {

			user = (EditText) getView().findViewById(R.id.user);
			pass = (EditText) getView().findViewById(R.id.pass);
			submit = (Button) getView().findViewById(R.id.submit);

			cb = (CheckBox) getView().findViewById(R.id.checkBox1);
			xU = (ImageView) getView().findViewById(R.id.xU);
			xP = (ImageView) getView().findViewById(R.id.xP);

			submit.setOnClickListener(this);
			xU.setOnClickListener(this);
			xP.setOnClickListener(this);

			cb.setOnCheckedChangeListener(this);
			String u = l.getUser();
			String p = l.getPass();

			if (l.getcheckBox()) {
				cb.setChecked(true);
				if (!user.equals("null")) {
					user.setText(u, BufferType.EDITABLE);
					pass.setText(p, BufferType.EDITABLE);
				}

			}

			EasyTracker tracker = EasyTracker
					.getInstance(getSherlockActivity());
			tracker.set(Fields.SCREEN_NAME, "Fragment Websis");
			tracker.send(MapBuilder.createAppView().build());
		} catch (Exception e) {

		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.xP:
			pass.setText("");
			break;
		case R.id.xU:
			user.setText("");
			break;
		case R.id.submit:
			if (user.getText().toString() != null
					&& !user.getText().toString().equals("")) {

				try {
					String userA = user.getText().toString();
					String passA = pass.getText().toString();

					if (ResultActivity.check(userA, passA)) {

						if (cb.isChecked())
							new logtheshit(getSherlockActivity()).setLoginData(
									userA, passA);

						submit.setTextColor(Color.WHITE);

						if (InternetCheck.haveInternet(getSherlockActivity())) {
							result(userA, passA);
						} else
							InternetCheck.showNoConnectionDialog(
									getSherlockActivity(), 0);
					} else {
						Toast.makeText(getSherlockActivity(),
								"Invalid username and password",
								Toast.LENGTH_SHORT).show();
						submit.setTextColor(Color.WHITE);

					}

				} catch (NullPointerException npe) {
					npe.printStackTrace();
				}

			} else {
				submit.setTextColor(Color.WHITE);
				Toast.makeText(getSherlockActivity(), "Text Fields Empty",
						Toast.LENGTH_SHORT).show();

			}
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

		new logtheshit(getSherlockActivity()).setcheckBox(arg1);
		if (arg1) {

			try {
				String u = user.getText().toString();
				String p = pass.getText().toString();
				if (ResultActivity.check(u, p)) {
					if (cb.isChecked())
						new logtheshit(getSherlockActivity())
								.setLoginData(u, p);
				}
			} catch (NullPointerException npe) {
				npe.printStackTrace();
			}

		}

	}

	public void result(String userA, String passA) {

		Intent A = new Intent(getSherlockActivity(), ResultActivity.class);
		A.putExtra("user", userA);
		A.putExtra("pass", passA);
		startActivity(A);
	}

	@Override
	public void onStop() {
		super.onStop();
		try {
			// EasyTracker.getInstance(this).activityStop(this);
		} catch (Exception e) {

		}
	}

}
