package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * File attachments for a given model object.
 */
public class AttachmentPanel extends Panel {

    /** The 'upload' property. */
    private static final String UPLOAD_PROPERTY = "upload";                               // NON-NLS

    /** The file upload received from the client. */
    private FileUpload upload;

    public AttachmentPanel( String id, ModelObject object ) {
        super( id );
        add( new ListView<Wrapper>( "attachments", getAttachments( object ) ) {           // NON-NLS
            @Override
            protected void populateItem( ListItem<Wrapper> item ) {
                final Wrapper wrapper = item.getModelObject();
                final Attachment a = wrapper.getAttachment();
                item.add( new ExternalLink( "attachment", a.getLink(), a.getLabel() ) );  // NON-NLS
                item.add( new CheckBox( "delete",                                         // NON-NLS
                        new PropertyModel<Boolean>( wrapper, "markedForDeletion" ) ) );   // NON-NLS
            }
        } );

        add( new FileUploadField( UPLOAD_PROPERTY,
                                  new PropertyModel<FileUpload>( this, UPLOAD_PROPERTY ) ) );
    }

    private List<Wrapper> getAttachments( ModelObject object ) {
        final List<Wrapper> result = new ArrayList<Wrapper>( 5 );

        final AttachmentManager manager = getAttachmentManager();
        if ( manager != null ) {
            final Iterator<Attachment> iterator = manager.attachments( object );
            while ( iterator.hasNext() )
                result.add( new Wrapper( iterator.next() ) );
        }

        return result;
    }

    private AttachmentManager getAttachmentManager() {
        final Project project = (Project) getApplication();
        return project.getAttachmentManager();
    }

    public FileUpload getUpload() {
        return upload;
    }

    public void setUpload( FileUpload upload ) {
        this.upload = upload;
    }

    //==================================================
    /** A wrapper to keep track of the deletion state of an attachment. */
    private static final class Wrapper {

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
        }

        public Attachment getAttachment() {
            return attachment;
        }
    }
}
