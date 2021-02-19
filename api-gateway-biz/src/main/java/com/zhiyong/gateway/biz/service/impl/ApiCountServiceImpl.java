package com.zhiyong.gateway.biz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhiyong.gateway.biz.service.ApiCountService;
import com.zhiyong.gateway.common.model.PageRequest;
import com.zhiyong.gateway.dal.dao.ApiCountExtMapper;
import com.zhiyong.gateway.dal.dao.ApiCountMapper;
import com.zhiyong.gateway.dal.domain.ApiCount;
import com.zhiyong.gateway.dal.domain.ApiCountExample;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @ClassName ApiCountServiceImpl
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/9 下午2:19
 **/
@Service
public class ApiCountServiceImpl implements ApiCountService {

    @Resource
    private ApiCountMapper apiCountMapper;
    @Resource
    private ApiCountExtMapper apiCountExtMapper;

    @Override
    public int saveApiCount(ApiCount apiCount) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String day = df.format(new Date());
        String method = apiCount.getApiName();
        String version = apiCount.getApiVersion();
        if (StringUtils.isBlank(method) || StringUtils.isBlank(version)) {
            return 0;
        }
        ApiCount count = getApiCountByCondition(day, method, version);
        int res = 0;
        if (count == null) {
            apiCount.setCreateTime(new Date());
            res = apiCountMapper.insertSelective(apiCount);
        } else {
            apiCount.setUpdateTime(new Date());
            apiCount.setId(count.getId());
            res = apiCountMapper.updateByPrimaryKeySelective(apiCount);
        }
        return res;
    }

    private ApiCount getApiCountByCondition(String day, String method, String version) {
        ApiCountExample example = new ApiCountExample();
        example.createCriteria().andCountDateEqualTo(day).andApiNameEqualTo(method).andApiVersionEqualTo(version);
        List<ApiCount> apiCountList = apiCountMapper.selectByExample(example);
        return CollectionUtils.isEmpty(apiCountList) ? null : apiCountList.get(0);
    }

    @Override
    public PageInfo<ApiCount> pagerListApiCount(String apiName, String startTime, String endTime,
                                                PageRequest pageRequest) {
        try {
            PageHelper.startPage(pageRequest.getPage(), pageRequest.getRows());
            String sort = " total_count desc ";
            if (StringUtils.isNotBlank(pageRequest.getSortColumn())) {
                sort = " " + pageRequest.getSortColumn() + " " + pageRequest.getOrder();
            }
            List<ApiCount> apiCountList = apiCountExtMapper.queryApiCount(apiName, startTime, endTime, sort);
            PageInfo<ApiCount> pageInfo = new PageInfo<>(apiCountList);
            return pageInfo;
        } finally {
            PageHelper.clearPage();
        }
    }
}
