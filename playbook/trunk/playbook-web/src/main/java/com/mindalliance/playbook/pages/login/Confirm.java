package com.mindalliance.playbook.pages.login;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.pages.TodoPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.http.HttpServletResponse;

/**
 * Backdoor instant login from registration confirmation link.
 */
public class Confirm extends WebPage {

    private static final long serialVersionUID = 5708040952911417703L;

    @SpringBean
    private AccountDao accountDao;

    public Confirm( PageParameters parameters ) {
        super( parameters );
        setStatelessHint( true );

        final StringValue key = parameters.get( "key" );
        Account account = accountDao.findByConfirmation( key.toString() );
        if ( account == null )
            throw new AbortWithHttpErrorCodeException(
                HttpServletResponse.SC_FORBIDDEN, "Invalid key" );

        account.setConfirmed( true );
        account.setConfirmation( null );
        accountDao.save( account );

        UserDetails details = accountDao.getDetails( account );

        Authentication token = new PreAuthenticatedAuthenticationToken( details, null, details.getAuthorities() );
        token.setAuthenticated( true );
        SecurityContextHolder.getContext().setAuthentication( token );
               
        setResponsePage( TodoPage.class );
    }
}
