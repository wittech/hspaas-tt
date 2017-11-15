package com.huashi.hsboss.model.base;

import com.huashi.hsboss.service.common.PageExt;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

/**
 * 实体基类，所有与表绑定的实体都继承该类
 * 
 * @author ym
 * @created_at 2016年6月22日下午2:11:29
 */
public class BaseModel<M extends Model<?>> extends Model<M> {

	private static final long serialVersionUID = 7039549396734524537L;

	/**
	 * 
	   * TODO 分页查询
	   * @param pageNum
	   * 	当前页码
	   * @param pageSize
	   * 	每页显示条数
	   * @param select
	   * @param sqlExceptSelect
	   * @param paras
	   * 	参数
	   * @return
	 */
	public PageExt<M> findPage(int pageNum, int pageSize, String select,String sqlExceptSelect, Object... paras) {
		
		Page<M> page = super.paginate(pageNum, pageSize, select, sqlExceptSelect, paras);
		
		return new PageExt<M>(page.getList(), pageNum, pageSize, page.getTotalPage(), page.getTotalRow());
	}

	public PageExt<M> findPage(int pageNum, int pageSize, String select, String sqlExceptSelect) {
		Page<M> page = super.paginate(pageNum, pageSize, select, sqlExceptSelect);
		
		return new PageExt<M>(page.getList(), pageNum, pageSize, page.getTotalPage(), page.getTotalRow());
	}

	
}
