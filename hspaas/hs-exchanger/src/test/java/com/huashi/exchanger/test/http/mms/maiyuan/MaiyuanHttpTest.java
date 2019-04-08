package com.huashi.exchanger.test.http.mms.maiyuan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.resolver.mms.http.maiyuan.MmsMaiyuanPassageResolver;
import com.huashi.mms.passage.domain.MmsPassageParameter;
import com.huashi.mms.template.constant.MmsTemplateContext.MediaType;
import com.huashi.mms.template.domain.MmsMessageTemplateBody;

public class MaiyuanHttpTest {

    private Logger               logger    = LoggerFactory.getLogger(getClass());

    MmsMaiyuanPassageResolver       resolver  = null;
    MmsPassageParameter          parameter = null;
    String                       mobile    = null;
    String                       title     = null;

    String                       extNumber = null;
    List<MmsMessageTemplateBody> bobies    = new ArrayList<>();

    @Before
    public void init() throws IOException {
        resolver = new MmsMaiyuanPassageResolver();
        parameter = new MmsPassageParameter();

        JSONObject pam = new JSONObject();
        pam.put("userId", "498");
        pam.put("account", "dianjixyk");
        pam.put("password", "djxyk58998787");

        parameter.setUrl("http://182.92.224.167:8888/sendmms.aspx");
        parameter.setParams(pam.toJSONString());

        mobile = "15868193450";
        // content = "【华时科技】您的短息验证码为1234452";
        
        title  = "邱淑贞大女儿靓照曝光，颜值不输妈妈，青春靓丽！";
        setBody();
    }
    
    private void setBody() throws IOException {
        MmsMessageTemplateBody body = new MmsMessageTemplateBody();
        body.setMediaName("txt");
        body.setMediaType(MediaType.TEXT.getCode());
        body.setData("其中更是有一张邱淑贞叼着扑克牌的照片，之后成为了邱淑贞标志性的照片。照片中她叼着一张扑克牌，但是扑克牌却看起来和她的脸差不多大小，可见邱淑贞的脸是多么的小，而虽然邱淑贞的身高并不算是很高，只有160，但是身材比例却是让很多人都感到非常的羡慕。".getBytes("UTF-8"));
        
        bobies.add(body);
        
        MmsMessageTemplateBody body2 = new MmsMessageTemplateBody();
        body2.setMediaName("jpg");
        body2.setMediaType(MediaType.IMAGE.getCode());
        body2.setData(getByte());
        
        bobies.add(body2);
    }
    
    private byte[] getByte() throws IOException {
        
        return Files.readAllBytes(Paths.get("C:\\Users\\tenx\\Desktop\\1.jpg"));
//              File f = new File("C:\\Users\\tenx\\Desktop\\1.jpg");       
//              BufferedImage bi;  
//              try {
//                bi = ImageIO.read(f);  
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();  
//                ImageIO.write(bi, "jpg", baos);  
//                byte[] bytes = baos.toByteArray();
//                  
//                return bytes;
//              } catch (IOException e) {  
//                e.printStackTrace();  
//              }  
//              return null;  
        
    }

    @Test
    public void test() {
        
        List<ProviderSendResponse> list = resolver.send(parameter, mobile, extNumber, title, bobies);

        logger.info(JSON.toJSONString(list));

        Assert.assertTrue("回执数据失败", CollectionUtils.isNotEmpty(list));

    }
}
