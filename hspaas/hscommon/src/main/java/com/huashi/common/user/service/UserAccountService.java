package com.huashi.common.user.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.apache.dubbo.config.annotation.Service;
import com.huashi.bill.order.exception.ExchangeException;
import com.huashi.bill.pay.constant.PayContext.PaySource;
import com.huashi.bill.pay.constant.PayContext.PayType;
import com.huashi.common.notice.service.INotificationMessageService;
import com.huashi.common.settings.context.SettingsContext.NotificationMessageTemplateType;
import com.huashi.common.user.dao.UserAccountLogMapper;
import com.huashi.common.user.dao.UserAccountMapper;
import com.huashi.common.user.domain.UserAccount;
import com.huashi.common.user.domain.UserAccountLog;

@Service
public class UserAccountService implements IUserAccountService{
	
	@Autowired
	private UserAccountMapper userAccountMapper;
	@Autowired
	private UserAccountLogMapper userAccountLogMapper;
	@Autowired
	private INotificationMessageService notificationMessageService;

	@Override
	public UserAccount getByUserId(int userId) {
		if(userId == 0) {
            {
                return null;
            }
        }
		
		return userAccountMapper.selectByUserId(userId);
	}
	
	@Override
	public double getAccountByUserId(int userId) {
		if (userId == 0) {
            {
                return 0d;
            }
        }

		UserAccount account = getByUserId(userId);
		if (account == null) {
            {
                return 0d;
            }
        }

		return account.getMoney();
	}
	
	
	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public boolean updateAccount(int userId, Double money, int paySource, int payType) {
		try {
			UserAccount userAccount = getByUserId(userId);
			userAccount.setMoney(userAccount.getMoney() + money);
			userAccount.setUserId(userId);
			userAccount.setUpdateTime(new Date());
			int result = userAccountMapper.updateByPrimaryKeySelective(userAccount);
			if (result > 0) {
				UserAccountLog log = new UserAccountLog();
				log.setBalance(money);
				log.setPaySource(paySource);
				log.setPayType(payType);
				log.setUserId(userAccount.getUserId());
				log.setCreateTime(new Date());
				
				notificationMessageService.save(userId, NotificationMessageTemplateType.USER_ACCOUNT_CHANGE, 
						String.format(NotificationMessageTemplateType.USER_ACCOUNT_CHANGE.getContent(), money));

				return userAccountLogMapper.insert(log) > 0;
			}

			return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public boolean exchange(int userId, int fromUserId, Double money) {
		validate(userId, fromUserId, money);
		
		try {
			boolean isOk = updateAccount(userId, money, PaySource.USER_ACCOUNT_EXCHANGE.getValue(), 
					PayType.HSUSER_EXCHANGE.getValue());
			if (isOk) {
                {
                    return updateAccount(fromUserId, -money, PaySource.USER_ACCOUNT_EXCHANGE.getValue(), PayType.HSUSER_EXCHANGE.getValue());
                }
            }
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private void validate(int userId, int fromUserId, Double money) {
		if(userId == 0 || fromUserId == 0) {
            {
                throw new ExchangeException("用户ID和转存人ID为空");
            }
        }
		
		if(money == 0d) {
            {
                throw new ExchangeException("转存额度为0");
            }
        }
		
		Double accountBalance = getAccountByUserId(fromUserId);
		if(accountBalance < money) {
            {
                throw new ExchangeException(String.format("用户平台额度不足，当前余额 : %d ", accountBalance));
            }
        }
		
	}

	@Override
	public boolean save(int userId) {
		UserAccount account = new UserAccount();
		account.setUserId(userId);
		account.setMoney(0d);
		account.setFreezeMoney(0d);
		account.setCreateTime(new Date());
		return userAccountMapper.insertSelective(account) > 0;
	}

}
