/**
 * 
 */
package com.huashi.common.provider.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.provider.dao.ProviderApiCodeMapper;
import com.huashi.common.provider.domain.ProviderApiCode;

/**
 * 状态码服务类
 * 
 * @author Administrator
 *
 */
//@Service(loadbalance="")
@Service
public class ProviderApiCodeService implements IProviderApiCodeService {

	@Autowired
	private ProviderApiCodeMapper providerApiCodeMapper;

	@Override
	public List<ProviderApiCode> findTypeAndCodeRecord(int type, String code) {
		if (type == 0 || StringUtils.isEmpty(code)) {
			return null;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		params.put("code", code);
		return providerApiCodeMapper.findTypeAndCodeRecord(params);
	}
}
