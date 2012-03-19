package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.dao.user.UserContactInfoService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Channelable;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.components.ChannelListPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private ChannelsUserInfo temp;
    private static final String EMAIL_REGEX = "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}";

    @SpringBean
    private ChannelsUser user;

    @SpringBean
    private ChannelsUserDao userDao;

    @SpringBean
    private UserContactInfoService userContactInfoService;

    @SpringBean
    private PlanParticipationService planParticipationService;

    private Pattern emailPattern;
    private boolean passwordOk = false;
    private String newPassword = "";
    private String repeatNewPassword = "";
    private TextField<String> newPasswordText;
    private TextField<String> repeatNewPasswordText;
    private List<String> errors;
    private WebMarkupContainer errorsContainer;

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
        errors = new ArrayList<String>();
        emailPattern = Pattern.compile( EMAIL_REGEX );
        addUserInfoContainer();
        addIdentity();
        addUserRole();
        addParticipation();
        addUserContactInfo();
        addPassword();
        addErrors();
        addButtons();
    }

    private void resetTemp() {
        temp = new ChannelsUserInfo(
                user.getUsername(),
                user.getFullName(),
                user.getEmail()
        );
        newPassword = "";
        repeatNewPassword = "";
    }

    private void addUserInfoContainer() {
        userInfoContainer = new WebMarkupContainer( "userInfo" );
        userInfoContainer.setOutputMarkupId( true );
        addOrReplace( userInfoContainer );
    }

    private void addIdentity() {
        userInfoContainer.add( new Label( "userId", new Model<String>( user.getUserInfo().getUsername() ) ) );
        userInfoContainer.add( new TextField<String>( "fullName", new PropertyModel<String>( this, "fullName" ) )
                .add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        adjustFields( target );
                    }
                } )
        );
        final TextField<String> emailText = new TextField<String>( "email", new PropertyModel<String>( this, "email" ) );
        emailText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields( target );
                target.add( emailText );
            }
        } );
        userInfoContainer.add( emailText );
    }

    private boolean isValidEmail( String value ) {
        Matcher matcher = emailPattern.matcher( value.toUpperCase() );
        return matcher.matches();
    }

    private void addUserRole() {
        userInfoContainer.add( new Label( "userRole", getUserRole() ) );
    }

    private void addParticipation() {
        ListView<PlanParticipation> participationList = new ListView<PlanParticipation>(
                "participations",
                planParticipationService.getParticipations( getPlan(), getUser().getUserInfo(), getQueryService() )
        ) {
            @Override
            protected void populateItem( ListItem<PlanParticipation> item ) {
                PlanParticipation participation = item.getModelObject();
                String assignation = getAssignation( participation.getActor( getQueryService() ) );
                item.add( new Label( "participation", assignation ) );
            }
        };
        userInfoContainer.add( participationList );
    }

    private void addUserContactInfo() {
        WebMarkupContainer updatedContactContainer = new WebMarkupContainer( "userContact" );
        updatedContactContainer.add(
                new ChannelListPanel(
                        "contactInfo",
                        new Model<Channelable>( new UserChannels(
                                getUser().getUserInfo()
                        ) ),
                        false ) );
        userInfoContainer.add( updatedContactContainer );
    }

    private String getUserRole() {
        String userRole = user.isAdmin()
                ? "administrator (and in all other plans)"
                : user.isPlanner()
                ? "planner"
                : "participant";
        return ( ChannelsUtils.startsWithVowel( userRole ) ? " an " : " a " ) + userRole + ".";
    }

    private String getAssignation( Actor actor ) {
        StringBuilder sb = new StringBuilder();
        if ( actor != null ) {
            sb.append( "I participate as " );
            sb.append( actor.getName() );
            String channelsString = actor.getChannelsString();
            if ( !channelsString.isEmpty() ) {
                sb.append( ", reachable via " );
                sb.append( channelsString );
            }
            sb.append( '.' );
        }
        return sb.toString();
    }


    private void addPassword() {
        PasswordTextField passwordField = new PasswordTextField( "password", new PropertyModel<String>( this, "password" ) );
        passwordField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields( target );
            }
        } );
        userInfoContainer.add( passwordField );
        newPasswordText = new TextField<String>( "newPassword", new PropertyModel<String>( this, "newPassword" ) );
        newPasswordText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields( target );
            }
        } );
        newPasswordText.setEnabled( false );
        userInfoContainer.add( newPasswordText );
        repeatNewPasswordText = new TextField<String>( "repeatNewPassword", new PropertyModel<String>( this, "repeatNewPassword" ) );
        repeatNewPasswordText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields( target );
            }
        } );
        repeatNewPasswordText.setEnabled( false );
        userInfoContainer.add( repeatNewPasswordText );
    }

    private void addErrors() {
        errorsContainer = new WebMarkupContainer( "errorsContainer" );
        errorsContainer.setOutputMarkupId( true );
        userInfoContainer.addOrReplace( errorsContainer );
        ListView<String> errorsList = new ListView<String>(
                "errors",
                errors ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                item.add( new Label( "error", new Model<String>( item.getModelObject() ) ) );
            }
        };
        errorsContainer.add( errorsList );
        makeVisible( errorsContainer, !errors.isEmpty() );
    }

    private void addButtons() {
        AjaxFallbackLink<String> reset = new AjaxFallbackLink<String>( "reset1" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                resetAll();
                target.add( UserInfoPanel.this );
            }
        };
        userInfoContainer.add( reset );
        AjaxFallbackLink<String> otherReset = new AjaxFallbackLink<String>( "reset2" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                resetAll();
                target.add( UserInfoPanel.this );
            }
        };
        userInfoContainer.add( otherReset );
        AjaxFallbackLink<String> applyButton = new AjaxFallbackLink<String>( "apply1" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                applyChanges( target );
            }
        };
        userInfoContainer.add( applyButton );
        AjaxFallbackLink<String> otherApplyButton = new AjaxFallbackLink<String>( "apply2" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                applyChanges( target );
            }
        };
        userInfoContainer.add( otherApplyButton );
    }

    private void applyChanges( AjaxRequestTarget target ) {
        try {
            if ( save() ) {
                Change change = new Change( Change.Type.Updated, getPlan() );
                change.setProperty( "user" );
                if ( !newPassword.isEmpty() ) {
                    change.setMessage( isValidNewPassword()
                            ? "Your password is changed."
                            : "Your password was NOT changed (new password not confirmed)." );
                }
                resetAll();
                target.add( UserInfoPanel.this );
                update( target, change );
            } else {
                Change change = new Change( Change.Type.None );
                change.setMessage( "No changes were made" );
                update( target, change );
            }
        } catch ( IOException e ) {
            LOG.error( "Failed to save user info", e );
            target.appendJavaScript( "alert('Failed to save');" );
            target.add( UserInfoPanel.this );
        }    }

    private void adjustFields( AjaxRequestTarget target ) {
        resetErrors();
        addErrors();
        target.add( errorsContainer );
        // applyButton.setEnabled( canSave() );
        // target.add( applyButton );
        newPasswordText.setEnabled( passwordOk );
        repeatNewPasswordText.setEnabled( passwordOk );
        target.add( newPasswordText );
        target.add( repeatNewPasswordText );
    }

    private void resetErrors() {
        errors = new ArrayList<String>();
        if ( temp.getFullName().isEmpty() )
            errors.add( "The name is required" );
        if ( temp.getEmail().isEmpty() )
            errors.add( "An email address is required" );
        else if ( !isValidEmail( temp.getEmail() ) )
            errors.add( "The email address is invalid" );
        if ( !newPassword.isEmpty() && !repeatNewPassword.isEmpty() && !isValidNewPassword() ) {
            if ( !newPassword.equals( repeatNewPassword ) )
                errors.add( "The new password is not correctly repeated" );
            else
                errors.add( "The new password must have at least 6 characters" );
        }/* else {
            errors.add( "Provide a new password and confirm it" );
        }*/
    }

    private boolean isRobustPassword( String val ) {
        return val.length() > 5;
    }

    private boolean canSave() {
        return !temp.getFullName().isEmpty()
                //      && ( newPassword.isEmpty() || isValidNewPassword() )
                && isValidEmail( getEmail() );
    }

    private boolean save() throws IOException {
        if ( !newPassword.isEmpty() && isValidNewPassword() ) {
            temp.setPassword( newPassword );
        }
        if ( canSave() ) {
            userDao.updateIdentity( user.getUserInfo(), temp );
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidNewPassword() {
        return passwordOk &&
                isRobustPassword( newPassword )
                && repeatNewPassword.equals( newPassword );
    }

    public String getFullName() {
        return temp.getFullName();
    }

    public void setFullName( String val ) {
        temp.setFullName( val == null ? "" : val );
    }

    public String getEmail() {
        return temp.getEmail();
    }

    public void setEmail( String val ) {
        temp.setEmail( val == null ? "" : val );
    }

    public String getPassword() {
        return "";
    }

    public void setPassword( String val ) {
        temp.setPassword( val == null ? "" : val );
        passwordOk = temp.getPassword().equals( user.getPassword() );
        newPassword = "";
        repeatNewPassword = "";
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword( String val ) {
        newPassword = val == null ? "" : val;
        repeatNewPassword = "";
    }

    public String getRepeatNewPassword() {
        return repeatNewPassword;
    }

    public void setRepeatNewPassword( String val ) {
        repeatNewPassword = val == null ? "" : val;
    }


    //////////////////////////////////////

    public class UserChannels implements Channelable {


        private ChannelsUserInfo userInfo;

        public UserChannels( ChannelsUserInfo userInfo ) {
            this.userInfo = userInfo;
        }

        @Override
        public List<Channel> getEffectiveChannels() {
            return userContactInfoService.findChannels( getUserInfo(), getQueryService() );
        }

        @Override
        public List<Channel> getModifiableChannels() {
            return getEffectiveChannels();
        }

        @Override
        public void addChannel( Channel channel ) {
            userContactInfoService.addChannel( getUserInfo().getUsername(), getUserInfo(), channel );
        }

        @Override
        public void removeChannel( Channel channel ) {
            userContactInfoService.removeChannel( getUserInfo(), channel );
        }

        @Override
        public void setAddress( Channel channel, String address ) {
            userContactInfoService.setAddress( getUserInfo(), channel, address );
        }

       @Override
        public String getChannelsString() {
            return Channel.toString( getEffectiveChannels() );
        }

        @Override
        public List<Channel> allChannels() {
            return getEffectiveChannels();
        }

        @Override
        public boolean canBeUnicast() {
            return true;
        }

        @Override
        public boolean canSetChannels() {
            return true;
        }

        @Override
        public String validate( Channel channel ) {
            return channel.isValid() ? null : "Invalid address";
        }

        @Override
        public boolean isEntity() {
            return false;
        }

        @Override
        public boolean isModelObject() {
            return false;
        }

        @Override
        public boolean hasChannelFor( final TransmissionMedium medium, final Place planLocale ) {
            return CollectionUtils.exists(
                    getEffectiveChannels(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (Channel) object ).getMedium().narrowsOrEquals( medium, planLocale );
                        }
                    }
            );
        }

        @Override
        public boolean canBeLocked() {
            return false;
        }

        @Override
        public boolean hasAddresses() {
            return true;
        }

        @Override
        public long getId() {
            return getUserInfo().getId();
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public String getTypeName() {
            return "user contact info";
        }

        @Override
        public boolean isModifiableInProduction() {
            return true;
        }

        @Override
        public String getName() {
            return getUserInfo().getFullName();
        }

        private ChannelsUserInfo getUserInfo() {
            return userInfo;
        }
    }
}
