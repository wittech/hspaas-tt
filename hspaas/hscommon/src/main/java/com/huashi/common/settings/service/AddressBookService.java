
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.apache.dubbo.config.annotation.Service;
import com.huashi.common.settings.dao.AddressBookMapper;
import com.huashi.common.settings.domain.AddressBook;
import com.huashi.common.vo.PaginationVo;

/**
 * TODO 请在此处添加注释
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月7日 下午11:08:32
 */
@Service
public class AddressBookService implements IAddressBookService {

	@Autowired
	private AddressBookMapper addressBookMapper;

	@Override
	public boolean save(AddressBook addressBook) {
		addressBook.setCreateTime(new Date());
		int id = addressBookMapper.insert(addressBook);
		if (id > 0) {
			if (addressBook.getIsDefault() == 1) {
				return addressBookMapper.updateStatus(id) > 0;
			}
			return true;
		}
		return false;
	}

	@Override
	public int delete(int id) {
		return addressBookMapper.deleteByPrimaryKey(id);
	}

	@Override
	public boolean update(AddressBook addressBook) {
		addressBook.setModifyTime(new Date());
		if (addressBook.getIsDefault() == 1) {
			int count = addressBookMapper.updateByPrimaryKeySelective(addressBook);
			if (count > 0) {
				addressBookMapper.updateStatus(Integer.parseInt(String.valueOf(addressBook.getId())));
				return count > 0;
			}
		} else {
			return addressBookMapper.updateByPrimaryKeySelective(addressBook) > 0;
		}
		return false;
	}

	@Override
	public List<AddressBook> findList(int userId) {
		List<AddressBook> list = addressBookMapper.findListByUserId(userId);
		if (list == null || list.isEmpty()) {
            {
                return null;
            }
        }
		return list;
	}

	@Override
	public PaginationVo<AddressBook> findPage(int userId, String mobile, String startDate, String endDate,
			String currentPage) {
		if (userId<=0) {
            {
                return null;
            }
        }

		int _currentPage = PaginationVo.parse(currentPage);

		Map<String, Object> params =  new HashMap<String,Object>();
		params.put("userId", userId);
		int totalRecord = addressBookMapper.getCountByUserId(params);
		if (totalRecord == 0) {
            {
                return null;
            }
        }
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		params.put("mobile", mobile);

		params.put("startPage", PaginationVo.getStartPage(_currentPage));
		params.put("pageRecord", PaginationVo.DEFAULT_RECORD_PER_PAGE);

		List<AddressBook> list = addressBookMapper.findPageListByUserId(params);
		if (list == null || list.isEmpty()) {
            {
                return null;
            }
        }
		return new PaginationVo<AddressBook>(list, _currentPage, totalRecord);
	}

	@Override
	public AddressBook getById(int id) {
		return addressBookMapper.selectByPrimaryKey(id);
	}

	@Override
	public AddressBook getDefaultAddress(int userId) {
		return addressBookMapper.selectByDefaultFlag(userId);
	}

}
