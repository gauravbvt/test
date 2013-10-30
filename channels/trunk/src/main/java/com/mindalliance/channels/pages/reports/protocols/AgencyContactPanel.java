package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.community.AgencyData;
import com.mindalliance.channels.api.procedures.ChannelData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * Agency contact panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/26/12
 * Time: 1:33 PM
 */
public class AgencyContactPanel extends AbstractDataPanel {

    private AgencyData agencyData;
    private WebMarkupContainer orgContactDetails;

    public AgencyContactPanel( String id,
                               String serverUrl,
                               Agency agency,
                               ProtocolsFinder finder,
                               CommunityService communityService ) {
        super( id, finder );
        initData( serverUrl, agency, communityService );
        init();
    }

    private void initData( String serverUrl, Agency agency, CommunityService communityService ) {
        agencyData = new AgencyData( serverUrl, agency, communityService );
    }

    private void init() {
        addName();
        addOrgContactDetails();
    }

    private void addOrgContactDetails() {
        orgContactDetails = new WebMarkupContainer( "orgContactDetails" );
        orgContactDetails.setVisible( hasContactDetails() );
        add( orgContactDetails );
        addParent();
        addStreetAddress();
        addContactInfo();
        addMission();
        addDocumentation();
    }

    private boolean hasContactDetails() {
        return agencyData != null && (
                agencyData.getParent() != null
                        || ( agencyData.getAddress() != null && !agencyData.getAddress().isEmpty() )
                        || !agencyData.getChannels().isEmpty()
                        || ( agencyData.getMission() == null && !agencyData.getMission().isEmpty() )
                        || !agencyData.getDocumentation().hasReportableDocuments()
        );
    }

    private void addName() {
        add( new Label( "name", agencyData == null ? "???" : agencyData.getName() ) );
    }

    private void addParent() {
        AgencyData parentData = agencyData.getParent();
        orgContactDetails.add( makeAttributeContainer( "parent", parentData == null ? null : parentData.getName() ) );
    }

    private void addStreetAddress() {
        orgContactDetails.add( makeAttributeContainer(
                "streetAddress",
                agencyData == null ? "???" : agencyData.getAddress() ) );
    }

    private void addContactInfo() {
        List<ChannelData> mediaAddresses = agencyData == null
                ? new ArrayList<ChannelData>()
                : agencyData.getChannels();
        WebMarkupContainer addressContainer = new WebMarkupContainer( "mediaAddresses" );
        addressContainer.setVisible( !mediaAddresses.isEmpty() );
        orgContactDetails.add( addressContainer );
        addressContainer.add(
                !mediaAddresses.isEmpty()
                        ? new ContactAddressesPanel( "addresses", mediaAddresses, getFinder() )
                        : new Label( "addresses", "" )
        );
    }

    private void addMission() {
        orgContactDetails.add( makeAttributeContainer(
                "mission",
                agencyData == null ? "???" : agencyData.getMission() ) );
    }

    private void addDocumentation() {
        DocumentationData documentationData = agencyData.getDocumentation();
        DocumentationPanel docPanel = new DocumentationPanel( "documentation", documentationData, getFinder() );
        docPanel.setVisible( documentationData.hasReportableDocuments() );
        orgContactDetails.add( docPanel );
    }

}
