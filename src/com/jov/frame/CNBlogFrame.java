package com.jov.frame;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.jov.adapter.CSDNDataAdapter;
import com.jov.bean.BlogBean;
import com.jov.db.DBHelper;
import com.jov.itog.R;
import com.jov.net.HTMLParser;
import com.jov.net.ThreadPoolUtils;
import com.jov.util.Common;
import com.jov.util.Constants;
import com.jov.view.PullDownView;

public class CNBlogFrame extends Fragment implements
		PullDownView.OnPullDownListener {
	private View view;
	private Context context;
	private List<BlogBean> list;
	private PullDownView cnPullDownView;
	private CSDNDataAdapter cnAdapter;
	private ListView cnListView;
	private static boolean isDoingUpdate = false;
	private int pageNo = 1;
	private DBHelper db;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.cnblog_frame, container, false);
		context = view.getContext();
		list = new ArrayList<BlogBean>();
		db = new DBHelper(context);
		initView();
		return view;
	}

	private void initView() {
		cnPullDownView = (PullDownView) view.findViewById(R.id.cnblog_listview);
		cnPullDownView.setOnPullDownListener(this);
		cnAdapter = new CSDNDataAdapter(context, list);
		cnListView = cnPullDownView.getListView();
		cnListView.setAdapter(cnAdapter);
		cnPullDownView.enableAutoFetchMore(true, 3);
		cnPullDownView.setShowFooter();
		cnPullDownView.setShowHeader();
		runThread();
	}

	private Handler cnHand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 200:
				List<BlogBean> result = (List<BlogBean>) msg.obj;
				if (result != null) {
					list.clear();
					list.addAll(result);
					cnAdapter.notifyDataSetChanged();
					isDoingUpdate = false;
					pageNo = 1;
				}
				cnPullDownView.RefreshComplete();
				break;
			default:
				Toast.makeText(context, "【博客园】请求出现异常，您可以尝试再次刷新！",
						Toast.LENGTH_SHORT).show();
				isDoingUpdate = false;
				cnPullDownView.RefreshComplete();
				break;
			}
		};
	};

	private Handler nextPageCNBLOGHand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 200:
				List<BlogBean> result = (List<BlogBean>) msg.obj;
				if (result != null) {
					list.addAll(result);
					cnAdapter.notifyDataSetChanged();
					isDoingUpdate = false;
					pageNo++;
				}
				cnPullDownView.notifyDidMore();
				break;
			default:
				Toast.makeText(context, "【博客园】请求出现异常，您可以尝试再次刷新！",
						Toast.LENGTH_SHORT).show();
				isDoingUpdate = false;
				cnPullDownView.notifyDidMore();
				break;
			}
		};
	};

	private void runThread() {
		if (!Common.isNetworkConnected(context)) {
			if (list.size() == 0) {
				List<BlogBean> result = db.getBlog(Constants.CNBLOG_FLAG_3);
				list.addAll(result);
			}
			cnPullDownView.RefreshComplete();
			return;
		}
		if (!isDoingUpdate) {
			ThreadPoolUtils.execute(new HTMLParser(cnHand, Constants.CN_BLOGS,
					false));
			isDoingUpdate = true;
		}
	}

	@Override
	public void onRefresh() {
		runThread();
	}

	@Override
	public void onMore() {
		if (!Common.isNetworkConnected(context)) {
			cnPullDownView.notifyDidMore();
			return;
		}
		if (!isDoingUpdate) {
			ThreadPoolUtils.execute(new HTMLParser(nextPageCNBLOGHand,
					Constants.CN_BLOGS + "?&page=" + pageNo, false));
			isDoingUpdate = true;
		}
	}
}
