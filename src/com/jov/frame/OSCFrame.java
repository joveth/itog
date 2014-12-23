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

public class OSCFrame extends Fragment implements
		PullDownView.OnPullDownListener {
	private View view;
	private Context context;
	private List<BlogBean> list;
	private PullDownView mPullDownView;
	private CSDNDataAdapter adapter;
	private ListView mListView;
	private static boolean isDoingUpdate = false;
	private int pageNo = 1;
	private DBHelper db;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.osc_frame, container, false);
		context = view.getContext();
		list = new ArrayList<BlogBean>();
		db = new DBHelper(context);
		initView();
		return view;
	}

	private void initView() {
		mPullDownView = (PullDownView) view.findViewById(R.id.osc_listview);
		mPullDownView.setOnPullDownListener(this);
		adapter = new CSDNDataAdapter(context, list);
		mListView = mPullDownView.getListView();
		mListView.setAdapter(adapter);
		mPullDownView.enableAutoFetchMore(true, 3);
		mPullDownView.setShowFooter();
		mPullDownView.setShowHeader();
		runThread();
	}

	private void runThread() {
		if (!Common.isNetworkConnected(context)) {
			if (list.size() == 0) {
				List<BlogBean> result = db.getBlog(Constants.OSCHINA_FLAG_5);
				list.addAll(result);
			}
			mPullDownView.RefreshComplete();
			return;
		}
		if (!isDoingUpdate) {
			ThreadPoolUtils.execute(new HTMLParser(csdnHand,
					Constants.OSCHINA_BLOG, false));
			isDoingUpdate = true;
		}
	}

	private Handler csdnHand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 200:
				List<BlogBean> result = (List<BlogBean>) msg.obj;
				if (result != null) {
					list.clear();
					list.addAll(result);
					adapter.notifyDataSetChanged();
					isDoingUpdate = false;
					pageNo = 1;
				}
				mPullDownView.RefreshComplete();
				break;
			case 110:
				break;
			default:
				Toast.makeText(context, "【开源中国】请求出现异常，您可以尝试再次刷新！",
						Toast.LENGTH_SHORT).show();
				isDoingUpdate = false;
				mPullDownView.RefreshComplete();
				break;
			}
		};
	};

	private Handler nextPageCSDNHand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 200:
				List<BlogBean> result = (List<BlogBean>) msg.obj;
				if (result != null) {
					list.addAll(result);
					adapter.notifyDataSetChanged();
					isDoingUpdate = false;
					pageNo++;
				}
				mPullDownView.notifyDidMore();
				break;
			default:
				Toast.makeText(context, "【开源中国】请求出现异常，您可以尝试再次刷新！",
						Toast.LENGTH_SHORT).show();
				isDoingUpdate = false;
				mPullDownView.notifyDidMore();
				break;
			}
		};
	};

	@Override
	public void onRefresh() {
		runThread();
	}

	@Override
	public void onMore() {
		if (!isDoingUpdate) {
			if (!Common.isNetworkConnected(context)) {
				mPullDownView.notifyDidMore();
				return;
			}
			ThreadPoolUtils.execute(new HTMLParser(nextPageCSDNHand,
					Constants.OSCHINA_BLOG + "?type=0&p=" + pageNo
							+ "#catalogs", false));
			isDoingUpdate = true;
		}
	}
}
