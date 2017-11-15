package com.huashi.common.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerUtil {

	/**
	 * 
	   * TODO 转换
	   * @param content
	   * @param values
	   * @return
	 */
	public static String parse(String content, Map<String, String> values) {
		try {
			// 创建一个合适的Configration对象
			Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);

			StringTemplateLoader stringLoader = new StringTemplateLoader();
			stringLoader.putTemplate("myTemplate", content);

			configuration.setTemplateLoader(stringLoader);
			configuration.setDefaultEncoding("UTF-8"); // 这个一定要设置，不然在生成的页面中 会乱码
			configuration.setLogTemplateExceptions(false);

			Template template = configuration.getTemplate("myTemplate", "UTF-8");

			StringWriter writer = new StringWriter();
			template.process(values, writer);
			System.out.println(writer.toString());

			return writer.toString();
		} catch (IOException | TemplateException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void main(String[] args) {
		Map<String,String> values = new HashMap<>();
		values.put("url", "www.hspaas.com");
		String content = "<div style='width:700px;margin:0 auto 13px;font-size:12px;font-family:'Microsoft YaHei''><div><img src='https://www.bestsign.cn/images/newIndexImg/loginSlogan_03.png' alt=''></div><div style='border:1px solid #ddd;width:690px;background-color:#fcfcfc;box-sizing:border-box'><div style='padding-left:40px;padding-right:40px'><div style='text-align:center;color:#3d3d3d;border-bottom:1px solid #ddd;font-size:14px;line-height:55px;margin-top:10px'><strong>邮箱认证</strong></div><div style='padding-left:2em;line-height:22px'><div style='margin-left:-2em;margin-top:20px'>尊敬的用户，您好！</div><div style='margin-bottom:20px'>这是<span style='color:#319acc;font-size:14px'>【华时融合】</span>平台为您发出的邮箱有效性校验邮件。</div><div>非常感谢您选择华时融合平台成为您的合作伙伴，帮助您加快工作流程，提高工作效率。</div><div>请按照下面的步骤完成您的邮箱校验过程，完成校验后，您可进一步设置账号信息，完成注册。</div><a href='${url}' style='text-decoration:none'><div style='text-align:center;color:#fff;background-color:#319acc;width:198px;height:44px;line-height:44px;font-size:16px;margin:24px auto;border-radius:2px'>点击认证</div></a><div>如果点击无效，请复制下方网页地址到浏览器地址栏中打开：</div><div style='text-indent:0;color:#319acc;margin-bottom:20px;width:100%;word-wrap:break-word;word-break:break-all'>${url}</div><div>如果您在使用过程中需要任何帮助，欢迎联系华时客服。</div><div>客服邮箱：<span style='text-decoration:underline'><a href='mailto:support@bestsign.cn' style='text-decoration:none;color:#319acc'>support@hspaas.com</a></span></div><div>24小时热线：<span style='color:#319acc'>400-0716-800</span></div><div style='text-align:right'><img src='https://www.bestsign.cn/imgers/tempsignssq.png' alt='' style='margin-bottom:5px'><br><span>BestSign，Any time，Any where，Any Device.</span></div><div style='color:#b3b3b3;text-align:center;margin-top:20px'>此为华时融合系统邮件，请勿回复。请保管好您的邮箱，避免被他人盗用。</div></div></div><div style='border-top:1px solid #f0f0f0;text-align:center;line-height:43px'><a href='${(weburl)!}' style='text-decoration:none'><img src='https://www.bestsign.cn/imgers/usessqrightaway.png' alt='' style='margin-bottom:-5px'> <span style='color:#319acc;font-size:14px;border-bottom:1px solid #319acc;padding-bottom:2px'>立即使用华时融合</span></a></div><div style='background-color:#f0f0f0;padding:30px 75px 10px;text-align:center'><p style='color:#323232'>华时融合，短信服务、流量服务、语音服务集一体的融合平台</p><div style='border-bottom:1px solid #d9d9d9;padding-top:20px;padding-bottom:20px;margin-bottom:15px'><img src='https://www.bestsign.cn/imgers/templogogroup.png' alt=''></div><p style='color:#999'>杭州华时科技有限公司</p><p style='color:#999;margin-top:-5px'>Copyright &copy 2015-2016 Huashi 版权所有</p></div></div></div> ";
		System.out.println(parse(content, values));
	}
}
