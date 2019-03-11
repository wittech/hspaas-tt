package com.huashi.mms.template.service;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.mms.template.domain.MmsMessageTemplate;

/**
 * TODO 彩信模板服务接口
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月15日 上午11:20:05
 */
public interface IMmsTemplateService {

    /**
     * 获取用户模板列表数据(分页)
     * 
     * @param userId 用户编号
     * @param status 审批状态
     * @param content 模板内容
     * @param currentPage 当前页码
     * @return
     */
    PaginationVo<MmsMessageTemplate> findPage(int userId, String status, String content, String currentPage);

    /**
     * TODO 添加模板
     * 
     * @param template
     * @return
     */
    boolean save(MmsMessageTemplate template);

    /**
     * TODO 模板审核
     * 
     * @param id
     * @param status
     * @param remark
     * @return
     */
    boolean approve(long id, int status, String remark);

    /**
     * TODO 更新模板内容
     * 
     * @param template
     * @return
     */
    boolean update(MmsMessageTemplate template);

    /**
     * TODO 删除模板
     * 
     * @param id
     * @return
     */
    boolean deleteById(long id);

    /**
     * TODO 删除模板（判断模板是否属于该用户）
     * 
     * @param id
     * @param userId
     * @return
     */
    boolean delete(long id, int userId);

    /**
     * TODO 根据模板ID查询模板
     * 
     * @param id
     * @return
     */
    MmsMessageTemplate get(long id);

    /**
     * TODO 后台查询模板内容
     * 
     * @param pageNum
     * @param keyword
     * @param status
     * @param userId
     * @return
     */
    BossPaginationVo<MmsMessageTemplate> findPageBoos(int pageNum, String keyword, String status, String userId);

    /**
     * TODO 根据用户ID和短信内容查询模板信息（优先级排序）
     * 
     * @param userId
     * @param content
     * @return
     */
    MmsMessageTemplate getByContent(int userId, String content);

    /**
     * TODO 判断输入内容是否符合模板内容
     * 
     * @param id
     * @param content
     * @return
     */
    boolean isContentMatched(long id, String content);

    /**
     * TODO 批量添加短信模板（针对其他配置项都一样，只有模板内容为多个的情况）
     * 
     * @param template
     * @param contents
     * @return
     */
    boolean saveToBatchContent(MmsMessageTemplate template, String[] contents);

    /**
     * TODO 将模板数据加载到REDIS
     * 
     * @return
     */
    boolean reloadToRedis();
}