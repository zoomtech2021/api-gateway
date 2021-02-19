$(function () {
    $('#auth-list').datagrid({
        frozenColumns: [[{
            field: 'ck',
            checkbox: true
        }]],
        singleSelect: true,
        method: 'post',
        url: '/admin/getAuthList.do',
        //fit: true,
        height: getGridHeight() - 10,
        fitColumns: true,
        rownumbers: true,
        pagination: true,
        striped: true,
        loadMsg: '正在拼命加载数据...',
        pageSize: 10,
        pageList: [10, 20, 30, 50, 100],
        columns: [[
            {field: 'id', title: 'ID', width: 40, align: 'center', sortable: true},
            {field: 'account', title: '账号', width: 150, sortable: true},
            {field: 'groupName', title: '类目', width: 150, align: 'center', sortable: true},
            {
                field: 'createTime',
                title: '创建时间',
                width: 150,
                align: 'center',
                sortable: true,
                formatter: function (value) {
                    return dateFormatter(value);
                }
            }
        ]],
        rowStyler: function (index, row) {
            return 'background-color:white;';
        },
        onBeforeLoad: function (param) {
            param.account = $("#s-account").val();
        },
        toolbar: [{
            iconCls: 'icon-add',
            text: '添加',
            handler: function () {
                initUserList();
                initApiGroupList();
                $('#add-auth').dialog('open');
            }
        }, '-', {
            text: '删除',
            iconCls: 'icon-remove',
            handler: delAuth
        }]
    });

    $("#search").click(function () {
        $('#auth-list').datagrid('reload');
    });
});

function delAuth() {
    var row = $('#auth-list').datagrid('getSelected');
    if (!row) {
        $.messager.alert('提示', '请选择需要删除的记录');
        return;
    }
    $.messager.confirm('提示', '删除后无法恢复，确定要删除吗?', function (r) {
        if (r) {
            $.post("/admin/delAuth.do", {id: row.id}, function (res) {
                if (res.code != 200) {
                    $.messager.alert('提示', '对不起，权限删除失败:' + res.msg);
                    return;
                }
                $('#auth-list').datagrid('reload');
            });
        }
    });
}

function submitAddAuth() {
    var validate = $("#add-auth-form").form('validate');
    if (validate) {
        var params = $('#add-auth-form').serialize();
        $.post("/admin/saveAuth.do", params, function (res) {
            if (res.code != 200) {
                $.messager.alert('提示', '对不起，权限添加失败:' + res.msg);
                return;
            }
            $('#add-auth').dialog('close');
            $('#auth-list').datagrid('reload');
        });
    }
}

function initApiGroupList() {
    $.post("/admin/getAllApiGroup.do", {}, function (res) {
        if (res.code != 200) {
            $.messager.alert('提示', '对不起，获取API类目列表失败:' + res.msg);
            return;
        }
        $('#groupList').combobox({
            valueField: "id",
            textField: "groupName",
            data: res.data || []
        });
    });
}

function initUserList() {
    $.post("/admin/getUserList.do", {page: 1, rows: 1000}, function (res) {
        $('#userList').combobox({
            valueField: "id",
            textField: "account",
            data: res.rows || []
        });
    });
}

