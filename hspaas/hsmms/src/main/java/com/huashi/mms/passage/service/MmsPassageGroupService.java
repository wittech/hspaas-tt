package com.huashi.mms.passage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import org.apache.dubbo.config.annotation.Service;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.mms.passage.dao.MmsPassageGroupDetailMapper;
import com.huashi.mms.passage.dao.MmsPassageGroupMapper;
import com.huashi.mms.passage.domain.MmsPassageGroup;
import com.huashi.mms.passage.domain.MmsPassageGroupDetail;

/**
 * TODO 彩信通道组服务
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月20日 下午11:44:32
 */
@Service
public class MmsPassageGroupService implements IMmsPassageGroupService {

    @Autowired
    private MmsPassageGroupMapper       mapper;
    @Autowired
    private MmsPassageGroupDetailMapper detailMapper;

    private final Logger                logger = LoggerFactory.getLogger(getClass());

    @Override
    @Transactional(readOnly = false)
    public boolean create(MmsPassageGroup group) {
        try {
            mapper.insert(group);
            int priority = 1;
            for (MmsPassageGroupDetail passage : group.getDetailList()) {
                passage.setPriority(priority);
                passage.setGroupId(group.getId());
                detailMapper.insert(passage);
                priority++;
            }
            return true;
        } catch (Exception e) {
            logger.error("添加通道组失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return false;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean update(MmsPassageGroup group) {
        try {
            mapper.updateByPrimaryKeySelective(group);
            int priority = 1;
            detailMapper.deleteByGroupId(group.getId().intValue());
            for (MmsPassageGroupDetail passage : group.getDetailList()) {
                passage.setPriority(priority);
                passage.setGroupId(group.getId());
                detailMapper.insert(passage);
                priority++;
            }
            return true;
        } catch (Exception e) {
            logger.error("修改通道组失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return false;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean deleteById(int id) {
        try {
            mapper.deleteByPrimaryKey(id);
            detailMapper.deleteByGroupId(id);
        } catch (Exception e) {
            logger.error("删除通道组失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return false;
    }

    @Override
    public BossPaginationVo<MmsPassageGroup> findPage(int pageNum, String keyword) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("keyword", keyword);
        BossPaginationVo<MmsPassageGroup> page = new BossPaginationVo<>();
        page.setCurrentPage(pageNum);
        int total = mapper.findCount(paramMap);
        if (total <= 0) {
            return page;
        }
        page.setTotalCount(total);
        paramMap.put("start", page.getStartPosition());
        paramMap.put("end", page.getPageSize());
        List<MmsPassageGroup> dataList = mapper.findList(paramMap);
        page.getList().addAll(dataList);
        return page;
    }

    @Override
    public MmsPassageGroup findById(int id) {
        MmsPassageGroup group = mapper.selectByPrimaryKey(id);
        List<MmsPassageGroupDetail> passageList = detailMapper.findPassageByGroupId(id);
        group.getDetailList().addAll(passageList);
        return group;
    }

    @Override
    public List<MmsPassageGroup> findAll() {
        return mapper.findAll();
    }

    @Override
    public List<MmsPassageGroupDetail> findPassageByGroupId(int groupId) {
        return detailMapper.findPassageByGroupId(groupId);
    }

    @Override
    public boolean doChangeGroupPassage(int groupId, int passageId) {
        try {
            return detailMapper.updateGroupPassageId(groupId, passageId) > 0;
        } catch (Exception e) {
            logger.error("切换通道组中通道出错", e);
            return false;
        }

    }

}
