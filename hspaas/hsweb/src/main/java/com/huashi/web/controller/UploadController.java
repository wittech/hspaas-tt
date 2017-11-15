package com.huashi.web.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.huashi.common.util.FileUploadUtil;
import com.huashi.web.filter.PermissionClear;

@Controller
@PermissionClear
@RequestMapping("/upload")
public class UploadController extends BaseController {

	/**
	 * 
	 * TODO 文件上传
	 * 
	 * @param file
	 * @return
	 */
	@RequestMapping(value = "/transport")
	@ResponseBody
	public FileUploadUtil.FileUploadResult transport(@RequestParam("file") MultipartFile file) {
		try {
			return FileUploadUtil.upload(file.getInputStream(), file.getOriginalFilename(), tmpStoreDirectory);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
