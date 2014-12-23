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
import com.jov.util.StringUtil;

public class HTMLCNBLOGParser extends HTMLParser {
	public HTMLCNBLOGParser(Handler hand, String url, boolean isNeedGetContent) {
		super(hand, url,isNeedGetContent);
	}

	public List<BlogBean> parser(String url, boolean isNeedGetContent) throws ClientProtocolException,
			IOException {
		String htmlStr = GetResource.doGet(url);
		List<BlogBean> blogList = new ArrayList<BlogBean>();
		BlogBean blog = null;
		if(StringUtil.isEmpty(htmlStr)){
			return blogList;
		}
		Document doc = Jsoup.parse(htmlStr);
		Elements units = doc.getElementsByClass(Constants.CN_BLOG_LIST);
		for (int i = 0; i < units.size(); i++) {
			blog = new BlogBean();
			Element unit_ele = units.get(i);
			Element item_ele = unit_ele.getElementsByClass("post_item_body")
					.get(0);
			Element h3_ele = item_ele.getElementsByTag("h3").get(0);
			Element h3_a_ele = h3_ele.child(0);
			String title = h3_a_ele.text();
			String href = h3_a_ele.attr("href");
			blog.setTitle(title);
			blog.setLink(href);
			Element content_ele = item_ele.getElementsByClass(
					"post_item_summary").get(0);
			String shortDesc = content_ele.text();
			blog.setShortDesc(shortDesc);

			Element other_ele = item_ele.getElementsByClass("post_item_foot")
					.get(0);
			Element user_ele = other_ele.child(0);
			String date = other_ele.textNodes().get(1).text();
			date = date.substring(date.indexOf("-") + 1);
			blog.setDate(date);
			String userName = user_ele.text();
			blog.setAuthor(userName);
			/*
			 * Element date_ele = other_ele.child(1); String date =
			 * date_ele.text(); blog.setDate(date);
			 */
			Element read_ele = other_ele.child(2);
			String read = read_ele.text();
			blog.setRead(read);
			Element comment_ele = other_ele.child(1);
			String comment = comment_ele.text();
			blog.setComment(comment);
			blog.setSourceType(Constants.CNBLOG_FLAG_3);
			if(isNeedGetContent){
				String content = getHTML(GetResource.getHtml(href, "utf-8"));
				blog.setContent(content);
			}
			blogList.add(blog);
		}
		return blogList;
	}

	@Override
	public String getHTML(String result) {
		Document doc = Jsoup.parse(result);
		Elements units = doc.getElementsByClass("post");
		Element unit_ele = units.get(0);
		String html = htmlHeader + unit_ele.html();
		return html;
	}
}
