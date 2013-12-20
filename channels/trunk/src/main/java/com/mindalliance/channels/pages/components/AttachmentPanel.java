package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.Attachable;
import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.Attachment.Type;
import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.Upload;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.AttachDocument;
import com.mindalliance.channels.core.command.commands.CopyAttachment;
import com.mindalliance.channels.core.command.commands.DetachDocument;
import com.mindalliance.channels.core.model.AttachmentImpl;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.imaging.ImagingService;
import org.apache.commons.validator.UrlValidator;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * File attachments for a given model object.
 */
public class AttachmentPanel extends AbstractCommandablePanel {

    private final static Logger LOG = LoggerFactory.getLogger( AttachmentPanel.class );

    /**
     * Submit button.
     */
    private AjaxButton submit;
    /**
     * The name of the attachment.
     */
    private String name;
    /**
     * Attachment type choice.
     */
    private DropDownChoice<Type> typeChoice;
    /**
     * Whether to show the new attachment controls.
     */
    private boolean showingControls = false;
    private WebMarkupContainer addAttachmentContainer;
    private Label fileLabel;
    private Label urlLabel;
    private AjaxLink<String> urlKindLink;
    private AjaxLink<String> fileKindLink;
    private AjaxLink<String> attachUrlLink;

    /**
     * Available attachment kind. Each kind should have a corresponding field.
     */
    public enum Kind {
        File,
        URL
    }

    /**
     * The attachment manager.
     */
    @SpringBean
    private AttachmentManager attachmentManager;
    /**
     * Imaging service.
     */
    @SpringBean
    private ImagingService imagingService;

    /**
     * The upload field.
     */
    private FileUploadField uploadField;

    /**
     * The url field.
     */
    private TextField<String> urlField;

    /**
     * The file uploads received from the client.
     */
    private List<FileUpload> uploads;

    /**
     * The attachment kind (e.g. file or url)
     */
    private Kind kind = Kind.File;

    /**
     * The input field for the attachment's name.
     */
    private TextField nameField;

    /**
     * The selected type for the upload.
     */
    private Attachment.Type selectedType = Attachment.Type.Reference;
    /**
     * Overall container.
     */
    private WebMarkupContainer container;
    /**
     * Attachments list container.
     */
    private WebMarkupContainer attachmentsContainer;
    /**
     * Attachment controls container.
     */
    private WebMarkupContainer controlsContainer;
    /**
     * Where documents get attached
     */
    private String attachablePath = "";

    /**
     * Enforces readOnly state if true.
     */
    private boolean readOnly;

    /**
     * The content of the url field.
     */
    private String url;

    public AttachmentPanel(
            String id,
            IModel<? extends ModelObject> model ) {
        this( id, model, "", false );
    }

    public AttachmentPanel(
            String id,
            IModel<? extends ModelObject> model,
            boolean readOnly ) {
        this( id, model, "", readOnly );
    }

    public AttachmentPanel(
            String id,
            IModel<? extends ModelObject> model,
            String attachablePath ) {
        this( id, model, attachablePath, false );
    }

    public AttachmentPanel(
            String id,
            IModel<? extends ModelObject> model,
            String attachablePath,
            boolean readOnly ) {
        super( id, model, null );
        this.readOnly = readOnly;
        this.attachablePath = attachablePath;
        init();
    }

    private void init() {
        container = new WebMarkupContainer( "container" );
        container.setOutputMarkupId( true );
        addOrReplace( container );
        addAttachmentList();
        addNewAttachment();
        adjustFields();
    }

    private void addNewAttachment() {
        addAttachmentContainer = new WebMarkupContainer( "addAttachment" );
        addAttachmentContainer.setOutputMarkupId( true );
        container.addOrReplace( addAttachmentContainer );
        makeVisible( addAttachmentContainer, !readOnly && isLockedByUserIfNeeded( getAttachee() ) );
        addControlsHeader();
        addControls();
    }

    private void addControlsHeader() {
        AjaxLink<String> showControlsLink = new AjaxLink<String>( "showControls" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                showingControls = !showingControls;
                addControls();
                makeVisible( controlsContainer, showingControls );
                target.add( controlsContainer );
            }
        };
        showControlsLink.setOutputMarkupId( true );
        addTipTitle( showControlsLink, "Click to expand or collapse" );
        showControlsLink.add( makeHelpIcon( "help", "user", "attachments", "add-attachment", "images/help_guide_gray.png" ) );
        addAttachmentContainer.addOrReplace( showControlsLink );
    }

    private void addControls() {
        controlsContainer = new WebMarkupContainer( "controls" );
        controlsContainer.setOutputMarkupId( true );
        makeVisible( controlsContainer, showingControls );
        addAttachmentContainer.addOrReplace( controlsContainer );
        addTypeChoice();
        addSelectedKind();
        addKindSelector();
        addNameField();
        addUploadField();
        addUrlField();
        addSubmit();
        addAttachUrl();
        updateNewAttachmentFields();
    }

    /**
     * {@inheritDoc}
     */
    protected void onBeforeRender() {
        super.onBeforeRender();
        adjustFields();
        submit.setEnabled( false );
    }

    private void addSubmit() {
        submit = new AjaxButton( "submit" ) {
            protected void onSubmit( AjaxRequestTarget target, Form<?> form ) {
                if ( uploads != null && !uploads.isEmpty() ) {
                    init();
                    refresh( target );
                    update( target, new Change( Change.Type.Unknown, "attachments" ) );
                }
            }

            @Override
            protected void onError( AjaxRequestTarget target, Form<?> form ) {
                target.appendJavaScript( "alert('Upload failed. Please try again." );
            }
        };
        submit.setOutputMarkupId( true );
        makeVisible( submit, getKind() == Kind.File );
        submit.setEnabled( false );
        controlsContainer.addOrReplace( submit );
    }

    private void addAttachUrl() {
        attachUrlLink = new AjaxLink<String>( "attachUrl" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                if ( url != null && !url.isEmpty() ) {
                    attachUrl();
                    init();
                    refresh( target );
                    update( target, new Change( Change.Type.Unknown, "attachments" ) );
                }
            }
        };
        attachUrlLink.setOutputMarkupId( true );
        makeVisible( attachUrlLink, getKind() == Kind.URL );
        controlsContainer.addOrReplace( attachUrlLink );
    }

    private void adjustFields() {
        makeVisible( uploadField, Kind.File.equals( kind ) );
        makeVisible( urlField, Kind.URL.equals( kind ) );
        makeVisible( attachUrlLink, getKind() == Kind.URL );
        makeVisible( submit, getKind() == Kind.File );
        makeVisible( addAttachmentContainer, !readOnly && isLockedByUserIfNeeded( getAttachee() ) );
        makeVisible( controlsContainer, showingControls );
    }

    private void refresh( AjaxRequestTarget target ) {
        init();
        target.add( container );
    }

    private void addUrlField() {
        urlField = new TextField<String>(
                "url", new PropertyModel<String>( this, "url" ) );
        urlField.setOutputMarkupId( true );
        urlField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        controlsContainer.addOrReplace( urlField );
    }

    private void addNameField() {
        nameField = new TextField<String>(
                "name",
                new PropertyModel<String>( this, "name" )
        );
        nameField.setOutputMarkupId( true );
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        addInputHint( nameField, "An optional name for the attachment" );
        controlsContainer.addOrReplace( nameField );
    }

    private void addUploadField() {
        uploadField = new FileUploadField(
                "upload", new PropertyModel<List<FileUpload>>( this, "uploads" ) );
        uploadField.setOutputMarkupId( true );
        uploadField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                submit.setEnabled( true );
                target.add( submit );
            }
        } );
        controlsContainer.addOrReplace( uploadField );
    }

    private void addTypeChoice() {
        typeChoice = new DropDownChoice<Attachment.Type>( "type",
                new PropertyModel<Attachment.Type>( this, "selectedType" ),
                getAttachee().getAttachmentTypes( attachablePath ),
                new IChoiceRenderer<Attachment.Type>() {
                    public Object getDisplayValue( Attachment.Type object ) {
                        return object.getLabel();
                    }

                    public String getIdValue( Attachment.Type object, int index ) {
                        return Integer.toString( index );
                    }
                }
        );
        typeChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // Do nothing
            }
        } );
        typeChoice.setOutputMarkupId( true );
        controlsContainer.addOrReplace( typeChoice );
    }

    private void addAttachmentList() {
        attachmentsContainer = new WebMarkupContainer( "attachments-container" );
        attachmentsContainer.setOutputMarkupId( true );
        container.addOrReplace( attachmentsContainer );
        ListView<Attachment> attachmentList = new ListView<Attachment>( "attachments",
                new PropertyModel<List<Attachment>>( this, "attachments" ) ) {

            @Override
            protected void populateItem( ListItem<Attachment> item ) {
                Attachment a = item.getModelObject();
                ExternalLink documentLink = new ExternalLink( "attachment",
                        a.getUrl(), attachmentManager.getLabel( getCommunityService(), a ) );
                documentLink.add( new AttributeModifier( "target", new Model<String>( "_" ) ) );
                item.add( documentLink );
                addCopyImage( item );
                addDeleteImage( item );
                item.add( new AttributeModifier(
                        "class", new Model<String>( a.getType().getStyle() ) ) );
                addTipTitle( item, new Model<String>(
                        a.getType().getLabel() + " - " + a.getUrl()
                ) );
            }
        };
        attachmentList.setOutputMarkupId( true );
        makeVisible( attachmentsContainer, !getAttachments().isEmpty() );
        attachmentsContainer.addOrReplace( attachmentList );
    }

    private void addCopyImage( ListItem<Attachment> item ) {
        final Attachment attachment = item.getModelObject();
        AjaxLink copyLink = new AjaxLink( "copy" ) {
            public void onClick( AjaxRequestTarget target ) {
                Change change = doCommand( new CopyAttachment( getUser().getUsername(), attachment ) );
                change.setType( Change.Type.Copied );
                update( target, change );
            }
        };
        addTipTitle( copyLink, "Copy the attachment" );
        makeVisible( copyLink, !readOnly );
        item.add( copyLink );
    }

    private void addDeleteImage( ListItem<Attachment> item ) {
        final Attachment attachment = item.getModelObject();
        ConfirmedAjaxFallbackLink deleteLink = new ConfirmedAjaxFallbackLink(
                "delete",
                "Delete attachment?" ) {
            public void onClick( AjaxRequestTarget target ) {
                doCommand( new DetachDocument( getUser().getUsername(), getAttachee(),
                        attachablePath,
                        attachment ) );
                if ( attachment.isPicture() ) {
                    imagingService.deiconize( getCommunityService(), getAttachee() );
                }
                refresh( target );
                update( target,
                        new Change(
                                Change.Type.Updated,
                                getAttachee(),
                                "attachments"
                        ) );
            }
        };
        addTipTitle( deleteLink, "Remove the attachment" );
        makeVisible( deleteLink, !readOnly && isLockedByUserIfNeeded( getAttachee() ) );
        item.add( deleteLink );
    }

    private void addSelectedKind() {
        fileLabel = new Label( "fileLabel", "File" );
        controlsContainer.add( fileLabel );
        urlLabel = new Label( "urlLabel", "URL" );
        controlsContainer.add( urlLabel );
    }

    private void updateNewAttachmentFields() {
        makeVisible( uploadField, Kind.File.equals( getKind() ) );
        makeVisible( urlField, Kind.URL.equals( getKind() ) );
        makeVisible( fileKindLink, Kind.URL.equals( getKind() ) );
        makeVisible( urlKindLink, Kind.File.equals( getKind() ) );
        makeVisible( fileLabel, Kind.File.equals( getKind() ) );
        makeVisible( urlLabel, Kind.URL.equals( getKind() ) );
        makeVisible( attachUrlLink, getKind() == Kind.URL );
        makeVisible( submit, getKind() == Kind.File );
        submit.setEnabled( Kind.File.equals( getKind() ) );
    }

    private void addKindSelector() {
        urlKindLink = new AjaxLink<String>( "urlKind" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                setKind( Kind.URL );
                url = null;
                updateNewAttachmentFields();
                target.add( AttachmentPanel.this );
            }
        };
        controlsContainer.add( urlKindLink );
        fileKindLink = new AjaxLink<String>( "fileKind" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                setKind( Kind.File );
                url = null;
                updateNewAttachmentFields();
                target.add( AttachmentPanel.this );
            }
        };
        controlsContainer.add( fileKindLink );
    }

    /**
     * Get current attachments to list. Called by component.
     *
     * @return a list of attachments
     */
    public List<Attachment> getAttachments() {
        Attachable attachable = (Attachable) ChannelsUtils.getProperty( getAttachee(), attachablePath, null );
        return attachable.getAttachments();
    }

    public List<FileUpload> getUploads() {
        return uploads;
    }

    /**
     * Set uploads. Called when user attached a file and then submitted.
     *
     * @param uploads the uploaded files info
     */
    public void setUploads( List<FileUpload> uploads ) {
        this.uploads = uploads;
        if ( uploads != null ) {
            for ( final FileUpload upload : uploads ) {
                ModelObject mo = getAttachee();
                LoggerFactory.getLogger( getClass() ).info( "Attaching file to {}", mo );
                Attachment attachment = attachmentManager.upload(
                        getCommunityService(),
                        new Upload() {
                            @Override
                            public Type getSelectedType() {
                                return AttachmentPanel.this.getSelectedType();
                            }

                            @Override
                            public String getName() {
                                return AttachmentPanel.this.getName();
                            }

                            @Override
                            public String getFileName() {
                                return upload.getClientFileName();
                            }

                            @Override
                            public InputStream getInputStream() throws IOException {
                                return upload.getInputStream();
                            }
                        } );
                // Only add non-redundant attachment.
                if ( attachment != null ) {
                    if ( isContentValid( attachment.getUrl() ) ) {
                        doCommand( new AttachDocument( getUser().getUsername(), mo, attachablePath, attachment ) );
                        postProcess( attachment );
                    }
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName( String value ) {
        name = value;
    }

    public Attachment.Type getSelectedType() {
        return selectedType;
    }

    public void setSelectedType( Attachment.Type selectedType ) {
        this.selectedType = selectedType;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind( Kind kind ) {
        this.kind = kind;
    }

    public String getUrl() {
        return url;
    }

    /**
     * Set content of url field. Creates an attachment. Called on submit.
     *
     * @param value the url string
     */
    public void setUrl( String value ) {
        if ( value == null ) {
            url = null;
        } else {
            String newUrl = validateUrl( value );
            if ( newUrl != null )
                this.url = newUrl;
        }
    }

    private String validateUrl( String value ) {
        String wellFormedUrl = null;
        try {
            new URL( value );
            wellFormedUrl = value;

        } catch ( MalformedURLException e ) {
            LOG.warn( "Invalid URL: " + value );
            if ( !value.contains( "://" ) ) {
                wellFormedUrl = "http://" + value;
            }
        }
        if ( wellFormedUrl != null ) {
            wellFormedUrl = wellFormedUrl.replaceAll( "[^\000-\177]", "" ); // get rid of (often invisible) illegal characters
            if ( new UrlValidator().isValid( wellFormedUrl ) )
                return wellFormedUrl;
            else
                return null;
        }
        return wellFormedUrl;
    }

    private boolean isContentValid( String wellFormedUrl ) {
        return getSelectedType() != Type.Image || imagingService.isPicture( wellFormedUrl, getCommunityService() );
    }

    private void attachUrl() {
        if ( url != null && !url.isEmpty() ) {
            if ( isContentValid( url ) ) {
                ModelObject mo = getAttachee();
                LOG.info( "Attaching URL to {}", mo );
                Attachment attachment = new AttachmentImpl( url, getSelectedType(), getName() );
                doCommand( new AttachDocument( getUser().getUsername(), mo, attachablePath, attachment ) );
                postProcess( attachment );
            } else {
                LOG.info( "Invalid content at " + url );
            }
            this.url = null;
            this.name = "";
        }
    }

    private ModelObject getAttachee() {
        return (ModelObject) getDefaultModelObject();
    }

    private void postProcess( Attachment attachment ) {
        ModelObject attachee = getAttachee();
        if ( attachablePath.isEmpty() && attachment.isPicture() && attachee.isIconized() ) {
            imagingService.iconize( getCommunityService(), attachment.getUrl(), attachee );
        }
    }


}
