function doSign(params, appSecret) {
    if (!(typeof params == "object")) {
        alert('签名参数格式错误');
        return;
    }
    var paramArr = [];
    for (var key in params) {
        if(key == 'session' || key == 'tenantId'){
            continue;
        }
        var value = params[key];
        if (value && value != '') {
            paramArr.push(key + params[key]);
        }
    }
    var sortParams = paramArr.sort().join('');
    var hmacSign = hex_hmac_md5(appSecret, sortParams);
    return hmacSign.toUpperCase();
}
