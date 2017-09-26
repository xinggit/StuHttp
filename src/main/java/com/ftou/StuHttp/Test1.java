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

public class Test1 {

	public static void main(String[] args) throws Exception {
		login();
	}

	public static void login() throws Exception {
		

		
		String loginUrl = "https://xui.ptlogin2.qq.com/cgi-bin/xlogin?proxy_url=https%3A//qzs.qq.com/qzone/v6/portal/proxy.html&daid=5&&hide_title_bar=1&low_login=0&qlogin_auto_login=1&no_verifyimg=1&link_target=blank&appid=549000912&style=22&target=self&s_url=https%3A%2F%2Fqzs.qzone.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone%26from%3Diqq&pt_qr_app=%E6%89%8B%E6%9C%BAQQ%E7%A9%BA%E9%97%B4&pt_qr_link=http%3A//z.qzone.com/download.html&self_regurl=https%3A//qzs.qq.com/qzone/v6/reg/index.html&pt_qr_help_link=http%3A//z.qzone.com/download.html&pt_no_auth=0";
		WebDriver webDriver = new FirefoxDriver();
		webDriver.get(loginUrl);
		
		//账号密码登录//*[@id="switcher_plogin"]
		String useXpath = "//*[@id=\"switcher_plogin\"]";
		
		// 用户名输入框dom元素路径
		String usernameXpath = "//*[@id=\"u\"]";
		// 密码输入框dom元素路径
		String userpasswordXpath = "//*[@id=\"p\"]";
		// 验证码输入框dom元素路径
		String verifycodeXpath = "//*[@id=\"RANDOMCODE\"]";
		// 登录按钮dom元素路径
		String loginBtnXpath = "//*[@id=\"login_button\"]";
		try {
			//等待元素加载完成
			new WebDriverWait(webDriver, 1).until(ExpectedConditions.elementToBeClickable(By.xpath(useXpath)));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("加载完成......");
		//账号密码登录
		WebElement useElement = webDriver.findElement(By.xpath(useXpath));
		useElement.click();
		
		//用户名
		WebElement usernameElement = webDriver.findElement(By.xpath(usernameXpath));
		usernameElement.clear();
		usernameElement.sendKeys("1193166256");

		//密码
		WebElement passwordElement = webDriver.findElement(By.xpath(userpasswordXpath));
		passwordElement.clear();
		passwordElement.sendKeys("aassdd");

		//验证码
//		WebElement verifycodeElement = webDriver.findElement(By.xpath(verifycodeXpath));
//		verifycodeElement.clear();
//		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//		System.out.println("请输入下载下来的验证码中显示的数字...");
//		String code = br.readLine();
//		verifycodeElement.sendKeys(code.trim());
		
		//登录点击
		WebElement loginBtnElement = webDriver.findElement(By.xpath(loginBtnXpath));
		loginBtnElement.click();
		
		Thread.sleep(2000);

		CookieStore cookieStore = new BasicCookieStore();
		Set<Cookie> cookieSet = webDriver.manage().getCookies();
		webDriver.close();
		BasicClientCookie resultCookie = null;
		for (Cookie cookie : cookieSet) {
			// 创建httpclient定义的cookie
			resultCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
//			System.out.println(cookie.getName() + "=" + cookie.getValue() );
		}
		HttpClientBuilder builder = HttpClients.custom().setDefaultCookieStore(cookieStore);
		CloseableHttpClient httpClient = builder.build();
		String url = "https://user.qzone.qq.com/1193166256/infocenter?via=toolbar&_t_=0.28418564444713235";
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:36.0) Gecko/20100101 Firefox/36.0");
		httpGet.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpGet.addHeader("Accept-Encoding","gzip, deflate");
		httpGet.addHeader("If-Modified-Since","Thu, 21 Sep 2017 06:22:03 GMT");
		httpGet.addHeader("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		
		
		CloseableHttpResponse res = httpClient.execute(httpGet);
		InputStream in = null;
		try {
			in = res.getEntity().getContent();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedReader buff = new BufferedReader(new InputStreamReader(in,"utf-8"));
		String line = null;
		while((line = buff.readLine()) != null) {
			System.out.println(line);
		}
	}

}