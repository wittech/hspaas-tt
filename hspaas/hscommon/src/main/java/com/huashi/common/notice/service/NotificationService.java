package com.huashi.common.notice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.apache.dubbo.config.annotation.Service;
import com.huashi.common.notice.dao.NotificationMapper;
import com.huashi.common.notice.domain.Notification;
import com.huashi.common.vo.BossPaginationVo;

@Service
public class NotificationService implements INotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public List<Notification> findTopList() {
        return notificationMapper.selectByCount(6);
    }

    @Override
    public boolean create(Notification record) {
        try {
            notificationMapper.insert(record);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Notification record) {
        try {
            notificationMapper.updateByPrimaryKeySelective(record);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        try {
            notificationMapper.deleteByPrimaryKey(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Notification findById(int id) {
        return notificationMapper.selectByPrimaryKey(id);
    }

    @Override
    public BossPaginationVo<Notification> findPage(int pageNum) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        BossPaginationVo<Notification> page = new BossPaginationVo<Notification>();
        page.setCurrentPage(pageNum);
        int total = notificationMapper.findCount(paramMap);
        if (total <= 0) {
            return page;
        }
        page.setTotalCount(total);
        paramMap.put("start", page.getStartPosition());
        paramMap.put("end", page.getPageSize());
        List<Notification> dataList = notificationMapper.findList(paramMap);
        page.getList().addAll(dataList);
        return page;
    }

}
