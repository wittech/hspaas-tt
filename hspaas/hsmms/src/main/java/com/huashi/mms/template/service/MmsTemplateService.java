package com.huashi.mms.template.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.common.user.service.IUserMmsConfigService;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.util.IdGenerator;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.mms.template.constant.MmsTemplateContext.ApproveStatus;
import com.huashi.mms.template.dao.MmsMessageTemplateMapper;
import com.huashi.mms.template.domain.MmsMessageTemplate;
import com.huashi.mms.template.domain.MmsMessageTemplateBody;
import com.huashi.mms.template.exception.ModelApplyException;
import com.huashi.sms.passage.context.PassageContext.RouteType;

/**
 * 彩信模板服务实现
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月20日 下午10:06:27
 */
@Service
public class MmsTemplateService implements IMmsTemplateService {

    @Autowired
    private IdGenerator              idGenerator;
    @Reference
    private IUserService             userService;
    @Reference
    private IUserMmsConfigService    userMmsConfigService;
    @Autowired
    private MmsMediaFileService      mmsMediaFileService;
    @Autowired
    private MmsMessageTemplateMapper mmsMessageTemplateMapper;
    @Reference
    private IMmsTemplateBodyService  mmsTemplateBodyService;

    private final Logger             logger = LoggerFactory.getLogger(getClass());

    @Override
    public PaginationVo<MmsMessageTemplate> findPage(int userId, String status, String title, String currentPage) {
        int _currentPage = PaginationVo.parse(currentPage);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        if (StringUtils.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (StringUtils.isNotEmpty(title)) {
            params.put("title", title);
        }

        int totalRecord = mmsMessageTemplateMapper.getCountByUserId(params);
        if (totalRecord == 0) {
            return null;
        }

        params.put("startPage", PaginationVo.getStartPage(_currentPage));
        params.put("pageRecord", PaginationVo.DEFAULT_RECORD_PER_PAGE);

        List<MmsMessageTemplate> list = mmsMessageTemplateMapper.findPageListByUserId(params);
        if (list == null || list.isEmpty()) {
            return null;
        }

        return new PaginationVo<>(list, _currentPage, totalRecord);
    }

    @Override
    public boolean update(MmsMessageTemplate template) throws ModelApplyException {
        if (StringUtils.isEmpty(template.getName())) {
            throw new ModelApplyException("模板名称不能为空");
        }

        if (StringUtils.isEmpty(template.getTitle())) {
            throw new ModelApplyException("模板标题不能为空");
        }

        if (CollectionUtils.isEmpty(template.getBodies())) {
            throw new ModelApplyException("多媒体结构体数据为空");
        }

        MmsMessageTemplate originTemplate = null;
        try {
            originTemplate = isAllowAccess(template.getUserId(), template.getId());
        } catch (IllegalArgumentException e) {
            logger.error("模板数据鉴权失败 : {}", e.getMessage());
            return false;
        }

        // template.setRegexValue(parseContent2Regex(template.getContent()));
        template.setCreateTime(originTemplate.getCreateTime());

        try {
            template.setCreateTime(new Date());
            // 融合平台判断 后台添加 状态默认
            if (AppType.WEB.getCode() == template.getAppType()) {
                template.setStatus(ApproveStatus.WAITING.getValue());
            }

            // if (template.getStatus() == ApproveStatus.SUCCESS.getValue()) {
            // pushToRedis(template.getUserId(), template);
            // }

            boolean isOk = mmsTemplateBodyService.delete(template.getId());
            if (!isOk) {
                logger.error("模板数据[" + JSON.toJSONString(template) + "]删除模板内容失败");
                throw new ModelApplyException("数据操作异常");
            }

            isOk = mmsTemplateBodyService.batchSave(template.getBodies());
            if (!isOk) {
                logger.error("模板多媒体结构数据[" + JSON.toJSONString(template.getBodies()) + "]插入失败");
                throw new ModelApplyException();
            }

            isOk = mmsMessageTemplateMapper.updateByPrimaryKey(template) > 0;
            if (!isOk) {
                logger.error("模板数据[" + JSON.toJSONString(template) + "]更新失败");
                throw new ModelApplyException();
            }

            return isOk;

        } catch (Exception e) {
            logger.error("模板数据:{" + JSON.toJSONString(template) + "]修改失败", e);
            throw new ModelApplyException("模板修改失败");
        }
    }

    /**
     * 是否允许被访问（针对用户ID进行鉴权,防止恶意使用userID来篡改其他userId数据）
     * 
     * @param userId
     * @param templateId
     * @return
     */
    private MmsMessageTemplate isAllowAccess(int userId, long templateId) {
        MmsMessageTemplate template = get(templateId);
        if (template == null) {
            throw new IllegalArgumentException("模板 [" + templateId + "]信息为空");
        }

        // 仅针对WEB用户自己添加的模板进行过滤
        if (AppType.WEB.getCode() == template.getAppType() && template.getUserId() != userId) {
            throw new IllegalArgumentException("用户模板[" + templateId + "]数据不匹配，原模板用户ID:[" + template.getUserId()
                                               + "] , 本次用户ID:[" + userId + "]");
        }

        if (AppType.WEB.getCode() == template.getAppType() && template.getStatus() != ApproveStatus.WAITING.getValue()) {
            throw new IllegalArgumentException("用户模板[" + templateId + "]模板状态为非待审核状态[" + template.getStatus() + "]不能修改");
        }

        return template;
    }

    @Override
    public boolean deleteById(long id) {
        MmsMessageTemplate template = get(id);
        if (template == null) {
            logger.error("用户彩信模板为空，删除失败， ID：{}", id);
            return false;
        }

        return mmsMessageTemplateMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public MmsMessageTemplate get(long id) {
        return mmsMessageTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public BossPaginationVo<MmsMessageTemplate> findPageBoos(int pageNum, String keyword, String status, String userId) {
        BossPaginationVo<MmsMessageTemplate> page = new BossPaginationVo<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("keyword", keyword);
        paramMap.put("status", status);
        paramMap.put("userId", userId);
        int count = mmsMessageTemplateMapper.findCount(paramMap);
        page.setCurrentPage(pageNum);
        page.setTotalCount(count);
        paramMap.put("start", page.getStartPosition());
        paramMap.put("end", page.getPageSize());

        List<MmsMessageTemplate> list = mmsMessageTemplateMapper.findList(paramMap);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        for (MmsMessageTemplate t : list) {
            if (t == null) {
                continue;
            }

            t.setUserModel(userService.getByUserId(t.getUserId()));
            t.setApptypeText(AppType.parse(t.getAppType()).getName());
            t.setRouteTypeText(RouteType.parse(t.getRouteType()).getName());
        }
        page.setList(list);
        return page;
    }

    @Override
    public boolean approve(long id, int status, String remark) {
        MmsMessageTemplate template = get(id);
        template.setStatus(status);
        template.setRemark(remark);
        template.setApproveTime(new Date());

        return mmsMessageTemplateMapper.updateByPrimaryKeySelective(template) > 0;
    }

    @Override
    public boolean delete(long id, int userId) {
        try {
            isAllowAccess(userId, id);
        } catch (IllegalArgumentException e) {
            logger.error("模板数据鉴权失败 : {}", e.getMessage());
            return false;
        }

        return mmsMessageTemplateMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public MmsMessageTemplate getWithUserId(Long id, int userId) {
        try {
            MmsMessageTemplate template = isAllowAccess(userId, id);
            if (template == null) {
                return null;
            }

            template.setBodies(mmsTemplateBodyService.getBodiesByTemplateId(id));

            return template;
        } catch (Exception e) {
            logger.error("模板[" + id + "] 用户ID:[" + userId + "] 获取模板数据失败 ", e);
            return null;
        }
    }

    @Override
    public MmsMessageTemplate getByModelId(String modelId) {
        return mmsMessageTemplateMapper.selectByModelId(modelId);
    }

    @Override
    public boolean isModelIdAvaiable(String modelId, int userId) {
        MmsMessageTemplate template = getByModelId(modelId);
        if (template == null) {
            logger.error("模板ID[" + modelId + "]数据为空");
            return false;
        }

        if (userId == 0 || template.getUserId() != userId) {
            logger.error("模板ID[" + modelId + "] 用户[" + userId + "]数据权限不符");
            return false;
        }

        if (ApproveStatus.SUCCESS.getValue() != template.getStatus()) {
            logger.error("模板ID[" + modelId + "] 状态[" + template.getStatus() + "]下不可用");
            return false;
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = ModelApplyException.class)
    public String save(MmsMessageTemplate template) throws ModelApplyException {
        if (StringUtils.isEmpty(template.getName())) {
            throw new ModelApplyException("模板名称不能为空");
        }

        if (StringUtils.isEmpty(template.getTitle())) {
            throw new ModelApplyException("模板标题不能为空");
        }

        if (CollectionUtils.isEmpty(template.getBodies())) {
            throw new ModelApplyException("多媒体结构体数据为空");
        }

        try {
            template.setCreateTime(new Date());
            // 融合平台判断 后台添加 状态默认
            if (AppType.WEB.getCode() == template.getAppType()) {
                template.setStatus(ApproveStatus.WAITING.getValue());
            }

            // if (template.getStatus() == ApproveStatus.SUCCESS.getValue()) {
            // pushToRedis(template.getUserId(), template);
            // }

            template.setModelId(idGenerator.generateStr());
            boolean isOk = mmsMessageTemplateMapper.insertSelective(template) > 0;
            if (!isOk) {
                logger.error("模板数据[" + JSON.toJSONString(template) + "]插入失败");
                throw new ModelApplyException();
            }

            for (MmsMessageTemplateBody body : template.getBodies()) {
                body.setTemplateId(template.getId());
            }

            isOk = mmsTemplateBodyService.batchSave(template.getBodies());
            if (!isOk) {
                logger.error("模板多媒体结构数据[" + JSON.toJSONString(template.getBodies()) + "]插入失败");
                throw new ModelApplyException();
            }

            return template.getModelId();

        } catch (Exception e) {
            logger.error("模板数据:{" + JSON.toJSONString(template) + "]报备失败", e);
            throw new ModelApplyException("模板报备失败");
        }

    }
    
}
