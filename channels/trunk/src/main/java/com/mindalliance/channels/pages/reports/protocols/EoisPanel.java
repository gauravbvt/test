package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.ElementOfInformationData;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/27/12
 * Time: 8:43 AM
 */
public class EoisPanel extends AbstractDataPanel {

    private List<ElementOfInformationData> eoiDataList;
    private WebMarkupContainer eoisContainer;

    public EoisPanel( String id, List<ElementOfInformationData> eoiDataList, ProtocolsFinder finder ) {
        super( id, finder );
        this.eoiDataList = eoiDataList;
        init();
    }

    private void init() {
        eoisContainer = new WebMarkupContainer( "allEois" );
        add( eoisContainer );
        addEoiList();
    }

    private void addEoiList() {
        ListView<ElementOfInformationData> eoisListView = new ListView<ElementOfInformationData>(
                "eois",
                eoiDataList
        ) {
            @Override
            protected void populateItem( ListItem<ElementOfInformationData> item ) {
                ElementOfInformationData eoiData = item.getModelObject();
                // name
                item.add(  new Label( "name", eoiData.getName() ) );
                // classifications
                WebMarkupContainer classificationsContainer = new WebMarkupContainer( "classificationContainer" );
                item.add( classificationsContainer );
                classificationsContainer.setVisible( !eoiData.getClassifications().isEmpty() );
                SecurityClassificationsPanel securityClassificationsPanel = new SecurityClassificationsPanel(
                        "classifications",
                        eoiData.getClassifications(),
                        getFinder()
                );
                classificationsContainer.add( securityClassificationsPanel );
                // Description
                Label descriptionLabel = new Label( "description", eoiData.getDescription() );
                descriptionLabel.setVisible( eoiData.getDescription() != null
                        && !eoiData.getDescription().isEmpty() );
                item.add( descriptionLabel );
                // Handling
                Label handlingLabel = new Label( "handling", eoiData.getSpecialHandling() );
                handlingLabel.setVisible( eoiData.getSpecialHandling() != null
                        && !eoiData.getSpecialHandling().isEmpty() );
                item.add( handlingLabel );
            }
        };
        eoisContainer.add( eoisListView );
    }
}
