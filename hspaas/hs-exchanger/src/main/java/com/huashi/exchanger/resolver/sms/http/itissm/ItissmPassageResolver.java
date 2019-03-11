package com.huashi.exchanger.resolver.sms.http.itissm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.common.util.DateUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.resolver.HttpClientManager;
import com.huashi.exchanger.resolver.sms.http.AbstractPassageResolver;
import com.huashi.exchanger.template.handler.RequestTemplateHandler;
import com.huashi.exchanger.template.vo.TParameter;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

/**
 * TODO 云信通道解析器
 * 
 * @url http://112.124.24.5/api/MsgSend.asmx
 * @url http://yes.itissm.com
 * @author zhengying
 * @version V1.0
 * @date 2018年4月14日 下午5:59:00
 */
@Component
public class ItissmPassageResolver extends AbstractPassageResolver {

    @Override
    public List<ProviderSendResponse> send(SmsPassageParameter parameter, String mobile, String content,
                                           String extNumber) {

        try {
            TParameter tparameter = RequestTemplateHandler.parse(parameter.getParams());

            // 转换参数，并调用网关接口，接收返回结果
            String result = HttpClientManager.post(parameter.getUrl(),
                                                   sendRequest(tparameter, mobile, content, extNumber));

            // 解析返回结果并返回
            return sendResponse(result, parameter.getSuccessCode());
        } catch (Exception e) {
            logger.error("云信发送解析失败", e);
            throw new RuntimeException("解析失败");
        }
    }

    /**
     * TODO 发送短信组装请求信息
     * 
     * @param tparameter
     * @param mobile
     * @param content 短信内容
     * @param extNumber 扩展号
     * @return
     */
    private static Map<String, Object> sendRequest(TParameter tparameter, String mobile, String content,
                                                   String extNumber) {
        Map<String, Object> params = new HashMap<>();
        params.put("userCode", tparameter.getString("account"));
        params.put("userPass", tparameter.getString("password"));
        params.put("DesNo", mobile);
        params.put("Msg", content);

        // 通道号，需要在云信平台索要，阿拉伯数字
        params.put("Channel", tparameter.getString("channel_no"));
        params.put("ExeNo", extNumber == null ? "" : extNumber);

        return params;
    }

    private static final String RESPONSE_FILTER_PRIFIX = "<string xmlns=\"http://tempuri.org/\">";
    private static final String RESPONSE_FILTER_SUFFIX = "</string>";

    /**
     * TODO 获取实际意义的回执信息（取出XML一些无用信息）
     * 
     * @param result
     * @return
     */
    private static String getInFactResponse(String result) {
        return result.substring(result.indexOf(RESPONSE_FILTER_PRIFIX) + RESPONSE_FILTER_PRIFIX.length(),
                                result.indexOf(RESPONSE_FILTER_SUFFIX));
    }

    /**
     * TODO 解析发送返回值
     * 
     * @param result
     * @param successCode
     * @return
     */
    private List<ProviderSendResponse> sendResponse(String result, String successCode) {
        if (StringUtils.isEmpty(result)) {
            return null;
        }

        logger.info("回执信息--------------" + result);
        List<ProviderSendResponse> list = new ArrayList<>();
        try {

            result = getInFactResponse(result);

            long msgId = Long.parseLong(result);

            ProviderSendResponse response = new ProviderSendResponse();

            response.setStatusCode(msgId + "");

            // 如果回执数据大于0，则表明是任务ID，小于0则不设置SID，默认与我方任务SID一致
            if (msgId > 0) {
                response.setSid(msgId + "");
            }
            response.setSuccess(msgId > 0);
            response.setRemark(result);

            list.add(response);
            return list;

        } catch (Exception e) {
            logger.warn("云信通道回执数据异常", e);
            return null;
        }
    }

    /**
     * 解析回执数据报告（多个数据以|分割，数据格式: 任务ID,手机号码,状态）
     * 
     * @param report
     * @param successCode
     * @return
     * @see com.huashi.exchanger.resolver.AbstractMmsPassageResolver.custom.AbstractPassageResolver#mtDeliver(java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<SmsMtMessageDeliver> mtDeliver(String report, String successCode) {
        try {
            logger.info("下行状态报告简码：{} =========={}", code(), report);

            if (StringUtils.isEmpty(report)) {
                return null;
            }

            // {\"account\":\"HSKJYX\",\"GetReport\":\"2115238738241199173,18368031231,DELIVRD|2115238715962088136,18368031231,DELIVRD|\"}
            JSONObject body = JSON.parseObject(report);

            // 只返回-1或者-3则通道方异常
            if (!report.contains(",")) {
                throw new RuntimeException("云信应用程序异常 [" + report + "]");
            }

            report = body.getString("GetReport");
            if (StringUtils.isEmpty(report)) {
                return null;
            }

            /**
             * 数据格式：批次,号码,时间,状态| 批次A,号码A,时间A,状态A|批次B,号码B,时间B,状态B |批次C,号码C,时间C,状态C| …… 示例：
             * 2114355899380234221,13900000000,2014/06/10 15:34:11,DELIVRD| 2114355899380234221,13900000001,2014/06/10
             * 15:34:11,DELIVRD| 2114355899380234221,13900000002,2014/06/10 15:34:11,UNDELIVRD |
             */

            String[] rowArray = report.split("\\|");
            if (rowArray.length == 0) {
                return null;
            }

            List<SmsMtMessageDeliver> list = new ArrayList<>();
            SmsMtMessageDeliver response;
            for (String rowData : rowArray) {
                if (StringUtils.isEmpty(rowData)) {
                    continue;
                }

                // 节点数据，format: 任务ID,手机号码,状态
                String[] nodes = rowData.split(",");

                response = new SmsMtMessageDeliver();
                response.setMsgId(nodes[0]);
                response.setMobile(nodes[1]);
                response.setCmcp(CMCP.local(nodes[1]).getCode());
                response.setStatusCode(nodes[2]);
                response.setStatus((StringUtils.isNotEmpty(response.getStatusCode())
                                    && response.getStatusCode().equalsIgnoreCase(successCode) ? DeliverStatus.SUCCESS.getValue() : DeliverStatus.FAILED.getValue()));
                response.setDeliverTime(DateUtil.getNow());
                response.setCreateTime(new Date());
                response.setRemark(rowData);

                list.add(response);
            }

            // 解析返回结果并返回
            return list;
        } catch (Exception e) {
            logger.error("云信状态报告解析失败", e);
            throw new RuntimeException("解析失败");
        }
    }

    /**
     * 解析上行状态报告
     * 
     * @param report
     * @param passageId
     * @return
     * @see com.huashi.exchanger.resolver.AbstractMmsPassageResolver.custom.AbstractPassageResolver#moReceive(java.lang.String,
     * java.lang.Integer)
     */
    @Override
    public List<SmsMoMessageReceive> moReceive(String report, Integer passageId) {
        try {

            logger.info("上行报告简码：{} =========={}", code(), report);
            if (StringUtils.isEmpty(report)) {
                return null;
            }

            // {"GetMo":"15868193450|,|￥ﾥﾽ￧ﾚﾄ￯ﾼﾌ￦ﾈﾑ￧ﾟﾥ￩ﾁﾓ￤ﾺﾆ|,|2018/4/16 11:14:13|,|106914010526|;|15868193450|,|￥ﾗﾯ￥ﾗﾯ￯ﾼﾌ￥ﾏﾯ￤ﾻﾥ|,|2018/4/16 11:14:13|,|106914010526|;|","account":"HSKJYX"}
            JSONObject body = JSON.parseObject(report);
            report = body.getString("GetMo");

            if (StringUtils.isEmpty(report)) {
                return null;
            }

            /**
             * 数据格式： A号码|,|A回复内容|,|A回复时间|;| B号码|,|B回复内容|,|B回复时间…… 1) 每个号码及其回复、回复时间称为一组回复，号码、回复内容和回复时间之间用“|,|”分隔； 2)
             * 每组回复之间，用“|;|”分隔； 3) 没有回复时，返回空字符串。
             */
            List<SmsMoMessageReceive> list = new ArrayList<>();
            String[] rowArray = report.split("\\|;\\|");
            SmsMoMessageReceive response = null;
            for (String rowData : rowArray) {
                String[] nodes = rowData.split("\\|,\\|");

                response = new SmsMoMessageReceive();
                response.setPassageId(passageId);
                response.setMobile(nodes[0]);
                response.setContent(nodes[1]);
                response.setReceiveTime(dateNumberFormat(nodes[2], "yyyy/MM/dd HH:mm:ss"));
                response.setCreateTime(new Date());
                response.setCreateUnixtime(response.getCreateTime().getTime());
                list.add(response);

            }
            return list;
        } catch (Exception e) {
            logger.error("云信上行解析失败", e);
            throw new RuntimeException("解析失败");
        }
    }

    @Override
    public Double balance(TParameter tparameter, String url, Integer passageId) {
        return 0d;
    }

    @Override
    public String code() {
        return "itissm";
    }

    /**
     * 失败状态码|状态码说明 -1 应用程序异常 -3 用户名密码错误或者用户无效 -5 签名不正确（格式为:XXXX【签名内容】）注意，短信内容最后一个字符必须是】 -6:keyWords 含有关键字keyWords
     * （keyWords为敏感内容，如： -6:促销） -7 余额不足 -8 没有可用通道，或不在时间范围内 -9 发送号码一次不能超过1000个 -10 号码数量大于允许上限（不设置上限时，不可超过1000） -11
     * 号码数量小于允许下限 -12 模板不匹配 -13 Invalid Ip ip绑定用户，未绑定该ip -14 用户黑名单 -15 系统黑名单 -16 号码格式错误 -17
     * 无效号码（格式正常，可不是正确的电话号码,如12345456765） -18 没有设置用户的固定下发扩展号，不能自定义扩展 -19 强制模板通道，不能使用个性化接口 -20 包含非法字符 -21
     * 没有找到对应的SubmitID设置 -22 解密失败 -23 查询余额过频繁（至少间隔10秒）
     */

}
