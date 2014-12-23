package com.jov.net;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jov.bean.BlogBean;

/**
 * HTML 解析器
 */
public class HTMLParser implements Runnable {
	private static final String CSDN_DOMAIN = ".csdn.";
	private static final String CNBLOG_DOMAIN = ".cnblogs.";
	private static final String ITEYE_DOMAIN = ".iteye.";
	private static final String OSC_DOMAIN = ".oschina.";
	private HTMLParser htmlParser;
	protected String htmlHeader = "<style type=\"text/css\"> a {color: #3E62A6;text-decoration: none;};"
			+ " .cnblogs_code pre {font-family: Courier New!important;font-size: 12px!important;word-wrap: break-word;white-space: pre-wrap;};"
			+ "code{-webkit-border-radius: 0 0 0 0 !important;background: none !important;border: 0 !important;bottom: auto !important;float: none !important;height: auto !important;left: auto !important;line-height: 1.1em !important;margin: 0 !important;outline: 0 !important;overflow: visible !important;padding: 0 !important;position: static !important;right: auto !important;text-align: left !important;top: auto !important;vertical-align: baseline !important;width: auto !important;box-sizing: content-box !important;font-family: \"Consolas\",\"Bitstream Vera Sans Mono\",\"Courier New\",Courier,monospace !important;font-weight: normal !important;font-style: normal !important;font-size: 1em !important;min-height: inherit !important;min-height: auto !important;}"
			+ ".dp-highlighter ol li, .dp-highlighter .columns div {list-style: decimal-leading-zero;list-style-position: outside !important;border-left: 3px solid #6CE26C;background-color: #F8F8F8;color: #5C5C5C;padding: 0 3px 0 10px !important;margin: 0 !important;line-height: 150%;};"
			+ ".news_tag a {display: inline-block;margin: 0 5px 5px 0;padding: 0px 10px;background-color: #aab5c3;-webkit-border-radius: 10px;-moz-border-radius: 10px;-o-border-radius: 10px;border-radius: 10px;color: #fff;text-decoration: none;}</style>";

	public List<BlogBean> parser(String url, boolean isNeedGetContent)
			throws ClientProtocolException, IOException {
		return null;
	}

	public String getHTML(String url) {
		return null;
	}

	public HTMLParser(Handler hand, String url, boolean isNeedGetContent) {
		this.hand = hand;
		this.url = url;
		this.isNeedGetContent = isNeedGetContent;
	}

	public HTMLParser generatorParser() {
		if (url.contains(CSDN_DOMAIN)) {
			return new HTMLCSDNParser(hand, url, isNeedGetContent);
		} else if (url.contains(CNBLOG_DOMAIN)) {
			return new HTMLCNBLOGParser(hand, url, isNeedGetContent);
		} else if (url.contains(ITEYE_DOMAIN)) {
			return new HTMLITEYEParser(hand, url, isNeedGetContent);
		} else if (url.contains(OSC_DOMAIN)) {
			return new HTMLOSCParser(hand, url, isNeedGetContent);
		}
		return null;
	}

	private Handler hand;
	private String url;
	private boolean isNeedGetContent = false;

	@Override
	public void run() {
		// 获取我们回调主ui的message
		Message msg = hand.obtainMessage();
		Log.e("jov", url);
		htmlParser = generatorParser();
		if (htmlParser == null) {
			msg.what = 100;
		} else {
			try {
				List<BlogBean> result = htmlParser.parser(url,isNeedGetContent);
				msg.what = 200;
				msg.obj = result;
			} catch (ClientProtocolException e) {
				msg.what = 404;
			} catch (IOException e) {
				msg.what = 100;
			}
		}
		// 给主ui发送消息传递数据
		hand.sendMessage(msg);
	}
}
