package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.ElementOfInformationData;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.AttributeModifier;
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
                item.add( new Label( "name", eoiData.getName() ) );
                // classifications
                String classificationString = eoiData.getClassificationsLabel();
                Label securityClassificationsLabel = new Label(
                        "classifications",
                        classificationString.isEmpty() ? "--" : classificationString
                );
                item.add( securityClassificationsLabel );
                // Description / question answered
                String description = eoiData.getDescription();
                description = StringEscapeUtils.escapeXml( description == null ? "" : description );
                Label descriptionLabel = new Label(
                        "description",
                        description.isEmpty() ? "--" : description );
                descriptionLabel.add( new AttributeModifier(
                        "class",
                        description.endsWith( "?" )
                                ? "eoi-question"
                                : "eoi-description" ) );
                item.add( descriptionLabel );
                // Handling
                String specialHandling = eoiData.getSpecialHandling();
                Label handlingLabel = new Label(
                        "handling",
                        specialHandling == null || specialHandling.isEmpty() ? "--" : specialHandling
                         );
                item.add( handlingLabel );
            }
        };
        eoisContainer.add( eoisListView );
    }
}
