package com.thorough.library.system.session;


import com.thorough.library.system.session.adapter.ShiroSessionAdapter;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;

/**
 * 系统层面用SessionManager
 * 具体的Session实现方式可以自定义
 * */

@Component
public class ShiroSessionManager implements SessionManager{

    @Override
    public Session getSession() {
        try{
            Subject subject = SecurityUtils.getSubject();
            //查看subject.getSession是否每次都创建一个新Session
            org.apache.shiro.session.Session session = subject.getSession(false);
            if (session == null){
                session = subject.getSession();
            }
            if (session != null){
                ShiroSessionAdapter sessionAdapter = new ShiroSessionAdapter(session);
                return sessionAdapter;
            }
        }catch (InvalidSessionException e){
            e.printStackTrace();
        }
        return null;
    }

}
