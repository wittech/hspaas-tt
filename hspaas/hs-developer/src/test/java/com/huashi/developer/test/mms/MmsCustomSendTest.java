package com.huashi.developer.test.mms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.util.SecurityUtil;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.constants.OpenApiCode.CommonApiCode;
import com.huashi.mms.template.constant.MmsTemplateContext;
import com.huashi.mms.template.domain.MmsMessageTemplateBody;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MmsCustomSendTest {

    String           url;
    FormBody.Builder bulider;
    String           apptype;

    @Before
    public void before() throws IOException {
        // url = "http://dev.hspaas.cn:8080/sms/send";
        url = "http://127.0.0.1:8080/mms/send";

        // String appkey = "hsjXxJ2gO75iOK";
        // String password = "468134467dac9849da8902ff953b2d6c";

        String appkey = "hsjXxJ2gO75iOK";
        String password = "e3293685e23847fce6a8afc532de6dac";
        
        // 华时boss
        String mobile = "15868193450";

        String time = System.currentTimeMillis() + "";
        apptype = AppType.WEB.getCode() + "";

        String title = "测试标题";

        List<MmsMessageTemplateBody>  bodies = new ArrayList<>();
        MmsMessageTemplateBody body = new MmsMessageTemplateBody();
        body.setMediaName("txt");
        body.setMediaType(MmsTemplateContext.MediaType.TEXT.getCode());
        body.setContent("测试彩信内容");

        bodies.add(body);

        MmsMessageTemplateBody imageBody = new MmsMessageTemplateBody();
        imageBody.setMediaName("jpg");
        imageBody.setMediaType(MmsTemplateContext.MediaType.IMAGE.getCode());
        imageBody.setContent(Base64.encodeBase64String(Files.readAllBytes(Paths.get("/Users/tenx/Documents/test/image/test.jpg"))));

        bodies.add(imageBody);

        System.out.println("彩信内容：" + JSON.toJSONString(bodies));

        // 1467735756927
        bulider = new FormBody.Builder()
                .add("appkey", appkey)
                .add("appsecret", SecurityUtil.md5Hex(password + mobile + time))
                .add("timestamp", time)
                .add("mobile", mobile)
                .add("title", title)
                .add("bodies", JSON.toJSONString(bodies))
                .add("attach", "test889")
                .add("extNumber","333")
                .add("callback","http://localhost:9999/sms/status_report");

    }

    @Test
    public void test() {
        Map<String, Object> result = JSON.parseObject(post(), new TypeReference<Map<String, Object>>() {
        });

        System.out.println(JSON.toJSONString(result));

        Assert.assertTrue(result.get("code").toString(),
                          result.get("code").toString().equals(CommonApiCode.COMMON_SUCCESS.getCode() + ""));
    }

    private String post() {
        Request request = new Request.Builder().url(url).post(bulider.build()).addHeader("apptype", apptype).build();
        try {
            Response response = new OkHttpClient().newCall(request).execute();
            if (response.isSuccessful()) return response.body().string();
            else System.err.println("URL:{}, 回执失败");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException(String.format("URL: %s 调用失败！", url));
    }
}
