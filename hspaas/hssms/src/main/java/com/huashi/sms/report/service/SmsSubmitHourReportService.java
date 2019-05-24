package com.huashi.sms.report.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.common.settings.domain.Province;
import com.huashi.common.settings.service.IProvinceService;
import com.huashi.common.user.model.UserModel;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.util.DateUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.sms.passage.context.PassageContext;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.huashi.sms.record.service.ISmsMtSubmitService;
import com.huashi.sms.report.dao.SmsSubmitHourReportMapper;
import com.huashi.sms.report.domain.SmsSubmitHourReport;
import com.huashi.sms.report.model.ChinessCmcpSubmitReportVo;
import com.huashi.sms.report.model.ChinessCmcpSubmitReportVo.CmcpAmount;

@Service
public class SmsSubmitHourReportService implements ISmsSubmitHourReportService {

    @Autowired
    private SmsSubmitHourReportMapper smsSubmitHourReportMapper;
    @Autowired
    private ISmsMtSubmitService       smsMtSubmitService;

    @Reference
    private IUserService              userService;
    @Reference
    private IProvinceService          provinceService;
    @Autowired
    private ISmsPassageService        smsPassageService;

    private final Logger              logger = LoggerFactory.getLogger(getClass());

    @Override
    public int beBornSubmitHourReport() {
        return beBornSubmitHourReport(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int beBornSubmitHourReport(int hour) {
        int count = 0;
        logger.info("For finishing it will be execute " + hour + " times ");
        long startTimeCounter = System.currentTimeMillis();
        for (int i = 0; i <= hour; i++) {
            // 当前时间在减去指定小时的时间数（如72小时）在减去1，保证落地数据00:00
            Long startTime = DateUtil.getXHourWithMzSzMillis(-hour - 1 + i);
            // 当前时间减1，通过人为方式错开时间点，如5点执行4点前的数据59:59
            Long endTime = DateUtil.getAfterXHourWithMzSzMillis(-hour - 1 + i);

            int currentCount = beBornSubmitHourReportByHourTimeRange(startTime, endTime);
            logger.info("No." + i + "'s result is " + currentCount + " between " + startTime + " and " + endTime);
            count += currentCount;
        }

        logger.info("Summarize is " + count + "in " + hour + " hours, it cost "
                    + (System.currentTimeMillis() - startTimeCounter) + " ms");

        return count;
    }

    /**
     * TODO 按照时间范围执行小时报表生成
     * 
     * @param startTime
     * @param endTime
     * @return
     */
    private int beBornSubmitHourReportByHourTimeRange(long startTime, long endTime) {
        List<Map<String, Object>> list = smsMtSubmitService.getSubmitStatReport(startTime, endTime);
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("Ignore by querying data empty between " + startTime + " and " + endTime);
            return 0;
        }

        List<SmsSubmitHourReport> batchList = new ArrayList<>();
        SmsSubmitHourReport report = null;
        for (Map<String, Object> map : list) {
            if (MapUtils.isEmpty(map)) {
                logger.error("开始时间：{} 截止时间：{}， 报表离线统计包含数据为空，整体失败，[严重,需补修]", startTime, endTime);
                break;
            }

            try {
                report = new SmsSubmitHourReport();
                copyProperties(map, report, startTime);
                batchList.add(report);
            } catch (Exception e) {
                logger.error("开始时间：{} 截止时间：{}， 报表离线统计失败，整体失败，[严重,需补修]", startTime, endTime, e);
                break;
            }
        }

        if (CollectionUtils.isEmpty(batchList)) {
            logger.warn("Ignore by querying data empty between " + startTime + " and " + endTime);
            return 0;
        }

        try {
            smsSubmitHourReportMapper.deleteBtHourTime(startTime, endTime);
            if (smsSubmitHourReportMapper.batchInsert(batchList) > 0) {
                return batchList.size();
            }

            throw new RuntimeException("Batch insert failed");
        } catch (Exception e) {
            logger.error("Invoked method[beBornSubmitHourReport] failed by args [" + startTime + " - " + endTime + "]",
                         e);
            throw new RuntimeException(e);
        }
    }

    /**
     * TODO 复制数据到数据模型
     * 
     * @param source
     * @param target
     * @param endMillisTime
     */
    private void copyProperties(Map<String, Object> source, SmsSubmitHourReport target, Long startMillisTime) {
        target.setUserId(Integer.parseInt(source.get("user_id").toString()));
        target.setProvinceCode(source.get("province_code") == null ? 0 : Integer.parseInt(source.get("province_code").toString()));
        target.setPassageId(Integer.parseInt(source.get("passage_id").toString()));
        target.setSubmitCount(Integer.parseInt(source.get("submit_count").toString()));
        target.setBillCount(Integer.parseInt(source.get("bill_count").toString()));
        target.setSuccessCount(Integer.parseInt(source.get("success_count").toString()));
        target.setSubmitFailedCount(Integer.parseInt(source.get("submit_failed_count").toString()));
        target.setUnknownCount(Integer.parseInt(source.get("unknown_count").toString()));
        target.setOtherCount(target.getBillCount() - target.getSuccessCount() - target.getSubmitFailedCount()
                             - target.getUnknownCount());
        target.setHourTime(startMillisTime);

        target.setStatus(0);// 状态目前没用到，暂时0
        target.setBornHours(72); // 落地时限，默认72小时i
    }

    @Override
    public Map<UserModel, List<SmsSubmitHourReport>> findUserPassageSubmitReport(Integer userId, String startDate,
                                                                                 String endDate) {

        try {
            List<SmsSubmitHourReport> list = smsSubmitHourReportMapper.selectUserPassageSubmitReport(userId,
                                                                                                     parseDateStr2StartLongTime(startDate),
                                                                                                     parseDateStr2EndLongTime(endDate));

            if (CollectionUtils.isEmpty(list)) {
                return null;
            }

            Map<Integer, String> passageNameMap = new HashMap<Integer, String>();
            Map<Integer, UserModel> userModelMap = new HashMap<Integer, UserModel>();

            Map<Integer, List<SmsSubmitHourReport>> map = new HashMap<Integer, List<SmsSubmitHourReport>>();
            for (SmsSubmitHourReport report : list) {
                if (report == null || report.getUserId() == null || report.getPassageId() == null) {
                    logger.error("用户通道提交报表数据包含异常数据, {}", JSON.toJSONString(report));
                    return null;
                }

                // 根据通道ID转义通道名称
                report.setPassageName(getPassageName(passageNameMap, report.getPassageId()));

                if (!map.containsKey(report.getUserId())) {
                    List<SmsSubmitHourReport> chlidrenList = new ArrayList<SmsSubmitHourReport>();
                    chlidrenList.add(report);
                    map.put(report.getUserId(), chlidrenList);
                    continue;
                }

                map.get(report.getUserId()).add(report);
            }

            Map<UserModel, List<SmsSubmitHourReport>> container = new HashMap<UserModel, List<SmsSubmitHourReport>>();
            UserModel userModel = null;
            for (Integer userIdKey : map.keySet()) {
                userModel = getUserModel(userModelMap, userIdKey);
                if (userModel == null) {
                    logger.error("用户通道提交报表数据包含用户数据为空, {}", JSON.toJSONString(map.get(userIdKey)));
                    return null;
                }

                container.put(userModel, map.get(userIdKey));
            }

            return container;

        } catch (Exception e) {
            logger.error("获取用户通道短信提交报表失败", e);
            return null;
        }
    }

    /**
     * TODO 获取用户模型数据
     * 
     * @param userModelMap
     * @param usreId
     * @return
     */
    private UserModel getUserModel(Map<Integer, UserModel> userModelMap, Integer usreId) {
        if (MapUtils.isNotEmpty(userModelMap) && userModelMap.containsKey(usreId)) {
            return userModelMap.get(usreId);
        }

        UserModel userModel = userService.getByUserId(usreId);
        if (userModel == null) {
            return new UserModel(usreId, usreId + "(无)", usreId + "(无)");
        }

        userModelMap.put(usreId, userModel);
        return userModel;
    }

    /**
     * TODO 获取通道名称（如果通道ID为空，则设置为N/A，如果通道ID未找到相关通道则直接返回 通道ID转字符输出）
     * 
     * @param passageNameMap
     * @param passageId
     * @return
     */
    private String getPassageName(Map<Integer, String> passageNameMap, Integer passageId) {
        if (passageId == null || passageId == PassageContext.EXCEPTION_PASSAGE_ID) {
            return "N/A";
        }

        if (MapUtils.isNotEmpty(passageNameMap) && passageNameMap.containsKey(passageId)) {
            return passageNameMap.get(passageId);
        }

        SmsPassage passage = smsPassageService.findById(passageId);
        if (passage == null) {
            passageNameMap.put(passageId, passageId.toString());
            return passageId.toString();
        }

        passageNameMap.put(passageId, passage.getName());
        return passage.getName();
    }

    /**
     * TODO 根据省份代码获取省份名称，如果省份代码为空，则返回G/A
     * 
     * @param provinceNameMap
     * @param provinceCode
     * @return
     */
    private String getProviceName(Map<Integer, String> provinceNameMap, Integer provinceCode) {
        if (provinceCode == null) {
            return "G/A";
        }

        if (MapUtils.isNotEmpty(provinceNameMap) && provinceNameMap.containsKey(provinceCode)) {
            return provinceNameMap.get(provinceCode);
        }

        Province province = provinceService.get(provinceCode);

        if (province == null) {
            provinceNameMap.put(provinceCode, provinceCode.toString());
            return provinceCode.toString();
        }

        provinceNameMap.put(provinceCode, province.getName());
        return province.getName();
    }

    @Override
    public List<SmsSubmitHourReport> findUserSubmitReport(Integer userId, String startDate, String endDate) {
        try {
            List<SmsSubmitHourReport> list = smsSubmitHourReportMapper.selectUserSubmitReport(userId,
                                                                                              parseDateStr2StartLongTime(startDate),
                                                                                              parseDateStr2EndLongTime(endDate));

            if (CollectionUtils.isEmpty(list)) {
                return null;
            }

            Map<Integer, UserModel> userModelMap = new HashMap<Integer, UserModel>();

            UserModel userModel = null;
            for (SmsSubmitHourReport report : list) {
                if (report == null || report.getUserId() == null) {
                    logger.error("用户提交报表数据包含异常数据, {}", JSON.toJSONString(report));
                    return null;
                }

                userModel = getUserModel(userModelMap, report.getUserId());
                if (userModel == null) {
                    logger.error("用户提交报表数据包含用户数据为空, {}", JSON.toJSONString(report));
                    return null;
                }

                report.setUserModel(userModel);
            }

            return list;

        } catch (Exception e) {
            logger.error("获取用户短信提交报表失败", e);
            return null;
        }
    }

    @Override
    public List<SmsSubmitHourReport> findPassageSubmitReport(Integer passageId, String startDate, String endDate) {
        try {
            List<SmsSubmitHourReport> list = smsSubmitHourReportMapper.selectPassageSubmitReport(passageId,
                                                                                                 parseDateStr2StartLongTime(startDate),
                                                                                                 parseDateStr2EndLongTime(endDate));

            if (CollectionUtils.isEmpty(list)) {
                return null;
            }

            Map<Integer, String> passageNameMap = new HashMap<Integer, String>();

            for (SmsSubmitHourReport report : list) {
                if (report == null || report.getPassageId() == null) {
                    logger.error("通道提交报表数据包含异常数据, {}", JSON.toJSONString(report));
                    return null;
                }

                // 根据通道ID转义通道名称
                report.setPassageName(getPassageName(passageNameMap, report.getPassageId()));
            }

            return list;

        } catch (Exception e) {
            logger.error("获取通道短信提交报表失败", e);
            return null;
        }
    }

    @Override
    public List<SmsSubmitHourReport> findProvinceSubmitReport(Integer passageId, String startDate, String endDate) {
        try {
            List<SmsSubmitHourReport> list = smsSubmitHourReportMapper.selectProvinceSubmitReport(passageId,
                                                                                                  parseDateStr2StartLongTime(startDate),
                                                                                                  parseDateStr2EndLongTime(endDate));

            if (CollectionUtils.isEmpty(list)) {
                return null;
            }

            Map<Integer, String> provinceNameMap = new HashMap<Integer, String>();

            for (SmsSubmitHourReport report : list) {
                if (report == null || report.getProvinceCode() == null) {
                    logger.error("省份提交报表数据包含异常数据, {}", JSON.toJSONString(report));
                    return null;
                }

                // 根据省份代码转义省份名称
                report.setPronvinceName(getProviceName(provinceNameMap, report.getProvinceCode()));
            }

            return list;

        } catch (Exception e) {
            logger.error("获取省份短信提交报表失败", e);
            return null;
        }
    }

    /**
     * TODO 转换日期时间至开始毫秒时间（日期尾部追加当天第一秒00:00:00）
     * 
     * @param dateStr
     * @return
     */
    private static long parseDateStr2StartLongTime(String dateStr) {
        if (StringUtils.isNotEmpty(dateStr)) {
            return DateUtil.getSecondDate(dateStr + " 00:00:00").getTime();
        }

        return 0L;
    }

    /**
     * TODO 转换日期时间至截止毫秒时间（日期尾部追加当天最后一秒23:59:59）
     * 
     * @param dateStr
     * @return
     */
    private static long parseDateStr2EndLongTime(String dateStr) {
        if (StringUtils.isNotEmpty(dateStr)) {
            return DateUtil.getSecondDate(dateStr + " 23:59:59").getTime();
        }

        return System.currentTimeMillis();
    }

    /**
     * TODO 获取省份代码
     * 
     * @param provinceCode
     * @return
     */
    private static Integer getProvinceCode(Object provinceCode) {
        try {
            if (provinceCode == null) {
                return null;
            }

            return Integer.parseInt(provinceCode.toString());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * TODO 设置运营商（CMCP）的数据
     * 
     * @param cmcp 运营商
     * @param cmcpAmount 运营商数量
     * @param reportVo
     */
    private static void setCmcpData(Object cmcp, CmcpAmount cmcpAmount, ChinessCmcpSubmitReportVo reportVo) {
        try {
            if (cmcp == null || StringUtils.isBlank(cmcp.toString())
                || Province.PROVINCE_CODE_ALLOVER_COUNTRY.equals(cmcp.toString())) {
                return;
            }

            Integer _cmcp = Integer.parseInt(cmcp.toString());
            if (CMCP.CHINA_MOBILE.getCode() == _cmcp) {
                reportVo.getCmlist().add(cmcpAmount);
            } else if (CMCP.CHINA_TELECOM.getCode() == _cmcp) {
                reportVo.getCtlist().add(cmcpAmount);
            } else if (CMCP.CHINA_UNICOM.getCode() == _cmcp) {
                reportVo.getCulist().add(cmcpAmount);
            }

        } catch (Exception e) {
            return;
        }
    }

    public static void main(String[] args) {
        // System.out.println(parseDateStr2StartLongTime("2017-09-02"));
        // System.out.println(parseDateStr2EndLongTime("2017-09-02"));

        // System.out.println(new Date(1541836800000L));
        System.out.println(System.currentTimeMillis());
        int hour = 72;

        for (int i = 0; i <= hour; i++) {
            // 当前时间在减去指定小时的时间数（如72小时）在减去1，保证落地数据
            Long startTime = DateUtil.getXHourWithMzSzMillis(-hour - 1 + i);
            // 当前时间减1，通过人为方式错开时间点，如5点执行4点前的数据
            Long endTime = DateUtil.getAfterXHourWithMzSzMillis(-hour - 1 + i);
            System.out.println("start:" + (-hour - 1 + i) + "-" + new Date(startTime) + "            end:"
                               + (-hour - 1 + i + 1) + "-" + new Date(endTime));
        }
    }

    @Override
    public ChinessCmcpSubmitReportVo getCmcpSubmitReport(String startDate, String endDate) {
        try {
            List<Map<String, Object>> list = smsMtSubmitService.getSubmitCmcpReport(parseDateStr2StartLongTime(startDate),
                                                                                    parseDateStr2EndLongTime(endDate));

            if (CollectionUtils.isEmpty(list)) {
                return null;
            }

            Map<Integer, String> provinceMap = provinceService.findNamesInMap();
            if (MapUtils.isEmpty(provinceMap)) {
                logger.error("获取省份信息失败");
                return null;
            }

            ChinessCmcpSubmitReportVo reportVo = new ChinessCmcpSubmitReportVo();
            CmcpAmount cmcpAmount = null;

            for (Map<String, Object> report : list) {
                if (report == null || report.get("province_code") == null
                    || StringUtils.isBlank(report.get("province_code").toString())) {
                    continue;
                }

                cmcpAmount = new CmcpAmount();
                cmcpAmount.setName(provinceMap.get(getProvinceCode(report.get("province_code"))));
                cmcpAmount.setValue(report.get("count") == null ? 0 : Integer.parseInt(report.get("count").toString()));

                setCmcpData(report.get("cmcp"), cmcpAmount, reportVo);
            }

            return reportVo;

        } catch (Exception e) {
            logger.error("获取省份运营商统计失败", e);
            return null;
        }
    }

    @Override
    public List<SmsSubmitHourReport> findUserSubmitReportInDailyFilter(Integer userId, String startDate, String endDate) {
        try {
            List<SmsSubmitHourReport> list = smsSubmitHourReportMapper.selectUserSubmitReportGroupByDaily(userId,
                                                                                              parseDateStr2StartLongTime(startDate),
                                                                                              parseDateStr2EndLongTime(endDate));

//            if (CollectionUtils.isEmpty(list)) {
//                return null;
//            }
//
//            Map<Integer, UserModel> userModelMap = new HashMap<Integer, UserModel>();
//
//            UserModel userModel = null;
//            for (SmsSubmitHourReport report : list) {
//                if (report == null || report.getUserId() == null) {
//                    logger.error("用户提交报表数据包含异常数据, {}", JSON.toJSONString(report));
//                    return null;
//                }
//
//                userModel = getUserModel(userModelMap, report.getUserId());
//                if (userModel == null) {
//                    logger.error("用户提交报表数据包含用户数据为空, {}", JSON.toJSONString(report));
//                    return null;
//                }
//
//                report.setUserModel(userModel);
//            }

            return list;

        } catch (Exception e) {
            logger.error("获取用户短信提交报表失败", e);
            return null;
        }
    }

}
