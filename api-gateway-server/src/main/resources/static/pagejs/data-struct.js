$(function () {
    $('#struct-list').datagrid({
        frozenColumns: [[{
            field: 'ck',
            checkbox: true
        }]],
        singleSelect: true,
        method: 'post',
        url: '/admin/getDataStructList.do',
        //fit: true,
        height: getGridHeight() - 10,
        fitColumns: true,
        rownumbers: true,
        pagination: true,
        striped: true,
        loadMsg: '正在拼命加载数据...',
        pageSize: 20,
        pageList: [20, 30, 50, 100],
        columns: [[
            {field: 'id', title: 'ID', width: 40, align: 'center', sortable: true},
            {field: 'typeName', title: '类型名', width: 300, sortable: true},
            {field: 'typeDesc', title: '类型描述', width: 250, sortable: true},
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
                field: 'typeJson',
                title: '类型结构定义',
                width: 130,
                align: 'center',
                formatter: function (value, row, index) {
                    return '<a href="javascript:previewJson(' + row.id + ')" style="text-decoration: underline;color:#0000ff;">预览</a>';
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
            param.typeName = $("#s-typeName").val();
            param.typeDesc = $("#s-typeDesc").val();
        },
        toolbar: [{
            iconCls: 'icon-add',
            text: '添加',
            handler: function () {
                parent.addTab("添加类型", '/struct/add-struct.html', 'icon icon-nav');
            }
        }, '-', {
            text: '修改',
            iconCls: 'icon-edit',
            handler: function () {
                var row = $('#struct-list').datagrid('getSelected');
                if (!row) {
                    $.messager.alert('提示', '请选择需要修改的记录');
                    return;
                }
                if (row.state == 1) {
                    $.messager.confirm('提示', '您修改的类型处于发布状态，确定要修改吗?', function (r) {
                        if (r) {
                            parent.addTab("修改类型-" + row.id, '/struct/add-struct.html?id=' + row.id, 'icon icon-nav');
                        }
                    });
                } else if (row.state == 2) {
                    $.messager.alert('提示', '已下线记录不能再修改');
                    return;
                } else {
                    parent.addTab("修改类型-" + row.id, '/struct/add-struct.html?id=' + row.id, 'icon icon-nav');
                }
            }
        }, '-', {
            text: '发布',
            iconCls: 'icon-redo',
            handler: function () {
                editTypeState(1);
            }
        }, '-', {
            text: '下线',
            iconCls: 'icon-remove',
            handler: function () {
                editTypeState(2);
            }
        }, '-', {
            text: '查看帮助手册',
            iconCls: 'icon-help',
            handler: function () {
                document.getElementById("open-struct-help").click();
            }
        }]
    });

    $("#search").click(function () {
        $('#struct-list').datagrid('reload');
    });
});

function editTypeState(state) {
    var row = $('#struct-list').datagrid('getSelected');
    if (!row) {
        $.messager.alert('提示', '请选择需要操作的记录');
        return;
    }
    var tip = state == 1 ? "发布" : "下线";
    $.messager.confirm('提示', '确定要' + tip + '该API吗?', function (r) {
        if (r) {
            $.post("/admin/editTypeState.do", {id: row.id, nowState: row.state, newState: state}, function (res) {
                if (res.code != 200) {
                    $.messager.alert('提示', '对不起，类型' + tip + '失败:' + res.msg);
                    return;
                }
                $('#struct-list').datagrid('reload');
            });
        }
    });
}

function previewJson(id) {
    $('#jsonPreviewWin').find("[name='iframe']").attr("src", "/struct/json-preview.html?id=" + id);
    $('#jsonPreviewWin').window('open');
}

window.top["reload_type_struct_list"] = function () {
    $("#search").click();
}