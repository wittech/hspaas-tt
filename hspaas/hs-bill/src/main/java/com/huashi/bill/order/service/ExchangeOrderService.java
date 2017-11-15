package com.huashi.bill.order.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.bill.order.constant.ExchangeOrderContext.ExchangeStatus;
import com.huashi.bill.order.constant.ExchangeOrderContext.ExchangeType;
import com.huashi.bill.order.dao.ExchangeOrderMapper;
import com.huashi.bill.order.domain.ExchangeOrder;
import com.huashi.bill.order.exception.ExchangeException;
import com.huashi.bill.order.model.ExchangeOrderModel;
import com.huashi.bill.order.util.TradeOrderNoBuilder;
import com.huashi.common.user.service.IUserAccountService;
import com.huashi.common.user.service.IUserBalanceService;

@Service
public class ExchangeOrderService implements IExchangeOrderService {

	@Reference
	private IUserBalanceService userBalanceService;
	@Reference
	private IUserAccountService userAccountService;
	@Autowired
	private ExchangeOrderMapper exchangeOrderMapper;

	private Object lock = new Object();

	/**
	 * 
	 * TODO 初始化订单
	 * 
	 * @param model
	 * @return
	 */
	private ExchangeOrder init(ExchangeOrderModel model) {
		ExchangeOrder exchangeOrder = new ExchangeOrder();
		exchangeOrder.setOrderNo(TradeOrderNoBuilder.build());
		exchangeOrder.setUserId(model.getUserId());
		exchangeOrder.setFromUserId(model.getFromUserId());
		exchangeOrder.setAppType(model.getAppType().getCode());
		exchangeOrder.setProductFee(model.getProductFee());
		exchangeOrder.setExchangeType(model.getExchangeType().getValue());
		exchangeOrder.setPlatformType(model.getPlatformType().getCode());
		exchangeOrder.setCreateTime(new Date());
		exchangeOrder.setOperator(model.getOperator());
		return exchangeOrder;
	}

	@Override
	public String buildExchangeOrder(ExchangeOrderModel model) {
		synchronized (lock) {
			ExchangeOrder exchangeOrder = init(model);
			
			try {
				boolean isSuccess = false;
				if (ExchangeType.ACCOUNT_MONEY == model.getExchangeType())
					isSuccess = userAccountService.exchange(model.getUserId(), model.getFromUserId(), model.getProductFee());
				else
					isSuccess = userBalanceService.exchange(model.getUserId(), model.getFromUserId(), model.getPlatformType().getCode(),
							model.getProductFee().intValue());
				
				if(isSuccess) {
					exchangeOrder.setStatus(ExchangeStatus.COMPLETED.getValue());
					exchangeOrder.setCompleteTime(new Date());
					exchangeOrderMapper.insertSelective(exchangeOrder);
				}
				
				return exchangeOrder.getOrderNo();
			} catch (Exception e) {
				exchangeOrder.setStatus(ExchangeStatus.DEAL_FAILED.getValue());
				exchangeOrderMapper.insertSelective(exchangeOrder);
				
				throw new ExchangeException(e);
			}
		}
	}

}
