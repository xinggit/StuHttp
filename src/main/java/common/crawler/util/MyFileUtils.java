package common.crawler.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 文件读写工具类
 * Created by shencheng on 2017/6/5.
 */
public class MyFileUtils {
    /**
     * 读取文件到字符串
     *
     * @param filename 文件路径
     * @param encoding 编码
     * @return 文件内容字符串
     * @throws IOException
     */
    public static String readFileToString(String filename, String encoding) throws IOException {
        return IOUtils.toString(MyFileUtils.getFileInputStream(filename), encoding);
    }

    /**
     * 读取文件所有行
     *
     * @param filename 文件路径
     * @param encoding 编码
     * @return 文件所有行
     * @throws IOException
     */
    public static List<String> readLines(String filename, String encoding) throws IOException {
        return IOUtils.readLines(MyFileUtils.getFileInputStream(filename), encoding);
    }

    /**
     * 从文本文件中读取不重复并且不为空的行
     *
     * @param filename 要读取的文件名
     * @param encoding 编码
     * @return 文本文件中不重复的行集合
     * @throws IOException
     */
    public static List<String> readUniqueAndNotEmptyLines(String filename, String encoding) throws IOException {
        List<String> uniqueLines = new ArrayList<String>();
        List<String> lines = MyFileUtils.readLines(filename, encoding);
        if (lines != null && !lines.isEmpty()) {
            for (String line : lines) {
                if (!StringUtils.isBlank(line) && !uniqueLines.contains(line)) {
                    uniqueLines.add(line);
                }
            }
        }
        return uniqueLines;
    }

    /**
     * 从文本文件中读取不重复并且不为空的行,并为每行添加指定的后缀
     *
     * @param filename 要读取的文件名
     * @param encoding 编码
     * @param suffix   要添加的后缀
     * @return 文本文件中不重复的行集合
     * @throws IOException
     */
    public static List<String> readUniqueAndNotEmptyLinesWithSuffix(String filename, String encoding, String suffix)
            throws
            IOException {
        List<String> uniqueLines = new ArrayList<String>();
        List<String> lines = MyFileUtils.readLines(filename, encoding);
        if (lines != null && !lines.isEmpty()) {
            for (String line : lines) {
                if (!StringUtils.isBlank(line) && !uniqueLines.contains(line)) {
                    uniqueLines.add(line + suffix);
                }
            }
        }
        return uniqueLines;
    }

    /**
     * 将所有行写入文本文件
     *
     * @param filename 要写入的文件路径
     * @param encoding 编码
     * @param lines    要写入的行
     * @throws IOException
     */
    public static void writeLines(String filename, String encoding, Collection<String> lines) throws IOException {
        FileUtils.writeLines(new File(filename), encoding, lines);
    }

    /**
     * 从类路径或者maven的Resouce目录下获取文件流
     *
     * @param filename 文件名称
     * @return 文件
     * @throws IOException
     */
    public static InputStream getFileInputStream(String filename) throws IOException {
        File file = new File(filename);
        InputStream inputStream = null;
        if (file.exists()) {
            inputStream = new FileInputStream(file);
        } else {
            inputStream = MyFileUtils.class.getClassLoader().getResourceAsStream(filename);
        }
        return inputStream;
    }
}
