package com.huashi.web.client;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.notice.vo.BaseResponse;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.user.service.IUserDeveloperService;
import com.huashi.constants.OpenApiCode;
import com.huashi.constants.OpenApiCode.CommonApiCode;

public class BaseClient {

    @Value("${hspaas.api.url}")
    protected String                rootApiUrl;

    protected final Logger          logger = LoggerFactory.getLogger(getClass());

    @Reference
    protected IUserDeveloperService userDeveloperService;

    /**
     * 根据用户ID获取开发者信息
     * 
     * @param userId
     * @return
     */
    protected UserDeveloper getByUserId(int userId) {
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

    /**
     * 解析返回值
     * 
     * @param result
     * @return
     */
    protected BaseResponse parse(String result) {
        if (StringUtils.isEmpty(result)) {
            return new BaseResponse(false, CommonApiCode.COMMON_SERVER_EXCEPTION.getMessage());
        }

        Map<String, Object> m = JSON.parseObject(result, new TypeReference<Map<String, Object>>() {
        });

        Object o = m.get("code");
        if (o == null || StringUtils.isEmpty(o.toString())) {
            return new BaseResponse(false, CommonApiCode.COMMON_SERVER_EXCEPTION.getMessage());
        }

        boolean isOk = OpenApiCode.SUCCESS.equals(o.toString());

        return new BaseResponse(isOk, isOk ? "处理成功" : m.getOrDefault("message", "").toString());
    }
}
