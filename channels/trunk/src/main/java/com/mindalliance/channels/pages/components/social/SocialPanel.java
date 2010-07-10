package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.social.PlannerMessagingService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Social interactions panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 5, 2010
 * Time: 10:03:51 AM
 */
public class SocialPanel extends AbstractUpdatablePanel {

    @SpringBean
    private PlannerMessagingService plannerMessagingService;

    public static final String SEND_MESSAGE = "sendMessage";

    private AjaxTabbedPanel tabbedPanel;

    private PlannerMessageListPanel plannerMessageListPanel;

    private CommandEventListPanel commandEventListPanel;

    private PlannerPresenceListPanel plannerPresenceListPanel;

    public SocialPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addSocialTabs();
    }

    private void addSocialTabs() {
        tabbedPanel = new AjaxTabbedPanel( "tabs", getTabs() );
        tabbedPanel.setOutputMarkupId( true );
        add( tabbedPanel );
    }

    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add( new AbstractTab( new Model<String>( "Presence" ) ) {
            public Panel getPanel( String id ) {
                plannerPresenceListPanel = new PlannerPresenceListPanel( id, SocialPanel.this );
                return plannerPresenceListPanel;
            }
        } );
        tabs.add( new AbstractTab( new Model<String>( "Activities" ) ) {
            public Panel getPanel( String id ) {
                commandEventListPanel = new CommandEventListPanel( id, SocialPanel.this );
                return commandEventListPanel;
            }
        } );
        tabs.add( new AbstractTab( new Model<String>( "Messages" ) ) {
            public Panel getPanel( String id ) {
                plannerMessageListPanel = new PlannerMessageListPanel( id, SocialPanel.this );
                return plannerMessageListPanel;
            }
        } );
        return tabs;
    }

    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        if ( plannerPresenceListPanel != null && tabbedPanel.getSelectedTab() == 0 )
            plannerPresenceListPanel.refresh( target, change );
        if ( commandEventListPanel != null && tabbedPanel.getSelectedTab() == 1 )
            commandEventListPanel.refresh( target, change );
        if ( plannerMessageListPanel != null && tabbedPanel.getSelectedTab() == 2 )
            plannerMessageListPanel.refresh( target, change );
    }

    public void update( AjaxRequestTarget target, Object object, String action ) {
        if ( object instanceof String ) {
            if ( action.equals( SEND_MESSAGE ) ) {
                if ( tabbedPanel.getSelectedTab() != 2 ) {
                    tabbedPanel.setSelectedTab( 2 );
                    target.addComponent( tabbedPanel );
                }
                plannerMessageListPanel.newMessage( (String) object, target );
            }
        }
    }

    public void newMessage( AjaxRequestTarget target, Change change ) {
        ModelObject about = (ModelObject) change.getSubject( getQueryService() );
        tabbedPanel.setSelectedTab( 2 );
        plannerMessageListPanel.newMessage( "", about, target );
        target.addComponent( tabbedPanel );
    }
}
