package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.attachments.Attachment;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.List;

/**
 * A read-only list of attachments.
 */
class AttachmentListPanel extends Panel {

    /** URL prefix for attachment links. */
    private static final String PREFIX = "../../";

    AttachmentListPanel( String id, List<Attachment> attachments ) {
        super( id );
        add( new ListView<Attachment>( "attachment", attachments ) {
            @Override
            protected void populateItem( ListItem<Attachment> item ) {
                Attachment attachment = item.getModelObject();
                String url = attachment.getUrl();
                String filePart = url.substring( url.lastIndexOf( (int) '/' ) + 1 );
                item.add( new ExternalLink( "link", PREFIX + url, filePart ) );
            }
        } );

        setVisible( !attachments.isEmpty() );
        setRenderBodyOnly( true );
    }
}
