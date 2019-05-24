package com.huashi.web.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.apache.dubbo.config.annotation.Reference;
import com.huashi.common.notice.vo.BaseResponse;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.user.service.IUserDeveloperService;
import com.huashi.common.util.SecurityUtil;

@Component
public class MmsClient extends BaseClient {

    /**
     * 彩信路径路由
     */
    private static final String   MMS_ROUTER               = "/mms";

    /**
     * 发送自定义内容彩信方法
     */
    private static final String   SEND_MMS_ACTION          = "/send";

    /**
     * 发送模板彩信方法
     */
    private static final String   SEND_MMS_BY_MODEL_ACTION = "/sendByModel";

    /**
     * 申请彩信模板方法
     */
    private static final String   APPLY_MODEL_ACTION       = "/applyModel";

    @Value("${hspaas.api.url}")
    private String                rootApiUrl;

    @Reference
    private IUserDeveloperService userDeveloperService;

    /**
     * 根据用户ID获取开发者信息
     * 
     * @param userId
     * @return
     */
    private UserDeveloper getByUserId(int userId) {
        try {
            if (userId == 0) {
                return null;
            }

            return userDeveloperService.getByUserId(userId);
        } catch (Exception e) {
            logger.error("getByUserId[" + userId + "] failed", e);
            return null;
        }

    }

    private String getMmsUrl(String action) {
        if (StringUtils.isEmpty(rootApiUrl)) {
            throw new IllegalArgumentException("Args [rootApiUrl] is empty");
        }

        return rootApiUrl + MMS_ROUTER + action;
    }

    /**
     * TODO 彩信发送
     * 
     * @param userId
     * @param mobile
     * @param title
     * @param body
     * @return
     */
    public BaseResponse sendMms(int userId, String mobile, String title, String body) {
        if (StringUtils.isBlank(mobile)) {
            return new BaseResponse(false, "手机号码不能为空");
        }

        if (StringUtils.isBlank(title)) {
            return new BaseResponse(false, "彩信标题不能为空");
        }

        if (StringUtils.isBlank(body)) {
            return new BaseResponse(false, "彩信内容不能为空");
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
        params.put("title", title);
        params.put("body", body);
        params.put("timestamp", timestamp + "");

        String result = HttpClientUtil.post(getMmsUrl(SEND_MMS_ACTION), params);

        return parse(result);
    }

    /**
     * TODO 根据模板ID发送彩信
     * 
     * @param userId
     * @param mobile
     * @param modelId
     * @return
     */
    public BaseResponse sendMmsByModel(int userId, String mobile, String modelId) {
        if (StringUtils.isBlank(mobile)) {
            return new BaseResponse(false, "手机号码不能为空");
        }

        if (StringUtils.isBlank(modelId)) {
            return new BaseResponse(false, "彩信模板ID不能为空");
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
        params.put("modelId", modelId);
        params.put("timestamp", timestamp + "");

        String result = HttpClientUtil.post(getMmsUrl(SEND_MMS_BY_MODEL_ACTION), params);

        return parse(result);
    }

    /**
     * TODO 模板申请
     * 
     * @param userId
     * @param name
     * @param title
     * @param body
     * @return
     */
    public BaseResponse applyModel(int userId, String name, String title, String body) {
        if (StringUtils.isBlank(name)) {
            return new BaseResponse(false, "彩信名称不能为空");
        }

        if (StringUtils.isBlank(title)) {
            return new BaseResponse(false, "彩信标题不能为空");
        }

        if (StringUtils.isBlank(body)) {
            return new BaseResponse(false, "彩信内容不能为空");
        }

        UserDeveloper developer = getByUserId(userId);
        if (developer == null) {
            return new BaseResponse(false, "授权失败，请稍后重试");
        }

        long timestamp = System.currentTimeMillis();

        Map<String, Object> params = new HashMap<>();
        params.put("appkey", developer.getAppKey());
        params.put("appsecret", SecurityUtil.md5Hex(developer.getAppSecret() + timestamp));
        params.put("name", name);
        params.put("title", title);
        params.put("body", body);
        params.put("timestamp", timestamp + "");

        String result = HttpClientUtil.post(getMmsUrl(APPLY_MODEL_ACTION), params);

        return parse(result);
    }

}
