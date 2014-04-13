package com.abs192.mitfiles.frags;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abs192.mitfiles.MainActivity;
import com.abs192.mitfiles.R;
import com.abs192.mitfiles.misc.CustomListViewAdapter;
import com.abs192.mitfiles.misc.CustomListViewAdapter.ViewHolder;
import com.abs192.mitfiles.misc.FetchPage;
import com.abs192.mitfiles.misc.InternetCheck;
import com.abs192.mitfiles.misc.OfflineManager;
import com.abs192.mitfiles.misc.RowItem;
import com.abs192.mitfiles.misc.logtheshit;
import com.actionbarsherlock.app.SherlockFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

public class FragHome extends SherlockFragment implements OnItemClickListener,
		OnItemLongClickListener {

	private static final String MITFILES_ERROR_0 = "MITFILES.ERROR.0";
	private static final String MITFILES_ERROR_1 = "MITFILES.ERROR.1";
	public static boolean searching = false;
	public ProgressBar pb;
	public static String presentAddress;
	public ListView lv;
	AlertDialog.Builder dialogBuilder = null;
	int poker_face = 0;

	public FragHome() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fraghome, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		try {

			lv = (ListView) getView().findViewById(R.id.listview);
			lv.setOnItemClickListener(this);
			lv.setOnItemLongClickListener(this);
			pb = (ProgressBar) getView().findViewById(R.id.pB);
			pb.setVisibility(View.INVISIBLE);

			String url = getArguments().getString("url");
			if (!url.equals("null")) {

				if (url.contains("http://resource.mitfiles.com/"))
					url = url.replaceAll(" ", "%20");
				else if (url.contains("http:/resource.mitfiles.com/"))
					url = url.replaceAll(" ", "%20").replaceFirst(
							"http:/resource.mitfiles.com/",
							"http://resource.mitfiles.com/");

				if (!url.endsWith("/")) {
					String A[];
					A = url.split("/");
					int l = A.length;

					String s = "";

					for (int i = 3; i < l - 1; i++) {
						s = s + A[i] + "/";
					}

					url = "http://resource.mitfiles.com/" + s.trim();
				}

				presentAddress = url;
				refresh();
			} else {

				if (presentAddress == null || lv.getCount() == 0) {
					presentAddress = new logtheshit(getSherlockActivity())
							.getDefaultAdd();
					if (new logtheshit(getSherlockActivity()).getBranch()
							.equals("None"))
						selectOnePlease();
					else
						refresh();
				}

			}

			EasyTracker tracker = EasyTracker
					.getInstance(getSherlockActivity());
			tracker.set(Fields.SCREEN_NAME, "Fragment Home");
			tracker.send(MapBuilder.createAppView().build());

		} catch (Exception e) {
			e.printStackTrace();
			presentAddress = new logtheshit(getSherlockActivity())
					.getDefaultAdd();
			refresh();
		}
	}

	@SuppressLint("NewApi")
	private void selectOnePlease() {
		try {
			AlertDialog.Builder builder;
			// pull a dialog to choose
			if (Build.VERSION.SDK_INT >= 11)
				builder = new AlertDialog.Builder(getSherlockActivity());
			else
				builder = new AlertDialog.Builder(getSherlockActivity());
			builder.setTitle("Department: ");
			builder.setCancelable(true);
			builder.setItems(getResources().getStringArray(R.array.branch),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {

							String c = getResources().getStringArray(
									R.array.branch)[item];
							new logtheshit(getSherlockActivity()).setBranch(c);
							refresh();
							return;
						}
					});
			AlertDialog alert = builder.create();
			alert.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void goback() {

		if (!searching) {

			if (presentAddress.equals("http://resource.mitfiles.com/"))
				getSherlockActivity().finish();
			else {

				String saveException = presentAddress;
				String A[];
				A = presentAddress.split("/");
				int l = A.length;

				String s = "";

				for (int i = 3; i < l - 1; i++) {
					s = s + A[i] + "/";
				}

				presentAddress = "http://resource.mitfiles.com/" + s.trim();
				try {
					if (InternetCheck.haveInternet(getSherlockActivity())) {
						new FetchPage(this).execute(presentAddress);
					} else {
						error(0);
						presentAddress = saveException;
					}

				} catch (Exception e) {
					e.printStackTrace();
					presentAddress = saveException;
				}
			}
		} else {
			Toast.makeText(getSherlockActivity(), "Please wait... ",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void refresh() {
		if (!searching) {
			if (InternetCheck.haveInternet(getSherlockActivity())) {
				new FetchPage(this).execute(presentAddress.trim());
			} else
				error(0);

		} else {
			Toast.makeText(getSherlockActivity(), "Please wait... ",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
		if (!searching) {

			ViewHolder holder = (ViewHolder) v.getTag();
			String sel = holder.txtTitle.getText().toString();
			String surl = holder.url.toString();
			boolean offline = holder.offlineStatus;
			if (surl != null && surl.equals(MITFILES_ERROR_0)) {
				if (InternetCheck.haveInternet(getSherlockActivity())) {

					refresh();

				} else {
					try {
						int rev_poker = reversePokerFace();
						RowItem r = (RowItem) lv.getItemAtPosition(0);
						r.setImageId(rev_poker);
						holder.imageView.setImageResource(rev_poker);

					} catch (Exception e) {

					}
					InternetCheck.showNoConnectionDialog(getSherlockActivity(),
							0);
				}

				return;
			} else if (surl != null && surl.equals(MITFILES_ERROR_1)) {
				if (InternetCheck.haveInternet(getSherlockActivity())) {

					refresh();

				} else {
					try {
						int rev_poker = reversePokerFace();
						RowItem r = (RowItem) lv.getItemAtPosition(0);
						r.setImageId(rev_poker);
						holder.imageView.setImageResource(rev_poker);

					} catch (Exception e) {

					}
					InternetCheck.showNoConnectionDialog(getSherlockActivity(),
							1);
				}
				return;
			} else if (sel.endsWith("/")) {

				try {
					if (InternetCheck.haveInternet(getSherlockActivity())) {

						sel = (sel.trim()).replaceAll(" ", "%20");
						presentAddress = presentAddress + sel;
						new FetchPage(this).execute(presentAddress.trim());
					} else
						InternetCheck.showNoConnectionDialog(
								getSherlockActivity(), 0);

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (sel.equals("Parent Directory")) {

				if (InternetCheck.haveInternet(getSherlockActivity()))
					goback();
				else
					InternetCheck.showNoConnectionDialog(getSherlockActivity(),
							0);
				return;

			} else {

				if (offline) {

					// open in offline mitfiles
					MainActivity act = (MainActivity) getSherlockActivity();
					act.getIntent().putExtra("FORCE", "FOLDERFORCE");
					act.getIntent().putExtra("url",
							presentAddress.replaceAll("%20", " ") + sel);
					act.change();

				} else {
					final String fsel = sel;
					if (offline) {
						final CharSequence[] items = {
								"View in Offline MITFILES",
								"Open in other apps" };
						final String url = presentAddress + holder.url;
						AlertDialog.Builder builder = new AlertDialog.Builder(
								getSherlockActivity());
						builder.setCancelable(true);
						builder.setItems(items,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int item) {

										if (item == 0) {
											MainActivity act = (MainActivity) getSherlockActivity();
											act.getIntent().putExtra("FORCE",
													"FOLDERFORCE");
											act.getIntent()
													.putExtra("url", url);
											act.change();
										} else if (item == 1) {
											callAppChooser(fsel);
										}
										return;
									}
								});
						AlertDialog alert = builder.create();
						alert.show();

					} else {

						final CharSequence[] items = {
								"Download to Offline MITFILES",
								"Open in other apps" };
						final String url = presentAddress + holder.url;
						AlertDialog.Builder builder = new AlertDialog.Builder(
								getSherlockActivity());
						builder.setCancelable(true);
						builder.setItems(items,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int item) {

										if (item == 0) {
											(new OfflineManager())
													.downloadAndSave(
															getSherlockActivity(),
															url);
										} else if (item == 1) {
											callAppChooser(fsel);
										}
										return;
									}
								});
						AlertDialog alert = builder.create();
						alert.show();
					}

				}
			}
		}
	}

	public Intent openExludingApp(String packageNameToExclude, String text) {
		List<Intent> targetedShareIntents = new ArrayList<Intent>();
		List<ResolveInfo> resInfo = getSherlockActivity().getPackageManager()
				.queryIntentActivities(createShareIntent(text), 0);
		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				Intent targetedShare = createShareIntent(text);

				if (!info.activityInfo.packageName
						.equalsIgnoreCase(packageNameToExclude)) {
					targetedShare.setPackage(info.activityInfo.packageName);
					targetedShareIntents.add(targetedShare);
				}
			}

			Intent chooserIntent = Intent.createChooser(
					targetedShareIntents.remove(0), "Select app to open");
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
					targetedShareIntents.toArray(new Parcelable[] {}));
			return chooserIntent;
		}
		return null;
	}

	private Intent createShareIntent(String sel) {
		sel = (sel.trim()).replaceAll(" ", "%20");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(android.net.Uri.parse(presentAddress + sel));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return intent;
	}

	protected void callAppChooser(String sel) {
		Intent intent = null;
		intent = openExludingApp(getSherlockActivity().getPackageName(), sel);
		startActivity(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		lv.invalidate();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (!searching) {
			ViewHolder holder = (ViewHolder) arg1.getTag();
			String A = holder.txtTitle.getText().toString();
			String surl = holder.url.toString();
			boolean offline = holder.offlineStatus;

			if (A.endsWith("/")) {
				// make a menu dialog
				final CharSequence[] items = {
						"\nSelect this as the default folder\n(This folder will be opened on every restart)\n",
						"Reset to IV Sem folder" };
				final String url = presentAddress + holder.url;
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getSherlockActivity());
				builder.setTitle("Change Default URL: " + url);
				builder.setCancelable(true);
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						if (item == 0)
							new logtheshit(getSherlockActivity())
									.setDefaultAdd(url);
						else
							new logtheshit(getSherlockActivity())
									.setDefaultAdd("http://resource.mitfiles.com/CSE/II%20year/IV%20sem/");

						return;
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				return true;
			} else if (surl != null && surl.equals(MITFILES_ERROR_0)) {

			} else if (surl != null && surl.equals(MITFILES_ERROR_1)) {

			} else {
				if (offline) {

					final CharSequence[] items = { "View in Offline MITFILES" };
					final String url = presentAddress + holder.url;
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getSherlockActivity());
					builder.setCancelable(true);
					builder.setItems(items,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {

									if (item == 0) {
										MainActivity act = (MainActivity) getSherlockActivity();
										act.getIntent().putExtra("FORCE",
												"FOLDERFORCE");
										act.getIntent().putExtra("url", url);
										act.change();
									}
									return;
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
					return true;

				} else {
					final CharSequence[] items = { "Save to Offline MITFILES" };
					final String url = presentAddress + holder.url;
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getSherlockActivity());
					builder.setCancelable(true);
					builder.setItems(items,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {

									if (item == 0) {
										(new OfflineManager()).downloadAndSave(
												getSherlockActivity(), url);
									}
									return;
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onStop() {
		super.onStop();
		try {
			// EasyTracker.getInstance(getSherlockActivity()).activityStop(getSherlockActivity());
		} catch (Exception e) {

		}
	}

	public void error(int i) {

		int[] err = { R.drawable.ic_error_left, R.drawable.ic_error_right,
				R.drawable.ic_error_right };

		if (lv.getCount() != 0) {

			if (i == 0) {
				InternetCheck.showNoConnectionDialog(getSherlockActivity(), 0);
				return;
			} else if (i == 1) {
				InternetCheck.showNoConnectionDialog(getSherlockActivity(), 1);
				return;
			}

		} else {

			try {
				String errorText = null;
				RowItem r;
				switch (i) {
				case 1:
					errorText = "LOST NETWORK CONNECTION";
					poker_face = err[new Random().nextInt(err.length)];
					r = new RowItem(1, poker_face, errorText, MITFILES_ERROR_0,
							false);

					break;

				default:
					errorText = "CAN'T CONNECT TO THE INTERNET";
					poker_face = err[new Random().nextInt(err.length)];
					r = new RowItem(1, poker_face, errorText, MITFILES_ERROR_0,
							false);
					break;
				}

				try {
					lv.removeAllViewsInLayout();
				} catch (Exception e) {
					e.printStackTrace();
				}

				ArrayList<RowItem> rowItems = new ArrayList<RowItem>();

				rowItems.add(r);

				CustomListViewAdapter adapter = new CustomListViewAdapter(
						getActivity(), R.layout.listitem, rowItems);
				lv.setAdapter(adapter);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private int reversePokerFace() {
		if (poker_face == 0)
			return 0;
		if (poker_face == R.drawable.ic_error_left) {
			return poker_face = R.drawable.ic_error_right;
		} else {
			return poker_face = R.drawable.ic_error_left;
		}
	}

}
