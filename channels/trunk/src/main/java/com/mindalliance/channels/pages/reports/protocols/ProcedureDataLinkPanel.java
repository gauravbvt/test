package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.ProcedureData;
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
public class ProcedureDataLinkPanel extends AbstractDataPanel {
    private ProcedureData procedureData;

    public ProcedureDataLinkPanel( String id, ProcedureData procedureData, ProtocolsFinder finder ) {
        super( id, finder );
        this.procedureData = procedureData;
        init( finder );
    }

    private void init( ProtocolsFinder finder ) {
        WebMarkupContainer link = new WebMarkupContainer( "link" );
        link.add(  new AttributeModifier( "href", "#" + procedureData.getAnchor() ) );
        add( link );
        link.add( new Label( "taskName", procedureData.getTaskLabel() ) );
        WebMarkupContainer responsibility = new WebMarkupContainer( "responsibility" );
        responsibility.setVisible( finder.isMultipleParticipation() );
        link.add(  responsibility );
        responsibility.add( new Label( "title", procedureData.getTitleOrRole() )  );
        responsibility.add( new Label( "org", procedureData.getOrganizationLabel() )  );
    }
}
