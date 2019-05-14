package com.thorough.library.shiro.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shiro", ignoreUnknownFields = false)
public class ShiroProperties {
    private String loginUrl;
    private String successUrl;
    private int sessionTimeout;
    private int sessionTimeoutClean;
    private String shiroName;

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public int getSessionTimeoutClean() {
        return sessionTimeoutClean;
    }

    public void setSessionTimeoutClean(int sessionTimeoutClean) {
        this.sessionTimeoutClean = sessionTimeoutClean;
    }

    public String getShiroName() {
        return shiroName;
    }

    public void setShiroName(String shiroName) {
        this.shiroName = shiroName;
    }
}
