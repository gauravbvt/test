package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.CollaborationRhythmPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/6/14
 * Time: 2:30 PM
 */
public class CollaborationRhythmFloatingPanel extends AbstractFloatingCommandablePanel {

    private CollaborationRhythmPanel collaborationRhythmPanel;

    public CollaborationRhythmFloatingPanel( String id ) {
        super( id );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "improving";
    }

    @Override
    public String getHelpTopicId() {
        return "rhythm";
    }

    private void init() {
        addHeading();
        addCollaborationRhythmPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "Collaboration rhythm" ) );
    }

    private void addCollaborationRhythmPanel() {
        collaborationRhythmPanel = new CollaborationRhythmPanel( "collaborationRhythm" );
        getContentContainer().add( collaborationRhythmPanel );
    }

    @Override
    protected String getTitle() {
        return "Collaboration rhythm";
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.RHYTHM );
        update( target, change );
    }

    @Override
    protected int getWidth() {
        return 1000;
    }

    @Override
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        super.refresh( target, change, aspect );
        collaborationRhythmPanel.refresh( target, change );
    }
}
