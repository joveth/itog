package com.jov.net;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.jov.bean.BlogBean;
import com.jov.db.DBHelper;
import com.jov.util.Common;
import com.jov.util.Constants;

public class DownloadManager {
	private Context context;
	private DBHelper db;

	public DownloadManager(Context context) {
		this.context = context;

	}

	public void doLoadThread() {
		if (!Common.isNetworkConnected(context)) {
			Toast.makeText(context, "网络未开启，无法离线", Toast.LENGTH_SHORT).show();
			return;
		} else {
			db = new DBHelper(context);
			Toast.makeText(context, "开始缓存博客", Toast.LENGTH_SHORT).show();
			ThreadPoolUtils.execute(new HTMLParser(csdnHand,
					Constants.CSDN_BLOG, true));
			ThreadPoolUtils.execute(new HTMLParser(cnHand, Constants.CN_BLOGS,
					true));
			ThreadPoolUtils.execute(new HTMLParser(iteyeHand,
					Constants.ITEYE_BLOG, true));
			ThreadPoolUtils.execute(new HTMLParser(oscHand,
					Constants.OSCHINA_BLOG, true));
		}
	}

	private Handler csdnHand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 200:
				List<BlogBean> result = (List<BlogBean>) msg.obj;
				if (result != null) {
					// 只保留一次离线
					db.deleteAll(false, Constants.CSDN_FLAG_2);
					doInsert(result);
					Toast.makeText(context, "CSDN缓存已完成", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			default:
				Toast.makeText(context, "CSDN离线缓存失败", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		};
	};
	private Handler cnHand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 200:
				List<BlogBean> result = (List<BlogBean>) msg.obj;
				if (result != null) {
					db.deleteAll(false, Constants.CNBLOG_FLAG_3);
					doInsert(result);
					Toast.makeText(context, "博客园缓存已完成", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			default:
				Toast.makeText(context, "博客园离线缓存失败", Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};
	private Handler iteyeHand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 200:
				List<BlogBean> result = (List<BlogBean>) msg.obj;
				if (result != null) {
					db.deleteAll(false, Constants.ITEYE_FLAG_4);
					doInsert(result);
					Toast.makeText(context, "ITEYE缓存已完成", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			default:
				Toast.makeText(context, "ITEYE离线缓存失败", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		};
	};
	private Handler oscHand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 200:
				List<BlogBean> result = (List<BlogBean>) msg.obj;
				if (result != null) {
					db.deleteAll(false, Constants.OSCHINA_FLAG_5);
					doInsert(result);
					Toast.makeText(context, "开源中国缓存已完成", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			default:
				Toast.makeText(context, "开源中国离线缓存失败", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		};
	};

	private void doInsert(List<BlogBean> list) {
		for (BlogBean bean : list) {
			db.insertBlog(bean);
		}
	}
}
