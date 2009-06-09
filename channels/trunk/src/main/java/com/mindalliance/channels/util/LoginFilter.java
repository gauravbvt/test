package com.mindalliance.channels.util;

import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.ui.webapp.AuthenticationProcessingFilter;

/**
 * A filter that switch to the proper default page given the user's role.
 */
public class LoginFilter  extends AuthenticationProcessingFilter {

    /** The default modeler role. */
    private static final String ROLE_PLANNER = "ROLE_PLANNER";

    /** The URL to redirect to for the modeler role. */
    private String modelerUrl = "";

    /** The modeler role to test for. */
    private String modelerRole = ROLE_PLANNER;

    public LoginFilter() {
    }

    /**
     * @return either the modeler URL of the default target URL
     */
    @Override
    public String getDefaultTargetUrl() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.isAuthenticated() && containsRole( authentication, modelerRole ) ?
               modelerUrl : super.getDefaultTargetUrl();
    }

    /**
     * Test for a given role string in an authentication object.
     * @param authentication the authentication
     * @param role the role
     * @return true is role is included
     */
    public static boolean containsRole( Authentication authentication, String role ) {
        for ( GrantedAuthority a : authentication.getAuthorities() )
            if ( a.getAuthority().equals( role ) )
                return true;

        return false;
    }

    public String getModelerUrl() {
        return modelerUrl;
    }

    public void setModelerUrl( String modelerUrl ) {
        this.modelerUrl = modelerUrl;
    }

    public String getModelerRole() {
        return modelerRole;
    }

    public void setModelerRole( String modelerRole ) {
        this.modelerRole = modelerRole;
    }
}
