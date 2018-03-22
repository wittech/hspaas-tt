package com.huashi.web.prervice.common;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.provider.domain.ProviderApiCode;
import com.huashi.common.provider.service.IProviderApiCodeService;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.fs.order.domain.FluxOrder;
import com.huashi.fs.order.service.IFluxOrderService;
import com.huashi.sms.record.service.ISmsMtSubmitService;
import com.huashi.vs.record.domain.Record;
import com.huashi.vs.record.service.IRecordService;

/**
 * TODO 搜索服务
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年9月16日 下午8:59:26
 */
@Service
public class SearchPrervice {

    @Reference
    private ISmsMtSubmitService     mtSmsService;
    @Reference
    private IFluxOrderService       flowOrderService;
    @Reference
    private IRecordService          vsRecordService;
    @Reference
    private IProviderApiCodeService providerApiCodeService;

    // 快捷查询-手机号码状态查询
    private static final int        QUICK_SEARCH_STATUS = 1;

    /**
     * TODO 首页快捷搜索
     * 
     * @param userId 用户编号
     * @param content 搜索内容
     * @param platformType 平台类型
     * @param quickType 搜索类型
     * @return
     */
    public Object quickSearch(int userId, String content, int platformType, int quickType) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }

        // 手机记录查询
        if (quickType == QUICK_SEARCH_STATUS) {
            switch (PlatformType.parse(platformType)) {
                case SEND_MESSAGE_SERVICE: {
                    return mtSmsService.findLastestRecord(userId, content);
                }
                case FLUX_SERVICE: {
                    List<FluxOrder> orders = flowOrderService.findUserIdAndMobileRecord(userId, content);
                    return CollectionUtils.isEmpty(orders) ? null : orders.iterator().next();
                }
                case VOICE_SERVICE: {
                    List<Record> records = vsRecordService.findMobileRecord(userId, content);
                    return CollectionUtils.isEmpty(records) ? null : records.iterator().next();
                }
                default: {
                    return null;
                }
            }
        }
        // 状态码查询
        List<ProviderApiCode> codes = providerApiCodeService.findTypeAndCodeRecord(platformType, content);
        return CollectionUtils.isEmpty(codes) ? null : codes.iterator().next();
    }

}
