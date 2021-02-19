package com.zhiyong.gateway.biz.model;

import com.zhiyong.gateway.dal.domain.ApiCfg;
import com.zhiyong.gateway.dal.domain.ApiParamCfg;
import com.zhiyong.gateway.dal.domain.ApiResultCfg;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName ApiCache
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/1 下午8:49
 **/
@Data
public class ApiCache implements Serializable {
    private static final long serialVersionUID = 7374601711950263607L;
    private ApiCfg apiCfg;
    private List<ApiParamCfg> paramCfgs;
    private List<ApiResultCfg> resultCfgs;
}
