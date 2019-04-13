<header id="navbar">
    <div id="navbar-container" class="boxed">
        <div class="navbar-header">
            <a href="${BASE_PATH}/main" class="navbar-brand">
                <i class="fa fa-cube brand-icon"></i>
                <div class="brand-title">
                    <span class="brand-text">华时融合平台</span>
                </div>
            </a>
        </div>
        <div class="navbar-content clearfix">
            <ul class="nav navbar-top-links pull-left">
                <li class="tgl-menu-btn">
                    <a class="mainnav-toggle" href="#"> <i class="fa fa-navicon fa-lg"></i> </a>
                </li>
			<#assign topMenuList = session.menuList>
			<#list topMenuList as tl>
                <li <#if tl.id == session.leftMenu.id>class="open"</#if>>
                
                    <a href="${BASE_PATH}/main/top/${tl.id}">
                    	<#if tl.icon?? && tl.icon != "">
                    		<i class="fa ${(tl.icon)!}"></i>
                    	<#else>
                    		<i class="fa fa-dashboard"></i>
                    	</#if>
                        <span>${tl.menuName}</span>
                    </a>
                </li>
			</#list>
			<#--
            <li class="open">
                <a>
                    <i class="fa fa-folder-open-o"></i>
                    <span>基础信息</span>
                </a>
            </li>


            <li>
                <a>
                    <i class="fa fa-envelope-o"></i> 短信管理
                </a>
            </li>

            <li>
                <a>
                    <i class="fa fa-wifi"></i> 流量管理
                </a>
            </li>
            <li>
                <a>
                    <i class="fa fa-volume-up"></i> 语音管理
                </a>
            </li>
            <li>
                <a>
                    <i class="fa fa-gears"></i> 系统设置
                </a>
            </li>
            -->
            </ul>
            <ul class="nav navbar-top-links pull-right">
                <li id="dropdown-user" class="dropdown">
                    <a href="#" data-toggle="dropdown" class="dropdown-toggle text-right">
                        <div class="username hidden-xs" style="font-size:13px;font-weight:100">${session.realName?if_exists}</div>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right with-arrow">

                        <!-- User dropdown menu -->
                        <ul class="head-list">
                            <li>
                                <a href="#"> <i class="fa fa-user fa-fw fa-lg"></i> 个人信息 </a>
                            </li>
                            <li>
                                <a href="javascript:void(0);" onclick="openEditPasswordWin();"> <i class="fa fa-key fa-fw fa-lg"></i> 修改密码 </a>
                            </li>
                            <li>
                                <a href="#"> <i class="fa fa-gear fa-fw fa-lg"></i> 个人设置 </a>
                            </li>
                            <li>
                                <a href="javascript:void(0);" onclick="logout();"> <i class="fa fa-sign-out fa-fw"></i>退出 </a>
                            </li>
                        </ul>
                    </div>
                </li>
                <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
                <!--Navigation toogle button
                <a id="demo-toggle-aside" href="#"> <i class="fa fa-navicon fa-lg"></i> </a>
            </li> 
            <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->

            </ul>
        </div>
    </div>

</header>
 <script src="${BASE_PATH}/resources/js/common.js"></script>
 <link href="${BASE_PATH}/resources/js/confirm/jquery-confirm.css" rel="stylesheet">
<link href="${BASE_PATH}/resources/css/bootstrap/font-awesome.min.css" rel="stylesheet">
<div class="modal fade" id="passwordModal">
    <div class="modal-dialog" style="width:600px">
        <div class="modal-content" style="width:600px">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">修改密码</h4>
            </div>
            <div class="modal-body" id="passwordModelBody">
                <form id="passwordform" class="form-horizontal">
                <div class="form-group">
                    <label class="col-xs-3 control-label">账号</label>
                    <div class="col-xs-6">
                        <input type="text" class="form-control" value="${session.loginName?if_exists}" readonly="readonly" disabled/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-xs-3 control-label">原密码</label>
                    <div class="col-xs-6">
                        <input type="password" class="form-control" name="originalPassword" id="originalPassword" placeholder="请输入原密码"/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-xs-3 control-label">新密码</label>
                    <div class="col-xs-6">
                        <input type="password" class="form-control" name="newPassword" id="newPassword" placeholder="请输入新密码"/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-xs-3 control-label">确认密码</label>
                    <div class="col-xs-6">
                        <input type="password" class="form-control" name="secondaryNewPassword" id="secondaryNewPassword" placeholder="再次输入确认密码"/>
                    </div>
                </div>
            </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-success" onclick="passwordFormSubmit();">确定</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div>


<div id="pop" style="display:none;">
    <div id="popHead">
        <a id="popClose" title="关闭">关闭</a>
        <h2>温馨提示</h2>
    </div>
    <div id="popContent">
        <dl>
            <dt id="popTitle"><a href="#"></a></dt>
            <dd id="popIntro"></dd>
        </dl>
        <p id="popMore"><a href="#">查看 »</a></p>
    </div>
</div>

<script type="text/javascript">
    function logout(){
        if(confirm('确定要退出吗？')){
            location.href = "${BASE_PATH}/account/exit"
        }
    }

    function openEditPasswordWin(){
        $('#passwordModal').modal();
    }

    function passwordFormSubmit(){
        var originalPassword = $('#originalPassword').val();
        if($.trim(originalPassword) == ''){
            Boss.alert("请输入原密码！");
            return;
        }
        var newPassword = $('#newPassword').val();
        if($.trim(newPassword) == ''){
            Boss.alert("请输入新密码！");
            return;
        }
        var secondaryNewPassword = $('#secondaryNewPassword').val();
        if($.trim(secondaryNewPassword) == ''){
            Boss.alert("请输入确认密码！");
            return;
        }
        if($("#newPassword").val().trim() !=$("#secondaryNewPassword").val().trim()){
            Boss.alert("两次密码输入不一致！请重新输入！");
            return;
        }
        $.ajax({
            url:'${BASE_PATH}/boss/user/updatePassword',
            dataType:'json',
            data:$('#passwordform').serialize(),
            type:'post',
            success:function(data){
                if(data.result){
                    Boss.alertToCallback('修改成功！',function(){
                        location.reload();
                    });
                }else{
                    Boss.alert(data.message);
                }
            },error:function(data){
                Boss.alert('修改失败!请稍后重试!');
            }
        });
    }

    setInterval(function(){
        getWaitSmsTaskCount();
    }, 5 * 60 * 1000);

    function getWaitSmsTaskCount(){
        $.ajax({
            url:'${BASE_PATH}/common/message_info',
            dataType:'json',
            success:function(data){
                var count = parseInt(data.wait_task_count);
                if(count >= 20){
                    var title = count+'条待处理的短信任务';
                    var url = '${BASE_PATH}/sms/record/under_way_list';
                    var content = '融合平台的待处理短信任务已经有<font color="red">'+count+'</font>条了，快去处理吧！';
                    var pop=new Pop(title,url,content);
                }

            },error:function(data){
            }
        })
    }

</script>
