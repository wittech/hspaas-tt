package com.huashi.bill.order.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.bill.order.dao.TradeOrderInvoiceMapper;
import com.huashi.bill.order.domain.TradeOrderInvoice;
import com.huashi.common.settings.domain.AddressBook;
import com.huashi.common.settings.service.IAddressBookService;
import com.huashi.common.user.domain.UserProfile;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.util.DateUtil;

@Service
public class TradeOrderInvoiceService implements ITradeOrderInvoiceService {

	@Reference
	private IUserService userService;
	@Reference
	private IAddressBookService addressBookService;
	@Autowired
	private TradeOrderInvoiceMapper tradeOrderInvoiceMapper;

	@Override
	public boolean save(long orderId, String title, String name, String mobile, String address) {
		TradeOrderInvoice invoice = new TradeOrderInvoice();
		invoice.setAddress(address);
		invoice.setTitle(title);
		invoice.setContactPhone(mobile);
		invoice.setOrderId(orderId);
		invoice.setContactName(name);
		invoice.setCreateTime(new Date());

		return tradeOrderInvoiceMapper.insertSelective(invoice) > 0;
	}

	@Override
	public TradeOrderInvoice getByOrderId(Long orderId) {
		if(orderId <=0){
			return null;
		}
		TradeOrderInvoice order = tradeOrderInvoiceMapper.selectByOrderId(orderId);
		if(order !=null){
			order.setCreateTimeText(DateUtil.getDayStr(order.getCreateTime()));
		}
		return order;
	}

	@Override
	public TradeOrderInvoice getLastest(int userId) {
		if(userId == 0)
			return null;
		
		TradeOrderInvoice invoice = tradeOrderInvoiceMapper.selectLastest(userId);
		if(invoice != null)
			return invoice;
		
		invoice = new TradeOrderInvoice();
		invoice.setTitle(getUserInvoiceTitle(userId));
		
		AddressBook contact = addressBookService.getDefaultAddress(userId);
		if(contact == null)
			return invoice;
		
		invoice.setContactName(contact.getName());
		invoice.setContactPhone(contact.getMobile());
		invoice.setAddress(contact.getAddress());
		
		return invoice;
	}
	
	/**
	 * 
	 * TODO 获取用户的发票抬头 [前期用用户基础信息公司名称，后期采用实名认证公司名称]
	 * 
	 * @param userId
	 * @return
	 */
	private String getUserInvoiceTitle(int userId) {
		UserProfile userProfile = userService.getProfileByUserId(userId);
		if (userProfile == null)
			return "";

		return userProfile.getCompany();
	}

}