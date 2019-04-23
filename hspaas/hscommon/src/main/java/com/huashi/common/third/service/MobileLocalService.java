package com.huashi.common.third.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.config.async.AsyncService;
import com.huashi.common.config.redis.CommonRedisConstant;
import com.huashi.common.settings.dao.ProvinceLocalMapper;
import com.huashi.common.settings.domain.Province;
import com.huashi.common.settings.domain.ProvinceLocal;
import com.huashi.common.third.model.MobileCatagory;
import com.huashi.constants.CommonContext.CMCP;

/**
 * @author tenx
 */
@Service
public class MobileLocalService implements IMobileLocalService {

    @Autowired
    private ProvinceLocalMapper provinceLocalMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private AsyncService        asyncService;

    private final Logger        logger = LoggerFactory.getLogger(getClass());

    @Override
    public MobileCatagory doCatagory(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return null;
        }

        String[] numbers = mobile.split(MobileCatagory.MOBILE_SPLIT_CHARCATOR);
        if (numbers.length == 0) {
            return null;
        }

        return doCatagory(Arrays.asList(numbers));
    }

    @Override
    public MobileCatagory doCatagory(List<String> numbers) {
        if (CollectionUtils.isEmpty(numbers)) {
            logger.error("手机号码为空,分流失败");
            return null;
        }

        MobileCatagory response = new MobileCatagory();
        try {

            // 移动号码
            Map<Integer, String> cmNumbers = new HashMap<>();
            // 电信号码
            Map<Integer, String> ctNumbers = new HashMap<>();
            // 联通号码
            Map<Integer, String> cuNumbers = new HashMap<>();

            // 已过滤号码
            StringBuilder filterNumbers = new StringBuilder();

            // 重复号码
            StringBuilder repeatNumbers = new StringBuilder();
            for (String number : numbers) {

                // 判断手机号码是否无效
                if (isInvalidMobile(number)) {
                    filterNumbers.append(number).append(MobileCatagory.MOBILE_SPLIT_CHARCATOR);
                    response.setFilterSize(response.getFilterSize() + 1);
                    continue;
                }

                ProvinceLocal pl = pickupMobileLocal(number);
                if (pl == null) {
                    // 如果截取 手机号码归属地位失败（手机号码前7位），则放置过滤号码中
                    filterNumbers.append(number).append(MobileCatagory.MOBILE_SPLIT_CHARCATOR);
                    response.setFilterSize(response.getFilterSize() + 1);
                    continue;
                }

                // 根据CMCP代码获取组装相关运营商信息
                switch (CMCP.getByCode(pl.getCmcp())) {
                    case CHINA_MOBILE: {
                        boolean isRepeat = reputCmcpMobiles(cmNumbers, pl.getProvinceCode(), number);
                        if (isRepeat) {
                            // 如果号码重复，则放置重复号码
                            repeatNumbers.append(number).append(MobileCatagory.MOBILE_SPLIT_CHARCATOR);
                            response.setRepeatSize(response.getRepeatSize() + 1);
                        }
                        break;
                    }
                    case CHINA_TELECOM: {
                        boolean isRepeat = reputCmcpMobiles(ctNumbers, pl.getProvinceCode(), number);
                        if (isRepeat) {
                            // 如果号码重复，则放置重复号码
                            repeatNumbers.append(number).append(MobileCatagory.MOBILE_SPLIT_CHARCATOR);
                            response.setRepeatSize(response.getRepeatSize() + 1);
                        }
                        break;
                    }
                    case CHINA_UNICOM: {
                        boolean isRepeat = reputCmcpMobiles(cuNumbers, pl.getProvinceCode(), number);
                        if (isRepeat) {
                            // 如果号码重复，则放置重复号码
                            repeatNumbers.append(number).append(MobileCatagory.MOBILE_SPLIT_CHARCATOR);
                            response.setRepeatSize(response.getRepeatSize() + 1);
                        }
                        break;
                    }
                    default:
                        filterNumbers.append(number).append(MobileCatagory.MOBILE_SPLIT_CHARCATOR);
                        response.setFilterSize(response.getFilterSize() + 1);
                        break;
                }

            }
            response.setCmNumbers(cmNumbers);
            response.setCtNumbers(ctNumbers);
            response.setCuNumbers(cuNumbers);
            response.setFilterNumbers(cutTail(filterNumbers.toString()));
            response.setRepeatNumbers(cutTail(repeatNumbers.toString()));
            response.setSuccess(true);

        } catch (Exception e) {
            logger.error("手机号码[" + numbers + "]分流错误", e);
            response.setSuccess(false);
            response.setMsg("手机号码[" + numbers.size() + "]分流错误");
        }

        logger.info(response.toString());
        return response;
    }

    /**
     * TODO 去除结尾符号信息
     *
     * @param mobile
     * @return
     */
    private static String cutTail(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return "";
        }

        return mobile.substring(0, mobile.length() - 1);
    }

    /**
     * 根据手机号码获取省份-运营归属信息
     * 
     * @param mobile 手机号码
     * @return 省份-运营商归属信息
     */
    private ProvinceLocal getProvinceLocalIfNotFound(String mobile) {
        CMCP cmcp = CMCP.local(mobile);

        if (CMCP.UNRECOGNIZED == cmcp) {
            logger.warn("手机号码[" + mobile + "]无法匹配任何运营商");
        } else if (CMCP.GLOBAL == cmcp) {
            logger.warn("手机号码[" + mobile + "]匹配运营商为'全网'");
        }

        return new ProvinceLocal(Province.PROVINCE_CODE_ALLOVER_COUNTRY, cmcp.getCode());
    }

    /**
     * TODO 提取手机号码归属地位（手机号码前7位确定归属地）
     *
     * @param mobile 手机号码
     * @return 省份-运营商 归属信息
     */
    private ProvinceLocal pickupMobileLocal(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return null;
        }

        try {
            // 手机号码前7位确定归属地
            String area = mobile.trim().substring(0, 7);

            ProvinceLocal pl = CommonRedisConstant.GLOBAL_MOBILES_LOCAL.get(area);

            return pl == null ? getProvinceLocalIfNotFound(mobile) : pl;

        } catch (Exception e) {
            logger.error("手机号码：{} 提取省份-运营商归属信息失败：{}", mobile, e.getMessage());
            return null;
        }
    }

    /**
     * TODO 判断手机号码是否无效
     *
     * @param mobile
     * @return
     */
    private boolean isInvalidMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return true;
        }

        if (mobile.trim().length() != 11) {
            return true;
        }

        return false;
    }

    /**
     * TODO 重新放置省份运营商手机号码数据
     *
     * @param mobiles 已组装的运营商手机号码数据
     * @param provinceCode 手机号码归属省份代码
     * @param mobile 手机号码
     */
    private boolean reputCmcpMobiles(Map<Integer, String> mobiles, Integer provinceCode, String mobile) {

        // 判断手机号码中是否已经重复
        Collection<String> values = mobiles.values();
        for (String marray : values) {
            if (marray.contains(mobile)) {
                return true;
            }
        }

        if (mobiles.containsKey(provinceCode)) {
            mobiles.put(provinceCode, mobiles.get(provinceCode) + MobileCatagory.MOBILE_SPLIT_CHARCATOR + mobile);
        } else {
            mobiles.put(provinceCode, mobile);
        }
        return false;
    }

    /**
     * 将数据加载到内存
     * 
     * @param list 省份-运营商数据
     */
    private void load2Cache(List<ProvinceLocal> list) {
        for (ProvinceLocal pl : list) {
            CommonRedisConstant.GLOBAL_MOBILES_LOCAL.put(pl.getNumberArea(), pl);
        }
    }

    private String getKey() {
        return CommonRedisConstant.RED_PROVINCE_MOBILES_LOCAL;
    }

    @Override
    public boolean reload() {
        try {
            String value = stringRedisTemplate.opsForValue().get(getKey());
            if (StringUtils.isNotEmpty(value)) {
                CommonRedisConstant.GLOBAL_MOBILES_LOCAL = JSON.parseObject(value,
                                                                            new TypeReference<Map<String, ProvinceLocal>>() {
                                                                            });
                return true;
            }

            List<ProvinceLocal> list = provinceLocalMapper.selectAvaiable();
            if (CollectionUtils.isEmpty(list)) {
                logger.error("省份手机号码归属地数据为空，加载失败");
                return false;
            }

            load2Cache(list);

            asyncService.publishMobilesLocalToRedis();

            return true;
        } catch (Exception e) {
            logger.error("省份手机号码归属地加载异常", e);
            return false;
        } finally {
            logger.info("Global mobiles local data[" + CommonRedisConstant.GLOBAL_MOBILES_LOCAL.size() + "] has loaded");
        }

    }

    @Override
    public ProvinceLocal getByMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return null;
        }

        return pickupMobileLocal(mobile);
    }

    @Override
    public Map<String, ProvinceLocal> getByMobiles(String[] mobiles) {
        if (mobiles == null || mobiles.length == 0) {
            return null;
        }

        Map<String, ProvinceLocal> mobileLocal = new HashMap<>();
        for (String mobile : mobiles) {
            ProvinceLocal pl = getByMobile(mobile);
            if (pl == null) {
                continue;
            }

            mobileLocal.put(mobile, pl);
        }

        return mobileLocal;
    }

}
