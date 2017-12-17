
/**************************************************************************
* Copyright (c) 2015-2016 HangZhou Huashi, Inc.
* All rights reserved.
* 
* 项目名称：华时短信平台
* 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
*           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
*           识产权保护的内容。                            
***************************************************************************/
package com.huashi.common.service.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.service.dao.OpinionFeedBackMapper;
import com.huashi.common.service.domain.OpinionFeedBack;

/**
 * TODO 意见反馈服务接口实现类
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月5日 下午12:22:01
 */
@Service
public class OpinionFeedBackService implements IOpinionFeedBackService {

	@Autowired
	private OpinionFeedBackMapper opinionFeedBackMapper;

	@Override
	public boolean insert(OpinionFeedBack opinionFeedBack) {
		try {
			if (opinionFeedBack.getTitle().isEmpty() || opinionFeedBack.getContent().isEmpty()) {
                {
                    return false;
                }
            }
			opinionFeedBack.setCreateTime(new Date());
			return opinionFeedBackMapper.insert(opinionFeedBack) > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
