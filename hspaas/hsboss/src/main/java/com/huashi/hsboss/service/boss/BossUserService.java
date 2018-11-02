package com.huashi.hsboss.service.boss;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.huashi.hsboss.constant.SystemConstant;
import com.huashi.hsboss.model.boss.BossUser;
import com.huashi.hsboss.service.common.BaseService;
import com.huashi.hsboss.service.common.PageExt;
import com.huashi.hsboss.util.GoogleAuthenticator;
import com.huashi.hsboss.util.QrCodeUtils;
import com.jfinal.plugin.activerecord.Db;

@Service
public class BossUserService extends BaseService {

    /**
     * MFA域定义
     */
    private static final String MFA_DOMAIN = "hspaas.cn";

    /**
     * TODO 根据登录账号/手机号码/邮箱查询有效账户信息
     * 
     * @param loginName
     * @return
     */
    public BossUser findByLogin(String loginName) {
        String sql = "select * from hsboss_user h where (h.login_name = ? or h.mobile = ? or h.email = ?) and delete_flag = 0 limit 1";
        return BossUser.DAO.findFirst(sql, loginName, loginName, loginName);
    }

    /**
     * TODO 根据关键字查询用户信息（分页）
     * 
     * @param pageNum 分页码
     * @param keyword 关键字
     * @return
     */
    public PageExt<BossUser> findPage(int pageNum, String keyword) {
        String sql = "from hsboss_user h where h.delete_flag = 0 ";
        if (StringUtils.isNotBlank(keyword)) {
            sql += "and h.login_name like '%" + keyword + "%' ";
        }
        sql += "order by h.id desc";
        PageExt<BossUser> page = BossUser.DAO.findPage(pageNum, pageSize, "select h.* ", sql);
        if (page == null || CollectionUtils.isEmpty(page.getList())) {
            return null;
        }

        for (BossUser bossUser : page.getList()) {
            if (StringUtils.isEmpty(bossUser.getStr("mfa"))) {
                continue;
            }

            bossUser.setMfaQrcode(getMfaQrCodeImage(bossUser.getStr("login_name"), bossUser.getStr("mfa")));
        }

        return page;
    }

    /**
     * TODO 生成MFA二维码图片信息
     * 
     * @param loginName
     * @param secret
     * @return
     */
    private static String getMfaQrCodeImage(String loginName, String secret) {
        String qrCodeUrl = GoogleAuthenticator.getQRBarcodeURL(loginName, MFA_DOMAIN, secret);
        return QrCodeUtils.genQRCode(qrCodeUrl);
    }

    public Map<String, Object> create(BossUser bossUser, String created, String roleIds) {
        try {
            String loginName = bossUser.getStr("login_name");
            String sql = "select * from hsboss_user where login_name = ? limit 1";
            BossUser source = BossUser.DAO.findFirst(sql, loginName);
            if (source != null) {
                return resultFail("登录名已存在，请重新输入！");
            }

            // 生成谷歌MFA密钥
            String secret = GoogleAuthenticator.generateSecretKey();
            bossUser.set("mfa", secret);

            bossUser.set("password", DigestUtils.md5Hex(SystemConstant.DEFAULT_PASSWORD));
            bossUser.set("created", created);
            bossUser.set("created_at", new Date());
            bossUser.save();

            String[] arrayIds = roleIds.split(",");
            List<String> sqlList = new ArrayList<String>();
            for (String roleId : arrayIds) {
                int rId = Integer.parseInt(roleId);
                String roleSql = "insert into hsboss_user_role_ref (role_id,user_id) values (%d,%d)";
                roleSql = String.format(roleSql, rId, bossUser.getInt("id"));
                sqlList.add(roleSql);
            }
            Db.batch(sqlList, sqlList.size());
            return resultSuccess(getMfaQrCodeImage(loginName, secret));
        } catch (Exception e) {
            return resultDefaultFail();
        }
    }

    public Map<String, Object> updatePassword(int userId) {
        try {
            BossUser bossUser = BossUser.DAO.findById(userId);
            bossUser.set("password", DigestUtils.md5Hex(SystemConstant.DEFAULT_PASSWORD));
            bossUser.update();
            return resultSuccess("密码重置成功，已初始为系统默认密码！");
        } catch (Exception e) {
            return resultDefaultFail();
        }
    }

    /**
     * TODO 更新用户信息
     * 
     * @param bossUser
     * @param roleIds
     * @return
     */
    public Map<String, Object> update(BossUser bossUser, String roleIds) {
        try {
            String loginName = bossUser.getStr("login_name");
            String sql = "select * from hsboss_user where login_name = ? and id != ? limit 1";
            BossUser source = BossUser.DAO.findFirst(sql, loginName, bossUser.get("id"));
            if (source != null) {
                return resultFail("登录名已存在，请重新输入！");
            }
            bossUser.update();

            String deleteRole = "delete from hsboss_user_role_ref where user_id = ?";
            Db.update(deleteRole, bossUser.getInt("id"));
            String[] arrayIds = roleIds.split(",");
            List<String> sqlList = new ArrayList<String>();
            for (String roleId : arrayIds) {
                int rId = Integer.parseInt(roleId);
                String roleSql = String.format("insert into hsboss_user_role_ref (role_id,user_id) values (%d,%d)",
                                               rId, bossUser.getInt("id"));
                sqlList.add(roleSql);
            }
            Db.batch(sqlList, sqlList.size());
            return resultDefaultSuccess();
        } catch (Exception e) {
            return resultDefaultFail();
        }
    }

    public Map<String, Object> disabled(int userId, int flag) {
        try {
            BossUser user = BossUser.DAO.findFirst("select * from hsboss_user where id = ? limit 1", userId);
            if (user != null) {
                user.set("disabled_flag", flag);
                user.update();
                return resultDefaultSuccess();
            }
            return resultFail("用户不存在！");
        } catch (Exception e) {
            return resultDefaultFail();
        }
    }

    public Map<String, Object> delete(int userId) {
        try {
            BossUser user = BossUser.DAO.findFirst("select * from hsboss_user where id = ? limit 1", userId);
            if (user != null) {
                user.set("delete_flag", 1);
                user.update();
                return resultDefaultSuccess();
            }
            return resultFail("用户不存在！");
        } catch (Exception e) {
            return resultDefaultFail();
        }
    }

    public Map<String, Object> updateNewPassword(int userId, String originalPassword, String newPassword) {
        try {
            BossUser bossUser = BossUser.DAO.findById(userId);
            if (bossUser == null) {
                return resultFail("用户不存在！");
            }
            if (bossUser.getStr("password").toUpperCase().equals(DigestUtils.md5Hex(originalPassword).toUpperCase())) {
                bossUser.set("password", DigestUtils.md5Hex(newPassword).toUpperCase());
                bossUser.update();
                return resultSuccess("密码修改成功！");
            }
        } catch (Exception e) {
            return resultFail("修改密码异常！" + e.getMessage());
        }
        return resultFail("原密码输入错误！请重新输入！");
    }

    public Map<String, Object> updateMfa(int userId) {
        try {
            BossUser bossUser = BossUser.DAO.findById(userId);
            if (bossUser == null) {
                return resultFail("用户不存在！");
            }

            // 生成谷歌MFA密钥
            String secret = GoogleAuthenticator.generateSecretKey();
            bossUser.set("mfa", secret);

            bossUser.update();
            return resultSuccess("动态口令更新成功！");
        } catch (Exception e) {
            return resultFail("动态口令更新异常！" + e.getMessage());
        }
    }
}
