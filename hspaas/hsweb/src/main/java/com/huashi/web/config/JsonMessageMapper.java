package com.huashi.web.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
  * TODO 定制json转化
  * @author zhengying
  * @version V1.0   
  * @date 2016年9月7日 下午12:29:15
 */
public class JsonMessageMapper extends ObjectMapper {

	private static final long serialVersionUID = -5211670742761214046L;

	/**
     * 构造函数
     */
    public JsonMessageMapper() {
        super();
        // json序列化时将number类型转成string，保证小数点.00的显示
        this.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        registerModule(simpleModule);

        // 空值处理为空串
//        this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
//            @Override
//            public void serialize(Object value, JsonGenerator jg, SerializerProvider sp)
//                    throws IOException, JsonProcessingException {
//                jg.writeString("");
//            }
//        });
    }
}
