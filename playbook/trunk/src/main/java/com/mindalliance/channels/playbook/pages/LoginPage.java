package com.mindalliance.channels.playbook.pages;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.authentication.pages.SignInPage;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.request.target.basic.EmptyRequestTarget;

import java.util.Map;

/**
 * ...
 */
public class LoginPage extends SignInPage {

   public LoginPage() {
       super();
//       if( ( (PlaybookSession) getSession() ).isSignedIn())
//       {
//           // redirect to hide username and password from URL after user is logged in
//           setRedirect( true );
//           setResponsePage( PlaybookPage.class );
//       }
//       else
//       {
//           redirectToSecurityCheck();
//       }
   }

    /**
     * Common servlet login workaround
     */
    private void redirectToSecurityCheck()
    {
        final Map parametersMap = ( (WebRequestCycle) RequestCycle.get() ).getWebRequest().getHttpServletRequest().getParameterMap();
        if( parametersMap.containsKey( "username" ) && parametersMap.containsKey( "password" ) )
        {
            // getting parameters from POST request
            final String userName = ( ( String[] )parametersMap.get( "username" ) )[ 0 ];
            final String userPassword = ( ( String[] )parametersMap.get( "password" ) )[ 0 ];

            // if POST parameters are ok, redirect them to j_security_check
            if( ( userName != null ) && ( userPassword != null ) )
            {
                getRequestCycle().setRedirect( false );
                getRequestCycle().setRequestTarget( EmptyRequestTarget.getInstance() );

                getResponse().redirect(
                        "/j_security_check?j_username=" + userName + "&j_password=" + userPassword );
            }
        }
    }
}