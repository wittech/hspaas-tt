/** index.js 首页加载初始化 */
layui.config({
	base: '/static/build/js/'
}).use(['app', 'message', 'form'], function() {
	var app = layui.app
		,$ = layui.$
		,layer = layui.layer
		,message = layui.message
		,form = layui.form;

	var userCache = layui.data(userCacheName);
	var homepage = server_domain +"/user/profile";
	if(userCache.homepage){
		homepage = userCache.homepage;
	}

	//主入口
	app.set({
		type: 'iframe',
		homepage: homepage
	}).init();
	
	$('dl.skin > dd').on('click', function() {
		var $that = $(this);
		var skin = $that.children('a').data('skin');
		switchSkin(skin);
	});
	var setSkin = function(value) {
			layui.data('kit_skin', {
				key: 'skin',
				value: value
			});
		},
		getSkinName = function() {
			return layui.data('kit_skin').skin;
		},
		switchSkin = function(value) {
			var _target = $('link[kit-skin]')[0];
			_target.href = _target.href.substring(0, _target.href.lastIndexOf('/') + 1) + value + _target.href.substring(_target.href.lastIndexOf('.'));
			setSkin(value);
		},
		initSkin = function() {
			var skin = getSkinName();
			switchSkin(skin === undefined ? 'default' : skin);
		}();

	var logout = function() {
		$.ajax({
			url: server_domain+"/logout",
			data: null,
			type: "get",
			success: function (data) {
				location.href= server_domain + '/login';
			},
			error: function (data) {
				layer.msg(data.responseJSON.message);
			}
		});
	};

	$('#updatePwd').on('click', function () {
		layer.open({
			type: 2,
			title: '修改密码',
			area: ['500px', '330px'],
			shade: bgshade,
			maxmin: true,
			content: server_domain + '/user/password',
			zIndex: layer.zIndex,
			success: function(layero){
				layer.setTop(layero);
			},
			end: function () {
			}
		});
	});

	$('#logout').on('click', function() {
		//删除本地存储
		layui.data(userCacheName,null);
		logout();
	});
	
	$('#myProfile').on('click', function() {
		 parent.tab.tabAdd({
	         url: server_domain + 'user/profile',
	         icon: 'fa-user',
	         title: '我的信息'
	     });
	});
	
	/*
	$(".layui-logo").on('click', function() {
		location.href = server_domain + "/";
	});
	*/
	
//	$('#myAccount').on('click', function() {
//		 parent.tab.tabAdd({
//	         url: server_domain + 'user/account',
//	         icon: 'fa-user',
//	         title: '我的余额'
//	     });
//	});
	
});