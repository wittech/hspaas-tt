package com.huashi.bill.order.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.bill.order.OrderBuilderException;
import com.huashi.bill.order.constant.TradeOrderContext;
import com.huashi.bill.order.constant.TradeOrderContext.TradeOrderStatus;
import com.huashi.bill.order.constant.TradeOrderContext.TradeType;
import com.huashi.bill.order.dao.TradeOrderMapper;
import com.huashi.bill.order.domain.TradeOrder;
import com.huashi.bill.order.domain.TradeOrderInvoice;
import com.huashi.bill.order.exception.TradeUpdateException;
import com.huashi.bill.order.model.AlipayTradeOrderModel;
import com.huashi.bill.order.model.OfflineTradeOrderModel;
import com.huashi.bill.order.model.TradeOrderModel;
import com.huashi.bill.order.util.TradeOrderNoBuilder;
import com.huashi.bill.pay.constant.PayContext.PaySource;
import com.huashi.bill.pay.constant.PayContext.PayType;
import com.huashi.bill.pay.model.AlipayModel;
import com.huashi.bill.pay.service.IAlipayService;
import com.huashi.bill.product.domain.Combo;
import com.huashi.bill.product.domain.Product;
import com.huashi.bill.product.service.IComboService;
import com.huashi.bill.product.service.IProductService;
import com.huashi.common.finance.constant.InvoiceContext.ExpressStatus;
import com.huashi.common.finance.constant.InvoiceContext.InvoiceType;
import com.huashi.common.finance.domain.InvoiceRecord;
import com.huashi.common.finance.service.IInvoiceBalanceService;
import com.huashi.common.finance.service.IInvoiceRecordService;
import com.huashi.common.user.service.IUserAccountService;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TradeOrderService implements ITradeOrderService {

	@Autowired
	private IComboService comboService;
	@Autowired
	private IProductService productService;
	@Autowired
	private ITradeOrderInvoiceService tradeOrderInvoiceService;
	@Autowired
	private TradeOrderMapper tradeOrderMapper;
	@Autowired
	private IAlipayService alipayService;
	@Reference
	private IUserBalanceService userBalanceService;
	@Reference
	private IInvoiceRecordService invoiceRecordService;
	@Reference
	private IInvoiceBalanceService invoiceBalanceService;
	@Reference
	private IUserAccountService userAccountService;

	private Object lock = new Object();

	@Override
	public long buildTradeOrder(int userId, String productName, double totalFee, int payType, String operator) {
		return 0;
	}

	/**
	 * 
	 * TODO 初始化订单
	 * 
	 * @param model
	 * @return
	 */
	private TradeOrder init(TradeOrderModel model) {
		TradeOrder tradeOrder = new TradeOrder();
		tradeOrder.setUserId(model.getUserId());
		tradeOrder.setOrderNo(TradeOrderNoBuilder.build());
		tradeOrder.setTradeNo(tradeOrder.getOrderNo());
		tradeOrder.setPayType(model.getPayType());
		tradeOrder.setStatus(TradeOrderStatus.WAIT_PAY.getValue());
		tradeOrder.setCreateTime(new Date());
		tradeOrder.setTradeType(model.getTradeType());
		
		if(TradeType.ACCOUNT_MONEY_CHARGE.getValue() == model.getTradeType()) {
			// 交易类型为站内充值
			tradeOrder.setProductFee(Double.parseDouble(model.getProductFee()));
			tradeOrder.setProductName(StringUtils.isNotEmpty(model.getProductName()) ? model.getProductName()
					: String.format(TradeOrderContext.TRADE_ORDER_SUBJECT_TRANS_REG, model.getProductFee()));
			tradeOrder.setTradeType(TradeType.ACCOUNT_MONEY_CHARGE.getValue());
			
		} else {
			// 产品套餐购买
			Combo combo = comboService.findById(model.getComboId());
			if (combo == null)
				throw new RuntimeException("套餐数据有误");
			
			tradeOrder.setProductComboId(model.getComboId());
			tradeOrder.setProductName(combo.getName());
			tradeOrder.setProductFee(combo.getSellMoney());
		}
		
		tradeOrder.setInvoiceFlag(model.getInvoiceFlag() != null && model.getInvoiceFlag() == 1);
		return tradeOrder;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = OrderBuilderException.class)
	public String buildTradeOrder(AlipayTradeOrderModel model) {
		TradeOrder tradeOrder = init(model);
		tradeOrder.setPayType(PayType.ALIPAY.getValue());
		try {
			int result = tradeOrderMapper.insertSelective(tradeOrder);
			if (result == 0)
				throw new OrderBuilderException("订单生成失败");
			
			// 如果需要开发票，则直接自动生成开票记录和 订单关联的开票信息
			if (tradeOrder.getInvoiceFlag()) {
				boolean isSuccess = tradeOrderInvoiceService.save(tradeOrder.getId(), model.getInvoiceTitle(), model.getName(),
						model.getMobile(), model.getAddress());
				if(!isSuccess)
					throw new OrderBuilderException("订单生成失败");
			}
			
			return alipay(model, tradeOrder);

		} catch (Exception e) {
			e.printStackTrace();
			throw new OrderBuilderException("订单生成失败");
		}
	}
	
	/**
	 * 
	   * TODO 发票处理逻辑
	   * 
	   * @param
	   * @param
	   * @return
	 */
	private boolean doOrderInvoiceProcess(TradeOrder tradeOrder) {
		try {
			if (tradeOrder.getInvoiceFlag()) {
				TradeOrderInvoice orderInvoice = tradeOrderInvoiceService.getByOrderId(tradeOrder.getId());
				if(orderInvoice == null)
					throw new RuntimeException("订单发票信息为空");
				
				InvoiceRecord invoiceRecord = new InvoiceRecord();
				invoiceRecord.setAddress(orderInvoice.getAddress());
				invoiceRecord.setMoney(tradeOrder.getProductFee());
				invoiceRecord.setMailMan(orderInvoice.getContactName());
				invoiceRecord.setMemo(String.format("订单类型：%s，自动申请开发票", TradeType.parse(tradeOrder.getTradeType()).getTitle()));
				invoiceRecord.setMobile(orderInvoice.getContactPhone());
				invoiceRecord.setTitle(orderInvoice.getTitle());
				invoiceRecord.setStatus(ExpressStatus.WAITING.getCode());
				invoiceRecord.setUserId(tradeOrder.getUserId());
				invoiceRecord.setType(InvoiceType.GENERAL.getCode());
				invoiceRecord.setNeedUpdateBalance(false);
				return invoiceRecordService.save(invoiceRecord);
			} else {
				// 如果不需要开发票，则将本次消费的金额直接累加更新至可开票余额信息
				return invoiceBalanceService.updateAvaiableBalance(tradeOrder.getUserId(), tradeOrder.getProductFee());
			}
			
		} catch (Exception e) {
			throw new OrderBuilderException("订单生成失败");
		}
		
	}

	/**
	 * 
	 * TODO 支付宝前置参数设置
	 * 
	 * @param model
	 * @param tradeOrder
	 * @return
	 */
	private String alipay(AlipayTradeOrderModel model, TradeOrder tradeOrder) {
		AlipayModel alipayModel = new AlipayModel();
		alipayModel.setTradeNo(tradeOrder.getTradeNo());
		alipayModel.setProduct(tradeOrder.getProductName());
		alipayModel.setTotalFee(String.valueOf(tradeOrder.getProductFee()));
		alipayModel.setPageUrl(model.getPageUrl());
		alipayModel.setNotifyUrl(model.getNotifyUrl());
		alipayModel.setIp(model.getIp());

		return alipayService.buildOrder(alipayModel);
	}

	@Override
	public String buildTradeOrder(OfflineTradeOrderModel model) {
		TradeOrder tradeOrder = init(model);
		tradeOrder.setPayType(PayType.OFFLINE_TRANSFER.getValue());
		try {
			int result = tradeOrderMapper.insertSelective(tradeOrder);
			if (result == 0)
				throw new OrderBuilderException("订单生成失败");

			if (tradeOrder.getInvoiceFlag()) {
				tradeOrderInvoiceService.save(tradeOrder.getId(), model.getInvoiceTitle(), model.getName(),
						model.getMobile(), model.getAddress());
			}

			return tradeOrder.getOrderNo();

		} catch (Exception e) {
			e.printStackTrace();
			throw new OrderBuilderException("订单生成失败");
		}
	}

	@Override
	public boolean updateOrderPayCompleted(String tradeNo) {
		synchronized (lock) {
			TradeOrder tradeOrder = tradeOrderMapper.selectByTradeNo(tradeNo);
			if(tradeOrder == null)
				throw new TradeUpdateException("订单无效");
			
			if(tradeOrder.getStatus() != null &&tradeOrder.getStatus() == TradeOrderStatus.COMPLETED.getValue())
				return true;
			
			int result = tradeOrderMapper.updateOrder2Comoplte(tradeOrder.getId());
			if(result == 0)
				throw new TradeUpdateException("订单更新失败");
			
			if(TradeType.ACCOUNT_MONEY_CHARGE.getValue() == tradeOrder.getTradeType()) {
				// 交易类型为站内充值
				userAccountService.updateAccount(tradeOrder.getUserId(), tradeOrder.getProductFee(), 
						PaySource.TRADE_ORDER_PAY.getValue(), tradeOrder.getPayType());
				
			} else {
				// 产品套餐购买
				List<Product> products = productService.getProductListByComboId(tradeOrder.getProductComboId());
				if(CollectionUtils.isEmpty(products))
					throw new TradeUpdateException("订单中无套餐包数据");
				
				try {
					for(Product product : products) {
						userBalanceService.updateBalance(tradeOrder.getUserId(), product.getAmount(), product.getType(),
								PaySource.TRADE_ORDER_PAY, PayType.ALIPAY, (double)product.getAmount(), (double)product.getAmount(), 
								"支付宝充值", true);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			// 更新发票余额相关信息
			doOrderInvoiceProcess(tradeOrder);
			
			return true;
		}
	}
	
	@Override
	public PaginationVo<TradeOrder> findPage(int userId,String orderNo, String startDate, String endDate,String currentPage,String status,String payType) {
		int _currentPage = PaginationVo.parse(currentPage);

		Map<String, Object> params =  new HashMap<String,Object>();
		params.put("userId", userId);
		if(StringUtils.isNotEmpty(startDate)){
			params.put("startDate", startDate);
		}
		if(StringUtils.isNotEmpty(endDate)){
			params.put("endDate", endDate);
		}
		if(StringUtils.isNotEmpty(orderNo)){
			params.put("orderNo", orderNo);
		}
		if(StringUtils.isNotEmpty(status)){
			params.put("status", status);
		}
		if(StringUtils.isNotEmpty(payType)){
			params.put("payType", payType);
		}
		int totalRecord = tradeOrderMapper.getCountByUserId(params);
		if (totalRecord == 0)
			return null;

		params.put("startPage", PaginationVo.getStartPage(_currentPage));
		params.put("pageRecord", PaginationVo.DEFAULT_RECORD_PER_PAGE);

		List<TradeOrder> list = tradeOrderMapper.findPageListByUserId(params);
		if (list == null || list.isEmpty())
			return null;
		return new PaginationVo<TradeOrder>(list, _currentPage, totalRecord);
	}

	@Override
	public BossPaginationVo<TradeOrder> findPage(int pageNum, String keyword, String start, String end, int status) {
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("keyword", keyword);
		paramMap.put("startDate", start);
		paramMap.put("endDate", end);
		paramMap.put("status", status);
		BossPaginationVo<TradeOrder> page = new BossPaginationVo<TradeOrder>();
		page.setCurrentPage(pageNum);
		int total = tradeOrderMapper.findCount(paramMap);
		if(total <= 0){
			return page;
		}
		page.setTotalCount(total);
		paramMap.put("start", page.getStartPosition());
		paramMap.put("end", page.getPageSize());
		List<TradeOrder> dataList = tradeOrderMapper.findList(paramMap);
		page.getList().addAll(dataList);
		return page;
	}

}
