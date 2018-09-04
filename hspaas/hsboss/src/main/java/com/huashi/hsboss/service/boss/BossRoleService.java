package com.huashi.hsboss.service.boss;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huashi.hsboss.model.boss.BossMenu;
import com.huashi.hsboss.model.boss.BossOper;
import com.jfinal.plugin.activerecord.Db;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.huashi.hsboss.model.boss.BossRole;
import com.huashi.hsboss.model.boss.BossUser;
import com.huashi.hsboss.service.common.BaseService;
import com.huashi.hsboss.service.common.PageExt;

@Service
public class BossRoleService extends BaseService{

	private static final Logger LOG = LoggerFactory.getLogger(BossRoleService.class);
	
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
			LOG.error("新增角色异常", e);
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
			LOG.error("修改角色异常", e);
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
			LOG.error("删除角色异常", e);
			return resultDefaultFail();
		}
	}

	public Map<String, Object> saveAuth(int roleId,String operIds){
		try {

			Db.update("delete from hsboss_role_oper_ref where role_id = ?", roleId);

			List<String> sqlList = new LinkedList<String>();
			String[] arrayOperIds = operIds.split(",");
			for(String operId : arrayOperIds) {
				StringBuffer sql = new StringBuffer();
				sql.append("insert into hsboss_role_oper_ref (role_id,oper_id) values (");
				sql.append(roleId);
				sql.append(",");
				sql.append(operId);
				sql.append(")");
				sqlList.add(sql.toString());
			}
			Db.batch(sqlList,sqlList.size());
			return resultDefaultSuccess();
		} catch (Exception e) {
			LOG.error("保存权限异常", e);
			return resultDefaultFail();
		}
	}

	public String getOperByRoleId(Integer roleId){
		//角色已有的操作权限
		Set<String> hasOper = new HashSet<String>();
		if(roleId != null){
			List<BossOper> operList = BossOper.DAO.find("select b.* from hsboss_role_oper_ref a,hsboss_oper b where a.oper_id = b.id and a.role_id=?", roleId);
			if(operList != null){
				for(BossOper oper: operList){
					hasOper.add(oper.getStr("oper_code"));
				}
			}
		}

		//所有菜单
		String menu_sql = "select * from hsboss_menu";
		List<BossMenu> allMenuList = BossMenu.DAO.find(menu_sql);
		JSONArray jsonarray = new JSONArray();
		if(allMenuList != null){
			JSONObject cldObj = null;
			for (BossMenu menu: allMenuList) {
				cldObj = new JSONObject();
				int id = menu.getInt("id");
				cldObj.put("id", "menu_"+ id);
				cldObj.put("name", menu.get("menu_name"));
				cldObj.put("pId", id == 0? menu.get("parent_id"): "menu_"+ menu.get("parent_id"));
				cldObj.put("open", menu.getInt("parent_id") <= 0);
				jsonarray.add(cldObj);
			}
		}
		//所有操作
		String oper_sql = "select * from hsboss_oper";
		List<BossOper> allOperList = BossOper.DAO.find(oper_sql);
		if(allOperList != null){
			JSONObject cldObj = null;
			String oper_code = null;
			for (BossOper oper: allOperList) {
				cldObj = new JSONObject();
				oper_code = oper.get("oper_code");
				cldObj.put("id", oper.getInt("id"));
				cldObj.put("name", oper.get("oper_name"));
				cldObj.put("pId", "menu_"+oper.get("menu_id"));
				cldObj.put("code", oper_code);

				if(hasOper.contains(oper_code)){
					cldObj.put("checked", true);
				}
				cldObj.put("open", false);
				jsonarray.add(cldObj);
			}
		}

		return jsonarray.toString();
	}

}
