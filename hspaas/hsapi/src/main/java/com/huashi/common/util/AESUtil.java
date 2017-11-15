package com.huashi.common.util;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AESUtil {

	public static final String KEY_ALGORITHM = "AES";

	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

	public static final String UTF_8 = "UTF-8"; // 默认编码

	// 转换密钥
	private static Key toKey(byte[] key) {
		KeyGenerator keyGenerator = null;
		try {
			keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			return null;
		}

		SecureRandom secureRandom = null;
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			return null;
		}
		secureRandom.setSeed(key);
		keyGenerator.init(128, secureRandom);

		// 实例化DES的材料
		SecretKey secretKey = keyGenerator.generateKey();
		SecretKeySpec sKeySpec = new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
		return sKeySpec;
	}

	// 解密
	public static byte[] decrypt(byte[] data, byte[] key) {
		Key k = toKey(key);
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			// 初始化，设置为解密模式
			cipher.init(Cipher.DECRYPT_MODE, k);
			return cipher.doFinal(data);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// 加密
	public static byte[] encrypt(byte[] data, byte[] key) {
		// 还原密钥
		Key k = toKey(key);
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, k);
			return cipher.doFinal(data);
		} catch (Exception ex) {
			return null;
		}
	}

	// 加密
	public static String encrypt(String data, String key) {
		try {
			// 还原密钥
			Key k = toKey(key.getBytes(UTF_8));
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, k);
			return Base64.encodeBase64String(cipher.doFinal(data.getBytes(UTF_8)));
		} catch (Exception ex) {
			return null;
		}
	}

	// 生成密钥
	public static byte[] initKey() throws Exception {
		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
		kg.init(256);
		SecretKey secretKey = kg.generateKey();
		return secretKey.getEncoded();
	}

	// public static void main(String args[]) throws Exception {
	// String str = "3321__+*334郑营SS3E3E3" ;
	// byte[] inputData = str.getBytes();
	// System.out.println("原文:"+str);
	//
	// //初始化密钥
	// byte[] key = "hehe".getBytes();
	// System.out.println("密钥:"+ Base64.encodeBase64String(key));
	//
	// //加密
	// inputData = AESUtil.encrypt(inputData,key);
	// System.out.println("加密后:"+ new String(Hex.encode(inputData)));
	//
	// //解密
	// byte[] outputData = AESUtil.decrypt(inputData,key);
	// String outstr = new String(outputData);
	// System.out.println(outstr);
	// }

}
