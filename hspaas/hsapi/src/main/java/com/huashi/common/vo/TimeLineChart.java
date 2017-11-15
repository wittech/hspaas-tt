package com.huashi.common.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
  * TODO 曲线图工具
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2016年10月9日 下午11:00:53
 */
public class TimeLineChart implements Serializable {

	private static final long serialVersionUID = -6875750666460989846L;
	private String xlable; // 横轴分类
	private String lineType; // 多条趋势图线类型(如多个地市，蓝线代表泸州、红线为乐山)
	private int amount;

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getXlable() {
		return xlable;
	}

	public void setXlable(String xlable) {
		this.xlable = xlable;
	}

	public String getLineType() {
		return lineType;
	}

	public void setLineType(String lineType) {
		this.lineType = lineType;
	}

	/**
	 * 
	   * TODO 绘制
	   * 
	   * @param charts
	   * 	曲线图数据
	   * @param title
	   * 	曲线图大标题（横轴）
	   * @param ylable
	   * 	曲线图纵轴标题
	   * @param unit
	   * 	单位显示
	   * @return
	 */
	public static Map<String, Object> draw(List<TimeLineChart> charts, String title, String ylable, String unit) {
		Map<String, Object> container = new HashMap<String, Object>();
		List<String> xlables = new ArrayList<String>();
		Map<String, List<Integer>> data = new HashMap<String, List<Integer>>();
		for (TimeLineChart lc : charts) {
			if (!xlables.contains(lc.getXlable())) {
				xlables.add(lc.getXlable());
			}
			List<Integer> list = data.get(lc.getLineType());
			if (list == null) {
				list = new ArrayList<Integer>();
			}
			list.add(lc.getAmount());
			data.put(lc.getLineType(), list);
		}
		Map<String, Object> serie = null;
		List<Map<String, Object>> series = new ArrayList<Map<String, Object>>();
		for (String key : data.keySet()) {
			serie = new HashMap<String, Object>();
			serie.put("name", key);
			serie.put("data", data.get(key));
			series.add(serie);
		}
		container.put("title", title);
		container.put("ylable", ylable);
		container.put("xlable", xlables);
		container.put("unit", unit);
		container.put("series", series);
		return container;
	}

}