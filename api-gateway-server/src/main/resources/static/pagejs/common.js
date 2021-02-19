$.ajaxSetup({
    contentType: "application/x-www-form-urlencoded;charset=utf-8",
    complete: function (xmlHttpRequest, textStatus) {
        if (xmlHttpRequest.status == 403) {
            window.top.location.replace("/login.html");
        }
    }
});

function dateFormatter(value, split) {
    split = split || '/';
    var date = new Date(value);
    var year = date.getFullYear().toString();
    var month = (date.getMonth() + 1);
    var day = date.getDate().toString();
    var hour = date.getHours().toString();
    var minutes = date.getMinutes().toString();
    var seconds = date.getSeconds().toString();
    if (month < 10) {
        month = "0" + month;
    }
    if (day < 10) {
        day = "0" + day;
    }
    if (hour < 10) {
        hour = "0" + hour;
    }
    if (minutes < 10) {
        minutes = "0" + minutes;
    }
    if (seconds < 10) {
        seconds = "0" + seconds;
    }
    return year + split + month + split + day + " " + hour + ":" + minutes + ":" + seconds;
}

function dateFormatYMD(date) {
    var year = date.getFullYear().toString();
    var month = (date.getMonth() + 1);
    var day = date.getDate().toString();
    if (month < 10) {
        month = "0" + month;
    }
    if (day < 10) {
        day = "0" + day;
    }
    return year + "-" + month + "-" + day;
}

String.prototype.startWith = function (startStr) {
    var d = this.length - startStr.length;
    return (d >= 0 && this.indexOf(startStr) == 0)
}

String.prototype.endWith = function (endStr) {
    var d = this.length - endStr.length;
    return (d >= 0 && this.lastIndexOf(endStr) == d)
}

function getGridHeight() {
    var height = document.body.clientHeight - 100;
    return height <= 0 ? 480 : height;
}

$(function () {
    $(".layout-button-left").hide();
});
