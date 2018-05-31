var Hs = {
	version : "1.0.1",
	domain : document.domain,
	host : window.location.host,
	port : window.location.port,
	base : "http://" + window.location.host,

	isBlank : function(obj) {
		return obj == undefined || obj == null || obj.length == 0;
	},

	ajax : function(options) {
		options = options || {};
		if (options.url == undefined) {
			console.error("ajax url not setted");
			return;
		}

		$.ajax({
			url : Hs.base + options.url,
			dataType : Hs.isBlank(options.dataType) ? "json" : options.dataType,
			beforeSend : function() {
				if (options.beforeSend)
					options.beforeSend();
			},
			async : Hs.isBlank(options.async) ? false : options.async,
			data : options.data,
			type : "POST",
			success : function(data) {
				if (options.afterSend)
					options.afterSend();

				if (options.success)
					options.success(data);
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				if (options.afterSend)
					options.afterSend();
				
				if (options.error)
					options.error();
				
			},
			complete: function (XMLHttpRequest, textStatus) {
				if(XMLHttpRequest.getResponseHeader){
					var sessionStatus = XMLHttpRequest.getResponseHeader("sessionstatus");
					if (sessionStatus == 'timeout') {
						window.location = XMLHttpRequest.getResponseHeader("location");
						return;
					}
				}
			}
		});
	},

	logout : function() {
		window.wxc.xcConfirm("您确认退出系统吗？",
				window.wxc.xcConfirm.typeEnum.confirm, {
					height : 260,
					onOk : function(v) {
						location.href = Hs.base + "/logout";
					}
				});
	},

	alert : function(title, callback) {
		window.wxc.xcConfirm(title, window.wxc.xcConfirm.typeEnum.info, {
			height : 260,
			onOk : function(v) {
				if (callback)
					callback();
				
				window.wxc.close();
			}
		});
	},
	
	success : function(title, callback) {
		window.wxc.xcConfirm(title, window.wxc.xcConfirm.typeEnum.success, {
			height : 260,
			onOk : function(v) {
				if (callback)
					callback();
				
				window.wxc.close();
			}
		});
	},
	
	error : function(title, callback) {
		window.wxc.xcConfirm(title, window.wxc.xcConfirm.typeEnum.error, {
			height : 260,
			onOk : function(v) {
				if (callback)
					callback();
				
				window.wxc.close();
			}
		});

	},

	confirm : function(title, callback) {
		window.wxc.xcConfirm(title, window.wxc.xcConfirm.typeEnum.confirm, {
			height : 260,
			onOk : function(v) {
				if (callback)
					callback();
			}
		});
	},
	
	close : function() {
		window.wxc.close();
	}

};

var Pagination = {
	go : function(currentPage) {
		if (currentPage == undefined || currentPage == null)
			currentPage = 1;
		Hs.ajax({
			url : $("#list_form").attr("action"),
			dataType : "html",
			data : "currentPage=" + currentPage + "&" + $("#list_form").serialize(),
			beforeSend : function() {
				Pagination.loading()
			},
			success : function(data) {
				Pagination.unloading();
				$("#list_container").html(data);
				Pagination.page_bind();
				Pagination.jump_bind()
			},
			afterSend : function() {
				Pagination.unloading();
			}
			
		})
	},
	loading : function() {
		$('#list_container').empty();
		$('#list_container').append("<div style='text-align:center;height:50px;'><img src='"+ Hs.base+"/assets/images/loading.gif' /></div>")
	},
	unloading : function() {
		$('#list_container').empty()
	},
	jump_bind : function() {
		$(".jumper").click(function() {
			$("#jump_page").css({
				"color" : "#888",
				"border" : "1px #ccc solid"
			});
			var jumpPage = $("#jump_page").val();
			if (jumpPage == undefined || jumpPage == null
					|| jumpPage == "" || isNaN(jumpPage)) {
				$("#jump_page").css({
					"color" : "#F00",
					"border" : "1px solid #F00"
				});
				return
			}
			var totalPage = $(this).attr("data-page");
			if (totalPage == undefined)
				return;
			if (parseInt(jumpPage) > parseInt(totalPage)) {
				$("#jump_page").css({
					"color" : "#F00",
					"border" : "1px solid #F00"
				});
				return
			}
			Pagination.go(jumpPage)
		})
	},
	page_bind : function() {
		$("#list_container span[data-name='1.16.1']").click(function() {
			var page = $(this).attr("data-page");
			if (page == undefined)
				return;
			Pagination.go(page)
		})
	}
};

//$.ajaxSetup({
//	global: false,
//	type: "POST",
//	complete: function (XMLHttpRequest, textStatus) {
//		if(XMLHttpRequest.getResponseHeader){
//			var sessionStatus = XMLHttpRequest.getResponseHeader("sessionstatus");
//			if (sessionStatus == 'timeout') {
//				window.location = XMLHttpRequest.getResponseHeader("location");
//				return;
//			}
//		}
//	}
//});