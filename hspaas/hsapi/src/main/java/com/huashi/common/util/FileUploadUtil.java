package com.huashi.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public final class FileUploadUtil {

	public static final char[] N36_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	private static String randomFileName() {
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return String.format("%s%d", f.format(new Date()), new Random().nextInt(100));
	}

	public static String dateFileName() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date());
	}

	/**
	 * 获取最终重命名后的文件路径
	 * 
	 * @param uploadPath
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static String getFinalPath(String uploadPath, String fileName, String suffix) throws Exception {
		StringBuilder finalPath = new StringBuilder();
		finalPath.append(uploadPath).append(File.separator).append(fileName).append(suffix);
		return finalPath.toString();
	}

	public static String fileName(String fileNamePrefix) {
		return String.format("%s%s", fileNamePrefix, randomFileName());
	}

	public static String suffix(String oldFileName) {
		return oldFileName.substring(oldFileName.lastIndexOf("."));
	}

	/**
	 * 文件上传并返回最终上传的文件路径
	 * 
	 * @param file
	 * @param path
	 * @param name
	 * @return
	 * @return
	 * @throws Exception
	 */
	// public static Object[] upload(File file, String fileNamePrefix,
	// String uploadPath, String oldFileName) throws Exception {
	// String finalPath = "";
	// int availableSize = 0;
	// String filename = fileName(fileNamePrefix);
	// String suffix = suffix(oldFileName);
	// FileInputStream inputStream = null;
	// FileOutputStream outputStream = null;
	// try {
	// finalPath = getFinalPath(uploadPath, filename, suffix);
	// inputStream = new FileInputStream(file);
	// availableSize = inputStream.available();
	// outputStream = new FileOutputStream(finalPath);
	// byte[] buf = new byte[1024];
	// int length = 0;
	// while ((length = inputStream.read(buf)) != -1) {
	// outputStream.write(buf, 0, length);
	// }
	// } catch (Exception e) {
	// finalPath = "";
	// } finally {
	// outputStream.close();
	// inputStream.close();
	// }
	// return new Object[] { finalPath, availableSize, filename, suffix };
	// }

	public static void directory(String path) {
		if (StringUtils.isEmpty(path))
			return;
		File f = new File(path);
		if (!f.exists() || (!f.isDirectory())) {
			f.mkdir();
		}
	}

	public static FileUploadResult upload(File file, String filename, String uploadpath, String prefix) {
		String imgName = "";
		String realPath = "";
		String fullPath = "";
		String diskPath = "";
		long availableSize = 0l;
		try {
			InputStream is = new FileInputStream(file);
			String ext = filename.substring(filename.indexOf("."), filename.length());
			imgName = getRandFileName();
			String newName = imgName + ext;
			String dateFileName = dateFileName();
			String filePath = uploadpath + File.separator + getDiskPath(dateFileName);
			File outFile = new File(filePath);
			if (!outFile.exists()) {
				outFile.mkdirs();
			}
			diskPath = filePath + File.separator + newName;
			File fileName = new File(diskPath);
			fileName.createNewFile();
			availableSize = is.available();
			OutputStream os = new FileOutputStream(fileName);
			byte[] b = new byte[1024];
			int j;
			while ((j = is.read(b)) != -1) {
				os.write(b, 0, j);
			}
			is.close();
			os.flush();
			os.close();
			realPath = prefix + String.format("/%s/%s", getDomainPath(dateFileName), newName);
			// This value is not use for this moment
			fullPath = String.format("%s/%s/%s", uploadpath, getDomainPath(dateFileName), newName);
		} catch (IOException e) {
			return new FileUploadUtil.FileUploadResult(FileUploadResult.FAILURE);
		}
		return new FileUploadResult(imgName, realPath, diskPath, fullPath, availableSize, FileUploadResult.SUCCESS);
	}
	
	public static FileUploadResult upload(InputStream inputStream, String filename, String uploadpath) {
		return upload(inputStream, filename, uploadpath, "");
	}
	
	public static FileUploadResult upload(InputStream inputStream, String filename, String uploadpath, String prefix) {
		String imgName = "";
		String realPath = "";
		String fullPath = "";
		String diskPath = "";
		long availableSize = 0l;
		try {
			String ext = filename.substring(filename.indexOf("."), filename.length());
			imgName = getRandFileName();
			String newName = imgName + ext;
			String dateFileName = dateFileName();
			String filePath = uploadpath + File.separator + getDiskPath(dateFileName);
			File outFile = new File(filePath);
			if (!outFile.exists()) {
				outFile.mkdirs();
			}
			diskPath = filePath + File.separator + newName;
			File fileName = new File(diskPath);
			fileName.createNewFile();
			availableSize = inputStream.available();
			OutputStream os = new FileOutputStream(fileName);
			byte[] b = new byte[1024];
			int j;
			while ((j = inputStream.read(b)) != -1) {
				os.write(b, 0, j);
			}
			inputStream.close();
			os.flush();
			os.close();
			realPath = prefix + String.format("/%s/%s", getDomainPath(dateFileName), newName);
			// This value is not use for this moment
			fullPath = String.format("%s/%s/%s", uploadpath, getDomainPath(dateFileName), newName);
		} catch (IOException e) {
			return new FileUploadUtil.FileUploadResult(FileUploadResult.FAILURE);
		}
		return new FileUploadResult(imgName, realPath, diskPath, fullPath, availableSize, FileUploadResult.SUCCESS);
	}
	
	public static FileUploadResult upload(byte[] file, String filename, String uploadpath) {
		return upload(file, filename, uploadpath, "");
	}
	
	public static FileUploadResult upload(byte[] file, String filename, String uploadpath, String prefix) {
		String imgName = "";
		String realPath = "";
		String fullPath = "";
		String diskPath = "";
		long availableSize = 0l;
		try {
			String ext = filename.substring(filename.indexOf("."), filename.length());
			imgName = getRandFileName();
			String newName = imgName + ext;
			String dateFileName = dateFileName();
			String filePath = uploadpath + File.separator + getDiskPath(dateFileName);
			File outFile = new File(filePath);
			if (!outFile.exists()) {
				outFile.mkdirs();
			}
			diskPath = filePath + File.separator + newName;
			File fileName = new File(diskPath);
			fileName.createNewFile();
			availableSize = file.length;
			Files.write(Paths.get(diskPath), file);
			realPath = prefix + String.format("/%s/%s", getDomainPath(dateFileName), newName);
			// This value is not use for this moment
			fullPath = String.format("%s/%s/%s", uploadpath, getDomainPath(dateFileName), newName);
		} catch (IOException e) {
			return new FileUploadUtil.FileUploadResult(FileUploadResult.FAILURE);
		}
		return new FileUploadResult(imgName, realPath, diskPath, fullPath, availableSize, FileUploadResult.SUCCESS);
	}

	public static String getRandFileName() {
		String name = longToN36(System.currentTimeMillis());
		return RandomStringUtils.random(4, N36_CHARS) + name;
	}

	public static String longToN36(long l) {
		return longToNBuf(l, N36_CHARS).reverse().toString();
	}

	private static StringBuilder longToNBuf(long l, char[] chars) {
		int upgrade = chars.length;
		StringBuilder result = new StringBuilder();
		int last;
		while (l > 0) {
			last = (int) (l % upgrade);
			result.append(chars[last]);
			l /= upgrade;
		}
		return result;
	}

	/**
	 * 
	 * TODO 获取硬盘图片路径
	 * 
	 * @param date
	 * @return
	 */
	public static String getDiskPath(String date) {
		StringBuilder fileName = new StringBuilder("");
		for (String d : date.split("-")) {
			fileName.append(d).append(File.separator);
		}
		return fileName.toString().substring(0, fileName.length() - 1);
	}

	/**
	 * 
	 * TODO 获取域名图片路径
	 * 
	 * @param date
	 * @return
	 */
	public static String getDomainPath(String date) {
		StringBuilder fileName = new StringBuilder("");
		for (String d : date.split("-")) {
			fileName.append(d).append("/");
		}
		return fileName.toString().substring(0, fileName.length() - 1);
	}

	public static String buildFileName(String suffixName) {
		return String.format("%s%s%s%s", getDiskPath(dateFileName()), File.separator, getRandFileName(), suffixName);
	}

	public static String parsePathToDiskMode(String fileName) {
		if (StringUtils.isEmpty(fileName))
			return "";
		return fileName.replaceAll("/", "\\" + File.separator);
	}

	public static String parsePathToUrlMode(String fileName) {
		if (StringUtils.isEmpty(fileName))
			return "";
		return fileName.replaceAll("\\" + File.separator, "/");
	}

	public static class FileUploadResult {

		private String filename;// 随机生成文件名称，12位字母和数字随机码
		private String realPath;// 文件相对路径，包括按日期生成的目录结构，如
								// /2014/11/11/fcvpi24rx3bq.jpg
		private long fileSize;// 文件占用硬盘空间大小
		private String diskPath; // 物理硬盘全路径
		private int result;// 最终处理结果
		private String fullPath;
		public static final int FAILURE = 0;
		public static final int SUCCESS = 1;

		public FileUploadResult(String filename, String realPath, String fullpath, long fileSize, int result) {
			super();
			this.filename = filename;
			this.realPath = realPath;
			this.fileSize = fileSize;
			this.result = result;
			this.fullPath = fullpath;
		}

		public FileUploadResult(String filename, String realPath, String diskPath, String fullpath, long fileSize,
				int result) {
			super();
			this.filename = filename;
			this.realPath = realPath;
			this.diskPath = diskPath;
			this.fileSize = fileSize;
			this.result = result;
			this.fullPath = fullpath;
		}

		public FileUploadResult(int result) {
			super();
			this.result = result;
		}

		public String getRealPath() {
			return realPath;
		}

		public void setRealPath(String realPath) {
			this.realPath = realPath;
		}

		public long getFileSize() {
			return fileSize;
		}

		public void setFileSize(long fileSize) {
			this.fileSize = fileSize;
		}

		public int getResult() {
			return result;
		}

		public void setResult(int result) {
			this.result = result;
		}

		public String getFilename() {
			return filename;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}

		public String getFullPath() {
			return fullPath;
		}

		public void setFullPath(String fullPath) {
			this.fullPath = fullPath;
		}

		public String getDiskPath() {
			return diskPath;
		}

		public void setDiskPath(String diskPath) {
			this.diskPath = diskPath;
		}

	}

}
