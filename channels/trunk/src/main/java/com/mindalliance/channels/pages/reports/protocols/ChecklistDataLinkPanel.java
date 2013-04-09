package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.checklist.ChecklistData;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/25/12
 * Time: 1:22 PM
 */
public class ChecklistDataLinkPanel extends AbstractDataPanel {
    private ChecklistData checklistData;

    public ChecklistDataLinkPanel( String id, ChecklistData checklistData, ProtocolsFinder finder ) {
        super( id, finder );
        this.checklistData = checklistData;
        init( finder );
    }

    private void init( ProtocolsFinder finder ) {
        WebMarkupContainer link = new WebMarkupContainer( "link" );
        link.add(  new AttributeModifier( "href", "#" + checklistData.getAnchor() ) );
        add( link );
        link.add( new Label( "taskName", checklistData.getTaskLabel() ) );
        WebMarkupContainer responsibility = new WebMarkupContainer( "responsibility" );
        responsibility.setVisible( finder.isMultipleParticipation() );
        link.add(  responsibility );
        responsibility.add( new Label( "title", checklistData.getTitleOrRole() ) );
        responsibility.add( new Label( "org", checklistData.getOrganizationLabel() )  );
    }
}
