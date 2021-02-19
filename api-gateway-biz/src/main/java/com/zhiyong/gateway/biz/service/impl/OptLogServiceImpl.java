package com.zhiyong.gateway.biz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhiyong.gateway.biz.service.OptLogService;
import com.zhiyong.gateway.common.model.PageRequest;
import com.zhiyong.gateway.dal.dao.OptLogMapper;
import com.zhiyong.gateway.dal.domain.OptLog;
import com.zhiyong.gateway.dal.domain.OptLogExample;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @ClassName OptLogServiceImpl
 * @Description: 操作日志服务
 * @Author 毛军锐
 * @Date 2020/12/22 上午11:28
 **/
@Service
public class OptLogServiceImpl implements OptLogService {

    @Resource
    private OptLogMapper optLogMapper;

    @Override
    public void saveOptLog(OptLog optLog) {
        optLog.setCreateTime(new Date());
        optLogMapper.insertSelective(optLog);
    }

    @Override
    public PageInfo<OptLog> pagerOptLog(String content, PageRequest pageRequest) {
        try {
            PageHelper.startPage(pageRequest.getPage(), pageRequest.getRows());
            OptLogExample example = new OptLogExample();
            OptLogExample.Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(content)) {
                criteria.andContentLike("%" + content + "%");
            }
            example.setOrderByClause(" id desc ");
            if (StringUtils.isBlank(pageRequest.getSortColumn())) {
                example.setOrderByClause(" id desc ");
            } else {
                example.setOrderByClause(" " + pageRequest.getSortColumn() + " " + pageRequest.getOrder());
            }
            List<OptLog> optLogs = optLogMapper.selectByExample(example);
            PageInfo<OptLog> pageInfo = new PageInfo<>(optLogs);
            return pageInfo;
        } finally {
            PageHelper.clearPage();
        }
    }
}
