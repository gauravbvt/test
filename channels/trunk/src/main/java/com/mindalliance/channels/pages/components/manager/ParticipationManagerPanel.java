package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.participation.ParticipationAnalyst;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Participation manager panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/4/12
 * Time: 10:44 AM
 */
public class ParticipationManagerPanel extends AbstractUpdatablePanel {

    @SpringBean
    private UserParticipationService userParticipationService;

    @SpringBean
    private ParticipationAnalyst participationAnalyst;

    private AjaxTabbedPanel<ITab> tabbedPanel;

    public ParticipationManagerPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, model );
        init();
    }

    private void init() {
        addTabPanel();
    }

    private void addTabPanel() {
        tabbedPanel = new AjaxTabbedPanel<ITab>( "tabs", getTabs() ) {
            @Override
            protected void onAjaxUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.NeedsRefresh ) );
            }
        };
        tabbedPanel.setOutputMarkupId( true );
        addOrReplace( tabbedPanel );
    }

    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add( new AbstractTab( new Model<String>( "Organizations" ) ) {
            public Panel getPanel( String id ) {
                // return new OrganizationsParticipationPanel( id, getModel() );
                return new OrgParticipationManager( id );
            }
        } );
        tabs.add( new AbstractTab( new Model<String>( "Users" ) ) {
            public Panel getPanel( String id ) {
                return new UserParticipationManager( id );
            }
        } );
        tabs.add( new AbstractTab( new PropertyModel<String>( this, "toConfirmTitle" ) ) {
            public Panel getPanel( String id ) {
                return new ParticipationConfirmationsPanel( id, getModel() );
            }
        } );
        tabs.add( new AbstractTab( new PropertyModel<String>( this, "issuesTitle" ) ) {
            public Panel getPanel( String id ) {
                return new ParticipationIssuesPanel( id );
            }
        } );
        tabs.add( new AbstractTab( new Model<String>( "Planners" ) ) {
                @Override
                public Panel getPanel( String id ) {
                    return new CommunityPlannersPanel( id );
                }
            } );
        return tabs;
    }

    public String getToConfirmTitle() {
        int toConfirmCount = userParticipationService
                .listUserParticipationsAwaitingConfirmationBy( getUser(), getCommunityService() ).size();
        return "Confirmations (" + toConfirmCount + " pending)";
    }

    public String getIssuesTitle() {
        int issuesCount = participationAnalyst.detectAllIssues( getCommunityService() ).size();
        return "Participation issues (" + issuesCount +")";
    }


    public void updateContent( AjaxRequestTarget target ) {
        // do nothing
    }

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
