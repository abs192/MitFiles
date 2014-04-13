package com.abs192.mitfiles.misc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

import com.abs192.mitfiles.MainActivity;
import com.abs192.mitfiles.R;

public class OfflineManager {

	Context context;
	File file;
	String stringURL, URL, title;
	PendingIntent pi;
	int previousProgress;
	NotificationManager nm;
	NotificationCompat.Builder builder = null;
	Notification n;
	private int NOTIFY_ID;
	private static int CONST_NOTIFY_ID = 11120;

	public void downloadAndSave(final Context context, String sURL) {
		this.context = context;
		NOTIFY_ID = CONST_NOTIFY_ID++;
		AsyncTask<String, Integer, Boolean> async = new AsyncTask<String, Integer, Boolean>() {

			@SuppressLint("NewApi")
			@Override
			protected Boolean doInBackground(String... arg0) {

				try {

					URL = arg0[0];
					stringURL = arg0[0].replaceAll("%20", " ");
					String[] a = stringURL.split("/");
					title = a[a.length - 1].trim();

					nm = (NotificationManager) context
							.getSystemService(Context.NOTIFICATION_SERVICE);

					Intent i = new Intent(context, MainActivity.class);
					i.putExtra("FORCE", "FOLDERFORCE");
					i.putExtra("url", stringURL);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					pi = PendingIntent.getActivity(context, 0, i,
							PendingIntent.FLAG_UPDATE_CURRENT);

					previousProgress = 0;
					builder = new NotificationCompat.Builder(context);
					builder.setContentTitle("Downloading: " + title)
							.setContentText("" + stringURL)
							.setContentIntent(pi)
							.setProgress(100, previousProgress, false)
							.setSmallIcon(R.drawable.ic_launcher)
							.setOngoing(false);
					n = builder.build();
					nm.notify(NOTIFY_ID, n);

					URL url = new URL(arg0[0]);

					// create the new connection
					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();

					// set up some things on the connection
					urlConnection.setRequestMethod("GET");
					urlConnection.setDoOutput(true);

					// and connect!
					urlConnection.connect();

					File SDCardRoot = Environment.getExternalStorageDirectory();
					file = new File(SDCardRoot, "/MITFILES/"
							+ stringURL.replaceFirst(
									"http://resource.mitfiles.com/", ""));

					File directory = new File(file.getParentFile()
							.getAbsolutePath());
					directory.mkdirs();

					if (!file.exists())
						file.createNewFile();

					// this will be used to write the downloaded data into the
					// file
					// we
					// created
					FileOutputStream fileOutput = new FileOutputStream(file);

					InputStream inputStream = urlConnection.getInputStream();

					// this is the total size of the file
					int totalSize = urlConnection.getContentLength();
					// variable to store total downloaded bytes
					int downloadedSize = 0;

					// create a buffer...
					byte[] buffer = new byte[1024];
					int bufferLength = 0; // used to store a temporary size of
											// the
											// buffer

					// now, read through the input buffer and write the contents
					// to
					// the
					// file
					while ((bufferLength = inputStream.read(buffer)) > 0) {
						// add the data in the buffer to the file in the file
						// output
						// stream (the file on the sd card
						fileOutput.write(buffer, 0, bufferLength);
						// add up the size so we know how much is downloaded
						downloadedSize += bufferLength;
						// this is where you would do something to report the
						// prgress,
						// like this maybe
						updateProgress(builder, downloadedSize, totalSize);

					}
					// close the output stream when done

					fileOutput.close();

					// catch some possible errors...
				} catch (MalformedURLException e) {
					e.printStackTrace();

					return false;
				} catch (IOException e) {
					e.printStackTrace();

					return false;
				} catch (RuntimeException e) {
					try {
						cancel(true);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					return false;
				} catch (Exception e) {
					return false;
				}
				return true;

			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					doneDownloading(builder);
				} else {
					failedDownloading(builder);
				}
			}

		};
		async.execute(sURL);
	}

	protected void updateProgress(NotificationCompat.Builder builder,
			int downloadedSize, int totalSize) {

		if ((int) (downloadedSize * 100 / totalSize) > (previousProgress + 1)) {
			previousProgress = (int) (downloadedSize * 100 / totalSize);

			builder = new NotificationCompat.Builder(context);
			builder.setContentTitle("Downloading: " + title)
					.setContentText(
							"" + previousProgress + "% \t"
									+ (NOTIFY_ID % 11120 + 1) + "/"
									+ (CONST_NOTIFY_ID % 11120))
					.setContentIntent(pi)
					.setProgress(100, previousProgress, false)
					.setSmallIcon(R.drawable.ic_launcher).setOngoing(false);
			n = builder.build();
			nm.notify(NOTIFY_ID, n);
		}
	}

	protected void doneDownloading(NotificationCompat.Builder builder) {
		Intent i = new Intent(context, MainActivity.class);
		i.putExtra("FORCE", "FOLDERFORCE");
		i.putExtra("url", stringURL);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		pi = PendingIntent.getActivity(context, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);
		builder = new NotificationCompat.Builder(context);
		builder.setContentText("Download Successful")
				.setContentTitle("" + title).setContentIntent(pi)
				.setSmallIcon(R.drawable.ic_launcher).setOngoing(false);
		n = builder.build();
		nm.notify(NOTIFY_ID, n);
	}

	protected void failedDownloading(NotificationCompat.Builder builder) {
		Intent i = new Intent(context, MainActivity.class);
		i.putExtra("title", "HOMEFORCE");
		i.putExtra("url", URL);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		pi = PendingIntent.getActivity(context, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);
		builder = new NotificationCompat.Builder(context);
		builder.setContentText("Download Failed").setContentTitle("" + title)
				.setContentIntent(pi).setSmallIcon(R.drawable.ic_launcher)
				.setOngoing(false);
		n = builder.build();
		nm.notify(NOTIFY_ID, n);
		file.delete();
	}

	public static void removeNotification(Context context, int i) {
		try {
			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancel(OfflineManager.CONST_NOTIFY_ID + i);
		} catch (Exception e) {

		}
	}

}
