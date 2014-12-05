package com.jov.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;

import com.jov.bean.UpdateBean;
import com.jov.itog.MainActivity;
import com.jov.itog.R;
import com.jov.util.FileUtil;
import com.jov.util.XMLReader;

public class UpdateManager {
	private static final int TIMEOUT = 10 * 1000;// 超时
	private static String down_url = "";
	private static final int DOWN_OK = 21;
	private static final int DOWN_ERROR = 20;
	private static final int NEW_UPDATE = 22;
	private NotificationManager notificationManager;
	private Notification notification;
	private Intent updateIntent;
	private PendingIntent pendingIntent;
	private int notification_id = 0;
	private String uri = "http://jovmusic.qiniudn.com/";
	private Context context;
	private String version = "2";
	private UpdateBean bean;

	public UpdateManager(Context context) {
		this.context = context;
	}

	public void checkVersionThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (checkVersion()) {
					Message message = new Message();
					message.what = NEW_UPDATE;
					handler.sendMessage(message);
				}
			}
		}).start();
	}

	private void downLoadThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				long downloadSize = downloadUpdateFile(down_url,
						FileUtil.updateFile);
				Message message = new Message();
				if (downloadSize > 0) {
					// 下载成功
					message.what = DOWN_OK;
					handler.sendMessage(message);
				} else {
					message.what = DOWN_ERROR;
					handler.sendMessage(message);
				}
			}
		}).start();
	}

	
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_OK:
				// 下载完成，点击安装
				Uri uri = Uri.fromFile(FileUtil.updateFile);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(uri,
						"application/vnd.android.package-archive");
				pendingIntent = PendingIntent
						.getActivity(context, 0, intent, 0);
				notification.tickerText="IT博客下载完成，点击安装";
				notification.setLatestEventInfo(context, "IT博客", "下载完成，点击安装",
						pendingIntent);
				notificationManager.notify(notification_id, notification);
				context.startActivity(intent);
				break;
			case DOWN_ERROR:
				notification.setLatestEventInfo(context, "IT博客", "下载失败",
						pendingIntent);
				break;
			case NEW_UPDATE:
				if (bean == null) {
					break;
				}
				showDialog(bean.getDesc());
				break;
			default:
				break;
			}
		}
	};

	private void showDialog(String msg) {
		// 发现新版本，提示用户更新
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		StringBuilder build = new StringBuilder();
		alert.setTitle("发现新的版本")
				.setMessage(msg)
				.setPositiveButton("现在更新",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								FileUtil.createFile("itog");
								createNotification();
								downLoadThread();
							}
						})
				.setNegativeButton("下次再说",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
		alert.create().show();
	}

	private boolean checkVersion() {
		String path = uri + "update.xml";
		URL url;
		try {
			url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(6 * 1000);
			if (conn.getResponseCode() == 200) {
				InputStream inStream = conn.getInputStream();
				XMLReader reader = new XMLReader();
				bean = reader.parseUpdate(inStream);
				if (bean == null) {
					return false;
				} else if (bean.getVersion().compareTo(version) > 0) {
					down_url = bean.getName();
					return true;
				}
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private RemoteViews contentView;

	private void createNotification() {
		notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText="IT博客更新";
		contentView = new RemoteViews(context.getPackageName(),
				R.layout.notification_item);
		contentView.setTextViewText(R.id.notificationTitle, "正在下载");
		contentView.setTextViewText(R.id.notificationPercent, "0%");
		contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);
		notification.contentView = contentView;
		updateIntent = new Intent(context, MainActivity.class);
		updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		pendingIntent = PendingIntent.getActivity(context, 0, updateIntent, 0);
		notification.contentIntent = pendingIntent;
		notificationManager.notify(notification_id, notification);
	}

	private long downloadUpdateFile(String down_url, File file) {
		if (file==null){
			return 0;
		}
		int down_step = 5;// 提示step
		int totalSize;// 文件总大小
		int downloadCount = 0;// 已经下载好的大小
		int updateCount = 0;// 已经上传的文件大小
		InputStream inputStream;
		OutputStream outputStream;
		URL url;
		try {
			url = new URL(down_url);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setConnectTimeout(TIMEOUT);
			httpURLConnection.setReadTimeout(TIMEOUT);
			// 获取下载文件的size
			totalSize = httpURLConnection.getContentLength();
			if (httpURLConnection.getResponseCode() == 404) {
				return 0;
			}
			inputStream = httpURLConnection.getInputStream();
			outputStream = new FileOutputStream(file, false);// 文件存在则覆盖掉
			byte buffer[] = new byte[1024];
			int readsize = 0;
			while ((readsize = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, readsize);
				downloadCount += readsize;// 时时获取下载到的大小
				if (updateCount == 0
						|| (downloadCount * 100 / totalSize - down_step) >= updateCount) {
					updateCount += down_step;
					contentView.setTextViewText(R.id.notificationPercent,
							updateCount + "%");
					contentView.setProgressBar(R.id.notificationProgress, 100,
							updateCount, false);
					// show_view
					notificationManager.notify(notification_id, notification);
				}
			}
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
			inputStream.close();
			outputStream.close();
			return downloadCount;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
