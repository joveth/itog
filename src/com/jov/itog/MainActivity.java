package com.jov.itog;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.jov.net.DownloadManager;
import com.jov.net.UpdateManager;
import com.jov.util.Common;
import com.jov.util.Constants;
import com.jov.view.DethPageTransformer;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity {

	public static final String PAGE1_ID = "page1";
	public static final String PAGE2_ID = "page2";
	public static final String PAGE3_ID = "page3";
	public static final String PAGE4_ID = "page4";

	private TabHost tabHost; // TabHost
	private List<View> views; // ViewPager内的View对象集合
	private FragmentManager manager; // Activity管理器
	private ViewPager pager; // ViewPager

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setHomeButtonEnabled(true);
		// 初始化资源
		pager = (ViewPager) findViewById(R.id.viewpager);
		tabHost = (TabHost) findViewById(R.id.tab_host);
		manager = getSupportFragmentManager();
		views = new ArrayList<View>();

		views.add(manager.findFragmentById(R.id.fragment_image).getView());
		views.add(manager.findFragmentById(R.id.fragment_text).getView());
		views.add(manager.findFragmentById(R.id.fragment_both).getView());
		views.add(manager.findFragmentById(R.id.fragment_osc).getView());

		// 管理tabHost开始
		tabHost.setup();

		// 传一个空的内容给TabHost，不能用上面两个fragment
		TabContentFactory factory = new TabContentFactory() {
			@Override
			public View createTabContent(String tag) {
				return new View(MainActivity.this);
			}
		};

		// tab1
		TabSpec tabSpec1 = tabHost.newTabSpec(PAGE1_ID);
		tabSpec1.setIndicator(createTabView(R.string.fragment_image_str));
		tabSpec1.setContent(factory);
		tabHost.addTab(tabSpec1);
		// tab2
		TabSpec tabSpec2 = tabHost.newTabSpec(PAGE2_ID);
		tabSpec2.setIndicator(createTabView(R.string.fragment_text_str));
		tabSpec2.setContent(factory);
		tabHost.addTab(tabSpec2);
		// tab3
		TabSpec tabSpec3 = tabHost.newTabSpec(PAGE3_ID);
		tabSpec3.setIndicator(createTabView(R.string.fragment_both_str));
		tabSpec3.setContent(factory);
		tabHost.addTab(tabSpec3);
		// tab4
		TabSpec tabSpec4 = tabHost.newTabSpec(PAGE4_ID);
		tabSpec4.setIndicator(createTabView(R.string.fragment_osc_str));
		tabSpec4.setContent(factory);
		tabHost.addTab(tabSpec4);
		tabHost.setCurrentTab(0);
		// 管理tabHost结束

		// 设置监听器和适配器
		pager.setAdapter(new PageAdapter());
		pager.setPageTransformer(true, new DethPageTransformer());
		pager.setOnPageChangeListener(new PageChangeListener());
		tabHost.setOnTabChangedListener(new TabChangeListener());
		UpdateManager updateManager = new UpdateManager(this);
		updateManager.checkVersionThread();
		if (!Common.isNetworkConnected(this)) {
			Toast.makeText(this, "亲，木有开启网络哦！", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	/**
	 * PageView Adapter
	 * 
	 * @author Administrator
	 * 
	 */
	private class PageAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup view, int position, Object arg2) {
			view.removeView(views.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			try {
				if (views.get(position).getParent() == null) {
					view.addView(views.get(position));
				} else {
					((ViewGroup) views.get(position).getParent())
							.removeView(views.get(position));
					view.addView(views.get(position));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return views.get(position);
		}
	}

	/**
	 * 标签页点击切换监听器
	 * 
	 * @author Administrator
	 * 
	 */
	private class TabChangeListener implements OnTabChangeListener {
		@Override
		public void onTabChanged(String tabId) {
			if (PAGE4_ID.equals(tabId)) {
				pager.setCurrentItem(3);
			} else if (PAGE1_ID.equals(tabId)) {
				pager.setCurrentItem(0);
			} else if (PAGE2_ID.equals(tabId)) {
				pager.setCurrentItem(1);
			} else if (PAGE3_ID.equals(tabId)) {
				pager.setCurrentItem(2);
			}
		}
	}

	/**
	 * ViewPager滑动切换监听器
	 * 
	 * @author Administrator
	 * 
	 */
	private class PageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			tabHost.setCurrentTab(arg0);
		}
	}

	/**
	 * 创建tab View
	 * 
	 * @param string
	 * @return
	 */
	private View createTabView(int stringId) {
		View tabView = getLayoutInflater().inflate(R.layout.tab, null);
		TextView textView = (TextView) tabView.findViewById(R.id.tab_text);
		textView.setText(stringId);
		return tabView;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu_fav) {
			switchTo(FavActivity.class);
			return true;
		} else if (id == R.id.menu_setting) {
			switchTo(AboutActivity.class);
			return true;
		} else if (id == android.R.id.home) {
			switchTo(DownloadActivity.class);
		} else if (id == R.id.menu_download) {
			if (!Common.isNetworkConnected(this)) {
				Toast.makeText(this, "网络未开启，无法离线", Toast.LENGTH_SHORT).show();
				return true;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("只会缓存各博客网站的第一页内容，并清除上次缓存的内容，确定缓存吗？")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									DownloadManager download = new DownloadManager(
											MainActivity.this);
									download.doLoadThread();
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

		} else if (id == R.id.menu_switch) {
			Toast.makeText(this, "切换到IT资讯功能暂未实现，敬请期待……", Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void switchTo(Class clazz) {
		Intent intent = new Intent();
		intent.setClass(this, clazz);
		startActivity(intent);
	}

	private long exitTime = 0;

	public void backTo() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(), "亲，再按一次才能退出哦！",
					Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			finish();
			System.exit(0);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			backTo();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {

		}
		return super.onKeyDown(keyCode, event);
	}
}
