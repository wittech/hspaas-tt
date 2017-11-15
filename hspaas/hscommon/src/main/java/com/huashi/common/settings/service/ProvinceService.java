package com.huashi.common.settings.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.config.redis.CommonRedisConstant;
import com.huashi.common.settings.dao.ProvinceMapper;
import com.huashi.common.settings.domain.Province;

@Service
public class ProvinceService implements IProvinceService{
	
	@Autowired
	private ProvinceMapper provinceMapper;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public List<Province> findAvaiable() {
		try {
			Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(CommonRedisConstant.RED_PROVINCE);
			if(MapUtils.isNotEmpty(map)) {
				List<Province> provinces = new ArrayList<>();
				
				map.forEach((k,v) -> {
					provinces.add(new Province(Integer.parseInt(k.toString()), v.toString()));
				});
				return provinces;
			}
		} catch (Exception e) {
			logger.warn("省份REDIS加载出错 {}", e.getMessage());
		}
		
		return provinceMapper.selectAllAvaiable();
	}

	@Override
	public boolean reloadToRedis() {
		List<Province> list = provinceMapper.selectAllAvaiable();
		if(CollectionUtils.isEmpty(list)) {
			logger.error("省份数据加载为空，请检查");
			return false;
		}
			
		try {
			Map<String, String> ps = new HashMap<>();
			for(Province p : list) {
				// 目前暂时只需要省份的代码和名称对应关系，后续可扩展JSON方式存入
//				ps.put(p.getCode().toString(), JSON.toJSONString(p, new SimplePropertyPreFilter("code", "name")));
				ps.put(p.getCode().toString(), p.getName());
			}
			stringRedisTemplate.delete(CommonRedisConstant.RED_PROVINCE);
			stringRedisTemplate.opsForHash().putAll(CommonRedisConstant.RED_PROVINCE, ps);
			return true;
		} catch (Exception e) {
			logger.warn("REDIS加载省份信息失败", e);
		}
		
		return false;
	}

	@Override
	public Province get(Integer provinceCode) {
		if(provinceCode == null)
			return null;
		
		if(provinceCode == 0)
			return new Province(Province.PROVINCE_CODE_ALLOVER_COUNTRY, "全国");
		
		try {
			Object o = stringRedisTemplate.opsForHash().get(CommonRedisConstant.RED_PROVINCE, provinceCode.toString());
			if(o == null)
				throw new RuntimeException("Redis查询数据为空");
			
			return new Province(provinceCode, o.toString());
		} catch (Exception e) {
			logger.warn("省份代码：{} 查询数据异常，信息：{}", provinceCode, e.getMessage());
			return provinceMapper.selectByCode(provinceCode);
		}
	}

	@Override
	public Map<Integer, String> findNamesInMap() {
		List<Province> list = findAvaiable();
		if(CollectionUtils.isEmpty(list))
			return null;
		
		Map<Integer, String> map = new HashMap<Integer, String>();
		for(Province province : list) {
			map.put(province.getCode(), province.getName());
		}
		
		return map;
	}

}
