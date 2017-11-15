package com.huashi.monitor.passage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.exchanger.service.ISmsProviderService;
import com.huashi.monitor.config.redis.MonitorRedisConstant;
import com.huashi.monitor.constant.MonitorConstant;
import com.huashi.monitor.constant.MonitorPassageContext.PassagePullRunnintStatus;
import com.huashi.monitor.passage.model.PassagePullReport;
import com.huashi.monitor.passage.model.PassageReachRateReport;
import com.huashi.monitor.passage.thread.BaseThread;
import com.huashi.monitor.passage.thread.PassageMoReportPullThread;
import com.huashi.monitor.passage.thread.PassageMtReportPullThread;
import com.huashi.monitor.passage.thread.PassageReachRateReportPullThread;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.domain.SmsPassageAccess;
import com.huashi.sms.passage.domain.SmsPassageReachrateSettings;
import com.huashi.sms.passage.service.ISmsPassageAccessService;
import com.huashi.sms.passage.service.ISmsPassageReachrateSettingsService;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.huashi.sms.record.service.ISmsMoMessageService;
import com.huashi.sms.record.service.ISmsMtDeliverService;
import com.huashi.sms.record.service.ISmsMtSubmitService;

@Service
public class PassageMonitorService implements IPassageMonitorService{
	
//	@Reference(mock="return null", check = false)
	@Reference
	private ISmsProviderService smsProviderService;
	@Reference
	private ISmsMtDeliverService smsMtDeliverService;
	@Reference
	private ISmsMoMessageService smsMoMessageService;
	@Reference
	private ISmsPassageAccessService smsPassageAccessService;
	@Reference
	private ISmsPassageService smsPassageService;
	@Reference
	private ISmsMtSubmitService smsMtSubmitService;
	@Reference
    private ISmsPassageReachrateSettingsService smsPassageReachrateSettingsService;
	
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	
	private Logger logger = LoggerFactory.getLogger(PassageMonitorService.class);
	
	/**
	 * 
	   * TODO 获取通道线程名称
	   * 
	   * @param passageId
	   * @param callType
	   * @return
	 */
	private String getPassageThreadName(Integer passageId, Integer callType) {
		return String.format("%s-sms-%d-%d", BaseThread.PASSAGE_PULL_THREAD_PREFIX, passageId, callType);
	}
	
	
	/**
	 * 
	   * TODO 通道下行状态扫描
	 */
	private void doSmsPassageStatusPulling() {
		List<SmsPassageAccess> list = smsPassageAccessService.findWaitPulling(PassageCallType.STATUS_RECEIPT_WITH_SELF_GET);
		if(CollectionUtils.isEmpty(list)) {
			logger.warn("未检索到通道下行状态报告回执");
			return;
		}
		
		for(SmsPassageAccess access : list) {
			joinPassageThread(getPassageThreadName(access.getPassageId(), access.getCallType()), access);
		}
	}
	
	/**
	 * 
	   * TODO 通道上行回执数据扫描
	 */
	private void doSmsPassageMoPulling() {
		List<SmsPassageAccess> list = smsPassageAccessService.findWaitPulling(PassageCallType.SMS_MO_REPORT_WITH_SELF_GET);
		if(CollectionUtils.isEmpty(list)) {
			logger.warn("未检索到通道上行报告回执");
			return;
		}
		
		for(SmsPassageAccess access : list) {
			joinPassageThread(getPassageThreadName(access.getPassageId(), access.getCallType()), access);
		}
	}

	private void doSmsPassageMonitorPulling(){
        try {
        	StringBuilder passage = new StringBuilder();
        	List<SmsPassageReachrateSettings> list = smsPassageReachrateSettingsService.getByUseable();
        	for(SmsPassageReachrateSettings model : list){
                String threadName = String.format("%s-sms-passage-monitor-%d",BaseThread.PASSAGE_PULL_THREAD_PREFIX,model.getId());
                Thread thread = new Thread(new PassageReachRateReportPullThread(model,smsMtSubmitService,
                        smsPassageReachrateSettingsService,smsPassageService, stringRedisTemplate),threadName);
                thread.start();
                
                passage.append("通道:").append(model.getPassageId()).append(";");
            }
        	
        	logger.info("通道回执率监控已开启：{}", passage.toString());
		} catch (Exception e) {
			logger.info("通道回执率监控启动失败：{}", e);
		}
        
    }



	@Override
	public List<PassagePullReport> findPassagePullReport() {
		try {
			Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(MonitorConstant.RD_PASSAGE_PULL_THREAD_GROUP);
			if(MapUtils.isNotEmpty(map)) {
				List<PassagePullReport> reports = new ArrayList<>();
				
				map.forEach((k,v) -> {
					reports.add(JSON.parseObject(v.toString(), PassagePullReport.class));
				});
				return reports;
			}
			
			
		} catch (Exception e) {
			logger.error("查询带轮训通道队列报告失败", e);
		}
		
		return null;
	}

	@Override
	public void flushPullReport() {
		try {
			Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(MonitorConstant.RD_PASSAGE_PULL_THREAD_GROUP);
			logger.info("通道轮训重启清除数据：{}", MapUtils.isEmpty(map) ? "无" : map);
			
			stringRedisTemplate.delete(MonitorConstant.RD_PASSAGE_PULL_THREAD_GROUP);
			
		} catch (Exception e) {
			logger.error("清除轮训通道数据异常", e);
		}
		
	}

	@Override
	public boolean startPassagePull() {
		try {
			flushPullReport();
			
			doSmsPassageStatusPulling();
			
			doSmsPassageMoPulling();

			doSmsPassageMonitorPulling();
			
			return true;
		} catch (Exception e) {
			logger.error("开启通道轮训总开关失败", e);
			return false;
		}
	}


	
	/**
	 * 
	   * TODO 加入线程至监管中
	   * 
	   * @param threadName
	   * @param access
	 */
	private void joinPassageThread(String threadName, SmsPassageAccess access) {
		try {
			Thread thread =  null;
			PassageCallType callType = PassageCallType.parse(access.getCallType());
			
			switch (callType) {
			case STATUS_RECEIPT_WITH_SELF_GET:
				thread = new Thread(new PassageMtReportPullThread(access, smsMtDeliverService, smsProviderService,
						this), threadName);
				break;
				
			case SMS_MO_REPORT_WITH_SELF_GET:
				thread = new Thread(new PassageMoReportPullThread(access, smsMoMessageService, smsProviderService,
						this), threadName);
				break;

			default:
				break;
			}
			
			if(thread == null) {
				logger.warn("通道调用类型(PassageCallType)无法解析到具体的业务：{}，无法启动监听", access.getCallType());
				return;
			}
			
			thread.start();
			
			BaseThread.PASSAGES_IN_RUNNING.put(threadName, true);
			
			logger.info("{} 通道轮训线程：{} 已开启监听", callType.getName(), threadName);
			
			PassagePullReport report = new PassagePullReport();
			report.setPassageId(access.getPassageId());
			report.setCallType(access.getCallType());
			report.setPassageCode(access.getPassageCode());
			report.setStatus(PassagePullRunnintStatus.YES.getCode());
			
			// 根据通道ID获取通道信息，查询账号信息（方便区分 一个通道给我放开多个通道进行友好辨认）
			SmsPassage smsPassage = smsPassageService.findById(access.getPassageId());
			if(smsPassage != null) {
				report.setAccount(smsPassage.getAccount());
				report.setPassageCode(smsPassage.getCode());
			}
			
			stringRedisTemplate.opsForHash().put(MonitorConstant.RD_PASSAGE_PULL_THREAD_GROUP, threadName, JSON.toJSONString(report, 
					SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty));
			
		} catch (Exception e) {
			logger.error("线程名称：{} 加入到监管中心失败", threadName);
		}
		
	}


	@Override
	public boolean addPassagePull(SmsPassageAccess access) {
		String key =  null;
		try {
			key = getPassageThreadName(access.getPassageId(), access.getCallType());
			
			// 如果运行中通道已经有此通道信息了，则不需要再启动一个线程
			if(BaseThread.PASSAGES_IN_RUNNING.containsKey(key) && BaseThread.PASSAGES_IN_RUNNING.get(key)) {
				logger.info("当前线程组中已经存在此通道：{}信息，无需重新开启", key);
				return true;
			}
			
			joinPassageThread(key, access);
			
			logger.info("运行中轮训通道：{} 增加通道完成");
			
			return true;
		} catch (Exception e) {
			logger.info("运行中轮训通道：{} 增加通道失败", key, e);
		}
		
		return false;
	}


	@Override
	public boolean removePasagePull(SmsPassageAccess access) {
		String key =  null;
		try {
			key = getPassageThreadName(access.getPassageId(), access.getCallType());
			
			BaseThread.PASSAGES_IN_RUNNING.put(key, false);
			
			Object obj = stringRedisTemplate.opsForHash().get(MonitorConstant.RD_PASSAGE_PULL_THREAD_GROUP, key);
			if(obj == null) {
				logger.warn("REDIS 移除轮训通道：{} 失败，数据为空（已被移除或从未生效）");
				return true;
			}
			
			PassagePullReport report = JSON.parseObject(obj.toString(), PassagePullReport.class);
			report.setStatus(PassagePullRunnintStatus.NO.getCode());
			
			stringRedisTemplate.opsForHash().put(MonitorConstant.RD_PASSAGE_PULL_THREAD_GROUP, key, JSON.toJSONString(report, 
					SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty));
			
			logger.info("运行中轮训通道：{} 终止操作完成", key);
			
			return true;
		} catch (Exception e) {
			logger.info("运行中轮训通道：{} 终止操作失败", key, e);
		}
		
		return false;
	}


	@Override
	public boolean updatePullReport(PassagePullReport report) {
		return false;
	}

	@Override
	public boolean updatePullReportToRedis(String key, PassagePullReport report) {
		try {
			Object obj = stringRedisTemplate.opsForHash().get(MonitorConstant.RD_PASSAGE_PULL_THREAD_GROUP, key);
			if(obj == null) {
				logger.warn("REDIS 移除轮训通道：{} 失败，数据为空（已被移除或从未生效）");
				return true;
			}
			
			PassagePullReport plainReport = JSON.parseObject(obj.toString(), PassagePullReport.class);
			plainReport.setIntevel(report.getIntevel());
			plainReport.setLastTime(report.getLastTime());
			plainReport.setLastAmount(report.getLastAmount());
			plainReport.setCostTime(report.getCostTime());
			plainReport.setPullAvaiableTimes(report.getPullAvaiableTimes());
			
			stringRedisTemplate.opsForHash().put(MonitorConstant.RD_PASSAGE_PULL_THREAD_GROUP, key, JSON.toJSONString(plainReport, 
					SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty));

			return true;
			
		} catch (Exception e) {
			logger.error("更新通道轮训线程：{} 失败", e);
		}
		return false;
	}

    @Override
    public boolean addSmsPassageMonitor(SmsPassageReachrateSettings model) {
        try {
            String threadName = String.format("%s-sms-passage-monitor-%d",BaseThread.PASSAGE_PULL_THREAD_PREFIX,model.getId());
            Thread thread = new Thread(new PassageReachRateReportPullThread(model,smsMtSubmitService,
                    smsPassageReachrateSettingsService, smsPassageService, stringRedisTemplate),threadName);
            thread.start();
            return true;
        }catch (Exception e){
            logger.error("添加通道监控失败！",e);
        }
        return false;
    }

    /**
     * 
       * TODO 从REDIS KEY中摘取通道ID
       * 
       * @param key
       * @return
     */
    private static Integer pickPassageIdFromKey(String key) {
    	try {
			return Integer.parseInt(key.split(":")[1]);
		} catch (Exception e) {
			return null;
		}
    }

	@Override
	public Map<Integer, List<PassageReachRateReport>> findReachrateReport(Integer passageId) {
		try {
			Map<Integer, List<PassageReachRateReport>> report = new HashMap<Integer, List<PassageReachRateReport>>();
			List<PassageReachRateReport> list = null;
			
			if(passageId == null) {
				// 查询所有符合条件的KEYS
				Set<String> keys = stringRedisTemplate.keys(MonitorRedisConstant.RED_PASSAGE_REACHRATE_REPORT + "*");
				if(CollectionUtils.isEmpty(keys))
					return null;
				
				for(String key : keys) {
					passageId = pickPassageIdFromKey(key);
					if(passageId == null)
						continue;
					
					list = getReachrateReport(key);
					if(CollectionUtils.isEmpty(list))
						continue;
					
					report.put(passageId, list);
				}
				
				return report;
			}
			
			list = getReachrateReport(String.format("%s:%d", MonitorRedisConstant.RED_PASSAGE_REACHRATE_REPORT, passageId));
			if(CollectionUtils.isEmpty(list))
				return null;
			
			report.put(passageId, list);
			
			return report;
		} catch (Exception e) {
			logger.error("通道到达率统计报告查询REDIS失败", e);
			return null;
		}
	}

	private List<PassageReachRateReport> getReachrateReport(String key) {
		try {
			Set<String> container = stringRedisTemplate.opsForZSet().reverseRangeByScore(key, 0, System.currentTimeMillis());
			if(CollectionUtils.isEmpty(container))
				return null;
			
			List<PassageReachRateReport> list = new ArrayList<PassageReachRateReport>(container.size());
			for(String report : container) {
				list.add(JSON.parseObject(report, PassageReachRateReport.class));
			}
			
			return list;
		} catch (Exception e) {
			logger.error("REDIS key : {} 获取通道到达率报告失败", e);
			return null;
		}
	}

}
