package com.huashi.hsboss.model.boss;

import com.huashi.hsboss.model.base.BaseModel;
import com.jfinal.ext.plugin.tablebind.TableBind;

@TableBind(tableName = "hsboss_user", pkName = "id")
public class BossUser extends BaseModel<BossUser> {

	private static final long serialVersionUID = 3158724708403952584L;
	public static final BossUser DAO = new BossUser();
}
