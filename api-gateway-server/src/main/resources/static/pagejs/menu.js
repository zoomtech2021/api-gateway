var menuConfig = {
    "menus": [
        {
            "menuid": "1", "icon": "icon-sys", "menuname": "API管理",
            "menus": [
                {"menuid": "11", "menuname": "应用管理", "icon": "icon-nav", "url": "/app/app-list.html"},
                {"menuid": "12", "menuname": "类目管理", "icon": "icon-nav", "url": "/group/group-list.html"},
                {"menuid": "13", "menuname": "API管理", "icon": "icon-nav", "url": "/api/api-list.html"},
                {"menuid": "14", "menuname": "参数类型", "icon": "icon-nav", "url": "/struct/struct-list.html"},
                {"menuid": "15", "menuname": "测试工具", "icon": "icon-nav", "url": "/api/api-test-tool.html"},
            ]
        }, {
            "menuid": "2", "icon": "icon-sys", "menuname": "API监控",
            "menus": [
                {"menuid": "21", "menuname": "API统计", "icon": "icon-nav", "url": "/monitor/api-count.html"},
                {"menuid": "22", "menuname": "操作日志", "icon": "icon-nav", "url": "/monitor/opt-log.html"}
            ]
        }
    ]
};
var userMangeMenu = {
    "menuid": "3", "icon": "icon-sys", "menuname": "系统管理",
    "menus": [
        {"menuid": "31", "menuname": "用户管理", "icon": "icon-nav", "url": "/user/user-list.html"},
        {"menuid": "32", "menuname": "权限管理", "icon": "icon-nav", "url": "/user/auth-list.html"}
    ]
};

//初始化左侧
function InitLeftMenu() {
    $("#nav").accordion({animate: false});

    $.each(menuConfig.menus, function (i, n) {
        var menulist = '';
        menulist += '<ul>';
        $.each(n.menus, function (j, o) {
            menulist += '<li><div><a ref="' + o.menuid + '" href="javascript:void(0)" rel="' + o.url + '" ><span class="icon ' + o.icon + '" >&nbsp;</span><span class="nav">' + o.menuname + '</span></a></div></li> ';
        })
        menulist += '</ul>';

        $('#nav').accordion('add', {
            title: n.menuname,
            content: menulist,
            iconCls: 'icon ' + n.icon
        });

    });

    $('.easyui-accordion li a').click(function () {
        var tabTitle = $(this).children('.nav').text();

        var url = $(this).attr("rel");
        var menuid = $(this).attr("ref");
        var icon = getIcon(menuid, icon);

        addTab(tabTitle, url, icon);
        $('.easyui-accordion li div').removeClass("selected");
        $(this).parent().addClass("selected");
    }).hover(function () {
        $(this).parent().addClass("hover");
    }, function () {
        $(this).parent().removeClass("hover");
    });

    //选中第一个
    var panels = $('#nav').accordion('panels');
    var t = panels[0].panel('options').title;
    $('#nav').accordion('select', t);
}

//获取左侧导航的图标
function getIcon(menuid) {
    var icon = 'icon ';
    $.each(menuConfig.menus, function (i, n) {
        $.each(n.menus, function (j, o) {
            if (o.menuid == menuid) {
                icon += o.icon;
            }
        })
    })

    return icon;
}


$(function () {
    var account = $("#loginUser").text();
    if (account == 'admin') {
        menuConfig.menus.push(userMangeMenu);
    }
    InitLeftMenu();
});