package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.AttachmentManager;
import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.AttachDocument;
import com.mindalliance.channels.command.commands.DetachDocument;
import com.mindalliance.channels.model.ModelObject;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.SubmitLink;
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

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * File attachments for a given model object.
 */
public class AttachmentPanel extends AbstractCommandablePanel {

    private SubmitLink submit;

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
     * The selected type for the upload.
     */
    private Attachment.Type selectedType = Attachment.Type.Document;
    /**
     * Attachments list container.
     */

    private WebMarkupContainer attachmentsContainer;

    /**
     * The content of the url field.
     */
    private String url;

    public AttachmentPanel( String id, IModel<? extends ModelObject> model ) {
        super( id, model, null );
        setOutputMarkupId( true );
        addAttachmentList();
        addTypeSelector();
        addKindSelector();
        addUploadField();
        addUrlField();
        addSubmit();
        adjustFields();
    }

    /**
     * {@inheritDoc}
     */
    protected void onBeforeRender() {
        super.onBeforeRender();
        makeVisible( submit, false );
    }

    private void addSubmit() {
        submit = new SubmitLink( "submit" );
        submit.setOutputMarkupId( true );
        submit.setEnabled( false );
        add( submit );
    }

    private void adjustFields() {
        makeVisible( submit, false );
        makeVisible( uploadField, Kind.File.equals( kind ) );
        makeVisible( urlField, Kind.URL.equals( kind ) );
    }

    private void refresh( AjaxRequestTarget target ) {
        adjustFields();
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
                        (ModelObject) getDefaultModelObject(),
                        "attachmentTickets"
                ) );
            }
        } );
        add( urlField );
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
        add( uploadField );
    }

    private void addTypeSelector() {
        add( new DropDownChoice<Attachment.Type>( "type",                                 // NON-NLS
                new PropertyModel<Attachment.Type>( this, "selectedType" ),               // NON-NLS
                Arrays.asList( Attachment.Type.values() ),
                new IChoiceRenderer<Attachment.Type>() {
                    public Object getDisplayValue( Attachment.Type object ) {
                        return object.getLabel();
                    }

                    public String getIdValue( Attachment.Type object, int index ) {
                        return Integer.toString( index );
                    }
                }
        ) );
    }

    private void addAttachmentList() {
        attachmentsContainer = new WebMarkupContainer( "attachments-container" );
        attachmentsContainer.setOutputMarkupId( true );
        add( attachmentsContainer );
        ListView<Wrapper> attachmentList = new ListView<Wrapper>( "attachments",           // NON-NLS
                new PropertyModel<List<Wrapper>>( this, "attachments" ) ) {               // NON-NLS

            @Override
            protected void populateItem( ListItem<Wrapper> item ) {
                Wrapper wrapper = item.getModelObject();
                Attachment a = wrapper.getAttachment();
                ExternalLink documentLink = new ExternalLink( "attachment",                                 // NON-NLS
                        a.getUrl(), a.getLabel() );
                documentLink.add( new AttributeModifier( "target", true, new Model<String>( "_" ) ) );
                item.add( documentLink );
                addDeleteImage( item );
                item.add( new AttributeModifier(
                        "class", true, new Model<String>( a.getType().getStyle() ) ) );   // NON-NLS
                item.add( new AttributeModifier(
                        "title", true, new Model<String>( a.getType().getLabel() ) ) );   // NON-NLS
            }
        };
        attachmentsContainer.add( attachmentList );
    }

    private void addDeleteImage( ListItem<Wrapper> item ) {
        final Wrapper wrapper = item.getModelObject();
        AjaxFallbackLink deletelink = new AjaxFallbackLink( "delete" ) {
            public void onClick( AjaxRequestTarget target ) {
                wrapper.deleteAttachment();
                refresh( target );
                update( target, new Change(
                        Change.Type.Updated,
                        (ModelObject) getDefaultModelObject(),
                        "attachmentTickets"
                ) );
            }
        };
        item.add( deletelink );
    }

    private void addKindSelector() {
        RadioChoice<Kind> kindSelector = new RadioChoice<Kind>(
                "radios",                                                                 // NON-NLS
                new PropertyModel<Kind>( this, "kind" ),                                  // NON-NLS
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
        add( kindSelector );
    }

    /**
     * Get current attachments to list. Called by component.
     *
     * @return a list of wrapped attachments
     */
    public List<Wrapper> getAttachments() {
        List<Wrapper> result = new ArrayList<Wrapper>();
        ModelObject object = (ModelObject) getDefaultModelObject();
        for ( String ticket : object.getAttachmentTickets() ) {
            Attachment attachment = attachmentManager.getAttachment( ticket );
            if ( attachment != null )
                result.add( new Wrapper( ticket, attachment ) );
        }
        return result;
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
            ModelObject mo = (ModelObject) getDefaultModelObject();
            LoggerFactory.getLogger( getClass() ).info( "Attaching file to {}", mo );
            String ticket = attachmentManager.attach( getSelectedType(), upload, mo.getAttachmentTickets() );
            // Only add non-redundant attachment.
            if ( ticket != null ) {
                doCommand( new AttachDocument( mo, ticket ) );
            }
        }
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
     * @param url the url string
     */
    public void setUrl( String url ) {
        this.url = url;

        if ( url != null ) {
            ModelObject mo = (ModelObject) getDefaultModelObject();
            Logger logger = LoggerFactory.getLogger( getClass() );

            logger.info( "Attaching URL to {}", mo );
            try {
                String ticket = attachmentManager.attach(
                        getSelectedType(),
                        new URL( url ),
                        mo.getAttachmentTickets() );
                if ( ticket != null ) {
                    doCommand( new AttachDocument( mo, ticket ) );
                }
                this.url = null;
            } catch ( MalformedURLException e ) {
                logger.warn( "Invalid URL: " + url );
                if ( url.indexOf( "://" ) < 0 ) {
                    setUrl( "http://" + url );
                }
            }
        }
    }

    //==================================================
    /**
     * A wrapper to keep track of the deletion state of an attachment.
     */
    private final class Wrapper implements Serializable {
        /**
         * The attachment's ticket.
         */
        private String ticket;
        /**
         * The underlying attachment.
         */
        private Attachment attachment;


        private Wrapper( String ticket, Attachment attachment ) {
            this.ticket = ticket;
            this.attachment = attachment;
        }


        public void deleteAttachment() {
            ModelObject object = (ModelObject) getDefaultModelObject();
            attachmentManager.detach( ticket );
            doCommand( new DetachDocument( object, ticket ) );
        }

        public Attachment getAttachment() {
            return attachment;
        }
    }
}
