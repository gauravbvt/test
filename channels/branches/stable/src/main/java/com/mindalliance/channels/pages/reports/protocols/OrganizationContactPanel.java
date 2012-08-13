package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.entities.OrganizationData;
import com.mindalliance.channels.api.procedures.ChannelData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.model.Organization;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * Organization contact panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/26/12
 * Time: 1:33 PM
 */
public class OrganizationContactPanel extends AbstractDataPanel {
    private Organization organization;
    private OrganizationData organizationData;
    private OrganizationData parentData;

    public OrganizationContactPanel( String id, Organization organization, ProtocolsFinder finder ) {
        super( id, finder );
        this.organization = organization;
        initData();
        init();
    }

    private void initData() {
        organizationData = findInScope( OrganizationData.class, organization.getId() );
        Long parentId = organizationData.getParentId();
        if ( parentId != null ) {
            parentData = findInScope( OrganizationData.class, parentId );
        }
    }

    private void init() {
        addName();
        addParent();
        addStreetAddress();
        addContactInfo();
        addMission();
        addDocumentation();
    }

    private void addName() {
        add( new Label( "name", organizationData == null ? "???" : organizationData.getName() ) );
    }

    private void addParent() {
        add(  makeAttributeContainer( "parent", parentData == null ? null : parentData.getName() ) );
    }

    private void addStreetAddress() {
        add( makeAttributeContainer(
                "streetAddress",
                organizationData == null ? "???" : organizationData.getFullAddress() ) );
    }

    private void addContactInfo() {
        List<ChannelData> mediaAddresses = organizationData == null
                ? new ArrayList<ChannelData>()
                : organizationData.getChannels();
        WebMarkupContainer addressContainer = new WebMarkupContainer( "mediaAddresses" );
        addressContainer.setVisible( !mediaAddresses.isEmpty() );
        add( addressContainer );
        addressContainer.add(
                !mediaAddresses.isEmpty()
                        ? new ContactAddressesPanel( "addresses", mediaAddresses, getFinder() )
                        : new Label( "addresses", "" )
        );
    }

    private void addMission() {
        add( makeAttributeContainer(
                "mission",
                organizationData == null ? "???" : organizationData.getMission() ) );
    }

    private void addDocumentation() {
        DocumentationData documentationData = organizationData.getDocumentation();
        DocumentationPanel docPanel = new DocumentationPanel( "documentation", documentationData, getFinder() );
        docPanel.setVisible( documentationData.hasReportableDocuments() );
        add( docPanel );
    }

}
