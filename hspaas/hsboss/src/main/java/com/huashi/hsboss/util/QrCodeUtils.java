package com.huashi.hsboss.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

/**
 * TODO 二维码生成工具类
 * 
 * @author zhengying
 * @version V1.0
 * @date 2018年10月31日 下午6:06:20
 */
public class QrCodeUtils {

    public static String genQRCode(String content) {
        int width = 300;
        int height = 300;
        String format = "png";

        try {
            // 生成二维码
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, format, bao);
            String qrcode = Base64.getEncoder().encodeToString(bao.toByteArray());
            bao.flush();
            bao.close();
            return qrcode;
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
