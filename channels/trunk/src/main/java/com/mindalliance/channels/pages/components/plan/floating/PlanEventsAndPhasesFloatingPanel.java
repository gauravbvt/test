package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.PhaseListPanel;
import com.mindalliance.channels.pages.components.plan.PlanEventsPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Plan event floating panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 3:47 PM
 */
public class PlanEventsAndPhasesFloatingPanel extends AbstractFloatingCommandablePanel {

    private PlanEventsPanel planEventsPanel;

    private AjaxTabbedPanel<ITab> tabbedPanel;

    public PlanEventsAndPhasesFloatingPanel( String id, IModel<Event> eventModel ) {
        super( id, eventModel );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "scoping";
    }

    @Override
    public String getHelpTopicId() {
        return "eventsAndPhases";
    }

    private void init() {
        addHeading();
        addTabPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "All events and phases" ) );
    }

    private void addTabPanel() {
        tabbedPanel = new AjaxTabbedPanel<ITab>( "tabs", getTabs() );
        tabbedPanel.setOutputMarkupId( true );
        getContentContainer().addOrReplace( tabbedPanel );
    }

    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add( new AbstractTab( new Model<String>( "Events" ) ) {
            public Panel getPanel( String id ) {
                return new PlanEventsPanel(
                        id,
                        new Model<Plan>(getPlan() ),
                        null );
            }
        } );
        tabs.add( new AbstractTab( new Model<String>( "Event phases" ) ) {
            public Panel getPanel( String id ) {
                return new PhaseListPanel( id );
            }
        } );
        return tabs;
    }


    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.ALL_EVENTS);
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "All events and phases";
    }

    public void updateContent( AjaxRequestTarget target ) {
        // do nothing
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updatables ) {
        if ( change.isUpdated() ) {
            int selection = tabbedPanel.getSelectedTab();
            addTabPanel();
            tabbedPanel.setSelectedTab( selection );
            target.add( tabbedPanel );
        } else {
            super.updateWith( target, change, updatables );
        }
    }


}
