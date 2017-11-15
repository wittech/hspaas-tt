package com.huashi.hsboss.service.boss;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huashi.hsboss.model.boss.BossRole;
import com.huashi.hsboss.model.boss.BossUser;
import com.huashi.hsboss.service.common.BaseService;
import com.huashi.hsboss.service.common.PageExt;

@Service
public class BossRoleService extends BaseService{
	
	
	public PageExt<BossRole> findPage(int pageNum){
		String sql = "from hsboss_role h order by h.id desc";
		return BossRole.DAO.findPage(pageNum, pageSize, "select h.* ", sql);
	}
	
	public List<BossRole> findAll(){
		return BossRole.DAO.find("select * from hsboss_role order by id desc");
	}
	
	public List<BossRole> getUserRoleList(int userId){
		String sql = "select r.* from hsboss_role r,hsboss_user_role_ref rf where r.id = rf.role_id and rf.user_id = ?";
		return BossRole.DAO.find(sql, userId);
	}
	
	public Map<String, Object> create(BossRole bossRole,String loginName){
		try {
			String roleName = bossRole.getStr("role_name");
			String sql = "select * from hsboss_role where role_name = ? limit 1";
			BossRole source = BossRole.DAO.findFirst(sql,roleName);
			if(source != null){
				return resultFail("角色名已存在，请重新输入！");
			}
			bossRole.set("created", loginName);
			bossRole.set("created_at", new Date());
			bossRole.save();
			return resultDefaultSuccess();
		} catch (Exception e) {
			return resultDefaultFail();
		}
	}
	
	public Map<String, Object> setAuth(int roleId,String operIds){
		//TODO this is set role oper or menu
		return resultDefaultFail();
	}
	
	public Map<String,Object> update(BossRole bossRole){
		try {
			String roleName = bossRole.getStr("role_name");
			String sql = "select * from hsboss_role where role_name = ? and id != ? limit 1";
			BossRole source = BossRole.DAO.findFirst(sql,roleName,bossRole.get("id"));
			if(source != null){
				return resultFail("角色名已存在，请重新输入！");
			}
			bossRole.update();
			return resultDefaultSuccess();
		} catch (Exception e) {
			return resultDefaultFail();
		}
	}
	
	public Map<String, Object> delete(int roleId){
		try {
			BossUser user = BossUser.DAO.findFirst("select * from hsboss_user_role_ref where role_id = ? limit 1",roleId);
			if(user != null){
				return resultFail("该角色下存在用户，无法删除!");
			}
			BossRole.DAO.deleteById(roleId);
			return resultDefaultSuccess();
		} catch (Exception e) {
			return resultDefaultFail();
		}
	}

}
