package com.thorough.core.modules.configuration.shiro;


import com.thorough.library.shiro.filter.SysFormAuthenticationFilter;
import com.thorough.library.shiro.filter.SysLogoutFilter;
import com.thorough.library.shiro.filter.SysUserFilter;
import com.thorough.library.specification.shiro.FilterChainDefinitionMap;
import com.thorough.library.utils.PropertyUtil;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class PathFilterChainDefinitionMap implements FilterChainDefinitionMap {
    @Override
    public Map<String, String> getFilterMap() {

        Map<String,String> map = new LinkedHashMap<>();
        //按照顺序过滤，在前面的被先过滤，如果前面的符合，后面的就不会在过滤，查看源码PathMatchingFilterChainResolver103行
        //同一个路径可以配置多个过滤器
        map.put(PropertyUtil.getProperty("adminPath")+"/servlet/validateCodeServlet","anon");
        map.put(PropertyUtil.getProperty("adminPath")+"/static/**","anon");
        map.put(PropertyUtil.getProperty("adminPath"),"anon");
        map.put(PropertyUtil.getProperty("adminPath")+"/login","authc");
        map.put(PropertyUtil.getProperty("adminPath")+"/logout","logout"); //可用于后端退出请求，退出后直接跳转到指定路径
        map.put(PropertyUtil.getProperty("adminPath")+"/**","user");
        map.put("/act/editor/**","user");
        map.put("/ReportServer/**","user");
        return map;
    }

    @Override
    public Map<String, Filter> getFilter() {
        //类似于CasFilter、FormAuthenticationFilter等filter不该注入到Spring中，如果直接注入到spring中，则会出现在ApplicationFilterChain中
        SysFormAuthenticationFilter authenticationFilter = new SysFormAuthenticationFilter();
        SysLogoutFilter logoutFilter = new SysLogoutFilter();
        logoutFilter.setRedirectUrl(PropertyUtil.getProperty("adminPath")); //设置退出以后进入的页面
        SysUserFilter userFilter = new SysUserFilter();
        Map<String,Filter> filters = new HashMap<>();

        filters.put("authc",authenticationFilter);
        filters.put("logout",logoutFilter);
        filters.put("user",userFilter);
        return filters;
    }
}
