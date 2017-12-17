package com.huashi.bill.bill.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.bill.bill.dao.ConsumptionReportMapper;
import com.huashi.bill.bill.domain.ConsumptionReport;
import com.huashi.bill.bill.model.FluxDiscountModel;
import com.huashi.common.util.DateUtil;
import com.huashi.common.vo.TimeLineChart;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.fs.product.domain.FluxProduct;
import com.huashi.fs.product.service.IFluxProductService;
import com.huashi.sms.record.service.ISmsMtSubmitService;

@Service
public class BillService implements IBillService {
	
//	@Reference
//	private IUserService userService;
//	@Reference
//	private IUserSmsConfigService userSmsConfigService;
	@Reference
	private IFluxProductService fluxProductService;
	@Autowired
	private ConsumptionReportMapper consumptionReportMapper;
	@Reference
	private ISmsMtSubmitService smsMtSubmitService;
	
	@Override
	public FluxDiscountModel getFluxDiscountPrice(int userId, String packages, String mobile) {
		// 根据手机号码判断归属地、运营商
		CMCP cmcp = CMCP.local(mobile);
		if (CMCP.UNRECOGNIZED == cmcp) {
            {
                throw new IllegalArgumentException("手机号码无法识别归属地：" + mobile);
            }
        }

		// 根据套餐面值、运营商及归属地 及用户确定的通道组 ？？确定产品（判断是否有）
		List<FluxProduct> products = fluxProductService.findByPackage(packages, cmcp.getCode());
		if (CollectionUtils.isEmpty(products)) {
            {
                throw new IllegalArgumentException("未找到该面值流量套餐信息：" + packages + "M");
            }
        }

		// 根据用户查询具体的流量通道组，判断该通道组下是否支持该流量产品（暂不做）

		FluxProduct product = products.iterator().next();
		return new FluxDiscountModel(product.getId(), product.getName(), product.getOfficialPrice(),
				product.getOutPriceOff());
	}

	@Override
	public Map<String, Object> getConsumptionReport(int userId, int platformType, int limitSize) {
		List<ConsumptionReport> list = consumptionReportMapper.selectByUserIdAndType(userId, platformType, limitSize);
		if(CollectionUtils.isEmpty(list)) {
            {
                return null;
            }
        }
		
		String[] result = timelineTitle(platformType);
		if(result.length != 3) {
            {
                return null;
            }
        }
		
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
		
		if(CollectionUtils.isEmpty(list)) {
            {
                return;
            }
        }
		
		consumptionReportMapper.batchInsert(list);
		
	}

}
