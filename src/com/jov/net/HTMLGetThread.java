package com.jov.net;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HTMLGetThread implements Runnable {

	private Handler hand;
	private String url;
	private HTMLParser parser;

	public HTMLGetThread(Handler hand, String res) {
		this.hand = hand;
		url = res;
		parser = new HTMLParser(hand, url,false).generatorParser();
	}

	@Override
	public void run() {
		Message msg = hand.obtainMessage();
		Log.e("jov", url);
		try {
			if (parser == null) {
				msg.what = 100;
			}else{
				String result = parser.getHTML(GetResource.doGet(url));
				msg.what = 200;
				msg.obj = result;
			}
		} catch (ClientProtocolException e) {
			msg.what = 404;
		} catch (IOException e) {
			msg.what = 100;
		}
		hand.sendMessage(msg);
	}
}
