package com.huashi.hsboss.model.boss;

import com.huashi.hsboss.model.base.BaseModel;
import com.jfinal.ext.plugin.tablebind.TableBind;

@TableBind(tableName = "hsboss_user", pkName = "id")
public class BossUser extends BaseModel<BossUser> {

    /**
     * MFA二维码信息
     */
    private String               mfaQrcode;

    private static final long    serialVersionUID = 3158724708403952584L;
    public static final BossUser DAO              = new BossUser();

    public String getMfaQrcode() {
        return mfaQrcode;
    }

    public void setMfaQrcode(String mfaQrcode) {
        this.mfaQrcode = mfaQrcode;
    }

}
