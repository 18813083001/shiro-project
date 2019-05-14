package com.thorough.library.specification.system;

import com.thorough.library.system.model.entity.User;
import com.thorough.library.system.utils.UserUtils;

import java.io.Serializable;

/**
 * 授权用户信息
 */
public class Principal implements Serializable {
    private static final long serialVersionUID = 7247714666080613254L;
    private String id; // 编号
    private String loginName; // 登录名
    private String name; // 姓名
    private boolean mobileLogin; // 是否手机登录

    //		private Map<String, Object> cacheMap;

    public Principal(User user, boolean mobileLogin) {
        this.id = user.getId();
        this.loginName = user.getLoginName();
        this.name = user.getName();
        this.mobileLogin = mobileLogin;
    }

    public String getId() {
        return id;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getName() {
        return name;
    }

    public boolean isMobileLogin() {
        return mobileLogin;
    }


    /**
     * 获取SESSIONID
     */
    public String getSessionid() {
        try{
            return (String) UserUtils.getSession().getId();
        }catch (Exception e) {
            return "";
        }
    }


    @Override
    public String toString() {
        return id;
    }

}
