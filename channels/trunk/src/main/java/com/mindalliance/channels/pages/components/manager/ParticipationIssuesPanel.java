package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;

/**
 * Participation issues panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/8/13
 * Time: 2:34 PM
 */
public class ParticipationIssuesPanel extends AbstractUpdatablePanel {


    public ParticipationIssuesPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addCollaborationPlanIssuesPanel();
    }

    private void addCollaborationPlanIssuesPanel() {
        CollaborationPlanIssuesPanel issuesPanel = new CollaborationPlanIssuesPanel( "issues" );
        add( issuesPanel );
    }


 }
