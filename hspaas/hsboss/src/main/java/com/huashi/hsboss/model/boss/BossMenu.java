/**
 * 
 */
package com.huashi.hsboss.model.boss;

import com.huashi.hsboss.model.base.BaseModel;
import com.jfinal.ext.plugin.tablebind.TableBind;

/**
 * @author ym
 * @created_at 2016年6月22日下午9:16:50
 */
@TableBind(tableName = "hsboss_menu", pkName = "id")
public class BossMenu extends BaseModel<BossMenu> {

	private static final long serialVersionUID = 2416465322971375893L;
	public static final BossMenu DAO = new BossMenu();
}
