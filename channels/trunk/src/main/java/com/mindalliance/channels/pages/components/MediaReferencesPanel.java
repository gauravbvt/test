package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.model.ModelObject;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;
import java.util.Set;

/**
 * Media references quick access panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 14, 2010
 * Time: 10:25:00 AM
 */
public class MediaReferencesPanel extends AbstractUpdatablePanel {

    @SpringBean
    private AttachmentManager attachmentManager;

    /**
     * Max number of media references to list.
     */
    private static final int MAX_SIZE = 6;
    /**
     * Part model.
     */
    private IModel<? extends ModelObject> moModel;

    public MediaReferencesPanel( String id, IModel<? extends ModelObject> moModel, Set<Long> expansions ) {
        super( id, moModel, expansions );
        this.moModel = moModel;
        init();
    }

    private void init() {
        List<Attachment> mediaRefs = attachmentManager.getMediaReferences( moModel.getObject() );
        boolean tooMany = mediaRefs.size() > MAX_SIZE;
        if ( tooMany ) mediaRefs = mediaRefs.subList( 0, MAX_SIZE );
        WebMarkupContainer mediaRefsContainer = new WebMarkupContainer( "mediaRefsList" );
        add( mediaRefsContainer );
        mediaRefsContainer.setVisible( !mediaRefs.isEmpty() );
        ListView<Attachment> mediaList = new ListView<Attachment>( "mediaRefs", mediaRefs ) {
            protected void populateItem( ListItem<Attachment> item ) {
                  Attachment attachment = item.getModelObject();
                ExternalLink link = new ExternalLink( "link", attachment.getUrl() );
                item.add( link );
                String src = attachmentManager.isImageReference( attachment )
                        ? "/images/image.png"
                        : attachmentManager.isVideoReference( attachment )
                        ? "/images/movie.png"
                        : "";
                String title = attachmentManager.isImageReference( attachment )
                        ? "Image"
                        : attachmentManager.isVideoReference( attachment )
                        ? "Video"
                        : "";
                WebMarkupContainer icon = new WebMarkupContainer( "icon" );
                icon.add(  new AttributeModifier(
                        "src",  true, new Model<String>( src ) ) );
                icon.add(  new AttributeModifier(
                        "alt",  true, new Model<String>( title ) ) );
                icon.add(  new AttributeModifier(
                        "title",  true, new Model<String>( attachmentManager.getLabel( getPlan(), attachment ) ) ) );
                link.add( icon );
            }
        };
        WebMarkupContainer more = new WebMarkupContainer( "more" );
        more.setVisible( tooMany );
        add( more );
        mediaRefsContainer.add( mediaList );
    }
}
