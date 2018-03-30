package com.huashi.sms.test;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.huashi.common.util.IdGenerator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-dubbo-consumer.xml"})
public class BaseTest {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	public IdGenerator idGenerator() {
		return new IdGenerator(1);
	}
	
	
}
