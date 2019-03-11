<div class="row">
    <div class="col-lg-1"></div>
    <div class="col-lg-1">&nbsp;&nbsp;通道设置</div>
    <div class="col-lg-8">
        <table class="table table-bordered table-striped">
            <thead>
                <tr>
                    <th width="10%">省份</th>
                    <#list cmcps as c>
                        <#if c.getCode() != 0 && c.getCode() != 4>
                            <th width="25%">${c.getTitle()}</th>
                        </#if>
                    </#list>
                </tr>
            </thead>
            <tbody>
                <tr code="0" name="其他">
                    <td><b>其他</b></td>
                    <td>
                        <a class="selectPassage btn btn-xs btn-success" cmcp="1" href="javascript:void(0);"><i class="fa fa-cog"></i>&nbsp;设置</a>
                        <span id="position_${routeType}_0_1" class="finishSelectPassage" style="cursor: pointer"
                              data-placement="top" data-html="true" data-toggle="popover" title="已选通道"
                              data-content=""
                        >

                        </span>
                    </td>
                    <td>
                        <a class="selectPassage btn btn-xs btn-info" cmcp="2" href="javascript:void(0);"><i class="fa fa-cog"></i>&nbsp;设置</a>
                        <span id="position_${routeType}_0_2" class="finishSelectPassage" style="cursor: pointer"
                              data-placement="top" data-html="true" data-toggle="popover" title="已选通道"
                              data-content=""
                        >

                        </span>
                    </td>
                    <td>
                        <a class="selectPassage btn btn-xs btn-primary" cmcp="3" href="javascript:void(0);"><i class="fa fa-cog"></i>&nbsp;设置</a>
                        <span id="position_${routeType}_0_3" class="finishSelectPassage" style="cursor: pointer"
                              data-placement="top" data-html="true" data-toggle="popover" title="已选通道"
                              data-content=""
                        >

                        </span>
                    </td>
                </tr>
            <#list provinceList as pl>
                <tr code="${pl.code}" name="${pl.name}">
                    <td><b>${pl.name}</b></td>
                    <td>
                        <a class="selectPassage btn btn-xs btn-success" cmcp="1" href="javascript:void(0);"><i class="fa fa-cog"></i>&nbsp;设置</a>
                        <span id="position_${routeType}_${pl.code}_1" class="finishSelectPassage" style="cursor: pointer"
                              data-placement="top" data-html="true" data-toggle="popover" title="已选通道"
                              data-content=""
                        >

                        </span>
                    </td>
                    <td>
                        <a class="selectPassage btn btn-xs btn-info" cmcp="2" href="javascript:void(0);"><i class="fa fa-cog"></i>&nbsp;设置</a>
                        <span id="position_${routeType}_${pl.code}_2" class="finishSelectPassage" style="cursor: pointer"
                              data-placement="top" data-html="true" data-toggle="popover" title="已选通道"
                              data-content=""
                        >

                        </span>
                    </td>
                    <td>
                        <a class="selectPassage btn btn-xs btn-primary" cmcp="3" href="javascript:void(0);"><i class="fa fa-cog"></i>&nbsp;设置</a>
                        <span id="position_${routeType}_${pl.code}_3" class="finishSelectPassage" style="cursor: pointer"
                              data-placement="top" data-html="true" data-toggle="popover" title="已选通道"
                              data-content=""
                        >

                        </span>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
</div>