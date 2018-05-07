package com.huashi.sms.settings.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.settings.context.SettingsContext;
import com.huashi.common.settings.context.SettingsContext.SystemConfigType;
import com.huashi.common.settings.context.SettingsContext.WordsLibrary;
import com.huashi.common.settings.domain.SystemConfig;
import com.huashi.common.settings.service.ISystemConfigService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.settings.constant.SmsSettingsContext.ForbiddenWordsSwitch;
import com.huashi.sms.settings.dao.ForbiddenWordsMapper;
import com.huashi.sms.settings.domain.ForbiddenWords;
import com.huashi.sms.settings.filter.SensitiveWordFilter;

@Service
public class ForbiddenWordsService implements IForbiddenWordsService {

	@Autowired
	private ForbiddenWordsMapper forbiddenWordsMapper;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Reference
	private ISystemConfigService systemConfigService;

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean isContainsForbiddenWords(String content) {
		if (StringUtils.isEmpty(content)) {
            return false;
        }

		try {
			return SensitiveWordFilter.isContains(content);
		} catch (Exception e) {
			logger.error("解析是否包含敏感词失败", e);
			return false;
		}

	}

	@Override
	public boolean isContainsForbiddenWords(String content,
			Set<String> safeWords) {
		Set<String> words = filterForbiddenWords(content);
		if (CollectionUtils.isEmpty(words)) {
            return false;
        }

		if (CollectionUtils.isEmpty(safeWords)) {
            return true;
        }

		// 如果报备的敏感词包含本次检索出来的敏感词，则认为本次无敏感词
		return !safeWords.containsAll(words);
	}

	@Override
	public Set<String> filterForbiddenWords(String content) {
		if (StringUtils.isEmpty(content)) {
            return null;
        }

		// 【全棉时代天猫旗舰店】520亲子节，卫生巾化妆棉爆款直降50%OFF！￥品质棉，新生爱￥（复制整段再打开手机淘宝抢先看）退订回N
		// 过滤内容敏感词
		Set<String> sensitiveWords = SensitiveWordFilter.doFilter(content);

		return CollectionUtils.isEmpty(sensitiveWords) ? null : sensitiveWords;

	}

	@Override
	public Set<String> filterForbiddenWords(String content,
			Set<String> safeWords) {
		Set<String> set = filterForbiddenWords(content);
		if (CollectionUtils.isEmpty(set)) {
			return null;
		}

		set.removeAll(safeWords);

		return set;
	}

	@Override
	public List<String> findForbiddenWordsLibrary() {
		try {
			Set<String> set = stringRedisTemplate.opsForSet().members(
					SmsRedisConstant.RED_FORBIDDEN_WORDS);

			if (CollectionUtils.isNotEmpty(set)) {
                return new ArrayList<>(set);
            }

		} catch (Exception e) {
			logger.warn("Redis敏感词加载失败.", e);
		}

		List<String> list = forbiddenWordsMapper.selectAllWords();
		try {
			stringRedisTemplate.opsForSet().add(
					SmsRedisConstant.RED_FORBIDDEN_WORDS,
					list.toArray(new String[] {}));
		} catch (Exception e) {
			logger.warn("Redis敏感词同步失败.", e);
		}

		return list;
	}

	@Override
	public boolean saveForbiddenWords(ForbiddenWords word) {
		if(word == null || StringUtils.isBlank(word.getWord()) 
				|| StringUtils.isBlank(word.getLabel())) {
            return false;
        }
		
		try {
			stringRedisTemplate.opsForSet().add(
					SmsRedisConstant.RED_FORBIDDEN_WORDS, word.getWord());
		} catch (Exception e) {
			logger.warn("Redis敏感词同步失败.", e);
		}

		// 暂时均默认为1级
		word.setLevel(1);
		word.setCreateTime(new Date());
		int result = forbiddenWordsMapper.insertSelective(word);
		if (result <= 0) {
            return false;
        }

		return freshForbiddenWords();
	}

	/**
	 * 
	 * TODO 刷新内部缓存库（敏感词目前加载在JVM本地内存，全局MAP中）
	 * 
	 * @return
	 */
	private boolean freshForbiddenWords() {
		try {
			// 初始化WORDS
			SensitiveWordFilter.setSensitiveWord(new ArrayList<>(findForbiddenWordsLibrary()));
			return true;
		} catch (Exception e) {
			logger.warn("REDIS刷新敏感词数据失败", e);
			return false;
		}

	}

	@Override
	public boolean reloadRedisForbiddenWords() {
		List<String> words = forbiddenWordsMapper.selectAllWords();
		if (CollectionUtils.isEmpty(words)) {
			logger.info("数据库未检索到敏感词，放弃填充REDIS");
			return true;
		}
		try {
			// 初始化WORDS
			SensitiveWordFilter.setSensitiveWord(words);

			stringRedisTemplate.delete(SmsRedisConstant.RED_FORBIDDEN_WORDS);
			stringRedisTemplate.opsForSet().add(
					SmsRedisConstant.RED_FORBIDDEN_WORDS,
					words.toArray(new String[] {}));
			return true;
		} catch (Exception e) {
			logger.warn("REDIS重载敏感词数据失败", e);
			return false;
		}

	}

	@Override
	public BossPaginationVo<ForbiddenWords> findPage(int pageNum, String keyword) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("keyword", keyword);
		BossPaginationVo<ForbiddenWords> page = new BossPaginationVo<ForbiddenWords>();
		page.setCurrentPage(pageNum);
		int total = forbiddenWordsMapper.findCount(paramMap);
		if (total <= 0) {
			return page;
		}
		page.setTotalCount(total);
		paramMap.put("start", page.getStartPosition());
		paramMap.put("end", page.getPageSize());
		List<ForbiddenWords> dataList = forbiddenWordsMapper.findList(paramMap);
		page.getList().addAll(dataList);
		return page;
	}

	@Override
	public boolean deleteWord(int id) {
		try {
			ForbiddenWords words = forbiddenWordsMapper.selectByPrimaryKey(id);
			stringRedisTemplate.opsForSet().remove(
					SmsRedisConstant.RED_FORBIDDEN_WORDS, words.getWord());

			freshForbiddenWords();

		} catch (Exception e) {
			logger.warn("Redis 删除敏感词信息失败, id : {}", id, e);
			return false;
		}
		return forbiddenWordsMapper.deleteByPrimaryKey(id) > 0;
	}

	@Override
	public boolean isForbiddenWordsAllowPassed() {
		try {
			String forbiddenWordsSwitch = stringRedisTemplate.opsForValue()
					.get(SmsRedisConstant.RED_FORBIDDEN_WORDS_SWITCH);
			if (StringUtils.isEmpty(forbiddenWordsSwitch)) {
                return false;
            }

			return ForbiddenWordsSwitch.isOpen(forbiddenWordsSwitch);

		} catch (Exception e) {
			logger.warn("Redis敏感词开关查询失败，将采用默认开启..", e);
			return false;
		}

	}

	@Override
	public String[] findWordsLabelLibrary() {
		SystemConfig systemConfig = systemConfigService.findByTypeAndKey(
				SystemConfigType.WORDS_LIBRARY.name(),
				WordsLibrary.FORBIDDEN_LABEL.name());
		if (systemConfig == null
				|| StringUtils.isEmpty(systemConfig.getAttrValue())) {
			logger.warn("敏感词标签库未配置，请及时配置");
			return null;
		}

		return systemConfig.getAttrValue().split(
				SettingsContext.MULTI_VALUE_SEPERATOR);
	}

	@Override
	public List<ForbiddenWords> getLabelByWords(String words) {
		if (StringUtils.isEmpty(words)) {
            return null;
        }

		String[] wordsArray = words.split(",");
		ForbiddenWords forbiddenWords = null;

		List<ForbiddenWords> list = new ArrayList<>();
		if (wordsArray.length == 1) {
			// 如果只有一个词汇，并且为空则直接返回空
			if (StringUtils.isBlank(wordsArray[0])) {
                return null;
            }

			forbiddenWords = forbiddenWordsMapper.selectByWord(wordsArray[0]);
			if (forbiddenWords == null) {
                return null;
            }

			list.add(forbiddenWords);
			return list;
		}

		List<ForbiddenWords> wordLib = forbiddenWordsMapper.selectByMultiWord(wordsArray);
		if(CollectionUtils.isEmpty(wordLib)) {
            return null;
        }
		
		Map<String, ForbiddenWords> map = new HashMap<>();
		// 如果存在多个标签，需要判断是否是同一个，如果为同一个标签则只返回一个即可
		for(ForbiddenWords word : wordLib) {
			if(!map.containsKey(word.getLabel())) {
				map.put(word.getLabel(), word);
				continue;
			}
			
			ForbiddenWords originWord = map.get(word.getLabel());
			originWord.setWord(originWord.getWord() + "," + word.getWord());
			
			map.put(word.getLabel(), originWord);
		}
		
		for(String label : map.keySet()) {
			list.add(map.get(label));
		}
		
		return list;
	}

	@Override
	public ForbiddenWords get(int id) {
		return forbiddenWordsMapper.selectByPrimaryKey(id);
	}

	@Override
	public boolean update(ForbiddenWords words) {
		// 暂时均默认为1级
		words.setLevel(1);
		words.setLabel(words.getLabel());
		
		return forbiddenWordsMapper.updateByPrimaryKeySelective(words) > 0;
	}
}
