package com.huashi.sms.config.cache.redis.serializer;

import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;

public class MesssageReceiptFilter extends SimplePropertyPreFilter {

	public MesssageReceiptFilter() {
		
		super(SmsMtMessageSubmit.class,"sid", "msgId", "passageId","createTime");
	}


	public MesssageReceiptFilter(SmsMtMessageDeliver deliver) {
		
		super(SmsMtMessageSubmit.class, "sid", "msgId", "passageId","createTime", "pushUrl", "retryTimes");
	}
	
}
