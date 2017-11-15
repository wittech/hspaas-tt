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
import com.huashi.common.settings.dao.ProvinceLocalMapper;
import com.huashi.common.settings.domain.Province;
import com.huashi.common.settings.domain.ProvinceLocal;
import com.huashi.common.third.model.MobileCatagory;
import com.huashi.constants.CommonContext.CMCP;

@Service
public class MobileLocalService implements IMobileLocalService {

	@Autowired
	private ProvinceLocalMapper provinceLocalMapper;

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	// 全局手机归属号码（与REDIS 同步采用广播模式）
	public static Map<String, ProvinceLocal> GLOBAL_MOBILE_LOCATION = new HashMap<String, ProvinceLocal>();

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public MobileCatagory doCatagory(String mobileNumber) {
		if (StringUtils.isEmpty(mobileNumber))
			return null;

		MobileCatagory response = new MobileCatagory();
		try {
			String[] numbers = mobileNumber.split(MobileCatagory.MOBILE_SPLIT_CHARCATOR);
			if (numbers.length == 0)
				return null;

			return doCatagory(Arrays.asList(numbers));
		} catch (Exception e) {
			logger.error("{} 号码分流错误, 信息为: {}", mobileNumber, e.getMessage());
			response.setSuccess(false);
			response.setMsg("号码分流错误");
		}
		logger.info(response.toString());
		return response;
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
			Map<Integer, String> cmNumbers = new HashMap<Integer, String>();
			// 电信号码
			Map<Integer, String> ctNumbers = new HashMap<Integer, String>();
			// 联通号码
			Map<Integer, String> cuNumbers = new HashMap<Integer, String>();

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

				String area = pickupMobileLocal(number);
				if (area == null) {
					// 如果截取 手机号码归属地位失败（手机号码前7位），则放置过滤号码中
					filterNumbers.append(number).append(MobileCatagory.MOBILE_SPLIT_CHARCATOR);
					response.setFilterSize(response.getFilterSize() + 1);
					continue;
				}

				ProvinceLocal pl = GLOBAL_MOBILE_LOCATION.get(area);
				if (pl == null) {
					// 如果不能确定省份,则根据 手机号码判断运营商,然后省份代码定义为 全国(0)
					CMCP cmcp = CMCP.local(number);
					switch (cmcp) {
					case UNRECOGNIZED: {
						filterNumbers.append(number).append(MobileCatagory.MOBILE_SPLIT_CHARCATOR);
						response.setFilterSize(response.getFilterSize() + 1);
						break;
					}
					case CHINA_MOBILE: {
						boolean isRepeat = reputCmcpMobiles(cmNumbers, Province.PROVINCE_CODE_ALLOVER_COUNTRY, number);
						if (isRepeat) {
							// 如果号码重复，则放置重复号码
							repeatNumbers.append(number).append(MobileCatagory.MOBILE_SPLIT_CHARCATOR);
							response.setRepeatSize(response.getRepeatSize() + 1);
						}
						break;
					}
					case CHINA_TELECOM: {
						boolean isRepeat = reputCmcpMobiles(ctNumbers, Province.PROVINCE_CODE_ALLOVER_COUNTRY, number);
						if (isRepeat) {
							// 如果号码重复，则放置重复号码
							repeatNumbers.append(number).append(MobileCatagory.MOBILE_SPLIT_CHARCATOR);
							response.setRepeatSize(response.getRepeatSize() + 1);
						}
						break;
					}
					case CHINA_UNICOM: {
						boolean isRepeat = reputCmcpMobiles(cuNumbers, Province.PROVINCE_CODE_ALLOVER_COUNTRY, number);
						if (isRepeat) {
							// 如果号码重复，则放置重复号码
							repeatNumbers.append(number).append(MobileCatagory.MOBILE_SPLIT_CHARCATOR);
							response.setRepeatSize(response.getRepeatSize() + 1);
						}
						break;
					}
					default: {
						break;
					}
					}
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
			logger.error("{} 号码分流错误, 信息为: {}", numbers, e.getMessage());
			response.setSuccess(false);
			response.setMsg("号码分流错误");
		}
		logger.info(response.toString());
		return response;
	}

	/**
	 * 
	 * TODO 去除结尾符号信息
	 * 
	 * @param mobile
	 * @return
	 */
	private static String cutTail(String mobile) {
		if (StringUtils.isEmpty(mobile))
			return "";

		return mobile.substring(0, mobile.length() - 1);
	}

	/**
	 * 
	 * TODO 提取手机号码归属地位（手机号码前7位确定归属地）
	 * 
	 * @param mobile
	 * @return
	 */
	private String pickupMobileLocal(String mobile) {
		if (StringUtils.isEmpty(mobile))
			return null;

		try {
			return mobile.trim().substring(0, 7);
		} catch (Exception e) {
			logger.error("手机号码：{}提取归属地失败：{}", mobile, e.getMessage());
			return null;
		}
	}

	/**
	 * 
	 * TODO 判断手机号码是否无效
	 * 
	 * @param mobile
	 * @return
	 */
	private boolean isInvalidMobile(String mobile) {
		if (StringUtils.isEmpty(mobile))
			return true;

		if (mobile.trim().length() != 11)
			return true;

		return false;
	}

	/**
	 * 
	 * TODO 重新放置省份运营商手机号码数据
	 * 
	 * @param mobiles
	 *            已组装的运营商手机号码数据
	 * @param provinceCode
	 *            手机号码归属省份代码
	 * @param mobile
	 *            手机号码
	 */
	private boolean reputCmcpMobiles(Map<Integer, String> mobiles, Integer provinceCode, String mobile) {

		// 判断手机号码中是否已经重复
		Collection<String> values = mobiles.values();
		for (String marray : values) {
			if (marray.contains(mobile))
				return true;
		}

		if (mobiles.containsKey(provinceCode)) {
			mobiles.put(provinceCode,
					String.format("%s%s%s", mobiles.get(provinceCode), MobileCatagory.MOBILE_SPLIT_CHARCATOR, mobile));
		} else {
			mobiles.put(provinceCode, mobile);
		}
		return false;
	}

	@Override
	public boolean reload() {
		try {
			List<ProvinceLocal> list = provinceLocalMapper.selectAvaiable();
			if (CollectionUtils.isEmpty(list)) {
				logger.error("省份手机号码归属地数据为空，加载失败");
				return false;
			}

			for (ProvinceLocal pl : list) {
				GLOBAL_MOBILE_LOCATION.put(pl.getNumberArea(), pl);
			}
			return true;
		} catch (Exception e) {
			logger.error("份手机号码归属地加载异常");
		}

		return false;
	}

}
