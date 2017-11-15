<form id="detail_form_${ruleType.getValue()}" class="form-horizontal ruleForms">
	<input type="hidden" name="detail_callType_${ruleType.getValue()}.callType" class="detailRuleType" id="detail_callType_${ruleType.getValue()}" value="${ruleType.getValue()}">
	<div class="form-group">
	    <label class="col-xs-2 control-label">请求URL</label>
	    <div class="col-xs-8">
	        <input type="text" class="form-control validate[required,maxSize[200]]" name="detail_url_${ruleType.getValue()}.url" id="detail_url_${ruleType.getValue()}" placeholder="请输入请求URL">
	    </div>
	</div>
	<div class="form-group">
	    <label class="col-xs-2 control-label">设置请求参数</label>
	    <div class="col-xs-5">
	        <a class="btn btn-info btn-sm" onclick="addRow(1);">加参数</a>
	    </div>
	</div>
	<div id="requestParam${ruleType.getValue()}">
	    
	</div>
	
	<div class="form-group" style="display:none">
	    <label class="col-xs-2 control-label">响应参数解析</label>
	    <div class="col-xs-5">
	        <a class="btn btn-info btn-sm" onclick="addRow(2);">加定位</a>
	    </div>
	</div>
	<div id="parseRule${ruleType.getValue()}">
	   	
	</div>
	
	<div class="form-group" style="display:none">
	    <label class="col-xs-2 control-label">结果格式</label>
	    <div class="col-xs-8">
	        <input type="text" class="form-control validate[optional,maxSize[200]]" name="detail_result_format_${ruleType.getValue()}" id="detail_result_format_${ruleType.getValue()}" placeholder="请输入结果格式">
	    </div>
	</div>
	
	<div class="form-group">
	    <label class="col-xs-2 control-label">成功码标识</label>
	    <div class="col-xs-8">
	        <input type="text" class="form-control validate[optional,maxSize[200]]" name="detail_success_${ruleType.getValue()}" id="detail_success_${ruleType.getValue()}" placeholder="请输入成功码标记">
	    </div>
	</div>
</form>