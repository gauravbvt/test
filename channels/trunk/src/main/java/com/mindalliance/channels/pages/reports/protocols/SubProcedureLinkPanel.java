package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.ElementOfInformationData;
import com.mindalliance.channels.api.procedures.DiscoveryData;
import com.mindalliance.channels.api.procedures.InformationData;
import com.mindalliance.channels.api.procedures.ResearchData;
import com.mindalliance.channels.api.procedures.TaskData;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

/**
 * Subprocedure link panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/29/12
 * Time: 3:42 PM
 */
public class SubProcedureLinkPanel extends AbstractDataPanel {

    private DiscoveryData discoveryData;
    private ResearchData researchData;

    public SubProcedureLinkPanel( String id, DiscoveryData discoveryData, ProtocolsFinder finder ) {
        super( id, finder );
        this.discoveryData = discoveryData;
        init();
    }

    public SubProcedureLinkPanel( String id, ResearchData researchData, ProtocolsFinder finder ) {
        super( id, finder );
        this.researchData = researchData;
        init();
    }

    private void init() {
        addInformation();
        addEois();
        addTaskLink();
    }

    protected void addInformation() {
        add( new Label(
                "header",
                discoveryData != null
                        ? "Upon acquiring"
                        : "When researching"
        ) );
        add( new Label( "information", getInformationData().getName() ) );
    }

    protected void addEois() {
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
    }

    protected InformationData getInformationData() {
        return discoveryData != null
                ? discoveryData.getInformationDiscovered().getInformation()
                : researchData.getInformation();
    }

    protected List<ElementOfInformationData> getEois() {
        return getInformationData().getEOIs();
    }


    private void addTaskLink() {
        WebMarkupContainer link = new WebMarkupContainer( "link" );
        link.add(  new AttributeModifier( "href", "#" + getSubOrFollowUpTask().getAnchor() ) );
        add( link );
        link.add( new Label( "taskName",
                "I do task \"" + getSubOrFollowUpTask().getLabel() + "\"" ) );

    }

    private TaskData getSubOrFollowUpTask() {
        return discoveryData == null
                ? researchData.getResearchTask()
                : discoveryData.getFollowUpTask();
    }

}
