package com.huashi.sms.config.rabbit;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.huashi.sms.task.context.MQConstant;

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

	@Resource
	private AmqpAdmin amqpAdmin;
	@Resource
	private MessageConverter messageConverter;
	@Resource
	private ConnectionFactory rabbitConnectionFactory;
	
	@Value("${mq.rabbit.consumers}")
	private int concurrentConsumers;
	@Value("${mq.rabbit.maxconsumers}")
	private int maxConcurrentConsumers;
	
	@Value("${mq.rabbit.consumers.direct:5}")
	private int directConcurrentConsumers;
	
//	@Value("${mq.rabbit.threadnum}")
//	private int rabbitPrefetchCount;
	
	@Value("${mq.rabbit.prefetch}")
	private int rabbitPrefetchCount;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
//	@Resource
//	private SimpleMessageListenerContainer simpleMessageListenerContainer;

	/**
	 * 
	   * TODO 创建队列
	   * 
	   * @param queueName
	   * @param channelAwareMessageListener
	 */
	public void createQueue(String queueName, ChannelAwareMessageListener channelAwareMessageListener) {
		createQueue(queueName, false, channelAwareMessageListener);
	}
	/**
	 * 
	   * TODO 创建指定消费者数量消息队列
	   * 
	   * @param queueName
	   * 	队列名称
	   * @param isDirectProtocol
	   * 	是否为直连协议
	   * @param channelAwareMessageListener
	   * 	监听相关 
	 */
	public void createQueue(String queueName, boolean isDirectProtocol, ChannelAwareMessageListener channelAwareMessageListener) {
		try {
			DirectExchange exchange = new DirectExchange(MQConstant.EXCHANGE_SMS, true, false);
			amqpAdmin.declareExchange(exchange);

			Queue queue = new Queue(queueName, true, false, false, setQueueFeatures());
			Binding smsWaitProcessBinding = BindingBuilder.bind(queue).to(exchange).with(queueName);
			amqpAdmin.declareQueue(queue);
			amqpAdmin.declareBinding(smsWaitProcessBinding);
			
			// 如果为直连协议则控制消费者为预设的个数，其他协议则默认消费者配置
			SimpleMessageListenerContainer container = this.messageListenerContainer(queueName, 
					isDirectProtocol ? directConcurrentConsumers : concurrentConsumers, 
					channelAwareMessageListener);
			container.afterPropertiesSet();

//	        container.addQueueNames(queueName);
	        if(!container.isRunning())
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
		amqpAdmin.deleteQueue(queueName);
	}
	
	/**
	 * 
	   * TODO 判断队列是否存在
	   * 
	   * @param queueName
	   * @return
	 */
	public boolean isQueueExists(String queueName) {
		return amqpAdmin.getQueueProperties(queueName) != null;
	}

	private Map<String, Object> setQueueFeatures() {
		Map<String, Object> args = new HashMap<>();
		// 最大优先级定义
		args.put("x-max-priority", 10);
		// 过期时间，单位毫秒
		// args .put("x-message-ttl", 60000);
		// 30分钟，单位毫秒
		// args.put("x-expires", 1800000);
		// 集群高可用，数据复制
		args.put("x-ha-policy", "all");
		return args;
	}
	
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
	public SimpleMessageListenerContainer messageListenerContainer(String queueName, Integer consumers, 
			ChannelAwareMessageListener channelAwareMessageListener) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(rabbitConnectionFactory);
		container.setConcurrentConsumers(consumers);
		container.setMaxConcurrentConsumers(maxConcurrentConsumers);
		container.setPrefetchCount(rabbitPrefetchCount);
		container.setMessageConverter(messageConverter);
		container.addQueueNames(queueName);
		
		// 关键所在，指定线程池
//		ExecutorService service = Executors.newFixedThreadPool(10);
//		container.setTaskExecutor(service);

		// 设置是否自动启动
		container.setAutoStartup(true);
		// 设置拦截器信息
		// container.setAdviceChain(adviceChain);

		// 设置事务
		// container.setChannelTransacted(true);

		// 设置公平分发，同 channel.basicQos(1);
		container.setPrefetchCount(rabbitPrefetchCount);

		// 设置优先级
//		container.setConsumerArguments(Collections.<String, Object> singletonMap("x-priority", Integer.valueOf(10)));
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 设置确认模式手工确认
		
		container.setMessageListener(channelAwareMessageListener);
		
		return container;
	}
	
}
