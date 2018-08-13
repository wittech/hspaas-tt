package com.huashi.sms.template.service;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.sms.template.domain.MessageTemplate;

/**
 * TODO 短信模板服务接口
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年2月21日 下午2:28:29
 */
public interface ISmsTemplateService {

    /**
     * TODO 请在此处添加注释
     * 
     * @param userId 用户ID
     * @param modeIds 模板ID，多个ID以逗号分隔
     * @param title 模板标题
     * @param type 类型 1:行业短信 2:营销短信
     * @param status 状态 1:审核中，2:审核通过，3:审核失败
     * @param pageNo 当前页码
     * @param pageSize 每页条数
     * @return
     */
    PaginationVo<MessageTemplate> findPage(int userId, String modeIds, String title, Integer type, Integer status,
                                           Integer pageNo, Integer pageSize);

    /**
     * TODO 添加模板
     * 
     * @param template
     * @return
     */
    long save(MessageTemplate template);

    /**
     * TODO 模板审核
     * 
     * @param id
     * @param approveStatus
     * @param operator
     * @return
     */
    boolean approve(long id, int approveStatus, String operator);

    /**
     * TODO 更新模板内容
     * 
     * @param template
     * @return
     */
    boolean update(MessageTemplate template);

    /**
     * TODO 删除模板
     * 
     * @param id
     * @return
     */
    boolean deleteById(long id);

    /**
     * TODO 根据模板ID查询模板
     * 
     * @param id
     * @return
     */
    MessageTemplate get(long id);

    /**
     * TODO 后台查询模板内容
     * 
     * @param pageNum
     * @param keyword
     * @param status
     * @param userId
     * @return
     */
    BossPaginationVo<MessageTemplate> findPageBoos(int pageNum, String keyword, String status, String userId);

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
    boolean saveToBatchContent(MessageTemplate template, String[] contents);

    /**
     * TODO 将模板数据加载到REDIS
     * 
     * @return
     */
    boolean reloadToRedis();
}
