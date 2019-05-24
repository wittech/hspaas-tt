/**
 * 
 */
package com.huashi.vs.record.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.dubbo.config.annotation.Service;
import com.huashi.common.vo.PaginationVo;
import com.huashi.vs.record.dao.RecordMapper;
import com.huashi.vs.record.domain.Record;

/**
 * 语音发送记录服务接口实现类
 * @author Administrator
 *
 */
@Service
public class RecordService implements IRecordService{

	@Autowired
	private RecordMapper recordMapper;
	
	@Override
	public boolean send(Record record) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PaginationVo<Record> findPage(int userId, String phoneNumber, String startDate, String endDate,
			String currentPage, String status,String type) {
		if (userId<=0) {
            {
                return null;
            }
        }

		int _currentPage = PaginationVo.parse(currentPage);

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		if (StringUtils.isNotEmpty(phoneNumber)) {
			params.put("phoneNumber", phoneNumber);
		}
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		params.put("receiveStatus", status);
		params.put("type", type);
		int totalRecord = recordMapper.getCountByUserId(params);
		if (totalRecord == 0) {
            {
                return null;
            }
        }

		params.put("startPage", PaginationVo.getStartPage(_currentPage));
		params.put("pageRecord", PaginationVo.DEFAULT_RECORD_PER_PAGE);

		List<Record> list = recordMapper.findPageListByUserId(params);
		if (list == null || list.isEmpty()) {
            {
                return null;
            }
        }
		return new PaginationVo<Record>(list, _currentPage, totalRecord);
	}

	@Override
	public Record selectByPrimaryKey(Long id) {
		return recordMapper.selectByPrimaryKey(id);
	}
	
	@Override
	public List<Record> findMobileRecord(int userId,String mobile) {
		if(userId<=0 || StringUtils.isEmpty(mobile)){
			return null;
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userId", userId);
		params.put("mobile", mobile);
		return recordMapper.findMobileRecord(params);
	}

}
