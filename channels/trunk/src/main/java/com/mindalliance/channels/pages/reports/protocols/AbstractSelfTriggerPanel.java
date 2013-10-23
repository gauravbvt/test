package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.ElementOfInformationData;
import com.mindalliance.channels.api.procedures.SharedInformationData;
import com.mindalliance.channels.api.procedures.TriggerData;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/29/12
 * Time: 4:01 PM
 */
public class AbstractSelfTriggerPanel extends AbstractTriggerDataPanel {

    public AbstractSelfTriggerPanel( String id, TriggerData triggerData, ProtocolsFinder finder ) {
        super( id, triggerData, finder );
    }

    protected void addInformation() {
        add( new Label(
                "header",
                getTriggerData().isOnFollowingUp()
                        ? "Upon acquiring"
                        : "When researching"
        ) );
        add( new Label( "information", getInformationData().getName() ) );
    }

  /*  protected void addEois() {
        WebMarkupContainer eoisContainer = new WebMarkupContainer( "eoisContainer" );
        eoisContainer.setVisible( !getEois().isEmpty() );
        add( eoisContainer );
        ListView<ElementOfInformationData> eoisListView = new ListView<ElementOfInformationData>(
                "eois",
                getEois()
        ) {
            @Override
            protected void populateItem( ListItem<ElementOfInformationData> item ) {
                ElementOfInformationData eoiData = item.getModelObject();
                item.add( new Label( "content", eoiData.getName() ) );
            }
        };
        eoisContainer.add( eoisListView );
    }*/

    protected void addEois() {
        int count = getEois().size();
        WebMarkupContainer eoisContainer = new WebMarkupContainer( "eoisContainer" );
        eoisContainer.setVisible( count > 0 );
        add( eoisContainer );
        eoisContainer.add( new Label( "eois", asCSVs( getEois() ) ));
    }


    protected SharedInformationData getInformationData() {
        return getTriggerData().isOnFollowingUp()
                ? getTriggerData().getOnFollowUp().getInformation()
                : getTriggerData().getOnResearch().getInformation();
    }

    protected List<ElementOfInformationData> getEois() {
        return getInformationData().getEOIs();
    }

}
