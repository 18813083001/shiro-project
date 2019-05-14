package com.thorough.library.system.session.adapter;


import com.thorough.library.system.session.Session;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

public class ShiroSessionAdapter implements Session{

    private final org.apache.shiro.session.Session session;
    public ShiroSessionAdapter(org.apache.shiro.session.Session session){
        this.session = session;
    }

    @Override
    public Serializable getId() {
        return session.getId();
    }

    @Override
    public Date getStartTimestamp() {
        return session.getStartTimestamp();
    }

    @Override
    public Date getLastAccessTime() {
        return session.getLastAccessTime();
    }

    @Override
    public long getTimeout() {
        return session.getTimeout();
    }

    @Override
    public void setTimeout(long maxIdleTimeInMillis) {
        session.setTimeout(maxIdleTimeInMillis);
    }

    @Override
    public String getHost() {
        return session.getHost();
    }

    @Override
    public void touch() {
        session.touch();
    }

    @Override
    public void stop() {
        session.stop();
    }

    @Override
    public Collection<Object> getAttributeKeys() {
        return session.getAttributeKeys();
    }

    @Override
    public Object getAttribute(Object key) {
        return session.getAttribute(key);
    }

    @Override
    public void setAttribute(Object key, Object value) {
        session.setAttribute(key,value);
    }

    @Override
    public Object removeAttribute(Object key) {
        return session.removeAttribute(key);
    }
}
