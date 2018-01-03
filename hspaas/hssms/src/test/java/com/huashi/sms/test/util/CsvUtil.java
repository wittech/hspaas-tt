package com.huashi.sms.test.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CsvUtil {

    /**
     * 
     * TODO 解析CSV 文件数据
     * 
     * @param localPath
     * @return
     */
    public static List<String[]> csvAnalysis(String localPath) {
        List<String[]> list = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(localPath))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                // 第一行不做处理
//                lineNo++;
//                if (lineNo == 1) {
//                    continue;
//                }

                // app_no,app_userid,app_ip,app_name,app_address,app_email,app_idcode,app_linkman,app_iccode,app_orgcode,app_taxcode,app_cerno,app_createtime

                String[] data = line.split(",");
                
                list.add(data);
            }
            
            System.out.println("数据共：" + list.size());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
