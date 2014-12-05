package com.jov.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Handler;

import com.jov.bean.BlogBean;
import com.jov.util.Constants;

public class HTMLCSDNParser extends HTMLParser {
	public HTMLCSDNParser(Handler hand, String url) {
		super(hand, url);
	}

	public List<BlogBean> parser(String url) throws ClientProtocolException,
			IOException {
		String htmlStr = GetResource.doGet(url);
		List<BlogBean> blogList = new ArrayList<BlogBean>();
		BlogBean blog = null;
		if (htmlStr == null) {
			return null;
		}
		Document doc = Jsoup.parse(htmlStr);
		Elements units = doc.getElementsByClass(Constants.CSDN_BLOG_LIST);
		for (int i = 0; i < units.size(); i++) {
			blog = new BlogBean();
			Element unit_ele = units.get(i);
			Element h1_ele = unit_ele.getElementsByTag("h1").get(0);
			Element h1_a_ele = h1_ele.child(0);
			String sortType = h1_a_ele.text();
			blog.setSortType(sortType);
			Element h1_a_title = h1_ele.child(1);
			String title = h1_a_title.text();
			String href = h1_a_title.attr("href");
			blog.setTitle(title);
			blog.setLink(href);

			Element dl_ele = unit_ele.getElementsByTag("dl").get(0);
			Element dd_ele = dl_ele.getElementsByTag("dd").get(0);
			String shortDesc = dd_ele.text();
			blog.setShortDesc(shortDesc);
			Element other_ele = unit_ele.getElementsByClass("about_info")
					.get(0);// dl
			Element span_ele = other_ele.child(1);// dt
			Element user_ele = span_ele.child(0);
			String userName = user_ele.text();
			blog.setAuthor(userName);
			Element date_ele = span_ele.child(1);
			String date = date_ele.text();
			blog.setDate(date);
			Element read_ele = span_ele.child(2);
			String read = read_ele.text();
			blog.setRead(read);
			Element comment_ele = span_ele.child(3);
			String comment = comment_ele.text();
			blog.setComment(comment);

			blogList.add(blog);
		}
		return blogList;
	}

	@Override
	public String getHTML(String result) {
		Document doc = Jsoup.parse(result);
		Elements units = doc.getElementsByClass("main");
		Element unit_ele = units.get(0);
		String html = htmlHeader;
		Element unit_arc_detail = unit_ele.getElementById("article_details");
		html += unit_arc_detail.getElementsByClass("article_title").html();
		html += unit_arc_detail.getElementsByClass("article_manage").html();
		html += unit_arc_detail.getElementById("article_content").html();
		html += unit_ele.getElementsByClass("comment_class").html();
		return html;
	}
}
