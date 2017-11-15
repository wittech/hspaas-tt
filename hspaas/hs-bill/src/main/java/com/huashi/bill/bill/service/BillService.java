package com.huashi.bill.bill.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.huashi.bill.bill.constant.SmsBillConstant;
import com.huashi.bill.bill.dao.ConsumptionReportMapper;
import com.huashi.bill.bill.domain.ConsumptionReport;
import com.huashi.bill.bill.model.FluxDiscountModel;
import com.huashi.bill.bill.model.SmsP2PBillModel;
import com.huashi.common.user.domain.UserSmsConfig;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.user.service.IUserSmsConfigService;
import com.huashi.common.util.DateUtil;
import com.huashi.common.vo.TimeLineChart;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.fs.product.domain.FluxProduct;
import com.huashi.fs.product.service.IFluxProductService;
import com.huashi.sms.record.service.ISmsMtSubmitService;

@Service
public class BillService implements IBillService {
	
	@Reference
	private IUserService userService;
	@Reference
	private IUserSmsConfigService userSmsConfigService;
	@Reference
	private IFluxProductService fluxProductService;
	@Autowired
	private ConsumptionReportMapper consumptionReportMapper;
	@Reference
	private ISmsMtSubmitService smsMtSubmitService;
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public FluxDiscountModel getFluxDiscountPrice(int userId, String packages, String mobile) {
		// 根据手机号码判断归属地、运营商
		CMCP cmcp = CMCP.local(mobile);
		if (CMCP.UNRECOGNIZED == cmcp)
			throw new IllegalArgumentException("手机号码无法识别归属地：" + mobile);

		// 根据套餐面值、运营商及归属地 及用户确定的通道组 ？？确定产品（判断是否有）
		List<FluxProduct> products = fluxProductService.findByPackage(packages, cmcp.getCode());
		if (CollectionUtils.isEmpty(products))
			throw new IllegalArgumentException("未找到该面值流量套餐信息：" + packages + "M");

		// 根据用户查询具体的流量通道组，判断该通道组下是否支持该流量产品（暂不做）

		FluxProduct product = products.iterator().next();
		return new FluxDiscountModel(product.getId(), product.getName(), product.getOfficialPrice(),
				product.getOutPriceOff());
	}

	@Override
	public int getSmsFeeByWords(int userId, String content) {
		if (StringUtils.isEmpty(content))
			throw new IllegalArgumentException("短信内容为空，无法计算计费条数");

		int wordsPerNum = getUserSmsWordsPerNum(userId);
		
		return calculateSmsNum(wordsPerNum, content);
	}
	
	/**
	 * 
	   * TODO 根据用户ID查询每条短信计费字数
	   * 
	   * @param userId
	   * @return
	 */
	private int getUserSmsWordsPerNum(int userId) {
		int wordsPerNum = SmsBillConstant.WORDS_SIZE_PER_NUM;
		try {
			UserSmsConfig userSmsConfig = userSmsConfigService.getByUserId(userId);
			if (userSmsConfig != null)
				wordsPerNum = userSmsConfig.getSmsWords();
		} catch (Exception e) {
			logger.warn("查询用户：{} 短信字数配置失败，将以默认每条字数：{}计费", userId, wordsPerNum, e);
		}
		return wordsPerNum;
	}
	
	/**
	 * 
	   * TODO 根据短信内容和每条计费字数 计算总费用
	   * 
	   * @param wordsPerNum
	   * 		每条计费字数
	   * @param content
	   * 		短信内容
	   * @return
	 */
	private int calculateSmsNum(int wordsPerNum, String content) {
		if(StringUtils.isEmpty(content))
			return SmsBillConstant.CONTENT_WORDS_EXCEPTION_COUNT_FEE;
		
		// 按照70个字为单位，减去设置的第一条短信计费字数，得出实际签名内容长度
		int realTotalWords = SmsBillConstant.WORDS_SIZE_PER_NUM - wordsPerNum + content.length();
		if(realTotalWords <= SmsBillConstant.WORDS_SIZE_PER_NUM) {
			// 如果减除的字数后 小于等于0，则按照1条计费
			return SmsBillConstant.CONTENT_WORDS_EXCEPTION_COUNT_FEE;
		}

		// 长短信计费按照67字计费
		return realTotalWords % SmsBillConstant.LONG_TEXT_MESSAGE_WORDS_SIZE_PER_NUM == 0  ? realTotalWords / SmsBillConstant.LONG_TEXT_MESSAGE_WORDS_SIZE_PER_NUM 
				: realTotalWords / SmsBillConstant.LONG_TEXT_MESSAGE_WORDS_SIZE_PER_NUM + 1;
	}
	
	@Override
	public SmsP2PBillModel getSmsFeeInP2P(int userId, List<JSONObject> p2pBodies){
		if (CollectionUtils.isEmpty(p2pBodies))
			throw new IllegalArgumentException("点对点短信报文为空，无法计算计费条数");
		
		// 总费用
		int smsTotalNum = 0;
		int wordsPerNum = getUserSmsWordsPerNum(userId);
		for(JSONObject obj : p2pBodies) {
			int num = calculateSmsNum(wordsPerNum, obj.getString("content"));
			obj.put("fee", num);
			smsTotalNum += num;
		}
		
		return new SmsP2PBillModel(smsTotalNum, p2pBodies);
	}
	
	@Override
	public SmsP2PBillModel getSmsFeeInP2PTemplate(int userId, String content, List<JSONObject> p2pBodies) {
		if (CollectionUtils.isEmpty(p2pBodies) || StringUtils.isEmpty(content))
			throw new IllegalArgumentException("模板点对点短信内容或报文为空，无法计算计费条数");
		
		// 总费用
		int smsTotalNum = 0;
		
		int wordsPerNum = getUserSmsWordsPerNum(userId);
		String finalContent = null;
		for(JSONObject obj : p2pBodies) {
			finalContent = doReplaceTemplateContent(content, obj.getObject("args", Object[].class));
			
			int num = calculateSmsNum(wordsPerNum, finalContent);
			obj.put("content", finalContent);
			obj.put("fee", num);
			smsTotalNum += num;
		}
		
		return new SmsP2PBillModel(smsTotalNum, p2pBodies);
	}
	
	/**
	 * 
	   * TODO 替换模板点对点短信内容
	   * 
	   * @param content
	   * 		短信内容
	   * @param args
	   * 		参数信息
	   * @return
	 */
	private static String doReplaceTemplateContent(String content, Object[] args) {
		Pattern pattern = Pattern.compile("#args#");
		Matcher matcher = pattern.matcher(content);
		
		StringBuffer finalContent = new StringBuffer();
		int index = 0;
		while (matcher.find()) {
			if(index >= args.length)
				break;
			
			matcher.appendReplacement(finalContent, args[index].toString());
			index++;
		}
		
		matcher.appendTail(finalContent);//添加尾巴
		return finalContent.toString();
	}

	@Override
	public Map<String, Object> getConsumptionReport(int userId, int platformType, int limitSize) {
		List<ConsumptionReport> list = consumptionReportMapper.selectByUserIdAndType(userId, platformType, limitSize);
		if(CollectionUtils.isEmpty(list))
			return null;
		
		String[] result = timelineTitle(platformType);
		if(result.length != 3)
			return null;
		
		return TimeLineChart.draw(data2Chart(list), result[0], result[1], result[2]);
	}
	
	private static List<TimeLineChart> data2Chart(List<ConsumptionReport> report) {
		List<TimeLineChart> list = new ArrayList<>();
		TimeLineChart tlc = null;
		for(ConsumptionReport cr : report) {
			tlc = new TimeLineChart();
			tlc.setAmount(cr.getAmount());
			tlc.setXlable(DateUtil.getDayStr(cr.getRecordDate()));
			tlc.setLineType("数量统计");
			list.add(tlc);
		}
		return list;
	}
	
	/**
	 * 
	   * TODO 转换标题
	   * @param platform
	   * @return
	 */
	private static String[] timelineTitle(int platform) {
		String[] result = new String[3];
		switch(PlatformType.parse(platform)) {
		case SEND_MESSAGE_SERVICE : {
			result[0] = "近期短信发送记录统计";
			result[1] = "发送数量（条）";
			result[2] = "条";
			break;
		}
		case FLUX_SERVICE : {
			result[0] = "近期流量充值记录统计";
			result[1] = "充值金额（元）";
			result[2] = "元";
			break;
		}
		case VOICE_SERVICE : {
			result[0] = "近期语音发送记录统计";
			result[1] = "发送数量（条）";
			result[2] = "条";
			break;
		}
		default:
			break;
		}
		return result;
	}

	@Override
	public void updateConsumptionReport(int platformType) {
		List<ConsumptionReport> list = new ArrayList<>();
		switch(PlatformType.parse(platformType)) {
		case SEND_MESSAGE_SERVICE : {
			list = smsMtSubmitService.getConsumeMessageInYestday();
			break;
		}
		case FLUX_SERVICE : {
			break;
		}
		case VOICE_SERVICE : {
			break;
		}
		
		default:
			throw new RuntimeException("找不到平台类型");
		}
		
		if(CollectionUtils.isEmpty(list))
			return;
		
		consumptionReportMapper.batchInsert(list);
		
	}

}
