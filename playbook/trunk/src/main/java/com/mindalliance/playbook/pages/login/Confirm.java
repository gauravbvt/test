package com.mindalliance.playbook.pages.login;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.pages.MobilePage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.validation.validator.StringValidator.MinimumLengthValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

import javax.servlet.http.HttpServletResponse;

/**
 * The confirmation page for resolving a pending registration.
 */
public class Confirm extends MobilePage {

    private static final Logger LOG = LoggerFactory.getLogger( Confirm.class );

    @SpringBean
    private AccountDao accountDao;

    private String password;

    private String confirm;

    public Confirm( PageParameters parameters ) {
        super( parameters );
        setStatelessHint( true );

        final StringValue key = parameters.get( "key" );
        Account account = accountDao.findByKey( key.toString() );
        if ( account == null )
            throw new AbortWithHttpErrorCodeException(
                HttpServletResponse.SC_FORBIDDEN,
                "Invalid key" );

        setDefaultModel( new CompoundPropertyModel<Confirm>( this ) );

        PasswordTextField pwField = new PasswordTextField( "password" );
        pwField.setRequired( true );
        pwField.add( new MinimumLengthValidator( 5 ) );
        PasswordTextField cField = new PasswordTextField( "confirm" );
        cField.setRequired( true );
        
        Form form = new StatelessForm( "form" ) {
            @Override
            public void onSubmit() {
                Account account = accountDao.findByKey( key.toString() );

                account.setConfirmation( null );
                account.setPassword( getHash( password ) );
                account.setConfirmed( true );

                accountDao.save( account );

                WebResponse response = (WebResponse) getResponse();
                response.sendRedirect( "/logout" );
            }
        };

        add(
            new FeedbackPanel( "feedback" ),
            form.add(
                pwField,
                cField ) );

        form.add(
            new EqualPasswordInputValidator(
                pwField,
                cField ) );
    }

    @Override
    public String getPageTitle() {
        return "Playbook - Account activation";
    }

    static String getHash( String password ) {
        MessageDigestPasswordEncoder encoder = new MessageDigestPasswordEncoder(
            "sha",
            true );
        return encoder.encodePassword(
            password,
            null );
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm( String confirm ) {
        this.confirm = confirm;
    }
}
