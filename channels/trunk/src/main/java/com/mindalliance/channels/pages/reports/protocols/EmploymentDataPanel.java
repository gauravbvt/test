package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.entities.OrganizationData;
import com.mindalliance.channels.api.entities.PlaceData;
import com.mindalliance.channels.api.directory.ContactData;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

/**
 * Employment data panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/22/12
 * Time: 4:52 PM
 */
public class EmploymentDataPanel extends AbstractDataPanel {
    private EmploymentData employmentData;

    public EmploymentDataPanel( String id, EmploymentData employmentData ) {
        super( id );
        this.employmentData = employmentData;
        init();
    }

    private void init() {
        add( new Label( "jobTitle", employmentData.getTitle()) );
        WebMarkupContainer jurContainer = new WebMarkupContainer( "jobJurisdictionContainer" );
        jurContainer.setVisible( employmentData.getJurisdictionId() != null );
        add( jurContainer );
        jurContainer.add( new Label(
                "jobJurisdiction",
                entityName( PlaceData.class, employmentData.getJurisdictionId() ) ) );
        add( new Label(
                "jobOrg",
                entityName( OrganizationData.class, employmentData.getOrganizationId() )
                ) );
        WebMarkupContainer supervisorsContainer = new WebMarkupContainer( "supervisorsContainer" );
        supervisorsContainer.setVisible( employmentData.getSupervisorId() != null );
        add( supervisorsContainer );
        ListView<ContactData> supervisorsListView = new ListView<ContactData>(
                "supervisors",
                findContacts( employmentData.getSupervisorId() )
                ) {
            @Override
            protected void populateItem( ListItem<ContactData> item ) {
                item.add( new ContactLinkPanel( "contact", item.getModelObject() ) );
            }
        };
        supervisorsContainer.add( supervisorsListView );
    }


}
