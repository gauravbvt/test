package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.RequestData;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

/**
 * Panel listing expected queries (non-triggering requests).
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/10/13
 * Time: 2:09 PM
 */
public class QueriesPanel extends AbstractDataPanel {
    public QueriesPanel( String id, ProtocolsFinder finder ) {
        super( id, finder );
        init();
    }

    private void init() {
        WebMarkupContainer queriesContainer = new WebMarkupContainer( "queriesContainer" );
        add(  queriesContainer );
        addQueries( queriesContainer );
    }

    private void addQueries( WebMarkupContainer queriesContainer ) {
        ListView<RequestData> queriesListView = new ListView<RequestData>(
                "queries",
                getFinder().getExpectedQueries() ) {
            @Override
            protected void populateItem( ListItem<RequestData> item ) {
                RequestData requestData = item.getModelObject();
                item.add( new QueryAnswerPanel(
                        "query",
                        requestData,
                        item.getIndex(),
                        getFinder()
                ) );
            }
        };
        queriesContainer.add( queriesListView );
    }
}
