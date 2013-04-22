package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.SecurityClassificationData;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

/**
 * Security classifications panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/27/12
 * Time: 10:12 AM
 */
public class SecurityClassificationsPanel extends AbstractDataPanel {
    private List<SecurityClassificationData> classificationDataList;

    public SecurityClassificationsPanel(
            String id,
            List<SecurityClassificationData> classificationDataList,
            ProtocolsFinder finder ) {
        super( id, finder );
        this.classificationDataList = classificationDataList;
        init();
    }

    private void init() {
        WebMarkupContainer classificationsContainer = new WebMarkupContainer( "allClassifications" );
        add( classificationsContainer );
        ListView<SecurityClassificationData> classificationsListView = new ListView<SecurityClassificationData>(
                "classifications",
                classificationDataList
        ) {
            @Override
            protected void populateItem( ListItem<SecurityClassificationData> item ) {
                SecurityClassificationData classificationData = item.getModelObject();
                item.add( new Label( "system", classificationData.getSystem() ) );
                item.add( new Label( "level", classificationData.getLevel() ) );
            }
        };
        classificationsContainer.add( classificationsListView );
    }
}
