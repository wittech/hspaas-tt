package com.huashi.exchanger.resolver.cmpp.v3;

import com.huawei.insa2.comm.cmpp.message.CMPPMessage;
import com.huawei.insa2.comm.cmpp30.message.CMPP30DeliverMessage;
import com.huawei.insa2.util.Args;
import com.huawei.smproxy.SMProxy30;

public class Cmpp3ManageProxy extends SMProxy30 {

	// 通道ID
	private Integer passageId;
	private Cmpp3ProxySender cmppProxySender;

	public Cmpp3ManageProxy(Cmpp3ProxySender proxySend, Integer passageId, Args args) {
		super(args);
		this.passageId = passageId;
		this.cmppProxySender = proxySend;
	}

	@Override
	public CMPPMessage onDeliver(CMPP30DeliverMessage msg) {
//		logger.info("华时SMProxyRec接收短信\r\n");
		cmppProxySender.doProcessDeliverMessage(msg);
		return super.onDeliver(msg);
	}

	@Override
	public void onTerminate() {
		cmppProxySender.onTerminate(passageId);
	}

	
}
