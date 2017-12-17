package com.huashi.sms.signature.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.util.PatternUtil;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.signature.dao.SignatureExtNoMapper;
import com.huashi.sms.signature.domain.SignatureExtNo;

@Service
public class SignatureExtNoService implements ISignatureExtNoService{
	
	@Reference
	private IUserService userService;
	@Autowired
	private SignatureExtNoMapper signatureExtNoMapper;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public BossPaginationVo<SignatureExtNo> findPage(Map<String, Object> condition) {
		BossPaginationVo<SignatureExtNo> page = new BossPaginationVo<SignatureExtNo>();
		int count = signatureExtNoMapper.findCount(condition);
		page.setCurrentPage(Integer.parseInt(condition.getOrDefault("currentPage", 1).toString()));
		page.setTotalCount(count);
		condition.put("start", page.getStartPosition());
		condition.put("end", page.getPageSize());
		
		List<SignatureExtNo> list = signatureExtNoMapper.findList(condition);
		if(CollectionUtils.isEmpty(list)) {
            return null;
        }
		
		for (SignatureExtNo t : list) {
			if (t == null) {
                continue;
            }
			
			t.setUserModel(userService.getByUserId(t.getUserId()));
		}
		page.setList(list);
		return page;
	}

	@Override
	public boolean save(SignatureExtNo signatureExtNo) {
		if (StringUtils.isEmpty(signatureExtNo.getSignature()) 
				|| StringUtils.isEmpty(signatureExtNo.getExtNumber())) {
			logger.error("签名或扩展号码为空");
			return false;
		}
		
		int result = signatureExtNoMapper.insert(signatureExtNo);
		if(result <= 0) {
            return false;
        }

		pushToRedis(signatureExtNo);
		
		return true;
	}

	@Override
	public boolean update(SignatureExtNo signatureExtNo) {
		SignatureExtNo td = get(signatureExtNo.getId());
		if(td == null) {
			logger.error("签名数据为空, id:{}", signatureExtNo.getId());
			return false;
		}
		
		signatureExtNo.setCreateTime(new Date());
		int result = signatureExtNoMapper.updateByPrimaryKey(signatureExtNo);
		if(result > 0) {
            pushToRedis(signatureExtNo);
        }
		
		return result > 0;
	}

	@Override
	public boolean delete(Long id) {
		SignatureExtNo signatureExtNo = get(id);
		if (signatureExtNo == null) {
			logger.error("用户签名数据为空，删除失败， ID：{}", id);
			return false;
		}

		try {
			removeRedis(signatureExtNo.getUserId(), id);
		} catch (Exception e) {
			logger.error("移除REDIS用户签名失败， ID：{}", id, e);
		}

		return signatureExtNoMapper.deleteByPrimaryKey(id) > 0;
	}

	@Override
	public SignatureExtNo get(Long id) {
		return signatureExtNoMapper.selectByPrimaryKey(id);
	}

	@Override
	public boolean reloadToRedis() {
		List<SignatureExtNo> list = signatureExtNoMapper.findAll();
		if (CollectionUtils.isEmpty(list)) {
			logger.warn("用户签名数据为空");
			return false;
		}
		
		try {
			stringRedisTemplate.delete(stringRedisTemplate.keys(SmsRedisConstant.RED_USER_SIGNATURE_EXT_NO + "*"));
		} catch (Exception e) {
			logger.error("移除REDIS 签名扩展号码失败", e);
		}
		
		for (SignatureExtNo signatureExtNo : list) {
			pushToRedis(signatureExtNo);
		}
		
		return true;
	}

	private String getKey(Integer userId) {
		return String.format("%s:%d", SmsRedisConstant.RED_USER_SIGNATURE_EXT_NO, userId);
	}
	
	private void pushToRedis(SignatureExtNo signatureExtNo) {
		if(signatureExtNo == null) {
            return;
        }
		
		try {
			stringRedisTemplate.opsForHash().put(getKey(signatureExtNo.getUserId()), 
					signatureExtNo.getId().toString(), JSON.toJSONString(signatureExtNo , 
							new SimplePropertyPreFilter("id", "userId", "signature", "extNumber")));
		} catch (Exception e) {
			logger.error("签名扩展号加载到REDIS失败", e);
		}
	}
	
	/**
	 * 
	   * TODO 数据库查询用户签名扩展号码信息
	   * @param userId
	   * @param content
	   * @return
	 */
	private String getFromDb(Integer userId, String content) {
		List<SignatureExtNo> list = signatureExtNoMapper.selectByUserId(userId);
		if(CollectionUtils.isEmpty(list)) {
            return null;
        }
		
		for(SignatureExtNo signatureExtNo : list) {
			// 根据内容匹配用户的扩展号码
			String extNumber = getExtNumber(signatureExtNo, content);
			if(extNumber == null) {
                continue;
            }
			
			return extNumber;
		}
		
		return null;
	}

	/**
	 * 
	 * TODO 查询REDIS
	 * 
	 * @param userId
	 */
	private String getFromRedis(Integer userId, String content) {
		try {
			Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(getKey(userId));
			if(MapUtils.isEmpty(map)) {
                return null;
            }
			
			SignatureExtNo signatureExtNo = null;
			for(Object key : map.keySet()) {
				Object obj = map.get(key);
				if(obj == null) {
                    continue;
                }
				
				signatureExtNo = JSON.parseObject(obj.toString(), SignatureExtNo.class);
				
				// 根据内容匹配用户的扩展号码
				String extNumber = getExtNumber(signatureExtNo, content);
				if(extNumber == null) {
                    continue;
                }
				
				return extNumber;
			}
			
			return null;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	   * TODO 获取用户扩展号码
	   * @param signatureExtNo
	   * @param content
	   * @return
	 */
	private static String getExtNumber(SignatureExtNo signatureExtNo, String content) {
		if(signatureExtNo == null) {
            return null;
        }
		
		String signature = buildSignaturePattern(signatureExtNo.getSignature());
		if(StringUtils.isEmpty(signature)) {
            return null;
        }
		
		if(content.startsWith(signature) || content.endsWith(signature)) {
            return signatureExtNo.getExtNumber();
        }
		
		return null;
	}
	
	/**
	 * 
	   * TODO 签名组装
	   * 
	   * @param signature
	   * @return
	 */
	private static String buildSignaturePattern(String signature) {
		if(StringUtils.isEmpty(signature)) {
            return null;
        }
		
		return String.format("【%s】", signature);
	}

	private void removeRedis(Integer userId, Long id) {
		try {
			stringRedisTemplate.opsForHash().delete(getKey(userId), id.toString());
		} catch (Exception e) {
			logger.warn("REDIS 用户签名移除失败", e);
		}
	}

	@Override
	public String getExtNumber(Integer userId, String content) {
		if(StringUtils.isEmpty(content) || (!PatternUtil.isContainsSignature(content))
				|| userId == null) {
            return null;
        }
		
		try {
			return getFromRedis(userId, content);
		} catch (Exception e) {
			// 如果出错则由数据库补偿
			return getFromDb(userId, content);
		}
	}
}
