package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.DocumentData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/2/12
 * Time: 8:37 AM
 */
public class DocumentationPanel extends AbstractDataPanel {

    private DocumentationData documentationData;

    public DocumentationPanel( String id, DocumentationData documentationData, ProtocolsFinder finder ) {
        super( id, finder );
        this.documentationData = documentationData;
        init();
    }

    private void init() {
        ListView<DocumentData> documentListView = new ListView<DocumentData>(
                "documents",
                documentationData.reportableDocuments()
        ) {
            @Override
            protected void populateItem( ListItem<DocumentData> item ) {
                DocumentData documentData = item.getModelObject();
                WebMarkupContainer a = new WebMarkupContainer( "documentLink" );
                a.add( new AttributeModifier( "href", documentData.getUrl() ) );
                a.add( new AttributeModifier( "class", documentData.getType() ) );
                item.add( new Label( "documentType", documentData.getType() ) );
                a.add( new Label( "documentName", documentData.getName() ) );
                item.add( a );
            }
        };
        add( documentListView );
    }

}
