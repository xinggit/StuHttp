package common.crawler.util;

import javax.servlet.http.Cookie;

/**
 * cookie操作帮助类
 * Created by shencheng on 2017/5/21.
 */
public class CookieUtils {
    /**
     * 获取cookie值
     *
     * @param cookies
     * @param cookieName
     * @return 返回指定的cookie值，如果找不到返回null
     */
    public static String getCookieValue(Cookie[] cookies, String cookieName) {
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().toLowerCase().equals(cookieName.toLowerCase())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
