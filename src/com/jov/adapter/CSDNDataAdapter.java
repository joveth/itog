package com.jov.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jov.bean.BlogBean;
import com.jov.itog.R;
import com.jov.itog.WebViewActivity;
import com.jov.util.StringUtil;

public class CSDNDataAdapter extends BaseAdapter {
	private List<BlogBean> list;
	private Context ctx;

	public CSDNDataAdapter(Context ctx, List<BlogBean> list) {
		this.list = list;
		this.ctx = ctx;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View arg1, ViewGroup arg2) {
		final Holder hold;
		if (arg1 == null) {
			hold = new Holder();
			arg1 = View.inflate(ctx, R.layout.csdn_item, null);
			hold.TitleText = (TextView) arg1.findViewById(R.id.Item_title);
			hold.MainText = (TextView) arg1.findViewById(R.id.Item_MainText);
			hold.itemIdText = (TextView) arg1.findViewById(R.id.item_author);
			hold.itemComment = (TextView) arg1
					.findViewById(R.id.item_comment_text);
			hold.itemRead = (TextView) arg1.findViewById(R.id.item_read_text);
			hold.more = (LinearLayout) arg1.findViewById(R.id.item_more);
			hold.detail = (LinearLayout) arg1.findViewById(R.id.item_lay);
			arg1.setTag(hold);
		} else {
			hold = (Holder) arg1.getTag();
		}
		hold.MainText.setText(list.get(position).getShortDesc());

		hold.TitleText.setText((list.get(position).getSortType() == null ? ""
				: list.get(position).getSortType())
				+ list.get(position).getTitle());
		hold.more.setVisibility(View.GONE);
		hold.itemIdText.setText(list.get(position).getAuthor()
				+ "  "
				+ (list.get(position).getDate() == null ? "" : list.get(
						position).getDate()));
		if (StringUtil.isEmpty(list.get(position).getRead())) {
			hold.itemRead.setVisibility(View.GONE);
		} else {
			hold.itemRead.setText(list.get(position).getRead());
		}
		if (StringUtil.isEmpty(list.get(position).getComment())) {
			hold.itemComment.setVisibility(View.GONE);
		} else {
			hold.itemComment.setText(list.get(position).getComment());
		}

		hold.more.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
			}
		});
		hold.detail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ctx, WebViewActivity.class);
				intent.putExtra("url", list.get(position).getLink());
				intent.putExtra("sortType",
						list.get(position).getSortType() == null ? "" : list
								.get(position).getSortType());
				intent.putExtra("shortDesc", list.get(position).getShortDesc());
				intent.putExtra("author", list.get(position).getAuthor());
				intent.putExtra(
						"date",
						list.get(position).getDate() == null ? "" : list.get(
								position).getDate());
				intent.putExtra("read", list.get(position).getRead());
				intent.putExtra("comment", list.get(position).getComment());
				intent.putExtra("title", list.get(position).getTitle());
				ctx.startActivity(intent);
			}
		});

		return arg1;
	}

	static class Holder {
		TextView MainText;
		TextView TitleText;
		TextView itemComment;
		TextView itemIdText;
		TextView itemRead;
		LinearLayout more;
		LinearLayout detail;
	}
}
