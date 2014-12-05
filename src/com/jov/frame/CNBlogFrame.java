package com.jov.frame;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.jov.adapter.CSDNDataAdapter;
import com.jov.bean.BlogBean;
import com.jov.itog.R;
import com.jov.net.HTMLParser;
import com.jov.net.ThreadPoolUtils;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.cnblog_frame, container, false);
		context = view.getContext();
		list = new ArrayList<BlogBean>();
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
		ThreadPoolUtils.execute(new HTMLParser(cnHand, Constants.CN_BLOGS));
		isDoingUpdate = true;
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
					Log.v("listsize", list.size() + "");
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
					Log.v("listsize", list.size() + "");
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

	@Override
	public void onRefresh() {
		if (!isDoingUpdate) {
			ThreadPoolUtils.execute(new HTMLParser(cnHand, Constants.CN_BLOGS));
			isDoingUpdate = true;
		}
	}

	@Override
	public void onMore() {
		if (!isDoingUpdate) {
			pageNo++;
			ThreadPoolUtils.execute(new HTMLParser(nextPageCNBLOGHand,
					Constants.CN_BLOGS + "?&page=" + pageNo));
			isDoingUpdate = true;
		}
	}
}
