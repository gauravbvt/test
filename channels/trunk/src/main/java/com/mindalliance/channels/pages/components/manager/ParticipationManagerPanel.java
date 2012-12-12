package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

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

    public ParticipationManagerPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, model );
        init();
    }

    private void init() {
        addTabPanel();
    }

    private void addTabPanel() {
        AjaxTabbedPanel tabbedPanel = new AjaxTabbedPanel<ITab>( "tabs", getTabs() ) {
            @Override
            protected void onAjaxUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.NeedsRefresh ) );
            }
        };
        tabbedPanel.setOutputMarkupId( true );
        add( tabbedPanel );
    }

    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add( new AbstractTab( new Model<String>( "User participation" ) ) {
            public Panel getPanel( String id ) {
                return new UsersParticipationPanel( id, getModel() );
            }
        } );
        tabs.add( new AbstractTab( new Model<String>( "Organization participation" ) ) {
            public Panel getPanel( String id ) {
                return new OrganizationsParticipationPanel( id, getModel() );
            }
        } );
        tabs.add( new AbstractTab( new PropertyModel<String>( this, "todosTitle" ) ) {
            public Panel getPanel( String id ) {
                return new ParticipationTodosPanel( id, getModel() );
            }
        } );

        return tabs;
    }

    public String getTodosTitle() {
        return "Todo"; // todo - add count
    }


    public void updateContent( AjaxRequestTarget target ) {
        // do nothing
    }
}
