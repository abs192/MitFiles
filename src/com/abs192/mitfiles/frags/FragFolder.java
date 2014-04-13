package com.abs192.mitfiles.frags;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.abs192.mitfiles.R;
import com.abs192.mitfiles.misc.CustomListViewAdapter;
import com.abs192.mitfiles.misc.CustomListViewAdapter.ViewHolder;
import com.abs192.mitfiles.misc.RowItem;
import com.abs192.mitfiles.misc.logtheshit;
import com.actionbarsherlock.app.SherlockFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

public class FragFolder extends SherlockFragment implements
		OnItemClickListener, OnItemLongClickListener {

	public ProgressBar pb;
	public static File presentFile;
	public ListView lv;
	File SDCardRoot = Environment.getExternalStorageDirectory();
	String TAG = "Offline Folder";

	public FragFolder() {
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
		Log.i(TAG, "onStart");
		try {
			lv = (ListView) getView().findViewById(R.id.listview);
			lv.setOnItemClickListener(this);
			lv.setOnItemLongClickListener(this);
			pb = (ProgressBar) getView().findViewById(R.id.pB);
			pb.setVisibility(View.INVISIBLE);

			String url = getArguments().getString("url");
			Log.i(TAG, "url" + url);
			if (!url.equals("null")) {

				if (url.contains("http://resource.mitfiles.com/"))
					url = url.replaceAll("%20", " ").replaceFirst(
							"http://resource.mitfiles.com/", "/MITFILES/");
				else if (url.contains("http:/resource.mitfiles.com/"))
					url = url.replaceAll("%20", " ").replaceFirst(
							"http:/resource.mitfiles.com/", "/MITFILES/");

				presentFile = new File(SDCardRoot, url);
				Log.i(TAG,
						"url not null presentfile= "
								+ presentFile.getAbsolutePath());
			} else {

				String def = new logtheshit(getSherlockActivity())
						.getDefaultAdd();
				Log.i(TAG, "url null def=" + def);
				if (def.contains("http://resource.mitfiles.com/"))
					def = def.replaceAll("%20", " ").replaceFirst(
							"http://resource.mitfiles.com/", "/MITFILES/");
				else if (def.contains("http:/resource.mitfiles.com/"))
					def = def.replaceAll("%20", " ").replaceFirst(
							"http:/resource.mitfiles.com/", "/MITFILES/");

				presentFile = new File(SDCardRoot, def);
				Log.i(TAG,
						"url null presentfile= "
								+ presentFile.getAbsolutePath());

			}

			checkFiles();
			Log.i(TAG, "presentFile = " + presentFile.getAbsolutePath() + "");
			refresh();

			EasyTracker tracker = EasyTracker
					.getInstance(getSherlockActivity());
			tracker.set(Fields.SCREEN_NAME, "Fragment Offline MITFILES");
			tracker.send(MapBuilder.createAppView().build());

		} catch (Exception e) {
			e.printStackTrace();
			String def = new logtheshit(getSherlockActivity()).getDefaultAdd();
			if (def.contains("http://resource.mitfiles.com/"))
				def = def.replaceAll("%20", " ").replaceFirst(
						"http://resource.mitfiles.com/", "/MITFILES/");
			else if (def.contains("http:/resource.mitfiles.com/"))
				def = def.replaceAll("%20", " ").replaceFirst(
						"http:/resource.mitfiles.com/", "/MITFILES/");

			presentFile = new File(SDCardRoot, def);
			refresh();
		}

	}

	private void checkFiles() throws IOException {
		Log.i(TAG, "checkFiles");
		File directory = new File(presentFile.getAbsolutePath());
		directory.mkdirs();

	}

	private void delete(File file) {
		if (file.isDirectory()) {
			String fileList[] = file.list();
			if (fileList.length == 0) {
				System.out.println("Deleting Directory : " + file.getPath());
				file.delete();
			} else {
				int size = fileList.length;
				for (int i = 0; i < size; i++) {
					String fileName = fileList[i];
					String fullPath = file.getPath() + "/" + fileName;
					File fileOrFolder = new File(fullPath);
					delete(fileOrFolder);
				}
			}
		} else {
			file.delete();
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View v, int arg2,
			long arg3) {

		ViewHolder holder = (ViewHolder) v.getTag();
		String sel = holder.txtTitle.getText().toString();

		if (sel.endsWith("/")) {
			final CharSequence[] items = { "Delete Folder. " + sel
					+ "\n[NOTE: Deletes all files and folders inside]" };
			final File pr = new File(presentFile,
					holder.url.replace("%20", " "));
			AlertDialog.Builder builder = new AlertDialog.Builder(
					getSherlockActivity());
			builder.setCancelable(true);
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					if (item == 0) {
						if (pr.exists()) {
							do {
								delete(pr);
								refresh();
							} while (pr.exists());
						}
					}
					return;
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
			return true;
		} else {
			final CharSequence[] items = { "Delete File: " + sel };
			final File pr = new File(presentFile,
					holder.url.replace("%20", " "));
			AlertDialog.Builder builder = new AlertDialog.Builder(
					getSherlockActivity());
			builder.setCancelable(true);
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					if (item == 0) {
						if (pr.exists()) {
							do {
								delete(pr);
								refresh();
							} while (pr.exists());
						}
					}
					return;
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
			return true;

		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {

		ViewHolder holder = (ViewHolder) v.getTag();
		String sel = holder.txtTitle.getText().toString();

		if (sel.endsWith("/")) {

			presentFile = new File(presentFile, "" + sel.trim());
			refresh();

		} else if (sel.equals("Parent Directory")) {

			presentFile = presentFile.getParentFile();
			refresh();

		} else {

			File file = new File(presentFile.getAbsolutePath() + File.separator
					+ sel);
			Log.i(TAG, "file:" + file.getAbsolutePath());
			if (sel.endsWith(".txt")) {

				try {

					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setDataAndType(Uri.fromFile(file), "text/plain");
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
					return;

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (sel.endsWith(".pdf")) {
				try {

					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file), "application/pdf");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					startActivity(intent);
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {

				try {

					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file),
							getMyMimeTechnique(sel));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					startActivity(intent);
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

	}

	private String getMyMimeTechnique(String sel) {
		if (sel.endsWith(".jpg") || sel.endsWith(".jpeg")
				|| sel.endsWith(".png"))
			return "image/*";
		else if (sel.endsWith(".docx") || (sel.endsWith(".doc")))
			return "application/msword";
		else if (sel.endsWith(".ppt") || (sel.endsWith(".pptx")))
			return "application/vnd.ms-powerpoint";
		else if (sel.endsWith(".xls") || (sel.endsWith(".xlt")))
			return "application/vnd.ms-excel";
		else if (sel.endsWith("zip"))
			return "application/zip";
		return "application/"
				+ sel.substring(sel.length() - 4, sel.length() - 1);
	}

	private void refresh() {
		Log.i(TAG, "refreshing.. " + presentFile.getAbsolutePath());
		ArrayList<RowItem> rowItems = new ArrayList<RowItem>();

		if (!presentFile.isDirectory()) {
			presentFile = presentFile.getParentFile();
			Log.i(TAG, "not a directory");
			Log.i(TAG, presentFile.getAbsolutePath());
		}
		if (!(presentFile.exists()))
			Log.i(TAG, "doesnot exist");

		if (!(presentFile.getName().equals("MITFILES"))) {
			Log.i(TAG, "parent not mitfiles :)");
			String s = "Parent Directory";
			rowItems.add(new RowItem(2, getImage(s), s, s, false));
		} else {
			Log.i(TAG, "parent is mitfiles :(");
		}

		String[] data = presentFile.list();
		if (data != null) {
			Arrays.sort(data);
			for (String d : data) {
				File f = new File(presentFile, d);

				if (f.isDirectory())
					d = d + "/";
				RowItem item = new RowItem(2, getImage(d), d, d.replaceAll(" ",
						"%20"), false);
				rowItems.add(item);
				Log.i(TAG, d + "added");
			}
		} else
			Log.i(TAG, "list: empty");

		CustomListViewAdapter adapter = new CustomListViewAdapter(
				getSherlockActivity(), R.layout.listitem, rowItems);
		lv.setAdapter(adapter);

	}

	private int getImage(String s) {

		if (s.equals("Parent Directory")) {
			return R.drawable.ic_up;
		} else if (s.endsWith(".txt")) {
			return R.drawable.ic_txt;
		} else if (s.endsWith(".pdf")) {
			return R.drawable.ic_pdf;
		} else if (s.endsWith(".docx") || (s.endsWith(".doc"))) {
			return R.drawable.ic_word;
		} else if (s.endsWith(".html")) {
			return R.drawable.ic_html;
		} else if (s.endsWith(".jpeg") || (s.endsWith(".png"))
				|| (s.endsWith(".jpg"))) {
			return R.drawable.ic_pic;
		} else if (s.endsWith(".ppt") || s.endsWith(".pptx")) {
			return R.drawable.ic_ppt;
		} else if (s.endsWith("/")) {
			return R.drawable.ic_folder;
		}

		return R.drawable.ic_uk;
	}
}
