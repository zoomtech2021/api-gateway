package com.zhiyong.gateway.biz.cache;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.zhiyong.gateway.biz.service.ApiConfigService;
import com.zhiyong.gateway.dal.domain.ApiCfg;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @ClassName FlowControlRuleCache
 * @Description: 流控规则缓存
 * @Author 毛军锐
 * @Date 2021/1/12 下午3:06
 **/
@Component
public class FlowControlRuleCache {

    @Resource
    private ApiConfigService apiConfigService;

    @PostConstruct
    private void initRule() {
        List<ApiCfg> apiCfgList = apiConfigService.listApi();
        if (CollectionUtils.isEmpty(apiCfgList)) {
            return;
        }

        List<FlowRule> ruleList = FlowRuleManager.getRules();
        for (ApiCfg apiCfg : apiCfgList) {
            String resource = apiCfg.getApiName() + ":" + apiCfg.getApiVersion();
            FlowRule rule = new FlowRule();
            rule.setResource(resource);
            rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
            rule.setCount(apiCfg.getCallLimit());
            ruleList.add(rule);
        }
        FlowRuleManager.loadRules(ruleList);
    }

    /**
     * 更新限流规则
     *
     * @param apiCfg
     */
    public void updateRule(ApiCfg apiCfg) {
        if (apiCfg == null) {
            return;
        }

        List<FlowRule> ruleList = FlowRuleManager.getRules();
        Map<String, FlowRule> flowRuleMap = ruleList.stream().collect(
                Collectors.toMap(FlowRule::getResource, flowRule -> flowRule));

        String resource = apiCfg.getApiName() + ":" + apiCfg.getApiVersion();
        FlowRule rule = flowRuleMap.get(resource);
        if (rule == null) {
            rule = new FlowRule();
            rule.setResource(resource);
            rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
            rule.setCount(apiCfg.getCallLimit());
            ruleList.add(rule);
            FlowRuleManager.loadRules(ruleList);
        } else if (rule.getCount() != apiCfg.getCallLimit()) {
            rule.setCount(apiCfg.getCallLimit());
            FlowRuleManager.loadRules(ruleList);
        }
    }
}
