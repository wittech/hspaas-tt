package com.huashi.hsboss.config;

/**
 * 初始化配置
 * @author ym
 * @created_at 2016年6月22日下午2:09:50
 */
public class InitConfig {

	public static String fileSaveRoot;
	
	public static String fileStaticAddr;

	public static String getFileSaveRoot() {
		return fileSaveRoot;
	}

	public static void setFileSaveRoot(String fileSaveRoot) {
		InitConfig.fileSaveRoot = fileSaveRoot;
	}

	public static String getFileStaticAddr() {
		return fileStaticAddr;
	}

	public static void setFileStaticAddr(String fileStaticAddr) {
		InitConfig.fileStaticAddr = fileStaticAddr;
	}
	
	
}
