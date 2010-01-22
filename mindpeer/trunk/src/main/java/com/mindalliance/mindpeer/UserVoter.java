// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.vote.AccessDecisionVoter;

/**
 * User-based security voter.
 *
 * This voter processes method annotations of the form <code>@Secured( "USER" )</code> and
 * <code>@Secured( "USER==property" )</code>.
 *
 * <p>The first form applies to a method of the {@link com.mindalliance.mindpeer.model.User} class.
 * It succeed when the user object is the same as the authenticated user. It is used for example,
 * to allow a user to change its own password.</p>
 *
 * <p>The second form ensures that the authenticated user is the same as
 * <code>object.getProperty</code>. A path notation can also be used.
 *
 * For example, <code>@Secured( "USER==profile.user" )</code> on an object's method would allow
 * the authenticated user if the same as <code>object.getProfile().getUser()</code>.</p>
 */
public class UserVoter implements AccessDecisionVoter {

    private static final String PREFIX = "USER";

    private static final String LONG_PREFIX = "USER==";

    /**
     * Create a new UserVoter instance.
     */
    public UserVoter() {
    }

    /**
     * Indicates whether this <code>AccessDecisionVoter</code> is able to vote on the passed
     * <code>ConfigAttribute</code>.<p>This allows the <code>AbstractSecurityInterceptor</code> to
     * check every configuration attribute can be consumed by the configured
     * <code>AccessDecisionManager</code> and/or <code>RunAsManager</code> and/or
     * <code>AfterInvocationManager</code>.</p>
     *
     * @param attribute a configuration attribute that has been configured against the
     *        <code>AbstractSecurityInterceptor</code>
     *
     * @return true if the attribute is <code>USER</code> or starts with <code>USER==</code>.
     */
    public boolean supports( ConfigAttribute attribute ) {
        String s = attribute.getAttribute();
        return s != null
            && ( s.equals( PREFIX )
              || s.startsWith( LONG_PREFIX ) && !s.equals( LONG_PREFIX ) );
    }

    /**
     * Indicates whether the <code>AccessDecisionVoter</code> implementation is able to provide
     * access control votes for the indicated secured object type.
     *
     * @param clazz the class that is being queried
     *
     * @return always true
     */
    @SuppressWarnings( { "RawUseOfParameterizedType" } )
    public boolean supports( Class clazz ) {
        return true;
    }

    /**
     * Indicates whether or not access is granted.
     * <p>The decision must be affirmative (<code>ACCESS_GRANTED</code>), negative
     * (<code>ACCESS_DENIED</code>)
     * or the <code>AccessDecisionVoter</code> can abstain (<code>ACCESS_ABSTAIN</code>) from
     * voting. Under no circumstances should implementing classes return any other value. If a
     * weighting of results is desired, this should be handled in a custom
     * {@link org.springframework.security.AccessDecisionManager} instead.
     * </p>
     * <p>Unless an <code>AccessDecisionVoter</code> is specifically intended to vote on an access
     * control
     * decision due to a passed method invocation or configuration attribute parameter, it must
     * return <code>ACCESS_ABSTAIN</code>. This prevents the coordinating
     * <code>AccessDecisionManager</code> from counting votes from those
     * <code>AccessDecisionVoter</code>s without a legitimate interest in the access control
     * decision.
     * </p>
     * <p>Whilst the method invocation is passed as a parameter to maximise flexibility in making
     * access control decisions, implementing classes must never modify the behaviour of the method
     * invocation (such as calling <Code>MethodInvocation.proceed()</code>).</p>
     *
     * @param authentication the caller invoking the method
     * @param object the secured object
     * @param config the configuration attributes associated with the method being invoked
     *
     * @return either {@link #ACCESS_GRANTED}, {@link #ACCESS_ABSTAIN} or {@link #ACCESS_DENIED}
     */
    public int vote(
            Authentication authentication, Object object, ConfigAttributeDefinition config ) {
        Object user = authentication.getPrincipal();

        for ( Object ca : config.getConfigAttributes() ) {
            String attribute = ( (ConfigAttribute) ca ).getAttribute();
            if ( attribute.equals( PREFIX ) ) {
                // Check if object is the principal
                return user.equals( object ) ? ACCESS_GRANTED : ACCESS_DENIED;

            } else if ( attribute.startsWith( LONG_PREFIX ) && !attribute.equals( LONG_PREFIX ) ) {
                // Check if object property is the principal
                String property = attribute.substring( LONG_PREFIX.length() );
                Object value = new BeanWrapperImpl( object ).getPropertyValue( property );
                return user.equals( value ) ? ACCESS_GRANTED : ACCESS_DENIED;
            }
        }

        return ACCESS_ABSTAIN;
    }
}
