package com.huashi.mms.template.service;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.mms.template.domain.MmsMessageTemplate;
import com.huashi.mms.template.exception.ModelApplyException;

/**
 * 彩信模板服务接口
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
     * @param title 模板标题
     * @param currentPage 当前页码
     * @return
     */
    PaginationVo<MmsMessageTemplate> findPage(int userId, String status, String title, String currentPage);

    /**
     * 添加模板
     * 
     * @param template
     * @return
     */
    String save(MmsMessageTemplate template) throws ModelApplyException;

    /**
     * 模板审核
     * 
     * @param id
     * @param status
     * @param remark
     * @return
     */
    boolean approve(long id, int status, String remark);

    /**
     * 更新模板内容
     * 
     * @param template
     * @return
     */
    boolean update(MmsMessageTemplate template) throws ModelApplyException;

    /**
     * 删除模板
     * 
     * @param id
     * @return
     */
    boolean deleteById(long id);

    /**
     * 删除模板（判断模板是否属于该用户）
     * 
     * @param id
     * @param userId
     * @return
     */
    boolean delete(long id, int userId);

    /**
     * 根据模板ID查询模板
     * 
     * @param id
     * @return
     */
    MmsMessageTemplate get(long id);

    /**
     * 后台查询模板内容
     * 
     * @param pageNum
     * @param keyword
     * @param status
     * @param userId
     * @return
     */
    BossPaginationVo<MmsMessageTemplate> findPageBoos(int pageNum, String keyword, String status, String userId);

    /**
     * 根据用户ID判断并获取彩信模版
     * 
     * @param id
     * @param userId
     * @return
     */
    MmsMessageTemplate getWithUserId(Long id, int userId);

    /**
     * 根据对外模板ID查询模板详情
     * 
     * @param modelId
     * @return
     */
    MmsMessageTemplate getByModelId(String modelId);

    /**
     * 模板是否可用
     * 
     * @param modelId
     * @param userId
     * @return
     */
    boolean isModelIdAvaiable(String modelId, int userId);
    
}
