package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.Channels;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * File attachments for a given model object.
 */
public class AttachmentPanel extends Panel {

    /** The 'upload' property. */
    private static final String UPLOAD_PROPERTY = "upload";                               // NON-NLS

    /** The 'selectedType' property. */
    private static final String TYPE_PROPERTY = "selectedType";                           // NON-NLS

    /** The file upload received from the client. */
    private FileUpload upload;

    /** The selected type for the upload. */
    private Attachment.Type selectedType = Attachment.Type.Document;

    public AttachmentPanel( String id, IModel<? extends ModelObject> model ) {
        super( id, model );
        setOutputMarkupId( true );

        add( new ListView<Wrapper>( "attachments",                                        // NON-NLS
                new PropertyModel<List<Wrapper>>( this, "attachments" ) ) {               // NON-NLS
            @Override
            protected void populateItem( ListItem<Wrapper> item ) {
                Wrapper wrapper = item.getModelObject();
                Attachment a = wrapper.getAttachment();
                item.add( new ExternalLink( "attachment",                                 // NON-NLS
                                            a.getUrl().toString(), a.getLabel() ) );
                item.add( new CheckBox( "delete",                                         // NON-NLS
                        new PropertyModel<Boolean>( wrapper, "markedForDeletion" ) ) );   // NON-NLS
                item.add( new AttributeModifier(
                        "class", true, new Model<String>( a.getType().getStyle() ) ) );   // NON-NLS
                item.add( new AttributeModifier(
                        "title", true, new Model<String>( a.getType().getLabel() ) ) );   // NON-NLS
            }
        } );

        add( new DropDownChoice<Attachment.Type>( "type",                                 // NON-NLS
                new PropertyModel<Attachment.Type>( this, TYPE_PROPERTY ),
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

        add( new FileUploadField(
                UPLOAD_PROPERTY, new PropertyModel<FileUpload>( this, UPLOAD_PROPERTY ) ) );
    }

    public List<Wrapper> getAttachments() {
        List<Wrapper> result = new ArrayList<Wrapper>();

        AttachmentManager manager = getAttachmentManager();
        if ( manager != null ) {
            ModelObject object = (ModelObject) getDefaultModelObject();
            Iterator<Attachment> iterator = manager.attachments( object );
            while ( iterator.hasNext() )
                result.add( new Wrapper( iterator.next() ) );
        }

        return result;
    }

    private AttachmentManager getAttachmentManager() {
        Channels app = (Channels) getApplication();
        return app.getAttachmentManager();
    }

    public FileUpload getUpload() {
        return upload;
    }

    /**
     * Set an upload. Called when user attached a file and then submitted.
     * @param upload the uploaded file info
     */
    public void setUpload( FileUpload upload ) {
        this.upload = upload;
        if ( upload != null ) {
            ModelObject object = (ModelObject) getDefaultModelObject();
            LoggerFactory.getLogger( getClass() ).info(  "Attaching file to {}", object );
            getAttachmentManager().attach( object, getSelectedType(), upload );
        }
    }

    public Attachment.Type getSelectedType() {
        return selectedType;
    }

    public void setSelectedType( Attachment.Type selectedType ) {
        this.selectedType = selectedType;
    }

    //==================================================
    /** A wrapper to keep track of the deletion state of an attachment. */
    private final class Wrapper implements Serializable {

        /** The underlying attachment. */
        private Attachment attachment;

        /** True if user marked item for deletion. */
        private boolean markedForDeletion;

        private Wrapper( Attachment attachment ) {
            this.attachment = attachment;
        }

        public boolean isMarkedForDeletion() {
            return markedForDeletion;
        }

        public void setMarkedForDeletion( boolean markedForDeletion ) {
            this.markedForDeletion = markedForDeletion;
            if ( markedForDeletion ) {
                ModelObject object = (ModelObject) getDefaultModelObject();
                Channels.instance().getAttachmentManager().detach( object, attachment );
            }
        }

        public Attachment getAttachment() {
            return attachment;
        }
    }
}
