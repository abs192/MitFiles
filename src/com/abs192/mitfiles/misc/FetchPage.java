package com.abs192.mitfiles.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.abs192.mitfiles.MainActivity;
import com.abs192.mitfiles.frags.FragHome;
import com.abs192.mitfiles.R;

public class FetchPage extends AsyncTask<String, Void, String> {

	private FragHome frag;
	Context context;
	static int oldp, newp;

	public FetchPage(FragHome frag) {
		this.frag = frag;
		this.context = this.frag.getActivity();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		this.frag.pb.setVisibility(View.VISIBLE);
		FragHome.searching = true;
	}

	@Override
	protected void onPostExecute(String result) {

		if (!result.equals("error")) {
			FragHome.searching = false;
			this.frag.pb.setVisibility(View.INVISIBLE);
			ParseData pd = new ParseData(result);

			ArrayList<String> data = new ArrayList<String>();
			ArrayList<String> url = new ArrayList<String>();

			data = pd.getData();
			url = pd.getUrl();
			frag.lv.removeAllViewsInLayout();

			populateList(data, url);
			try {
				((MainActivity) this.frag.getActivity()).setTitle("\t"
						+ title(FragHome.presentAddress));
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		} else {
			frag.error(1);
			FragHome.searching = false;
			this.frag.pb.setVisibility(View.INVISIBLE);
		}
	}

	private String title(String p) {

		String[] A = p.split("/");
		return ((A[A.length - 1]).replaceAll("%20", " ").toUpperCase(Locale
				.getDefault()));
	}

	private void populateList(ArrayList<String> data, ArrayList<String> url) {

		ArrayList<RowItem> rowItems = new ArrayList<RowItem>();

		if (data != null && url != null) {

			for (int i = 0; i < data.size(); i++) {
				String d = data.get(i).trim().replaceAll("%20", " ");
				RowItem item = new RowItem(1, getImage(d), d, url.get(i),
						checkIfOfflinePresent(FragHome.presentAddress
								+ url.get(i)));
				rowItems.add(item);
			}

			CustomListViewAdapter adapter = new CustomListViewAdapter(context,
					R.layout.listitem, rowItems);
			frag.lv.setAdapter(adapter);
		}
	}

	private boolean checkIfOfflinePresent(String p) {
		File SDCardRoot = Environment.getExternalStorageDirectory();
		p = p.replaceAll("%20", " ");
		File f = new File(SDCardRoot, p.replace(
				"http://resource.mitfiles.com/", "/MITFILES/"));

		if (!f.isDirectory() && f.exists())
			return true;

		return false;
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

	@Override
	protected String doInBackground(String... arg0) {

		try {
			String url = arg0[0].trim();
			Log.i("A", url);
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = null;
			try {
				response = httpClient.execute(httpGet, localContext);
			} catch (ClientProtocolException e1) {
				e1.printStackTrace();
				return "error";
			} catch (IOException e1) {
				e1.printStackTrace();
				return "error";
			} catch (Exception e) {
				e.printStackTrace();

				Toast t = Toast.makeText(context, e.toString(),
						Toast.LENGTH_SHORT);
				t.setGravity(Gravity.CENTER, 0, 0);
				t.show();
				return "error";
			}
			String result = "";

			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
				return "error";
			} catch (IOException e1) {
				e1.printStackTrace();
				return "error";
			} catch (NullPointerException e) {
				e.printStackTrace();
				return "error";
			}

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {

					result += line + "\n";

				}
			} catch (IOException e) {
				e.printStackTrace();
				return "error";
			}

			return result;
		} catch (RuntimeException rte) {
			rte.printStackTrace();
			return "error";
		}

	}
}
