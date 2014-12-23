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

public class HTMLOSCParser extends HTMLParser {
	public HTMLOSCParser(Handler hand, String url, boolean isNeedGetContent) {
		super(hand, url, isNeedGetContent);
	}

	public List<BlogBean> parser(String url, boolean isNeedGetContent)
			throws ClientProtocolException, IOException {
		String htmlStr = null;
		htmlStr = GetResource.getHtml(url, "utf-8");
		List<BlogBean> blogList = new ArrayList<BlogBean>();
		BlogBean blog = null;
		if (htmlStr == null) {
			return null;
		}
		Document doc = Jsoup.parse(htmlStr);
		Element unit_blog = doc.getElementById(Constants.OSC_LIST);
		Elements units = unit_blog.child(1).getElementsByTag("li");
		for (int i = 0; i < units.size(); i++) {
			blog = new BlogBean();
			Element unit_ele = units.get(i);
			Element div_ele = unit_ele.getElementsByClass("b").get(0);
			Element h1_a_ele = div_ele.child(0).child(0);
			String title = h1_a_ele.text();
			String href = h1_a_ele.attr("href");
			blog.setTitle(title);
			blog.setLink(href);

			Element p_ele = div_ele.getElementsByTag("p").get(0);
			String shortDesc = p_ele.text();
			blog.setShortDesc(shortDesc);
			Element other_ele = div_ele.getElementsByClass("date").get(0);// dl
			String userName = other_ele.text();
			blog.setAuthor(userName);
			blog.setSourceType(Constants.OSCHINA_FLAG_5);
			if (isNeedGetContent) {
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
		String html = htmlHeader;
		html += doc.getElementsByClass("BlogTitle").get(0).html();
		if (doc.getElementsByClass("BlogAbstracts").size() > 0) {
			html += doc.getElementsByClass("BlogAbstracts").get(0).html();
		}
		html += doc.getElementsByClass("BlogContent").get(0).html();
		if (doc.getElementsByClass("BlogComments").size() > 0) {
			html += doc.getElementsByClass("BlogComments").get(0).html();
		}
		return html;
	}
}
