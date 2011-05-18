package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.dao.FileUserDetailsService;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.dao.UserInfo;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.util.ChannelsUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/17/11
 * Time: 1:07 PM
 */
public class UserInfoPanel extends AbstractSocialListPanel {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( UserInfoPanel.class );

    private WebMarkupContainer userInfoContainer;
    private UserInfo temp;
    private static final String EMAIL_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$";

    @SpringBean
    private UserDetailsService userDetailsService;
    @SpringBean
    private User user;

    private Pattern emailPattern;
    private boolean passwordOk = false;
    private String newPassword = "";
    private String repeatNewPassword = "";
    private AjaxFallbackLink<String> applyButton;

    public UserInfoPanel( String id, SocialPanel socialPanel, boolean collapsible ) {
        super( id, collapsible );
        init();
    }

    protected void init() {
        super.init();
        resetAll();
    }

    private void resetAll() {
        resetTemp();
        emailPattern = Pattern.compile( EMAIL_REGEX );
        addUserInfoContainer();
        addIdentity();
        addParticipation();
        addPassword();
        addButtons();
    }

    private void resetTemp() {
        temp = new UserInfo( user.getUsername(), user.getUserInfo().toString() );
        newPassword = "";
        repeatNewPassword = "";
    }

    private void addUserInfoContainer() {
        userInfoContainer = new WebMarkupContainer( "userInfo" );
        userInfoContainer.setOutputMarkupId( true );
        addOrReplace( userInfoContainer );
    }

    private void addIdentity() {
        userInfoContainer.add( new TextField<String>( "fullName", new PropertyModel<String>( this, "fullName" ) )
                .add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        adjustFields( target );
                    }
                } )
        );
        TextField<String> emailText = new TextField<String>( "email", new PropertyModel<String>( this, "email" ) );
        emailText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields( target );
            }
        } );
        emailText.add( new AbstractValidator<String>() {
            @Override
            protected void onValidate( IValidatable<String> validatable ) {
                if ( !isValidEmail( validatable.getValue() ) ) {
                    error( validatable, "InvalidEmail" );
                }
            }
        } );
        userInfoContainer.add( emailText );
    }

    private boolean isValidEmail( String value ) {
        Matcher matcher = emailPattern.matcher( value );
        return matcher.matches();
    }

    private void addParticipation() {
        String assignation = getAssignation();
        userInfoContainer.add( new Label( "userRole", getUserRole() ) );
        Label assignationLabel = new Label( "assignation", assignation );
        assignationLabel.setVisible( !assignation.isEmpty() );
        userInfoContainer.add( assignationLabel );


    }

    private String getUserRole() {
        String userRole = user.isAdmin()
                            ? "administrator"
                            : user.isPlanner()
                            ? "planner"
                            : "participant";
        return ( ChannelsUtils.startsWithVowel( userRole ) ? " an " : " a " ) + userRole + ".";
    }

    private String getAssignation() {
        Actor actor = findActor( getQueryService(), user.getUsername() );
        if ( actor != null ) {
            return "I participate as " + actor.getName() + ".";
        } else {
            return "";
        }
    }

    private static Actor findActor( QueryService queryService, String userName ) {
        Participation participation = queryService.findParticipation( userName );
        return participation != null && participation.getActor() != null
                ? participation.getActor()
                : null;
    }


    private void addPassword() {
        userInfoContainer.add( new TextField<String>( "password", new PropertyModel<String>( this, "password" ) )
                .add( new AbstractValidator<String>() {
                    @Override
                    protected void onValidate( IValidatable<String> validatable ) {
                        if ( !validatable.getValue().equals( user.getPassword() ) ) {
                            error( validatable, "IncorrectPassword" );
                        }
                    }
                } ) );
        TextField<String> newPasswordText = new TextField<String>( "newPassword", new PropertyModel<String>( this, "newPassword" ) );
        newPasswordText.add( new AbstractValidator<String>() {
            @Override
            protected void onValidate( IValidatable<String> validatable ) {
                if ( validatable.getValue().length() < 6 ) {
                    error( validatable, "PasswordTooShort" );
                }
            }
        } );
        newPasswordText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields( target );
            }
        } );
        userInfoContainer.add( newPasswordText );
        TextField<String> repeatNewPasswordText = new TextField<String>( "repeatNewPassword", new PropertyModel<String>( this, "repeatNewPassword" ) );
        repeatNewPasswordText.add( new AbstractValidator<String>() {
            @Override
            protected void onValidate( IValidatable<String> validatable ) {
                if ( !validatable.getValue().equals( getNewPassword() ) ) {
                    error( validatable, "DifferentNewPassword" );
                }
            }
        } );
        repeatNewPasswordText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields( target );
            }
        } );
        userInfoContainer.add( repeatNewPasswordText );
    }

    private void addButtons() {
        AjaxFallbackLink<String> reset = new AjaxFallbackLink<String>( "reset" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                resetAll();
                target.addComponent( UserInfoPanel.this );
            }
        };
        userInfoContainer.add( reset );
        applyButton = new AjaxFallbackLink<String>( "apply" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                try {
                    save();
                } catch ( IOException e ) {
                    LOG.error( "Failed to save user info", e );
                    target.appendJavascript( "alert('Failed to save');" );
                }
                resetAll();
                target.addComponent( UserInfoPanel.this );
            }
        };
        userInfoContainer.add( applyButton );
    }

    private void adjustFields( AjaxRequestTarget target ) {
        applyButton.setEnabled( canSave() );
        target.addComponent( applyButton );
    }

    private boolean canSave() {
        return ( newPassword.isEmpty() || isValidNewPassword() ) && isValidEmail( getEmail() );
    }

    private void save() throws IOException {
        if ( !newPassword.isEmpty() && isValidNewPassword() ) {
            temp.setPassword( newPassword );
        }
        if ( canSave() ) {
            user.setUserInfo( temp );
            ( (FileUserDetailsService) userDetailsService ).save();
        }
    }

    private boolean isValidNewPassword() {
        return passwordOk &&
                newPassword.length() > 5
                && repeatNewPassword.equals( newPassword );
    }

    public String getFullName() {
        return temp.getFullName();
    }

    public void setFullName( String val ) {
        if ( !val.isEmpty() ) {
            temp.setFullName( val );
        }
    }

    public String getEmail() {
        return temp.getEmail();
    }

    public void setEmail( String val ) {
        if ( !val.isEmpty() ) {
            temp.setEmail( val );
        }
    }

    public String getPassword() {
        return "";
    }

    public void setPassword( String val ) {
        // todo - save hash of password
        passwordOk = val.equals( user.getPassword() );
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword( String val ) {
        this.newPassword = val;
    }

    public String getRepeatNewPassword() {
        return repeatNewPassword;
    }

    public void setRepeatNewPassword( String val ) {
        this.repeatNewPassword = val;
    }
}
