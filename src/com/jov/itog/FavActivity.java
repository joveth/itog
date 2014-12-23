package com.jov.itog;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.jov.adapter.CSDNDataAdapter;
import com.jov.bean.BlogBean;
import com.jov.db.DBHelper;
import com.jov.util.Constants;
import com.jov.view.PullDownView;

@SuppressLint("NewApi")
public class FavActivity extends Activity implements
		PullDownView.OnPullDownListener {
	private List<BlogBean> list;
	private PullDownView cnPullDownView;
	private CSDNDataAdapter cnAdapter;
	private ListView cnListView;
	private int pageNo = 1;
	private DBHelper db;
	private int total = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fav_frame);
		list = new ArrayList<BlogBean>();
		db = new DBHelper(this);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		initData();
		initView();
	}

	private void initData() {
		int temp = db.getTotalCount(Constants.FAV_FLAG_1);
		total = temp % 20 == 0 ? temp / 20 : (temp / 20 + 1);
		if (total == 0)
			Toast.makeText(FavActivity.this, "亲，还没有收藏哦", Toast.LENGTH_SHORT)
					.show();
		list.clear();
		list.addAll(db.getBlog(pageNo, Constants.FAV_FLAG_1));
	}

	private void initView() {
		cnPullDownView = (PullDownView) findViewById(R.id.fav_listview);
		cnPullDownView.setOnPullDownListener(this);
		cnAdapter = new CSDNDataAdapter(this, list);
		cnListView = cnPullDownView.getListView();
		cnListView.setAdapter(cnAdapter);
		cnPullDownView.enableAutoFetchMore(true, 3);
		cnPullDownView.setHideFooter();
		cnPullDownView.setShowHeader();
		cnPullDownView.RefreshComplete();
	}

	@Override
	public void onRefresh() {
		pageNo = 1;
		initData();
		cnAdapter.notifyDataSetChanged();
		cnPullDownView.RefreshComplete();
	}

	@Override
	public void onMore() {
		pageNo = pageNo > total ? total : (pageNo + 1);
		if (pageNo > total) {
			pageNo = total;
		} else {
			pageNo++;
			list.addAll(db.getBlog(pageNo, Constants.FAV_FLAG_1));
			cnAdapter.notifyDataSetChanged();
		}
		cnPullDownView.notifyDidMore();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.fav_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu_clearfav && total != 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("亲，确定全部清空吗？")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									db.deleteAll(false, Constants.FAV_FLAG_1);
									list.clear();
									cnAdapter.notifyDataSetChanged();
									Toast.makeText(FavActivity.this, "已清空",
											Toast.LENGTH_SHORT).show();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
			return true;
		} else if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
