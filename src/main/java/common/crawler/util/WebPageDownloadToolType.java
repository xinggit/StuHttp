package common.crawler.util;

/**
 * 请求web网页所使用的工具类型
 * Created by shencheng on 2017/6/7.
 */
public enum WebPageDownloadToolType {
    /**
     * 使用apache httpclient
     */
    HTTPCLIENT,
    /**
     * 使用谷歌浏览器
     */
    CHROME,
    /**
     * 使用火狐浏览器
     */
    FIREFOX
}
