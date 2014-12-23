package com.jov.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

public class GetResource {
	public static String doGet(String url) throws ClientProtocolException,
			IOException {
		String result = null;// 我们的网络交互返回值
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader(
				"user-agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setIntParameter(
				HttpConnectionParams.CONNECTION_TIMEOUT, 5 * 1000);
		httpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
				30 * 1000);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
		}
		return result;
	}

	public static String getHtml(String path, String charset)
			throws ClientProtocolException, IOException {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty(
				"user-agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
		conn.setConnectTimeout(5 * 1000);
		InputStream inStream = conn.getInputStream();// 通过输入流获取html数据
		byte[] data = readInputStream(inStream);// 得到html的二进制数据
		String html = new String(data, charset);
		return html;
	}

	public static byte[] readInputStream(InputStream inStream)
			throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}
}
