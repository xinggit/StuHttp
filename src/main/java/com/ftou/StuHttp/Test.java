package com.ftou.StuHttp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

public class Test {

	public static void main(String[] args) throws Exception {
		String code = null;
		getCode("http://59.51.24.46/hysf/verifycode.servlet");
		// 第二步：用Post方法带若干参数尝试登录，需要手工输入下载验证码中显示的字母、数字
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("请输入下载下来的验证码中显示的数字...");
		String yan = br.readLine();
		code = yan.trim();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("USERNAME", "14100137"));
		formparams.add(new BasicNameValuePair("PASSWORD", "14100137"));
		formparams.add(new BasicNameValuePair("useDogCode", ""));
		formparams.add(new BasicNameValuePair("useDogCode", ""));
		formparams.add(new BasicNameValuePair("RANDOMCODE", code));
		formparams.add(new BasicNameValuePair("x", "24"));
		formparams.add(new BasicNameValuePair("y", "5"));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
		HttpPost httppost = new HttpPost("http://59.51.24.46/hysf/Logon.do?method=logon");
		httppost.setEntity(entity);
		CloseableHttpResponse res = httpclient.execute(httppost);

		RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();// 标准Cookie策略
		httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();// 设置进去

		Header[] headers = res.getHeaders("Set-Cookie");
		StringBuffer cookie_v = new StringBuffer();
		for (Header header : headers) {
			cookie_v.append(header.getValue() + "; ");
		}
		httppost.releaseConnection();
		String val = cookie_v.substring(0, cookie_v.length() - 2);
		System.out.println(val);
		System.out.println("---------------------------------");
		BasicHeader cookie1 = new BasicHeader("Cookie", val);
		get("http://59.51.24.46/hysf/framework/main.jsp", null,httpclient);

	}

	public static void getCode(String url) throws Exception {
		String destfilename = "C:\\Users\\lhy\\Desktop\\verifycode.png";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url);
		File file = new File(destfilename);
		if (file.exists()) {
			file.delete();
		}
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		InputStream in = entity.getContent();
		try {
			FileOutputStream fout = new FileOutputStream(file);
			int l = -1;
			byte[] tmp = new byte[2048];
			while ((l = in.read(tmp)) != -1) {
				fout.write(tmp);
			}
			fout.close();
		} finally {
			in.close();
		}
		httpget.releaseConnection();

	}

	public static void get(String url, Header head, CloseableHttpClient httpclient) throws Exception {

		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response1 = null;
		try {
			response1 = httpclient.execute(httpGet);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(response1.getStatusLine());
		HttpEntity entity = response1.getEntity();
		InputStream in = null;
		try {
			in = entity.getContent();

		} catch (IOException e) {
			e.printStackTrace();
		}

		BufferedReader buff = new BufferedReader(new InputStreamReader(in, "utf-8"));
		String line = null;
		while ((line = buff.readLine()) != null) {
			System.out.println(line);
		}
	}

}
