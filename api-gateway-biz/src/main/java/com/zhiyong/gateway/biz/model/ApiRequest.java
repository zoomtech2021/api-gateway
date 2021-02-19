package com.zhiyong.gateway.biz.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName ApiRequest
 * @Description: api请求对象
 * @Author 毛军锐
 * @Date 2020/11/24 下午8:26
 **/
@Data
public class ApiRequest extends BaseRequest {
    /**
     * 请求方标识
     */
    private String appKey;
    /**
     * 参数签名
     */
    private String sign;
    /**
     * 请求API名称
     */
    private String method;
    /**
     * 登录token
     */
    private String session;
    /**
     * 时间戳
     */
    private String timestamp;
    /**
     * 版本号
     */
    private String version;

    /**
     * 租户ID
     */
    private String tenantId;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(super.getClientIp())) {
            sb.append("[ip=").append(super.getClientIp()).append("]");
        }
        if (StringUtils.isNotBlank(super.getDeviceId())) {
            sb.append("[deviceId=").append(super.getDeviceId()).append("]");
        }
        if (StringUtils.isNotBlank(super.getOs())) {
            sb.append("[os=").append(super.getOs()).append("]");
        }
        if (StringUtils.isNotBlank(super.getOsVersion())) {
            sb.append("[osVersion=").append(super.getOsVersion()).append("]");
        }
        if (StringUtils.isNotBlank(super.getUserAgent())) {
            sb.append("[userAgent=").append(super.getUserAgent()).append("]");
        }
        if (StringUtils.isNotBlank(super.getReferer())) {
            sb.append("[referer=").append(super.getReferer()).append("]");
        }
        if (StringUtils.isNotBlank(appKey)) {
            sb.append("[appKey=").append(appKey).append("]");
        }
        if (StringUtils.isNotBlank(sign)) {
            sb.append("[sign=").append(sign).append("]");
        }
        if (StringUtils.isNotBlank(session)) {
            sb.append("[session=").append(session).append("]");
        }
        if (StringUtils.isNotBlank(version)) {
            sb.append("[version=").append(version).append("]");
        }
        return sb.toString();
    }
}
