/**
 * 
 */
package com.huashi.sms.record.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.constants.OpenApiCode.CommonApiCode;
import com.huashi.sms.record.dao.SmsApiFailedRecordMapper;
import com.huashi.sms.record.domain.SmsApiFailedRecord;

/**
 * 
  * TODO 短信接口调用错误记录服务实现类
  * 
  * developer 网关鉴权失败/余额不足等...
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2016年4月25日 上午9:49:37
 */
@Service
public class SmsApiFailedRecordService implements ISmsApiFaildRecordService {

	@Reference
	private IUserService userService;
	@Autowired
	private SmsApiFailedRecordMapper smsApiFailedRecordMapper;
	
	private Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

	@Override
	public PaginationVo<SmsApiFailedRecord> findPage(int userId, String phoneNumber, String startDate, String endDate,
			String currentPage) {
		if (userId<=0) {
            return null;
        }

		int _currentPage = PaginationVo.parse(currentPage);

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		if (StringUtils.isNotEmpty(phoneNumber)) {
			params.put("phoneNumber", phoneNumber);
		}
		params.put("startDate", startDate);
		params.put("endDate", endDate);

		int totalRecord = smsApiFailedRecordMapper.selectCount(params);
		if (totalRecord == 0) {
            return null;
        }

		params.put("startPage", PaginationVo.getStartPage(_currentPage));
		params.put("pageRecord", PaginationVo.DEFAULT_RECORD_PER_PAGE);

		List<SmsApiFailedRecord> list = smsApiFailedRecordMapper.selectPageList(params);
		if (list == null || list.isEmpty()) {
            return null;
        }

		for (SmsApiFailedRecord mr : list) {
			CommonApiCode code = CommonApiCode.parse(mr.getRespCode());
			mr.setErrorCodeText(code == null ? "" : code.getMessage());
		}

		return new PaginationVo<>(list, _currentPage, totalRecord);
	}

	@Override
	public boolean save(SmsApiFailedRecord record) {
		record.setCreateTime(new Date());
		try {
			return smsApiFailedRecordMapper.insertSelective(record) > 0;
		} catch (Exception e) {
			logger.warn("调用接口失败记录插入失败", e);
			return false;
		}
	}

	@Override
	public BossPaginationVo<SmsApiFailedRecord> findPage(int pageNum, String keyword) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("keyword", keyword);
		BossPaginationVo<SmsApiFailedRecord> page = new BossPaginationVo<>();
		page.setCurrentPage(pageNum);
		int total = smsApiFailedRecordMapper.findCount(paramMap);
		if (total <= 0) {
			return page;
		}
		page.setTotalCount(total);
		paramMap.put("start", page.getStartPosition());
		paramMap.put("end", page.getPageSize());
		List<SmsApiFailedRecord> dataList = smsApiFailedRecordMapper.findList(paramMap);
		for(SmsApiFailedRecord record : dataList){
			if(record != null && record.getUserId() != null){
				record.setUserModel(userService.getByUserId(record.getUserId()));
			}
		}
		page.getList().addAll(dataList);
		return page;
	}
}
