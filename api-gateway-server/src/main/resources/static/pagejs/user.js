$(function () {
    $('#user-list').datagrid({
        frozenColumns: [[{
            field: 'ck',
            checkbox: true
        }]],
        singleSelect: true,
        method: 'post',
        url: '/admin/getUserList.do',
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
            {field: 'role', title: '角色', width: 150, align: 'center', sortable: true},
            {field: 'department', title: '部门', width: 150, align: 'center', sortable: true},
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
            {
                field: 'updateTime',
                title: '修改时间',
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
                $('#add-User').dialog('open');
                $("#add-User-form").form('clear');
                $("#add-User-form").find("input[name='password']").parent().parent().attr("disabled", false);
                $("#add-User-form").find("input[name='password']").parent().parent().show();
            }
        }, '-', {
            text: '修改',
            iconCls: 'icon-edit',
            handler: editUser
        }, '-', {
            text: '删除',
            iconCls: 'icon-remove',
            handler: delUser
        }]
    });

    $("#search").click(function () {
        $('#user-list').datagrid('reload');
    });
});

function editUser() {
    var row = $('#user-list').datagrid('getSelected');
    if (!row) {
        $.messager.alert('提示', '请选择需要编辑的记录');
        return;
    }
    $("#add-User-form").form('clear');
    $("#add-User-form").find("input[name='password']").parent().parent().attr("disabled", true);
    $("#add-User-form").find("input[name='password']").parent().parent().hide();
    $('#add-User').dialog('open');
    $("#add-User-form").form('load', row);
}

function delUser() {
    var row = $('#user-list').datagrid('getSelected');
    if (!row) {
        $.messager.alert('提示', '请选择需要删除的记录');
        return;
    }
    $.messager.confirm('提示', '删除后无法恢复，确定要删除吗?', function (r) {
        if (r) {
            $.post("/admin/delUser.do", {id: row.id}, function (res) {
                if (res.code != 200) {
                    $.messager.alert('提示', '对不起，用户删除失败:' + res.msg);
                    return;
                }
                $('#user-list').datagrid('reload');
            });
        }
    });
}

function clearAddUser() {
    $('#add-User-form').form('clear');
}

function submitAddUser() {
    var validate = $("#add-User-form").form('validate');
    if (validate) {
        var params = $('#add-User-form').serialize();
        $.post("/admin/saveUser.do", params, function (res) {
            if (res.code != 200) {
                $.messager.alert('提示', '对不起，用户添加失败:' + res.msg);
                return;
            }
            $('#add-User').dialog('close');
            $('#user-list').datagrid('reload');
            clearAddUser();
        });
    }
}





//修改密码
function modifyPwd() {
    var newPwd = $('#txtNewPass').val();
    var rePwd = $('#txtRePass').val();

    if (!newPwd) {
        $.messager.alert('系统提示', '请输入密码！', 'warning');
        return;
    }

    if (newPwd.length < 6) {
        $.messager.alert('系统提示', '密码不能小于6位！', 'warning');
        return;
    }

    if (!rePwd) {
        $.messager.alert('系统提示', '请再一次输入密码！', 'warning');
        return;
    }

    if (newPwd != rePwd) {
        $.messager.alert('系统提示', '两次密码不一致，请重新输入！', 'warning');
        return;
    }

    $.post('/admin/modifyPwd.do', {newPwd: newPwd}, function (res) {
        if (res.code != 200) {
            $.messager.alert('提示', '密码修改失败');
            return;
        }
        $.messager.alert('提示', '密码修改成功');
        $('#w').window('close');
        location.href = '/login.html';
    })
}

$(function () {
    $('#editPass').click(function () {
        $('#w').window('open');
    });

    $('#btnEp').click(function () {
        modifyPwd();
    })

    $('#btnCancel').click(function () {
        $('#w').window('close');
    })

    $('#loginOut').click(function () {
        $.post("/admin/logout.do", {}, function (res) {
            if (res.code != 200) {
                $.messager.alert('提示', '系统异常');
                return;
            }
            location.href = '/';
        });
    })
});