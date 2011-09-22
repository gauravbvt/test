package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.Attachable;
import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.Attachment.Type;
import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.Upload;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.AttachmentImpl;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.AttachDocument;
import com.mindalliance.channels.core.command.commands.CopyAttachment;
import com.mindalliance.channels.core.command.commands.DetachDocument;
import com.mindalliance.channels.engine.imaging.ImagingService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
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
import java.util.Arrays;
import java.util.List;

/**
 * File attachments for a given model object.
 */
public class AttachmentPanel extends AbstractCommandablePanel {

    /**
     * Submit button.
     */
    private AjaxButton submit;
    /**
     * The name of the attachment.
     */
    private String name;

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
     * The file upload received from the client.
     */
    private FileUpload upload;

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
     * The content of the url field.
     */
    private String url;

    public AttachmentPanel(
            String id,
            IModel<? extends ModelObject> model ) {
        this( id, model, "" );
    }

    public AttachmentPanel(
            String id,
            IModel<? extends ModelObject> model,
            String attachablePath ) {
        super( id, model, null );
        this.attachablePath = attachablePath;
        setOutputMarkupId( true );
        container = new WebMarkupContainer( "container" );
        container.setOutputMarkupId( true );
        add( container );
        addAttachmentList();
        controlsContainer = new WebMarkupContainer( "controls" );
        controlsContainer.setOutputMarkupId( true );
        makeVisible( controlsContainer, isLockedByUserIfNeeded( getAttachee() ) );
        container.add( controlsContainer );
        addTypeChoice();
        addKindSelector();
        addNameField();
        addUploadField();
        addUrlField();
        addSubmit();
        // adjustFields();
    }

    /**
     * {@inheritDoc}
     */
    protected void onBeforeRender() {
        super.onBeforeRender();
        adjustFields();
        makeVisible( submit, false );
    }

    private void addSubmit() {
        submit = new AjaxButton( "submit" ) {
            protected void onSubmit( AjaxRequestTarget target, Form<?> form ) {
                update( target, new Change( Change.Type.Unknown ) );
            }
        };
        submit.setOutputMarkupId( true );
        submit.setEnabled( false );
        controlsContainer.add( submit );
    }

    private void adjustFields() {
        makeVisible( submit, false );
        makeVisible( uploadField, Kind.File.equals( kind ) );
        makeVisible( urlField, Kind.URL.equals( kind ) );
        makeVisible( controlsContainer, isLockedByUserIfNeeded( getAttachee() ) );
    }

    private void refresh( AjaxRequestTarget target ) {
        adjustFields();
        target.addComponent( nameField );
        target.addComponent( controlsContainer );
        target.addComponent( uploadField );
        target.addComponent( urlField );
        target.addComponent( submit );
        target.addComponent( attachmentsContainer );
    }

    private void addUrlField() {
        urlField = new TextField<String>(
                "url", new PropertyModel<String>( this, "url" ) );
        urlField.setOutputMarkupId( true );
        urlField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                refresh( target );
                update( target, new Change(
                        Change.Type.Updated,
                        getAttachee(),
                        "attachmentTickets"
                ) );
            }
        } );
        controlsContainer.add( urlField );
    }

    private void addNameField() {
        nameField = new TextField<String>(
                "name",
                new PropertyModel<String>( this, "name" )
        );
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                /*refresh( target );
               update( target, new Change(
                       Change.Type.Updated,
                       getAttachee(),
                       "attachmentTickets"
               ) );*/
            }
        } );
        controlsContainer.add( nameField );
    }

    private void addUploadField() {
        uploadField = new FileUploadField(
                "upload", new PropertyModel<FileUpload>( this, "upload" ) );
        uploadField.setOutputMarkupId( true );
        uploadField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                makeVisible( submit, true );
                submit.setEnabled( true );
                target.addComponent( submit );
            }
        } );
        controlsContainer.add( uploadField );
    }

    private void addTypeChoice() {
        DropDownChoice<Attachment.Type> typeChoice = new DropDownChoice<Attachment.Type>( "type",
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
        controlsContainer.add( typeChoice );
    }

    private void addAttachmentList() {
        attachmentsContainer = new WebMarkupContainer( "attachments-container" );
        attachmentsContainer.setOutputMarkupId( true );
        container.add( attachmentsContainer );
        ListView<Attachment> attachmentList = new ListView<Attachment>( "attachments",
                new PropertyModel<List<Attachment>>( this, "attachments" ) ) {

            @Override
            protected void populateItem( ListItem<Attachment> item ) {
                Attachment a = item.getModelObject();
                ExternalLink documentLink = new ExternalLink( "attachment",
                        a.getUrl(), attachmentManager.getLabel( getPlan(), a ) );
                documentLink.add( new AttributeModifier( "target", true, new Model<String>( "_" ) ) );
                item.add( documentLink );
                addCopyImage( item );
                addDeleteImage( item );
                item.add( new AttributeModifier(
                        "class", true, new Model<String>( a.getType().getStyle() ) ) );
                item.add( new AttributeModifier(
                        "title",
                        true,
                        new Model<String>(
                                a.getType().getLabel() + " - " + a.getUrl()
                        ) ) );
            }
        };
        makeVisible( attachmentsContainer, !getAttachments().isEmpty() );
        attachmentsContainer.add( attachmentList );
    }

    private void addCopyImage( ListItem<Attachment> item ) {
        final Attachment attachment = item.getModelObject();
        AjaxFallbackLink deletelink = new AjaxFallbackLink( "copy" ) {
            public void onClick( AjaxRequestTarget target ) {
                Change change = doCommand( new CopyAttachment( User.current().getUsername(), attachment ) );
                change.setType( Change.Type.Copied );
                update( target, change );
            }
        };
        item.add( deletelink );
    }

    private void addDeleteImage( ListItem<Attachment> item ) {
        final Attachment attachment = item.getModelObject();
        ConfirmedAjaxFallbackLink deleteLink = new ConfirmedAjaxFallbackLink(
                "delete",
                "Delete attachment?" ) {
            public void onClick( AjaxRequestTarget target ) {
                doCommand( new DetachDocument( User.current().getUsername(), getAttachee(),
                        attachablePath,
                        attachment ) );
                if ( attachment.isPicture() ) {
                    imagingService.deiconize( getAttachee() );
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
        makeVisible( deleteLink, isLockedByUserIfNeeded( getAttachee() ) );
        item.add( deleteLink );
    }

    private void addKindSelector() {
        RadioChoice<Kind> kindSelector = new RadioChoice<Kind>(
                "radios",
                new PropertyModel<Kind>( this, "kind" ),
                Arrays.asList( Kind.values() ),
                new IChoiceRenderer<Kind>() {
                    public Object getDisplayValue( Kind object ) {
                        return " " + object.toString();
                    }

                    public String getIdValue( Kind object, int index ) {
                        return object.name();
                    }
                }
        );
        kindSelector.setSuffix( " " );
        kindSelector.add( new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                Kind k = (Kind) getComponent().getDefaultModelObject();
                makeVisible( uploadField, Kind.File.equals( k ) );
                makeVisible( urlField, Kind.URL.equals( k ) );
                makeVisible( submit, Kind.File.equals( kind ) );
                target.addComponent( AttachmentPanel.this );
            }
        } );
        controlsContainer.add( kindSelector );
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

    public FileUpload getUpload() {
        return upload;
    }

    /**
     * Set an upload. Called when user attached a file and then submitted.
     *
     * @param upload the uploaded file info
     */
    public void setUpload( FileUpload upload ) {
        this.upload = upload;
        if ( upload != null ) {
            ModelObject mo = getAttachee();
            LoggerFactory.getLogger( getClass() ).info( "Attaching file to {}", mo );
            Attachment attachment = attachmentManager.upload(
                    getPlan(),
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
                            return AttachmentPanel.this.upload.getClientFileName();
                        }

                        @Override
                        public InputStream getInputStream() throws IOException {
                            return AttachmentPanel.this.upload.getInputStream();
                        }
                    } );
            // Only add non-redundant attachment.
            if ( attachment != null ) {
                doCommand( new AttachDocument( User.current().getUsername(), mo, attachablePath, attachment ) );
                postProcess( attachment );
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
        Logger logger = LoggerFactory.getLogger( getClass() );
        this.url = value;
        if ( value != null ) {
            ModelObject mo = getAttachee();

            logger.info( "Attaching URL to {}", mo );
            // URL url;
            try {
                new URL( value );
                Attachment attachment = new AttachmentImpl( value, getSelectedType(), getName() );
                doCommand( new AttachDocument( User.current().getUsername(), mo, attachablePath, attachment ) );
                postProcess( attachment );
                this.url = null;
                this.name = "";
            } catch ( MalformedURLException e ) {
                logger.warn( "Invalid URL: " + value );
                if ( value.indexOf( "://" ) < 0 ) {
                    setUrl( "http://" + value );
                }
            }
        }
    }

    private ModelObject getAttachee() {
        return (ModelObject) getDefaultModelObject();
    }

    private void postProcess( Attachment attachment ) {
        ModelObject attachee = getAttachee();
        if ( attachablePath.isEmpty() && attachment.isPicture() && attachee.isIconized() ) {
            imagingService.iconize( getPlan(), attachment.getUrl(), attachee );
        }
    }


}
