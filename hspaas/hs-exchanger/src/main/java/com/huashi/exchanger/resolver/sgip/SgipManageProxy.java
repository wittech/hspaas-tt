package com.huashi.exchanger.resolver.sgip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.insa2.comm.sgip.message.SGIPDeliverMessage;
import com.huawei.insa2.comm.sgip.message.SGIPMessage;
import com.huawei.insa2.comm.sgip.message.SGIPReportMessage;
import com.huawei.insa2.util.Args;
import com.huawei.smproxy.SGIPSMProxy;

public class SgipManageProxy extends SGIPSMProxy {

	private Integer passageId;
	private SgipProxySender sgipProxySender;
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	public SgipManageProxy(SgipProxySender sgipProxySender, Integer passageId, Args args) {
		super(args);
		try{
			// SGIP 短连接需要调用 startService，主要是为了开启短信上行报告及短信状态回执监听服务端口
    		super.startService(args.get("local-ip", "127.0.0.1"),  args.get("local-port", 8752));
    		this.passageId = passageId;
    		this.sgipProxySender = sgipProxySender;
    	}catch( IllegalStateException e){
    		logger.error("init SGIPProxyRec error",e);
    	}
	}

	@Override
	public SGIPMessage onDeliver(SGIPDeliverMessage msg) {
//		logger.info("华时SMProxyRec接收短信\r\n");
		sgipProxySender.moReceive(msg);
		return super.onDeliver(msg);
	}
	

	@Override
	public SGIPMessage onReport(SGIPReportMessage msg) {
		sgipProxySender.mtDeliver(msg);
		return super.onReport(msg);
	}

	@Override
	public void onTerminate() {
		sgipProxySender.onTerminate(passageId);
	}

	
}
