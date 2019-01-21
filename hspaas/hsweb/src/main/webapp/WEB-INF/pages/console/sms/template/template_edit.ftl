<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>短信模板修改 - 短信平台 - 华时融合平台</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${rc.contextPath}/static/plugins/layui2/css/layui.css" media="all" />
    <link rel="stylesheet" href="${rc.contextPath}/static/css/global.css" media="all">
    <link rel="stylesheet" href="${rc.contextPath}/static/plugins/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" href="${rc.contextPath}/static/css/form.css" media="all">
    <link rel="stylesheet" href="${rc.contextPath}/static/build/css/themes/default.css" media="all" id="skin" kit-skin />
</head>

<body>
<div class="layui-fluid" style="margin-top:5px;">
	<form class="layui-form" action="" id="form_data"  lay-filter="form-job-edit">
		<input id="id" name="id" type="hidden" value="${template.id!''}" />
		<div class="layui-form-item">
			<label class="layui-form-label">模板内容：<br/>
			<a href="javascript:addVar();" class="layui-btn layui-btn-xs layui-btn-danger">添加变量(+)</a>
			</label>
			<div class="layui-input-block">
				<textarea id="content" name="content" placeholder="请输入模板内容" class="layui-textarea" lay-verify="required">${template.content!''}</textarea>
			</div>
        </div>
		<div class="layui-form-item layui-hide">
            <input type="button" lay-submit lay-filter="LAY-btn-submit" id="LAY-btn-submit" value="确认">
        </div>
	</form>
</div>

<script type="text/javascript" src="${rc.contextPath}/static/js/custom_defines.js"></script>
<script type="text/javascript" src="${rc.contextPath}/static/plugins/layui2/layui.js"></script>
<script type="text/javascript" src="${rc.contextPath}/static/js/jquery-1.12.0.min.js"></script>

<script type="text/javascript">

	(function ($) {
        $.fn.extend({
            insertAtCaret: function (myValue) {
                var $t = $(this)[0];
                if (document.selection) {
                    this.focus();
                    sel = document.selection.createRange();
                    sel.text = myValue;
                    this.focus();
                } else
                    if ($t.selectionStart || $t.selectionStart == '0') {
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
    
    function addVar(){
    	$("#content").insertAtCaret("#code#");
    };

	layui.config({
	    base: '${rc.contextPath}/static/plugins/layui2/lay/modules/'
	}).use(['form', 'element'], function(){
		var $ = layui.$
                ,admin = layui.admin
                ,form = layui.form
                ,modal = layui.modal;
	    
	});
</script>
    
</script>

</body>
</html>