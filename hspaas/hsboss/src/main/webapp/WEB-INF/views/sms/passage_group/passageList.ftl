<div class="row" style="width:799px">
    <div class="col-lg-4">
        <select class="form-control sourcePassage" style="height: 200px;" multiple="multiple">
            <#list passageList as pl>
            	<#if pl.status?? && pl.status == 0>
                <option value="${pl.id}">
                    ${pl.name}
                    <#if pl.type == 1>
                        &nbsp;[独]
                    </#if>
                    <#if pl.cmcp == 4>
                        &nbsp;[全]
                    </#if>
                </option>
                </#if>
            </#list>
        </select>
    </div>

    <div class="col-lg-2" style="text-align: center">
        <div class="row" style="margin-top:25px">
            <a class="btn btn-default btn-xs sourceButton" >&nbsp;&nbsp;选&nbsp;&nbsp;择&nbsp;&nbsp;>>&nbsp;&nbsp;</a>
        </div>
        <div class="row" style="margin-top:30px">
            <a class="btn btn-default btn-xs targetButton">&nbsp;&nbsp;<<&nbsp;&nbsp;取&nbsp;&nbsp;消&nbsp;&nbsp;</a>
        </div>
    </div>

    <div class="col-lg-4">
        <select class="form-control targetPassage" style="height: 200px;"  multiple="multiple">

        </select>
    </div>

    <div class="col-lg-1" style="text-align: center;margin-left:20px">
        <div class="row" style="margin-top:25px">
            <a class="btn btn-info btn-xs upButton">向上&nbsp;&nbsp;↑</a>
        </div>
        <div class="row" style="margin-top:30px">
            <a class="btn btn-warning btn-xs downButton">向下&nbsp;&nbsp;↓</a>
        </div>
    </div>

</div>