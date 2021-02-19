$(function () {
    initGroupTree();
    initGrid();
    $("#search").click(function () {
        $('#api-list').datagrid('reload');
    });
});

function initGroupTree() {
    $.post("/admin/getAllApiGroup.do", {}, function (res) {
        if (res.code != 200) {
            $.messager.alert('提示', '获取API类目失败:' + res.msg);
            return;
        }

        var data = [];
        data.push({id: -1, text: '全部', isLeaf: true})
        $.each(res.data || [], function (n, item) {
            data.push({id: item.id, text: item.groupName, isLeaf: true});
        });
        $('#groupList').tree({
            data: data,
            onClick: function (node) {
                $("#groupId").val(node.id);
                $("#search").click();
            },
            onLoadSuccess: function (node, data) {
                var n = $('#groupList').tree('find', data[0].id);
                $('#groupList').tree('select', n.target);
            }
        });
    });
}

function initGrid() {
    $('#api-list').datagrid({
        singleSelect: true,
        method: 'post',
        url: '/admin/getApiList.do',
        //fit: true,
        height: getGridHeight(),
        //fitColumns: true,
        rownumbers: true,
        pagination: true,
        striped: true,
        nowrap: true,
        loadMsg: '正在拼命加载数据...',
        pageSize: 20,
        pageList: [20, 30, 50, 100, 200],
        frozenColumns: [[
            {field: 'ck', checkbox: true},
            //{field: 'id', title: 'ID', width: 40, align: 'center', sortable: true},
            {
                field: 'apiName', title: 'API名称', width: 300, sortable: true, formatter: function (value, row) {
                    return '<a href="javascript:previewApi(' + row.id + ')" style="color: dodgerblue" title="' + value + '">' + value + '</a>';
                }
            },
            {field: 'apiVersion', title: '版本', width: 80, align: 'center', sortable: true},
            {
                field: 'apiType', title: '类型', width: 80, align: 'center', sortable: true, formatter: function (value) {
                    if (value == 1) {
                        return '<span style="color:blueviolet;font-weight: bold;">回调API</span>';
                    } else {
                        return '<span style="color:darkgray;font-weight: bold;">普通API</span>';
                    }
                }
            }
        ]],
        columns: [[
            {
                field: 'state', title: '状态', align: 'center', sortable: true, width: 80, formatter: function (value) {
                    if (value == 0) {
                        return '<span style="color: darkorange;font-weight: bold;">草稿</span>';
                    } else if (value == 1) {
                        return '<span style="color: limegreen;font-weight: bold;">已发布</span>';
                    } else if (value == 2) {
                        return '<span style="color: indianred;font-weight: bold;">已下线</span>';
                    }
                }
            },
            {
                field: 'apiDesc', title: '描述', width: 200, sortable: true, formatter: function (value) {
                    return "<span title='" + value + "' class='easyui-tooltip'>" + value + "</span>";
                }
            },
            {field: 'groupName', title: '类目', width: 130, sortable: true},
            {field: 'httpMethod', title: '请求方式', width: 70, align: 'center', sortable: true},
            {
                field: 'needLogin',
                title: '是否需登录',
                width: 80,
                align: 'center',
                sortable: true,
                formatter: function (value) {
                    if (value == 0) {
                        return '<span style="color: darkorange;font-weight: bold;">Yes</span>';
                    } else if (value == 1) {
                        return '<span style="color: limegreen;font-weight: bold;">No</span>';
                    }
                }
            },
            {
                field: 'needAuth',
                title: '是否需授权',
                width: 80,
                align: 'center',
                sortable: true,
                formatter: function (value) {
                    if (value == 0) {
                        return '<span style="color: darkorange;font-weight: bold;">Yes</span>';
                    } else if (value == 1) {
                        return '<span style="color: limegreen;font-weight: bold;">No</span>';
                    }
                }
            },
            {
                field: 'callLimit',
                title: '限流数',
                width: 100,
                align: 'center',
                sortable: true,
                formatter: function (value) {
                    if (value == -1) {
                        return '不限流';
                    }
                    return value;
                }
            },
            {
                field: 'createTime',
                title: '创建时间',
                width: 150,
                align: 'center',
                sortable: true,
                formatter: function (value) {
                    return dateFormatter(value);
                }
            },
            {field: 'creator', title: '创建人', width: 100, align: 'center', sortable: true},
            {
                field: 'updateTime',
                title: '最后修改时间',
                width: 150,
                align: 'center',
                sortable: true,
                formatter: function (value) {
                    if (!value) {
                        return;
                    }
                    return dateFormatter(value);
                }
            },
            {field: 'updater', title: '最后修改人', width: 100, align: 'center', sortable: true}
        ]],
        rowStyler: function (index, row) {
            return 'background-color:white;';
        },
        onBeforeLoad: function (param) {
            param.apiName = $("#s-apiName").val();
            param.apiDesc = $("#s-apiDesc").val();
            param.groupId = $("#groupId").val();
        },
        toolbar: [{
            iconCls: 'icon-add',
            text: '添加',
            handler: function () {
                var groupId = $("#groupId").val() || '';
                parent.addTab("添加API", '/api/add-api.html?groupId=' + groupId, 'icon icon-nav');
            }
        }, '-', {
            text: '修改',
            iconCls: 'icon-edit',
            handler: function () {
                var row = $('#api-list').datagrid('getSelected');
                if (!row) {
                    $.messager.alert('提示', '请选择需要修改的记录');
                    return;
                }
                if (row.state == 1) {
                    $.messager.confirm('提示', '您修改的API处于发布状态，确定要修改吗?', function (r) {
                        if (r) {
                            parent.addTab("修改API-" + row.id, '/api/add-api.html?id=' + row.id, 'icon icon-nav');
                        }
                    });
                } else if (row.state == 2) {
                    $.messager.alert('提示', '已下线记录不能再修改');
                    return;
                } else {
                    parent.addTab("修改API-" + row.id, '/api/add-api.html?id=' + row.id, 'icon icon-nav');
                }
            }
        }, '-', {
            text: '发布',
            iconCls: 'icon-redo',
            handler: function () {
                editApiState(1);
            }
        }, '-', {
            text: '下线',
            iconCls: 'icon-remove',
            handler: function () {
                editApiState(2);
            }
        }]
    });
}

function previewApi(id) {
    parent.addTab("预览API-" + id, '/api/preview-api.html?id=' + id, 'icon icon-nav');
}

function editApiState(state) {
    var row = $('#api-list').datagrid('getSelected');
    if (!row) {
        $.messager.alert('提示', '请选择需要操作的记录');
        return;
    }
    var tip = state == 1 ? "发布" : "下线";
    $.messager.confirm('提示', '确定要' + tip + '该API吗?', function (r) {
        if (r) {
            $.post("/admin/editApiState.do", {id: row.id, nowState: row.state, newState: state}, function (res) {
                if (res.code != 200) {
                    $.messager.alert('提示', '对不起，API' + tip + '失败:' + res.msg);
                    return;
                }
                $('#api-list').datagrid('reload');
            });
        }
    });
}

window.top["reload_api_list"] = function () {
    $("#search").click();
}