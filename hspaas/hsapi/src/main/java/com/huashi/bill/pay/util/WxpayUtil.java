package com.huashi.bill.pay.util;


public class WxpayUtil {

//	public static byte[] encodeQrcode(String content) throws QrcodeFaildException {
//		if (content == null || "".equals(content))
//			throw new QrcodeFaildException("微信支付二维码生成失败");
//		
//		multiFormatWriter multiFormatWriter = new MultiFormatWriter();
//		Map hints = new HashMap();
//		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 设置字符集编码类型
//		BitMatrix bitMatrix = null;
//		try {
//			bitMatrix = multiFormatWriter.encode(content,
//					BarcodeFormat.QR_CODE, 300, 300, hints);
//			BufferedImage image = toBufferedImage(bitMatrix);
//			// 输出二维码图片流
//			try {
//				ImageIO.write(image, "png", response.getOutputStream());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} catch (WriterException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//	}
}
