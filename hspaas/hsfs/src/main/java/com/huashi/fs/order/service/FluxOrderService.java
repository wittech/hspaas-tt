/**
 * 
 */
package com.huashi.fs.order.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.dubbo.config.annotation.Service;
import com.huashi.common.util.LogUtils;
import com.huashi.common.vo.PaginationVo;
import com.huashi.fs.order.dao.FluxOrderMapper;
import com.huashi.fs.order.domain.FluxOrder;

/**
 * 流量订单服务实现类
 * @author Administrator
 *
 */
@Service
public class FluxOrderService implements IFluxOrderService{
	
	@Autowired
	private FluxOrderMapper orderMapper;

	@Override
	public PaginationVo<FluxOrder> findPage(int userId, String mobile, String startDate, String endDate,
			String currentPage) {
		if (userId<=0) {
            return null;
        }

		int _currentPage = PaginationVo.parse(currentPage);

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		if (StringUtils.isNotEmpty(mobile)) {
			params.put("mobile", mobile);
		}
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		LogUtils.info("入参params="+params);
		int totalRecord = orderMapper.getCountByUserId(params);
		if (totalRecord == 0) {
            return null;
        }

		params.put("startPage", PaginationVo.getStartPage(_currentPage));
		params.put("pageRecord", PaginationVo.DEFAULT_RECORD_PER_PAGE);

		List<FluxOrder> list = orderMapper.findPageListByUserId(params);
		if (list == null || list.isEmpty()) {
            return null;
        }
		return new PaginationVo<FluxOrder>(list, _currentPage, totalRecord);
	}
	
	@Override
	public List<FluxOrder> findUserIdAndMobileRecord(int userId,String mobile){
		if(userId <=0 || StringUtils.isEmpty(mobile)){
			return null;
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userId", userId);
		params.put("mobile", mobile);
		return orderMapper.findUserIdAndMobileRecord(params);
	}
	
}
