<!DOCTYPE html>
<html lang="en">
<head>
     <title>短信发送记录 - 华时融合平台</title>
     <#include "/common/assets.ftl"/>
</head>
<body class="body">

<!-- 工具集 -->
<div class="my-btn-box">
	<span class="fl">
        <a class="layui-btn btn-add btn-default" id="btn-refresh"><i class="layui-icon">&#x1002;</i></a>
    </span>
    <span class="fr">
    	<span class="layui-form-label">搜索条件：</span>
        <div class="layui-input-inline">
            <input type="text" id="startDate" name="startDate" autocomplete="off" placeholder="请选择发送开始日期" value="${(startDate)!}" class="layui-input">
        </div>
        
        <div class="layui-input-inline">
            <input type="text" id="stopDate" name="stopDate" autocomplete="off" placeholder="请选择发送截止日期" value="${(stopDate)!}" class="layui-input">
        </div>
        <div class="layui-input-inline">
            <input type="text" autocomplete="off" placeholder="请输入手机号码" value="${(mobile)!}" class="layui-input">
        </div>
        <button class="layui-btn mgl-20">查询</button>
        <button class="layui-btn layui-btn-disabled">清空</button>
    </span>
</div>

<!-- 表格 -->
<div id="dateTable"></div>

<script src="${rc.contextPath}/assets/layui/layui.js" charset="utf-8"></script>
<script type="text/javascript">
	// 配置扩展方法路径
	layui.config({
	    base: '${rc.contextPath}/assets/static/js/'
	}).extend({
	    vip_nav: 'vip_nav'
	    , vip_tab: 'vip_tab'
	    , vip_table: 'vip_table'
	});
</script>
<script type="text/javascript">

    // layui方法
    layui.use(['table', 'form', 'layer', 'vip_table', 'laydate'], function () {

        // 操作对象
        var form = layui.form
                , table = layui.table
                , layer = layui.layer
                , vipTable = layui.vip_table
                , laydate = layui.laydate
                , $ = layui.jquery;
                
        //日期
        laydate.render({
            elem: '#startDate'
        });
        laydate.render({
            elem: '#stopDate'
        });
        
        // 表格渲染
        var tableIns = table.render({
            elem: '#dateTable'                  //指定原始表格元素选择器（推荐id选择器）
            , height: vipTable.getFullHeight()    //容器高度
            , cols: [[
                , {field: 'id', title: '序', width: 80}
                , {field: 'sid', title: 'SID', width: 120}
                , {field: 'mobile', title: '手机号码', width: 120}
                , {field: 'sendTime', title: '发送时间', width: 180}
                , {field: 'sendStatus', title: '发送状态', width: 120}
                , {field: 'receiveTime', title: '回执时间', width: 180}
                , {field: 'receiveStatus', title: '回执状态', width: 120}
                , {field: 'pushInfo', title: '推送信息', width: 120}
            ]]
            , url: './../json/data_table.json'
            , method: 'get'
            , page: true
            , limits: [30, 60, 90, 150, 300]
            , limit: 30 //默认采用30
            , loading: false
            , done: function (res, curr, count) {
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                console.log(res);

                //得到当前页码
                console.log(curr);

                //得到数据总量
                console.log(count);
            }
        });

        // 获取选中行
        table.on('checkbox(dataCheck)', function (obj) {
            layer.msg('123');
            console.log(obj.checked); //当前是否选中状态
            console.log(obj.data); //选中行的相关数据
            console.log(obj.type); //如果触发的是全选，则为：all，如果触发的是单选，则为：one
        });

        // 刷新
        $('#btn-refresh').on('click', function () {
            tableIns.reload();
        });


        // you code ...

    });
</script>
<!-- 表格操作按钮集 -->
<script type="text/html" id="barOption">
    <a class="layui-btn layui-btn-mini" lay-event="detail">查看</a>
    <a class="layui-btn layui-btn-mini layui-btn-normal" lay-event="edit">编辑</a>
    <a class="layui-btn layui-btn-mini layui-btn-danger" lay-event="del">删除</a>
</script>
</body>
</html>