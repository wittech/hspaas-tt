package com.huashi.hsboss.web.controller.sms;

import com.huashi.common.user.domain.UserProfile;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.hsboss.annotation.ActionMode;
import com.huashi.hsboss.annotation.AuthCode;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject;
import com.huashi.hsboss.constant.EnumConstant;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.constant.OperCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.sms.settings.domain.SmsMobileWhiteList;
import com.huashi.sms.settings.service.ISmsMobileWhiteListService;
import com.jfinal.ext.route.ControllerBind;

import java.util.Map;

/**
 * TODO
 * Author youngmeng
 * Created 2016-10-14 15:28
 */
@ControllerBind(controllerKey = "/sms/white_list")
@ViewMenu(code= MenuCode.MENU_CODE_2004002)
public class WhiteListController extends BaseController {

    @Inject.BY_NAME
    private ISmsMobileWhiteListService iMobileWhiteListService;
    @Inject.BY_NAME
    private IUserService iUserService;

    @AuthCode(code= {OperCode.OPER_CODE_2004002001, OperCode.OPER_CODE_2004002002})
	@ActionMode
    public void index(){
        String keyword = getPara("keyword");
        BossPaginationVo<SmsMobileWhiteList> page = iMobileWhiteListService.findPage(getPN(),keyword);
        setAttr("page",page);
        setAttr("keyword",keyword);
    }

    @AuthCode(code= OperCode.OPER_CODE_2004002001)
	@ActionMode
    public void add(){

    }

    @AuthCode(code= OperCode.OPER_CODE_2004002001)
	@ActionMode(type = EnumConstant.ActionType.JSON)
    public void create(){
        SmsMobileWhiteList whiteList = getModel(SmsMobileWhiteList.class,"whiteList");
        Map<String,Object> result = iMobileWhiteListService.batchInsert(whiteList);
        renderResultJson(result.get("result_code").toString().equals("success"),result.get("result_msg").toString());
    }

    @AuthCode(code= OperCode.OPER_CODE_2004002002)
   	@ActionMode(type = EnumConstant.ActionType.JSON)
    public void delete(){
        int result = iMobileWhiteListService.deleteByPrimaryKey(getParaToInt("id"));
        renderResultJson(result > 0);
    }

    public void userList(){
        String fullName = getPara("fullName");
        String mobile = getPara("mobile");
        String company = getPara("company");
        String cardNo = getPara("cardNo");
        String state = getPara("state");
        BossPaginationVo<UserProfile> page = iUserService.findPage(getPN(),
                fullName, mobile, company,cardNo,state,null);
        page.setJumpPageFunction("userJumpPage");
        setAttr("fullName", fullName);
        setAttr("mobile", mobile);
        setAttr("company", company);
        setAttr("cardNo", cardNo);
        setAttr("state", state);
        setAttr("page", page);
        setAttr("userId", getParaToInt("userId",-1));
    }
}
