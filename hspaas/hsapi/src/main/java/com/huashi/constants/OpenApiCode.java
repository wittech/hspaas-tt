package com.huashi.constants;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * TODO 融合平台接口状态码
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年7月24日 下午10:18:47
 */
public class OpenApiCode {
	
	// 调用平台成功码
	public static final String SUCCESS = "0";
	// 推送至用户侧成功码
	public static final String DELIVER_SUCCESS = "DELIVRD";

	/**
     * 
     * TODO 融合平台公用状态码
     *
     * @author zhengying
     * @version V1.0.0
     * @date 2016年9月21日 上午12:00:33
     */
    public enum ApiReponseCode {
        SUCCESS("0", "业务校验成功"), 
            REQUEST_EXCEPTION("101", "必填项为空"),
            SMS_CONTEXT_TOO_LONG("102", "短信长度不能超过670字符度"),
            APPKEY_INVALID("103", "应用不存在"), 
            TEMPLATE_NOT_EXISTS("104", "模板不存在"),
            TEMPLATE_INVALID("105", "模板未审核"),
            BALANCE_NOT_ENOUGH("106", "短信余额不足"),
            API_DEVELOPER_INVALID("107", "客户账号停用"),
            AUTHENTICATION_FAILED("108", "签名验证失败"), 
            MOBILE_INVALID("109", "无效手机号"), 
            SERVER_EXCEPTION("110", "系统异常"), 
            MOBILES_OUT_RANGE("111", "手机号数量超过上限"), 
            TIMESTAMP_EXPIRED("112", "时间戳相差过大"),
            SNED_TIME_INVALID("113", " 设定的发送时间不合法"),  
            IP_ILLEGAL("114", "IP鉴权不通过"),   
            PROXY_NOT_EXISTS("118", "代理商不存在"),
            PROXY_INVALID("119", "代理商审核未通过"),  
            APP_TEMPLATE_NOT_MATCHED("120", "应用与短信模版不对应"),
            TEMPLATE_SIGN_NOT_MATCHED("122", "模版签名长度不合法，必须为3～8个字"),
            TEMPLATE_TITLE_NOT_MATCHED("123", "模版标题长度不合法，必须小于12个字"),
            TEMPLATE_CONTEXT_NOT_MATCHED("124", "模版内容长度不合法，签名+内容必须小于348个字"),
            TEMPLATE_SIZE_LIMIT("125", "模版编号个数超过限制，不得超过100个"),
            CONTEXT_HAS_SENSIWORDS("127", "内容中有敏感词");
        
        private String code;
        private String message;

        private ApiReponseCode(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
        
        public static ApiReponseCode parse(String code) {
            if(StringUtils.isEmpty(code)) {
                {
                    return null;
                }
            }
             
            for(ApiReponseCode api : ApiReponseCode.values()) {
                if(api.getCode().equalsIgnoreCase(code)) {
                    return api;
                }
            }
            return null;
        }
        
        @Override
        public String toString() {
            return enumToJsonMessage(code, message);
        }

    }
	
	/**
	 * 
	   * TODO 枚举信息转JSON格式输出
	   * 
	   * @param code
	   * @param message
	   * @return
	 */
	public static String enumToJsonMessage(String code, String message) {
		JSONObject object = new JSONObject();
		object.put("code", code);
		object.put("message", message);
		return JSON.toJSONString(object);
	}
	
	/**
	 * 
	  * TODO 短信推送状态码
	  *
	  * @author zhengying
	  * @version V1.0.0   
	  * @date 2016年12月25日 下午2:44:28
	 */
	public enum SmsPushCode {
			SMS_SAME_MOBILE_NUM_SEND_BY_HIGN_FREQUENCY("BEYOND_SPEED", "同一个手机号码发送频率过快"), 
			SMS_SAME_MOBILE_NUM_BEYOND_LIMIT_IN_ONE_DAY("BEYOND_TIMES", "同一个手机号码一天内超限"), 
			SMS_MOBILE_BLACKLIST("BLACK", "短信黑名单"), 
			SMS_TASK_REJECT("REJECT", "驳回任务"),
			SMS_SUBMIT_PASSAGE_FAILED("S0099", "提交通道失败");

		private String code;
		private String message;

		private SmsPushCode(String code, String message) {
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}

		public String getMessage() {
			return message;
		}
		
		public static SmsPushCode parse(String code) {
			if(StringUtils.isEmpty(code)) {
                {
                    return null;
                }
            }
			 
			for(SmsPushCode api : SmsPushCode.values()) {
				if(api.getCode().equalsIgnoreCase(code)) {
                    {
                        return api;
                    }
                }
			}
			return null;
		}

		@Override
		public String toString() {
			return enumToJsonMessage(code, message);
		}

	}

}