package com.ftou.StuHttp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Test2 {

	public static void main(String[] args) throws Exception {
		login();
	}

	public static void login() throws Exception {

		String loginUrl = "https://account.xiaomi.com/pass/serviceLogin?callback=https%3A%2F%2Forder.mi.com%2Flogin%2Fcallback%3Ffollowup%3Dhttps%253A%252F%252Fwww.mi.com%252Findex.html%26sign%3DMjM0MWU0NjBlOTU1YzY4NGQzOTc3MDk4N2M2MjQ5Y2ZiZTMxNTFlZQ%2C%2C&sid=mi_eshop&_bannerBiz=mistore&_qrsize=180";
		WebDriver webDriver = new FirefoxDriver();
		webDriver.get(loginUrl);

		// 用户名输入框dom元素路径
		String usernameXpath = "//*[@id=\"username\"]";
		// 密码输入框dom元素路径
		String userpasswordXpath = "//*[@id=\"pwd\"]";
		// 验证码输入框dom元素路径
		String verifycodeXpath = "//*[@id=\"RANDOMCODE\"]";
		// 登录按钮dom元素路径
		String loginBtnXpath = "//*[@id=\"login-button\"]";
		try {
			// 等待元素加载完成
			new WebDriverWait(webDriver, 1).until(ExpectedConditions.elementToBeClickable(By.xpath(loginBtnXpath)));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("加载完成......");

		// 用户名
		WebElement usernameElement = webDriver.findElement(By.xpath(usernameXpath));
		usernameElement.clear();
		usernameElement.sendKeys("18473483641");

		// 密码
		WebElement passwordElement = webDriver.findElement(By.xpath(userpasswordXpath));
		passwordElement.clear();
		passwordElement.sendKeys("zhaoxing19960919");

		// 登录点击
		WebElement loginBtnElement = webDriver.findElement(By.xpath(loginBtnXpath));
		loginBtnElement.click();

		Thread.sleep(2000);

		CookieStore cookieStore = new BasicCookieStore();
		Set<Cookie> cookieSet = webDriver.manage().getCookies();
		BasicClientCookie resultCookie = null;
		for (Cookie cookie : cookieSet) {
			// 创建httpclient定义的cookie
			resultCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
			System.out.println(cookie.getName() + "=" + cookie.getValue());
			cookieStore.addCookie(resultCookie);
			resultCookie.setDomain(cookie.getDomain());
			resultCookie.setExpiryDate(cookie.getExpiry());
			resultCookie.setPath(cookie.getPath());
			resultCookie.setExpiryDate(cookie.getExpiry());
		}
		HttpClientBuilder builder = HttpClients.custom().setDefaultCookieStore(cookieStore);
		CloseableHttpClient httpClient = builder.build();
		String url = "https://www.mi.com/index.html";
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:36.0) Gecko/20100101 Firefox/36.0");
		httpGet.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpGet.addHeader("Accept-Encoding", "gzip, deflate");
		httpGet.addHeader("If-Modified-Since", "Thu, 21 Sep 2017 06:22:03 GMT");
		httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");

		CloseableHttpResponse res = httpClient.execute(httpGet);
		InputStream in = null;
		try {
			in = res.getEntity().getContent();
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