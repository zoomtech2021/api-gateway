$(function () {
    initApiList();
});

function initApiList() {
    $('#apiList').combobox({
        url: "/admin/getAllApiList.do",
        mode: "remote",
        valueField: "id",
        textField: "method",
        groupField: "apiGroup",
        groupPosition: "sticky",
        onSelect: function (option) {
            $("#apiType").val(option.apiType);
            $("#httpMethod").val(option.httpMethod);
            $("#version").val(option.apiVersion);
            $("#method").val(option.apiName);
            $("#apiDesc").html(option.apiDesc || '');
            if (option.apiType == 0) {
                $("#appKeyTr").show();
                if (option.needLogin != 0) {
                    $("#loginSession").hide();
                    $("#loginSession").attr("disabled", true);
                } else {
                    $("#loginSession").show();
                    $("#loginSession").attr("disabled", false);
                }
            } else {
                $("#loginSession").hide();
                $("#loginSession").attr("disabled", true);
                $("#appKeyTr").hide();
            }
            loadApiParams(option.id);
            previewReqUrl();
        }
    });
}

function loadApiParams(id) {
    if (!id) {
        return;
    }
    $.post("/admin/getApiParamsList.do", {apiId: id}, function (res) {
        if (res.code != 200) {
            $.messager.alert('提示', '对不起，API请求参数列表获取失败:' + res.msg);
            return;
        }
        $("#bizParam").empty();
        var paramList = res.data || [];
        if (paramList.length > 0) {
            $.each(paramList, function (n, item) {
                var isRequired = item.isRequired == 0 ? "true" : "false";
                var isRequiredDom = item.isRequired == 0 ? '<font color="red">*</font>' : '&nbsp;';
                if (item.paramSource == 0) {
                    var isArray = (item.paramType == 'java.util.List' || item.paramType == 'java.util.Set' || item.paramType.endWith('[]')) ? true : false;
                    var trDom = "<tr class='urlParam'>"
                        + "   <td style='width: 90px;'>" + isRequiredDom + item.paramName + ":</td>"
                        + "   <td>"
                        + "       <input class='easyui-textbox' type='text' is_array='" + isArray + "' name='" + item.paramName + "' is_require='" + isRequired + "' placeholder='请输入" + item.paramDesc + "'/>"
                        + "   <br/>参数类型：(" + item.paramType + ")" + (isArray == true ? "<span style='color: red;'>&nbsp，例如：[1,2,3]或[\"A\",\"B\",\"C\"]</span>" : "")
                        + "   </td>"
                        + "</tr>";
                    $("#bizParam").append(trDom);
                } else {
                    var trDom = "<tr class='bodyParam'>"
                        + "   <td style='width: 90px;'>" + isRequiredDom + item.paramName + ":</td>"
                        + "   <td><textarea name='" + item.paramName + "' is_require='" + isRequired + "' placeholder='请输入" + item.paramDesc + "（JSON格式）' rows='15' cols='60'></textarea>"
                        + "   参数类型：(" + item.paramType + ")"
                        + "   </td>"
                        + "</tr>";
                    $("#bizParam").append(trDom);
                }
            });
        }
    });
}

function sendRequest() {
    if ($('#apiType').val() == 1) {
        submitRequest();
    } else {
        sendApiRequest();
    }
}

function sendApiRequest() {
    // 参数校验
    if (!$("#method").val() || !$("#version").val()) {
        $.messager.alert('提示', '请选择一个要测试的API');
        return;
    }
    if (!$("#appKey").val()) {
        $.messager.alert('提示', '请填写一个appKey');
        return;
    }
    if ($("#loginSession").is(':visible')) {
        if (!$("#session").val()) {
            $.messager.alert('提示', '请填写登录session');
            return;
        }
    } else {
        if (!$("#tenantId").val()) {
            $.messager.alert('提示', '请填写租户ID');
            return;
        }
    }
    if (!$("#sign").val()) {
        $.messager.alert('提示', '签名生成失败');
        return;
    }
    submitRequest();
}

function submitRequest() {
    try {
        $("#sendRequestBtn").attr("disabled", true);
        var reqUrl = joinUrlBizParam();
        if (!reqUrl) {
            return;
        }
        $("#reqUrl").text(reqUrl);
        $("#response").text('请求中...');
        if ($("#httpMethod").val() == 'GET') {
            $.ajax({
                url: reqUrl,
                success: function (res) {
                    $("#sendRequestBtn").attr("disabled", false);
                    if ($('#apiType').val() == 0 && res.code != 200) {
                        $("#response").text(new Date() + " - 请求失败：" + res.msg);
                        return;
                    }
                    $("#response").text(JSON.stringify(res, null, '\t'));
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    $("#response").text(errorThrown);
                }
            });
        } else {
            var fields = $(".bodyParam").find("textarea");
            if (!fields) return;
            var paramName = $(fields[0]).attr("name");
            var paramVal = $(fields[0]).val();
            var required = $(fields[0]).attr("is_require");
            if (required == 'true' && !paramVal) {
                $.messager.alert('提示', '业务参数[' + paramName + ']为必填项');
                return;
            }
            var bodyParams = paramVal || {};
            $.ajax({
                url: reqUrl,
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                data: bodyParams,
                success: function (res) {
                    $("#sendRequestBtn").attr("disabled", false);
                    if ($('#apiType').val() == 0 && res.code != 200) {
                        $("#response").text(new Date() + " - 请求失败：" + res.msg);
                        return;
                    }
                    $("#response").text(JSON.stringify(res, null, '\t'));
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    $("#response").text(errorThrown);
                }
            });
        }
    } catch (e) {
        $("#response").text(e);
    }
}

function joinUrlBizParam() {
    var fields = $(".urlParam").find("input[type='text']");
    var bizParams = {};
    if (fields) {
        for (var i = 0; i < fields.length; i++) {
            var item = fields[i];
            var paramName = $(item).attr("name");
            var paramVal = $(item).val() || '';
            var required = $(item).attr("is_require");
            var isArray = $(item).attr("is_array");
            if (required == 'true' && paramVal == '') {
                $.messager.alert('提示', '业务参数[' + paramName + ']为必填项');
                return;
            }
            if (isArray == 'true') {
                if (!paramVal.startWith("[") || !paramVal.endWith("]")) {
                    $.messager.alert('提示', '业务参数[' + paramName + ']格式错误，请参考示例');
                    return;
                }
            }
            if (required == 'true' || required == 'false' && paramVal != '') {
                bizParams[paramName] = paramVal;
            }
        }
    }
    var reqUrl = buildApiReqUrl(bizParams);
    return reqUrl;
}

function previewReqUrl() {
    var reqUrl = buildApiReqUrl();
    $("#reqUrl").text(reqUrl);
}

function buildApiReqUrl(bizParams) {
    if ($('#apiType').val() == 1) {
        return buildCallbackReqUrl(bizParams);
    }

    // 组装公共参数
    var timestamp = dateFormatter(new Date().getTime(), "-");
    var commonParams = {
        "method": $("#method").val() || '',
        "version": $("#version").val() || '',
        "appKey": $("#appKey").val() || '',
        "session": $("#session").val() || '',
        "timestamp": encodeURIComponent(timestamp) || '',
    };

    var domain = document.location.protocol + "//" + document.location.host;
    var reqUrl = "/router/rest?appKey={appKey}&&session={session}&method={method}&timestamp={timestamp}&version={version}&sign={sign}";

    var signParams = commonParams;
    var bizParamArr = [];
    if (bizParams) {
        for (var key in bizParams) {
            var value = bizParams[key];
            if (value.startWith("[") && value.endWith("]")) {
                var valArr = eval("(" + value + ")");
                signParams[key] = encodeURIComponent(valArr.join(","));
                $.each(valArr, function (i, item) {
                    bizParamArr.push(key + "=" + item);
                });
            } else {
                signParams[key] = encodeURIComponent(value);
                bizParamArr.push(key + "=" + value);
            }
        }
    }

    // 组装签名参数
    var sign = getParamSign(signParams);
    commonParams["sign"] = sign;

    for (var key in commonParams) {
        var value = commonParams[key];
        reqUrl = reqUrl.replace('{' + key + '}', value);
    }

    if ($("#tenantId").val()) {
        reqUrl += "&tenantId=" + $("#tenantId").val();
    }

    if (bizParamArr.length > 0) {
        reqUrl += "&" + bizParamArr.join("&");
    }

    return domain + reqUrl;
}

function buildCallbackReqUrl(bizParams) {
    var domain = document.location.protocol + "//" + document.location.host;
    var reqUrl = "/router/callback/" + $("#method").val();
    var bizParamArr = [];
    if ($("#tenantId").val()) {
        bizParamArr.push("tenantId=" + $("#tenantId").val());
    }
    if (bizParams) {
        for (var key in bizParams) {
            var value = bizParams[key];
            if (value.startWith("[") && value.endWith("]")) {
                var valArr = eval("(" + value + ")");
                $.each(valArr, function (i, item) {
                    bizParamArr.push(key + "=" + item);
                });
            } else {
                bizParamArr.push(key + "=" + value);
            }
        }
    }

    if (bizParamArr.length > 0) {
        reqUrl += "?" + bizParamArr.join("&");
    }
    return domain + reqUrl;
}

function getParamSign(params) {
    var appSecret = "test";
    var appKey = $("#appKey").val();
    if (appKey) {
        appSecret = getAppSecret(appKey);
    }
    var sign = doSign(params, appSecret);
    $("#sign").val(sign);
    return sign;
}

function getAppSecret(appKey) {
    var appSecret;
    $.ajax({
        url: '/admin/getAppSecret.do',
        data: {appKey: appKey},
        cache: false,
        async: false,
        type: "POST",
        dataType: 'json',
        success: function (res) {
            if (res.code != 200) {
                $.messager.alert('提示', '对不起，AppSecret获取失败:' + res.msg);
                return;
            }
            appSecret = res.data;
            if (!appSecret) {
                $.messager.alert('提示', 'appKey输入不正确');
                return;
            }
        }
    });
    return appSecret;
}

function clearForm() {
    parent.addTab('测试工具', '/api/api-test-tool.html', 'icon icon-nav');
}
