package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.UserUploadService;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Channelable;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.db.data.ContactInfo;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.pages.components.ChannelListPanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
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
    private ChannelsUser temp;
    private static final String EMAIL_REGEX = "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}";


    @SpringBean
    private UserRecordService userInfoService;

    @SpringBean
    private ImagingService imagingService;

    @SpringBean
    private UserUploadService userUploadService;


    private Pattern emailPattern;
    private List<String> errors;
    private WebMarkupContainer errorsContainer;
    private WebMarkupContainer photoImg;
    private ConfirmedAjaxFallbackLink removePhotoButton;
    private WebMarkupContainer uploadContainer;
    private FileUploadField uploadPhotoField;
    private AjaxSubmitLink uploadButton;
    private WebMarkupContainer updatedContactContainer;

    public UserInfoPanel( String id, SocialPanel socialPanel, boolean collapsible ) {
        super( id, collapsible );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return null;  // Todo
    }

    @Override
    public String getHelpTopicId() {
        return null;  // Todo
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
        addUserContactInfo();
        addErrors();
        addButtons();
    }

    private void resetTemp() {
        ChannelsUser user = getUser();
        UserRecord tempUserInfo = new UserRecord( user.getUserRecord() );
        temp = new ChannelsUser( tempUserInfo );
    }

    private void addUserInfoContainer() {
        userInfoContainer = new WebMarkupContainer( "userInfo" );
        userInfoContainer.setOutputMarkupId( true );
        addOrReplace( userInfoContainer );
    }

    private void addIdentity() {
        userInfoContainer.add( new Label( "userId", new Model<String>( getUser().getUserRecord().getUsername() ) ) );
        addFullNameField();
        addEmailField();
        addPhotoFields();
        userInfoContainer.add( makeHelpIcon( "helpIdentity", "about-me", "my-identity", "images/help_guide_gray.png" ) );
    }

    private void addFullNameField() {
        userInfoContainer.add( new TextField<String>( "fullName", new PropertyModel<String>( this, "fullName" ) )
                .add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        adjustFields( target );
                    }
                } )
        );
    }

    private void addEmailField() {
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

    private void addPhotoFields() {
        addPhotoImage();
        addRemoveButton();
        addUploadFields();
    }

    private void addPhotoImage() {
        photoImg = new WebMarkupContainer( "photo" );
        photoImg.setOutputMarkupId( true );
        photoImg.add( new AttributeModifier( "src", new PropertyModel( this, "squaredPhotoSrc" ) ) );
        userInfoContainer.addOrReplace( photoImg );
    }

    private void addRemoveButton() {
        removePhotoButton = new ConfirmedAjaxFallbackLink( "removePhoto", "Remove photo?" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                boolean success = removePhoto();
                adjustFields( target );
                update( target, Change.message( success ? "Photo removed" : "Could not remove photo" ) );
                target.add( userInfoContainer );
            }
        };
        removePhotoButton.setOutputMarkupId( true );
        makeVisible( removePhotoButton, hasPhoto() );
        userInfoContainer.addOrReplace( removePhotoButton );
    }

    private boolean removePhoto() {
        boolean exists = hasPhoto();
        temp.setPhoto( null );
        return exists;
    }

    private void addUploadFields() {
        uploadContainer = new WebMarkupContainer( "uploadContainer" );
        uploadContainer.setOutputMarkupId( true );
        userInfoContainer.addOrReplace( uploadContainer );
        uploadPhotoField = new FileUploadField(
                "uploadPhoto", new PropertyModel<List<FileUpload>>( this, "uploads" ) );
        uploadPhotoField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                makeVisible( uploadButton, true );
                target.add( uploadButton );
            }
        } );
        uploadContainer.add( uploadPhotoField );
        // submit
        uploadButton = new AjaxSubmitLink( "upload" ) {
            @Override
            protected void onSubmit( AjaxRequestTarget target, Form<?> form ) {
                target.add( UserInfoPanel.this );
                update( target, new Change( Change.Type.Unknown ) );
            }

            @Override
            protected void onError( AjaxRequestTarget target, Form<?> form ) {
                addPhotoFields();
                adjustFields( target );
                target.add( userInfoContainer );
                update( target, new Change( Change.Type.Unknown ) );
                // update( target, Change.message( "Failed to upload photo" ) );
            }
        };
        uploadButton.setOutputMarkupId( true );
        makeVisible( uploadButton, false );
        uploadContainer.add( uploadButton );
        makeVisible( uploadContainer, !hasPhoto() );
    }

    /**
     * {@inheritDoc}
     */
    protected void onBeforeRender() {
        super.onBeforeRender();
        makeVisible( uploadContainer, !hasPhoto() );
        makeVisible( removePhotoButton, hasPhoto() );
    }


    public List<FileUpload> getUploads() {
        return null;
    }

    public void setUploads( List<FileUpload> uploads ) {
        /*
      The file uploads received from the client.
     */
        FileUpload upload = uploadPhotoField.getFileUpload();
        if ( upload != null ) {
            LoggerFactory.getLogger( getClass() ).info( "Uploading photo for {}", temp );
            userUploadService.uploadUserPhoto( temp, upload, getCommunityService() );
        }
    }


    public String getSquaredPhotoSrc() {
        String src = null;
        if ( hasPhoto() ) {
            src = userUploadService.getSquareUserIconURL( temp );
        }
        return src == null ? "images/actor.user.png" : src;
    }

    public boolean hasPhoto() {
        return temp.getPhoto() != null;
    }


    private boolean isValidEmail( String value ) {
        Matcher matcher = emailPattern.matcher( value.toUpperCase() );
        return matcher.matches();
    }

    @SuppressWarnings( "unchecked" )
    private List<ContactInfo>findInvalidContactInfos() {
        return (List<ContactInfo>)CollectionUtils.select(
                temp.getUserRecord().getContactInfoList(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (ContactInfo) object ).isValid( getCommunityService() );
                    }
                }
        );
    }

    private void addUserContactInfo() {
        updatedContactContainer = new WebMarkupContainer( "userContact" );
        updatedContactContainer.add(
                new ChannelListPanel(
                        "contactInfo",
                        new Model<Channelable>( new UserChannels(
                                temp.getUserRecord()
                        ) ),
                        false,     // don't allow adding new media
                        true ) );  // restrict to immutable media
        updatedContactContainer.add( makeHelpIcon( "helpContact", "about-me", "my-contact-info", "images/help_guide_gray.png" ) );
        userInfoContainer.add( updatedContactContainer );
    }


    private void addErrors() {
        errorsContainer = new WebMarkupContainer( "errorsContainer" );
        errorsContainer.setOutputMarkupId( true );
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
        userInfoContainer.addOrReplace( errorsContainer );
    }

    private void addButtons() {
        AjaxLink<String> reset = new AjaxLink<String>( "reset1" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                resetAll();
                target.add( UserInfoPanel.this );
            }
        };
        userInfoContainer.add( reset );
        AjaxLink<String> yetAnotherReset = new AjaxLink<String>( "reset2" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                resetAll();
                target.add( UserInfoPanel.this );
            }
        };
        updatedContactContainer.add( yetAnotherReset );

        AjaxLink<String> applyButton = new AjaxLink<String>( "apply1" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                applyChanges( target );
            }
        };
        userInfoContainer.add( applyButton );
        AjaxLink<String> yetAnotherApplyButton = new AjaxLink<String>( "apply2" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                applyChanges( target );
            }
        };
        updatedContactContainer.add( yetAnotherApplyButton );

    }

    private void applyChanges( AjaxRequestTarget target ) {
        try {
            adjustFields( target );
            if ( save() ) {
                Change change = new Change( Change.Type.Updated, getPlan() );
                change.setProperty( "user" );
                change.setMessage( "Changes were applied." );
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
        }
    }

    private void adjustFields( AjaxRequestTarget target ) {
        resetErrors();
        addErrors();
        target.add( errorsContainer );
        makeVisible( removePhotoButton, hasPhoto() );
        target.add( removePhotoButton );
        makeVisible( uploadContainer, !hasPhoto() );
        target.add( uploadContainer );
    }

    private void resetErrors() {
        errors = new ArrayList<String>();
        if ( temp.getFullName().isEmpty() )
            errors.add( "The name is required" );
        if ( temp.getEmail().isEmpty() )
            errors.add( "An email address is required" );
        else if ( !isValidEmail( temp.getEmail() ) )
            errors.add( "The email address is invalid" );
        for ( ContactInfo contactInfo : findInvalidContactInfos() ) {
            errors.add( contactInfo.asChannel( getCommunityService() ).getLabel() + " is not valid");
        }
    }

    private boolean canSave() {
        return !temp.getFullName().isEmpty()
                && isValidEmail( getEmail() )
                && findInvalidContactInfos().isEmpty();
    }

    private boolean save() throws IOException {
        boolean changed = !getUser().getUserRecord().sameAs( temp.getUserRecord() );
        if ( changed && canSave() ) {
            return userInfoService.updateUserRecord(
                    getUser().getUserRecord(),
                    temp.getUserRecord(),
                    getCommunityService() );
        } else {
            return false;
        }
    }


    public String getFullName() {
        return temp.getFullName();
    }

    public void setFullName( String val ) {
        temp.getUserRecord().setFullName( val == null ? "" : val );
    }


    public String getEmail() {
        return temp.getEmail();
    }

    public void setEmail( String val ) {
        temp.getUserRecord().setEmail( val == null ? "" : val );
    }


    //////////////////////////////////////

    public class UserChannels implements Channelable {


        private UserRecord tempUserRecord;

        public UserChannels( UserRecord tempUserRecord ) {
            this.tempUserRecord = tempUserRecord;
        }

        @Override
        public List<Channel> getEffectiveChannels() {
            return getUserRecord().findChannels( getCommunityService() );
        }

        @Override
        public List<Channel> getModifiableChannels() {
            return getEffectiveChannels();
        }

        @Override
        public void addChannel( Channel channel ) {
            getUserRecord().addChannel( channel );
        }

        @Override
        public void removeChannel( Channel channel ) {
            getUserRecord().removeChannel( channel );
        }

        @Override
        public void setAddress( Channel channel, String address ) {
            getUserRecord().setAddress( channel, address );
        }

        @Override
        public boolean canSetFormat() {
            return false;
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
            return getUserRecord().getId();
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
        public String getKindLabel() {
            return getTypeName();
        }

        @Override
        public boolean isModifiableInProduction() {
            return true;
        }

        @Override
        public String getClassLabel() {
            return getClass().getSimpleName();
        }

        @Override
        public String getName() {
            return getUserRecord().getFullName();
        }

        private UserRecord getUserRecord() {
            return tempUserRecord;
        }
    }
}
