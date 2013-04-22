package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;

/**
 * User participations panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 22, 2010
 * Time: 9:50:48 AM
 */
public class PlanUsersParticipationPanel extends AbstractCommandablePanel {  // todo - COMMUNITY - remove

    private ParticipationsPanel participationsPanel;

    public PlanUsersParticipationPanel( String id ) {
        super( id );
        init();
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }



    private void init() {
        addParticipations();
        addRefreshButton();
    }

    private void addParticipations() {
        participationsPanel = new ParticipationsPanel( "participations" );
        addOrReplace( participationsPanel );
    }

    private void addRefreshButton() {
        AjaxLink refreshButton = new IndicatingAjaxLink( "refresh" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                addParticipations();
                target.add( participationsPanel );
            }
        };
        add( refreshButton );
    }


}
