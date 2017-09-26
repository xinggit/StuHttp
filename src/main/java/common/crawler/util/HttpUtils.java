package common.crawler.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http请求帮助类
 *
 * @author shencheng
 */
public class HttpUtils {
    private static final Log log = LogFactory.getLog(HttpUtils.class);

    /**
     * 发送httpGet请求
     *
     * @param url 要请求的url
     * @return 页面html
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String httpGet(String url) throws Exception {
        return httpGet(url, null, 0, null);
    }

    /**
     * 发送httpget请求
     *
     * @param url        要请求的url
     * @param retryTimes 请求失败时，最大重试次数
     * @return 页面html
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String httpGet(String url, int retryTimes) throws Exception {
        return httpGet(url, null, retryTimes, null);
    }

    /**
     * 发送httpget请求
     *
     * @param url            要请求的url
     * @param retryTimes     请求失败时，最大重试次数
     * @param failureStrings a string list,if the return html contains any string in the list,
     *                       means this request is failed
     * @return 页面html
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String httpGet(String url, int retryTimes, List<String> failureStrings) throws Exception {
        return httpGet(url, null, retryTimes, failureStrings);
    }

    /**
     * 发送httpget请求
     *
     * @param url            要请求的url
     * @param headers        要添加的自定义请求头
     * @param retryTimes     请求失败时，最大重试次数
     * @param failureStrings a string list,if the return html contains any string in the list,
     *                       means this request is failed
     * @return 页面html
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String httpGet(String url, Map<String, String> headers, int retryTimes, List<String> failureStrings) throws InterruptedException {
        // http执行请求对象
        CloseableHttpClient closeableHttpClient = null;
        // http响应对象
        CloseableHttpResponse closeableHttpResponse = null;
        // 返回的html
        String html = null;
        try {
            closeableHttpClient = HttpClients.createDefault();
            // 定义http请求
            HttpGet httpGet = new HttpGet(url);
            // 添加通用请求头
            httpGet.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            httpGet.addHeader("Accept-Encoding", "gzip, deflate, sdch");
            httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
            httpGet.addHeader("Cache-Control", "no-cache");
            httpGet.addHeader("Connection", "keep-alive");
            httpGet.addHeader("Pragma", "no-cache");
            httpGet.addHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 " +
                            "Safari/537.36");
            //extract host header from url
            Pattern pattern = Pattern.compile("://(?<host>[^/]+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                httpGet.addHeader("Host", matcher.group("host"));
            }
            //设置自定义请求头
            if (headers != null && !headers.isEmpty()) {
                for (String key : headers.keySet()) {
                    if (httpGet.containsHeader(key)) {
                        httpGet.removeHeaders(key);
                    }
                    httpGet.addHeader(key, headers.get(key));
                }
            }
            // 执行http请求
            closeableHttpResponse = closeableHttpClient.execute(httpGet);
            // 获取响应的html
            HttpEntity httpEntity = closeableHttpResponse.getEntity();
            if (httpEntity != null) {
                html = EntityUtils.toString(httpEntity, "utf-8");
            }
            // 标记响应实体已经被消息，以释放资源
            EntityUtils.consume(httpEntity);
            //if this request is failure
            if (failureStrings != null && !failureStrings.isEmpty()) {
                for (String string : failureStrings) {
                    if (html.contains(string)) {
                        throw new Exception("request failed, the return html contains failureString: " + string);
                    }
                }
            }
            return html;
        } catch (Exception ex) {
            if (retryTimes <= 0) {
                log.error("请求页面失败，已达最大重试次数，url=" + url + ", 异常消息：" + ex.getMessage(), ex);
                return null;
            } else {
                log.error("请求页面失败，即将开始倒数第" + retryTimes + "次重试，url=" + url + ", 异常消息：" + ex.getMessage(), ex);
                TimeUnit.SECONDS.sleep(1);
                return httpGet(url, headers, --retryTimes, failureStrings);
            }
        } finally {
            try {
                if (closeableHttpResponse != null) {
                    closeableHttpResponse.close();
                }
                if (closeableHttpClient != null) {
                    closeableHttpClient.close();
                }
            } catch (Exception e) {
                log.error("HttpResponse或HttpClient资源释放失败,url:" + url, e);
            }
        }
    }

    /**
     * 发送httpPost请求
     *
     * @param url   要请求的url
     * @param paras 要传递的post参数
     * @return 页面html
     * @throws IOException
     */
    public static String httpPost(String url, Map<String, String> paras) {
        return httpPost(url, paras, null, 0);
    }

    /**
     * 发送httpPost请求
     *
     * @param url        要请求的url
     * @param paras      要传递的post参数
     * @param retryTimes 请求失败时，最大重试次数
     * @return 页面html
     * @throws IOException
     */
    public static String httpPost(String url, Map<String, String> paras, int retryTimes) {
        return httpPost(url, paras, null, retryTimes);
    }

    /**
     * 发送httpPost请求
     *
     * @param url        要请求的url
     * @param paras      要传递的post参数
     * @param headers    要添加的自定义请求头
     * @param retryTimes 请求失败时，最大重试次数
     * @return 页面html
     * @throws IOException
     */
    public static String httpPost(String url, Map<String, String> paras, Map<String, String> headers, int retryTimes) {
        // http执行请求对象
        CloseableHttpClient closeableHttpClient = null;

        // http响应对象
        CloseableHttpResponse closeableHttpResponse = null;

        // 返回的html
        String html = null;
        try {
            closeableHttpClient = HttpClients.createDefault();

            // 定义http请求
            HttpPost httpPost = new HttpPost(url);

            // 添加通用请求头
            httpPost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            httpPost.addHeader("Accept-Encoding", "gzip, deflate, sdch");
            httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
            httpPost.addHeader("Cache-Control", "no-cache");
            httpPost.addHeader("Connection", "keep-alive");
            httpPost.addHeader("Pragma", "no-cache");
            httpPost.addHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 " +
                            "Safari/537.36");

            //设置自定义请求头
            if (headers != null && !headers.isEmpty()) {
                for (String key : headers.keySet()) {
                    if (httpPost.containsHeader(key)) {
                        httpPost.removeHeaders(key);
                    }
                    httpPost.addHeader(key, headers.get(key));
                }
            }

            //添加请求参数
            if (paras != null && !paras.isEmpty()) {
                List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
                for (String key : paras.keySet()) {
                    BasicNameValuePair pair = new BasicNameValuePair(key, paras.get(key));
                    pairs.add(pair);
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs);
                httpPost.setEntity(entity);
            }

            // 执行http请求
            closeableHttpResponse = closeableHttpClient.execute(httpPost);

            // 获取响应的html
            HttpEntity httpEntity = closeableHttpResponse.getEntity();
            if (httpEntity != null) {
                html = EntityUtils.toString(httpEntity, "utf-8");
            }

            // 标记响应实体已经被消息，以释放资源
            EntityUtils.consume(httpEntity);
            return html;
        } catch (Exception ex) {
            if (retryTimes <= 0) {
                log.error("请求页面失败，已达最大重试次数，url=" + url + ", 异常消息：" + ex.getMessage(), ex);
                return null;
            } else {
                log.error("请求页面失败，即将开始倒数第" + retryTimes + "次重试，url=" + url + ", 异常消息：" + ex.getMessage(), ex);
                return httpPost(url, headers, --retryTimes);
            }
        } finally {
            try {
                if (closeableHttpResponse != null) {
                    closeableHttpResponse.close();
                }
                if (closeableHttpClient != null) {
                    closeableHttpClient.close();
                }
            } catch (Exception e) {
                log.error("HttpResponse或HttpClient资源释放失败,url:" + url, e);
            }
        }
    }

    /**
     * 使用firefox浏览器去请求一个网页
     *
     * @param url        页面url
     * @param retryTimes 请求失败时，最多重试的次数
     * @return 网页的html代码
     */
    public static String httpFirefox(String url, int retryTimes) {
        return httpFirefox(null, url, null, 0, retryTimes);
    }

    /**
     * 使用firefox浏览器去请求一个网页
     *
     * @param url                        页面url
     * @param waitElementXpaths          页面要等待加载完成的dom元素的xpath路径集合
     * @param waitElementsTimeoutSeconds 页面等待每个dom元素加载完成超时时间，单位为秒
     * @param retryTimes                 请求失败时，最多重试的次数
     * @return 网页的html代码
     */
    public static String httpFirefox(String url, List<String> waitElementXpaths, int
            waitElementsTimeoutSeconds, int retryTimes) {
        return httpFirefox(null, url, waitElementXpaths, waitElementsTimeoutSeconds, retryTimes);
    }

    /**
     * 使用firefox浏览器去请求一个网页
     *
     * @param firefoxBinPath             火狐浏览器可执行程序绝对路径
     * @param url                        页面url
     * @param waitElementXpaths          页面要等待加载完成的dom元素的xpath路径集合
     * @param waitElementsTimeoutSeconds 页面等待每个dom元素加载完成超时时间，单位为秒
     * @param retryTimes                 请求失败时，最多重试的次数
     * @return 网页的html代码
     */
    public static String httpFirefox(String firefoxBinPath, String url, List<String> waitElementXpaths, int
            waitElementsTimeoutSeconds, int retryTimes) {
        FireFoxWebBrowserPool pool = null;
        WebBrowser webBrowser = null;
        if (retryTimes < 0) {
            retryTimes = 0;
        }
        String html = null;
        try {
            //获取火狐浏览器池
            pool = FireFoxWebBrowserPool.getInstance(firefoxBinPath);
            //从池中获取一个火狐浏览器实例
            webBrowser = pool.getWebBrowser();
            while (webBrowser == null) {
                log.info("未能从火狐浏览器池中获到浏览器实例，可能池中暂时没有可用的浏览器实例，5秒后尝试重新获取");
                Thread.sleep(5000);
                webBrowser = pool.getWebBrowser();
            }
            //请求网页
            webBrowser.getWebDriver().manage().timeouts().pageLoadTimeout(waitElementsTimeoutSeconds, TimeUnit.SECONDS);
            webBrowser.getWebDriver().get(url);
            //等待元素加载
            if (waitElementXpaths != null && !waitElementXpaths.isEmpty()) {
                for (String domXpath : waitElementXpaths) {
                    try {
                        new WebDriverWait(webBrowser.getWebDriver(), waitElementsTimeoutSeconds).until
                                (ExpectedConditions.presenceOfElementLocated(By.xpath(domXpath)));
                    } catch (Exception ex) {
                        log.warn("火狐浏览器等待页面元素加载完成发生异常,url:" + url + ", 元素xpath:" + domXpath + "，异常信息：" + ex, ex);
                    }
                }
            }
            html = webBrowser.getWebDriver().getPageSource();
            //释放浏览器实例，回归浏览器池中
            if (pool != null && webBrowser != null) {
                pool.releaseWebBrowser(webBrowser);
            }
            return html;
        } catch (Exception ex) {
            if (retryTimes == 0) {
                log.error("firefox请求网页异常,已达最大重试次数：" + ex.getMessage(), ex);
                if (webBrowser != null) {
                    html = webBrowser.getWebDriver().getPageSource();
                    //释放浏览器实例，回归浏览器池中
                    if (pool != null) {
                        pool.releaseWebBrowser(webBrowser);
                    }
                    return html;
                }
                return null;
            } else {
                log.error("firefox请求网页异常,即将开始倒数第" + retryTimes + "重试，异常信息：" + ex.getMessage(), ex);
                //释放浏览器实例，回归浏览器池中
                if (pool != null && webBrowser != null) {
                    pool.releaseWebBrowser(webBrowser);
                }
                return httpFirefox(firefoxBinPath, url, waitElementXpaths, waitElementsTimeoutSeconds, --retryTimes);
            }
        }
    }
}
