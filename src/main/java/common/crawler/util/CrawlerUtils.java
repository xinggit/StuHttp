package common.crawler.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 爬虫工具类
 * Created by shencheng on 2017/6/16.
 */
public class CrawlerUtils {
    /**
     * 页码点位符
     */
    public static final String PAGE_INDEX_PLACE_HOLDER = "pageIndexPlaceHolder";

    /**
     * 根据条件生成所有可行的url
     *
     * @param baseUrl    带占位符的原始url
     * @param conditions 要组装的条件map,key为条件占位符，value为该条件的所有值组合
     * @return
     */
    public static List<String> generateTaskUrls(String baseUrl, Map<String, List<String>> conditions) {
        List<String> urls = new ArrayList<String>();
        List<String> tempUrls = new ArrayList<String>();
        if (StringUtils.isBlank(baseUrl)) {
            throw new NullPointerException("baseUrl不能为空或者null");
        }
        if (conditions != null && !conditions.isEmpty()) {
            for (String key : conditions.keySet()) {
                List<String> values = conditions.get(key);
                if (urls.isEmpty()) {
                    for (String value : values) {
                        urls.add(baseUrl.replace(key, value));
                    }
                } else {
                    for (String value : values) {
                        for (String url : urls) {
                            String urlNext = url.replace(key, value);
                            tempUrls.add(urlNext);
                        }
                    }
                    urls.clear();
                    urls.addAll(tempUrls);
                    tempUrls.clear();
                }
            }
        }
        Collections.sort(urls);
        return urls;
    }
}
