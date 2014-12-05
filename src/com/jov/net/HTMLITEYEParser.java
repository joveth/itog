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

public class HTMLITEYEParser extends HTMLParser {
	public HTMLITEYEParser(Handler hand, String url) {
		super(hand, url);
	}

	public List<BlogBean> parser(String url) throws ClientProtocolException,
			IOException {
		String htmlStr = GetResource.doGet(url);
		List<BlogBean> blogList = new ArrayList<BlogBean>();
		BlogBean blog = null;
		Document doc = Jsoup.parse(htmlStr);
		Elements units = doc.getElementsByClass(Constants.ITEYE_LIST);
		for (int i = 0; i < units.size(); i++) {
			blog = new BlogBean();
			Element unit_ele = units.get(i);
			Element h3_ele = unit_ele.getElementsByTag("h3").get(0);
			Element h3_a_ele = h3_ele.getElementsByClass("category").get(0);
			String sortType = h3_a_ele.child(0).text();
			blog.setSortType(sortType);
			Element h1_a_title = h3_ele.child(1);
			String title = h1_a_title.text();
			String href = h1_a_title.attr("href");
			blog.setTitle(title);
			blog.setLink(href);

			Element des_ele = unit_ele.child(1);
			String shortDesc = des_ele.text();
			blog.setShortDesc(shortDesc);
			Element other_ele = unit_ele.getElementsByClass("blog_info").get(0);// dl
			Element user_ele = other_ele.child(1);
			String userName = user_ele.text();
			blog.setAuthor(userName);
			Element date_ele = other_ele.child(4);
			String date = date_ele.text();
			date = date.substring(date.indexOf("-") + 1);
			blog.setDate(date);
			Element read_ele = other_ele.child(3);
			String read = read_ele.text();
			blog.setRead(read);
			Element comment_ele = other_ele.child(2);
			String comment = comment_ele.text();
			blog.setComment(comment);

			blogList.add(blog);
		}
		return blogList;
	}

	@Override
	public String getHTML(String result) {
		Document doc = Jsoup.parse(result);
		Elements units = doc.getElementsByClass("blog_main");
		Element unit_ele = units.get(0);
		String html = htmlHeader;
		html += unit_ele.child(0).html();
		html += unit_ele.child(1).html();
		html += unit_ele.getElementsByClass("blog_bottom").get(0).html();
		html += unit_ele.getElementsByClass("blog_comment").get(0).html();
		return html;
	}
}
