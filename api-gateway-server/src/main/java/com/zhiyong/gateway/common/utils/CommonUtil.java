package com.zhiyong.gateway.common.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName CommonUtil
 * @Description: 公共工具类
 * @Author 毛军锐
 * @Date 2020/11/25 下午1:50
 **/
public class CommonUtil {

    /**
     * 生成32位UUID
     *
     * @return
     */
    public static String generateUid() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    /**
     * 获取客户端IP
     *
     * @param request
     * @return
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.equals(ip, "0:0:0:0:0:0:0:1")) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    /**
     * 获取request请求的body内容
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getRequestBody(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte[] buffer = new byte[contentLength];
        for (int ii = 0; ii < contentLength; ) {
            int len = request.getInputStream().read(buffer, ii, contentLength - ii);
            if (len == -1) {
                break;
            }
            ii += len;
        }
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        return new String(buffer, charEncoding);
    }

    /**
     * 获取Decode参数值
     *
     * @param encodeStr
     * @return
     */
    public static String urlDecode(String encodeStr) {
        try {
            return URLDecoder.decode(encodeStr, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return encodeStr;
        }
    }

    /**
     * 获取Decode参数值
     *
     * @param str
     * @return
     */
    public static String urlEncode(String str) {
        try {
            String encodeStr = URLEncoder.encode(str, "utf-8");
            // js和java的encode部分字符有差异
            return StringUtils.replace(encodeStr, "+", "%20");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }
}
