/**
 * 
 */
package com.huashi.hsboss.web.controller.mms;

import org.apache.commons.lang.StringUtils;

import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.hsboss.annotation.ActionMode;
import com.huashi.hsboss.annotation.AuthCode;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.EnumConstant;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.constant.OperCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.mms.passage.domain.MmsPassageAccess;
import com.huashi.mms.passage.service.IMmsPassageAccessService;
import com.huashi.mms.passage.service.IMmsPassageService;
import com.jfinal.ext.route.ControllerBind;

/**
 * 通道访问管理
 * 
 * @author Administrator
 */
@ControllerBind(controllerKey = "/mms/passage_access")
@ViewMenu(code = MenuCode.MENU_CODE_10003003)
public class MmsPassageAccessController extends BaseController {

    @BY_NAME
    private IMmsPassageAccessService iMmsPassageAccessService;
    @BY_NAME
    private IMmsPassageService       iMmsPassageService;
    @BY_NAME
    private IUserService             iUserService;

    @AuthCode(code = { OperCode.OPER_CODE_10003003001, OperCode.OPER_CODE_10003003002 })
    @ActionMode
    public void index() {
        String keyword = getPara("keyword");
        String userId = getPara("userId");
        int user = 0;
        if (StringUtils.isNotEmpty(userId)) {
            user = Integer.parseInt(userId);
        }
        BossPaginationVo<MmsPassageAccess> page = iMmsPassageAccessService.findPage(getPN(), keyword, user);
        if (StringUtils.isNotEmpty(keyword)) {
            setAttr("keyword", Integer.parseInt(keyword));
        }
        setAttr("userList", iUserService.findAll());
        setAttr("userId", user);
        setAttr("page", page);
        setAttr("passageList", iMmsPassageService.findAll());
    }

    @AuthCode(code = OperCode.OPER_CODE_10001003002)
    @ActionMode
    public void edit() {
        setAttr("passageList", iMmsPassageService.findAccessPassages(getParaToInt("groupId"), getParaToInt("cmcp"),
                                                                     getParaToInt("routeType")));
        long id = getParaToLong("id");
        MmsPassageAccess access = iMmsPassageAccessService.get((int) id);
        setAttr("mmsPassageAccess", access);
    }

    @AuthCode(code = OperCode.OPER_CODE_10001003002)
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void update() {
        MmsPassageAccess access = getModel(MmsPassageAccess.class, "mmsPassageAccess");
        renderJson(iMmsPassageAccessService.update(access));
    }

    @AuthCode(code = OperCode.OPER_CODE_10001003001)
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void loadingRedis() {
        renderResultJson(iMmsPassageAccessService.reload());
    }
}
