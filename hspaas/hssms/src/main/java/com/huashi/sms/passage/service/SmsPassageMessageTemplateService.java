package com.huashi.sms.passage.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.common.util.LogUtils;
import com.huashi.common.util.PatternUtil;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.passage.context.PassageContext.PassageMessageTemplateStatus;
import com.huashi.sms.passage.dao.SmsPassageMessageTemplateMapper;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.domain.SmsPassageMessageTemplate;

/**
 * 
 * TODO 通道短息内容模板服务实现
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年8月30日 下午1:54:31
 */
@Service
public class SmsPassageMessageTemplateService implements ISmsPassageMessageTemplateService {

	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private SmsPassageMessageTemplateMapper smsPassageMessageTemplateMapper;
	@Autowired
	private ISmsPassageService smsPassageService;
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean save(SmsPassageMessageTemplate passageMessageTemplate) {
		try {
			passageMessageTemplate.setStatus(PassageMessageTemplateStatus.AVAIABLE.getValue());
			passageMessageTemplate.setRegexValue(parseContent2Regex(passageMessageTemplate.getTemplateContent()));
			passageMessageTemplate.setParamNames(parseVariableName(passageMessageTemplate.getTemplateContent()));
			
			passageMessageTemplate.setCreateTime(new Date());
			
			int result = smsPassageMessageTemplateMapper.insert(passageMessageTemplate);
			if(result == 0)
				return false;
			
			pushToRedis(passageMessageTemplate);
			
			return true;
		} catch (Exception e) {
			logger.error("添加短信通道内容模板失败", e);
			return false;
		}
	}

	@Override
	public boolean update(SmsPassageMessageTemplate passageMessageTemplate) {
		try {
			passageMessageTemplate.setRegexValue(parseContent2Regex(passageMessageTemplate.getTemplateContent()));
			passageMessageTemplate.setParamNames(parseVariableName(passageMessageTemplate.getTemplateContent()));
			passageMessageTemplate.setStatus(PassageMessageTemplateStatus.AVAIABLE.getValue());
			int result = smsPassageMessageTemplateMapper.updateByPrimaryKeySelective(passageMessageTemplate);
			if(result == 0)
				return false;
			
			pushToRedis(passageMessageTemplate);
			
			return true;
		} catch (Exception e) {
			logger.error("修改短信通道内容模板失败", e);
			return false;
		}
	}
	
	/**
	 * 
	   * TODO 根据表达式提取内容中变量对应的值
	   * 
	   * @param content
	   * 	短信内容
	   * @param regex
	   * 	表达式
	   *  @param paramSize
	   *  	参数数量
	   * 
	   * @return
	 */
	public static String[] pickupValuesByRegex(String content, String regex, int paramSize) {
		if(StringUtils.isEmpty(content))
			return null;
		
        try {
        	Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(content);
            List<String> values = new ArrayList<>();
        	while(m.find()) {
        		// 拼接所有匹配的参数数据
        		for(int i=0; i< paramSize; i++) {
        			values.add(m.group(i+1));
        		}
            }
        	
        	return values.toArray(new String[]{});
		} catch (Exception e) {
			LogUtils.error("根据表达式查询短信内容参数异常", e);
			return null;
		}
	}
	
	/**
	 * 
	   * TODO 获取内容中的变量信息
	   * 
	   * @param content
	   * @return
	 */
	private static String parseVariableName(String content) {
		String regex = "#([a-zA-Z0-9]+)#";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        StringBuilder variableNames = new StringBuilder();
        while(m.find()) {
        	if(StringUtils.isEmpty(m.group()))
        		continue;
        	
            variableNames.append(m.group().replaceAll("#", "")).append(",");
        }
        
        if(variableNames.length() == 0)
        	return null;
        
        return variableNames.substring(0, variableNames.length() - 1);
	}
	
	/**
	 * 
	 * TODO 内容转换正则表达式
	 * 
	 * @param content
	 * @return
	 */
	private static String parseContent2Regex(String content) {
		// modify 变量内容 增加不可见字符
		content = content.replaceAll("#[a-z]*[0-9]*[A-Z]*#", "(.*)");
		// 去掉末尾可以增加空格等不可见字符，以免提供商模板不通过
		// return prefix+oriStr+"\\s*$";
		return String.format("^%s$", content);
	}

	@Override
	public boolean delete(Long id) {
		try {
			get(id);
			return smsPassageMessageTemplateMapper.deleteByPrimaryKey(id) > 0;
		} catch (Exception e) {
			logger.error("删除通道短信模板失败", e);
			return false;
		}
	}

	@Override
	public BossPaginationVo<SmsPassageMessageTemplate> findPage(Map<String, Object> params) {
		BossPaginationVo<SmsPassageMessageTemplate> page = new BossPaginationVo<SmsPassageMessageTemplate>();
		page.setCurrentPage(Integer.parseInt(params.getOrDefault("currentPage", 1).toString()));
		int total = smsPassageMessageTemplateMapper.selectCount(params);
		if (total <= 0)
			return page;
		
		page.setTotalCount(total);
		params.put("start", page.getStartPosition());
		params.put("end", page.getPageSize());
		List<SmsPassageMessageTemplate> dataList = smsPassageMessageTemplateMapper.selectList(params);
		if (CollectionUtils.isEmpty(dataList))
			return page;
		
		Map<Integer, String> passageMap = new HashMap<Integer, String>();
		for(SmsPassageMessageTemplate template : dataList) {
			if(passageMap.containsKey(template.getPassageId())) {
				template.setPassageName(passageMap.get(template.getPassageId()));
				continue;
			}
			
			SmsPassage smsPassage = smsPassageService.findById(template.getPassageId());
			if(smsPassage == null)
				continue;
			
			template.setPassageName(smsPassage.getName());
			passageMap.put(template.getPassageId(), smsPassage.getName());
		}

		page.getList().addAll(dataList);

		return page;
	}

	@Override
	public SmsPassageMessageTemplate get(Long id) {
		return smsPassageMessageTemplateMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<SmsPassageMessageTemplate> findByPassageId(int passageId) {
		return smsPassageMessageTemplateMapper.selectByPassageId(passageId);
	}

	@Override
	public SmsPassageMessageTemplate getByTemplateId(String templateId) {
		return smsPassageMessageTemplateMapper.selectByPassageTemplateId(templateId);
	}

	@Override
	public boolean disabledOrActive(Long id, int status) {
		return false;
	}
	
	/**
	 * 
	   * TODO 获取REDIS KEY数据
	   * 
	   * @param passageId
	   * @return
	 */
	private static String getRedisKey(Integer passageId) {
		return String.format("%s:%d", SmsRedisConstant.RED_USER_PASSAGE_MESSAGE_TEMPLATE, passageId);
	}
	
	private void pushToRedis(SmsPassageMessageTemplate template) {
		try {
			stringRedisTemplate.opsForHash().put(getRedisKey(template.getPassageId()), 
					template.getTemplateId(), JSON.toJSONString(template));
		} catch (Exception e) {
			logger.error("用户通道模板信息加载REDIS失败", e);
		}
	}

	@Override
	public boolean reloadToRedis() {
		List<SmsPassageMessageTemplate> list = smsPassageMessageTemplateMapper.selectAvaiable();
		if (CollectionUtils.isEmpty(list))
			return false;
		
		stringRedisTemplate.delete(stringRedisTemplate.keys(SmsRedisConstant.RED_USER_PASSAGE_MESSAGE_TEMPLATE + "*"));
		
		for (SmsPassageMessageTemplate template : list) {
			pushToRedis(template);
		}
		
		return true;
	}
	
	

	@Override
	public SmsPassageMessageTemplate getByMessageContent(Integer passageId, String messageContent) {
		try {
			List<SmsPassageMessageTemplate> list = findByPassageId(passageId);
			
			if (CollectionUtils.isNotEmpty(list)) {
				for (SmsPassageMessageTemplate template : list) {
					if (template == null) {
						logger.warn("LOOP当前值为空");
						continue;
					}

					if (StringUtils.isEmpty(template.getRegexValue()))
						continue;

					// 如果普通短信模板存在，则以普通模板为主
					if (PatternUtil.isRight(template.getRegexValue(), messageContent))
						return template;
					
				}
			}

		} catch (Exception e) {
			logger.error("通道短信模板查询失败", e);
		}
		return null;
	}

	@Override
	public boolean isContentMatched(Long id, String content) {
		SmsPassageMessageTemplate smsPassageMessageTemplate = get(id);
		if(smsPassageMessageTemplate == null)
			return false;
		
		return PatternUtil.isRight(smsPassageMessageTemplate.getRegexValue(), content);
	}

}
