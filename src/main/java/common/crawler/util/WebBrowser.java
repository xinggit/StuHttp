package common.crawler.util;

import org.openqa.selenium.WebDriver;

import java.util.Iterator;

/**
 * Created by Administrator on 2017/6/7.
 */
public class WebBrowser {
    /**
     * 浏览器实例
     */
    private WebDriver webDriver;

    /**
     * 浏览器实例id
     */
    private String id;

    /**
     * 被创建时间
     */
    private long createTime;

    /**
     * 是否锁定，锁定后该浏览器实例不能随机分配，只有传入正确的浏览器实例id才能获取该实例或者解锁，true为锁定状态，false为未锁定状态
     */
    private boolean locked;

    /**
     * 清理WebDriver,如果打开了多个窗口，只保留一个窗口，并清除所有cookies
     */
    public void clean() {
        Iterator<String> windows = this.webDriver.getWindowHandles().iterator();
        String lastWindow = null;
        if (windows.hasNext()) {
            lastWindow = windows.next();
        }
        while (windows.hasNext()) {
            this.webDriver.switchTo().window(windows.next());
            this.webDriver.close();
        }
        if (lastWindow != null) {
            this.webDriver.switchTo().window(lastWindow);
        }
        this.webDriver.manage().deleteAllCookies();
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
