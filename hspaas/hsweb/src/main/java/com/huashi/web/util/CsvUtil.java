package com.huashi.web.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvUtil.class);

    /**
     * TODO 读取文件第一列信息
     * 
     * @param inputStream
     * @return
     */
    public static Set<String> readFirstColomn(InputStream inputStream) {
        Set<String> list = new HashSet<>();
        int lineNo = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")))) {
            String line = null;
            String[] data = null;
            while ((line = reader.readLine()) != null) {
                // 第一行不做处理
                lineNo++;
                if (lineNo == 1) {
                    continue;
                }

                data = line.split(",");
                if (data == null || data.length == 0) {
                    continue;
                }

                list.add(data[0]);
            }

        } catch (Exception e) {
            LOGGER.error("CSV data read failed", e);
        }
        return list;
    }
}
