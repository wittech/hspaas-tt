
/**************************************************************************
* Copyright (c) 2015-2016 HangZhou Huashi, Inc.
* All rights reserved.
* 
* 项目名称：华时短信平台
* 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
*           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
*           识产权保护的内容。                            
***************************************************************************/
package com.huashi.common.settings.service;

import java.util.List;

import com.huashi.common.settings.domain.AddressBook;
import com.huashi.common.vo.PaginationVo;

/**
 * TODO 地址管理
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月7日 下午11:02:10
 */

public interface IAddressBookService {

	/**
	 * 
	 * TODO 根据id获取对象信息
	 * 
	 * @param id
	 * @return
	 */
	AddressBook getById(int id);

	/**
	 * 
	 * TODO 保存
	 * 
	 * @param addressBook
	 * @return
	 */
	boolean save(AddressBook addressBook);

	/**
	 * 
	 * TODO 删除
	 * 
	 * @param addressBook
	 * @return
	 */
	int delete(int id);

	/**
	 * 
	 * TODO 修改
	 * 
	 * @param balanceRemind
	 * @return
	 */
	boolean update(AddressBook addressBook);

	/**
	 * 
	 * TODO 查询全部地址（使用地方 开发票）
	 * 
	 * @param params
	 * @return
	 */
	List<AddressBook> findList(int userId);

	/**
	 * 
	 * TODO 分页
	 * 
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @param currentPage
	 * @return
	 */
	PaginationVo<AddressBook> findPage(int userId, String mobile, String startDate, String endDate, String currentPage);

	/**
	 * 
	 * TODO 获取用户默认地址
	 * 
	 * @param userId
	 * @return
	 */
	AddressBook getDefaultAddress(int userId);
}
