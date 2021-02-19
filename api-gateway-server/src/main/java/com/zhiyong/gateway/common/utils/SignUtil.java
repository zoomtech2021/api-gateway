package com.zhiyong.gateway.common.utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.exception.GatewayException;

/**
 * @ClassName SignUtil
 * @Description: 签名工具类
 * @Author 毛军锐
 * @Date 2020/11/26 上午10:44
 **/
public class SignUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignUtil.class);
    public static final String SIGN_METHOD_MD5 = "md5";
    public static final String SIGN_METHOD_HMAC = "hmac";
    private static final String CHARSET_UTF8 = "utf-8";


    /**
     * 对TOP请求进行签名。
     */
    public static String signTopRequest(Map<String, String> params, String secret, String signMethod) {
        try {
            // 第一步：检查参数是否已经排序
            String[] keys = params.keySet().toArray(new String[0]);
            Arrays.sort(keys);

            // 第二步：把所有参数名和参数值串在一起
            StringBuilder query = new StringBuilder();
            if (SIGN_METHOD_MD5.equals(signMethod)) {
                query.append(secret);
            }
            for (String key : keys) {
                String value = params.get(key);
                if (isNotEmpty(key) && isNotEmpty(value)) {
                    query.append(key).append(value);
                }
            }

            // 第三步：使用MD5/HMAC加密
            byte[] bytes;
            if (SIGN_METHOD_HMAC.equals(signMethod)) {
                bytes = encryptHmac(query.toString(), secret);
            } else {
                query.append(secret);
                bytes = encryptMD5(query.toString());
            }

            // 第四步：把二进制转化为大写的十六进制
            return byte2hex(bytes);
        } catch (Exception e) {
            LOGGER.error("签名失败：", e);
            throw new GatewayException(ErrorCode.ISP_API_SIGN_FAIL);
        }
    }

    /**
     * 对字节流进行HMAC_MD5摘要。
     */
    private static byte[] encryptHmac(String data, String secret) throws IOException {
        byte[] bytes = null;
        try {
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(CHARSET_UTF8), "HmacMD5");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            bytes = mac.doFinal(data.getBytes(CHARSET_UTF8));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse.toString());
        }
        return bytes;
    }

    /**
     * 对字符串采用UTF-8编码后，用MD5进行摘要。
     */
    private static byte[] encryptMD5(String data) throws IOException {
        return encryptMD5(data.getBytes(CHARSET_UTF8));
    }

    /**
     * 对字节流进行MD5摘要。
     */
    private static byte[] encryptMD5(byte[] data) throws IOException {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            bytes = md.digest(data);
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse.toString());
        }
        return bytes;
    }

    /**
     * 把字节流转换为十六进制表示方式。
     */
    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    private static boolean isNotEmpty(String value) {
        int strLen;
        if (value == null || (strLen = value.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(value.charAt(i)) == false)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> params = new HashMap<>();
        // 公共参数
        params.put("method", "zhiyong.organization.query.tree");
        params.put("appKey", "userCenter");
        params.put("session", "1234");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        params.put("timestamp", df.format(new Date()));
        // params.put("format", "json");
        params.put("version", "1.0");
        params.put("sign_method", "hmac");
        // 业务参数
        params.put("tenantId", "1100");
        // 签名参数
        params.put("sign", signTopRequest(params, "A123.", SIGN_METHOD_HMAC));

        System.out.println(JSON.toJSONString(params));
    }
}
