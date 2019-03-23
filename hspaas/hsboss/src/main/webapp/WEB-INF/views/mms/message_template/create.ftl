<!DOCTYPE html>
<html lang="en">

<head>
    <meta http-equiv="content-type" content="text/html;charset=UTF-8"/>
    <meta charset="utf-8">

    <title>融合平台</title>
    <link href="${BASE_PATH}/resources/css/bootstrap/bootstrap.min.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/bootstrap/style.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/js/confirm/jquery-confirm.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/bootstrap/font-awesome.min.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/bootstrap/pace.min.css" rel="stylesheet">
    <script src="${BASE_PATH}/resources/js/bootstrap/pace.min.js"></script>
    <script src="${BASE_PATH}/resources/js/common.js"></script>
    <#include "/WEB-INF/views/common/select_search.ftl">
</head>

<body>
<div id="container" class="effect mainnav-lg navbar-fixed mainnav-fixed">
    <#include "/WEB-INF/views/main/top.ftl">
    <div class="boxed">

        <div id="content-container">

            <div class="pageheader">
                <div class="breadcrumb-wrapper"><span class="label">所在位置:</span>
                    <ol class="breadcrumb">
                        <li><a href="#"> 管理平台 </a></li>
                        <li><a href="#"> 彩信管理 </a></li>
                        <li class="active">模板添加111333</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
                <div class="row">
                    <div class="col-md-12">
                        <section class="panel">
                            <div class="panel-heading">
                                <h3 class="panel-title"> Simple Form wizard  </h3>
                            </div>
                            <div class="panel-body">
                                <!-- START Form Wizard -->
                                <form class="form-horizontal form-bordered form-wizard clearfix" action="#" id="wizard" role="application"><div class="steps clearfix"><ul role="tablist"><li role="tab" class="first current" aria-disabled="false" aria-selected="true"><a id="wizard-t-0" href="#wizard-h-0" aria-controls="wizard-p-0"><span class="current-info audible">current step: </span><span class="number">1.</span><span class="title"> Login </span></a></li><li role="tab" class="disabled" aria-disabled="true"><a id="wizard-t-1" href="#wizard-h-1" aria-controls="wizard-p-1"><span class="number">2.</span><span class="title"> General information </span></a></li><li role="tab" class="disabled" aria-disabled="true"><a id="wizard-t-2" href="#wizard-h-2" aria-controls="wizard-p-2"><span class="number">3.</span><span class="title"> Education </span></a></li><li role="tab" class="disabled last" aria-disabled="true"><a id="wizard-t-3" href="#wizard-h-3" aria-controls="wizard-p-3"><span class="number">4.</span><span class="title"> Work experience </span></a></li></ul></div><div class="content clearfix">
                                        <!-- Wizard Container 1 -->
                                        <div class="wizard-title title current" id="wizard-h-0" tabindex="-1"> Login </div>
                                        <div class="wizard-container body current" id="wizard-p-0" role="tabpanel" aria-labelledby="wizard-h-0" aria-hidden="false" style="display: block;">
                                            <div class="form-group">
                                                <div class="col-md-12">
                                                    <h4 class="text-primary"> <i class="fa fa-sign-in"></i> Login Details </h4>
                                                    <p class="text-muted"> Enter Your Login Details </p>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="col-sm-2 control-label"> User ID : </label>
                                                <div class="col-sm-6">
                                                    <input class="form-control" name="name" type="text" placeholder="Type your Name">
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="col-sm-2 control-label"> Email Address : </label>
                                                <div class="col-sm-6">
                                                    <input class="form-control" name="name" type="email" placeholder="Type your Email">
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="col-sm-2 control-label"> Password : </label>
                                                <div class="col-sm-6">
                                                    <input class="form-control" name="name" type="password" placeholder="Type your password">
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="col-sm-2 control-label"> Re-Password : </label>
                                                <div class="col-sm-6">
                                                    <input class="form-control" name="name" type="password" placeholder="Type your password">
                                                </div>
                                            </div>
                                        </div>
                                        <!--/ Wizard Container 1 -->
                                        <!-- Wizard Container 2 -->
                                        <div class="wizard-title title" id="wizard-h-1" tabindex="-1"> General information </div>
                                        <div class="wizard-container body" id="wizard-p-1" role="tabpanel" aria-labelledby="wizard-h-1" aria-hidden="true" style="display: none;">
                                            <div class="form-group">
                                                <div class="col-md-12">
                                                    <h4 class="semibold text-primary"> <i class="fa fa-user"></i> General information </h4>
                                                    <p class="text-muted"> General information about applicant </p>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <div class="row">
                                                    <div class="col-md-6">
                                                        <label>First name: <span class="text-danger">*</span> </label>
                                                        <input type="text" name="First-name" class="form-control" placeholder="First Name">
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label>Last Name: <span class="text-danger">*</span></label>
                                                        <input type="text" name="Last-name" class="form-control" placeholder="Last Name">
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <div class="row">
                                                    <div class="col-md-6">
                                                        <label>Phone #:</label>
                                                        <input type="text" placeholder="+99-99-9999-9999" data-mask="+99-99-9999-9999" class="form-control">
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label>Date of birth:</label>
                                                        <input type="text" placeholder="99/99/9999" data-mask="99/99/9999" class="form-control">
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <div class="row">
                                                    <div class="col-md-12">
                                                        <label>Select State: </label>
                                                        <select class="form-control" id="source">
                                                            <option value="AK">Alaska</option>
                                                            <option value="HI">Hawaii</option>
                                                            <option value="CA">California</option>
                                                            <option value="NV">Nevada</option>
                                                            <option value="OR">Oregon</option>
                                                            <option value="WA">Washington</option>
                                                            <option value="AZ">Arizona</option>
                                                            <option value="CO">Colorado</option>
                                                            <option value="ID">Idaho</option>
                                                            <option value="MT">Montana</option>
                                                            <option value="NE">Nebraska</option>
                                                            <option value="NM">New Mexico</option>
                                                            <option value="ND">North Dakota</option>
                                                            <option value="UT">Utah</option>
                                                            <option value="WY">Wyoming</option>
                                                            <option value="AL">Alabama</option>
                                                            <option value="AR">Arkansas</option>
                                                            <option value="IL">Illinois</option>
                                                            <option value="IA">Iowa</option>
                                                            <option value="KS">Kansas</option>
                                                            <option value="KY">Kentucky</option>
                                                            <option value="LA">Louisiana</option>
                                                        </select>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <!--/ Wizard Container 2 -->
                                        <!-- Wizard Container 3 -->
                                        <div class="wizard-title title" id="wizard-h-2" tabindex="-1"> Education </div>
                                        <div class="wizard-container body" id="wizard-p-2" role="tabpanel" aria-labelledby="wizard-h-2" aria-hidden="true" style="display: none;">
                                            <div class="form-group">
                                                <div class="col-md-12">
                                                    <h4 class="semibold text-primary"> <i class="fa fa-book"></i> Education </h4>
                                                    <p class="text-muted"> Where and when did you get your degree </p>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <div class="row">
                                                    <div class="col-md-6">
                                                        <label>University: </label>
                                                        <input type="text" name="University" class="form-control" placeholder="University Name">
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label> Country: </label>
                                                        <input type="text" name="University-Country" class="form-control" placeholder="Choose a Country">
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <div class="row">
                                                    <div class="col-md-6">
                                                        <label> Degree level: </label>
                                                        <input type="text" name="Bachelor, Master etc.." class="form-control" placeholder="Bachelor, Master etc..">
                                                    </div>
                                                    <div class="col-md-3">
                                                        <label> From: </label>
                                                        <div class="row">
                                                            <div class="col-sm-8">
                                                                <select name="month" class="form-control">
                                                                    <option value="">Month</option>
                                                                    <option value="1">January</option>
                                                                    <option value="2">February</option>
                                                                    <option value="3">March</option>
                                                                    <option value="4">April</option>
                                                                    <option value="5">May</option>
                                                                    <option value="6">June</option>
                                                                    <option value="7">July</option>
                                                                    <option value="8">August</option>
                                                                    <option value="9">September</option>
                                                                    <option value="10">October</option>
                                                                    <option value="11">November</option>
                                                                    <option value="12">December</option>
                                                                </select>
                                                            </div>
                                                            <div class="col-sm-4">
                                                                <input type="text" name="University-Country" class="form-control" placeholder="Year">
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <label> To: </label>
                                                        <div class="row">
                                                            <div class="col-sm-8">
                                                                <select name="month" class="form-control">
                                                                    <option value="">Month</option>
                                                                    <option value="1">January</option>
                                                                    <option value="2">February</option>
                                                                    <option value="3">March</option>
                                                                    <option value="4">April</option>
                                                                    <option value="5">May</option>
                                                                    <option value="6">June</option>
                                                                    <option value="7">July</option>
                                                                    <option value="8">August</option>
                                                                    <option value="9">September</option>
                                                                    <option value="10">October</option>
                                                                    <option value="11">November</option>
                                                                    <option value="12">December</option>
                                                                </select>
                                                            </div>
                                                            <div class="col-sm-4">
                                                                <input type="text" name="University-Country" class="form-control" placeholder="Year">
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <!--/ Wizard Container 3 -->
                                        <!-- Wizard Container 4 -->
                                        <div class="wizard-title title" id="wizard-h-3" tabindex="-1"> Work experience </div>
                                        <div class="wizard-container body" id="wizard-p-3" role="tabpanel" aria-labelledby="wizard-h-3" aria-hidden="true" style="display: none;">
                                            <div class="form-group">
                                                <div class="col-md-12">
                                                    <h4 class="semibold text-primary"> <i class="fa fa-cog"></i> Work experience </h4>
                                                    <p class="text-muted"> Let us know about your work experience </p>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <div class="row">
                                                    <div class="col-md-6">
                                                        <label>Company: </label>
                                                        <input type="text" name="Work experience" class="form-control" placeholder="Work experience">
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label> Country: </label>
                                                        <input type="text" name="Country" class="form-control" placeholder="Choose a Country">
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <div class="row">
                                                    <div class="col-md-6">
                                                        <label> Position: </label>
                                                        <input type="text" name="Your Position" class="form-control" placeholder="Your Position">
                                                    </div>
                                                    <div class="col-md-3">
                                                        <label> From: </label>
                                                        <div class="row">
                                                            <div class="col-sm-8">
                                                                <select name="month" class="form-control">
                                                                    <option value="">Month</option>
                                                                    <option value="1">January</option>
                                                                    <option value="2">February</option>
                                                                    <option value="3">March</option>
                                                                    <option value="4">April</option>
                                                                    <option value="5">May</option>
                                                                    <option value="6">June</option>
                                                                    <option value="7">July</option>
                                                                    <option value="8">August</option>
                                                                    <option value="9">September</option>
                                                                    <option value="10">October</option>
                                                                    <option value="11">November</option>
                                                                    <option value="12">December</option>
                                                                </select>
                                                            </div>
                                                            <div class="col-sm-4">
                                                                <input type="text" name="University-Country" class="form-control" placeholder="Year">
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <label> To: </label>
                                                        <div class="row">
                                                            <div class="col-sm-8">
                                                                <select name="month" class="form-control">
                                                                    <option value="">Month</option>
                                                                    <option value="1">January</option>
                                                                    <option value="2">February</option>
                                                                    <option value="3">March</option>
                                                                    <option value="4">April</option>
                                                                    <option value="5">May</option>
                                                                    <option value="6">June</option>
                                                                    <option value="7">July</option>
                                                                    <option value="8">August</option>
                                                                    <option value="9">September</option>
                                                                    <option value="10">October</option>
                                                                    <option value="11">November</option>
                                                                    <option value="12">December</option>
                                                                </select>
                                                            </div>
                                                            <div class="col-sm-4">
                                                                <input type="text" name="University-Country" class="form-control" placeholder="Year">
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- Wizard Container 4 -->
                                    </div><div class="actions clearfix"><ul role="menu" aria-label="Pagination"><li class="disabled" aria-disabled="true"><a href="#previous" role="menuitem" class="btn btn-default">Previous</a></li><li aria-hidden="false" aria-disabled="false"><a href="#next" role="menuitem" class="btn btn-default">Next</a></li><li aria-hidden="true" style="display: none;"><a href="#finish" role="menuitem" class="btn btn-primary">Finish</a></li></ul></div></form>
                                <!--/ END Form Wizard -->
                            </div>
                        </section>
                    </div>
                </div>


                <div class="panel">
                    <!-- Panel heading -->
                    <div class="panel-heading">
                        <h3 class="panel-title">模板添加</h3>
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                        <div class="panel-body">
                            <div class="form-group">
                                <label class="col-xs-2 control-label">开户用户</label>
                                <div class="col-xs-4">
                                    <select id="userId" name="messageTemplate.userId"
                                            class="form-control selectpicker show-tick" data-live-search="true">
                                        <#if userList??>
                                            <#list userList as u>
                                                <option value="${u.userId!''}"
                                                        <#if userId?? && u.userId==userId>selected<#elseif task?? && task.userId?? && u.userId==task.userId>selected</#if>>${u.name!''}
                                                    -${u.username!''}</option>
                                            </#list>
                                        </#if>
                                    </select>
                                </div>
                                <label class="col-xs-2 control-label">路由类型</label>
                                <div class="col-xs-4">
                                    <select id="type" name="messageTemplate.routeType" class="form-control">
                                        <#if routeTypes??>
                                            <#list routeTypes as a>
                                                <option value="${a.getValue()!''}">${a.getName()!''}</option>
                                            </#list>
                                        </#if>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-2 control-label">模版名称</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required]" name="messageTemplate.name" id="name"
                                           placeholder="请输入模版名称，方便后续快速检索">
                                </div>
                                <label class="col-xs-2 control-label">模版标题</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required]" name="messageTemplate.title" id="title"
                                           placeholder="请输入模版标题">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-2 control-label">提交时间间隔</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,maxSize[7],custom[number]]"
                                           name="messageTemplate.submitInterval" id="submitInterval" value="30"
                                           placeholder="请输入彩信提交时间间隔（同一号码）">
                                </div>
                                <label class="col-xs-2 control-label">提交次数上限</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,maxSize[5],custom[number]]"
                                           name="messageTemplate.limitTimes" id="limitTimes" value="10"
                                           placeholder="请输入彩信每天提交次数上限（同一号码）">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-2 control-label">优先级</label>
                                <div class="col-xs-4">
                                    <input type="text"
                                           class="form-control validate[required,maxSize[10],custom[number],min[0]]"
                                           name="messageTemplate.priority" id="priority" value="5"
                                           placeholder="请输入模板优先级（越大越优先）">
                                </div>
                                <label class="col-xs-2 control-label">扩展号码</label>
                                <div class="col-xs-4">
                                    <input type="text" style="width: 590px;" class="form-control validate[maxSize[20]]"
                                           name="messageTemplate.extNumber" id="extNumber" placeholder="模板扩展号码"/>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-xs-16 col-xs-offset-3">
                                    <button type="buton" onclick="formSubmit();" class="btn btn-primary btn-sm" name="buttonSubmit">提交
                                    </button>
                                </div>
                            </div>
                        </div>
                    </form>


                    <div class="form-group" style="display:none" id="contentTemplateHtml">
                        <label class="col-xs-2 control-label">模板内容</label>
                        <div class="col-xs-6">
                                    <textarea class="form-control validate[required,maxSize[1000]]"
                                              name="content" rows="3"></textarea>
                        </div>
                        <div class="col-xs-1">
                            <a href="javascript:void(0);" onclick="removeContent(this);"
                               class="btn btn-danger btn-sm">移除</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <#include "/WEB-INF/views/main/left.ftl">
    </div>
</div>
</body>
<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script>
<script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script>
<script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
<#include "/WEB-INF/views/common/form_validation.ftl">
<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
<script type="text/javascript">
    $(function () {
        $('#myform').validationEngine('attach', {promptPosition: "topRight"});

        $('.blacklist').click(function () {
            $('#ignoreBlacklist').val($(this).val());
        });

        $('.fwords').click(function () {
            $('#ignoreForbiddenWords').val($(this).val());
        });
    });

    function formSubmit() {
        var allCheck = $('#myform').validationEngine('validate');
        if (!allCheck) {
            return;
        }
        $.ajax({
            url: '${BASE_PATH}/mms/message_template/save',
            dataType: 'json',
            data: $('#myform').serialize(),
            type: 'post',
            success: function (data) {
                if (data.result) {
                    Boss.alertToCallback('提交成功！', function () {
                        <#if task??>
                        location.href = "${BASE_PATH}/mms/record/under_way_list";
                        <#else>
                        location.href = "${BASE_PATH}/mms/message_template";
                        </#if>
                    });
                } else {
                    Boss.alert('提交失败！');
                }
            }, error: function (data) {
                Boss.alert('系统异常!请稍后重试！');
            }
        });
    }

    function addContent() {
        var html = $('#contentTemplateHtml').html();
        html = '<div class="form-group batchContent">' + html + '</div>';
        $('#myform .batchContent:last').after(html);
    }

    function removeContent(obj) {
        $(obj).parent().parent().remove();
    }

    (function ($) {
        $.fn.extend({
            insertAtCaret: function (myValue) {
                var $t = $(this)[0];
                if (document.selection) {
                    this.focus();
                    sel = document.selection.createRange();
                    sel.text = myValue;
                    this.focus();
                } else if ($t.selectionStart || $t.selectionStart == '0') {
                    var startPos = $t.selectionStart;
                    var endPos = $t.selectionEnd;
                    var scrollTop = $t.scrollTop;
                    $t.value = $t.value.substring(0, startPos) + myValue + $t.value.substring(endPos, $t.value.length);
                    this.focus();
                    $t.selectionStart = startPos + myValue.length;
                    $t.selectionEnd = startPos + myValue.length;
                    $t.scrollTop = scrollTop;
                } else {
                    this.value += myValue;
                    this.focus();
                }
            }
        })
    })(jQuery);

    function insertCode() {
        $("#content").insertAtCaret("#code#");
    }

</script>
</html>