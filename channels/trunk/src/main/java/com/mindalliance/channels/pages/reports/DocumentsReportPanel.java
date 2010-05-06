package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 27, 2010
 * Time: 12:19:45 PM
 */
public class DocumentsReportPanel extends Panel {

    /**
     * The attachment manager.
     */
    @SpringBean
    private AttachmentManager attachmentManager;
    /**
     * The model object with issues
     */
    private ModelObject modelObject;

    public DocumentsReportPanel( String id, IModel<ModelObject> model ) {
        super( id, model );
        setRenderBodyOnly( true );
        modelObject = model.getObject();
        init();
    }

    private void init() {
        List<Attachment> attachments = modelObject.getAttachments();
        WebMarkupContainer documentsContainer = new WebMarkupContainer( "documents-container" );
        add( documentsContainer );
        documentsContainer.add( new ListView<Attachment>( "documents", attachments ) {
            @Override
            protected void populateItem( ListItem<Attachment> item ) {
                Attachment attachment = item.getModelObject();
                item.add( new Label( "document-type", attachment.getType().getLabel() ) );
                item.add( new ExternalLink( "document-link",
                        Attachment.addPrefixIfRelative( attachment.getUrl(), "../" ), 
                        attachmentManager.getLabel( getPlan(), attachment ) ) );
                String styleClass = attachment.getType().name().toLowerCase();
                item.add( new AttributeModifier( "class", true,
                        new Model<String>( styleClass ) ) );
            }
        } );
        documentsContainer.setVisible( !attachments.isEmpty() );

    }

    private Plan getPlan() {
        return User.current().getPlan();
    }

}
