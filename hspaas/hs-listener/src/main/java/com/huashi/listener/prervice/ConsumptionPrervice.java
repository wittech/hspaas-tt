package com.huashi.listener.prervice;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.bill.bill.service.IBillService;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.listener.constant.ListenerConstant;

@Service
public class ConsumptionPrervice {
	
	@Reference
	private IBillService billService;

	@Scheduled(cron = ListenerConstant.CONSUMPTION_EXECUTE_CRON)
	public void updateYestdayConsumption() {
		// 
		
		billService.updateConsumptionReport(PlatformType.SEND_MESSAGE_SERVICE.getCode());
		
//		billService.updateConsumptionReport(PlatformType.FLUX_SERVICE.getCode());
		
//		billService.updateConsumptionReport(PlatformType.VOICE_SERVICE.getCode());
	}
}
