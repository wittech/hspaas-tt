package com.huashi.hsboss.web.controller;

import com.huashi.hsboss.annotation.AuthCode;
import com.huashi.hsboss.constant.OperCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.jfinal.ext.route.ControllerBind;

@ControllerBind(controllerKey = "/upload")
public class UploadController extends BaseController {

    @AuthCode(code = OperCode.OPER_CODE_COMMON)
    public void uploadFile() {

//        List<UploadFile> uploads = this.getFiles();
//        UploadFile uploadFile = this.getFile();
//
//        String fileName = uploadFile.getOriginalFileName();

//        File file = uploadFile.getFile();
//        FileService fs = new FileService();
//        File t = new File("S:\\file\\" + UUID.randomUUID().toString());
//        try {
//            t.createNewFile();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        fs.fileChannelCopy(file, t);
//        file.delete();
        this.renderHtml("success,<a href=\"./\">back</a>");
    }
}
