package com.huashi.common.user.service;

import java.util.List;
import java.util.Set;

import com.huashi.common.settings.domain.PushConfig;
import com.huashi.common.user.domain.User;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.user.domain.UserFluxDiscount;
import com.huashi.common.user.domain.UserProfile;
import com.huashi.common.user.model.UserModel;
import com.huashi.common.vo.BossPaginationVo;

/**
 * 
 * TODO 融合平台用户接口
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年8月28日 下午8:56:19
 */
public interface IUserService {

	/**
	 * 
	 * TODO 添加用户
	 * 
	 * @param user
	 * @param profile
	 * @return
	 */
	UserDeveloper save(User user, UserProfile profile);

	/**
	 * 
	 * TODO 获取全部用户
	 * 
	 * @return
	 */
	List<User> findAll();

	/**
	 * 
	 * TODO 获取全部用户基础信息
	 * 
	 * @return
	 */
	List<UserProfile> findAllUserProfile();

	/**
	 * 
	 * TODO 根据邮箱判断用户是否存在
	 * 
	 * @param email
	 * @return
	 */
	boolean isUserExistsByEmail(String email);

	/**
	 * 
	 * TODO 根据手机号码判断用户是否存在
	 * 
	 * @param mobile
	 * @return
	 */
	boolean isUserExistsByMobile(String mobile);

	/**
	 * 
	 * TODO 根据邮箱或手机号码获取用户信息
	 * 
	 * @param username
	 * @return
	 */
	User getByUsername(String username);

	User getByEmail(String email);

	User getByMobile(String mobile);

	/**
	 * 
	 * TODO 获取用户账号信息
	 * 
	 * @param userId
	 * @return
	 */
	User getById(int userId);

	/**
	 * 
	 * TODO 根据用户名和密码获取用户信息
	 * 
	 * @param username
	 *            电子邮箱或者手机号码
	 * @param password
	 *            密码
	 * @return
	 */
	boolean verify(String username, String password);

	/**
	 * 
	 * TODO 根据用户获取用户基础信息
	 * 
	 * @param userId
	 * @return
	 */
	UserProfile getProfileByUserId(int userId);

	/**
	 * 
	 * TODO 修改
	 * 
	 * @param id
	 * @return
	 */
	boolean updateByUserId(UserProfile record);

	/**
	 * 分页查询基本信息
	 * 
	 * @param pageNum
	 * @param fullName
	 * @param mobile
	 * @param company
	 * @return
	 */
	BossPaginationVo<UserProfile> findPage(int pageNum, String fullName, String mobile, String company, String cardNo,
			String state, String appkey);

	/**
	 * 
	 * TODO 更新用户状态
	 * 
	 * @param userId
	 * @param flag
	 * @return
	 */
	boolean changeStatus(int userId, String flag);

	/**
	 * 
	 * TODO 更新用户账号信息
	 * 
	 * @param user
	 * @return
	 */
	boolean updateUserInfo(User user, UserDeveloper developer);

	/**
	 * 
	 * TODO 更新用户基础信息
	 * 
	 * @param userProfile
	 * @return
	 */
	boolean updateUserProfile(UserProfile userProfile);

	/**
	 * 
	 * TODO 更新用户短信相关信息
	 * 
	 * @param userId
	 *            用户ID
	 * @param balance
	 *            短信剩余条数
	 * @param pushConfigList
	 *            短信推送配置信息（下行报告和上行报告）
	 * @return
	 */
	boolean updateSms(int userId, String balance, int payType, List<PushConfig> pushConfigList, int passageGroupId);

	/**
	 * 
	 * TODO 更新用户流量信息
	 * 
	 * @param userId
	 *            用户ID
	 * @param balance
	 *            流量余额（金额）
	 * @param pushConfig
	 *            流量充值结果推送配置
	 * @param userFluxDiscount
	 *            用户流量折扣信息
	 * @return
	 */
	boolean updateFs(int userId, String balance, int payType, PushConfig pushConfig, UserFluxDiscount userFluxDiscount,
			int passageGroupId);

	/**
	 * 
	 * TODO 更新语音信息
	 * 
	 * @param userId
	 *            用户ID
	 * @param balance
	 *            语音条数
	 * @param pushConfig
	 *            语音发送状态报告配置信息
	 * @return
	 */
	boolean updateVs(int userId, String balance, int payType, PushConfig pushConfig, int passageGroupId);

	/**
	 * 修改登录密码
	 * 
	 * @param userId
	 *            用户编号
	 * @param plainPassword
	 *            用户原密码
	 * @param newPassword
	 *            用户新密码
	 * @return
	 */
	boolean updatePasword(int userId, String plainPassword, String newPassword);

	/**
	 * 原密码校验
	 * 
	 * @param userId
	 * @param password
	 * @return
	 */
	boolean passwordExists(int userId, String password);

	/**
	 * 手机号码变更
	 * 
	 * @param user
	 * @return
	 */
	boolean updateByPrimaryKeySelective(User user);

	/**
	 * 
	 * TODO 根据用户ID获取用户关系数据
	 * 
	 * @param userId
	 * @return
	 */
	UserModel getByUserId(int userId);

	/**
	 * 
	 * TODO 保存或修改用户关系数据
	 * 
	 * @param userModel
	 * @return
	 */
	boolean saveUserModel(UserModel userModel);

	/**
	 * 
	 * TODO 重载用户关系数据到REDIS
	 * 
	 * @return
	 */
	boolean reloadModelToRedis();

	/**
	 * 
	 * TODO 查找用户关联信息
	 * 
	 * @return
	 */
	List<UserModel> findUserModels();

	/**
	 * 
	 * TODO 获取所有有效的用户IDS
	 * 
	 * @return
	 */
	Set<Integer> findAvaiableUserIds();

	/**
	 * 密码串获取
	 * 
	 * @return
	 */
	UserDeveloper generatePassword();
}
