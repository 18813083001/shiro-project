package com.thorough.library.system.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

public interface Session {

    /**
     * Returns the unique identifier assigned by the system upon session creation.
     * <p/>
     * All return values from this method are expected to have proper {@code toString()},
     * {@code equals()}, and {@code hashCode()} implementations. Good candidates for such
     * an identifier are {@link java.util.UUID UUID}s, {@link java.lang.Integer Integer}s, and
     * {@link java.lang.String String}s.
     *
     * @return The unique identifier assigned to the session upon creation.
     */
    Serializable getId();

    /**
     * Returns the time the session was started; that is, the time the system created the instance.
     *
     * @return The time the system created the session.
     */
    Date getStartTimestamp();

    /**
     * Returns the last time the application received a request or method invocation from the user associated
     * with this session.  Application calls to this method do not affect this access time.
     *
     * @return The time the user last interacted with the system.
     * @see #touch()
     */
    Date getLastAccessTime();

    /**
     * Returns the time in milliseconds that the session session may remain idle before expiring.
     * <ul>
     * <li>A negative return value means the session will never expire.</li>
     * <li>A non-negative return value (0 or greater) means the session expiration will occur if idle for that
     * length of time.</li>
     * </ul>
     * @return the time in milliseconds the session may remain idle before expiring.
     */
    long getTimeout();

    /**
     * Sets the time in milliseconds that the session may remain idle before expiring.
     * <ul>
     * <li>A negative value means the session will never expire.</li>
     * <li>A non-negative value (0 or greater) means the session expiration will occur if idle for that
     * length of time.</li>
     * </ul>
     * <p/>
     * @param maxIdleTimeInMillis the time in milliseconds that the session may remain idle before expiring.
     * @since 0.2
     */
    void setTimeout(long maxIdleTimeInMillis);

    /**
     * Returns the host name or IP string of the host that originated this session, or {@code null}
     * if the host is unknown.
     *
     * @return the host name or IP string of the host that originated this session, or {@code null}
     *         if the host address is unknown.
     */
    String getHost();

    /**
     * Explicitly updates the {@link #getLastAccessTime() lastAccessTime} of this session to the current time when
     * this method is invoked.  This method can be used to ensure a session does not time out.
     * <p/>
     * Most programmers won't use this method directly and will instead rely on the last access time to be updated
     * automatically as a result of an incoming web request or remote procedure call/method invocation.
     * <p/>
     * However, this method is particularly useful when supporting rich-client applications such as
     * Java Web Start app, Java or Flash applets, etc.  Although rare, it is possible in a rich-client
     * environment that a user continuously interacts with the client-side application without a
     * server-side method call ever being invoked.  If this happens over a long enough period of
     * time, the user's server-side session could time-out.  Again, such cases are rare since most
     * rich-clients frequently require server-side method invocations.
     * <p/>
     * In this example though, the user's session might still be considered valid because
     * the user is actively &quot;using&quot; the application, just not communicating with the
     * server. But because no server-side method calls are invoked, there is no way for the server
     * to know if the user is sitting idle or not, so it must assume so to maintain session
     * integrity.  This {@code touch()} method could be invoked by the rich-client application code during those
     * times to ensure that the next time a server-side method is invoked, In short terms, it could be used periodically
     * to ensure a session does not time out.
     * <p/>
     * How often this rich-client &quot;maintenance&quot; might occur is entirely dependent upon
     * the application and would be based on variables such as session timeout configuration,
     * usage characteristics of the client application, network utilization and application server
     * performance.
     *
     */
    void touch();

    /**
     * Explicitly stops (invalidates) this session and releases all associated resources.
     * <p/>
     * If this session has already been authenticated (i.e. the {@code Subject} that
     * owns this session has logged-in), calling this method explicitly might have undesired side effects:
     * <p/>
     * It is common for a {@code Subject} implementation to retain authentication state in the
     * {@code Session}.  If the session
     * is explicitly stopped by application code by calling this method directly, it could clear out any
     * authentication state that might exist, thereby effectively &quot;unauthenticating&quot; the {@code Subject}.
     * <p/>
     */
    void stop();

    /**
     * Returns the keys of all the attributes stored under this session.  If there are no
     * attributes, this returns an empty collection.
     */
    Collection<Object> getAttributeKeys() ;

    /**
     * Returns the object bound to this session identified by the specified key.  If there is no
     * object bound under the key, {@code null} is returned.
     */
    Object getAttribute(Object key) ;

    /**
     * Binds the specified {@code value} to this session, uniquely identified by the specified
     * {@code key} name.  If there is already an object bound under the {@code key} name, that
     * existing object will be replaced by the new {@code value}.
     */
    void setAttribute(Object key, Object value);

    /**
     * Removes (unbinds) the object bound to this session under the specified {@code key} name.
     *
     */
    Object removeAttribute(Object key);
}
