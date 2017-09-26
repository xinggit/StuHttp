package common.crawler.util;
/**
 * Created by shencheng on 2017/6/2.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.net.URI;

public class HadoopUtils {
    /**
     * hadoop服务url列表
     */
    public String[] urls;

    /**
     * hadoop用户
     */
    public String user;

    /**
     * 写入成功的hadoop urlf索引
     */
    public int successIndex = 0;

    /**
     * 日志记录器
     */
    public final static Log log = LogFactory.getLog(HadoopUtils.class);

    /**
     * @param dbConfigFile hdfs server config file
     */
    public HadoopUtils(String dbConfigFile) {
        try {
            // 读取配置
            String dbConfigFilePath = null;
            if (StringUtils.isBlank(dbConfigFile)) {
                dbConfigFilePath = "db.json";
            } else {
                dbConfigFilePath = dbConfigFile;
            }
            String dbConfigStr = MyFileUtils.readFileToString(dbConfigFilePath, "utf-8");
            JSONObject config = JSON.parseObject(dbConfigStr);
            JSONArray jsonArray = config.getJSONArray("urls");
            // 初始化urls
            urls = new String[jsonArray.size()];
            for (int i = 0; i < urls.length; i++) {
                urls[i] = jsonArray.getString(i);
            }
            // 设置用户名
            user = config.getString("user");
        } catch (Exception e) {
            log.warn("HadoopUtis初始化失败，" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建文件到HDFS系统上
     *
     * @throws Exception
     */
    public String createHdfsFile(String filename, String fileContent) throws Exception {
        String currentUrl = null;
        FileSystem fileSystem = null;
        FSDataOutputStream fsDataOutputStream = null;

        while (true) {
            try {
                // 初始化hadoop配置
                Configuration fsConfig = new Configuration();
                fsConfig.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");

                // 写入路径
                Path filePath = new Path(filename);

                // 打开hadoop连接
                currentUrl = urls[successIndex];
                fileSystem = FileSystem.get(new URI(currentUrl), fsConfig, user);

                // 写入文件
                fsDataOutputStream = fileSystem.create(filePath, true);
                fsDataOutputStream.write(fileContent.getBytes("UTF-8"));

                return currentUrl;
            } catch (Exception e) {
                log.error(e.toString() + ", hadoop写入文件失败，当前服务器为" + currentUrl + ", 既将尝试下一个服务器, 文件名=" +
                        filename, e);
                // 尝试下一个hdfs服务器
                successIndex = ++successIndex % urls.length;
                Thread.sleep(5000);
                continue;
            } finally {
                try {
                    if (fsDataOutputStream != null) {
                        fsDataOutputStream.close();
                    }
                    if (fileSystem != null) {
                        fileSystem.close();
                    }
                } catch (Exception ex) {
                    log.error("hadoop写失败后关闭资源失败," + ex.toString(), ex);
                }
            }
        }
    }
}
