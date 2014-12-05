package com.jov.util;


import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.jov.bean.UpdateBean;

public class XMLReader {
	public UpdateBean parseUpdate(InputStream is) throws Exception {
		if (is == null) {
			return null;
		}
		UpdateBean obj = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "UTF-8");
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				obj = new UpdateBean();
				break;
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("version")) {
					eventType = parser.next();
					obj.setVersion(parser.getText());
				} else if (parser.getName().equals("name")) {
					eventType = parser.next();
					obj.setName(parser.getText());
				} else if (parser.getName().equals("describe")) {
					eventType = parser.next();
					obj.setDesc(parser.getText());
				}
				break;
			case XmlPullParser.END_TAG:
				break;
			}
			eventType = parser.next();
		}
		return obj;
	}
}
