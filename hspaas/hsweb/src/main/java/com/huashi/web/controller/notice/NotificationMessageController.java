
/**************************************************************************
* Copyright (c) 2015-2016 HangZhou Huashi, Inc.
* All rights reserved.
* 
* 项目名称：华时短信平台
* 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
*           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
*           识产权保护的内容。                            
***************************************************************************/
package com.huashi.web.controller.notice;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.apache.dubbo.config.annotation.Reference;
import com.huashi.common.notice.domain.NotificationMessage;
import com.huashi.common.notice.service.INotificationMessageService;
import com.huashi.web.controller.BaseController;

/**
 * TODO 消息管理
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月5日 下午12:14:38
 */
@Controller
@RequestMapping("/notice/message")
public class NotificationMessageController extends BaseController {

	@Reference
	private INotificationMessageService notificationMessageService;

	/**
	 * 消息首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Model model) {
		return "/notice/message/index";
	}

	/**
	 * 
	 * TODO 消息记录查询
	 * 
	 * @param request
	 * @param currentPage
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list")
	public String list(Model model,int status) {
		model.addAttribute("page",notificationMessageService.findUserPage(getCurrentUserId(), request.getParameter("currentPage"),status));
//		notificationMessageService.updateToRead(getCurrentUserId());
		return "/notice/message/list";
	}

	/**
	 * 
	   * TODO 获取未读私信信息
	   * @return
	 */
	@RequestMapping(value = "/unread", method = RequestMethod.POST)
	@ResponseBody
	public int getUnReadList() {
		List<NotificationMessage> list = notificationMessageService.findUnReadList(getCurrentUserId());
		if (CollectionUtils.isEmpty(list)) {
            {
                return 0;
            }
        }
		return list.size();
	}
	
	/**
	 * 已读
	 * @return
	 */
	@RequestMapping(value = "/read", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateRead(NotificationMessage message){
		if(message !=null){
			if(message.getId()>0){
				NotificationMessage messages = notificationMessageService.findById(message.getId());
				if(messages !=null){
					if(messages.getStatus()==0){
						message.setStatus(1);
						return notificationMessageService.update(message);
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 删除
	 * @param message
	 * @return
	 */
	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	@ResponseBody
	public boolean delete(NotificationMessage message){
		if(message !=null){
			if(message.getId()>0){
				NotificationMessage messages = notificationMessageService.findById(message.getId());
				if(messages !=null){
					return notificationMessageService.delete(message.getId());
				}
			}
		}
		return false;
	}

}
