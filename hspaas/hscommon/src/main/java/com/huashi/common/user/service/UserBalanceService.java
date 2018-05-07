package com.huashi.common.user.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.huashi.bill.order.exception.ExchangeException;
import com.huashi.bill.pay.constant.PayContext.PaySource;
import com.huashi.bill.pay.constant.PayContext.PayType;
import com.huashi.common.notice.service.INotificationMessageService;
import com.huashi.common.settings.context.SettingsContext.NotificationMessageTemplateType;
import com.huashi.common.user.context.UserBalanceConstant;
import com.huashi.common.user.context.UserContext.BalancePayType;
import com.huashi.common.user.context.UserContext.BalanceStatus;
import com.huashi.common.user.dao.UserBalanceLogMapper;
import com.huashi.common.user.dao.UserBalanceMapper;
import com.huashi.common.user.domain.UserBalance;
import com.huashi.common.user.domain.UserBalanceLog;
import com.huashi.common.user.model.P2pBalanceResponse;
import com.huashi.common.user.model.UserModel;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.constants.CommonContext.PlatformType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO 用户账户额度信息
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年12月12日 下午7:04:48
 */
@Service
public class UserBalanceService implements IUserBalanceService {

    @Autowired
    private IUserService                userService;
    @Autowired
    private UserBalanceMapper           userBalanceMapper;
    @Autowired
    private UserBalanceLogMapper        userBalanceLogMapper;
    @Autowired
    private IUserSmsConfigService       userSmsConfigService;
    @Autowired
    private INotificationMessageService notificationMessageService;

    private Logger                      logger           = LoggerFactory.getLogger(getClass());

    /**
     * 点对点模板参数
     */
    private static final Pattern        PATTERN_P2P_ARGS = Pattern.compile("#args#");

    @Override
    public List<UserBalance> findByUserId(int userId) {
        return userBalanceMapper.selectByUserId(userId);
    }

    @Override
    public UserBalance getByUserId(int userId, PlatformType type) {
        if (userId == 0) {
            return null;
        }

        return userBalanceMapper.selectByUserIdAndType(userId, type.getCode());
    }

    private UserBalance getByUserId(int userId, int type) {
        return userBalanceMapper.selectByUserIdAndType(userId, type);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = RuntimeException.class)
    public boolean saveBalance(UserBalance balance) {
        try {
            balance.setPayType(balance.getPayType() == null ? BalancePayType.PREPAY.getValue() : balance.getPayType());
            balance.setCreateTime(new Date());
            int result = userBalanceMapper.insertSelective(balance);
            if (result > 0) {
                UserBalanceLog log = new UserBalanceLog();
                log.setBalance(balance.getBalance());
                log.setPayType(balance.getPayType());
                log.setUserId(balance.getUserId());
                log.setCreateTime(new Date());
                log.setPlatformType(balance.getType());
                log.setPaySource(balance.getPaySource().getValue());

                return userBalanceLogMapper.insert(log) > 0;
            }

            return false;
        } catch (Exception e) {
            logger.error("保存用户 [" + balance.getUserId() + "]失败", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public boolean updateBalance(int userId, int amount, int platformType, PaySource paySource, PayType payType,
                                 Double price, Double totalPrice, String remark, boolean isNotice) {
        try {
            UserBalance userBalance = getByUserId(userId, platformType);
            userBalance.setBalance(userBalance.getBalance() + amount);
            userBalance.setUserId(userId);
            userBalance.setRemark(remark);

            // 冲扣值后统一将告警状态设置为 “正常” add by 20170827
            userBalance.setStatus(BalanceStatus.AVAIABLE.getValue());
            userBalance.setModifyTime(new Date());
            int result = userBalanceMapper.updateByPrimaryKeySelective(userBalance);
            if (result > 0) {
                UserBalanceLog log = new UserBalanceLog();
                log.setBalance(Double.valueOf(amount));
                log.setPaySource(paySource.getValue());
                log.setPayType(payType == null ? null : payType.getValue());
                log.setUserId(userBalance.getUserId());
                log.setCreateTime(new Date());
                log.setPlatformType(userBalance.getType());
                log.setPrice(price);
                log.setTotalPrice(totalPrice);
                log.setRemark(remark);

                if (isNotice) {
                    notificationMessageService.save(userId,
                                                    NotificationMessageTemplateType.USER_BALACE_CHANGE,
                                                    String.format(NotificationMessageTemplateType.USER_BALACE_CHANGE.getContent(),
                                                                  PlatformType.parse(platformType).getName(),
                                                                  amount));
                }

                return userBalanceLogMapper.insert(log) > 0;
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public boolean exchange(int userId, int fromUserId, int type, int amount) {
        if (userId == 0) {
            throw new ExchangeException("用户ID为空");
        }

        validate(fromUserId, type, amount);

        try {
            boolean isOk = updateBalance(userId, amount, type, PaySource.USER_ACCOUNT_EXCHANGE,
                                         PayType.HSUSER_EXCHANGE, null, null, "余额转赠", true);
            if (isOk) {
                return updateBalance(fromUserId, -amount, type, PaySource.USER_ACCOUNT_EXCHANGE,
                                     PayType.HSUSER_EXCHANGE, null, null, "余额转赠", true);
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void validate(int fromUserId, int type, int balance) {
        if (fromUserId == 0) {
            throw new ExchangeException("转存人ID为空");
        }

        if (balance == 0d) {
            throw new ExchangeException("转存额度为0");
        }

        UserBalance userBalance = getByUserId(fromUserId, type);
        if (userBalance == null) {
            throw new ExchangeException("用户平台额度数据为空");
        }

        if (userBalance.getBalance() < balance) {
            throw new ExchangeException(String.format("用户平台额度不足，当前余额 : %d ", balance));
        }

    }

    @Override
    public boolean isBalanceEnough(int userId, PlatformType type, Double fee) {
        UserBalance userBalance = getByUserId(userId, type);
        if (userBalance == null) {
            logger.error("用户：{} ，平台类型：{} 余额数据异常，请检修", userId, type);
            return false;
        }

        // 如果用户付费类型为后付费则不判断 余额是否不足
        if (BalancePayType.POSTPAY.getValue() == userBalance.getPayType()) {
            logger.info("用户：{} ，平台类型：{} 付费类型为后付费，不检验可用余额", userId, type);
            return true;
        }

        if (userBalance.getBalance() < fee) {
            logger.warn("用户额度不足：用户ID：{} 平台类型：{} 可用余额：{} 本次计费：{}，", userId, type, userBalance.getBalance(), fee);
            return false;
        }

        return true;
    }

    @Override
    public BossPaginationVo<UserBalance> findPage(Integer userId, int currentPage) {
        BossPaginationVo<UserBalance> page = new BossPaginationVo<UserBalance>();
        page.setCurrentPage(currentPage);
        int total = userBalanceMapper.findCount(userId);
        if (total <= 0) {
            return page;
        }
        page.setTotalCount(total);
        List<UserBalance> list = userBalanceMapper.findList(userId, page.getStartPosition(), page.getPageSize());
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        for (UserBalance b : list) {
            UserModel u = userService.getByUserId(b.getUserId());
            if (u == null) {
                continue;
            }

            b.setName(u.getName());
        }
        page.getList().addAll(list);
        return page;
    }

    @Override
    public UserBalance getById(int id) {
        return userBalanceMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean deductBalance(int userId, int amount, int platformType, String remark) {
        try {
            UserBalance userBalance = getByUserId(userId, platformType);
            userBalance.setBalance(userBalance.getBalance() + amount);
            userBalance.setUserId(userId);
            if (StringUtils.isNotEmpty(remark)) {
                userBalance.setRemark(remark);
            }
            userBalance.setModifyTime(new Date());
            return userBalanceMapper.updateByPrimaryKeySelective(userBalance) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateBalanceWarning(UserBalance userBalance) {
        if (userBalance == null) {
            return false;
        }

        return userBalanceMapper.updateWarning(userBalance) > 0;
    }

    @Override
    public List<UserBalance> findAvaibleUserBalace() {
        return userBalanceMapper.selectAvaiableUserBalance();
    }

    @Override
    public boolean updateStatus(Integer id, Integer status) {
        return userBalanceMapper.updateStatus(id, status) > 0;
    }

    @Override
    public int calculateSmsAmount(int userId, String content) {
        if (userId == 0 || StringUtils.isEmpty(content)) {
            logger.error("userId :{} 短信报文为空，无法计算计费条数", userId);
            return UserBalanceConstant.CONTENT_WORDS_EXCEPTION_COUNT_FEE;
        }

        // 获取短信单条计费字数
        int wordsPerNum = userSmsConfigService.getSingleChars(userId);

        return calculateGroupSizeByContent(wordsPerNum, content);
    }

    /**
     * TODO 根据短信内容和每条计费字数 计算总费用
     * 
     * @param wordsPerNum 每条计费字数
     * @param content 短信内容
     * @return
     */
    private int calculateGroupSizeByContent(int wordsPerNum, String content) {
        if (StringUtils.isEmpty(content)) {
            return UserBalanceConstant.CONTENT_WORDS_EXCEPTION_COUNT_FEE;
        }

        // 按照70个字为单位，减去设置的第一条短信计费字数，得出实际签名内容长度
        int realTotalWords = UserBalanceConstant.WORDS_SIZE_PER_NUM - wordsPerNum + content.length();
        if (realTotalWords <= UserBalanceConstant.WORDS_SIZE_PER_NUM) {
            // 如果减除的字数后 小于等于0，则按照1条计费
            return UserBalanceConstant.CONTENT_SINGLE_FEE;
        }

        // 长短信计费按照67字计费
        return realTotalWords % UserBalanceConstant.LONG_TEXT_MESSAGE_WORDS_SIZE_PER_NUM == 0 ? realTotalWords
                                                                                                / UserBalanceConstant.LONG_TEXT_MESSAGE_WORDS_SIZE_PER_NUM : realTotalWords
                                                                                                                                                             / UserBalanceConstant.LONG_TEXT_MESSAGE_WORDS_SIZE_PER_NUM
                                                                                                                                                             + 1;
    }

    @Override
    public P2pBalanceResponse calculateP2pSmsAmount(int userId, List<JSONObject> p2pBodies) {
        if (CollectionUtils.isEmpty(p2pBodies)) {
            logger.error("userId :{} 点对点短信报文为空，无法计算计费条数", userId);
            return new P2pBalanceResponse(UserBalanceConstant.CONTENT_WORDS_EXCEPTION_COUNT_FEE, null);
        }

        // 总费用
        int smsTotalNum = 0;
        int wordsPerNum = userSmsConfigService.getSingleChars(userId);
        for (JSONObject obj : p2pBodies) {
            int num = calculateGroupSizeByContent(wordsPerNum, obj.getString("content"));
            obj.put("fee", num);
            smsTotalNum += num;
        }

        return new P2pBalanceResponse(smsTotalNum, p2pBodies);
    }

    @Override
    public P2pBalanceResponse calculateP2ptSmsAmount(int userId, String content, List<JSONObject> p2pBodies) {
        if (CollectionUtils.isEmpty(p2pBodies) || StringUtils.isEmpty(content)) {
            logger.error("userId :{} 模板点对点短信内容或报文为空，无法计算计费条数", userId);
            return new P2pBalanceResponse(UserBalanceConstant.CONTENT_WORDS_EXCEPTION_COUNT_FEE, null);
        }

        // 总费用
        int smsTotalNum = 0;

        int wordsPerNum = userSmsConfigService.getSingleChars(userId);
        String finalContent;
        for (JSONObject p2pBody : p2pBodies) {
            finalContent = translateP2pArgs(content, p2pBody.getObject("args", Object[].class));

            int num = calculateGroupSizeByContent(wordsPerNum, finalContent);
            p2pBody.put("content", finalContent);
            p2pBody.put("fee", num);
            smsTotalNum += num;
        }

        return new P2pBalanceResponse(smsTotalNum, p2pBodies);
    }

    /**
     * TODO 替换模板点对点短信内容
     * 
     * @param content 短信内容
     * @param args 参数信息
     * @return
     */
    private static String translateP2pArgs(String content, Object[] args) {
        Matcher matcher = PATTERN_P2P_ARGS.matcher(content);

        StringBuffer finalContent = new StringBuffer();
        int index = 0;
        while (matcher.find()) {
            if (index >= args.length) {
                break;
            }

            matcher.appendReplacement(finalContent, args[index].toString());
            index++;
        }

        matcher.appendTail(finalContent);// 添加尾巴
        return finalContent.toString();
    }

}
