package com.huashi.mms.passage.service;

import java.util.List;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.mms.passage.domain.MmsPassageGroup;
import com.huashi.mms.passage.domain.MmsPassageGroupDetail;

/**
 * 
  * TODO 彩信通道组服务
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2019年1月14日 下午3:59:10
 */
public interface IMmsPassageGroupService {

	boolean create(MmsPassageGroup group);

	boolean update(MmsPassageGroup group);

	boolean deleteById(int id);

	BossPaginationVo<MmsPassageGroup> findPage(int pageNum, String keyword);

	MmsPassageGroup findById(int id);

	List<MmsPassageGroup> findAll();

	List<MmsPassageGroupDetail> findPassageByGroupId(int groupId);

	/**
	 * 将通道组下面的所有通道切换值参数通道
	 * 
	 * @param passageId
	 * @return
	 */
	boolean doChangeGroupPassage(int groupId, int passageId);
}
