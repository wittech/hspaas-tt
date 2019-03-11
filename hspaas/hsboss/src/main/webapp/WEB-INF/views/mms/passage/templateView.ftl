<#if template?? && template.detailList?? && template.detailList?size gt 0>
    <#list template.detailList as pd>
    <form id="detail_form_${pd.callType}" style="display:none" class="form-horizontal">
        <input type="hidden" name="detail_callType_${pd.callType}.callType" class="detailRuleType"
               id="detail_callType_${pd.callType}" value="${pd.callType}">
        <div class="form-group">
            <label class="col-xs-2 control-label">请求URL</label>
            <div class="col-xs-8">
                <input type="text" class="form-control validate[required,maxSize[200]]"
                       name="detail_url_${pd.callType}.url" value="${pd.url}" id="detail_url_${pd.callType}"
                       placeholder="请输入请求URL">
            </div>
        </div>

        <div class="form-group">
            <label class="col-xs-2 control-label">请求参数</label>
        </div>
        <div id="requestParam${pd.callType}">
            <#assign requestList = pd.requestParams>
            <#if requestList?? && requestList?size gt 0>
                <#list requestList as rl>
                    <div class="form-group">
                        <label class="col-xs-2 control-label">${rl.showName!''}[${rl.requestName!''}]</label>
                        <div class="col-xs-8">
                            <input type="hidden" name="requestName" value="${rl.requestName!''}">
                            <input type="hidden" name="showName" value="${rl.showName!''}">
                            <input type="text" class="form-control validate[required,maxSize[100]]"
                                   value="${rl.defaultValue!''}" name="defaultValue" placeholder="请输入请求值">
                        </div>
                    </div>
                </#list>
            </#if>
        </div>

        <div class="form-group" style="display: none">
            <label class="col-xs-2 control-label">响应参数解析</label>
        </div>
        <div id="parseRule${pd.callType}" style="display: none">
            <#assign parseList = pd.parseParams />
            <#if parseList?? && parseList?size gt 0>
                <#list parseList as pl>
                    <div class="form-group">
                        <label class="col-xs-2 control-label">${pl.showName!''}[${pl.parseName!''}]</label>
                        <div class="col-xs-8">
                            <input type="hidden" name="parseName" value="${pl.parseName!''}">
                            <input type="hidden" name="showName" value="${pl.showName!''}">
                            <input type="text" class="form-control validate[required,maxSize[100]]"
                                   value="${pl.position!''}" name="position" placeholder="请输入解析位置">
                        </div>
                    </div>
                </#list>
            </#if>
        </div>

        <div class="form-group">
            <label class="col-xs-2 control-label">结果格式</label>
            <div class="col-xs-8">
                <input type="text" class="form-control validate[optional,maxSize[200]]"
                       value="${pd.showResultFormat!''}" name="detail_result_format_${pd.callType}"
                       id="detail_result_format_${pd.callType}" placeholder="请输入结果格式">
            </div>
        </div>

        <div class="form-group">
            <label class="col-xs-2 control-label">成功码标识</label>
            <div class="col-xs-8">
                <input type="text" class="form-control validate[optional,maxSize[200]]"
                       value="${pd.successCode!''}" name="detail_success_${pd.callType}"
                       id="detail_success_${pd.callType}" placeholder="请输入成功码标记">
            </div>
        </div>
    </form>
    </#list>
<#else>
    <div class="alert alert-warning" role="alert">该模板下未设置模板规则</div>
</#if>