package common.crawler.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 火狐浏览器池
 * Created by shencheng on 2017/6/7.
 */
public class FireFoxWebBrowserPool {
    /**
     * 单例实例
     */
    private static FireFoxWebBrowserPool INSTANCE;

    /**
     * 日志工具
     */
    private final static Log log = LogFactory.getLog(FireFoxWebBrowserPool.class);

    /**
     * 默认池容量
     */
    private static int CAPACITY_DEFAULT = 10;

    /**
     * 火狐浏览器可执行文件绝对路径名
     */
    private String webBrowerBinPath;

    /**
     * 存放浏览器实例
     */
    private Map<String, WebBrowser> webBrowserMap;

    /**
     * 池容量，池中最多可以存放的浏览器实例个数，默认为10，当设置的capacity大于当前的capacity时，以大的capacity为准
     */
    private int capacity = CAPACITY_DEFAULT;

    /**
     * 当前浏览器池中实际已经创建的实例个数
     */
    private int count;

    /**
     * 当前浏览器地中可用的（空闲的）实例个数
     */
    private int availableCount;

    /**
     * 获取当前池容量
     *
     * @return 当前池最大容量，池中最大可创建浏览器实例的个数
     */
    public static int getCapacity() {
        synchronized (FireFoxWebBrowserPool.class) {
            if (INSTANCE != null) {
                return INSTANCE.capacity;
            } else {
                return 0;
            }
        }
    }

    /**
     * 获取当前池中已经创建的实例个数
     *
     * @return 当前池中已经创建的实例个数
     */
    public static int getCount() {
        synchronized (FireFoxWebBrowserPool.class) {
            if (INSTANCE == null) {
                return 0;
            } else {
                return INSTANCE.count;
            }
        }
    }

    /**
     * 获取当前池中可用的浏览器实例个数
     *
     * @return 当前池中可用的浏览器实例个数
     */
    public static int getAvailableCount() {
        synchronized (FireFoxWebBrowserPool.class) {
            if (INSTANCE == null) {
                return 0;
            } else {
                return INSTANCE.availableCount;
            }
        }
    }

    private FireFoxWebBrowserPool() {
    }

    /**
     * 获取火狐浏览器池实例
     *
     * @return 火狐浏览器池实例
     */
    public static FireFoxWebBrowserPool getInstance() {
        return FireFoxWebBrowserPool.getInstance(null, CAPACITY_DEFAULT);
    }

    /**
     * 获取火狐浏览器池实例
     *
     * @param webBrowerBinPath 火狐浏览器可执行文件绝对路径名
     * @return 火狐浏览器池实例
     */
    public static FireFoxWebBrowserPool getInstance(String webBrowerBinPath) {
        return FireFoxWebBrowserPool.getInstance(webBrowerBinPath, CAPACITY_DEFAULT);
    }

    /**
     * 获取火狐浏览器池实例
     *
     * @param webBrowerBinPath 火狐浏览器可执行文件绝对路径名
     * @param capacity         初始化容量值，默认为10，当设置的capacity大于当前的capacity时，以大的capacity为准
     * @return 火狐浏览器池实例
     */
    public static FireFoxWebBrowserPool getInstance(String webBrowerBinPath, int capacity) {
        if (INSTANCE == null) {
            synchronized (FireFoxWebBrowserPool.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FireFoxWebBrowserPool();
                    INSTANCE.capacity = capacity;
                    if (StringUtils.isBlank(webBrowerBinPath)) {
                        //window默认路径
                        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                            INSTANCE.webBrowerBinPath = "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe";
                            File file = new File(INSTANCE.webBrowerBinPath);
                            if (!file.exists()) {
                                INSTANCE.webBrowerBinPath = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
                            }
                        } else {
                            //linux默认路径
                            INSTANCE.webBrowerBinPath = "/usr/lib64/firefox/firefox";
                        }
                    } else {
                        INSTANCE.webBrowerBinPath = webBrowerBinPath;
                    }
                    System.setProperty("webdriver.firefox.bin", INSTANCE.webBrowerBinPath);
                    INSTANCE.webBrowserMap = new HashMap<String, WebBrowser>();
                    //启动自动清理线程
                    INSTANCE.enableAutoClear(20);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 获取取一个空闲浏览器实例
     *
     * @return 池中第一个找到的未被锁定的浏览器实例
     */
    public WebBrowser getWebBrowser() {
        return getWebBrowser(null);
    }

    /**
     * 获取取指定id的空闲的浏览器实例
     *
     * @param id 浏览器实例id
     * @return 返回指定id的浏览器实例，如果id为null或空则返回池中第一个找到的未被锁定的浏览器实例
     */
    public WebBrowser getWebBrowser(String id) {
        synchronized (FireFoxWebBrowserPool.class) {
            WebBrowser webBrowser = getWebBrowserFromMap(id);
            if (webBrowser == null) {
                if (INSTANCE.count < INSTANCE.capacity) {
                    webBrowser = new WebBrowser();
                    webBrowser.setLocked(false);
                    if (StringUtils.isBlank(id)) {
                        webBrowser.setId(UUID.randomUUID().toString());
                    } else {
                        webBrowser.setId(id);
                    }
                    webBrowser.setCreateTime(System.currentTimeMillis());
                    WebDriver webDriver = new FirefoxDriver();
                    webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
                    webBrowser.setWebDriver(webDriver);
                    INSTANCE.count++;
                } else {
                    log.warn("获取浏览器实例失败，浏览器池已经满，当前容量为：" + INSTANCE.capacity + ", " +
                            "当前实例个数为：" + INSTANCE
                            .count);
                }
            } else {
                INSTANCE.webBrowserMap.remove(webBrowser.getId());
                INSTANCE.availableCount--;
            }
            log.info("从火狐浏览器池中获取到浏览器实例，实例id为：" + webBrowser.getId());
            return webBrowser;
        }
    }

    /**
     * 释放浏览器实例，回归到浏览器池中
     *
     * @param webBrowser 要释放的浏览器实例
     * @param locked     释放回浏览器池里时是否加锁，true为加锁，false为解锁，如果加锁则从池中获取该实例时必须指定其正确的id才能获取
     */
    public void releaseWebBrowser(WebBrowser webBrowser, boolean locked) {
        synchronized (FireFoxWebBrowserPool.class) {
            webBrowser.setLocked(locked);
            if (!INSTANCE.webBrowserMap.containsKey(webBrowser.getId())) {
                INSTANCE.webBrowserMap.put(webBrowser.getId(), webBrowser);
                INSTANCE.availableCount++;
            }
            String lockMessage = locked ? "锁定" : "解锁";
            log.info("将浏览器实例释放，回归火狐浏览器池中，并" + lockMessage + "，实例id为：" + webBrowser.getId());
        }
    }

    /**
     * 释放并解锁浏览器实例，回归到浏览器池中
     *
     * @param webBrowser 要释放的浏览器实例
     */
    public void releaseWebBrowser(WebBrowser webBrowser) {
        releaseWebBrowser(webBrowser, false);
    }

    /**
     * 从浏览器池map中获取一个实例
     *
     * @param id 浏览器实例的id
     * @return 返回指定id的浏览器实例，如果id为null或空则返回map中第一个未被锁定的浏览器实例
     */
    private WebBrowser getWebBrowserFromMap(String id) {
        if (!INSTANCE.webBrowserMap.isEmpty()) {
            if (StringUtils.isBlank(id)) {
                for (String key : INSTANCE.webBrowserMap.keySet()) {
                    WebBrowser webBrowser = INSTANCE.webBrowserMap.get(key);
                    if (!webBrowser.isLocked()) {
                        return webBrowser;
                    }
                }
            } else {
                return INSTANCE.webBrowserMap.get(id);
            }
        }
        return null;
    }

    /**
     * 火狐浏览器池定期自动清理任务
     */
    private void enableAutoClear(final int maxWebBrowserAliveMinutes) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        //每隔一分钟检查一次
                        Thread.sleep(60 * 1000);
                        synchronized (FireFoxWebBrowserPool.class) {
                            //清理前
                            log.info("火狐浏览器池自动清理线程即将进行清理，当前池容量：" + INSTANCE.capacity + ", 当前已经创建的实例个数:" +
                                    INSTANCE.count + ", " +
                                    "池中可用的实例个数：" + INSTANCE
                                    .availableCount);
                            //小于此创建时间的浏览器实例将被清理
                            long needClearCreateTime = System.currentTimeMillis() - maxWebBrowserAliveMinutes * 60 *
                                    1000;
                            //存储将要被清理的浏览器实例id
                            List<String> needClearKeys = new ArrayList<String>();
                            //分析要被清理的浏览器实例
                            for (String key : INSTANCE.webBrowserMap.keySet()) {
                                if (INSTANCE.webBrowserMap.get(key).getCreateTime() < needClearCreateTime) {
                                    needClearKeys.add(key);
                                }
                            }
                            //执行清理
                            for (String key : needClearKeys) {
                                WebBrowser webBrowser = INSTANCE.webBrowserMap.get(key);
                                webBrowser.getWebDriver().quit();
                                INSTANCE.webBrowserMap.remove(key);
                                INSTANCE.count--;
                                INSTANCE.availableCount--;
                            }
                            //清理后
                            log.info("火狐浏览器池自动清理线程已经完成清理，当前池容量：" + INSTANCE.capacity + ", 当前已经创建的实例个数:" +
                                    INSTANCE.count + ", " +
                                    "池中可用的实例个数：" + INSTANCE
                                    .availableCount + ", 被清理掉的个数：" + needClearKeys.size());
                        }
                    } catch (Exception ex) {
                        log.error("火狐浏览器池自动清理线程运行异常：" + ex.getMessage(), ex);
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        log.info("火狐浏览器池自动清理线程启动，存活超过" + maxWebBrowserAliveMinutes + "分钟的浏览器实例将被销毁。");
    }

    /**
     * 关闭池，并关闭所有打开的浏览器实例
     */
    public static void close() {
        if (INSTANCE != null && !INSTANCE.webBrowserMap.isEmpty()) {
            for (String key : INSTANCE.webBrowserMap.keySet()) {
                WebBrowser webBrowser = INSTANCE.webBrowserMap.get(key);
                webBrowser.getWebDriver().quit();
            }
        }
    }
}
