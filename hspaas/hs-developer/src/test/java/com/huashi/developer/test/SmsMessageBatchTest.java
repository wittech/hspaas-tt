package com.huashi.developer.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.MapUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.util.RandomUtil;
import com.huashi.common.util.SecurityUtil;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.constants.OpenApiCode;
import com.huashi.util.HttpClientUtil;

public class SmsMessageBatchTest {
    
    /**
     * 单个请求包中包含的手机号码数
     */
    private static int MOBILE_SIZE;
    
    /**
     * 总线程个数
     */
    private static int THREAD_SIZE;
    
    /**
     * 单个线程中调用发送次数
     */
    private static int RQEUST_SIZE;
    private static String APPKEY;
    private static String PASSWORD;
    private static String URL;
//    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    
    static {
        MOBILE_SIZE = 1;
        THREAD_SIZE = 200;
        RQEUST_SIZE = 5;
        
//        MOBILE_SIZE = 1;
//        THREAD_SIZE = 1;
//        RQEUST_SIZE = 1;
        
        APPKEY = "hsABtnntg3uFTS";
        PASSWORD = "d523e832d112202b966beb909df270b5";
        URL = "http://localhost:8080/sms/send";
//        String url = "http://dev.hspaas.cn:8080/sms/send";
    }
    
    private static String getMobiles() {
        StringBuilder builder = new StringBuilder();
        for(int i=0; i< MOBILE_SIZE; i++) {
            builder.append("158" + RandomUtil.getRandomNum(8)).append(",");
        }
        return builder.substring(0, builder.length() - 1);
    }

	public static boolean send() {
		String mobile = getMobiles();

		String content = String.format("【华时科技】您的短信验证码为%s，请尽快完成后续操作。", RandomUtil.getRandomNum());

		System.out.println(Thread.currentThread().getName() + "-> 短信内容：" + content);

		
		Map<String, Object> result = JSON.parseObject(call(mobile, content),
				new TypeReference<Map<String, Object>>() {
				});

		if(MapUtils.isEmpty(result) || !OpenApiCode.SUCCESS.equals(result.get("code").toString())) {
		    System.out.println(JSON.toJSONString(result));
		    return false;
		}
		
		return true;
	}


	public static void main(String[] args) throws InterruptedException {
		ExecutorService service = Executors.newFixedThreadPool(50);

		CountDownLatch cdl = new CountDownLatch(THREAD_SIZE);
		
		for(int i = 0; i< THREAD_SIZE; i++) {
		    service.execute(new SendWorker(cdl));
		}
		
		System.out.println("线程加入完毕，准备执行任务。。。");
		
		// 记录一下等待开始时间
        long s1 = System.currentTimeMillis();

        // 协同：让main线程等待任务都完成
        cdl.await();

        System.out.println("===========任务执行完成，耗时:" + (System.currentTimeMillis() - s1) +"ms==============");

        // TODO 再找出最大值

        // 线程池不需要用了，把它关闭
        service.shutdown();
	}
	
	static class SendWorker implements Runnable {
	    
	    CountDownLatch cdl;
	    SendWorker(CountDownLatch cdl) {
	        this.cdl = cdl;
	    }

	    @Override
        public void run() {
	        for (int i = 0; i < RQEUST_SIZE; i++) {
                send();
            }
            
	        cdl.countDown();
        }
    }

	/**
	 * 
	   * TODO 调用发送短信
	   * 
	   * @param mobile
	   * @param content
	   * @return
	 */
	private static String call(String mobile, String content) {
	    Map<String, Object> headers = new HashMap<>();
	    headers.put("apptype", AppType.WEB.getCode() + "");
	    
	    // 当前时间戳
	    String currentTime = System.currentTimeMillis() + "";
	    
	    
        Map<String ,Object> params = new HashMap<>();
        params.put("appkey", APPKEY);
        params.put("appsecret", SecurityUtil.md5Hex(PASSWORD + mobile + currentTime));
        params.put("timestamp", currentTime);
        params.put("mobile", mobile);
        params.put("content", content);
        params.put("attach", "test889");
        params.put("callback", "http://localhost:9999/sms/status_report");
        
        return HttpClientUtil.post(URL, headers, params, 100);
	}
}
