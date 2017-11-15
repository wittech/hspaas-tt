
/**************************************************************************
* Copyright (c) 2015-2016 HangZhou Huashi, Inc.
* All rights reserved.
* 
* 项目名称：华时短信平台
* 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
*           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
*           识产权保护的内容。                            
***************************************************************************/
package com.huashi.bill.product.dao;

import java.util.Map;

/**
 * TODO 套餐产品报价dao接口类
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月17日 下午10:26:05
 */

public interface ComboProductMapper {
	
	int deleteByComboId(int id);
	
	int insert(Map<String, Object> paramMap);
}
