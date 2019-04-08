package com.huashi.web.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.huashi.common.notice.vo.BaseResponse;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.util.SecurityUtil;
import com.huashi.util.HttpClientUtil;

@Component
public class SmsClient extends BaseClient {

    /**
     * 短信路径路由
     */
    private static final String SMS_ROUTER      = "/sms";

    /**
     * 发送短信方法
     */
    private static final String SEND_SMS_ACTION = "/send";

    private String getSmsUrl(String action) {
        if (StringUtils.isEmpty(rootApiUrl)) {
            throw new IllegalArgumentException("Args [rootApiUrl] is empty");
        }

        return rootApiUrl + SMS_ROUTER + action;
    }

    /**
     * @param url ：必填--发送连接地址URL--比如>http://api.hspaas.cn:8080/sms/send
     * @param appkey ：必填--用户帐号
     * @param password ：必填--数字签名：(接口密码、时间戳32位MD5加密生成)
     * @param mobile ：必填--发送的手机号码，多个可以用逗号隔比如>13512345678,13612345678
     * @param content ：必填--实际发送内容
     * @return 返回发送之后收到的信息
     */
    public BaseResponse sendSms(int userId, String mobile, String content) {
        if (StringUtils.isBlank(mobile)) {
            return new BaseResponse(false, "手机号码不能为空");
        }

        if (StringUtils.isBlank(content)) {
            return new BaseResponse(false, "短信内容不能为空");
        }

        UserDeveloper developer = getByUserId(userId);
        if (developer == null) {
            return new BaseResponse(false, "授权失败，请稍后重试");
        }

        long timestamp = System.currentTimeMillis();

        Map<String, Object> params = new HashMap<>();
        params.put("appkey", developer.getAppKey());
        params.put("appsecret", SecurityUtil.md5Hex(developer.getAppSecret() + mobile + timestamp));
        params.put("mobile", mobile);
        params.put("content", content);
        params.put("timestamp", timestamp + "");

        String result = HttpClientUtil.post(getSmsUrl(SEND_SMS_ACTION), params);

        return parse(result);
    }
}
