
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

import com.huashi.common.service.domain.OpinionFeedBack;

/**
 * TODO 意见反馈接口服务类
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月5日 下午12:20:16
 */

public interface IOpinionFeedBackService {
	/**
	 * 
	 * TODO 保存
	 * 
	 * @param opinionFeedBack
	 * @return
	 */
	boolean insert(OpinionFeedBack opinionFeedBack);
}
