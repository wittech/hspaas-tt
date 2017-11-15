<#if rowType == 1>
	<div class="form-group">
    	<label class="col-xs-2 control-label">显示名</label>
        <div class="col-xs-2">
            <input type="text" class="form-control validate[required,maxSize[100]]" name="showName" placeholder="请输入显示名">
        </div>
        <label class="col-xs-1 control-label">请求名</label>
        <div class="col-xs-2">
            <input type="text" class="form-control validate[required,maxSize[100]]" name="requestName" placeholder="请输入请求名">
        </div>
        <label class="col-xs-1 control-label">默认值</label>
        <div class="col-xs-2">
            <input type="text" class="form-control validate[optional,maxSize[100]]" name="defaultValue" placeholder="请输入默认值">
        </div>
        <div class="col-xs-1">
            <a class="btn btn-danger btn-sm" onclick="removeRow(this);">移除</a>
        </div>
    </div>
<#elseif rowType == 2>
	 <div class="form-group">
    	<label class="col-xs-2 control-label">显示名</label>
        <div class="col-xs-2">
            <input type="text" class="form-control validate[required,maxSize[100]]" name="showName" placeholder="请输入显示名">
        </div>
        <label class="col-xs-1 control-label">解析名</label>
        <div class="col-xs-2">
            <input type="text" class="form-control validate[required,maxSize[100]]" name="parseName" placeholder="请输入解析名">
        </div>
        <label class="col-xs-1 control-label">位置</label>
        <div class="col-xs-2">
            <input type="text" class="form-control validate[required,maxSize[100]]" name="position" placeholder="请输入解析位置">
        </div>
        <div class="col-xs-1">
            <a class="btn btn-danger btn-sm" onclick="removeRow(this);">移除</a>
        </div>
    </div>
</#if>