package com.huashi.sms.config.rabbit.listener;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.alibaba.druid.util.StringUtils;
import com.huashi.util.HttpClientUtil;

public class BaseListener {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	// 推送回执信息，如果用户回执success才算正常接收，否则重试，达到重试上限次数，抛弃
	public static final String PUSH_REPONSE_SUCCESS_CODE = "success";
	public static final int PUSH_RETRY_TIMES = 3;

	/**
	 * 
	 * TODO 调用用户回调地址
	 * 
	 * @param url
	 *            推送回调地址（HTTP）
	 * @param content
	 *            推送报文内容
	 * @param retryTimes
	 *            重试次数（默认3次）
	 * @return
	 */
	protected RetryResponse post(String url, String content, int retryTimes, int curretCount) {
		RetryResponse retryResponse = new RetryResponse();
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(content)) {
        	retryResponse.setResult("URL或内容为空");
        	return retryResponse;
        }
        logger.info("------------------------------HttpClient 推送开始-------------------------------");
        logger.info("req  url{}, content:{}", url, content);
        
        try {
        	String result = HttpClientUtil.postReport(url, content);
        	retryResponse.setResult(StringUtils.isEmpty(result) ? PUSH_REPONSE_SUCCESS_CODE : result);
            retryResponse.setSuccess(true);
            
        } catch (Exception e) {
            logger.error("调用用户推送地址解析失败：{}， 错误信息：{}", url, e.getMessage());
			retryResponse.setResult("调用异常失败");
        }
        
    	if(!retryResponse.isSuccess() && curretCount <= retryTimes)  {
    		curretCount = curretCount + 1;
    		retryResponse = post(url, content, retryTimes, curretCount);
    	}
    		
    	retryResponse.setAttemptTimes(curretCount > retryTimes ? retryTimes : curretCount);
        logger.info("------------------------------HttpClient 推送结束-------------------------------");
        return retryResponse;
	}
	
	@Test
	public void test() {
		RetryResponse response = post("http://183.159.166.103:31118/aa", "{\"receiveTime\":\"2017-02-24 12:27:12\",\"mobile\":\"18368031231\",\"sid\":1669965938914494464,\"status\":\"DELIVRD\"}测试", 2, 1);
		
		Assert.assertTrue(response.result, response.isSuccess);
	}
	

	/**
	 * 
	 * TODO 设置重试模板
	 * 
	 * @param retryTimes
	 * @return
	 */
	protected RetryTemplate retryTemplate(int retryTimes) {
		// 重试模板
		RetryTemplate retryTemplate = new RetryTemplate();
		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(500);
		backOffPolicy.setMultiplier(10.0);
		backOffPolicy.setMaxInterval(10000);
		retryTemplate.setBackOffPolicy(backOffPolicy);

		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
		// 尝试最大重试次数
		retryPolicy.setMaxAttempts(retryTimes);
		retryTemplate.setRetryPolicy(retryPolicy);

		return retryTemplate;
	}

	public class RetryResponse {
		// 尝试次数
		private int attemptTimes = 0;
		// 返回结果
		private String result;
		// 最后一次异常信息
		private Throwable lastThrowable;
		
		private boolean isSuccess = false;

		public int getAttemptTimes() {
			return attemptTimes;
		}

		public void setAttemptTimes(int attemptTimes) {
			this.attemptTimes = attemptTimes;
		}

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}

		public Throwable getLastThrowable() {
			return lastThrowable;
		}

		public void setLastThrowable(Throwable lastThrowable) {
			this.lastThrowable = lastThrowable;
		}

		public boolean isSuccess() {
			return isSuccess;
		}

		public void setSuccess(boolean isSuccess) {
			this.isSuccess = isSuccess;
		}
	}
}
