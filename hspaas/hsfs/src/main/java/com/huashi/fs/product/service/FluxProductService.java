/**
 * 
 */
package com.huashi.fs.product.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.druid.util.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import com.huashi.common.third.util.MobileLocalUtil;
import com.huashi.common.util.MobileNumberCatagoryUtil;
import com.huashi.common.util.MobileNumberCatagoryUtil.MobileCatagory;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.fs.product.dao.FluxProductMapper;
import com.huashi.fs.product.domain.FluxProduct;

/**
 * 
  * TODO 产品服务实现类
  * @author lizheng
  * 
  * @version V1.0   
  * @date 2016年8月29日 下午4:50:00
 */
@Service
public class FluxProductService implements IFluxProductService {
	
	@Autowired
	private FluxProductMapper productMapper;
	private Logger logger = LoggerFactory.getLogger(FluxProductService.class);
	
	@Override
	public Map<String, Object> findListAll(String mobile) {
		if (StringUtils.isEmpty(mobile)) {
            {
                return null;
            }
        }
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 运营商code
		int operatorCode = CMCP.local(mobile).getCode();
		resultMap.put("result_operator", MobileLocalUtil.carrier(mobile));
		resultMap.put("result_operator_code", operatorCode);
		// 返回套餐数据
		resultMap.put("result_list", productMapper.selectAllist(operatorCode));
		return resultMap;
	}

	@Override
	public Map<String, Object> findListByMobile(String mobile) {
		try {
			MobileCatagory response = MobileNumberCatagoryUtil.doCatagory(mobile);
			if(response == null) {
                {
                    return null;
                }
            }
			
			if(!response.isSuccess()) {
				logger.info("号码分流失败");
				return null;
			}
			
			Map<String, Object> resultMap = new HashMap<String, Object>();
			if(response.getCmSize() > 0) {
                {
                    resultMap.put("mobile_list", productMapper.selectAllist(CMCP.CHINA_MOBILE.getCode()));
                }
            }
			
			if(response.getCtSize() > 0) {
                {
                    resultMap.put("telecom_list", productMapper.selectAllist(CMCP.CHINA_TELECOM.getCode()));
                }
            }
			
			if(response.getCuSize() > 0) {
                resultMap.put("unicom_list", productMapper.selectAllist(CMCP.CHINA_UNICOM.getCode()));
            }
				
			resultMap.put("number_report", response);
				
			return resultMap;
		} catch (Exception e) {
			logger.error("流量数据解析出错, 信息为： ", e.getMessage() );
		}
		return null;
	}

	@Override
	public List<FluxProduct> findByPackage(String parValue, int cmcp) {
		return productMapper.selectByParValue(parValue, cmcp);
	}

}
