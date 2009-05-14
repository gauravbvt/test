package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.ModelObject;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
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
import java.util.Iterator;
import java.util.List;

/**
 * File attachments for a given model object.
 */
public class AttachmentPanel extends AbstractUpdatablePanel {

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
                        "attachments"
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
                item.add( new ExternalLink( "attachment",                                 // NON-NLS
                        a.getUrl(), a.getLabel() ) );
                addDeleteCheckBox( item );
                item.add( new AttributeModifier(
                        "class", true, new Model<String>( a.getType().getStyle() ) ) );   // NON-NLS
                item.add( new AttributeModifier(
                        "title", true, new Model<String>( a.getType().getLabel() ) ) );   // NON-NLS
            }
        };
        attachmentsContainer.add( attachmentList );
    }

    private void addDeleteCheckBox( ListItem<Wrapper> item ) {
        Wrapper wrapper = item.getModelObject();
        CheckBox deleteCheckBox = new CheckBox( "confirmed",                                         // NON-NLS
                new PropertyModel<Boolean>( wrapper, "confirmed" ) );
        deleteCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                refresh( target );
                update( target, new Change(
                        Change.Type.Updated,
                        (ModelObject) getDefaultModelObject(),
                        "attachments"
                ) );
            }
        } );
        item.add( deleteCheckBox );
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

        if ( attachmentManager != null ) {
            ModelObject object = (ModelObject) getDefaultModelObject();
            Iterator<Attachment> iterator = attachmentManager.attachments( object.getId() );
            while ( iterator.hasNext() )
                result.add( new Wrapper( iterator.next() ) );
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
            ModelObject object = (ModelObject) getDefaultModelObject();
            LoggerFactory.getLogger( getClass() ).info( "Attaching file to {}", object );
            attachmentManager.attach( object.getId(), getSelectedType(), upload );
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
            ModelObject object = (ModelObject) getDefaultModelObject();
            Logger logger = LoggerFactory.getLogger( getClass() );

            logger.info( "Attaching URL to {}", object );
            try {
                attachmentManager.attach( object.getId(), getSelectedType(), new URL( url ) );
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
         * The underlying attachment.
         */
        private Attachment attachment;

        /**
         * Unconfirming detaches the attachment.
         */
        private boolean confirmed;

        private Wrapper( Attachment attachment ) {
            this.attachment = attachment;
            confirmed = true;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public void setConfirmed( boolean confirmed ) {
            this.confirmed = confirmed;
            if ( !confirmed ) {
                ModelObject object = (ModelObject) getDefaultModelObject();
                attachmentManager.detach( object.getId(), attachment );
            }
        }

        public Attachment getAttachment() {
            return attachment;
        }
    }
}
