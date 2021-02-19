$(function () {
    initApiGroup();
    initApiTypeEvent();
    loadApiBaseInfo();
    loadApiParams();
    loadApiResult();
});

function initApiTypeEvent() {
    $('input[name="apiTypeRadio"]').click(function () {
        $("#api-param").find("tbody").empty();
        $("#api-result").find("tbody").empty();
        var sel = $(this).val();
        $('input[name="apiType"]').val(sel);
        if (sel == 0) {
            $('input[name="apiName"]').attr("placeholder", "zhiyong.user.accout.create");
            $('#apiNameTip').html("(格式：产品线.业务线.业务类型.操作，API名称+版本号全局唯一)");
            $('#default-result').show();
            $('input[name="apiVersion"]').attr('readonly', false);
        } else {
            $('input[name="apiName"]').attr("placeholder", "cc_live_notify");
            $('#apiNameTip').html("(作为回调地址的一部分，例如:http://xxx/router/callback/cc_live_notify?data=xxx)");
            $('#default-result').hide();
            $('input[name="apiVersion"]').val('1.0');
            $('input[name="apiVersion"]').attr('readonly', true);
        }
    });
}

function saveApiAll() {
    $.messager.confirm('提示', '确定要同时一起保存API基本信息、请求参数、响应参数信息吗?', function (r) {
        if (r) {
            var validate = $("#api-base-form").form('validate');
            if (validate) {
                var apiBase = $('#api-base-form').serializeJSON();
                if (!apiBase) {
                    $.messager.alert('提示', '请填写API基本信息');
                    return;
                }
                var apiParams = parseApiParams() || [];
                var apiResult = parseApiResult() || [];
                $.post("/admin/saveApiAll.do", {
                    apiBaseJson: JSON.stringify(apiBase),
                    apiParamsJson: JSON.stringify(apiParams),
                    apiResultJson: JSON.stringify(apiResult)
                }, function (res) {
                    if (res.code != 200) {
                        $.messager.alert('提示', '对不起，API信息保存失败:' + res.msg);
                        return;
                    }
                    $("#apiId").val(res.data);
                    $.messager.alert('提示', 'API信息保存成功', 'info', function () {
                        window.top["reload_api_list"].call();
                        closeApiTab();
                    });
                });
            }
        }
    });
}

function loadApiBaseInfo() {
    var apiId = $("#apiId").val();
    if (!apiId) {
        return;
    }
    $.post("/admin/getApiBaseInfo.do", {id: apiId}, function (res) {
        if (res.code != 200) {
            $.messager.alert('提示', '对不起，API基本信息获取失败:' + res.msg);
            return;
        }
        $("#api-base-form").form('load', res.data || {});
        $("#api-base-form").find("input[name='apiName']").attr("disabled", true);
        $("#api-base-form").find("input[name='apiVersion']").attr("disabled", true);
        $("#api-base-form").find('input[name="apiTypeRadio"][value=' + res.data.apiType + ']').click();
        $("#api-base-form").find('input[name="apiTypeRadio"][value=' + res.data.apiType + ']').attr("checked", true);
        $("#api-base-form").find("input[name='apiTypeRadio']").attr("disabled", true);
        $("#apiGroup").val(res.data.apiGroup);
        $("#httpMethod").val(res.data.httpMethod);
        $("#needLogin").val(res.data.needLogin);
        $("#needAuth").val(res.data.needAuth);
    });
}

function saveApiBaseInfo() {
    var validate = $("#api-base-form").form('validate');
    if (validate) {
        var params = $('#api-base-form').serializeJSON();
        if (params) {
            for (var key in params) {
                params[key] = $.trim(params[key]);
            }
        }
        $("#api-base-save").attr("disabled", true);
        $.post("/admin/saveApiBase.do", params, function (res) {
            $("#api-base-save").attr("disabled", false);
            if (res.code != 200) {
                $.messager.alert('提示', '对不起，API基本信息保存失败:' + res.msg);
                return;
            }
            $.messager.alert('提示', 'API基本信息保存成功');
            $("#apiId").val(res.data);
        });
    }
}


function loadApiParams() {
    var apiId = $("#apiId").val();
    if (!apiId) {
        return;
    }
    $("#api-param").find("tbody").empty();
    $.post("/admin/getApiParamsList.do", {apiId: apiId}, function (res) {
        if (res.code != 200) {
            $.messager.alert('提示', '对不起，API请求参数列表获取失败:' + res.msg);
            return;
        }
        setFormField('api-param', res.data || []);
    });
}

function parseApiParams() {
    var ids = $("#api-param-form").find("input[name='id']");
    var paramNames = $("#api-param-form").find("input[name='paramName']");
    var paramDescs = $("#api-param-form").find("input[name='paramDesc']");
    var paramTypeIds = $("#api-param-form").find("input[name='paramTypeId']");
    var paramTypes = $("#api-param-form").find("input[name='paramType']");
    var paramSources = $("#api-param-form").find("select[name='paramSource']");
    var isRequireds = $("#api-param-form").find("select[name='isRequired']");
    var examples = $("#api-param-form").find("input[name='example']");

    var apiParams = [];
    var haveBodyReqSource = false;
    for (var i = 0; i < paramNames.length; i++) {
        if (!$(paramNames[i]).val()) {
            $.messager.alert('提示', '参数名不能为空');
            return;
        }
        if (!$(paramDescs[i]).val()) {
            $.messager.alert('提示', '参数描述不能为空');
            return;
        }
        if (!$(paramTypeIds[i]).val()) {
            $.messager.alert('提示', '参数类型不能为空');
            return;
        }
        if ($(paramSources[i]).val() == 1) {
            if ($("#httpMethod").val() == 'GET') {
                $.messager.alert('提示', '请求方式为GET类型，不能设置Body来源的参数');
                return;
            }
            if (haveBodyReqSource) {
                $.messager.alert('提示', 'Body类型的参数只能有一个');
                return;
            }
            haveBodyReqSource = true;
        } else {
            var paramName = $(paramNames[i]).val();
            if (paramName == 'method' || paramName == 'version' || paramName == 'appKey'
                || paramName == 'session' || paramName == 'timestamp' || paramName == 'sign') {
                $.messager.alert('提示', '[' + paramName + ']属于系统参数关键字，不能作为URL中的业务参数名');
                return;
            }
        }
        apiParams.push({
            id: $(ids[i]).val(),
            paramIndex: i,
            paramName: $(paramNames[i]).val(),
            paramDesc: $(paramDescs[i]).val(),
            paramType: $(paramTypes[i]).val(),
            paramTypeId: $(paramTypeIds[i]).val(),
            paramSource: $(paramSources[i]).val(),
            isRequired: $(isRequireds[i]).val(),
            example: $(examples[i]).val()
        });
    }
    return apiParams;
}

function saveApiParams() {
    var apiId = $("#apiId").val();
    if (!apiId) {
        $.messager.alert('提示', '对不起请先保存API基本信息');
        return;
    }
    var apiParams = parseApiParams();
    if (!apiParams) {
        return;
    }
    console.log(apiParams);
    var reqParam = {apiId: apiId, apiParamsJson: JSON.stringify(apiParams)};

    $("#api-param-save").attr("disabled", true);
    $.post("/admin/saveApiParams.do", reqParam, function (res) {
        $("#api-param-save").attr("disabled", false);
        if (res.code != 200) {
            $.messager.alert('提示', '对不起，API请求参数信息保存失败:' + res.msg);
            return;
        }
        $.messager.alert('提示', 'API请求参数信息保存成功');
        loadApiParams();
    });
}

// 结果参数处理
function loadApiResult() {
    var apiId = $("#apiId").val();
    if (!apiId) {
        return;
    }
    $("#api-result").find("tbody").empty();
    $.post("/admin/getApiResultList.do", {apiId: apiId}, function (res) {
        if (res.code != 200) {
            $.messager.alert('提示', '对不起，API响应参数列表获取失败:' + res.msg);
            return;
        }
        setFormField('api-result', res.data || []);
    });
}

function parseApiResult() {
    var ids = $("#api-result-form").find("input[name='id']");
    var fieldNames = $("#api-result-form").find("input[name='fieldName']");
    var fieldDescs = $("#api-result-form").find("input[name='fieldDesc']");
    var fieldTypes = $("#api-result-form").find("select[name='fieldType']");
    var backFieldNames = $("#api-result-form").find("input[name='backFieldName']");
    var examples = $("#api-result-form").find("input[name='example']");

    var apiResult = [];
    for (var i = 0; i < fieldNames.length; i++) {
        if (!$(fieldNames[i]).val()) {
            $.messager.alert('提示', '字段名不能为空');
            return;
        }
        if (!$(fieldDescs[i]).val()) {
            $.messager.alert('提示', '字段描述不能为空');
            return;
        }
        if (!$(backFieldNames[i]).val()) {
            $.messager.alert('提示', '映射字段不能为空');
            return;
        }
        apiResult.push({
            id: $(ids[i]).val(),
            fieldIndex: i,
            fieldName: $(fieldNames[i]).val(),
            fieldDesc: $(fieldDescs[i]).val(),
            fieldType: $(fieldTypes[i]).val(),
            backFieldName: $(backFieldNames[i]).val(),
            example: $(examples[i]).val()
        });
    }
    return apiResult;
}

function saveApiResult() {
    var apiId = $("#apiId").val();
    if (!apiId) {
        $.messager.alert('提示', '对不起请先保存API基本信息');
        return;
    }

    var apiResult = parseApiResult() || [];
    console.log(apiResult);
    var reqParam = {apiId: apiId, apiResultJson: JSON.stringify(apiResult)};

    $("#api-result-save").attr("disabled", true);
    $.post("/admin/saveApiResult.do", reqParam, function (res) {
        $("#api-result-save").attr("disabled", false);
        if (res.code != 200) {
            $.messager.alert('提示', '对不起，API响应参数信息保存失败:' + res.msg);
            return;
        }
        $.messager.alert('提示', 'API响应参数信息保存成功');
        loadApiResult();
    });
}

var paramTypeSelectDom;

function openSelectParamTypeWin(e) {
    $('#selParamTypeWin').window('open');
    paramTypeSelectDom = e;
    searchParamType();
}

function searchParamType() {
    var keyword = $("#selectParamTypeForm").find("input[name='keyword']").val();
    var typeSelect = $("#selectParamTypeForm").find("select[name='paramTypeList']");
    $.post("/admin/getParamTypeList.do", {keyword: keyword}, function (res) {
        if (res.code != 200) {
            $.messager.alert('提示', '对不起，参数类型列表获取失败:' + res.msg);
            return;
        }
        $(typeSelect).empty();
        $.each(res.data, function (n, item) {
            var typeDesc = item.isPojo == 0 ? item.typeName : (item.typeDesc + "[" + item.typeName + "]")
            $(typeSelect).append("<option style='margin: 2px;' value='" + item.id + "'>" + typeDesc + "</option>");
        });
        $(typeSelect).children().dblclick(function (e) {
            submitParamType();
        });
    });
}

function submitParamType() {
    var typeSelect = $("#selectParamTypeForm").find("select[name='paramTypeList']");
    var selOption = $(typeSelect).find('option:selected');
    if (selOption.length == 0) {
        $.messager.alert('提示', '请选择一个参数类型');
        return;
    }
    if (selOption.length > 1) {
        $.messager.alert('提示', '对不起，仅能选择一个参数类型');
        return;
    }
    if (paramTypeSelectDom) {
        $(paramTypeSelectDom).val($(selOption).text());
        $(paramTypeSelectDom).parent().children("input[name='paramTypeId']").val($(selOption).val());
        paramTypeSelectDom = null;
    }
    closeParamType();
}

function closeParamType() {
    $('#selParamTypeWin').window('close');
}

function resetRowIndex(type) {
    var rows = $('#' + type).find("tbody>tr");
    if (rows.length > 0) {
        $("#" + type + "-add").hide();
    } else {
        $("#" + type + "-add").show();
    }
    $.each(rows, function (n, item) {
        $(item).children(":first").html(n + 1);
    });
}

function addRow(e, type) {
    var row = $('#' + type + '-tpl').find("tbody>tr").clone();
    if (!e) {
        $('#' + type).find("tbody").append(row);
    } else {
        $(e).parent().parent().after(row);
    }
    resetRowIndex(type);
    return row;
}

function removeRow(e) {
    $.messager.confirm('提示', '确定要删除吗?', function (r) {
        if (r) {
            var row = $(e).parent().parent();
            var tableId = row.parent().parent().attr("id");
            row.remove();
            resetRowIndex(tableId);
        }
    });
}

function setFormField(type, data) {
    for (var i = 0; i < data.length; i++) {
        var row = addRow(null, type);
        for (var key in data[i]) {
            var dom = $(row).find("[name='" + key + "']");
            if (dom) {
                $(dom[0]).val(data[i][key]);
            }
        }
    }
}

function initApiGroup() {
    var groupList = getApiGroup();
    if (groupList) {
        $("#apiGroup").empty();
        $.each(groupList || [], function (n, item) {
            $("#apiGroup").append("<option value='" + item.id + "'>" + item.groupName + "</option>");
        });
    }
    var defaultGroupId = $("#groupId").val();
    if (defaultGroupId) {
        $("#apiGroup").val(defaultGroupId);
    }
}

function getApiGroup() {
    var groupList;
    $.ajax({
        url: '/admin/getAllApiGroup.do',
        cache: false,
        async: false,
        type: "GET",
        dataType: 'json',
        success: function (res) {
            if (res.code != 200 || !res.data) {
                $.messager.alert('提示', '对不起，API类目获取失败:' + res.msg);
                return null;
            }
            groupList = res.data;
        }
    });
    return groupList;
}

function closeApiTab() {
    var apiId = $("#apiId").val();
    if (!apiId) {
        parent.$('#tabs').tabs('close', '添加API');
    } else {
        parent.$('#tabs').tabs('close', '修改API-' + apiId);
    }
}

function goGroupList() {
    parent.addTab('类目管理', '/group/group-list.html', 'icon icon-nav');
}