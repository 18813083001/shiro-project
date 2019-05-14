package com.thorough.library.shiro.configuration;

import com.thorough.library.shiro.cache.JedisCacheManager;
import com.thorough.library.shiro.filter.SysFormAuthenticationFilter;
import com.thorough.library.shiro.filter.SysLogoutFilter;
import com.thorough.library.shiro.filter.SysUserFilter;
import com.thorough.library.shiro.realm.SystemAuthorizingRealm;
import com.thorough.library.shiro.session.RedisSessionDAO;
import com.thorough.library.specification.shiro.FilterChainDefinitionMap;
import com.thorough.library.utils.IdGen;
import com.thorough.library.shiro.session.SessionManager;
import com.thorough.library.utils.PropertyUtil;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;
//import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(prefix = "shiro",name = "shiroName",matchIfMissing=true)
@EnableConfigurationProperties(ShiroProperties.class)
public class ShiroConfigure {


    private final ShiroProperties shiroProperties;

    public ShiroConfigure(ShiroProperties shiroProperties){
        this.shiroProperties = shiroProperties;
    }

    @Bean
    public FilterRegistrationBean delegatingFilterProxy(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("shiroFilter");
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }

    //类似于CasFilter、FormAuthenticationFilter等filter不该注入到Spring中，如果直接注入到spring中，则会出现在ApplicationFilterChain中
    @Bean(name="shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, FilterChainDefinitionMap filterChainDefinitionMap){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl(PropertyUtil.getProperty("adminPath")+"/login");
        shiroFilterFactoryBean.setSuccessUrl(PropertyUtil.getProperty("adminPath")+"/success");

        //类似于CasFilter、FormAuthenticationFilter等filter不该注入到Spring中，如果直接注入到spring中，则会出现在ApplicationFilterChain中
        SysFormAuthenticationFilter authenticationFilter = new SysFormAuthenticationFilter();
        SysLogoutFilter logoutFilter = new SysLogoutFilter();
        logoutFilter.setRedirectUrl(PropertyUtil.getProperty("adminPath")); //设置退出以后进入的页面
        SysUserFilter userFilter = new SysUserFilter();
        Map<String,Filter> filters = filterChainDefinitionMap.getFilter();
//                new HashMap<>();
//        filters.put("authc",authenticationFilter);
//        filters.put("logout",logoutFilter);
//        filters.put("user",userFilter);
        shiroFilterFactoryBean.setFilters(filters);

        Map<String,String> map = filterChainDefinitionMap.getFilterMap();
//                new LinkedHashMap<>();
//        //按照顺序过滤，在前面的被先过滤，如果前面的符合，后面的就不会在过滤，查看源码PathMatchingFilterChainResolver103行
//        //同一个路径可以配置多个过滤器
//        map.put(PropertyUtil.getProperty("adminPath")+"/servlet/validateCodeServlet","anon");
//        map.put(PropertyUtil.getProperty("adminPath")+"/static/**","anon");
//        map.put(PropertyUtil.getProperty("adminPath"),"anon");
//        map.put(PropertyUtil.getProperty("adminPath")+"/login","authc");
//        map.put(PropertyUtil.getProperty("adminPath")+"/logout","logout"); //可用于后端退出请求，退出后直接跳转到指定路径
//        map.put(PropertyUtil.getProperty("adminPath")+"/**","user");
//        map.put("/act/editor/**","user");
//        map.put("/ReportServer/**","user");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }

    @Bean(name="securityManager")
    public SecurityManager securityManager(AuthorizingRealm authorizingRealm,SessionManager sessionManager,
                                           CacheManager cacheManager){
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(authorizingRealm);
        defaultWebSecurityManager.setSessionManager(sessionManager);
        defaultWebSecurityManager.setCacheManager(cacheManager);
        return defaultWebSecurityManager;
    }


    @Bean(name="sessionManager")
    public SessionManager sessionManager(SessionDAO sessionDAO, SimpleCookie sessionIdCookie){
        SessionManager sessionManager = new SessionManager();
        sessionManager.setSessionDAO(sessionDAO);
        sessionManager.setGlobalSessionTimeout(Integer.parseInt(PropertyUtil.getProperty("session.sessionTimeout")));
        sessionManager.setSessionValidationInterval(Integer.parseInt(PropertyUtil.getProperty("session.sessionTimeoutClean")));
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionIdCookie(sessionIdCookie);
        sessionManager.setSessionIdCookieEnabled(true);
        return sessionManager;
    }

    @Bean(name="cacheManager")
    public CacheManager jedisCacheManager(){
        JedisCacheManager jedisCacheManager = new JedisCacheManager();
        return jedisCacheManager;
    }


    @Bean(name="sessionDAO")
    public SessionDAO cacheSessionDAO(){
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setSessionIdGenerator(new IdGen());
        redisSessionDAO.setSessionKeyPrefix(PropertyUtil.getProperty("redis.keyPrefix")+"_session");
        return redisSessionDAO;
    }

    @Bean(name = "authorizingRealm")
    public AuthorizingRealm authorizingRealm(){
        return new SystemAuthorizingRealm();
    }

    @Bean(name="sessionIdCookie")
    public SimpleCookie simpleCookie(){
        SimpleCookie cookie = new SimpleCookie("through.session.id");
        cookie.setPath("/");
        return cookie;
    }

//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
//        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
//        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
//        return authorizationAttributeSourceAdvisor;
//    }

//    @Bean(name="lifecycleBeanPostProcessor")
//    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
//        return new LifecycleBeanPostProcessor();
//    }

}
