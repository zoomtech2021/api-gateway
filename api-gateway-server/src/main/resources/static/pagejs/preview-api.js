$(function () {
    initApiGroup();
    initApiTypeEvent();
    loadApiBaseInfo();
    loadApiParams();
    loadApiResult();
    $("input").attr('disabled', true);
    $('select').attr('disabled', true);
});

function initApiTypeEvent() {
    $('input[name="apiTypeRadio"]').click(function () {
        var sel = $(this).val();
        $('input[name="apiType"]').val(sel);
        if (sel == '0') {
            $('input[name="apiName"]').attr("placeholder", "zhiyong.user.accout.create");
            $('#apiNameTip').html("(格式：产品线.业务线.业务类型.操作，API名称+版本号全局唯一)");
            $('#default-result').show();
        } else {
            $('input[name="apiName"]').attr("placeholder", "cc_live_notify");
            $('#apiNameTip').html("(作为回调地址的一部分，例如:http://xxx/router/callback/cc_live_notify?data=xxx)");
            $('#default-result').hide();
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
        $('input[name="apiTypeRadio"][value=' + res.data.apiType + ']').click();
        $('input[name="apiTypeRadio"][value=' + res.data.apiType + ']').attr("checked",true);
        $("#apiGroup").val(res.data.apiGroup);
        $("#httpMethod").val(res.data.httpMethod);
        $("#needLogin").val(res.data.needLogin);
        $("#needAuth").val(res.data.needAuth);
    });
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
    $.post("/admin/getAllApiGroup.do", {}, function (res) {
        if (res.code != 200) {
            $.messager.alert('提示', '获取API类目失败:' + res.msg);
            return;
        }
        $("#apiGroup").empty();
        $.each(res.data || [], function (n, item) {
            $("#apiGroup").append("<option value='" + item.id + "'>" + item.groupName + "</option>");
        });
    });
}
