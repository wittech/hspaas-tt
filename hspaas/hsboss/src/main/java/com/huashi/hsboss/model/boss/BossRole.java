package com.huashi.hsboss.model.boss;

import com.huashi.hsboss.model.base.BaseModel;
import com.jfinal.ext.plugin.tablebind.TableBind;

@TableBind(tableName = "hsboss_role", pkName = "id")
public class BossRole extends BaseModel<BossRole> {

	private static final long serialVersionUID = 3641317145340954265L;
	public static final BossRole DAO = new BossRole();

}
