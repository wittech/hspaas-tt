package com.huashi.sms.config.rabbit;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.stereotype.Component;

/**
 * 
  * TODO 消息队列管理
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2017年3月19日 下午2:33:51
 */
@Component
public class RabbitMessageQueueManager {

	@Autowired
	private MappingJackson2MessageConverter messageConverter;
	@Resource
	private ConnectionFactory activeMQConnectionFactory;
	
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private DefaultListableBeanFactory defaultListableBeanFactory;
	
	@Value("${mq.consumers:10}")
	private int concurrentConsumers;
	
	@Value("${mq.consumers.direct:5}")
	private int directConcurrentConsumers;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 
	   * TODO 创建指定消费者数量消息队列
	   * 
	   * @param queueName
	   * 	队列名称
	   * @param isDirectProtocol
	   * 	是否为直连协议
	   * @param messageListener
	   * 	监听相关 
	 */
	public void createQueue(String queueName, boolean isDirectProtocol, MessageListener messageListener) {
		try {
			
			// 如果为直连协议则控制消费者为预设的个数，其他协议则默认消费者配置
			SimpleMessageListenerContainer container = this.messageListenerContainer(queueName, 
					isDirectProtocol ? directConcurrentConsumers : concurrentConsumers, 
					messageListener);
			container.afterPropertiesSet();

//	        container.addQueueNames(queueName);
//	        if(!container.isRunning()) {
//                container.start();
//            }
	        
	        container.start();
			
		} catch (Exception e) {
			logger.error("创建队列：{}失败", queueName, e);
		}
	}
	
	/**
	 * 
	   * TODO 移除队列
	   * @param queueName
	 */
	public void removeQueue(String queueName) {
	}
	
//	private Map<String, Object> setQueueFeatures() {
//		Map<String, Object> args = new HashMap<>();
//		// 最大优先级定义
//		args.put("x-max-priority", 10);
//		// 过期时间，单位毫秒
//		// args .put("x-message-ttl", 60000);
//		// 30分钟，单位毫秒
//		// args.put("x-expires", 1800000);
//		// 集群高可用，数据复制
//		args.put("x-ha-policy", "all");
//		return args;
//	}
	
	/**
	 * 
	   * TODO 声明队列消费者信息
	   * 
	   * @param queueName
	   * 	队列名称
	   * @param consumers
	   * 	消费者线程数量
	   * @param channelAwareMessageListener
	   * @return
	 */
	private SimpleMessageListenerContainer messageListenerContainer(String queueName, Integer consumers,
			MessageListener messageListener) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		
		container.setConnectionFactory(activeMQConnectionFactory);
//		container.setBeanName("jmsListeningContainer" + queueName);
		container.setConcurrentConsumers(consumers);
		container.setMessageConverter(messageConverter);
		
		container.setDestinationName(queueName);
		container.setPubSubDomain(true);
		
//		defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinition);
//		
//		applicationContext.getbean
		
//		container.shutdown();
		
		
		// 关键所在，指定线程池
//		ExecutorService service = Executors.newFixedThreadPool(10);
//		container.setTaskExecutor(service);

		// 设置是否自动启动
		container.setAutoStartup(true);
		// 设置拦截器信息
		// container.setAdviceChain(adviceChain);

		// 设置事务
		// container.setChannelTransacted(true);

		// 设置优先级
//		container.setConsumerArguments(Collections.<String, Object> singletonMap("x-priority", Integer.valueOf(10)));

		container.setupMessageListener(messageListener);
		container.setConcurrency("10-50");
		
		return container;
	}
	
}
