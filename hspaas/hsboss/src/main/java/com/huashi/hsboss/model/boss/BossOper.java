/**
 * 
 */
package com.huashi.hsboss.model.boss;

import com.huashi.hsboss.model.base.BaseModel;
import com.jfinal.ext.plugin.tablebind.TableBind;

/**
 * @author ym
 * @created_at 2016年6月22日下午9:17:59
 */
@TableBind(tableName = "hsboss_oper", pkName = "id")
public class BossOper extends BaseModel<BossOper> {

	private static final long serialVersionUID = 2529520257349523001L;
	public static final BossOper DAO = new BossOper();
}
